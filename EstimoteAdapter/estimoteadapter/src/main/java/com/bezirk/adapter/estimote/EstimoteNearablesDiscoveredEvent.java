package com.bezirk.adapter.estimote;

import com.bezirk.middleware.messages.Event;

import java.util.List;

public class EstimoteNearablesDiscoveredEvent extends Event {
    private final List<EstimoteNearable> estimoteNearables;

    public EstimoteNearablesDiscoveredEvent(List<EstimoteNearable> estimoteNearables) {
        this.estimoteNearables = estimoteNearables;
    }

    public List<EstimoteNearable> getNearables() {
        return estimoteNearables;
    }
}
