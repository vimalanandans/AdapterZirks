package com.bezirk.adapter.obd.events;

import com.bezirk.adapter.obd.enums.OBDErrorMessages;
import com.bezirk.middleware.messages.Event;

public class ResponseObdStatusEvent extends Event {
    private final OBDErrorMessages errorMessage;
    private final boolean isDeviceConnected;

    public ResponseObdStatusEvent(OBDErrorMessages errorMessage, boolean isDeviceConnected) {
        this.errorMessage = errorMessage;
        this.isDeviceConnected = isDeviceConnected;
    }

    public OBDErrorMessages getErrorMessage() {
        return errorMessage;
    }

    public boolean isDeviceConnected() {
        return isDeviceConnected;
    }
}