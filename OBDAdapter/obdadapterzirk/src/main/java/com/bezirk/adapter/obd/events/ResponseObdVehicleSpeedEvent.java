package com.bezirk.adapter.obd.events;

import com.bezirk.middleware.messages.Event;

public class ResponseObdVehicleSpeedEvent extends Event {
    private final String result;

    public ResponseObdVehicleSpeedEvent(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }
}
