package com.bezirk.adapter.lightify;

import com.bezirk.hardwareevents.light.Light;
import com.bezirk.hardwareevents.light.LightsDetectedEvent;
import com.bezirk.hardwareevents.light.SetLightBrightnessEvent;
import com.bezirk.hardwareevents.light.TurnLightOffEvent;
import com.bezirk.hardwareevents.light.TurnLightOnEvent;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.java.proxy.BezirkMiddleware;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class LightifyZirkTest {
    private static final Logger logger = LoggerFactory.getLogger(LightifyZirkTest.class);

    public static void main(String[] args) {
        BezirkMiddleware.initialize();
        final Bezirk bezirk = BezirkMiddleware.registerZirk("Lightify Test Zirk");

        final EventSet lightEvents = new EventSet(LightsDetectedEvent.class);

        lightEvents.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                if (event instanceof LightsDetectedEvent) {
                    Set<Light> lights = ((LightsDetectedEvent) event).getLights();

                    for (final Light light : lights) {
                        logger.debug("Found light: {}", light.toString());

                        bezirk.sendEvent(new TurnLightOnEvent(light));
                        bezirk.sendEvent(new SetLightBrightnessEvent(light, 50));

                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                bezirk.sendEvent(new TurnLightOffEvent(light));
                            }
                        }, 2000);
                    }
                }
            }
        });

        bezirk.subscribe(lightEvents);

        Set<String> gateways = LightifyAdapter.discoverGateways();
        try {
            new LightifyAdapter(bezirk, gateways.toArray(new String[gateways.size()])[0]);
        } catch (IOException e) {
            logger.error("Failed to connect to lightify gateway", e);
        }
    }
}
