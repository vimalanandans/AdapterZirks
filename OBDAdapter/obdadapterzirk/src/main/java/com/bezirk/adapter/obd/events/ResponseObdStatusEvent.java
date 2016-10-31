package com.bezirk.adapter.obd.events;

import com.bezirk.middleware.messages.Event;

/**
 * Created by DEV6KOR on 9/8/2016.
 */
public class ResponseObdStatusEvent extends Event {
    public static final String TOPIC = ResponseObdStatusEvent.class.getSimpleName();
    private boolean isDeviceConnected;

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

    private String errorMessage;

    public ResponseObdStatusEvent(String errorMessage, boolean isDeviceConnected)
    {
        this.errorMessage = errorMessage;
        this.isDeviceConnected = isDeviceConnected;
    }
}
