package com.bezirk.adapter.obd.service;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.bezirk.adapter.obd.constants.CommandConstants;
import com.bezirk.adapter.obd.events.ResponseObdErrorCodesEvent;
import com.bezirk.adapter.obd.events.ResponseObdLiveDataEvent;
import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.control.TroubleCodesCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.ObdResetCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.enums.ObdProtocols;
import com.github.pires.obd.exceptions.MisunderstoodCommandException;
import com.github.pires.obd.exceptions.NoDataException;
import com.github.pires.obd.exceptions.UnableToConnectException;

import java.io.IOException;

/**
 * Created by DEV6KOR on 9/8/2016.
 */
public class ObdController {

    private BluetoothSocket sock = null;
    private static final String TAG = ObdController.class.getName();

    public ObdController(BluetoothSocket sock) {
        this.sock = sock;
    }

    public boolean initializeOBD() {
        try {
            new ObdResetCommand().run(sock.getInputStream(), sock.getOutputStream());
            try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }

            new EchoOffCommand().run(sock.getInputStream(), sock.getOutputStream());
            new LineFeedOffCommand().run(sock.getInputStream(), sock.getOutputStream());
            new SelectProtocolCommand(ObdProtocols.AUTO).run(sock.getInputStream(), sock.getOutputStream());

            new SelectProtocolCommand(ObdProtocols.valueOf(CommandConstants.PROTOCOL));

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
            //Close socket if applicable
        }
        return true;
    }

    public ResponseObdLiveDataEvent getObdLiveData(String attribute) {
        ObdCommand command = null;

        if (CommandConstants.ENGINE_RPM.equals(attribute)) {
            command = new RPMCommand();
            command.useImperialUnits(true);
            try {
                command.run(sock.getInputStream(), sock.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
                return null;
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
                return null;
            } catch (UnableToConnectException e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
                return null;
            } catch (MisunderstoodCommandException e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
                return null;
            } catch (NoDataException e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            } finally {
                //Close socket if applicable
            }
        }
        return new ResponseObdLiveDataEvent(command.getCalculatedResult());
    }

    public ResponseObdErrorCodesEvent getObdErrorCodes(String attribute) {
        String result = null;
        if (CommandConstants.ERR_CODES.equals(attribute)) {
            try {
//                Log.d(TAG, "Queueing jobs for connection configuration..");
//                new ObdResetCommand().run(sock.getInputStream(), sock.getOutputStream());
//                new EchoOffCommand().run(sock.getInputStream(), sock.getOutputStream());
//                new LineFeedOffCommand().run(sock.getInputStream(), sock.getOutputStream());
//                new SelectProtocolCommand(ObdProtocols.AUTO).run(sock.getInputStream(), sock.getOutputStream());

                ModifiedTroubleCodesObdCommand tcoc = new ModifiedTroubleCodesObdCommand();
                tcoc.run(sock.getInputStream(), sock.getOutputStream());
                result = tcoc.getFormattedResult();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
                return null;
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
                return null;
            } catch (UnableToConnectException e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
                return null;
            } catch (MisunderstoodCommandException e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
                return null;
            } catch (NoDataException e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            } finally {
                //Close socket if applicable
            }
        }
        return new ResponseObdErrorCodesEvent(result);
    }
    public class ModifiedTroubleCodesObdCommand extends TroubleCodesCommand {
        @Override
        public String getResult() {
            // remove unwanted response from output since this results in erroneous error codes
            return rawData.replace("SEARCHING...", "").replace("NODATA", "");
        }
    }
}
