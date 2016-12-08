package com.bezirk.adapter.obd.events;

import com.bezirk.middleware.messages.Event;

public class ResponseObdErrorCodesEvent extends Event {
    private final String result;

    public ResponseObdErrorCodesEvent(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }
}
