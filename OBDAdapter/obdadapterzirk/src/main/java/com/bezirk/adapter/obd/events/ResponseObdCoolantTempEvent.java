package com.bezirk.adapter.obd.events;

import com.bezirk.middleware.messages.Event;

public class ResponseObdCoolantTempEvent extends Event {
    private final String result;

    public ResponseObdCoolantTempEvent(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }
}
