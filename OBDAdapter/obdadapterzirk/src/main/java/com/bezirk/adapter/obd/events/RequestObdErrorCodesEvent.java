package com.bezirk.adapter.obd.events;

import com.bezirk.middleware.messages.Event;

public class RequestObdErrorCodesEvent extends Event
{
    public RequestObdErrorCodesEvent(String attribute)
    {
        this.attribute = attribute;
    }
    private String attribute;

    public static final String TOPIC = RequestObdErrorCodesEvent.class.getSimpleName();

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute)
    {
        this.attribute = attribute;
    }
}