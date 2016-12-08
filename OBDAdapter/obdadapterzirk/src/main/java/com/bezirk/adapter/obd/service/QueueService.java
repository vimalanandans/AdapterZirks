package com.bezirk.adapter.obd.service;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.bezirk.adapter.obd.config.CommandConstants;
import com.bezirk.adapter.obd.config.OBDCommandConfig;
import com.bezirk.adapter.obd.enums.OBDQueryParameter;
import com.github.pires.obd.commands.ObdCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class QueueService {
    static protected BlockingQueue<ObdCommand> commandQueue = new LinkedBlockingQueue<>();
    private List<OBDQueryParameter> queryParameters;
    private List<String> obdStrQueryParams;
    private static final String TAG = QueueService.class.getName();
    private Handler handler;
    List<ObdCommand> highFrequencyCommands;
    List<ObdCommand> lowFrequencyCommands;

    public QueueService()
    {
    }

    private final Runnable queueRunnable = new Runnable() {
        public void run() {
            if (commandQueue.isEmpty()) {
                addCommandsToQueue();
            }
            handler.postDelayed(queueRunnable, 200);
        }
    };

    private void addCommandsToQueue()
    {
        Log.d(TAG, "addCommandsToQueue");
        ObdCommand lowCommand;
        ObdCommand highCommand;
        int lowCommandCount = 0;
        int highCommandCount = 0;
        int lowCommandWindow = 2;
        int itemCount = 0;
        for(;highCommandCount<highFrequencyCommands.size(); highCommandCount++){
            highCommand = highFrequencyCommands.get(highCommandCount);
            commandQueue.add(highCommand);
            for(;lowCommandCount<lowFrequencyCommands.size();lowCommandCount++ ){
                lowCommand = lowFrequencyCommands.get(lowCommandCount);
                commandQueue.add(lowCommand);
                itemCount++;

                if(itemCount == lowCommandWindow){
                    break;
                }
            }
            if(lowCommandCount == lowFrequencyCommands.size()){
                break;
            }
            if(itemCount == lowCommandWindow){
                itemCount = 0;
                lowCommandCount++;
            }
            if(highCommandCount == highFrequencyCommands.size()-1){
                highCommandCount = -1;
            }
        }
    }

    public void stopQueueAddition()
    {
        handler.removeCallbacks(queueRunnable);
        commandQueue.clear();
    }

    public void prepareCommandsToQueue(List<OBDQueryParameter> parameters)
    {
        Log.d(TAG, "queueCommands");
        this.queryParameters = parameters;
        obdStrQueryParams = new ArrayList();

        for(OBDQueryParameter queryParameter: queryParameters)
        {
            obdStrQueryParams.add(queryParameter.getValue());
        }

        handler = new Handler(Looper.getMainLooper());
        highFrequencyCommands = new ArrayList();
        lowFrequencyCommands = new ArrayList();

        for (ObdCommand command : OBDCommandConfig.getCommands()) {
            if (obdStrQueryParams.contains(command.getName())){
                if(CommandConstants.highFrequencyParams.contains(command.getName())) {
                    highFrequencyCommands.add(command);
                }
                else{
                    lowFrequencyCommands.add(command);
                }
            }
        }
        new Handler().post(queueRunnable);
    }
}
