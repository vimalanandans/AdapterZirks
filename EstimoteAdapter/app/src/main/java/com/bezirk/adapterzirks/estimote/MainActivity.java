package com.bezirk.adapterzirks.estimote;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final String TAG = MainActivity.class.getName();

    private EstimoteAdapter estimoteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView statusTxtView = (TextView) findViewById(R.id.statusTxtView);

        AppManager.getAppManager().startBezirk(this, true, "Integrated Bezirk", null);
        final Bezirk bezirk = BezirkMiddleware.registerZirk(this, "Estimote Adapter Test");

        estimoteAdapter = new EstimoteAdapter(bezirk, getApplicationContext());

        EventSet eventSet = new EventSet(BeaconsDetectedEvt.class);
        eventSet.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint zirkEndPoint) {
                if (event instanceof BeaconsDetectedEvt) {
                    final BeaconsDetectedEvt beaconsDetectedEvt = (BeaconsDetectedEvt) event;
                    boolean foundMyCar = false;
                    for (Beacon beacon : beaconsDetectedEvt.getBeacons()) {
                        if ("9fd9a34e0dd90566".equals(beacon.getId())) {
                            final String foundCar = getString(R.string.found_car);

                            Log.d(TAG, foundCar);
                            statusTxtView.setText(foundCar);
                            foundMyCar = true;
                        }
                    }

                    if (!foundMyCar) {
                        final String lostCar = getString(R.string.lost_car);

                        Log.d(TAG, lostCar);
                        statusTxtView.setText(lostCar);
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
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will " +
                            "not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        estimoteAdapter.start();
    }

    // Use onDestroy instead if the app should keep receiving beacon notifications
    // while not in the foreground or when the screen is off.
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
