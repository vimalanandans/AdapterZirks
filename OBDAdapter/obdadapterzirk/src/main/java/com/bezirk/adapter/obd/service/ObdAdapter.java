package com.bezirk.adapter.obd.service;

import android.bluetooth.BluetoothSocket;

import com.bezirk.adapter.obd.constants.CommandConstants;
import com.bezirk.adapter.obd.events.RequestObdLiveDataEvent;
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
    private static final Logger logger = LoggerFactory.getLogger(ObdAdapter.class);

    public ObdAdapter(final Bezirk bezirk, BluetoothSocket socket) throws MalformedURLException {

        final ObdController controller = new ObdController(socket);
        final EventSet obdCommandEventSet = new EventSet(RequestObdLiveDataEvent.class);

        obdCommandEventSet.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                if (event instanceof RequestObdLiveDataEvent) {
                    final ResponseObdLiveDataEvent obdLiveDataEvent = controller.getObdLiveData(CommandConstants.ENGINE_RPM);
                    bezirk.sendEvent(sender, obdLiveDataEvent);
                }

            }
        });
        bezirk.subscribe(obdCommandEventSet);
    }
}