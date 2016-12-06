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
/**
 * Created by DEV6KOR on 9/8/2016.
 */
public class ObdAdapter {
    private static final String TAG = ObdAdapter.class.getName();
    private Bezirk bezirk;
    private EventSet obdCommandEventSet;
    static protected BlockingQueue<ObdCommand> commandQueue = new LinkedBlockingQueue<>();
    private ObdController controller;
    private QueueService service;
    private ZirkEndPoint senderId;
    private OBDResponseData obdResponseData;
    private List<OBDQueryParameter> parameters;

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
                }
                else if (event instanceof RequestObdStopEvent) {
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

    Thread execThread = new Thread(new Runnable() {
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

    public void sendResult(String commandName, String result)
    {
        if(commandName.equals(OBDQueryParameter.TROUBLE_CODES.getValue())){
            bezirk.sendEvent(senderId, new ResponseObdErrorCodesEvent(result));
        }
        if(commandName.equals(OBDQueryParameter.ENGINE_RPM.getValue())){
            bezirk.sendEvent(senderId, new ResponseObdEngineRPMEvent(result));
        }
        else if(commandName.equals(OBDQueryParameter.SPEED.getValue())){
            bezirk.sendEvent(senderId, new ResponseObdVehicleSpeedEvent(result));
        }
        else if(commandName.equals(OBDQueryParameter.ENGINE_COOLANT_TEMP.getValue())){
            bezirk.sendEvent(senderId, new ResponseObdCoolantTempEvent(result));
        }
        else{
            if(obdResponseData == null)
            {
                obdResponseData = new OBDResponseData();
            }
            if(obdResponseData.getFillCounter() == parameters.size()-1){
                bezirk.sendEvent(senderId, new ResponseOBDDataEvent(obdResponseData));
                obdResponseData = null;
            }
            else {
                prepareOBDResponseData(commandName, result);
            }
        }
    }

    void prepareOBDResponseData(String commandName, String result)
    {
        if(commandName.equals(OBDQueryParameter.ABS_LOAD.getValue())){
            obdResponseData.setAbsoluteLoad(result);
        }
        else if(commandName.equals(OBDQueryParameter.AMBIENT_AIR_TEMP.getValue())){
            obdResponseData.setAmbientAirTemperature(result);
        }
        else if(commandName.equals(OBDQueryParameter.BAROMETRIC_PRESSURE.getValue())){
            obdResponseData.setBarometricPressure(result);
        }
        else if(commandName.equals(OBDQueryParameter.CONTROL_MODULE_VOLTAGE.getValue())){
            obdResponseData.setCtrlModulePowerSupply(result);
        }
        else if(commandName.equals(OBDQueryParameter.DISTANCE_TRAVELED_AFTER_CODES_CLEARED.getValue())){
            obdResponseData.setDistanceSinceCodesCleared(result);
        }
        else if(commandName.equals(OBDQueryParameter.DISTANCE_TRAVELED_MIL_ON.getValue())){
            obdResponseData.setDistanceTraveledMILon(result);
        }
        else if(commandName.equals(OBDQueryParameter.AIR_FUEL_RATIO.getValue())){
            obdResponseData.setAirOrFuelRatio(result);
        }
        else if(commandName.equals(OBDQueryParameter.ENGINE_RUNTIME.getValue())){
            obdResponseData.setEngineRuntime(result);
        }
        else if(commandName.equals(OBDQueryParameter.ENGINE_OIL_TEMP.getValue())){
            obdResponseData.setEngineOilTemperature(result);
        }
        else if(commandName.equals(OBDQueryParameter.EQUIV_RATIO.getValue())){
            obdResponseData.setCmdEquivalenceRatio(result);
        }
        else if(commandName.equals(OBDQueryParameter.FUEL_CONSUMPTION_RATE.getValue())){
            obdResponseData.setFuelConsumptionRate(result);
        }
        else if(commandName.equals(OBDQueryParameter.FUEL_LEVEL.getValue())){
            obdResponseData.setFuelLevel(result);
        }
        else if(commandName.equals(OBDQueryParameter.FUEL_PRESSURE.getValue())){
            obdResponseData.setFuelPressure(result);
        }
        else if(commandName.equals(OBDQueryParameter.FUEL_RAIL_PRESSURE.getValue())){
            obdResponseData.setFuelRailPressure(result);
        }
        else if(commandName.equals(OBDQueryParameter.FUEL_TYPE.getValue())){
            obdResponseData.setFuelType(result);
        }
        else if(commandName.equals(OBDQueryParameter.INTAKE_MANIFOLD_PRESSURE.getValue())){
            obdResponseData.setIntakeManifoldPressure(result);
        }
        else if(commandName.equals(OBDQueryParameter.MAF.getValue())){
            obdResponseData.setMassAirFlow(result);
        }
        else if(commandName.equals(OBDQueryParameter.THROTTLE_POS.getValue())){
            obdResponseData.setThrottlePosition(result);
        }
        else if(commandName.equals(OBDQueryParameter.TROUBLE_CODES.getValue())){
            obdResponseData.setTroubleCodes(result);
        }
        else if(commandName.equals(OBDQueryParameter.TIMING_ADVANCE.getValue())){
            obdResponseData.setTimingAdvance(result);
        }
        else if(commandName.equals(OBDQueryParameter.VIN.getValue())){
            obdResponseData.setVehicleIdentificationNumber(result);
        }
        obdResponseData.incrementFillCounter();
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

    public void unSubscribeEventSet()
    {
        Log.d(TAG, "Unsubscribing to OBDCommandEventSet");
        bezirk.unsubscribe(obdCommandEventSet);
    }

    private void subscribeEventSet()
    {
        Log.d(TAG, "Subscribing to OBDCommandEventSet");
        bezirk.subscribe(obdCommandEventSet);
    }
}