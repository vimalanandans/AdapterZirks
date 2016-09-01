package com.bezirk.adapter.belkin.wemo;

import com.bezirk.hardwareevents.outlet.Outlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeMoController {
    private static final Logger logger = LoggerFactory.getLogger(WeMoController.class);

    public void turnSwitchOn(Outlet outlet) {
        sendPayload(outlet.getId(), "<BinaryState>1</BinaryState>", "SetBinaryState");
    }

    public void turnSwitchOff(Outlet outlet) {
        sendPayload(outlet.getId(), "<BinaryState>0</BinaryState>", "SetBinaryState");
    }

    private String sendPayload(String id, String stateBody, String soapAction) {
        String payload = String.format("<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                "<s:Body>" +
                "<u:%s xmlns:u=\"urn:Belkin:service:basicevent:1\">" +
                "%s" +
                "</u:%s>" +
                "</s:Body>" +
                "</s:Envelope>", soapAction, stateBody, soapAction);

        try {
            final URL requestUrl = new URL(id + "/upnp/control/basicevent1");

            final HttpURLConnection httpConnection = (HttpURLConnection) requestUrl.openConnection();
            httpConnection.setRequestProperty("SOAPACTION",
                    "\"urn:Belkin:service:basicevent:1#" + soapAction + "\"");
            httpConnection.setRequestProperty("Content-Type", "text/xml; charset=\"utf-8\"");
            httpConnection.setRequestProperty("Accept", "");
            httpConnection.setRequestMethod("POST");
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

            logger.trace("Sent to wemo switch: {}", payload);
            logger.trace("WeMo's payload response: {}", httpConnection.getResponseMessage());

            return httpReply.toString();

        } catch (IOException e) {
            logger.error("Error sending payload to wemo switch", e);
        }

        return "";
    }
}
