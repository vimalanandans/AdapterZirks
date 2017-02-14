package com.bezirk.adapter.obd.events;

import com.bezirk.middleware.messages.Event;

public class RequestObdStartEvent extends Event {
    private final String attribute;

    public RequestObdStartEvent(String attribute) {
        this.attribute = attribute;
    }

    public String getAttribute() {
        return this.attribute;
    }
}