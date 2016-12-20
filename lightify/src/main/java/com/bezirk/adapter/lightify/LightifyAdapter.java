package com.bezirk.adapter.lightify;

import com.bezirk.hardwareevents.light.Light;
import com.bezirk.hardwareevents.light.LightEvent;
import com.bezirk.hardwareevents.light.LightsDetectedEvent;
import com.bezirk.hardwareevents.light.SetLightBrightnessEvent;
import com.bezirk.hardwareevents.light.TurnLightOffEvent;
import com.bezirk.hardwareevents.light.TurnLightOnEvent;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LightifyAdapter {
    private static final Logger logger = LoggerFactory.getLogger(LightifyAdapter.class);

    private final LightifyController lightifyController;

    public LightifyAdapter(final Bezirk bezirk, String gatewayAddress) throws IOException {
        lightifyController = new LightifyController(gatewayAddress);

        final EventSet lightEventSet = new EventSet(TurnLightOnEvent.class, TurnLightOffEvent.class,
                SetLightBrightnessEvent.class);

        lightEventSet.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                if (event instanceof LightEvent) {
                    final Light light = ((LightEvent) event).getLight();

                    if (event instanceof TurnLightOnEvent) {
                        lightifyController.turnLightOn(light);
                    } else if (event instanceof TurnLightOffEvent) {
                        lightifyController.turnLightOff(light);
                    } else if (event instanceof SetLightBrightnessEvent) {
                        SetLightBrightnessEvent brightnessEvent = (SetLightBrightnessEvent) event;
                        logger.trace(brightnessEvent.toString());

                        lightifyController.setLightBrightness(light, brightnessEvent.getBrightnessLevel());
                    }
                }
            }
        });

        bezirk.subscribe(lightEventSet);
        logger.trace("Listening for osram lightify events");

        bezirk.sendEvent(new LightsDetectedEvent(lightifyController.findLights()));
        logger.trace("Sent discovered lights event for lightify");
    }

    public static Set<String> discoverGateways() {
        final Set<String> gateways = new HashSet<>();

        final byte[] address;

        try {
            address = InetAddress.getLocalHost().getAddress();
        } catch (UnknownHostException e) {
            logger.error("Failed to address of local host when discovering gateways", e);
            return gateways;
        }

        ExecutorService portScanExecutor = Executors.newFixedThreadPool(255);

        final byte myLastOctet = address[3];

        for (int testOctet = 0; testOctet < 256; testOctet++) {
            // Do not self scan
            if (testOctet == myLastOctet) {
                continue;
            }

            final int o = testOctet;

            portScanExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    address[3] = (byte) o;

                    final InetAddress testAddress;
                    try {
                        testAddress = InetAddress.getByAddress(address);
                    } catch (UnknownHostException e) {
                        logger.error("Failed to build lightify test address", e);
                        return;
                    }

                    try (Socket s = new Socket(testAddress, 4000)) {
                        // If we didn't get an exception opening this socket, port 4000 is
                        // open and it might be a lightify gateway.
                        gateways.add(testAddress.getHostAddress());
                    } catch (IOException e) {
                        // Not a potential lightify gateway
                    }
                }
            });
        }

        portScanExecutor.shutdown();
        try {
            portScanExecutor.awaitTermination(10000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.info("Interrupted port scan executor wait", e);
            Thread.currentThread().interrupt();
        }

        return gateways;
    }
}
