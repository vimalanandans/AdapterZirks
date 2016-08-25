package com.bezirk.adapterzirks.estimote;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.bezirk.hardwareevents.beacon.Beacon;
import com.bezirk.hardwareevents.beacon.BeaconsDetectedEvent;
import com.bezirk.hardwareevents.beacon.GetBeaconAttributesEvent;
import com.bezirk.hardwareevents.environment.Temperature;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Nearable;
import com.estimote.sdk.SystemRequirementsChecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EstimoteAdapter {
    public static final String MANUFACTURER_ESTIMOTE = "estimote";

    private static final String TAG = EstimoteAdapter.class.getName();

    private final BeaconManager beaconManager;
    private final Map<String, Nearable> beaconsSeen = new HashMap<>();
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
                            new Temperature(nearable.temperature, Temperature.TemperatureUnit.CELSIUS),
                            MANUFACTURER_ESTIMOTE);
                    beacons.add(beacon);

                    beaconsSeen.put(nearable.identifier, nearable);

                    Log.d(TAG, String.format("Estimote nearable detected: %s%n", beacon.toString()));
                }
                bezirk.sendEvent(new BeaconsDetectedEvent(beacons));
                Log.v(TAG, "Sent beacons detected event");
            }
        });

        final EventSet beaconEvents = new EventSet(GetBeaconAttributesEvent.class);

        beaconEvents.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                if (event instanceof GetBeaconAttributesEvent) {
                    GetBeaconAttributesEvent beaconAttributesEvent =
                            (GetBeaconAttributesEvent) event;

                    if (MANUFACTURER_ESTIMOTE.equals(beaconAttributesEvent.getManufacturer()) &&
                            beaconsSeen.containsKey(beaconAttributesEvent.getId())) {
                        final Nearable requestedNearable = beaconsSeen.get(beaconAttributesEvent.getId());

                        final EstimoteBeaconAttributesEvent beaconAttributes =
                                new EstimoteBeaconAttributesEvent(requestedNearable.bootloaderVersion,
                                        requestedNearable.currentMotionStateDuration,
                                        requestedNearable.firmwareVersion,
                                        requestedNearable.hardwareVersion,
                                        requestedNearable.identifier,
                                        requestedNearable.isMoving,
                                        requestedNearable.lastMotionStateDuration,
                                        convertOrientation(requestedNearable.orientation),
                                        requestedNearable.rssi, requestedNearable.xAcceleration,
                                        requestedNearable.yAcceleration, requestedNearable.zAcceleration);

                        bezirk.sendEvent(sender, beaconAttributes);
                        Log.v(TAG, "Received estimote attribute request, sent attributes");
                    }
                }
            }
        });

        bezirk.subscribe(beaconEvents);
    }

    // This conversion is done in case estimote changes their orientation enum in a way
    // that changes the ordinal values assigned to each member.
    private EstimoteBeaconAttributesEvent.EstimoteOrientation convertOrientation(Nearable.Orientation orientation) {
        switch (orientation) {
            case HORIZONTAL:
                return EstimoteBeaconAttributesEvent.EstimoteOrientation.HORIZONTAL;
            case HORIZONTAL_UPSIDE_DOWN:
                return EstimoteBeaconAttributesEvent.EstimoteOrientation.HORIZONTAL_UPSIDE_DOWN;
            case LEFT_SIDE:
                return EstimoteBeaconAttributesEvent.EstimoteOrientation.LEFT_SIDE;
            case RIGHT_SIDE:
                return EstimoteBeaconAttributesEvent.EstimoteOrientation.RIGHT_SIDE;
            case UNKNOWN:
                return EstimoteBeaconAttributesEvent.EstimoteOrientation.UNKNOWN;
            case VERTICAL:
                return EstimoteBeaconAttributesEvent.EstimoteOrientation.VERTICAL;
            case VERTICAL_UPSIDE_DOWN:
                return EstimoteBeaconAttributesEvent.EstimoteOrientation.VERTICAL_UPSIDE_DOWN;
        }

        return EstimoteBeaconAttributesEvent.EstimoteOrientation.UNKNOWN;
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
