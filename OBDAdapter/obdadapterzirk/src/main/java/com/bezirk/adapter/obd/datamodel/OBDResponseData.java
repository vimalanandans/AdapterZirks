package com.bezirk.adapter.obd.datamodel;

/**
 * Used as a data model to hold the responses from the command. The paramCount is the number of attributes with in the data model
 * It is used to track if all the attribute values have been set. Once all the values are set, the model is retured to UI for display
 */

public class OBDResponseData {

    private int fillCounter = 0;
    private double ambientAirTemperature;
    private double distanceTraveledMILon;
    private double engineCoolantTemp;
    private double engineRPM;
    private String vehicleIdentificationNumber;
    private double throttlePosition;
    private String fuelType;
    private double fuelLevel;
    private double engineOilTemperature;
    private double intakeAirTemperature;
    private double vehicleSpeed;
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

    public double getAmbientAirTemperature() {
        return ambientAirTemperature;
    }

    public void setAmbientAirTemperature(double ambientAirTemperature) {
        this.ambientAirTemperature = ambientAirTemperature;
    }

    public double getDistanceTraveledMILon() {
        return distanceTraveledMILon;
    }

    public void setDistanceTraveledMILon(double distanceTraveledMILon) {
        this.distanceTraveledMILon = distanceTraveledMILon;
    }

    public double getEngineCoolantTemp() {
        return engineCoolantTemp;
    }

    public void setEngineCoolantTemp(double engineCoolantTemp) {
        this.engineCoolantTemp = engineCoolantTemp;
    }

    public String getVehicleIdentificationNumber() {
        return vehicleIdentificationNumber;
    }

    public void setVehicleIdentificationNumber(String vehicleIdentificationNumber) {
        this.vehicleIdentificationNumber = vehicleIdentificationNumber;
    }

    public double getThrottlePosition() {
        return throttlePosition;
    }

    public void setThrottlePosition(double throttlePosition) {
        this.throttlePosition = throttlePosition;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public double getFuelLevel() {
        return fuelLevel;
    }

    public void setFuelLevel(double fuelLevel) {
        this.fuelLevel = fuelLevel;
    }


    public double getEngineOilTemperature() {
        return engineOilTemperature;
    }

    public void setEngineOilTemperature(double engineOilTemperature) {
        this.engineOilTemperature = engineOilTemperature;
    }

    public double getIntakeAirTemperature() {
        return intakeAirTemperature;
    }

    public void setIntakeAirTemperature(double intakeAirTemperature) {
        this.intakeAirTemperature = intakeAirTemperature;
    }

    public double getEngineRPM() {
        return engineRPM;
    }

    public void setEngineRPM(double engineRPM) {
        this.engineRPM = engineRPM;
    }

    public double getVehicleSpeed() {
        return vehicleSpeed;
    }

    public void setVehicleSpeed(double vehicleSpeed) {
        this.vehicleSpeed = vehicleSpeed;
    }
}
