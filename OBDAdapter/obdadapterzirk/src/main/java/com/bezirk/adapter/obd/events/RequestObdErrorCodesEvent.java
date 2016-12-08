package com.bezirk.adapter.obd.events;

import com.bezirk.middleware.messages.Event;

public class RequestObdErrorCodesEvent extends Event {
    private final String attribute;

    public RequestObdErrorCodesEvent(String attribute) {
        this.attribute = attribute;
    }

    public String getAttribute() {
        return attribute;
    }
}