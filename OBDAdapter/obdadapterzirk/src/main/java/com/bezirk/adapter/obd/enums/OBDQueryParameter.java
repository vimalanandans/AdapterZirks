package com.bezirk.adapter.obd.enums;

import android.support.annotation.Nullable;

import com.bezirk.adapter.obd.datamodel.OBDResponseData;

public enum OBDQueryParameter {

    AMBIENT_AIR_TEMP("Ambient Air Temperature") {
        @Override
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result) {
            obdResponseData.setAmbientAirTemperature(result);
        }
    },
    CONTROL_MODULE_VOLTAGE("Control Module Power Supply ") {
        @Override
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result) {
            obdResponseData.setCtrlModulePowerSupply(result);
        }
    },
    EQUIV_RATIO("Command Equivalence Ratio") {
        @Override
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result) {
            obdResponseData.setCmdEquivalenceRatio(result);
        }
    },

    DISTANCE_TRAVELED_MIL_ON("Distance traveled with MIL on") {
        @Override
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result) {
            obdResponseData.setDistanceTraveledMILon(result);
        }
    },
    TIMING_ADVANCE("Timing Advance") {
        @Override
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result) {
            obdResponseData.setTimingAdvance(result);
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
    MAF("Mass Air Flow") {
        @Override
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result) {
            obdResponseData.setMassAirFlow(result);
        }
    },
    THROTTLE_POS("Throttle Position") {
        @Override
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result) {
            obdResponseData.setThrottlePosition(result);
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
            obdResponseData.setFuelLevel(result);
        }
    },
    FUEL_CONSUMPTION_RATE("Fuel Consumption Rate") {
        @Override
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result) {
            obdResponseData.setFuelConsumptionRate(result);
        }
    },
    AIR_FUEL_RATIO("Air/Fuel Ratio") {
        @Override
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result) {
            obdResponseData.setAirOrFuelRatio(result);
        }
    },
    ENGINE_OIL_TEMP("Engine oil temperature") {
        @Override
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result) {
            obdResponseData.setEngineOilTemperature(result);
        }
    },
    BAROMETRIC_PRESSURE("Barometric Pressure") {
        @Override
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result) {
            obdResponseData.setBarometricPressure(result);
        }
    },
    FUEL_PRESSURE("Fuel Pressure") {
        @Override
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result) {
            obdResponseData.setFuelPressure(result);
        }
    },
    INTAKE_MANIFOLD_PRESSURE("Intake Manifold Pressure") {
        @Override
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result) {
            obdResponseData.setIntakeManifoldPressure(result);
        }
    },
    ABS_LOAD("Absolute load") {
        @Override
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result) {
            obdResponseData.setAbsoluteLoad(result);
        }
    },
    FUEL_RAIL_PRESSURE("Fuel Rail Pressure") {
        @Override
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result) {
            obdResponseData.setFuelRailPressure(result);
        }
    },
    ENGINE_COOLANT_TEMP("Engine Coolant Temperature") {
        @Override
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result) {
            obdResponseData.setEngineCoolantTemp(result);
        }
    },
    ENGINE_RPM("Engine RPM") {
        @Override
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result) {
            obdResponseData.setEngineRPM(result);
        }
    },
    SPEED("Vehicle Speed") {
        @Override
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result) {
            obdResponseData.setVehicleSpeed(result);
        }
    },
    ENGINE_RUNTIME("Engine Runtime") {
        @Override
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result) {
            obdResponseData.setEngineRuntime(result);
        }
    },
    DISTANCE_TRAVELED_AFTER_CODES_CLEARED("Distance since codes cleared") {
        @Override
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result) {
            obdResponseData.setDistanceSinceCodesCleared(result);
        }
    };

    private final String value;

    OBDQueryParameter(String value) {
        this.value = value;
    }

    @Nullable public static OBDQueryParameter getOBDQueryParameter(String command) {
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
