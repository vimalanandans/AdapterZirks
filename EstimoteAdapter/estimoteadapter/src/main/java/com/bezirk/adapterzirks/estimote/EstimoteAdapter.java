package com.bezirk.adapterzirks.estimote;

import android.app.Activity;
import android.content.Context;

import com.bezirk.hardwareevents.beacon.Beacon;
import com.bezirk.hardwareevents.beacon.BeaconsDetectedEvt;
import com.bezirk.middleware.Bezirk;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Nearable;
import com.estimote.sdk.SystemRequirementsChecker;

import java.util.ArrayList;
import java.util.List;

public class EstimoteAdapter {
    private final BeaconManager beaconManager;
    private String scanId;

    public EstimoteAdapter(final Bezirk bezirk, Context applicationContext) {
        beaconManager = new BeaconManager(applicationContext);
        beaconManager.setNearableListener(new BeaconManager.NearableListener() {
            @Override
            public void onNearablesDiscovered(List<Nearable> nearables) {
                final List<Beacon> beacons = new ArrayList<>();
                for (Nearable nearable : nearables) {
                    Beacon beacon = new Beacon();
                    beacon.id = nearable.identifier;
                    beacon.batteryLevelEnum = Beacon.BatteryLevelEnum.values()[nearable.batteryLevel.ordinal()];
                    beacon.isMoving = nearable.isMoving;
                    beacon.tempFaren = 9*nearable.temperature/5 + 32;
                    beacons.add(beacon);
                    System.out.format("Estimote nearable detected: %s%n", beacon.toString());
                }
                bezirk.sendEvent(new BeaconsDetectedEvt(beacons));
                System.out.println("Sent beacons detected event");
            }
        });
    }

    public void start() {
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                scanId = beaconManager.startNearableDiscovery();
            }
        });
    }

    public void stop() {
        beaconManager.stopNearableDiscovery(scanId);
    }

    public void resume(Activity mainActivity) {
        SystemRequirementsChecker.checkWithDefaultDialogs(mainActivity);
    }
}
