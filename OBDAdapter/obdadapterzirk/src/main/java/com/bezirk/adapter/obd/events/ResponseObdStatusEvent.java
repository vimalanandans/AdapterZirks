package com.bezirk.adapter.obd.events;

import com.bezirk.middleware.messages.Event;

public class ResponseObdStatusEvent extends Event {
    private final String errorMessage;
    private final boolean isDeviceConnected;

    public ResponseObdStatusEvent(String errorMessage, boolean isDeviceConnected) {
        this.errorMessage = errorMessage;
        this.isDeviceConnected = isDeviceConnected;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isDeviceConnected() {
        return isDeviceConnected;
    }
}