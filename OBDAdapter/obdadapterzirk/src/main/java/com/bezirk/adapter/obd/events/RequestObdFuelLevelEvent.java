package com.bezirk.adapter.obd.events;

import com.bezirk.middleware.messages.Event;

/**
 * Created by DEV6KOR on 9/8/2016.
 */
public class RequestObdFuelLevelEvent extends Event
{
    public RequestObdFuelLevelEvent(String attribute)
    {
        this.attribute = attribute;
    }
    private String attribute;

    public static final String TOPIC = RequestObdFuelLevelEvent.class.getSimpleName();

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute)
    {
        this.attribute = attribute;
    }
}