import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PhilipsHueController {

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
        URL url = null;

        try {
            url = new URL("http://192.168.1.30/api/oFZsQakh9XzQiVhkIuuv83xsycRsmfgcEn5eBvjm/lights/2/state");
        } catch (MalformedURLException exception) {
            exception.printStackTrace();
        }

        HttpURLConnection httpURLConnection = null;
        DataOutputStream dataOutputStream = null;

        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setRequestMethod("PUT");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStreamWriter osw = new OutputStreamWriter(httpURLConnection.getOutputStream());
            osw.write(payload);
            osw.flush();
            osw.close();
            System.err.println(httpURLConnection.getResponseMessage());

            System.out.println("Sent to Hue Bridge: " + payload);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }


}
