package com.bezirk.adapter.obd.datamodel;

/**
 * Used as a data model to hold the responses from the command. The paramCount is the number of attributes with in the data model
 * It is used to track if all the attribute values have been set. Once all the values are set, the model is retured to UI for display
 */

public class OBDResponseData {

    private int fillCounter = 0;
    private String ambientAirTemperature;
    private String distanceTraveledMILon;
    private String engineCoolantTemp;
    private String engineRPM;
    private String vehicleIdentificationNumber;
    private String throttlePosition;
    private String fuelType;
    private String fuelLevel;
    private String engineOilTemperature;
    private String intakeAirTemperature;
    private String vehicleSpeed;
    private String troubleCodes;

    public String getTroubleCodes() {
        return troubleCodes;
    }

    public void setTroubleCodes(String troubleCodes) {
        this.troubleCodes = troubleCodes;
    }

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

    public String getVehicleIdentificationNumber() {
        return vehicleIdentificationNumber;
    }

    public void setVehicleIdentificationNumber(String vehicleIdentificationNumber) {
        this.vehicleIdentificationNumber = vehicleIdentificationNumber;
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


    public String getEngineOilTemperature() {
        return engineOilTemperature;
    }

    public void setEngineOilTemperature(String engineOilTemperature) {
        this.engineOilTemperature = engineOilTemperature;
    }

    public String getIntakeAirTemperature() {
        return intakeAirTemperature;
    }

    public void setIntakeAirTemperature(String intakeAirTemperature) {
        this.intakeAirTemperature = intakeAirTemperature;
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
