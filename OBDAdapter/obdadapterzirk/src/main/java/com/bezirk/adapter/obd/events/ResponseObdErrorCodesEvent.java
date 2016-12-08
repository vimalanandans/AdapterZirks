package com.bezirk.adapter.obd.events;

import com.bezirk.middleware.messages.Event;

public class ResponseObdErrorCodesEvent extends Event {
    public static final String TOPIC = ResponseObdErrorCodesEvent.class.getSimpleName();
    private String attribute;
    private String result;

    public ResponseObdErrorCodesEvent(String result)
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
