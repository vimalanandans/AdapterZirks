package com.bezirk.adapterzirks.estimote;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView statusTxtView = (TextView) findViewById(R.id.statusTxtView);

        AppManager.getAppManager().startBezirk(this, true, "Integrated Bezirk", null);
        bezirk = BezirkMiddleware.registerZirk(this, "Estimote Adapter Test");

        estimoteAdapter = new EstimoteAdapter(bezirk, getApplicationContext());

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


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check 
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    //@Override 
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
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
        estimoteAdapter.resume(this);
    }
}
