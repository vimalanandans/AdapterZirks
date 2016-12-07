package com.bezirk.adapter.obd.events;

import com.bezirk.adapter.obd.enums.OBDQueryParameter;
import com.bezirk.middleware.messages.Event;

import java.util.List;

public class RequestObdStartEvent extends Event {
    private final List<OBDQueryParameter> parameters;

    public RequestObdStartEvent(List<OBDQueryParameter> parameters) {
        this.parameters = parameters;
    }

    public List<OBDQueryParameter> getParameters() {
        return parameters;
    }
}