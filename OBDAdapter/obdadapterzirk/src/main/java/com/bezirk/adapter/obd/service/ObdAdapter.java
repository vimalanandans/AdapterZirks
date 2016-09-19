package com.bezirk.adapter.obd.service;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.bezirk.adapter.obd.constants.CommandConstants;
import com.bezirk.adapter.obd.events.RequestObdErrorCodesEvent;
import com.bezirk.adapter.obd.events.RequestObdLiveDataEvent;
import com.bezirk.adapter.obd.events.ResponseObdErrorCodesEvent;
import com.bezirk.adapter.obd.events.ResponseObdLiveDataEvent;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;

/**
 * Created by DEV6KOR on 9/8/2016.
 */
public class ObdAdapter {
    private static final String TAG = ObdAdapter.class.getName();

    public ObdAdapter(final Bezirk bezirk, BluetoothSocket socket) throws MalformedURLException {

        final ObdController controller = new ObdController(socket);
        final EventSet obdCommandEventSet = new EventSet(RequestObdLiveDataEvent.class, RequestObdErrorCodesEvent.class);

        obdCommandEventSet.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                Log.e(TAG, "Inside receive Event of ObdAdapter");
                if (event instanceof RequestObdLiveDataEvent) {
                    Log.e(TAG, "Received the event RequestObdLiveDataEvent ");
                    final ResponseObdLiveDataEvent obdLiveDataEvent = controller.getObdLiveData(CommandConstants.ENGINE_RPM);
                    Log.e(TAG, "Sending response event ResponseObdLiveDataEvent... ");
                    bezirk.sendEvent(sender, obdLiveDataEvent);
                }
                else if(event instanceof RequestObdErrorCodesEvent) {
                    Log.e(TAG, "Received the event RequestObdErrorCodesEvent ");
                    final ResponseObdErrorCodesEvent obdErrorCodesEvent = controller.getObdErrorCodes(CommandConstants.ERR_CODES);
                    Log.e(TAG, "Sending response event ResponseObdErrorCodesEvent... ");
                    bezirk.sendEvent(sender, obdErrorCodesEvent);
                }
            }
        });
        bezirk.subscribe(obdCommandEventSet);
    }
}