package com.bezirk.adapter.obd.datamodel;

/**
 * Used as a data model to hold the responses from the command. The paramCount is the number of attributes with in the data model
 * It is used to track if all the attribute values have been set. Once all the values are set, the model is retured to UI for display
 */

public class OBDResponseData {

    private int fillCounter = 0;
    private String ambientAirTemperature;
    private String ctrlModulePowerSupply;
    private String cmdEquivalenceRatio;
    private String distanceTraveledMILon;
    private String engineCoolantTemp;
    private String engineRPM;
    private String timingAdvance;
    private String troubleCodes;
    private String vehicleIdentificationNumber;
    private String massAirFlow;
    private String throttlePosition;
    private String fuelType;
    private String fuelLevel;
    private String fuelConsumptionRate;
    private String airOrFuelRatio;
    private String engineOilTemperature;
    private String barometricPressure;
    private String fuelPressure;
    private String intakeManifoldPressure;
    private String absoluteLoad;
    private String fuelRailPressure;
    private String engineRuntime;
    private String distanceSinceCodesCleared;
    private String vehicleSpeed;

    public void incrementFillCounter() {
        this.fillCounter++;
    }

    public int getFillCounter() {
        return fillCounter;
    }

    public void setFillCounter(int fillCounter) {
        this.fillCounter = fillCounter;
    }

    public String getAmbientAirTemperature() {
        return ambientAirTemperature;
    }

    public void setAmbientAirTemperature(String ambientAirTemperature) {
        this.ambientAirTemperature = ambientAirTemperature;
    }

    public String getCtrlModulePowerSupply() {
        return ctrlModulePowerSupply;
    }

    public void setCtrlModulePowerSupply(String ctrlModulePowerSupply) {
        this.ctrlModulePowerSupply = ctrlModulePowerSupply;
    }

    public String getCmdEquivalenceRatio() {
        return cmdEquivalenceRatio;
    }

    public void setCmdEquivalenceRatio(String cmdEquivalenceRatio) {
        this.cmdEquivalenceRatio = cmdEquivalenceRatio;
    }

    public String getDistanceTraveledMILon() {
        return distanceTraveledMILon;
    }

    public void setDistanceTraveledMILon(String distanceTraveledMILon) {
        this.distanceTraveledMILon = distanceTraveledMILon;
    }

    public String getEngineCoolantTemp() {
        return engineCoolantTemp;
    }

    public void setEngineCoolantTemp(String engineCoolantTemp) {
        this.engineCoolantTemp = engineCoolantTemp;
    }

    public String getTimingAdvance() {
        return timingAdvance;
    }

    public void setTimingAdvance(String timingAdvance) {
        this.timingAdvance = timingAdvance;
    }

    public String getTroubleCodes() {
        return troubleCodes;
    }

    public void setTroubleCodes(String troubleCodes) {
        this.troubleCodes = troubleCodes;
    }

    public String getVehicleIdentificationNumber() {
        return vehicleIdentificationNumber;
    }

    public void setVehicleIdentificationNumber(String vehicleIdentificationNumber) {
        this.vehicleIdentificationNumber = vehicleIdentificationNumber;
    }

    public String getMassAirFlow() {
        return massAirFlow;
    }

    public void setMassAirFlow(String massAirFlow) {
        this.massAirFlow = massAirFlow;
    }

    public String getThrottlePosition() {
        return throttlePosition;
    }

    public void setThrottlePosition(String throttlePosition) {
        this.throttlePosition = throttlePosition;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getFuelLevel() {
        return fuelLevel;
    }

    public void setFuelLevel(String fuelLevel) {
        this.fuelLevel = fuelLevel;
    }

    public String getFuelConsumptionRate() {
        return fuelConsumptionRate;
    }

    public void setFuelConsumptionRate(String fuelConsumptionRate) {
        this.fuelConsumptionRate = fuelConsumptionRate;
    }

    public String getAirOrFuelRatio() {
        return airOrFuelRatio;
    }

    public void setAirOrFuelRatio(String airOrFuelRatio) {
        this.airOrFuelRatio = airOrFuelRatio;
    }

    public String getEngineOilTemperature() {
        return engineOilTemperature;
    }

    public void setEngineOilTemperature(String engineOilTemperature) {
        this.engineOilTemperature = engineOilTemperature;
    }

    public String getBarometricPressure() {
        return barometricPressure;
    }

    public void setBarometricPressure(String barometricPressure) {
        this.barometricPressure = barometricPressure;
    }

    public String getFuelPressure() {
        return fuelPressure;
    }


    public void setFuelPressure(String fuelPressure) {
        this.fuelPressure = fuelPressure;
    }


    public String getIntakeManifoldPressure() {
        return intakeManifoldPressure;
    }

    public void setIntakeManifoldPressure(String intakeManifoldPressure) {
        this.intakeManifoldPressure = intakeManifoldPressure;
    }

    public String getAbsoluteLoad() {
        return absoluteLoad;
    }

    public void setAbsoluteLoad(String absoluteLoad) {
        this.absoluteLoad = absoluteLoad;
    }

    public String getFuelRailPressure() {
        return fuelRailPressure;
    }

    public void setFuelRailPressure(String fuelRailPressure) {
        this.fuelRailPressure = fuelRailPressure;
    }

    public String getEngineRuntime() {
        return engineRuntime;
    }

    public void setEngineRuntime(String engineRuntime) {
        this.engineRuntime = engineRuntime;
    }

    public String getDistanceSinceCodesCleared() {
        return distanceSinceCodesCleared;
    }

    public void setDistanceSinceCodesCleared(String distanceSinceCodesCleared) {
        this.distanceSinceCodesCleared = distanceSinceCodesCleared;
    }

    public String getEngineRPM() {
        return engineRPM;
    }

    public void setEngineRPM(String engineRPM) {
        this.engineRPM = engineRPM;
    }

    public String getVehicleSpeed() {
        return vehicleSpeed;
    }

    public void setVehicleSpeed(String vehicleSpeed) {
        this.vehicleSpeed = vehicleSpeed;
    }
}
