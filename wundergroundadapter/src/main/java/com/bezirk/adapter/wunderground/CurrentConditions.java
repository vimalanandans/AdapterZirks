package com.bezirk.adapter.wunderground;

import com.bezirk.hardwareevents.Pressure;
import com.bezirk.hardwareevents.Temperature;

class CurrentConditions {
    private final Temperature temperature;
    private final Pressure pressure;
    private final double relativeHumidity;

    public CurrentConditions(Temperature temperature, Pressure pressure, double relativeHumidity) {
        this.temperature = temperature;
        this.pressure = pressure;
        this.relativeHumidity = relativeHumidity;
    }

    public Temperature getTemperature() {
        return temperature;
    }

    public Pressure getPressure() {
        return pressure;
    }

    public double getRelativeHumidity() {
        return relativeHumidity;
    }
}
