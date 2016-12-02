package com.bezirk.adapter.obd.enums;

public enum OBDQueryParameter {

    AMBIENT_AIR_TEMP("Ambient Air Temperature"),
    CONTROL_MODULE_VOLTAGE("Control Module Power Supply "),
    EQUIV_RATIO("Command Equivalence Ratio"),
    DISTANCE_TRAVELED_MIL_ON("Distance traveled with MIL on"),
    TIMING_ADVANCE("Timing Advance"),
    TROUBLE_CODES("Trouble Codes"),
    VIN("Vehicle Identification Number (VIN)"),
    MAF("Mass Air Flow"),
    THROTTLE_POS("Throttle Position"),
    FUEL_TYPE("Fuel Type"),
    FUEL_LEVEL("Fuel Level"),
    FUEL_CONSUMPTION_RATE("Fuel Consumption Rate"),
    AIR_FUEL_RATIO("Air/Fuel Ratio"),
    ENGINE_OIL_TEMP("Engine oil temperature"),
    BAROMETRIC_PRESSURE("Barometric Pressure"),
    FUEL_PRESSURE("Fuel Pressure"),
    INTAKE_MANIFOLD_PRESSURE("Intake Manifold Pressure"),
    ABS_LOAD("Absolute load"),
    FUEL_RAIL_PRESSURE("Fuel Rail Pressure"),
    ENGINE_COOLANT_TEMP("Engine Coolant Temperature"),
    ENGINE_RPM("Engine RPM"),
    SPEED("Vehicle Speed"),
    ENGINE_RUNTIME("Engine Runtime"),
    DISTANCE_TRAVELED_AFTER_CODES_CLEARED("Distance since codes cleared")
    ;

    private final String value;

    OBDQueryParameter(String value) {
        this.value = value;
    }

    public final String getValue() {
        return value;
    }

}
