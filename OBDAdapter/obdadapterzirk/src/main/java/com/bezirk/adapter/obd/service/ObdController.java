package com.bezirk.adapter.obd.service;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.bezirk.adapter.obd.config.CommandConstants;
import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.ObdResetCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.enums.ObdProtocols;
import com.github.pires.obd.exceptions.MisunderstoodCommandException;
import com.github.pires.obd.exceptions.NoDataException;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/***
 * This class is used to initialize the OBD Dongle with init commands and also to fetch the OBD data by writing to the Socket
 */
public class ObdController {
    private BluetoothSocket sock = null;
    private static final String TAG = ObdController.class.getName();
    private ExecutorService executor;

    public ObdController(BluetoothSocket sock) {
        this.sock = sock;
        try {
            initializeOBD();
        }
        catch(Exception e){
            Log.e(TAG, "Error Initializing OBD Device");
        }
        executor = Executors.newFixedThreadPool(10);
    }

    /***
     * This method initializes the OBD connection with 4 commands. This will be a one time command for one session
     * @return boolean
     * @throws Exception
     */
    public boolean initializeOBD() throws Exception{
        Log.i(TAG, "Initializing OBD Device..");
        try {
            if (sock.isConnected()) {
                new ObdResetCommand().run(sock.getInputStream(), sock.getOutputStream());
                try {
                    //A sleep of 500ms is provided for the ObdResetCommand to ensure reset is completed
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Log.e(TAG, "Error Initializing OBD Device", e);
                    throw e;
                }
                new EchoOffCommand().run(sock.getInputStream(), sock.getOutputStream());
                new LineFeedOffCommand().run(sock.getInputStream(), sock.getOutputStream());
                new SelectProtocolCommand(ObdProtocols.AUTO).run(sock.getInputStream(), sock.getOutputStream());
            }else {
                Log.d(TAG, "Can't run command on a closed socket.");
            }
        } catch (NoDataException e) {
            Log.e(TAG, e.getMessage());
            return false;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            throw e;
        }
        finally {
        }
        Log.i(TAG, "Initializing OBD Device..Completed");
        return true;
    }

    /***
     * This method executes the OBD command via the run method and returns the result (String) fetched from OBD
     * If there is no data provided by the OBD (ecu), then the result will be "NO DATA" string
     * If the command is not supported, then it will trigger "MisunderstoodCommandException" and in turn returns "NO DATA"
     *
     * @param command
     * @return String Result from OBD query
     * @throws Exception
     */
    public String executeCommand(final ObdCommand command) throws Exception {
        String resultFinal;

        Callable<String> callable = new Callable<String>(){
            @Override
            public String call() throws IOException, InterruptedException {
                String result = null;
                try {
                    if (sock.isConnected()) {
                        Log.d(TAG, "Now invoking Command:"+ command.getName());
                        command.run(sock.getInputStream(), sock.getOutputStream());
                        result = command.getCalculatedResult();
                        Log.d(TAG, "Fetched results of Command :" + result);
                    } else {
                        Log.d(TAG, "Can't run command on a closed socket.");
                    }
                } catch (MisunderstoodCommandException|NoDataException e) {
                    result = CommandConstants.NO_DATA;
                    Log.e(TAG, e.getMessage());
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                    throw e;
                }
                return result;
            }
        };

        // Executor will time out after 25 Seconds if the response is not returned within this duration
        // and it will stop the further fetch process from OBD
        Future<String> task = executor.submit(callable);
        try{
            resultFinal = task.get(25000, TimeUnit.DAYS.MILLISECONDS);
        } catch (InterruptedException|ExecutionException|TimeoutException e) {
            task.cancel(true);
            executor.shutdownNow();
            Log.e(TAG, e.getMessage(), e);
            throw e;
        }
        return resultFinal;
    }
}
