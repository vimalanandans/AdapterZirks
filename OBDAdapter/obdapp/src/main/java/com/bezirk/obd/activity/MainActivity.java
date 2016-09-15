package com.bezirk.obd.activity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.bezirk.obd.app.R;
import com.bezirk.obd.constants.Constants;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    SharedPreferences pref;
    ArrayList deviceStrs;
    Context appContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref=this.getSharedPreferences("user_options", MODE_PRIVATE);
        appContext = this.getApplicationContext();
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

        // show list



        Button button = (Button) findViewById(R.id.pairedDeviceButton);
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Toast.makeText(appContext, "Button Clicked", Toast.LENGTH_LONG).show();
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

                ArrayAdapter adapter = new ArrayAdapter(appContext, android.R.layout.select_dialog_singlechoice,
                        deviceStrs.toArray(new String[deviceStrs.size()]));

                int userChoice = pref.getInt(Constants.BLUETOOTH_SELECTED, -1);
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
                        editor.putInt(Constants.BLUETOOTH_SELECTED,position).apply();
                    }
                });

                alertDialog.setTitle("Choose Bluetooth device");
                alertDialog.show();
            }
        });
    }


}
