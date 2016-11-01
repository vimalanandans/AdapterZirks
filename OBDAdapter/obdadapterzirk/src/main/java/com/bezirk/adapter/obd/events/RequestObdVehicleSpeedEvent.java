package com.bezirk.adapter.obd.events;

import com.bezirk.middleware.messages.Event;

/**
 * Created by DEV6KOR on 9/8/2016.
 */
public class RequestObdVehicleSpeedEvent extends Event
{
    public RequestObdVehicleSpeedEvent(String attribute)
    {
        this.attribute = attribute;
    }
    private String attribute;

    public static final String TOPIC = RequestObdVehicleSpeedEvent.class.getSimpleName();

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute)
    {
        this.attribute = attribute;
    }
}