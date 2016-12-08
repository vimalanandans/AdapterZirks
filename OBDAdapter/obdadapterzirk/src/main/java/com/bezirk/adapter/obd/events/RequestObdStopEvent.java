package com.bezirk.adapter.obd.events;

import com.bezirk.middleware.messages.Event;

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