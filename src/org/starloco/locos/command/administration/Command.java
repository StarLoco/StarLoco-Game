package org.starloco.locos.command.administration;

import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.login.CommandData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Command {

    public static final HashMap<String, Command> commands = new HashMap<>();
    private final String name;
    private final String args;
    private final String desc;

    public Command(String command, String args, String desc) {
        this.name = command;
        this.args = args;
        this.desc = desc;

        Command.commands.put(command, this);
    }

    public String getName() {
        return name;
    }

    public String getArgs() {
        return args;
    }

    public String getDesc() {
        return desc;
    }
}
