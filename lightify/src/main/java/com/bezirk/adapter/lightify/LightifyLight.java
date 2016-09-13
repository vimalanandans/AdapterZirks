package com.bezirk.adapter.lightify;

import com.bezirk.hardwareevents.light.Light;

import java.util.Locale;

public class LightifyLight extends Light {
    private final short lightId;
    private final byte[] mac;
    private final byte type;
    private final byte[] firwareVersion;
    private final byte online;
    private final short groupId;
    private final byte status;

    public LightifyLight(String id, String hardwareName, short lightId, byte[] mac, byte type, byte[] firwareVersion, byte online, short groupId, byte status) {
        super(id, hardwareName);

        this.lightId = lightId;
        this.mac = mac;
        this.type = type;
        this.firwareVersion = firwareVersion;
        this.online = online;
        this.groupId = groupId;
        this.status = status;
    }

    public short getLightId() {
        return lightId;
    }

    public byte[] getMac() {
        return mac;
    }

    public byte getType() {
        return type;
    }

    public byte[] getFirwareVersion() {
        return firwareVersion;
    }

    public byte getOnline() {
        return online;
    }

    public short getGroupId() {
        return groupId;
    }

    public byte getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(),
                "lightify bulb: id = %x, type = %d, online = %d, groupId = %x, status (on/off) = %d",
                lightId, type, online, groupId, status);
    }
}
