package com.bezirk.adapter.obd.service;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.bezirk.adapter.obd.config.OBDCommandConfig;
import com.bezirk.adapter.obd.enums.OBDQueryParameter;
import com.github.pires.obd.commands.ObdCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class manages and prepares a blocking queue of commands and also performs add/clear operations on the queue.
 */
public class QueueService {
    protected static final BlockingQueue<ObdCommand> commandQueue = new LinkedBlockingQueue<>();
    private static final String TAG = QueueService.class.getName();
    private static final List<OBDQueryParameter> availableHighFrequencyCommandsEnums = Arrays.asList(
            OBDQueryParameter.ENGINE_RPM,
            OBDQueryParameter.SPEED
    );
    private List<OBDQueryParameter> highFrequencyCommandsEnums;
    private List<OBDQueryParameter> lowFrequencyCommandsEnums;
    private Handler handler;
    private int delayTimeOBDParameters = 200;
    private int delayTimeErrorCode = 200;
    public static final String ERROR_CODE = "ERROR_CODE";
    public static final String ALL_PARAMS = "ALL_PARAMS";

    /**
     * Runnable Task to add the commands to blocking queue. Once the addCommandsToQueue is invoked,
     * a delay is 200 milli seconds is introduced, and again the Runnable is invoked.
     */
    private final Runnable runnableOBDParameters = new Runnable() {
        @Override
        public void run() {
            if (commandQueue.isEmpty()) {
                addOBDCommandsToQueue();
            }
            handler.postDelayed(runnableOBDParameters, QueueService.this.delayTimeOBDParameters);
        }

        /**
         * This method mixes the high frequency commands and low frequency commands and adds to the blocking queue
         * High Frequency Commands are those commands for which values need to be retrieved very frequently (say once every 500ms)
         * Ex. Vehicle Speed, RPM
         * Low Frequency Commands are commands for which values are retrieved less frequently, because they change over a period of
         * time (say for every 10 seconds). Ex. Coolant Temperature, Engine oil temperature.
         *
         * The below method adds one high frequency command followed by 2 low frequency command into the queue, repetitively
         * Ex: RPM(high) -> EngineCoolantTemp(low) -> EngineOilTemp(low) -> Speed(high) -> ErrorCode(low) -> FuelLevel(low) -> RPM(high) -> ...
         */
        private void addOBDCommandsToQueue() {
            Log.v(TAG, "addOBDCommandsToQueue");
            int lowCommandCount = 0;
            int highCommandCount = 0;
            int lowCommandWindow = 2;
            int itemCount = 0;

            if(highFrequencyCommandsEnums.size() == 0) {
                for (; lowCommandCount < lowFrequencyCommandsEnums.size(); lowCommandCount++) {
                    final OBDQueryParameter lowCommandEnum = lowFrequencyCommandsEnums.get(lowCommandCount);
                    commandQueue.add(OBDCommandConfig.getOBDCommand(lowCommandEnum));
                }
            }
            else
            {
                for (; highCommandCount < highFrequencyCommandsEnums.size(); highCommandCount++) {
                    OBDQueryParameter highCommandEnum = highFrequencyCommandsEnums.get(highCommandCount);
                    commandQueue.add(OBDCommandConfig.getOBDCommand(highCommandEnum));
                    for (; lowCommandCount < lowFrequencyCommandsEnums.size(); lowCommandCount++) {
                        final OBDQueryParameter lowCommandEnum = lowFrequencyCommandsEnums.get(lowCommandCount);
                        commandQueue.add(OBDCommandConfig.getOBDCommand(lowCommandEnum));
                        itemCount++;

                        if (itemCount == lowCommandWindow) {
                            lowCommandCount++;
                            break;
                        }
                    }
/*                    if (lowCommandCount == lowFrequencyCommandsEnums.size() - 1) {
                        break;
                    }*/
                    if (lowCommandCount < lowFrequencyCommandsEnums.size() - 1){
                        if (itemCount == lowCommandWindow) {
                            itemCount = 0;
                        }
                        if (highCommandCount == highFrequencyCommandsEnums.size() - 1) {
                            highCommandCount = -1;
                        }
                    }
                }
            }
        }
    };

    /**
     * Runnable Task to add the commands to blocking queue. Once the addCommandsToQueue is invoked,
     * a delay is 200 milli seconds is introduced, and again the Runnable is invoked.
     */
    private final Runnable runnableErrorCode = new Runnable() {
        @Override
        public void run() {
            //if (commandQueue.isEmpty()) {
                addErrorCodeCommandToQueue();
            //}
            handler.postDelayed(runnableErrorCode, QueueService.this.delayTimeErrorCode);
        }

        private void addErrorCodeCommandToQueue() {
            Log.v(TAG, "addErrorCodeCommandToQueue");
            commandQueue.add(OBDCommandConfig.getOBDCommand(OBDQueryParameter.TROUBLE_CODES));
        }
    };

    /**
     * This method stops the addition of commands to the blocking queue by stopping the handler executing the Runnable
     * and also clears the commands in the blocking queue.
     * This is called, when the OBD data fetch needs to be stopped.
     */
    public boolean stopQueueAddition(String attribute) {
        boolean isStopped = false;
        if (handler != null) {
            if (attribute.equals(ERROR_CODE)) {
                handler.removeCallbacks(runnableErrorCode);
                isStopped = true;
            } else if (attribute.equals(ALL_PARAMS)) {
                handler.removeCallbacks(runnableOBDParameters);
                isStopped = true;
            }
        }
        return isStopped;
    }

    /**
     * This method prepares the list of high frequency and low frequency commands. (see comments above for description on Low
     * and high frequency commands.
     *
     * @param obdParameters List of commands to be queried
     */
    public void queueOBDCommands(List<OBDQueryParameter> obdParameters, int delayTime) {
        Log.v(TAG, "queueCommands");
        this.delayTimeOBDParameters = delayTime;
        handler = new Handler(Looper.getMainLooper());
        lowFrequencyCommandsEnums = new ArrayList<>();
        highFrequencyCommandsEnums = new ArrayList<>();

        for (OBDQueryParameter obdQueryParameter : obdParameters) {
            if (availableHighFrequencyCommandsEnums.contains(obdQueryParameter)) {
                highFrequencyCommandsEnums.add(obdQueryParameter);
            }
            else{
                lowFrequencyCommandsEnums.add(obdQueryParameter);
            }
        }
        new Handler().post(runnableOBDParameters);
    }

    public void queueErrorCodeCommand(int delayTime) {
        Log.v(TAG, "queueCommands");
        this.delayTimeErrorCode = delayTime;
        handler = new Handler(Looper.getMainLooper());
        new Handler().post(runnableErrorCode);
    }
}
