package com.bezirk.adapter.philips.hue;

import com.bezirk.adapter.upnp.UpnpDiscovery;
import com.bezirk.hardwareevents.HexColor;
import com.bezirk.hardwareevents.light.CurrentLightStateEvent;
import com.bezirk.hardwareevents.light.GetLightStateEvent;
import com.bezirk.hardwareevents.light.Light;
import com.bezirk.hardwareevents.light.LightEvent;
import com.bezirk.hardwareevents.light.LightsDetectedEvent;
import com.bezirk.hardwareevents.light.SetLightBrightnessEvent;
import com.bezirk.hardwareevents.light.SetLightColorEvent;
import com.bezirk.hardwareevents.light.TurnLightOffEvent;
import com.bezirk.hardwareevents.light.TurnLightOnEvent;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PhilipsHueAdapter {
    private static final Logger logger = LoggerFactory.getLogger(PhilipsHueAdapter.class);

    private final PhilipsHueController philipsHueController;

    public PhilipsHueAdapter(final Bezirk bezirk, String hueBridgeUrl, String hueBridgeApiKey) throws MalformedURLException {
        philipsHueController = new PhilipsHueController(hueBridgeUrl, hueBridgeApiKey);

        final EventSet lightEventSet = new EventSet(TurnLightOnEvent.class, TurnLightOffEvent.class,
                SetLightBrightnessEvent.class, SetLightColorEvent.class, GetLightStateEvent.class);

        lightEventSet.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                if (event instanceof LightEvent) {
                    final Light light = ((LightEvent) event).getLight();

                    if (event instanceof TurnLightOnEvent) {
                        philipsHueController.turnLightOn(light);
                    } else if (event instanceof TurnLightOffEvent) {
                        philipsHueController.turnLightOff(light);
                    } else if (event instanceof SetLightBrightnessEvent) {
                        SetLightBrightnessEvent brightnessEvent = (SetLightBrightnessEvent) event;
                        logger.trace(brightnessEvent.toString());

                        philipsHueController.setLightBrightness(light, brightnessEvent.getBrightnessLevel());
                    } else if (event instanceof SetLightColorEvent) {
                        final SetLightColorEvent colorEvent = (SetLightColorEvent) event;
                        logger.trace(colorEvent.toString());

                        setLightColor(light, colorEvent.getColor());
                    } else if (event instanceof GetLightStateEvent) {
                        final CurrentLightStateEvent lightStateEvent =
                                philipsHueController.getLightState(light);

                        // Light state can be null if we are checking the state of a light that
                        // is not a Philips product
                        if (lightStateEvent != null)
                            bezirk.sendEvent(sender, lightStateEvent);
                    }
                }
            }
        });

        bezirk.subscribe(lightEventSet);
        logger.trace("Listening for hue light events");

        bezirk.sendEvent(new LightsDetectedEvent(philipsHueController.findLights()));
        logger.trace("Sent discovered lights event for hue");
    }

    /**
     * Discover Philips hue bridges on the same subnet as the Zirk using the hue adapter. This
     * attempts to discover bridges using SSDP and falls back to N-UPNP if that fails. The returned
     * set is base urls of bridges in the form &lt;protocol&gt;://&lt;domain&gt;/ (e.g.
     * http://192.168.1.3/). This method blocks for 5 seconds due to SSDP discovery. Run the method
     * on another thread if this is a problem.
     *
     * @return base urls for discovered hue bridges.
     */
    public static Set<String> discoverHueBridges() {
        Set<String> bridges = discoverBridgesUpnp();

        if (bridges.isEmpty()) {
            bridges = discoverBridgesNupnp();
        }

        return bridges;
    }

    private static Set<String> discoverBridgesUpnp() {
        final UpnpDiscovery discovery = new UpnpDiscovery(5000, "IpBridge");
        final Set<String> locations = discovery.discoverDevices();

        final Set<String> bridgeBaseUrls = new HashSet<>();

        for (String location : locations) {
            if (logger.isTraceEnabled())
                logger.trace("Found potential Hue bridge: {}", location);

            final String deviceDescription = getHttpResponse(location);

            if (isHueBridge(deviceDescription)) {
                final String urlBase = parseUrlBase(deviceDescription);
                bridgeBaseUrls.add(urlBase);
            }
        }

        return bridgeBaseUrls;
    }

    private static boolean isHueBridge(String deviceDescription) {
        return deviceDescription.contains("hue bridge");
    }

    private static String parseUrlBase(String deviceDescription) {
        final String urlBaseTag = "<URLBase>";
        final int startTag = deviceDescription.indexOf(urlBaseTag) +
                urlBaseTag.length();
        final int endTag = deviceDescription.indexOf("</URLBase>", startTag);

        return deviceDescription.substring(startTag, endTag);
    }

    private static Set<String> discoverBridgesNupnp() {
        String nupnpResponse = getHttpResponse("https://www.meethue.com/api/nupnp");

        if (nupnpResponse.isEmpty()) return Collections.emptySet();

        final Set<String> bridgeBaseUrls = new HashSet<>();

        try {
            JSONArray bridges = (JSONArray) new JSONParser().parse(nupnpResponse);

            for (Object bridge : bridges) {
                String ip = (String) ((JSONObject) bridge).get("internalipaddress");
                bridgeBaseUrls.add(String.format("http://%s/", ip));
            }
        } catch (ParseException e) {
            logger.error("Failed to parse JSON for Hue N-UPNP discovery", e);
            return Collections.emptySet();
        }

        return bridgeBaseUrls;
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
            logger.error("Malformed URL for Hue discovery", e);
        } catch (IOException e) {
            logger.error("Hue discovery to get device description for " + url, e);
        }

        return "";
    }

    private void setLightColor(Light light, HexColor hexColor) {
        final float[] hsb = rgbStringToHsb(hexColor.getHexString());
        final int hue = (int) (hsb[0] * 65535);
        final int sat = (int) (hsb[1] * 255);
        final int bri = (int) (hsb[2] * 255);

        logger.trace("H: {} S: {} b: {}", hue, sat, bri);
        philipsHueController.setLightColorHSV(light, hue, sat, bri);
    }

    // This method is a combination of Color.decode and Color.RGBtoHSB from
    // java.awt.Color in OpenJDK. It is included hear to ensure this code does not depend
    // on Swing classes, which are not available on Android.
    /*
     * Copyright 1995-2007 Sun Microsystems, Inc.  All Rights Reserved.
     * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
     *
     * This code is free software; you can redistribute it and/or modify it
     * under the terms of the GNU General Public License version 2 only, as
     * published by the Free Software Foundation.  Sun designates this
     * particular file as subject to the "Classpath" exception as provided
     * by Sun in the LICENSE file that accompanied this code.
     *
     * This code is distributed in the hope that it will be useful, but WITHOUT
     * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
     * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
     * version 2 for more details (a copy is included in the LICENSE file that
     * accompanied this code).
     *
     * You should have received a copy of the GNU General Public License version
     * 2 along with this work; if not, write to the Free Software Foundation,
     * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
     *
     * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
     * CA 95054 USA or visit www.sun.com if you need additional information or
     * have any questions.
     */
    private float[] rgbStringToHsb(String nm) throws NumberFormatException {
        final Integer intval = Integer.decode(nm);
        final int i = intval.intValue();

        final int r = (i >> 16) & 0xFF;
        final int g = (i >> 8) & 0xFF;
        final int b = i & 0xFF;

        float hue, saturation, brightness;
        final float[] hsbvals = new float[3];

        int cmax = (r > g) ? r : g;
        if (b > cmax) cmax = b;
        int cmin = (r < g) ? r : g;
        if (b < cmin) cmin = b;

        brightness = ((float) cmax) / 255.0f;
        if (cmax != 0)
            saturation = ((float) (cmax - cmin)) / ((float) cmax);
        else
            saturation = 0;
        if (saturation == 0)
            hue = 0;
        else {
            float redc = ((float) (cmax - r)) / ((float) (cmax - cmin));
            float greenc = ((float) (cmax - g)) / ((float) (cmax - cmin));
            float bluec = ((float) (cmax - b)) / ((float) (cmax - cmin));
            if (r == cmax)
                hue = bluec - greenc;
            else if (g == cmax)
                hue = 2.0f + redc - bluec;
            else
                hue = 4.0f + greenc - redc;
            hue = hue / 6.0f;
            if (hue < 0)
                hue = hue + 1.0f;
        }

        hsbvals[0] = hue;
        hsbvals[1] = saturation;
        hsbvals[2] = brightness;

        return hsbvals;
    }
}