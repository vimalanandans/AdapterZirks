package com.bezirk.adapter.obd.service;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.bezirk.adapter.obd.constants.CommandConstants;
import com.bezirk.adapter.obd.events.RequestObdErrorCodesEvent;
import com.bezirk.adapter.obd.events.RequestObdLiveDataEvent;
import com.bezirk.adapter.obd.events.ResponseObdErrorCodesEvent;
import com.bezirk.adapter.obd.events.ResponseObdLiveDataEvent;
import com.bezirk.adapter.obd.events.ResponseObdStatusEvent;
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
        obdCommandEventSet = new EventSet(RequestObdLiveDataEvent.class, RequestObdErrorCodesEvent.class);
        this.bezirk = bezirk;

        obdCommandEventSet.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                if (event instanceof RequestObdLiveDataEvent) {
                    Log.e(TAG, "Received the event RequestObdLiveDataEvent ");
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
            if (senderEvent.getEvent() instanceof RequestObdLiveDataEvent) {
                try {
                    final ResponseObdLiveDataEvent obdLiveDataEvent = controller.getObdLiveData(CommandConstants.ENGINE_RPM);
                    bezirk.sendEvent(senderEvent.getZirkEndPoint(), obdLiveDataEvent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "Error while executing Commands from Queue for ResponseObdLiveDataEvent...Now sending event for ResponseObdStatusEvent");
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