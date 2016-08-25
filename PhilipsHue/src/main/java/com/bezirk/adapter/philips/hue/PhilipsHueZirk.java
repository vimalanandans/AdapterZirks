package com.bezirk.adapter.philips.hue;

import com.bezirk.hardwareevents.HexColor;
import com.bezirk.hardwareevents.light.SetLightBrightnessEvt;
import com.bezirk.hardwareevents.light.SetLightColorEvt;
import com.bezirk.hardwareevents.light.TurnLightOffEvt;
import com.bezirk.hardwareevents.light.TurnLightOnEvt;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;

public class PhilipsHueZirk {
    private static final Logger logger = LoggerFactory.getLogger(PhilipsHueZirk.class);

    private final PhilipsHueController philipsHueController = new PhilipsHueController();

    public PhilipsHueZirk(Bezirk bezirk) {
        final EventSet lightEventSet = new EventSet(TurnLightOnEvt.class, TurnLightOffEvt.class,
                SetLightBrightnessEvt.class, SetLightColorEvt.class);

        lightEventSet.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                if (event instanceof TurnLightOnEvt) {
                    philipsHueController.turnLightOn();
                } else if (event instanceof TurnLightOffEvt) {
                    philipsHueController.turnLightOff();
                } else if (event instanceof SetLightBrightnessEvt) {
                    SetLightBrightnessEvt brightnessEvt = (SetLightBrightnessEvt) event;
                    logger.trace(brightnessEvt.toString());

                    philipsHueController.setLightBrightness(brightnessEvt.getBrightnessLevel());
                } else if (event instanceof SetLightColorEvt) {
                    SetLightColorEvt colorEvent = (SetLightColorEvt) event;
                    logger.trace(colorEvent.toString());

                    setLightColor(colorEvent.getColor());
                }
            }
        });

        bezirk.subscribe(lightEventSet);
        logger.trace("Listening for light events");
    }

    private void setLightColor(HexColor hexColor) {
        final Color color = Color.decode(hexColor.getHexString());
        final float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        final int hue = (int)(hsb[0]*65535);
        final int sat = (int)(hsb[1]*255);
        final int bri = (int)(hsb[2]*255);

        logger.trace("H: {} S: {} b: {}", hue, sat, bri);
        philipsHueController.setLightColorHSV(hue, sat, bri);
    }
}