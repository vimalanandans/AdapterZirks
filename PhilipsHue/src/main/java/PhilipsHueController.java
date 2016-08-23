import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhilipsHueController {
    private static final Logger logger = LoggerFactory.getLogger(PhilipsHueController.class);

    public void turnLightOn() {
        String payload = "{\"on\":true}";
        sendPayload(payload);
    }

    public void turnLightOff() {
        String payload = "{\"on\":false}";
        sendPayload(payload);
    }

    public void setLightColorHSV(int h, int s, int v) {
        String payload = "{\"on\":true, \"hue\":" + h + ", \"sat\":" + s + ", \"bri\":" + v + "}";
        sendPayload(payload);
    }

    private void sendPayload(String payload) {
        final URL url;

        try {
            url = new URL("http://192.168.1.30/api/oFZsQakh9XzQiVhkIuuv83xsycRsmfgcEn5eBvjm/lights/2/state");
        } catch (MalformedURLException e) {
            logger.error("Invalid URL for Philips Hue bridge, payload meant for bridge will " +
                    "not be sent", e);
            return;
        }

        try {
            final HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
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
