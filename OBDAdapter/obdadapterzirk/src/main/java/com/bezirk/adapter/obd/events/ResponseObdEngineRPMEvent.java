package com.bezirk.adapter.obd.events;

import com.bezirk.middleware.messages.Event;

/**
 * Created by DEV6KOR on 9/8/2016.
 */
public class ResponseObdEngineRPMEvent extends Event {
    public static final String TOPIC = ResponseObdEngineRPMEvent.class.getSimpleName();
    private String attribute;
    private String result;

    public ResponseObdEngineRPMEvent(String result)
    {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute)
    {
        this.attribute = attribute;
    }
}
