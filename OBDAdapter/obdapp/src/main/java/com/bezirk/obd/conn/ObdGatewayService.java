package com.bezirk.obd.conn;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.bezirk.adapter.obd.constants.CommandConstants;
import com.bezirk.adapter.obd.service.ObdAdapter;
import com.bezirk.adapter.obd.service.ObdController;
import com.bezirk.obd.constants.Constants;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ObdGatewayService  {

    private static final String TAG = ObdGatewayService.class.getName();
    SharedPreferences prefs;
    protected boolean isRunning = false;
    private Context ctx = null;
    private BluetoothDevice dev = null;
    private BluetoothSocket sock = null;
    ObdAdapter obdAdapter;

    public ObdGatewayService(Context ctx)
    {
        this.ctx = ctx;
        prefs = this.ctx.getSharedPreferences("user_options", Context.MODE_PRIVATE);
    }

    public void startService() throws IOException {
        Log.d(TAG, "Starting service..");

        // get the remote Bluetooth device
        final String remoteDevice = prefs.getString(Constants.BLUETOOTH_DEVICE_ADD_SELECTED, null);

        if (remoteDevice == null || "".equals(remoteDevice)) {
            Toast.makeText(ctx, Constants.NO_DEVICE_SELECTED, Toast.LENGTH_LONG).show();
            Log.e(TAG, "No Bluetooth device has been selected.");
            stopService();
            throw new IOException();
        } else {
            final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
            dev = btAdapter.getRemoteDevice(remoteDevice);

            Log.d(TAG, "Stopping Bluetooth discovery.");
            btAdapter.cancelDiscovery();

            //Show notification here if necessary
            try {
                startObdConnection();
            } catch (Exception e) {
                Log.e(TAG, "There was an error while establishing connection. -> " + e.getMessage());
                stopService();
                throw new IOException();
            }
        }
    }

    private void startObdConnection() throws IOException {
        Log.d(TAG, "Starting OBD connection..");
        try {
            sock = BluetoothManager.connect(dev);
            isRunning = true;
            //uncomment the below line
            //obdAdapter = new ObdAdapter(bezirk, sock);
        } catch (Exception e2) {
            Log.e(TAG, "There was an error while establishing Bluetooth connection. Stopping app..", e2);
            stopService();
            throw new IOException();
        }
    }

    public Map fetchOBDData() throws IOException {
        Map resultMap = null;
        Log.d(TAG, "Fetching OBD Data...");
        try {
            //call the send event method
            resultMap = new HashMap();
            resultMap.put(CommandConstants.ENGINE_RPM, "500");
            resultMap.put(CommandConstants.ERR_CODES, "P1008, P6777");

        } catch (Exception e2) {
            Log.e(TAG, "There was an error while establishing Bluetooth connection. Stopping app..", e2);
            stopService();
            throw new IOException();
        }
        return resultMap;
    }

    public void stopService() {
        Log.d(TAG, "Stopping service..");

        isRunning = false;

        if (sock != null)
            // close socket
            try {
                sock.close();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
    }


    public boolean isRunning() {
        return isRunning;
    }
}