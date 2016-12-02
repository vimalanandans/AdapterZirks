package com.bezirk.adapter.obd.config;

import com.bezirk.adapter.obd.enums.OBDQueryParameter;

import java.util.Arrays;
import java.util.List;

/**
 * Created by DEV6KOR on 9/8/2016.
 */
public class CommandConstants {

    public static final String ERR_CODES = "ERROR_CODES";
    public static final String NO_DATA = "NO DATA";

    public static List<String> highFrequencyParams = Arrays.asList(
            OBDQueryParameter.ENGINE_RPM.getValue(),
            OBDQueryParameter.SPEED.getValue(),
            OBDQueryParameter.ENGINE_COOLANT_TEMP.getValue()
    );
}
