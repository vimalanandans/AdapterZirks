package com.bezirk.adapter.obd.service;

import android.bluetooth.BluetoothSocket;
import android.support.annotation.NonNull;
import android.util.Log;

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

public class ObdController {
    private static final String TAG = ObdController.class.getName();
    private static final String NO_DATA = "NO DATA";

    private final BluetoothSocket sock;
    private final ExecutorService executor;

    public ObdController(BluetoothSocket sock) {
        this.sock = sock;
        initializeOBD();

        executor = Executors.newFixedThreadPool(10);
    }

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
            }
        } catch (IOException e) {
            Log.e(TAG, "Error initializing communication with OBD adapter", e);
            return false;
        } catch (InterruptedException e) {
            Log.e(TAG, "OBD initialization interrupted", e);
            Thread.currentThread().interrupt();
            return false;
        }

        Log.v(TAG, "Initializing OBD Device..Completed");
        return true;
    }

    public String executeCommand(@NonNull final ObdCommand command) throws Exception {
        String resultFinal;

        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws IOException, InterruptedException {
                String result = null;
                try {
                    if (sock.isConnected()) {
                        Log.d(TAG, "Now invoking Command: " + command.getName());
                        command.run(sock.getInputStream(), sock.getOutputStream());
                        result = command.getCalculatedResult();
                        Log.d(TAG, "Fetched results of Command: " + result);
                    } else {
                        Log.d(TAG, "Can't run command on a closed socket.");
                    }
                } catch (MisunderstoodCommandException | NoDataException e) {
                    result = NO_DATA;
                    Log.e(TAG, "Failed to execute command: " + command.getName(), e);
                }

                return result;
            }
        };

        final Future<String> task = executor.submit(callable);
        try {
            resultFinal = task.get(25000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            task.cancel(true);
            executor.shutdownNow();
            Log.e(TAG, "Failed to complete execution of command: " + command.getName(), e);
            throw e;
        }
        return resultFinal;
    }
}
