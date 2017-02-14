package com.bezirk.adapter.obd.events;

import com.bezirk.adapter.obd.enums.OBDErrorMessages;
import com.bezirk.adapter.obd.enums.OBDQueryParameter;
import com.bezirk.middleware.messages.Event;

public class ResponseObdStatusEvent extends Event {
    private final OBDErrorMessages errorMessage;
    private final String parameter;
    private final boolean status;

    public ResponseObdStatusEvent(OBDErrorMessages errorMessage, String parameter, boolean status) {
        this.errorMessage = errorMessage;
        this.parameter = parameter;
        this.status = status;
    }

    public OBDErrorMessages getErrorMessage() {
        return errorMessage;
    }

    public boolean getStatus(){
        return status;
    }

    public String getParameter(){
        return parameter;
    }
}