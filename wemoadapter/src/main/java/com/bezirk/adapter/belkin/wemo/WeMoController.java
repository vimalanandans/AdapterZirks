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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WeMoController {
    private static final Logger logger = LoggerFactory.getLogger(WeMoController.class);

    private final Map<String, String> idToAddressMap = new HashMap<>();

    public WeMoController(Set<WeMoOutlet> knownOutlets) {
        for (WeMoOutlet outlet : knownOutlets) {
            idToAddressMap.put(outlet.getId(), outlet.getUrl());
        }
    }

    private String getOutletUrl(Outlet outlet) {
        if (idToAddressMap.containsKey(outlet.getId()) &&
                outlet.getHardwareName().startsWith(Hardware.MANUFACTURER.toString())) {
            return idToAddressMap.get(outlet.getId());
        }

        return null;
    }

    public void turnSwitchOn(Outlet outlet) {
        final String url = getOutletUrl(outlet);

        if (url != null) {
            sendPayload(url, "<BinaryState>1</BinaryState>", "SetBinaryState");

            if (logger.isTraceEnabled()) {
                logger.trace("Sent payload to turn on outlet {} - {}", outlet.getId(),
                        outlet.getHardwareName());
            }
        } else {
            if (logger.isTraceEnabled())
                logger.trace("Ignoring outlet on event for unknown outlet: {} - {}", outlet.getId(),
                        outlet.getHardwareName());
        }
    }

    public void turnSwitchOff(Outlet outlet) {
        final String url = getOutletUrl(outlet);

        if (url != null) {
            sendPayload(url, "<BinaryState>0</BinaryState>", "SetBinaryState");

            if (logger.isTraceEnabled()) {
                logger.trace("Sent payload to turn off outlet {} - {}", outlet.getId(),
                        outlet.getHardwareName());
            }
        } else {
            if (logger.isTraceEnabled())
                logger.trace("Ignoring outlet off event for unknown outlet: {} - {}", outlet.getId(),
                        outlet.getHardwareName());
        }
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

            if (logger.isTraceEnabled()) {
                logger.trace("Sent to WeMo switch: {}", payload);
                logger.trace("WeMo's payload response: {}", httpConnection.getResponseMessage());
            }

            return httpReply.toString();

        } catch (IOException e) {
            logger.error("Error sending payload to wemo switch", e);
        }

        return "";
    }

    public enum Hardware {
        MANUFACTURER("belkin"),
        HARDWARE_INSIGHT("belkin.insight");

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
