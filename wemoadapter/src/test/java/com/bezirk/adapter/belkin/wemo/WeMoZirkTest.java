package com.bezirk.adapter.belkin.wemo;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.proxy.BezirkMiddleware;

import java.util.Set;

public class WeMoZirkTest {
    public static void main(String[] args) {
        final Bezirk bezirk = BezirkMiddleware.registerZirk("WeMo Test Zirk");

        final Set<String> potentialSwitches = WeMoAdapter.discoverSwitches();

        for (String potentialSwitch : potentialSwitches) {
            System.out.println(potentialSwitch);
        }
    }
}
