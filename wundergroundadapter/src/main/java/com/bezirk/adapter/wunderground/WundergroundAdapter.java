package com.bezirk.adapter.wunderground;

import com.bezirk.hardwareevents.environment.BarometricPressureReadingEvent;
import com.bezirk.hardwareevents.environment.EnvironmentalSensor;
import com.bezirk.hardwareevents.environment.EnvironmentalSensorsDetectedEvent;
import com.bezirk.hardwareevents.environment.GetEnvironmentSensorReadingEvent;
import com.bezirk.hardwareevents.environment.HumidityReadingEvent;
import com.bezirk.hardwareevents.environment.TemperatureReadingEvent;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class WundergroundAdapter {
    public WundergroundAdapter(final Bezirk bezirk, String apiKey, final String state, final String city) {
        final WundergroundController controller = new WundergroundController(apiKey);

        // We create the sensor here so that we can use it when replying to events, but
        // we do not broadcast an event stating the sensor exists until after we subscribe
        // to environmental events. Otherwise Zirks may begin sending us environmental events
        // before we are ready to get them.
        final UUID sensorUuid = UUID.randomUUID();
        final Set<EnvironmentalSensor.SensorCapability> sensorCapabilities = new HashSet<>();
        sensorCapabilities.add(EnvironmentalSensor.SensorCapability.BAROMETRIC_PRESSURE);
        sensorCapabilities.add(EnvironmentalSensor.SensorCapability.HUMIDITY);
        sensorCapabilities.add(EnvironmentalSensor.SensorCapability.TEMPERATURE);
        final EnvironmentalSensor sensor = new EnvironmentalSensor(sensorUuid.toString(),
                sensorCapabilities, "wunderground");

        final EventSet environmentalEvents = new EventSet(GetEnvironmentSensorReadingEvent.class);

        environmentalEvents.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint zirkEndPoint) {
                if (event instanceof GetEnvironmentSensorReadingEvent) {
                    final GetEnvironmentSensorReadingEvent readingEvent =
                            (GetEnvironmentSensorReadingEvent) event;

                    if ("wunderground".equals(readingEvent.getSensor().getHardwareName()) &&
                            sensorUuid.toString().equals(readingEvent.getSensor().getId())) {
                        final CurrentConditions currentConditions =
                                controller.getCurrentConditions(state, city);

                        if (currentConditions == null) return;

                        if (readingEvent.getCapabilities().contains(
                                EnvironmentalSensor.SensorCapability.BAROMETRIC_PRESSURE)) {
                            bezirk.sendEvent(
                                    new BarometricPressureReadingEvent(sensor, currentConditions.getPressure()));
                        }

                        if (readingEvent.getCapabilities().contains(
                                EnvironmentalSensor.SensorCapability.HUMIDITY)) {
                            bezirk.sendEvent(
                                    new HumidityReadingEvent(sensor, currentConditions.getRelativeHumidity()));
                        }

                        if (readingEvent.getCapabilities().contains(
                                EnvironmentalSensor.SensorCapability.TEMPERATURE)) {
                            bezirk.sendEvent(
                                    new TemperatureReadingEvent(sensor, currentConditions.getTemperature()));
                        }
                    }
                }
            }
        });

        bezirk.subscribe(environmentalEvents);

        bezirk.sendEvent(new EnvironmentalSensorsDetectedEvent(Collections.singleton(sensor)));
    }
}
