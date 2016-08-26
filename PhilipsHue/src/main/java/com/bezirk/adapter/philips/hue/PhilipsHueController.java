package com.bezirk.adapter.philips.hue;

import com.bezirk.hardwareevents.HexColor;
import com.bezirk.hardwareevents.light.CurrentLightStateEvent;
import com.bezirk.hardwareevents.light.Light;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PhilipsHueController {
    private static final Logger logger = LoggerFactory.getLogger(PhilipsHueController.class);
    private final String baseBridgeUrl;

    public PhilipsHueController(String hueBridgeUrl, String apiKey) {
        this.baseBridgeUrl = String.format("%sapi/%s/", hueBridgeUrl, apiKey);
    }

    public Set<Light> findLights() {
        final String result = sendPayload(String.format("%s%s", baseBridgeUrl, "lights"), "GET", "");

        final Set<Light> foundLights = new HashSet<>();
        try {
            JSONObject json = (JSONObject) new JSONParser().parse(result);

            for (Object key : json.keySet()) {
                String lightId = (String) key;

                JSONObject lightData = (JSONObject) json.get(key);
                String manufacturerName = (String) lightData.get("manufacturername");

                final Light light;
                if (Hardware.MANUFACTURER.toString().equalsIgnoreCase(manufacturerName)) {
                    light = new Light(lightId, Hardware.HARDWARE_HUE.toString());
                } else {
                    light = new Light(lightId, "unknown");
                }

                foundLights.add(light);
            }
        } catch (ParseException e) {
            logger.error("Failed to parse JSON for Hue N-UPNP discovery", e);
            return Collections.emptySet();
        }

        return foundLights;
    }

    public CurrentLightStateEvent getLightState(String id) {
        final String result = sendPayload(String.format("%s%s/%s", baseBridgeUrl, "lights", id), "GET", "");

        try {
            final JSONObject json = (JSONObject) new JSONParser().parse(result);
            final JSONObject state = (JSONObject) json.get("state");

            final Boolean on = (Boolean) state.get("on");
            final CurrentLightStateEvent.LightState lightState;

            if (on) {
                lightState = CurrentLightStateEvent.LightState.ON;
            } else {
                lightState = CurrentLightStateEvent.LightState.OFF;
            }

            final float h = ((Long) state.get("hue")).floatValue();
            final float s = ((Long) state.get("sat")).floatValue();
            final int brightness = ((Long) state.get("bri")).intValue();

            final Color c = Color.getHSBColor(h, s, (float) brightness);
            final String hexColor = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(),
                    c.getBlue());

            return new CurrentLightStateEvent(id, lightState, brightness, new HexColor(hexColor));
        } catch (ParseException e) {
            logger.error("Failed to parse JSON for Hue N-UPNP discovery", e);
            return null;
        }
    }

    public void turnLightOn(String id) {
        String payload = "{\"on\":true}";
        sendPayload(String.format("%s%s/%s/%s", baseBridgeUrl, "lights", id, "state"), "PUT", payload);
    }

    public void turnLightOff(String id) {
        String payload = "{\"on\":false}";
        sendPayload(String.format("%s%s/%s/%s", baseBridgeUrl, "lights", id, "state"), "PUT", payload);
    }

    public void setLightBrightness(String id, byte brightnessLevel) {
        String payload = String.format("{\"bri\":%d}", brightnessLevel);
        sendPayload(String.format("%s%s/%s/%s", baseBridgeUrl, "lights", id, "state"), "PUT", payload);
    }

    public void setLightColorHSV(String id, int h, int s, int v) {
        String payload = String.format("{\"on\":true, \"hue\":%d, \"sat\":%d, \"bri\":%d}",
                h, s, v);
        sendPayload(String.format("%s%s/%s/%s", baseBridgeUrl, "lights", id, "state"), "PUT", payload);
    }

    private String sendPayload(String url, String requestMethod, String payload) {
        try {
            final URL requestUrl = new URL(url);

            final HttpURLConnection httpConnection = (HttpURLConnection) requestUrl.openConnection();
            httpConnection.setRequestProperty("Content-Type", "application/json");
            httpConnection.setRequestProperty("Accept", "application/json");
            httpConnection.setRequestMethod(requestMethod);
            httpConnection.setDoInput(true);

            if (!payload.isEmpty()) {
                httpConnection.setDoOutput(true);
                final OutputStreamWriter osw = new OutputStreamWriter(httpConnection.getOutputStream());
                osw.write(payload);
                osw.flush();
                osw.close();
            }

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(httpConnection.getInputStream()));

            final StringBuilder httpReply = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                httpReply.append(inputLine);
            in.close();

            logger.trace("Sent to Hue bridge: {}", payload);
            logger.trace("Bridge's payload response: {}", httpConnection.getResponseMessage());

            return httpReply.toString();

        } catch (IOException e) {
            logger.error("Error sending payload to Hue bridge", e);
        }

        return "";
    }

    public enum Hardware {
        MANUFACTURER("philips"),
        HARDWARE_HUE("philips.hue");

        private final String text;

        Hardware(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}
