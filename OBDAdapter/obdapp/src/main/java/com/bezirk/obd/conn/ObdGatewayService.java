package com.bezirk.obd.conn;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.bezirk.adapter.obd.constants.CommandConstants;
import com.bezirk.adapter.obd.events.RequestObdEngineRPMEvent;
import com.bezirk.adapter.obd.events.RequestObdErrorCodesEvent;
import com.bezirk.adapter.obd.service.ObdAdapter;
import com.bezirk.middleware.Bezirk;
import com.bezirk.obd.constants.Constants;

import java.io.IOException;
import java.util.Map;

public class ObdGatewayService  {

    private static final String TAG = ObdGatewayService.class.getName();
    SharedPreferences prefs;
    protected boolean isRunning = false;
    private Context ctx = null;
    private BluetoothDevice dev = null;
    private BluetoothSocket sock = null;
    ObdAdapter obdAdapter;
    Bezirk bezirk;

    public ObdGatewayService(Context ctx, Bezirk bezirk)
    {
        this.ctx = ctx;
        this.bezirk = bezirk;
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
            obdAdapter = new ObdAdapter(bezirk, sock);
        } catch (Exception e2) {
            Log.e(TAG, "There was an error while establishing Bluetooth connection. Stopping app..", e2);
            stopService();
            throw new IOException();
        }
    }

    public void fetchOBDData() throws IOException {
        Map resultMap = null;
        Log.d(TAG, "Fetching OBD Data...");
        try {
            //call the send event method
            Log.d(TAG, "Now sending Bezirk Send event for RequestObdEngineRPMEvent...");
            bezirk.sendEvent(new RequestObdEngineRPMEvent(CommandConstants.ENGINE_RPM));
            Log.d(TAG, "Now sending Bezirk Send event for RequestObdErrorCodesEvent...");
            bezirk.sendEvent(new RequestObdErrorCodesEvent(CommandConstants.ERR_CODES));
            Log.d(TAG, "Completed Sending both the events...");
        } catch (Exception e2) {
            Log.e(TAG, "There was an error while establishing Bluetooth connection. Stopping app..", e2);
            stopService();
            throw new IOException();
        }
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