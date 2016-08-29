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
    private final String deviceIdentifer;
    private final String searchTarget;

    public UpnpDiscovery(int upnpTimeout, String deviceIdentifier) {
        this.upnpTimeout = upnpTimeout;
        this.deviceIdentifer = deviceIdentifier;
        searchTarget = "ssdp:all";
    }

    public UpnpDiscovery(int upnpTimeout, String deviceIdentifier, String searchTarget) {
        this.upnpTimeout = upnpTimeout;
        this.deviceIdentifer = deviceIdentifier;
        this.searchTarget = searchTarget;
    }

    public Set<String> discoverDevices() {
        final String mSearch = "M-SEARCH * HTTP/1.1\r\nHOST: 239.255.255.250:1900\r\n" +
                "MAN: \"ssdp:discover\"\r\nMX: 5\r\nST: " + searchTarget + "\r\n\r\n";
        final byte[] sendData = mSearch.getBytes();

        final Set<String> deviceLocations = new HashSet<>();

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
                    logger.trace("SSDP client timed out", ste);
                }

                final String response = new String(receivePacket.getData());

                if (response.length() > 0 && response.contains(deviceIdentifer)) {
                    final String deviceLocation = parseUpnpLocation(response);

                   deviceLocations.add(deviceLocation);
                }
            }
        } catch (IOException e) {
            logger.error("SSDP discover failed", e);
        }

        return deviceLocations;
    }

    private static String parseUpnpLocation(String upnpResponse) {
        final String locationLabel = "LOCATION:";
        final int locationStart = upnpResponse.toUpperCase().indexOf(locationLabel) +
                locationLabel.length();
        final int endLine = upnpResponse.indexOf('\n', locationStart);

        return upnpResponse.substring(locationStart, endLine).trim();
    }
}
