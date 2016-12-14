package com.bezirk.adapter.obd.service;

import android.nfc.Tag;
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

    private final Runnable queueRunnable = new Runnable() {
        @Override
        public void run() {
            if (commandQueue.isEmpty()) {
                addCommandsToQueue();
            }
            handler.postDelayed(queueRunnable, 200);
        }

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
                if(lowCommandCount == 0)
                    Log.d(TAG, "ALERT Queue START");
                Log.d(TAG, "ALERT Added to Queue:"+highCommand.getName());
                for (; lowCommandCount < lowFrequencyCommands.size(); lowCommandCount++) {
                    lowCommand = lowFrequencyCommands.get(lowCommandCount);
                    commandQueue.add(lowCommand);
                    Log.d(TAG, "ALERT Added to Queue:"+lowCommand.getName() + "Lowcommand count:"+lowCommandCount);
                    itemCount++;

                    if (itemCount == lowCommandWindow) {
                        break;
                    }
                }
                if (lowCommandCount == lowFrequencyCommands.size()-1) {
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

    public void stopQueueAddition() {
        handler.removeCallbacks(queueRunnable);
        commandQueue.clear();
    }

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
                    Log.d(TAG,"added H "+command.getName());
                } else {
                    lowFrequencyCommands.add(command);
                    Log.d(TAG,"added L "+command.getName());
                }
            }
        }
        new Handler().post(queueRunnable);
    }
}
