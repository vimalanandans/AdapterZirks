package com.bezirk.adapter.obd.service;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.bezirk.adapter.obd.constants.CommandConstants;
import com.bezirk.adapter.obd.events.RequestObdEngineRPMEvent;
import com.bezirk.adapter.obd.events.RequestObdErrorCodesEvent;
import com.bezirk.adapter.obd.events.RequestObdFuelLevelEvent;
import com.bezirk.adapter.obd.events.RequestObdVehicleSpeedEvent;
import com.bezirk.adapter.obd.events.ResponseObdEngineRPMEvent;
import com.bezirk.adapter.obd.events.ResponseObdErrorCodesEvent;
import com.bezirk.adapter.obd.events.ResponseObdFuelLevelEvent;
import com.bezirk.adapter.obd.events.ResponseObdStatusEvent;
import com.bezirk.adapter.obd.events.ResponseObdVehicleSpeedEvent;
import com.bezirk.adapter.obd.events.SenderEvent;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;

import java.net.MalformedURLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
/**
 * Created by DEV6KOR on 9/8/2016.
 */
public class ObdAdapter {
    private static final String TAG = ObdAdapter.class.getName();
    private Bezirk bezirk;
    private EventSet obdCommandEventSet;
    protected BlockingQueue<SenderEvent> commandQueue = new LinkedBlockingQueue<>();
    public ObdController controller;

    public ObdAdapter(final Bezirk bezirk, BluetoothSocket socket) throws MalformedURLException {

        controller = new ObdController(socket);
        obdCommandEventSet = new EventSet(RequestObdEngineRPMEvent.class, RequestObdFuelLevelEvent.class,
                RequestObdVehicleSpeedEvent.class, RequestObdErrorCodesEvent.class);
        this.bezirk = bezirk;

        obdCommandEventSet.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                if (event instanceof RequestObdEngineRPMEvent) {
                    Log.e(TAG, "Received the event RequestObdEngineRPMEvent ");
                    commandQueue.add(new SenderEvent(sender, event));
                }
                else if (event instanceof RequestObdFuelLevelEvent) {
                    Log.e(TAG, "Received the event RequestObdFuelLevelEvent ");
                    commandQueue.add(new SenderEvent(sender, event));
                }
                else if (event instanceof RequestObdVehicleSpeedEvent) {
                    Log.e(TAG, "Received the event RequestObdVehicleSpeedEvent ");
                    commandQueue.add(new SenderEvent(sender, event));
                }
                else if(event instanceof RequestObdErrorCodesEvent) {
                    Log.e(TAG, "Received the event RequestObdErrorCodesEvent ");
                    commandQueue.add(new SenderEvent(sender, event));
                }
            }
        });
        bezirk.subscribe(obdCommandEventSet);
        Log.d(TAG, "Subscription Successful. Now starting Execution of Commands...");
        execThread.start();
    }

    Thread execThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                executeCommandsFromQueue();
            } catch (InterruptedException e) {
                Log.d(TAG, "Execution Interrupted. Now Interrupting the executionThread..");
                execThread.interrupt();
            }
        }
    });

    public void executeCommandsFromQueue() throws InterruptedException {
        while (!Thread.currentThread().isInterrupted()) {
            SenderEvent senderEvent = commandQueue.take();
            if (senderEvent.getEvent() instanceof RequestObdEngineRPMEvent) {
                try {
                    final ResponseObdEngineRPMEvent obdEngineRPMEvent = controller.getEngineRPM(CommandConstants.ENGINE_RPM);
                    bezirk.sendEvent(senderEvent.getZirkEndPoint(), obdEngineRPMEvent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "Error while executing Commands from Queue for ResponseObdEngineRPMEvent...Now sending event for ResponseObdStatusEvent");
                    bezirk.sendEvent(senderEvent.getZirkEndPoint(), new ResponseObdStatusEvent(e.getMessage(), false));
                    Log.d(TAG, "Now interrupting the Queue thread..");
                    Thread.currentThread().interrupt();
                    execThread.interrupt();
                }
            }
            else if (senderEvent.getEvent() instanceof RequestObdFuelLevelEvent) {
                try {
                    final ResponseObdFuelLevelEvent obdFuelLevelEvent = controller.getFuelLevel(CommandConstants.FUEL_LEVEL);
                    bezirk.sendEvent(senderEvent.getZirkEndPoint(), obdFuelLevelEvent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "Error while executing Commands from Queue for ResponseObdFuelLevelEvent...Now sending event for ResponseObdStatusEvent");
                    bezirk.sendEvent(senderEvent.getZirkEndPoint(), new ResponseObdStatusEvent(e.getMessage(), false));
                    Log.d(TAG, "Now interrupting the Queue thread..");
                    Thread.currentThread().interrupt();
                    execThread.interrupt();
                }
            }
            else if (senderEvent.getEvent() instanceof RequestObdVehicleSpeedEvent) {
                try {
                    final ResponseObdVehicleSpeedEvent obdVehicleSpeedEvent = controller.getObdVehicleSpeed(CommandConstants.VEH_SPEED);
                    bezirk.sendEvent(senderEvent.getZirkEndPoint(), obdVehicleSpeedEvent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "Error while executing Commands from Queue for ResponseObdVehicleSpeedEvent...Now sending event for ResponseObdStatusEvent");
                    bezirk.sendEvent(senderEvent.getZirkEndPoint(), new ResponseObdStatusEvent(e.getMessage(), false));
                    Log.d(TAG, "Now interrupting the Queue thread..");
                    Thread.currentThread().interrupt();
                    execThread.interrupt();
                }
            }
            else if (senderEvent.getEvent() instanceof RequestObdErrorCodesEvent) {
                try {
                    final ResponseObdErrorCodesEvent obdErrorCodesEvent = controller.getObdErrorCodes(CommandConstants.ERR_CODES);
                    bezirk.sendEvent(senderEvent.getZirkEndPoint(), obdErrorCodesEvent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "Error while executing Commands from Queue for RequestObdErrorCodesEvent...Now sending event for ResponseObdStatusEvent");
                    bezirk.sendEvent(senderEvent.getZirkEndPoint(), new ResponseObdStatusEvent(e.getMessage(), false));
                    Log.d(TAG, "Now interrupting the Queue thread..");
                    Thread.currentThread().interrupt();
                    execThread.interrupt();
                }
            }
        }
    }

    public void unSubscribeEventSet()
    {
        bezirk.unsubscribe(obdCommandEventSet);
    }
}