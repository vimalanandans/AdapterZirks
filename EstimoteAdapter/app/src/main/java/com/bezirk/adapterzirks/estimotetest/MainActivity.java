package com.bezirk.adapterzirks.estimotetest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.bezirk.componentManager.AppManager;
import com.bezirk.hardwareevents.beacon.Beacon;
import com.bezirk.hardwareevents.beacon.BeaconsDetectedEvt;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.bezirk.middleware.proxy.android.BezirkMiddleware;

public class MainActivity extends AppCompatActivity {

    private Bezirk bezirk;
    private EstimoteAdapter estimoteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView statusTxtView = (TextView) findViewById(R.id.statusTxtView);

        AppManager.getAppManager().startBezirk(this, true, "Integrated Bezirk", null);
        bezirk = BezirkMiddleware.registerZirk(this, "Estimote Adapter Test");

        estimoteAdapter = new EstimoteAdapter(bezirk, this);

        EventSet eventSet = new EventSet(BeaconsDetectedEvt.class);
        eventSet.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint zirkEndPoint) {
                if (event instanceof BeaconsDetectedEvt) {
                    BeaconsDetectedEvt beaconsDetectedEvt = (BeaconsDetectedEvt)event;
                    Beacon closestBeacon = beaconsDetectedEvt.beacons.get(0);
                    if (closestBeacon.id.equals("7d8fc2d3b67ea8a0")) {
                        System.out.println("Found my bed!");
                    }
                }
            }
        });

        bezirk.subscribe(eventSet);

    }

    @Override
    public void onStart() {
        super.onStart();
        estimoteAdapter.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        estimoteAdapter.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        estimoteAdapter.onResume();
    }
}
