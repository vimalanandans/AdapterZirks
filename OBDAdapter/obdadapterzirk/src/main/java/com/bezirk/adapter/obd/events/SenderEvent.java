package com.bezirk.adapter.obd.events;

import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;

public class SenderEvent {
    private ZirkEndPoint zirkEndPoint;
    private Event event;

    public SenderEvent(ZirkEndPoint zirkEndPoint, Event event) {
        this.zirkEndPoint = zirkEndPoint;
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    public void setEven(Event even) {
        this.event = even;
    }

    public ZirkEndPoint getZirkEndPoint() {
        return zirkEndPoint;
    }

    public void setZirkEndPoint(ZirkEndPoint zirkEndPoint) {
        this.zirkEndPoint = zirkEndPoint;
    }
}

