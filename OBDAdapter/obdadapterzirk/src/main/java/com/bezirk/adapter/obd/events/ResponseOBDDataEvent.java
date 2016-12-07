package com.bezirk.adapter.obd.events;

import com.bezirk.adapter.obd.datamodel.OBDResponseData;
import com.bezirk.middleware.messages.Event;

public class ResponseOBDDataEvent extends Event {
    private String attribute;
    private OBDResponseData obdResponseData;

    public ResponseOBDDataEvent(OBDResponseData obdResponseData) {
        this.obdResponseData = obdResponseData;
    }

    public OBDResponseData getObdResponseData() {
        return obdResponseData;
    }

    public void setObdResponseData(OBDResponseData obdResponseData) {
        this.obdResponseData = obdResponseData;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }
}
