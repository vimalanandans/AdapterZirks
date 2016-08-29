package com.bezirk.adapter.philips.hue;

import com.bezirk.hardwareevents.HexColor;
import com.bezirk.hardwareevents.light.CurrentLightStateEvent;
import com.bezirk.hardwareevents.light.GetLightStateEvent;
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

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
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
                    final String lightId = ((LightEvent) event).getId();

                    if (event instanceof TurnLightOnEvent) {
                        philipsHueController.turnLightOn(lightId);
                    } else if (event instanceof TurnLightOffEvent) {
                        philipsHueController.turnLightOff(lightId);
                    } else if (event instanceof SetLightBrightnessEvent) {
                        SetLightBrightnessEvent brightnessEvt = (SetLightBrightnessEvent) event;
                        logger.trace(brightnessEvt.toString());

                        philipsHueController.setLightBrightness(lightId, brightnessEvt.getBrightnessLevel());
                    } else if (event instanceof SetLightColorEvent) {
                        final SetLightColorEvent colorEvent = (SetLightColorEvent) event;
                        logger.trace(colorEvent.toString());

                        setLightColor(lightId, colorEvent.getColor());
                    } else if (event instanceof GetLightStateEvent) {
                        final CurrentLightStateEvent lightStateEvent =
                                philipsHueController.getLightState(lightId);

                        if (lightEventSet != null)
                            bezirk.sendEvent(sender, lightStateEvent);
                    }
                }
            }
        });

        bezirk.subscribe(lightEventSet);
        logger.trace("Listening for hue light events");

        bezirk.sendEvent(new LightsDetectedEvent(philipsHueController.findLights()));
        logger.trace("Sent discovered lights event");
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
        Set<String> bridges = discoverBridgesUpnp(5000);

        if (bridges.isEmpty()) {
            bridges = discoverBridgesNupnp();
        }

        return bridges;
    }

    private static Set<String> discoverBridgesUpnp(int upnpTimeout /* ms */) {
        String mSearch = "M-SEARCH * HTTP/1.1\r\nHOST: 239.255.255.250:1900\r\n" +
                "MAN: \"ssdp:discover\"\r\nMX: 5\r\nST: ssdp:all\r\n\r\n";
        final byte[] sendData = mSearch.getBytes();

        final Set<String> bridgeBaseUrls = new HashSet<>();

        try {
            final DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                    InetAddress.getByName("239.255.255.250"), 1900);

            final DatagramSocket clientSocket = new DatagramSocket();
            clientSocket.setSoTimeout(upnpTimeout); // Set based on MX timeout (5 seconds)
            clientSocket.send(sendPacket);

            final long start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start < upnpTimeout) {
                final byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                try {
                    clientSocket.receive(receivePacket);
                } catch (SocketTimeoutException ste) {
                    logger.trace("Hue SSDP client timed out", ste);
                }

                final String response = new String(receivePacket.getData());

                if (response.length() > 0 && response.contains("IpBridge")) {
                    final String deviceLocation = parseUpnpLocation(response);

                    if (logger.isTraceEnabled())
                        logger.trace("Found potential Hue bridge: {}", deviceLocation);

                    final String deviceDescription = getHttpResponse(parseUpnpLocation(response));

                    if (isHueBridge(deviceDescription)) {
                        final String urlBase = parseUrlBase(deviceDescription);
                        bridgeBaseUrls.add(urlBase);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Hue SSDP discover failed", e);
        }

        return bridgeBaseUrls;
    }

    private static String parseUpnpLocation(String upnpResponse) {
        final String locationLabel = "LOCATION:";
        final int locationStart = upnpResponse.toUpperCase().indexOf(locationLabel) +
                locationLabel.length();
        final int endLine = upnpResponse.indexOf('\n', locationStart);

        return upnpResponse.substring(locationStart, endLine).trim();
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
            logger.error("Hue discovery failed", e);
        }

        return "";
    }

    private void setLightColor(String lightId, HexColor hexColor) {
        final Color color = Color.decode(hexColor.getHexString());
        final float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        final int hue = (int) (hsb[0] * 65535);
        final int sat = (int) (hsb[1] * 255);
        final int bri = (int) (hsb[2] * 255);

        logger.trace("H: {} S: {} b: {}", hue, sat, bri);
        philipsHueController.setLightColorHSV(lightId, hue, sat, bri);
    }
}