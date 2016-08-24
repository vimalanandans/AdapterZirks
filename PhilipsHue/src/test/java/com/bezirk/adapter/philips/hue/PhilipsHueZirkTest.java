package com.bezirk.adapter.philips.hue;

import com.bezirk.hardwareevents.HexColor;
import com.bezirk.hardwareevents.light.SetLightColorEvt;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.proxy.BezirkMiddleware;

public class PhilipsHueZirkTest {
    public static void main(String[] args) {
        Bezirk bezirk = BezirkMiddleware.registerZirk("Philips Hue Test Zirk");

        bezirk.sendEvent(new SetLightColorEvt("", new HexColor("#FFFF00")));
        System.out.println("Sent color change event");
    }
}
