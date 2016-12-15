package com.bezirk.adapter.obd.config;

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

import java.util.ArrayList;
import java.util.List;

/***
 * This class will hold a list of all the OBD command objects that needs to be supported in the application.
 * Based on the selection of commands from the user, the corresponding command object shall be retrieved from the list by
 * using the getName() method within each of the command object.
 */
public final class OBDCommandConfig {

    public static List<ObdCommand> getCommands() {
        final List<ObdCommand> cmds = new ArrayList<>();

        // Control
        cmds.add(new DistanceMILOnCommand());
        cmds.add(new TroubleCodesCommand());
        cmds.add(new VinCommand());

        // Engine
        cmds.add(new RPMCommand());
        cmds.add(new ThrottlePositionCommand());

        // Fuel
        cmds.add(new FindFuelTypeCommand());
        cmds.add(new FuelLevelCommand());
        cmds.add(new OilTempCommand());


        // Temperature
        cmds.add(new AirIntakeTemperatureCommand());
        cmds.add(new AmbientAirTemperatureCommand());
        cmds.add(new EngineCoolantTemperatureCommand());

        // Misc
        cmds.add(new SpeedCommand());

        return cmds;
    }
}
