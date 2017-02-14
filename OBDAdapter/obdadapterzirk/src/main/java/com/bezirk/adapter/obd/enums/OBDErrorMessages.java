package com.bezirk.adapter.obd.enums;

/***
 * Enums for the different OBD Error Messages
 */

public enum OBDErrorMessages {
    STOP_OBD_STATUS("MSG : OBD fetch stopped for requested Parameter"),

    STOP_INTERRUPT_ERR("ERR : User Stop Request"),

    STOP_EXECUTION_ERR("ERR : Error while fetching OBD Parameters"),

    STOP_TIMEOUT_ERR("ERR : OBD fetch request timed out"),

    CONNECTION_SOCKET_ERR("ERR : Socket Error connecting to OBD Device"),

    INIT_OBD_ERR("ERR : Unable to Initialize OBD Device"),

    BLUETOOTH_CONN_DISCONNECTED("ERR : Bluetooth connection disconnected"),

    BLUETOOTH_TURNED_OFF("ERR : Bluetooth is turned OFF"),

    BLUETOOTH_OFF("ERR : Bluetooth is OFF");



    private String errorMessage;

    OBDErrorMessages(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public final String getValue() {
        return errorMessage;
    }
}
