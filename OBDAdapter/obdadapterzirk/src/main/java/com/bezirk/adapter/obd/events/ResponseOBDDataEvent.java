package com.bezirk.adapter.obd.events;

import com.bezirk.adapter.obd.datamodel.OBDResponseData;
import com.bezirk.middleware.messages.Event;

public class ResponseObdDataEvent extends Event {
    private final OBDResponseData obdResponseData;

    public ResponseObdDataEvent(OBDResponseData obdResponseData) {
        this.obdResponseData = obdResponseData;
    }

    public OBDResponseData getObdResponseData() {
        return obdResponseData;
    }
}
