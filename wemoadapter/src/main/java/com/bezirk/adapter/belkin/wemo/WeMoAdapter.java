package com.bezirk.adapter.belkin.wemo;

import com.bezirk.adapter.upnp.UpnpDiscovery;

import java.util.Set;

public class WeMoAdapter {
    public static Set<String> discoverSockets() {
        final UpnpDiscovery discovery = new UpnpDiscovery(5000, ".*uuid:Socket.*:urn:Belkin:device:controllee.*");
        final Set<String> locations = discovery.discoverDevices();

        return locations;
    }
}
