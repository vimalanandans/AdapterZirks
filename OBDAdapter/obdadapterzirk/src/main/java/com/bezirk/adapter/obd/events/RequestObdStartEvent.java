package com.bezirk.adapter.obd.events;

import com.bezirk.adapter.obd.enums.OBDQueryParameter;
import com.bezirk.middleware.messages.Event;

import java.util.List;

/**
 * Created by DEV6KOR on 9/8/2016.
 */
public class RequestObdStartEvent extends Event
{
    public RequestObdStartEvent(List<OBDQueryParameter> parameters)
    {
        this.parameters = parameters;
    }

    public List<OBDQueryParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<OBDQueryParameter> parameters) {
        this.parameters = parameters;
    }

    private List<OBDQueryParameter> parameters;

    public static final String TOPIC = RequestObdStartEvent.class.getSimpleName();
}