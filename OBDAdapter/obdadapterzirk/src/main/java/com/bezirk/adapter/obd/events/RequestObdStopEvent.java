package com.bezirk.adapter.obd.events;

import com.bezirk.adapter.obd.enums.OBDQueryParameter;
import com.bezirk.middleware.messages.Event;

import java.util.List;

/**
 * Created by DEV6KOR on 9/8/2016.
 */
public class RequestObdStopEvent extends Event
{
    public RequestObdStopEvent(String attribute)
    {
        this.attribute = attribute;
    }

    private String attribute;

    public static final String TOPIC = RequestObdStopEvent.class.getSimpleName();


    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }
}