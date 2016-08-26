package com.bezirk.adapter.philips.hue;

import com.bezirk.hardwareevents.light.CurrentLightStateEvent;
import com.bezirk.hardwareevents.light.GetLightStateEvent;
import com.bezirk.hardwareevents.light.Light;
import com.bezirk.hardwareevents.light.LightsDetectedEvent;
import com.bezirk.hardwareevents.light.TurnLightOffEvent;
import com.bezirk.hardwareevents.light.TurnLightOnEvent;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.bezirk.middleware.proxy.BezirkMiddleware;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class PhilipsHueZirkTest {
    private static final Logger logger = LoggerFactory.getLogger(PhilipsHueZirkTest.class);

    public static void main(String[] args) {
        final Bezirk bezirk = BezirkMiddleware.registerZirk("Philips Hue Test Zirk");

        final EventSet lightEvents = new EventSet(LightsDetectedEvent.class,
                CurrentLightStateEvent.class);

        lightEvents.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                if (event instanceof LightsDetectedEvent) {
                    Set<Light> lights = ((LightsDetectedEvent) event).getLights();

                    for (Light light : lights) {
                        bezirk.sendEvent(new GetLightStateEvent(light.getId()));
                        bezirk.sendEvent(new TurnLightOnEvent(light.getId()));

                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                bezirk.sendEvent(new TurnLightOffEvent(light.getId()));
                            }
                        }, 2000);
                    }
                } else if (event instanceof CurrentLightStateEvent) {
                    CurrentLightStateEvent lightState = (CurrentLightStateEvent) event;
                    logger.info(lightState.toString());
                }
            }
        });

        bezirk.subscribe(lightEvents);

        try {
            Set<String> hueBridges = PhilipsHueAdapter.discoverHueBridges();
            new PhilipsHueAdapter(bezirk, hueBridges.toArray(new String[hueBridges.size()])[0],
                    "oFZsQakh9XzQiVhkIuuv83xsycRsmfgcEn5eBvjm");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
