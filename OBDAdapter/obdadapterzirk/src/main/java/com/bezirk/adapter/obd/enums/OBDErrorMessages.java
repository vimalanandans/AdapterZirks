package com.bezirk.adapter.obd.enums;

/***
 * Enums for the different OBD Error Messages
 */

public enum OBDErrorMessages {
    STOP_INTERRUPT_ERR("ERR : User Stop Request"),

    STOP_EXECUTION_ERR("ERR : Error while fetching OBD Parameters"),

    STOP_TIMEOUT_ERR("ERR : OBD fetch request timed out"),

    CONNECTION_SOCKET_ERR("ERR : Socket Error connecting to OBD Device"),

    INIT_OBD_ERR("ERR : Unable to Initialize OBD Device");

    private String errorMessage;

    OBDErrorMessages(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public final String getValue() {
        return errorMessage;
    }
}
