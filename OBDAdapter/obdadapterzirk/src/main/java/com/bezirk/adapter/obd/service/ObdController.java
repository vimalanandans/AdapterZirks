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
import com.github.pires.obd.exceptions.NoDataException;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by DEV6KOR on 9/8/2016.
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

    public boolean initializeOBD() throws Exception{
        Log.d(TAG, "Initializing OBD Device..");
        try {
            if (sock.isConnected()) {
                new ObdResetCommand().run(sock.getInputStream(), sock.getOutputStream());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Log.e(TAG, "Error Initializing OBD Device", e);
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
        Log.d(TAG, "Initializing OBD Device..Completed");
        return true;
    }

/*    public ResponseObdErrorCodesEvent getObdErrorCodes(String attribute) throws Exception{
        ObdCommand command;

        String result = null;
        if (CommandConstants.ERR_CODES.equals(attribute)) {
            command = new ModifiedTroubleCodesObdCommand();
            command.useImperialUnits(true);
            result = executeCommand(command);
        }
        return new  ResponseObdErrorCodesEvent(result);
    }*/

    String executeCommand(final ObdCommand command) throws Exception {
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
                } catch (NoDataException e) {
                    result = CommandConstants.NO_DATA;
                    Log.e(TAG, e.getMessage());
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                    throw e;
                }
                finally {
                }
                return result;
            }
        };

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

/*    public class ModifiedTroubleCodesObdCommand extends TroubleCodesCommand {
        @Override
        public String getResult() {
            return rawData.replace("SEARCHING...", "").replace("NODATA", "");
        }
    }*/
}
