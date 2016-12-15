package com.bezirk.adapter.obd.enums;

import android.support.annotation.Nullable;

import com.bezirk.adapter.obd.datamodel.OBDResponseData;

/***
 * Enum for the different OBD Commands. Method updateOBDREsponseData is provided in each Enum. This will be called from the
 * method using this Enums, to set the OBDResponseData based on the command name returned after querying from OBD.
 */

public enum OBDQueryParameter {
    AMBIENT_AIR_TEMP("Ambient Air Temperature") {
        @Override
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result) {
            if (!result.equals(NO_DATA)) {
                obdResponseData.setAmbientAirTemperature(Double.parseDouble(result));
            } else {
                obdResponseData.setEngineOilTemperature(0);
            }

        }
    },

    DISTANCE_TRAVELED_MIL_ON("Distance traveled with MIL on") {
        @Override
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result) {
            if (!result.equals(NO_DATA)) {
                obdResponseData.setDistanceTraveledMILon(Double.parseDouble(result));
            } else {
                obdResponseData.setEngineOilTemperature(0);
            }

        }
    },

    TROUBLE_CODES("Trouble Codes") {
        @Override
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result) {
            obdResponseData.setTroubleCodes(result);
        }
    },

    VIN("Vehicle Identification Number (VIN)") {
        @Override
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result) {
            obdResponseData.setVehicleIdentificationNumber(result);
        }
    },

    THROTTLE_POS("Throttle Position") {
        @Override
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result) {
            if (!result.equals(NO_DATA)) {
                obdResponseData.setThrottlePosition(Double.parseDouble(result));
            } else {
                obdResponseData.setEngineOilTemperature(0);
            }
        }
    },

    FUEL_TYPE("Fuel Type") {
        @Override
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result) {
            obdResponseData.setFuelType(result);
        }
    },

    FUEL_LEVEL("Fuel Level") {
        @Override
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result) {
            if (!result.equals(NO_DATA)) {
                obdResponseData.setFuelLevel(Double.parseDouble(result));
            } else {
                obdResponseData.setEngineOilTemperature(0);
            }
        }
    },

    ENGINE_OIL_TEMP("Engine oil temperature") {
        @Override
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result) {
            if (!result.equals(NO_DATA)) {
                obdResponseData.setEngineOilTemperature(Double.parseDouble(result));
            } else {
                obdResponseData.setEngineOilTemperature(0);
            }
        }
    },

    AIR_INTAKE_TEMP("Air Intake Temperature") {
        @Override
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result) {
            if (!result.equals(NO_DATA)) {
                obdResponseData.setIntakeAirTemperature(Double.parseDouble(result));
            } else {
                obdResponseData.setEngineOilTemperature(0);
            }
        }
    },

    ENGINE_COOLANT_TEMP("Engine Coolant Temperature") {
        @Override
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result) {
            if (!result.equals(NO_DATA)) {
                obdResponseData.setEngineCoolantTemp(Double.parseDouble(result));
            } else {
                obdResponseData.setEngineOilTemperature(0);
            }
        }
    },

    ENGINE_RPM("Engine RPM") {
        @Override
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result) {
            if (!result.equals(NO_DATA)) {
                obdResponseData.setEngineRPM(Double.parseDouble(result));
            } else {
                obdResponseData.setEngineOilTemperature(0);
            }
        }
    },

    SPEED("Vehicle Speed") {
        @Override
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result) {
            if (!result.equals(NO_DATA)) {
                obdResponseData.setVehicleSpeed(Double.parseDouble(result));
            } else {
                obdResponseData.setEngineOilTemperature(0);
            }
        }
    };

    private final String value;
    private final static String NO_DATA = "NO DATA";

    OBDQueryParameter(String value) {
        this.value = value;
    }

    @Nullable
    public static OBDQueryParameter getOBDQueryParameter(String command) {
        for (OBDQueryParameter obdQueryParameter : OBDQueryParameter.values()) {
            if (obdQueryParameter.getValue().equalsIgnoreCase(command)) {
                return obdQueryParameter;
            }
        }

        return null;
    }

    public final String getValue() {
        return value;
    }

    public void updateOBDResponseData(OBDResponseData obdResponseData, String result) {
        this.updateOBDResponseData(obdResponseData, result);

    }
}
