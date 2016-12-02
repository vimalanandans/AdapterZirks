package com.bezirk.adapter.obd.datamodel;

/**
 * Created by rgj5kor on 11/29/2016.
 */
public class OBDResponseData {
    final static public int paramCount = 30;
    private int fillCounter = 0;
    private String ambientAirTemperature;
    private String ctrlModulePowerSupply;
    private String cmdEquivalenceRatio;
    private String distanceTraveledMILon;
    private String timingAdvance;
    private String troubleCodes;
    private String vehicleIdentificationNumber;
    private String engineLoad;
    private String massAirFlow;
    private String throttlePosition;
    private String relativeThrottlePosition;
    private String fuelType;
    private String fuelLevel;
    private String fuelRate;
    private String fuelConsumptionRate;
    private String airOrFuelRatio;
    private String widebandAirOrFuelRatio;
    private String engineOilTemperature;
    private String barometricPressure;
    private String fuelPressure;
    private String intakeManifoldPressure;
    private String absoluteLoad;
    private String fuelRailPressure;
    private String airIntakeTemperature;
    private String ignitionMonitor;
    private String dtcNumber;
    private String engineRuntime;
    private String pendingTroubleCodes;
    private String permanentTroubleCodes;
    private String distanceSinceCodesCleared;
    private String describeProtocol;
    private String describeProtocolNumber;



    private String timeRunWithMILOn;
    private String timeSinceTroubleCodesCleared;

    public void incrementFillCounter(){
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

    public String getEngineLoad() {
        return engineLoad;
    }

    public void setEngineLoad(String engineLoad) {
        this.engineLoad = engineLoad;
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

    public String getRelativeThrottlePosition() {
        return relativeThrottlePosition;
    }

    public void setRelativeThrottlePosition(String relativeThrottlePosition) {
        this.relativeThrottlePosition = relativeThrottlePosition;
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

    public String getWidebandAirOrFuelRatio() {
        return widebandAirOrFuelRatio;
    }

    public void setWidebandAirOrFuelRatio(String widebandAirOrFuelRatio) {
        this.widebandAirOrFuelRatio = widebandAirOrFuelRatio;
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


    public String getFuelRate() {
        return fuelRate;
    }

    public void setFuelRate(String fuelRate) {
        this.fuelRate = fuelRate;
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

    public String getAirIntakeTemperature() {
        return airIntakeTemperature;
    }

    public void setAirIntakeTemperature(String airIntakeTemperature) {
        this.airIntakeTemperature = airIntakeTemperature;
    }

    public String getIgnitionMonitor() {
        return ignitionMonitor;
    }

    public void setIgnitionMonitor(String ignitionMonitor) {
        this.ignitionMonitor = ignitionMonitor;
    }

    public String getDtcNumber() {
        return dtcNumber;
    }

    public void setDtcNumber(String dtcNumber) {
        this.dtcNumber = dtcNumber;
    }

    public String getEngineRuntime() {
        return engineRuntime;
    }

    public void setEngineRuntime(String engineRuntime) {
        this.engineRuntime = engineRuntime;
    }

    public String getPendingTroubleCodes() {
        return pendingTroubleCodes;
    }

    public void setPendingTroubleCodes(String pendingTroubleCodes) {
        this.pendingTroubleCodes = pendingTroubleCodes;
    }

    public String getPermanentTroubleCodes() {
        return permanentTroubleCodes;
    }

    public void setPermanentTroubleCodes(String permanentTroubleCodes) {
        this.permanentTroubleCodes = permanentTroubleCodes;
    }

    public String getDistanceSinceCodesCleared() {
        return distanceSinceCodesCleared;
    }

    public void setDistanceSinceCodesCleared(String distanceSinceCodesCleared) {
        this.distanceSinceCodesCleared = distanceSinceCodesCleared;
    }

    public String getDescribeProtocol() {
        return describeProtocol;
    }

    public void setDescribeProtocol(String describeProtocol) {
        this.describeProtocol = describeProtocol;
    }

    public String getDescribeProtocolNumber() {
        return describeProtocolNumber;
    }

    public void setDescribeProtocolNumber(String describeProtocolNumber) {
        this.describeProtocolNumber = describeProtocolNumber;
    }

    public String getTimeRunWithMILOn() {
        return timeRunWithMILOn;
    }

    public void setTimeRunWithMILOn(String timeRunWithMILOn) {
        this.timeRunWithMILOn = timeRunWithMILOn;
    }

    public String getTimeSinceTroubleCodesCleared() {
        return timeSinceTroubleCodesCleared;
    }

    public void setTimeSinceTroubleCodesCleared(String timeSinceTroubleCodesCleared) {
        this.timeSinceTroubleCodesCleared = timeSinceTroubleCodesCleared;
    }
}
