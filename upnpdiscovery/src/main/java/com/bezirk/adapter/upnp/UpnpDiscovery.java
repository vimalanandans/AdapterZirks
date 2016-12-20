package com.bezirk.adapter.upnp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Set;

public class UpnpDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(UpnpDiscovery.class);

    private final int upnpTimeout;
    private final String deviceIdentifier;
    private final String searchTarget;

    /**
     * Request responses from all U-PNP devices (i.e. search target is <code>ssdp:all</code>), wait
     * <code>upnpTimeout</code> milliseconds for a response, and only accept devices whose U-PNP
     * search response contains the string <code>deviceIdentifier</code>. A typical timeout for
     * U-PNP is between 5 and 10 seconds. If you are trying to discover a particular manufacturer's
     * hardware, consult their reference material to see if they recommend a timeout.
     *
     * @param upnpTimeout      the time in milliseconds to spend waiting for devices to respond to
     *                         our U-PNP search request
     * @param deviceIdentifier a substring in U-PNP search responses that uniquely matches the
     *                         desired devices' response
     */
    public UpnpDiscovery(int upnpTimeout, String deviceIdentifier) {
        this.upnpTimeout = upnpTimeout;
        this.deviceIdentifier = deviceIdentifier;
        searchTarget = "ssdp:all";
    }

    /**
     * The same as {@link #UpnpDiscovery(int, String)}, except the U-PNP search target must be
     * specified as well. The search target must comply with the U-PNP device architecture
     * <a href="https://openconnectivity.org/upnp/architectural-documents">specification</a>,
     * assuming this class inserts the required carriage returns and line feeds.
     *
     * @param upnpTimeout      the time in milliseconds to spend waiting for devices to respond to
     *                         our U-PNP search request
     * @param deviceIdentifier a substring in U-PNP search responses that uniquely matches the
     *                         desired devices' response
     * @param searchTarget     the U-PNP search target to use in the discovery M-SEARCH request
     */
    public UpnpDiscovery(int upnpTimeout, String deviceIdentifier, String searchTarget) {
        this.upnpTimeout = upnpTimeout;
        this.deviceIdentifier = deviceIdentifier;
        this.searchTarget = searchTarget;
    }

    private static String parseUpnpLocation(String upnpResponse) {
        final String locationLabel = "LOCATION:";
        final int locationStart = upnpResponse.toUpperCase().indexOf(locationLabel) +
                locationLabel.length();
        final int endLine = upnpResponse.indexOf('\n', locationStart);

        return upnpResponse.substring(locationStart, endLine).trim();
    }

    // The hardcoded IP address for sendPacket is a standard UPNP UDP broadcast address, thus
    // hardcoding it is not actually a vulnerability
    @SuppressWarnings("squid:S1313")
    public Set<String> discoverDevices() {
        final String mSearch = "M-SEARCH * HTTP/1.1\r\nHOST: 239.255.255.250:1900\r\n" +
                "MAN: \"ssdp:discover\"\r\nMX: " + String.valueOf(upnpTimeout / 1000) +
                "\r\nST: " + searchTarget + "\r\n\r\n";
        final byte[] sendData = mSearch.getBytes();

        final Set<String> deviceLocations = new HashSet<>();


        try {
            final DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                    InetAddress.getByName("239.255.255.250"), 1900);

            logger.trace("Sent U-PNP search request:\n{}", mSearch);

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
                    logger.trace("SSDP client timed out", ste);
                }

                final String response = new String(receivePacket.getData());

                if (response.length() <= 0) {
                    logger.trace("No UPNP response received");
                    continue;
                }

                if (response.contains(deviceIdentifier)) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Accepted U-PNP response for deviceIdentifier = {}:\n{}",
                                deviceIdentifier, response);
                    }

                    final String deviceLocation = parseUpnpLocation(response);

                    deviceLocations.add(deviceLocation);
                } else if (logger.isTraceEnabled()) {
                    logger.trace("Rejected U-PNP response, does not contain " +
                                    "deviceIdentifier = {}:\n{}",
                            deviceIdentifier, response);
                }
            }
        } catch (IOException e) {
            logger.error("SSDP discover failed", e);
        }

        return deviceLocations;
    }
}
