package com.bezirk.adapter.obd.events;

import com.bezirk.middleware.messages.Event;

public class ResponseObdStatusEvent extends Event {
    private boolean isDeviceConnected;
    private String errorMessage;

    public ResponseObdStatusEvent(String errorMessage, boolean isDeviceConnected) {
        this.errorMessage = errorMessage;
        this.isDeviceConnected = isDeviceConnected;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isDeviceConnected() {
        return isDeviceConnected;
    }

    public void setDeviceConnected(boolean deviceConnected) {
        isDeviceConnected = deviceConnected;
    }
}