package com.bezirk.adapter.obd.service;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.bezirk.adapter.obd.datamodel.OBDResponseData;
import com.bezirk.adapter.obd.enums.OBDErrorMessages;
import com.bezirk.adapter.obd.enums.OBDQueryParameter;
import com.bezirk.adapter.obd.events.RequestObdStartEvent;
import com.bezirk.adapter.obd.events.RequestObdStopEvent;
import com.bezirk.adapter.obd.events.ResponseOBDDataEvent;
import com.bezirk.adapter.obd.events.ResponseObdCoolantTempEvent;
import com.bezirk.adapter.obd.events.ResponseObdEngineRPMEvent;
import com.bezirk.adapter.obd.events.ResponseObdErrorCodesEvent;
import com.bezirk.adapter.obd.events.ResponseObdStatusEvent;
import com.bezirk.adapter.obd.events.ResponseObdVehicleSpeedEvent;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.github.pires.obd.commands.ObdCommand;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;

/**
 * This class serves as a means to:
 * 1. Initialize Bezirk, and for event subscription in the OBDAdapter
 * 2. Instantiate ObdController, that takes bluetooth socket object to query from the OBD Dongle.
 * 3. Instantiate QueueService, that creates a CommandQueue, to push all the OBD query commands for execution by ObdController
 */

public class ObdAdapter {
    protected static final BlockingQueue<ObdCommand> commandQueue = new LinkedBlockingQueue<>();
    private static final String LOG_INT_MSG = "Now interrupting the Queue thread..";
    private static final String TAG = ObdAdapter.class.getName();
    private final Bezirk bezirk;
    private final EventSet obdCommandEventSet;
    private final ObdController controller;
    private final QueueService service;
    private ZirkEndPoint senderId;
    private OBDResponseData obdResponseData;
    private List<OBDQueryParameter> parameters;

    /**
     * Below thread is started as soon as an event 'RequestObdStartEvent' is received. It will be interrupted when
     * bluetooth connection with the OBD Dongle is lost
     **/

    final Thread execThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Log.v(TAG, "Executing the Commands in Queue...");
                executeCommandsFromQueue();
            } catch (InterruptedException e) {
                Log.d(TAG, "Execution Interrupted. Now Interrupting the executionThread..");
                execThread.interrupt();
                bezirk.unsubscribe(obdCommandEventSet);
            }
        }
    });

    /**
     * Constructor for initializing event set, Bezirk and initialize QueueService
     *
     * @param bezirk
     * @param socket
     */
    public ObdAdapter(final Bezirk bezirk, final BluetoothSocket socket) {
        obdCommandEventSet = new EventSet(RequestObdStartEvent.class, RequestObdStopEvent.class);
        this.bezirk = bezirk;
        service = new QueueService();
        obdCommandEventSet.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                if (event instanceof RequestObdStartEvent) {
                    // This event is a trigger to start the execution of OBD Commands
                    Log.v(TAG, "Received the event RequestObdStartEvent");
                    senderId = sender;
                    parameters = ((RequestObdStartEvent) event).getParameters();
                    service.prepareCommandsToQueue(parameters);
                    execThread.start();
                } else if (event instanceof RequestObdStopEvent) {
                    // This event is a trigger to stop the execution of OBD Commands
                    Log.v(TAG, "Received the event RequestObdStopEvent");
                    senderId = sender;
                    service.stopQueueAddition();
                    unSubscribeEventSet();
                }
            }
        });
        subscribeEventSet();
        Log.d(TAG, "Subscription for OBD Events Successful. Initializing ObdController...");

        controller = new ObdController(bezirk, socket);
    }


    /**
     * This method will send out Bezirk Events to notify the UI to update the values.
     * OBD commands that need frequent refresh rate will be sent as separate event and those commands that do not
     * need frequent refresh will be encapsulated inside OBDResponseData and sent back as a single event.
     *
     * @param commandName The command value name as defined in the OBDQueryParameter enum
     * @param result      The result that is queried from the OBD Dongle
     */
    public void sendResult(final String commandName, final String result) {
        final OBDQueryParameter obdQueryParameter = OBDQueryParameter.getOBDQueryParameter(commandName);

        switch (obdQueryParameter) {
            case TROUBLE_CODES:
                bezirk.sendEvent(senderId, new ResponseObdErrorCodesEvent(result));
                if (obdResponseData != null) {
                    obdQueryParameter.updateOBDResponseData(obdResponseData, result);
                }
                break;
            case ENGINE_RPM:
                bezirk.sendEvent(senderId, new ResponseObdEngineRPMEvent(result));
                if (obdResponseData != null) {
                    obdQueryParameter.updateOBDResponseData(obdResponseData, result);
                }
                break;
            case SPEED:
                bezirk.sendEvent(senderId, new ResponseObdVehicleSpeedEvent(result));
                if (obdResponseData != null) {
                    obdQueryParameter.updateOBDResponseData(obdResponseData, result);
                }
                break;
            case ENGINE_COOLANT_TEMP:
                bezirk.sendEvent(senderId, new ResponseObdCoolantTempEvent(result));
                if (obdResponseData != null) {
                    obdQueryParameter.updateOBDResponseData(obdResponseData, result);
                }
                break;
            default:
                setObdResponseData(obdQueryParameter, result);
                break;
        }
    }

    /**
     * Method to set the values in OBDResponseData. It checks until all the attributes are
     * filled with values, by keeping a fillCounter. Once all are set, it triggers an event
     * and makes the obdResponseData null, for setting next series of values
     *
     * @param obdQueryParameter
     * @param result
     */
    private void setObdResponseData(final OBDQueryParameter obdQueryParameter, String result) {
        if (obdResponseData == null) {
            obdResponseData = new OBDResponseData();
        }
        if (obdResponseData.getFillCounter() == parameters.size() - 1) {
            bezirk.sendEvent(senderId, new ResponseOBDDataEvent(obdResponseData));
            obdResponseData = null;
        } else {
            if (obdQueryParameter != null) {
                obdQueryParameter.updateOBDResponseData(obdResponseData, result);
                obdResponseData.incrementFillCounter();
            }
        }
    }

    /***
     * This method will send the command to be executed to the ObdController. If there is any interrupt raised, then
     * it would terminate the execution, and clear the CommandQueue and stop the OBD data retrieval process. Also an event
     * is triggered to notify the UI about stoppage of the OBD retrieval data.
     * In case of stoppage of retrieval data, unSubscribeEventSet() is called. It will be subscribed again once
     * the OBD retrieval data is started.
     *
     * @throws InterruptedException
     */
    public void executeCommandsFromQueue() throws InterruptedException {
        while (!Thread.currentThread().isInterrupted()) {
            final ObdCommand command = QueueService.commandQueue.take();
            try {
                final String result = controller.executeCommand(command);
                final String commandName = command.getName();
                sendResult(commandName, result);
            } catch (InterruptedException e) {
                commandQueue.clear();
                Log.e(TAG, e.getMessage());
                Log.d(TAG, "STOP INTERRUPT while executing Commands from Queue for ResponseObdEngineRPMEvent...Now sending event for ResponseObdStatusEvent");
                bezirk.sendEvent(senderId, new ResponseObdStatusEvent(OBDErrorMessages.STOP_INTERRUPT_ERR, false));
                Log.d(TAG, LOG_INT_MSG);
                Thread.currentThread().interrupt();
                execThread.interrupt();
                unSubscribeEventSet();
            } catch (ExecutionException e) {
                Log.e(TAG, "Error while executing OBD Command", e);
                Log.d(TAG, "EXEC ERR executing Commands from Queue for ResponseObdEngineRPMEvent...Now sending event for ResponseObdStatusEvent");
                bezirk.sendEvent(senderId, new ResponseObdStatusEvent(OBDErrorMessages.STOP_EXECUTION_ERR, false));
                Log.d(TAG, LOG_INT_MSG);
                Thread.currentThread().interrupt();
                execThread.interrupt();
                unSubscribeEventSet();
            } catch (TimeoutException e) {
                Log.e(TAG, "OBD Command timed out", e);
                Log.d(TAG, "TIME OUT ERR while executing Commands from Queue for ResponseObdEngineRPMEvent...Now sending event for ResponseObdStatusEvent");
                bezirk.sendEvent(senderId, new ResponseObdStatusEvent(OBDErrorMessages.STOP_TIMEOUT_ERR, false));
                Log.d(TAG, LOG_INT_MSG);
                Thread.currentThread().interrupt();
                execThread.interrupt();
                unSubscribeEventSet();
            }
        }
    }

    public void unSubscribeEventSet() {
        Log.d(TAG, "Unsubscribing to OBDCommandEventSet");
        bezirk.unsubscribe(obdCommandEventSet);
    }

    private void subscribeEventSet() {
        Log.d(TAG, "Subscribing to OBDCommandEventSet");
        bezirk.subscribe(obdCommandEventSet);
    }
}