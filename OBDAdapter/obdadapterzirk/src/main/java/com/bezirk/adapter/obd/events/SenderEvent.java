package com.bezirk.adapter.obd.events;

import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;

public class SenderEvent {
    private final ZirkEndPoint zirkEndPoint;
    private final Event event;

    public SenderEvent(ZirkEndPoint zirkEndPoint, Event event) {
        this.zirkEndPoint = zirkEndPoint;
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    public ZirkEndPoint getZirkEndPoint() {
        return zirkEndPoint;
    }
}

