package com.bezirk.adapterzirks.estimote;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.bezirk.hardwareevents.beacon.Beacon;
import com.bezirk.hardwareevents.beacon.BeaconsDetectedEvt;
import com.bezirk.hardwareevents.environment.Temperature;
import com.bezirk.middleware.Bezirk;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Nearable;
import com.estimote.sdk.SystemRequirementsChecker;

import java.util.ArrayList;
import java.util.List;

public class EstimoteAdapter {
    private static final String TAG = EstimoteAdapter.class.getName();

    private final BeaconManager beaconManager;
    private String scanId;

    public EstimoteAdapter(final Bezirk bezirk, Context applicationContext) {
        beaconManager = new BeaconManager(applicationContext);
        beaconManager.setNearableListener(new BeaconManager.NearableListener() {
            @Override
            public void onNearablesDiscovered(List<Nearable> nearables) {
                final List<Beacon> beacons = new ArrayList<>();
                for (Nearable nearable : nearables) {
                    Beacon beacon = new Beacon(nearable.identifier,
                            Beacon.BatteryLevel.values()[nearable.batteryLevel.ordinal()],
                           new Temperature(nearable.temperature, Temperature.TemperatureUnit.CELSIUS));
                    beacons.add(beacon);
                    Log.d(TAG, String.format("Estimote nearable detected: %s%n", beacon.toString()));
                }
                bezirk.sendEvent(new BeaconsDetectedEvt(beacons));
                Log.v(TAG, "Sent beacons detected event");
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
