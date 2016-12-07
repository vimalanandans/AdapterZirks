package com.bezirk.adapter.wunderground;

import com.bezirk.hardwareevents.environment.BarometricPressureReadingEvent;
import com.bezirk.hardwareevents.environment.EnvironmentalSensor;
import com.bezirk.hardwareevents.environment.EnvironmentalSensorsDetectedEvent;
import com.bezirk.hardwareevents.environment.GetEnvironmentSensorReadingEvent;
import com.bezirk.hardwareevents.environment.HumidityReadingEvent;
import com.bezirk.hardwareevents.environment.TemperatureReadingEvent;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.java.proxy.BezirkMiddleware;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class WundergroundZirkTest {
    private static final Logger logger = LoggerFactory.getLogger(WundergroundZirkTest.class);

    public static void main(String[] args) {
        BezirkMiddleware.initialize();
        final Bezirk bezirk = BezirkMiddleware.registerZirk("Wunderground Test Zirk");

        final EventSet environmentalEvents = new EventSet(EnvironmentalSensorsDetectedEvent.class,
                BarometricPressureReadingEvent.class, HumidityReadingEvent.class,
                TemperatureReadingEvent.class);

        environmentalEvents.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint zirkEndPoint) {
                if (event instanceof EnvironmentalSensorsDetectedEvent) {
                    final Set<EnvironmentalSensor> sensors =
                            ((EnvironmentalSensorsDetectedEvent) event).getSensors();

                    for (EnvironmentalSensor sensor : sensors) {
                        bezirk.sendEvent(new GetEnvironmentSensorReadingEvent(sensor,
                                sensor.getCapabilities()));
                    }
                } else if (event instanceof BarometricPressureReadingEvent) {
                    final BarometricPressureReadingEvent pressureReading =
                            (BarometricPressureReadingEvent) event;
                    logger.info(pressureReading.toString());
                } else if (event instanceof HumidityReadingEvent) {
                    final HumidityReadingEvent humidityReading =
                            (HumidityReadingEvent) event;
                    logger.info(humidityReading.toString());
                } else if (event instanceof TemperatureReadingEvent) {
                    final TemperatureReadingEvent temperatureReading =
                            (TemperatureReadingEvent) event;
                    logger.info(temperatureReading.toString());
                }
            }
        });

        bezirk.subscribe(environmentalEvents);

        new WundergroundAdapter(bezirk, "API_KEY_HERE", "PA", "Pittsburgh");
    }
}
