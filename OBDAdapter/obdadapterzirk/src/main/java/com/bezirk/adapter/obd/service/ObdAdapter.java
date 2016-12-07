package com.bezirk.adapter.obd.service;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.bezirk.adapter.obd.datamodel.OBDResponseData;
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

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ObdAdapter {
    static protected final BlockingQueue<ObdCommand> commandQueue = new LinkedBlockingQueue<>();
    private static final String TAG = ObdAdapter.class.getName();
    private final Bezirk bezirk;
    private final EventSet obdCommandEventSet;
    private final ObdController controller;
    private final QueueService service;
    private ZirkEndPoint senderId;
    private OBDResponseData obdResponseData;
    private List<OBDQueryParameter> parameters;
    final Thread execThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Log.e(TAG, "Executing the Commands in Queue...");
                executeCommandsFromQueue();
            } catch (InterruptedException e) {
                Log.d(TAG, "Execution Interrupted. Now Interrupting the executionThread..");
                execThread.interrupt();
                bezirk.unsubscribe(obdCommandEventSet);
            } catch (Exception e) {
                execThread.interrupt();
                e.printStackTrace();
            }
        }
    });

    public ObdAdapter(final Bezirk bezirk, BluetoothSocket socket) throws MalformedURLException {
        controller = new ObdController(socket);
        obdCommandEventSet = new EventSet(RequestObdStartEvent.class, RequestObdStopEvent.class);
        this.bezirk = bezirk;
        service = new QueueService();
        obdCommandEventSet.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                if (event instanceof RequestObdStartEvent) {
                    Log.e(TAG, "Received the event RequestObdStartEvent ");
                    senderId = sender;
                    parameters = ((RequestObdStartEvent) event).getParameters();
                    service.prepareCommandsToQueue(parameters);
                    execThread.start();
                } else if (event instanceof RequestObdStopEvent) {
                    Log.e(TAG, "Received the event RequestObdStopEvent ");
                    senderId = sender;
                    service.stopQueueAddition();
                    unSubscribeEventSet();
                }
            }
        });
        subscribeEventSet();
        Log.d(TAG, "Subscription Successful. Now starting Execution of Commands...");
    }

    public void sendResult(String commandName, String result) {
        if (commandName.equals(OBDQueryParameter.TROUBLE_CODES.getValue())) {
            bezirk.sendEvent(senderId, new ResponseObdErrorCodesEvent(result));
        }
        if (commandName.equals(OBDQueryParameter.ENGINE_RPM.getValue())) {
            bezirk.sendEvent(senderId, new ResponseObdEngineRPMEvent(result));
        } else if (commandName.equals(OBDQueryParameter.SPEED.getValue())) {
            bezirk.sendEvent(senderId, new ResponseObdVehicleSpeedEvent(result));
        } else if (commandName.equals(OBDQueryParameter.ENGINE_COOLANT_TEMP.getValue())) {
            bezirk.sendEvent(senderId, new ResponseObdCoolantTempEvent(result));
        } else {
            if (obdResponseData == null) {
                obdResponseData = new OBDResponseData();
            }
            if (obdResponseData.getFillCounter() == parameters.size() - 1) {
                bezirk.sendEvent(senderId, new ResponseOBDDataEvent(obdResponseData));
                obdResponseData = null;
            } else {
                OBDQueryParameter obdQueryParameter = OBDQueryParameter.getOBDQueryParameter(commandName);
                if (obdQueryParameter != null) {
                    obdQueryParameter.updateOBDResponseData(obdResponseData, result);
                    obdResponseData.incrementFillCounter();
                }
            }
        }
    }

    public void executeCommandsFromQueue() throws InterruptedException {
        while (!Thread.currentThread().isInterrupted()) {
            ObdCommand command = QueueService.commandQueue.take();
            try {
                String result = controller.executeCommand(command);
                String commandName = command.getName();
                sendResult(commandName, result);

            } catch (Exception e) {
                commandQueue.clear();
                e.printStackTrace();
                Log.d(TAG, "Error while executing Commands from Queue for ResponseObdEngineRPMEvent...Now sending event for ResponseObdStatusEvent");
                bezirk.sendEvent(senderId, new ResponseObdStatusEvent(e.getMessage(), false));
                Log.d(TAG, "Now interrupting the Queue thread..");
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