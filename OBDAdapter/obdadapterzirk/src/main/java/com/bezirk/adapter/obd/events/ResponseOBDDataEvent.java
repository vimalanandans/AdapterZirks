package com.bezirk.adapter.obd.events;

import com.bezirk.adapter.obd.datamodel.OBDResponseData;
import com.bezirk.middleware.messages.Event;

public class ResponseOBDDataEvent extends Event {
    private final OBDResponseData obdResponseData;

    public ResponseOBDDataEvent(OBDResponseData obdResponseData) {
        this.obdResponseData = obdResponseData;
    }

    public OBDResponseData getObdResponseData() {
        return obdResponseData;
    }
}
