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
    private static final List<String> highFrequencyParams = Arrays.asList(
            OBDQueryParameter.ENGINE_RPM.getValue(),
            OBDQueryParameter.SPEED.getValue()
    );
    private List<ObdCommand> highFrequencyCommands;
    private List<ObdCommand> lowFrequencyCommands;
    private Handler handler;

    /**
     * Runnable Task to add the commands to blocking queue. Once the addCommandsToQueue is invoked,
     * a delay is 200 milli seconds is introduced, and again the Runnable is invoked.
     */
    private final Runnable queueRunnable = new Runnable() {
        @Override
        public void run() {
            if (commandQueue.isEmpty()) {
                addCommandsToQueue();
            }
            handler.postDelayed(queueRunnable, 200);
        }

        /**
         * This method mixes the high frequency commands and low frequency commands and adds to the blocking queue
         * High Frequency Commands are those commands for which values need to be retrieved very frequently (say once every 500ms)
         * Ex. Vehicle Speed, RPM
         * Low Frequency Commands are commands for which values are retrieved less frequently, because they change over a period of
         * time (say for every 10 seconds). Ex. Coolant Temperature, Engine oil temperature.
         *
         * The below method adds one high frequency command followed by 2 low frequency command into the queue, repititively
         * Ex: RPM(high) -> EngineCoolantTemp(low) -> EngineOilTemp(low) -> Speed(high) -> ErrorCode(low) -> FuelLevel(low) -> RPM(high) -> ...
         */
        private void addCommandsToQueue() {
            Log.d(TAG, "addCommandsToQueue");
            ObdCommand lowCommand;
            ObdCommand highCommand;
            int lowCommandCount = 0;
            int highCommandCount = 0;
            int lowCommandWindow = 2;
            int itemCount = 0;
            for (; highCommandCount < highFrequencyCommands.size(); highCommandCount++) {
                highCommand = highFrequencyCommands.get(highCommandCount);
                commandQueue.add(highCommand);
                for (; lowCommandCount < lowFrequencyCommands.size(); lowCommandCount++) {
                    lowCommand = lowFrequencyCommands.get(lowCommandCount);
                    commandQueue.add(lowCommand);
                    itemCount++;

                    if (itemCount == lowCommandWindow) {
                        break;
                    }
                }
                if (lowCommandCount == lowFrequencyCommands.size() - 1) {
                    break;
                }
                if (itemCount == lowCommandWindow) {
                    itemCount = 0;
                    lowCommandCount++;
                }
                if (highCommandCount == highFrequencyCommands.size() - 1) {
                    highCommandCount = -1;
                }
            }
        }
    };

    /**
     * This method stops the addition of commands to the blocking queue by stopping the handler executing the Runnable
     * and also clears the commands in the blocking queue.
     * This is called, when the OBD data fetch needs to be stopped.
     */
    public void stopQueueAddition() {
        handler.removeCallbacks(queueRunnable);
        commandQueue.clear();
    }

    /**
     * This method prepares the list of high frequency and low frequency commands. (see comments above for description on Low
     * and high frequency commands.
     * It also fetches the actual command objects from an Array List
     * @param parameters List of commands to be queried
     */
    public void prepareCommandsToQueue(List<OBDQueryParameter> parameters) {
        Log.v(TAG, "queueCommands");
        final List<String> obdStrQueryParams = new ArrayList<>();

        for (OBDQueryParameter queryParameter : parameters) {
            obdStrQueryParams.add(queryParameter.getValue());
        }

        handler = new Handler(Looper.getMainLooper());
        highFrequencyCommands = new ArrayList<>();
        lowFrequencyCommands = new ArrayList<>();

        for (ObdCommand command : OBDCommandConfig.getCommands()) {
            if (obdStrQueryParams.contains(command.getName())) {
                if (highFrequencyParams.contains(command.getName())) {
                    highFrequencyCommands.add(command);
                } else {
                    lowFrequencyCommands.add(command);
                }
            }
        }
        new Handler().post(queueRunnable);
    }
}
