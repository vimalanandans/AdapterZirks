package com.bezirk.adapter.obd.enums;

import com.bezirk.adapter.obd.datamodel.OBDResponseData;

/***
 * Enum for the different OBD Commands. Method updateOBDREsponseData is provided in each Enum. This will be called from the
 * method using this Enums, to set the OBDResponseData based on the command name returned after querying from OBD.
 */

public enum OBDQueryParameter {

    AMBIENT_AIR_TEMP("Ambient Air Temperature"){
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result){
            obdResponseData.setAmbientAirTemperature(result);
        }
    },
    CONTROL_MODULE_VOLTAGE("Control Module Power Supply "){
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result){
            obdResponseData.setCtrlModulePowerSupply(result);
        }
    },
    EQUIV_RATIO("Command Equivalence Ratio"){
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result){
            obdResponseData.setCmdEquivalenceRatio(result);
        }
    },

    DISTANCE_TRAVELED_MIL_ON("Distance traveled with MIL on"){
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result){
            obdResponseData.setDistanceTraveledMILon(result);
        }
    },
    TIMING_ADVANCE("Timing Advance"){
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result){
            obdResponseData.setTimingAdvance(result);
        }
    },
    TROUBLE_CODES("Trouble Codes"){
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result){
            obdResponseData.setTroubleCodes(result);
        }
    },
    VIN("Vehicle Identification Number (VIN)"){
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result){
            obdResponseData.setVehicleIdentificationNumber(result);
        }
    },
    MAF("Mass Air Flow"){
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result){
            obdResponseData.setMassAirFlow(result);
        }
    },
    THROTTLE_POS("Throttle Position"){
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result){
            obdResponseData.setThrottlePosition(result);
        }
    },
    FUEL_TYPE("Fuel Type"){
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result){
            obdResponseData.setFuelType(result);
        }
    },
    FUEL_LEVEL("Fuel Level"){
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result){
            obdResponseData.setFuelLevel(result);
        }
    },
    FUEL_CONSUMPTION_RATE("Fuel Consumption Rate"){
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result){
            obdResponseData.setFuelConsumptionRate(result);
        }
    },
    AIR_FUEL_RATIO("Air/Fuel Ratio"){
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result){
            obdResponseData.setAirOrFuelRatio(result);
        }
    },
    ENGINE_OIL_TEMP("Engine oil temperature"){
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result){
            obdResponseData.setEngineOilTemperature(result);
        }
    },
    BAROMETRIC_PRESSURE("Barometric Pressure"){
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result){
            obdResponseData.setBarometricPressure(result);
        }
    },
    FUEL_PRESSURE("Fuel Pressure"){
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result){
            obdResponseData.setFuelPressure(result);
        }
    },
    INTAKE_MANIFOLD_PRESSURE("Intake Manifold Pressure"){
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result){
            obdResponseData.setIntakeManifoldPressure(result);
        }
    },
    ABS_LOAD("Absolute load"){
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result){
            obdResponseData.setAbsoluteLoad(result);
        }
    },
    FUEL_RAIL_PRESSURE("Fuel Rail Pressure"){
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result){
            obdResponseData.setFuelRailPressure(result);
        }
    },
    ENGINE_COOLANT_TEMP("Engine Coolant Temperature"){
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result){
            obdResponseData.setEngineCoolantTemp(result);
        }
    },
    ENGINE_RPM("Engine RPM"){
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result){
            obdResponseData.setEngineRPM(result);
        }
    },
    SPEED("Vehicle Speed"){
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result){
            obdResponseData.setVehicleSpeed(result);
        }
    },
    ENGINE_RUNTIME("Engine Runtime"){
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result){
            obdResponseData.setEngineRuntime(result);
        }
    },
    DISTANCE_TRAVELED_AFTER_CODES_CLEARED("Distance since codes cleared"){
        public void updateOBDResponseData(OBDResponseData obdResponseData, String result){
            obdResponseData.setDistanceSinceCodesCleared(result);
        }
    } ;

    private final String value;

    OBDQueryParameter(String value) {
        this.value = value;
    }

    public final String getValue() {
        return value;
    }
    public void updateOBDResponseData(OBDResponseData obdResponseData,String result) {
        this.updateOBDResponseData(obdResponseData,result);
    }

    public final static OBDQueryParameter getOBDQueryParameter(String command){
        for(OBDQueryParameter obdQueryParameter:OBDQueryParameter.values()){
            if(obdQueryParameter.getValue().equalsIgnoreCase(command)){
                return obdQueryParameter;
            }
        }
        return null;
    }
}
