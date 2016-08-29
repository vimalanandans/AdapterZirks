package com.bezirk.adapter.belkin.wemo;

import com.bezirk.adapter.upnp.UpnpDiscovery;

import java.util.Set;

public class WeMoAdapter {
    public static Set<String> discoverSwitches() {
        final UpnpDiscovery discovery = new UpnpDiscovery(5000, "upnp:rootdevice",
                "upnp:rootdevice");
        final Set<String> locations = discovery.discoverDevices();

        return locations;
    }
}
