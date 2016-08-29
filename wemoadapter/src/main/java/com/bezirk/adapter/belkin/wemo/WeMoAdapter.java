package com.bezirk.adapter.belkin.wemo;

import com.bezirk.adapter.upnp.UpnpDiscovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class WeMoAdapter {
    private static final Logger logger = LoggerFactory.getLogger(WeMoAdapter.class);

    public static Set<String> discoverSwitches() {
        final UpnpDiscovery discovery = new UpnpDiscovery(5000, "upnp:rootdevice",
                "upnp:rootdevice");
        final Set<String> potentialSwitchLocations = discovery.discoverDevices();

        final Set<String> switchLocations = new HashSet<>();

        for (String location : potentialSwitchLocations) {
            final String deviceDescription = getHttpResponse(location);

            if (isWemoSwitch(deviceDescription)) {
                switchLocations.add(parseWeMoLocation(location));
            }
        }

        return switchLocations;
    }

    private static boolean isWemoSwitch(String deviceDescription) {
        return deviceDescription.contains("Belkin:device") && deviceDescription.contains("WeMo");
    }

    private static String parseWeMoLocation(String location) {
        return location.substring(0, location.lastIndexOf('/'));
    }

    private static String getHttpResponse(String url) {
        try {
            URL requestUrl = new URL(url);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            requestUrl.openConnection().getInputStream()));

            final StringBuilder httpReply = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                httpReply.append(inputLine);
            in.close();

            return httpReply.toString();
        } catch (MalformedURLException e) {
            logger.error("Malformed URL for WeMo discovery", e);
        } catch (IOException e) {
            logger.error("WeMo discovery failed to get device description for " +
                    url, e);
        }

        return "";
    }
}
