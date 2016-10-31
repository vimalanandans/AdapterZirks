package com.bezirk.adapter.obd.events;

import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.core.sphere.impl.Zirk;
import com.bezirk.middleware.messages.Event;

/**
 * Created by dev6kor on 10/31/2016.
 */
public class SenderEvent {
    private ZirkEndPoint zirkEndPoint;

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

    private Event event;
}

