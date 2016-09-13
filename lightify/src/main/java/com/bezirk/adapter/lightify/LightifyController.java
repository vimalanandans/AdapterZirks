package com.bezirk.adapter.lightify;

import com.bezirk.hardwareevents.light.Light;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashSet;
import java.util.Set;

public class LightifyController {
    private static final Logger logger = LoggerFactory.getLogger(LightifyController.class);
    private static final byte COMMAND_ALL_LIGHT_STATUS = 0x13;
    private static final byte COMMAND_BRIGHTNESS = 0x31;
    private static final byte COMMAND_ONOFF = 0x32;
    private static final byte COMMAND_LIGHT_STATUS = 0x68;

    private static final byte FLAG_NODE = 0;
    private static final byte FLAG_ZONE = 2;

    private int seq = 1;

    private final DataOutputStream dataOut;
    private final DataInputStream dataIn;

    public LightifyController(String gatewayAddress) throws IOException {
        final Socket gatewaySocket = new Socket(InetAddress.getByName(gatewayAddress), 4000);

        dataOut = new DataOutputStream(gatewaySocket.getOutputStream());
        dataIn = new DataInputStream(gatewaySocket.getInputStream());
    }

    public Set<Light> findLights() {
        final Set<Light> lights = new HashSet<>();

        final byte[] data = new byte[1];
        data[0] = 1;
        final byte[] response = sendPayload(buildCommand(FLAG_ZONE, COMMAND_ALL_LIGHT_STATUS, data));

        final ByteBuffer responseBuffer = ByteBuffer.allocate(response.length);
        responseBuffer.order(ByteOrder.LITTLE_ENDIAN);
        responseBuffer.put(response);

        final int lightsCount = responseBuffer.getShort(7);

        final int headerLength = 9;
        final int lightStatusLength = 50;

        for (int i = 0; i < lightsCount; i++) {
            final int lightStatusPos = headerLength + (i * lightStatusLength);

            byte[] lightStatusBytes = new byte[lightStatusLength];
            System.arraycopy(response, lightStatusPos, lightStatusBytes, 0, lightStatusLength);

            final ByteBuffer lightStatusBuffer = ByteBuffer.allocate(lightStatusLength);
            lightStatusBuffer.order(ByteOrder.LITTLE_ENDIAN);
            lightStatusBuffer.put(lightStatusBytes);
            lightStatusBuffer.position(0);

            short lightId = lightStatusBuffer.getShort();

            byte[] mac = new byte[8];
            lightStatusBuffer.get(mac);
            byte type = lightStatusBuffer.get();
            byte[] firmwareVersion = new byte[4];
            lightStatusBuffer.get(firmwareVersion);
            byte online = lightStatusBuffer.get();
            short groupId = lightStatusBuffer.getShort();
            byte status = lightStatusBuffer.get();

            if (type == 2 || type == 4 /** OSRAM  bulbs **/) {
                try {
                    final LightifyLight light = new LightifyLight(new String(mac, "ISO-8859-1"),
                            Hardware.HARDWARE_LIGHTIFY_BULB.toString(),
                            lightId, mac, type, firmwareVersion, online, groupId, status);
                    lights.add(light);

                    if (logger.isDebugEnabled()) logger.debug("Found new lightify light {}", light.toString());
                } catch (UnsupportedEncodingException e) {
                    logger.error("Failed to encode lightify light id", e);
                }
            }
        }

        return lights;
    }

    private boolean isLightifyCompatible(Light light) {
        return Hardware.HARDWARE_LIGHTIFY_BULB.toString().equals(light.getHardwareName());
    }

    public void turnLightOn(Light light) {
        if (!isLightifyCompatible(light)) return;

        setLightStatus(light, (byte) 1);
    }

    public void turnLightOff(Light light) {
        if (!isLightifyCompatible(light)) return;

        setLightStatus(light, (byte) 0);
    }

    private void setLightStatus(Light light, byte status) {
        final byte[] data = new byte[9];

        try {
            System.arraycopy(light.getId().getBytes("ISO-8859-1"), 0, data, 0, light.getId().length());
            data[8] = status;
        } catch (UnsupportedEncodingException e) {
            logger.error("Failed to decode lightify light id", e);
            return;
        }

        sendPayload(buildCommand(FLAG_NODE, COMMAND_ONOFF, data));
    }

    public void setLightBrightness(Light light, int brightnessLevel) {
        if (!isLightifyCompatible(light)) return;

        final byte[] data = new byte[11];

        try {
            System.arraycopy(light.getId().getBytes("ISO-8859-1"), 0, data, 0, light.getId().length());
            data[8] = (byte) brightnessLevel;
        } catch (UnsupportedEncodingException e) {
            logger.error("Failed to decode lightify light id", e);
            return;
        }

        sendPayload(buildCommand(FLAG_NODE, COMMAND_BRIGHTNESS, data));
    }

    private byte[] buildCommand(byte flag, byte command, byte[] data) {
        final int length = 6 + data.length;
        final ByteBuffer payload = ByteBuffer.allocate(length + 2);
        payload.order(ByteOrder.LITTLE_ENDIAN);

        payload.putShort((short) length);
        payload.put(flag);
        payload.put(command);
        payload.put((byte) 0);
        payload.put((byte) 0);
        payload.put((byte) 0x7);
        payload.put((byte) ++seq);
        payload.put(data);

        return payload.array();
    }

    private byte[] sendPayload(byte[] payload) {
        try {
            dataOut.write(payload);
        } catch (IOException e) {
            logger.error("Failed to send payload to Lightify gateway", e);
            return new byte[0];
        }

        byte[] reply;
        try {
            // Read the length of the reply
            final byte[] lengthBytes = new byte[2];
            if (dataIn.read(lengthBytes) != 2) {
                logger.error("Failed to read enough bytes from lightify command response to" +
                        " determine the response length");
                return new byte[0];
            }
            final ByteBuffer lengthBuffer = ByteBuffer.allocate(2);
            lengthBuffer.order(ByteOrder.LITTLE_ENDIAN);
            lengthBuffer.put(lengthBytes);
            final short length = lengthBuffer.getShort(0);

            // Read the actual reply
            reply = new byte[length];
            if (dataIn.read(reply) != length) {
                logger.error("Failed to read the expected number of bytes from lightify command " +
                        "response");
            }
        } catch (IOException e) {
            logger.error("Failed to receive reply from Lightify gateway", e);
            reply = new byte[0];
        }

        return reply;
    }

    public enum Hardware {
        MANUFACTURER("osram"),
        HARDWARE_LIGHTIFY_BULB("osram.lightify.bulb");

        private final String text;

        Hardware(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}
