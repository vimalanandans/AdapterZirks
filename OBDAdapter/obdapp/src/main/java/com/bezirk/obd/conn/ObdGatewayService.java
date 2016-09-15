package com.bezirk.obd.conn;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.bezirk.obd.constants.Constants;

import java.io.IOException;

public class ObdGatewayService  {

    private static final String TAG = ObdGatewayService.class.getName();
    SharedPreferences prefs;
    protected boolean isRunning = false;
    private Context ctx = null;
    private BluetoothDevice dev = null;
    private BluetoothSocket sock = null;

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
        isRunning = true;
        try {
            sock = BluetoothManager.connect(dev);
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