package com.bezirk.adapter.obd.service;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.bezirk.adapter.obd.constants.CommandConstants;
import com.bezirk.adapter.obd.events.ResponseObdLiveDataEvent;
import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.exceptions.UnsupportedCommandException;

import java.io.IOException;

/**
 * Created by DEV6KOR on 9/8/2016.
 */
public class ObdController {

    private BluetoothSocket sock = null;
    private static final String TAG = ObdController.class.getName();
    public ObdController(BluetoothSocket sock)
    {
        this.sock = sock;
    }

    public ResponseObdLiveDataEvent getObdLiveData(String attribute)
    {
        ObdCommand command = null;

        if(CommandConstants.ENGINE_RPM.equals(attribute)) {
            command = new RPMCommand();
            command.useImperialUnits(true);
            try {
                command.run(sock.getInputStream(), sock.getOutputStream());
            }
            catch (UnsupportedCommandException u) {
                Log.d(TAG, "Command not supported. -> " + u.getMessage());
            } catch (IOException io) {
                Log.e(TAG, "IO error. -> " + io.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "Failed to run command. -> " + e.getMessage());
            }
        }
        return new ResponseObdLiveDataEvent(command.getFormattedResult());
    }
}
