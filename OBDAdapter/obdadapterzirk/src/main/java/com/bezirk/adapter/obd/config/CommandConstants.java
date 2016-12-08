package com.bezirk.adapter.obd.config;

import com.bezirk.adapter.obd.enums.OBDQueryParameter;

import java.util.Arrays;
import java.util.List;

/**
 * For configuring parameters that needs to be queried very frequently wrt time
 */
public class CommandConstants {

    public static final String NO_DATA = "NO DATA";

    public static List<String> highFrequencyParams = Arrays.asList(
            OBDQueryParameter.ENGINE_RPM.getValue(),
            OBDQueryParameter.SPEED.getValue()
    );
}
