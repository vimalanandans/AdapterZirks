package com.bezirk.adapter.obd.events;

import com.bezirk.middleware.messages.Event;

/**
 * Created by DEV6KOR on 9/8/2016.
 */
public class ResponseObdVehicleSpeedEvent extends Event {
    public static final String TOPIC = ResponseObdVehicleSpeedEvent.class.getSimpleName();
    private String attribute;
    private String result;

    public ResponseObdVehicleSpeedEvent(String result)
    {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute)
    {
        this.attribute = attribute;
    }
}
