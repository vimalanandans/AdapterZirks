package com.bezirk.adapter.obd.service;

import android.bluetooth.BluetoothSocket;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bezirk.adapter.obd.enums.OBDErrorMessages;
import com.bezirk.adapter.obd.events.ResponseObdStatusEvent;
import com.bezirk.middleware.Bezirk;
import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.ObdResetCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.enums.ObdProtocols;
import com.github.pires.obd.exceptions.MisunderstoodCommandException;
import com.github.pires.obd.exceptions.NoDataException;
import com.github.pires.obd.exceptions.UnableToConnectException;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * This class is used to initialize the OBD Dongle with init commands and also to fetch the OBD data by writing to the Socket
 */

public class ObdController {
    private static final String TAG = ObdController.class.getName();
    private static final String NO_DATA = "NO DATA";

    private BluetoothSocket sock;
    private final ExecutorService executor;
    private final Bezirk bezirk;

    public ObdController(final Bezirk bezirk) {
        this.bezirk = bezirk;
        //initializeOBD();

        executor = Executors.newFixedThreadPool(10);
    }

    /**
     * This method initializes the OBD connection with 4 commands. This will be a one time command for one session
     *
     * @return boolean To indicate success/failure of initialization of OBD device
     */
    public boolean initializeOBD() {
        Log.v(TAG, "Initializing OBD Device..");
        try {
            if (sock.isConnected()) {
                new ObdResetCommand().run(sock.getInputStream(), sock.getOutputStream());
                Thread.sleep(500);
                new EchoOffCommand().run(sock.getInputStream(), sock.getOutputStream());
                new LineFeedOffCommand().run(sock.getInputStream(), sock.getOutputStream());
                new SelectProtocolCommand(ObdProtocols.AUTO).run(sock.getInputStream(), sock.getOutputStream());
            } else {
                Log.d(TAG, "Can't run command on a closed socket.");
                return false;
            }
        }
        catch (UnableToConnectException | MisunderstoodCommandException | NoDataException e) {
            Log.e(TAG, "Failed to execute initialization command");
            Log.e(TAG, e.getMessage());
            return false;
        }
        catch (IOException e) {
            Log.e(TAG, "Error initializing communication with OBD adapter", e);
            bezirk.sendEvent(new ResponseObdStatusEvent(OBDErrorMessages.INIT_OBD_ERR, null, false));
            return false;
        } catch (InterruptedException e) {
            Log.e(TAG, "OBD initialization interrupted", e);
            bezirk.sendEvent(new ResponseObdStatusEvent(OBDErrorMessages.INIT_OBD_ERR, null, false));
            Thread.currentThread().interrupt();
            return false;
        }
        Log.i(TAG, "Initializing OBD Device..Completed");
        return true;
    }

    /**
     * This method executes the OBD command via the run method and returns the result (String) fetched from OBD
     * If there is no data provided by the OBD (ecu), then the result will be "NO DATA" string
     * If the command is not supported, then it will trigger "MisunderstoodCommandException" and in turn returns "NO DATA"
     *
     * @param command The Parameter to be fetched from OBD Device
     * @return String Result from OBD query
     * @throws Exception thrown, If the commands times out, or interrupted while execution
     */
    public String executeCommand(@NonNull final ObdCommand command)
            throws InterruptedException, ExecutionException, TimeoutException {
        String resultFinal;

        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws IOException, InterruptedException {
                String result;
                try {
                    if (sock != null && sock.isConnected()) {
                        Log.d(TAG, "Now invoking Command: " + command.getName());
                        command.useImperialUnits(true);
                        command.setResponseTimeDelay(new Long(100));
                        command.run(sock.getInputStream(), sock.getOutputStream());
                        result = command.getCalculatedResult();
                        Log.d(TAG, "Fetched results of Command: " + result);
                    } else {
                        Log.e(TAG, "Can't run command on a closed socket.");
                        throw new IOException();
                    }
                } catch (UnableToConnectException | MisunderstoodCommandException | NoDataException e) {
                    result = NO_DATA;
                    Log.e(TAG, "Failed to execute command: No Data: " + command.getName());
                    Log.e(TAG, e.getMessage());
                }
                return result;
            }
        };

        // Executor will time out after 25 Seconds if the response is not returned within this duration
        // and it will stop the further fetch process from OBD
        final Future<String> task = executor.submit(callable);
        try {
            resultFinal = task.get(25000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            task.cancel(true);
            Log.e(TAG, "Failed to complete execution of command: " + command.getName());
            throw e;
        }
        return resultFinal;
    }

    public void setBluetoothSocket(BluetoothSocket socket){
        this.sock = socket;
    }
}
