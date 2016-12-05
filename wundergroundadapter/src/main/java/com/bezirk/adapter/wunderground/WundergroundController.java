package com.bezirk.adapter.wunderground;

import com.bezirk.hardwareevents.Pressure;
import com.bezirk.hardwareevents.Temperature;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class WundergroundController {
    private static final Logger logger = LoggerFactory.getLogger(WundergroundController.class);
    private final String baseUrl;

    WundergroundController(String apiKey) {
        baseUrl = String.format("http://api.wunderground.com/api/%s/conditions/q/", apiKey);
    }

    CurrentConditions getCurrentConditions(String state, String city) {
        final String weatherData = getWeatherData(baseUrl + state + "/" + city + ".json");
        try {
            final JSONObject json = (JSONObject) new JSONParser().parse(weatherData);
            final JSONObject currentObservation = (JSONObject) json.get("current_observation");

            final Double temperature = (Double) currentObservation.get("temp_f");
            final double pressure = Double.parseDouble((String) currentObservation.get("pressure_mb"));
            final String relativeHumidity = currentObservation.get("relative_humidity").toString();

            return new CurrentConditions(
                    new Temperature(temperature, Temperature.TemperatureUnit.FAHRENHEIT),
                    new Pressure(pressure, Pressure.PressureUnit.HECTOPASCALS),
                    Double.valueOf(relativeHumidity.replaceAll("%", "")));
        } catch (ParseException e) {
            logger.error("Failed to parse Wunderground response", e);
            return null;
        }
    }

    private String getWeatherData(String url) {
        try {
            final URL requestUrl = new URL(url);

            final HttpURLConnection httpConnection = (HttpURLConnection) requestUrl.openConnection();
            httpConnection.setRequestProperty("Content-Type", "application/json");
            httpConnection.setRequestProperty("Accept", "application/json");
            httpConnection.setRequestMethod("GET");
            httpConnection.setDoInput(true);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(httpConnection.getInputStream()));

            final StringBuilder httpReply = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                httpReply.append(inputLine);
            in.close();

            logger.trace("Request sent to: {}", url);
            logger.trace("Wunderground's response: {}", httpConnection.getResponseMessage());

            return httpReply.toString();

        } catch (IOException e) {
            logger.error("Error sending payload to Wunderground", e);
        }

        return "";
    }
}
