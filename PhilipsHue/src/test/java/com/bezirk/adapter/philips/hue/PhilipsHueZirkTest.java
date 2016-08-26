package com.bezirk.adapter.philips.hue;

import com.bezirk.hardwareevents.HexColor;
import com.bezirk.hardwareevents.light.SetLightColorEvt;
import com.bezirk.hardwareevents.light.TurnLightOnEvent;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.proxy.BezirkMiddleware;

import java.net.MalformedURLException;
import java.util.Set;

public class PhilipsHueZirkTest {
    public static void main(String[] args) {
        Bezirk bezirk = BezirkMiddleware.registerZirk("Philips Hue Test Zirk");

        try {
            Set<String> hueBridges = PhilipsHueAdapter.discoverHueBridges();
            new PhilipsHueAdapter(bezirk, hueBridges.toArray(new String[hueBridges.size()])[0],
                    "oFZsQakh9XzQiVhkIuuv83xsycRsmfgcEn5eBvjm");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        bezirk.sendEvent(new TurnLightOnEvent("1"));
        bezirk.sendEvent(new SetLightColorEvt("1", new HexColor("#FFFF00")));
        System.out.println("Sent color change event");
    }
}
