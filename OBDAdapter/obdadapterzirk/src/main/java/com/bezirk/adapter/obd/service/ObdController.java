package com.bezirk.adapter.obd.service;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.bezirk.adapter.obd.constants.CommandConstants;
import com.bezirk.adapter.obd.events.ResponseObdCoolantTempEvent;
import com.bezirk.adapter.obd.events.ResponseObdEngineRPMEvent;
import com.bezirk.adapter.obd.events.ResponseObdErrorCodesEvent;
import com.bezirk.adapter.obd.events.ResponseObdVehicleSpeedEvent;
import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.control.TroubleCodesCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.ObdResetCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;
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
 * Created by DEV6KOR on 9/8/2016.
 */
public class ObdController {

    private BluetoothSocket sock = null;
    private static final String TAG = ObdController.class.getName();
    private ExecutorService executor;

    public ObdController(BluetoothSocket sock) {
        this.sock = sock;
        initializeOBD();
        executor = Executors.newFixedThreadPool(10);
    }

    public boolean initializeOBD() {
        Log.d(TAG, "Initializing OBD Device..");
        try {
            if (sock.isConnected()) {
                new ObdResetCommand().run(sock.getInputStream(), sock.getOutputStream());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                new EchoOffCommand().run(sock.getInputStream(), sock.getOutputStream());
                new LineFeedOffCommand().run(sock.getInputStream(), sock.getOutputStream());
                new SelectProtocolCommand(ObdProtocols.AUTO).run(sock.getInputStream(), sock.getOutputStream());
            }else {
                Log.e(TAG, "Can't run command on a closed socket.");
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
            return false;
        } catch (UnableToConnectException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
            return false;
        } catch (MisunderstoodCommandException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
            return false;
        } catch (NoDataException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        } finally {
        }
        Log.d(TAG, "Initializing OBD Device..Completed");
        return true;
    }

    public ResponseObdEngineRPMEvent getEngineRPM(String attribute) throws Exception{
        ObdCommand command;

        String result = null;
        if (CommandConstants.ENGINE_RPM.equals(attribute)) {
            command = new RPMCommand();
            command.useImperialUnits(true);
            result = executeCommand(command);
        }
        return new  ResponseObdEngineRPMEvent(result);
    }

    public ResponseObdCoolantTempEvent getCoolantTemp(String attribute) throws Exception{
        ObdCommand command;

        String result = null;
        if (CommandConstants.COOLANT_TEMP.equals(attribute)) {
            command = new EngineCoolantTemperatureCommand();
            command.useImperialUnits(true);
            result = executeCommand(command);
        }
        return new ResponseObdCoolantTempEvent(result);
    }

    public ResponseObdVehicleSpeedEvent getObdVehicleSpeed(String attribute) throws Exception{
        ObdCommand command;

        String result = null;
        if (CommandConstants.VEH_SPEED.equals(attribute)) {
            command = new SpeedCommand();
            command.useImperialUnits(true);
            result = executeCommand(command);
        }
        return new ResponseObdVehicleSpeedEvent(result);
    }

    public ResponseObdErrorCodesEvent getObdErrorCodes(String attribute) throws Exception{
        ObdCommand command;

        String result = null;
        if (CommandConstants.ERR_CODES.equals(attribute)) {
            command = new ModifiedTroubleCodesObdCommand();
            command.useImperialUnits(true);
            result = executeCommand(command);
        }
        return new  ResponseObdErrorCodesEvent(result);
    }


    String executeCommand(final ObdCommand command) throws Exception {
        String resultFinal = "";

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
                        Log.e(TAG, "Can't run command on a closed socket.");
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "IOException");
                    Log.e(TAG, e.getMessage());
                    throw e;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.e(TAG, "InterruptedException");
                    Log.e(TAG, e.getMessage());
                    throw e;
                } catch (UnableToConnectException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                    Log.e(TAG, "UnableToConnectException");
                    throw e;
                } catch (MisunderstoodCommandException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                    Log.e(TAG, "MisunderstoodCommandException");
                    throw e;
                } catch (NoDataException e) {
                    //e.printStackTrace();
                    result = CommandConstants.NO_DATA;
                    Log.e(TAG, e.getMessage());
                    Log.e(TAG, "NoDataException");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                    Log.e(TAG, "Exception");
                    throw e;
                } finally {
                    Log.e(TAG, "Finally");
                    //Close socket if applicable
                }
                return result;
            }
        };

        Future<String> task = executor.submit(callable);
        try{
            resultFinal = task.get(25000, TimeUnit.DAYS.MILLISECONDS);
        } catch (InterruptedException e) {
            Log.e(TAG, "InterruptedException:5000");
            task.cancel(true);
            executor.shutdownNow();
            e.printStackTrace();
            throw new Exception(e);
        } catch (ExecutionException e) {
            Log.e(TAG, "ExecutionException:5000");
            task.cancel(true);
            executor.shutdownNow();
            e.printStackTrace();
            throw new Exception(e);
        } catch (TimeoutException e) {
            Log.e(TAG, "TimeoutException:5000");
            task.cancel(true);
            executor.shutdownNow();
            e.printStackTrace();
            throw new Exception(e);
        }
        return resultFinal;
    }

    public class ModifiedTroubleCodesObdCommand extends TroubleCodesCommand {
        @Override
        public String getResult() {
            return rawData.replace("SEARCHING...", "").replace("NODATA", "");
        }
    }
}
