package com.bezirk.adapter.philips.hue;

import com.bezirk.hardwareevents.HexColor;
import com.bezirk.hardwareevents.light.CurrentLightStateEvent;
import com.bezirk.hardwareevents.light.GetLightStateEvent;
import com.bezirk.hardwareevents.light.Light;
import com.bezirk.hardwareevents.light.LightsDetectedEvent;
import com.bezirk.hardwareevents.light.SetLightBrightnessEvent;
import com.bezirk.hardwareevents.light.SetLightColorEvent;
import com.bezirk.hardwareevents.light.TurnLightOffEvent;
import com.bezirk.hardwareevents.light.TurnLightOnEvent;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.bezirk.middleware.java.proxy.BezirkMiddleware;

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

                    for (final Light light : lights) {
                        logger.debug("Found light: {}", light.toString());

                        bezirk.sendEvent(new GetLightStateEvent(light));
                        bezirk.sendEvent(new TurnLightOnEvent(light));
                        bezirk.sendEvent(new SetLightColorEvent(light, new HexColor("#00FF00")));
                        bezirk.sendEvent(new SetLightBrightnessEvent(light, 125));

                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                bezirk.sendEvent(new TurnLightOffEvent(light));
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
