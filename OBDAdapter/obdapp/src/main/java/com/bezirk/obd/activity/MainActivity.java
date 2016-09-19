package com.bezirk.obd.activity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bezirk.adapter.obd.constants.CommandConstants;
import com.bezirk.adapter.obd.events.ResponseObdErrorCodesEvent;
import com.bezirk.adapter.obd.events.ResponseObdLiveDataEvent;
import com.bezirk.hardwareevents.beacon.Beacon;
import com.bezirk.hardwareevents.beacon.BeaconsDetectedEvent;
import com.bezirk.hardwareevents.beacon.GetBeaconAttributesEvent;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.android.BezirkMiddleware;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.bezirk.obd.app.R;
import com.bezirk.obd.conn.ObdGatewayService;
import com.bezirk.obd.constants.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    SharedPreferences pref;
    ArrayList deviceStrs;
    Context appContext;
    ObdGatewayService service;
    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BezirkMiddleware.initialize(this);
        final Bezirk bezirk = BezirkMiddleware.registerZirk("OBD Adapter");
        Log.d(TAG, "Created Bezirk Instance!!");

        final EventSet eventSet = new EventSet(ResponseObdErrorCodesEvent.class, ResponseObdLiveDataEvent.class);

        eventSet.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                System.out.println("REcevied event!!!!!!!!!!!!!!!!");
                if (event instanceof ResponseObdErrorCodesEvent) {
                    Log.v(TAG, "Received Event ResponseObdErrorCodesEvent");
                    ResponseObdErrorCodesEvent responseObdErrorCodesEvent = (ResponseObdErrorCodesEvent)event;
                    TextView errCodesTxtView = (TextView) findViewById( R.id.errorCodesValue );
                    errCodesTxtView.setText(responseObdErrorCodesEvent.getResult());
                }
                else if (event instanceof ResponseObdLiveDataEvent) {
                    Log.v(TAG, "Received Event ResponseObdLiveDataEvent");
                    ResponseObdLiveDataEvent responseObdLiveDataEvent = (ResponseObdLiveDataEvent)event;
                    TextView rpmTxtView = (TextView) findViewById( R.id.rpmValue );
                    rpmTxtView.setText(responseObdLiveDataEvent.getResult());
                }
            }
        });
        bezirk.subscribe(eventSet);

        pref=this.getSharedPreferences("user_options", MODE_PRIVATE);
        appContext = this.getApplicationContext();

        service = new ObdGatewayService(appContext, bezirk);

        deviceStrs = new ArrayList();
        final ArrayList devices = new ArrayList();

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0)
        {
            for (BluetoothDevice device : pairedDevices)
            {
                deviceStrs.add(device.getName() + "\n" + device.getAddress());
                devices.add(device.getAddress());
            }
        }

        Button selectDeviceBtn = (Button) findViewById(R.id.pairedDeviceButton);
        selectDeviceBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Toast.makeText(appContext, "Select a device", Toast.LENGTH_LONG).show();
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

                ArrayAdapter adapter = new ArrayAdapter(appContext, android.R.layout.select_dialog_singlechoice,
                        deviceStrs.toArray(new String[deviceStrs.size()]));

                int userChoice = pref.getInt(Constants.BLUETOOTH_DEVICE_POS_SELECTED, -1);
                alertDialog.setSingleChoiceItems(adapter, userChoice, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        int position = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        String deviceAddress = devices.get(position).toString();
                        System.out.println("deviceaddress::"+deviceStrs.get(position) +        "        "+position);
                        // TODO save deviceAddress
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putInt(Constants.BLUETOOTH_DEVICE_POS_SELECTED,position).apply();
                        editor.putString(Constants.BLUETOOTH_DEVICE_ADD_SELECTED,deviceAddress).apply();

                        TextView selectedDevice = (TextView) findViewById( R.id.selectedDevice );
                        selectedDevice.setText(deviceStrs.get(position).toString());

                        try {
                            service.startService();
                        }
                            catch(Exception e){
                                e.printStackTrace();
                            }
                        boolean connStatus = service.isRunning();
                        TextView obdConnStatus = (TextView) findViewById( R.id.obdConnStatusValue );
                        if(connStatus == true)
                            obdConnStatus.setText(Constants.CONNECTED);
                        else
                            obdConnStatus.setText(Constants.DISCONNECTED);
                    }
                });

                alertDialog.setTitle("Choose Bluetooth device");
                alertDialog.show();
            }
        });


        Button fetchDataBtn = (Button) findViewById(R.id.fetchData);
        fetchDataBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Toast.makeText(appContext, "Button Clicked", Toast.LENGTH_LONG).show();
                try {

                    service.fetchOBDData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
