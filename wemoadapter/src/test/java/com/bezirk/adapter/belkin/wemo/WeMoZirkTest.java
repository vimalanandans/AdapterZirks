package com.bezirk.adapter.belkin.wemo;

import com.bezirk.hardwareevents.outlet.Outlet;
import com.bezirk.hardwareevents.outlet.OutletsDetectedEvent;
import com.bezirk.hardwareevents.outlet.TurnOutletOffEvent;
import com.bezirk.hardwareevents.outlet.TurnOutletOnEvent;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.java.proxy.BezirkMiddleware;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class WeMoZirkTest {
    public static void main(String[] args) {
        final Bezirk bezirk = BezirkMiddleware.registerZirk("WeMo Test Zirk");

        final EventSet outletEvents = new EventSet(OutletsDetectedEvent.class);

        outletEvents.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint zirkEndPoint) {
                if (event instanceof OutletsDetectedEvent) {
                    final Set<Outlet> outlets = ((OutletsDetectedEvent) event).getOutlets();

                    for (Outlet outlet : outlets) {
                        bezirk.sendEvent(new TurnOutletOnEvent(outlet));

                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                bezirk.sendEvent(new TurnOutletOffEvent(outlet));
                            }
                        }, 4000);
                    }
                }
            }
        });

        bezirk.subscribe(outletEvents);

        new WeMoAdapter(bezirk);
    }
}
