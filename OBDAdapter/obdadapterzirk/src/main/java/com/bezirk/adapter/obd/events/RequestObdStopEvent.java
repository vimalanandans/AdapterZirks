package com.bezirk.adapter.obd.events;

import com.bezirk.middleware.messages.Event;

public class RequestObdStopEvent extends Event {
    private final String attribute;

    public RequestObdStopEvent(String attribute) {
        this.attribute = attribute;
    }

    public String getAttribute() {
        return attribute;
    }
}