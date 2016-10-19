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
        initializeOBD();
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
            //Close socket if applicable
        }
        Log.d(TAG, "Initializing OBD Device..Completed");
        return true;
    }

    public ResponseObdLiveDataEvent getObdLiveData(String attribute) {
        ObdCommand command = null;
        String result = null;
        if (CommandConstants.ENGINE_RPM.equals(attribute)) {
            command = new RPMCommand();
            command.useImperialUnits(true);
            try {
                if (sock.isConnected()) {
                    Log.d(TAG, "Now invoking RPMCommand");
                    command.run(sock.getInputStream(), sock.getOutputStream());
                    result = command.getCalculatedResult();
                    Log.d(TAG, "Fetched results of RPMCommand :"+result);
                }else {
                    Log.e(TAG, "Can't run command on a closed socket.");
                }
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
                //e.printStackTrace();
                Log.e(TAG, e.getMessage());
                result = CommandConstants.NO_DATA;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            } finally {
                //Close socket if applicable
            }
        }
        return new ResponseObdLiveDataEvent(result);
    }

    public ResponseObdErrorCodesEvent getObdErrorCodes(String attribute) {
        String result = null;
        if (CommandConstants.ERR_CODES.equals(attribute)) {
            try {
                if (sock.isConnected()) {
                    Log.d(TAG, "Now invoking ModifiedTroubleCodesObdCommand");
                    ModifiedTroubleCodesObdCommand tcoc = new ModifiedTroubleCodesObdCommand();
                    tcoc.run(sock.getInputStream(), sock.getOutputStream());
                    result = tcoc.getFormattedResult();
                    Log.d(TAG, "Fetched results of ModifiedTroubleCodesObdCommand :"+result);
                }else {
                    Log.e(TAG, "Can't run command on a closed socket.");
                }
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
                //e.printStackTrace();
                Log.e(TAG, e.getMessage());
                result = CommandConstants.NO_DATA;
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
            return rawData.replace("SEARCHING...", "").replace("NODATA", "");
        }
    }
}
