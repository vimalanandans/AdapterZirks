package com.bezirk.adapter.belkin.wemo;

import com.bezirk.hardwareevents.outlet.Outlet;

public class WeMoOutlet extends Outlet {
    private final String url;

    public WeMoOutlet(String id, String hardwareName, String url) {
        super(id, hardwareName);

        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return String.format("WeMo switch: id = %s, hardwareName = %s, url =  %s",
                getId(), getHardwareName(), getUrl());
    }
}
