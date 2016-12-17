package com.bezirk.adapter.obd.config;

import com.bezirk.adapter.obd.enums.OBDQueryParameter;
import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.control.DistanceMILOnCommand;
import com.github.pires.obd.commands.control.TroubleCodesCommand;
import com.github.pires.obd.commands.control.VinCommand;
import com.github.pires.obd.commands.engine.OilTempCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.engine.ThrottlePositionCommand;
import com.github.pires.obd.commands.fuel.FindFuelTypeCommand;
import com.github.pires.obd.commands.fuel.FuelLevelCommand;
import com.github.pires.obd.commands.temperature.AirIntakeTemperatureCommand;
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;

/***
 * This class will hold a list of all the OBD command objects that needs to be supported in the application.
 * Based on the selection of commands from the user, the corresponding command object will be returned from the list.
 */
public final class OBDCommandConfig {

    public static ObdCommand getOBDCommand(final OBDQueryParameter queryParameter) {
        ObdCommand command = null;
        switch (queryParameter) {
            case AMBIENT_AIR_TEMP:
                command = new AmbientAirTemperatureCommand();
                break;
            case DISTANCE_TRAVELED_MIL_ON:
                command = new DistanceMILOnCommand();
                break;
            case TROUBLE_CODES:
                command = new TroubleCodesCommand();
                break;
            case VIN:
                command = new VinCommand();
                break;
            case THROTTLE_POS:
                command = new ThrottlePositionCommand();
                break;
            case FUEL_TYPE:
                command = new FindFuelTypeCommand();
                break;
            case FUEL_LEVEL:
                command = new FuelLevelCommand();
                break;
            case ENGINE_OIL_TEMP:
                command = new OilTempCommand();
                break;
            case AIR_INTAKE_TEMP:
                command = new AirIntakeTemperatureCommand();
                break;
            case ENGINE_COOLANT_TEMP:
                command = new EngineCoolantTemperatureCommand();
                break;
            case ENGINE_RPM:
                command = new RPMCommand();
                break;
            case SPEED:
                command = new SpeedCommand();
                break;
        }
        return command;
    }
}
