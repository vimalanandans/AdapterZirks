package com.bezirk.adapter.philips.hue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PhilipsHueController {
    private static final Logger logger = LoggerFactory.getLogger(PhilipsHueController.class);
    private final String baseBridgeUrl;

    public PhilipsHueController(String hueBridgeUrl, String apiKey) {
       this.baseBridgeUrl = String.format("%sapi/%s/", hueBridgeUrl, apiKey);
    }

    public void turnLightOn(String id) {
        String payload = "{\"on\":true}";
        sendPayload(String.format("%s%s/%s/%s", baseBridgeUrl, "lights", id, "state"), payload);
    }

    public void turnLightOff(String id) {
        System.out.println(String.format("%s%s/%s/%s", baseBridgeUrl, "lights", id, "state"));

        String payload = "{\"on\":false}";
        sendPayload(String.format("%s%s/%s/%s", baseBridgeUrl, "lights", id, "state"), payload);
    }

    public void setLightBrightness(String id, byte brightnessLevel) {
        String payload = String.format("{\"bri\":%d}", brightnessLevel);
        sendPayload(String.format("%s%s/%s/%s", baseBridgeUrl, "lights", id, "state"), payload);
    }

    public void setLightColorHSV(String id, int h, int s, int v) {
        String payload = String.format("{\"on\":true, \"hue\":%d, \"sat\":%d, \"bri\":%d}",
                h, s, v);
        sendPayload(String.format("%s%s/%s/%s", baseBridgeUrl, "lights", id, "state"), payload);
    }

    private void sendPayload(String url, String payload) {
        try {
            final URL requestUrl = new URL(url);

            final HttpURLConnection httpConnection = (HttpURLConnection) requestUrl.openConnection();
            httpConnection.setRequestProperty("Content-Type", "application/json");
            httpConnection.setRequestProperty("Accept", "application/json");
            httpConnection.setRequestMethod("PUT");
            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);

            final OutputStreamWriter osw = new OutputStreamWriter(httpConnection.getOutputStream());
            osw.write(payload);
            osw.flush();
            osw.close();

            logger.trace("Bridge's payload response: {}", httpConnection.getResponseMessage());
            logger.trace("Sent to Hue bridge: {}", payload);

        } catch (IOException e) {
            logger.error("Error sending payload to Hue bridge", e);
        }
    }
}
