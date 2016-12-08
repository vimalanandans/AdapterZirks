package com.bezirk.adapter.obd.events;

import com.bezirk.middleware.messages.Event;

public class ResponseObdEngineRPMEvent extends Event {
    private final String result;

    public ResponseObdEngineRPMEvent(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }
}
