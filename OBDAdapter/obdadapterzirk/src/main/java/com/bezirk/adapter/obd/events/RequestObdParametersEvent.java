package com.bezirk.adapter.obd.events;

import com.bezirk.adapter.obd.enums.OBDQueryParameter;
import com.bezirk.middleware.messages.Event;

import java.util.List;

public class RequestObdParametersEvent extends Event {
    private final List<OBDQueryParameter> parameters;

    public RequestObdParametersEvent(List<OBDQueryParameter> parameters) {
        this.parameters = parameters;
    }

    public List<OBDQueryParameter> getParameters() {
        return parameters;
    }
}