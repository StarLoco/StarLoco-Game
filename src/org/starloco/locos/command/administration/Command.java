package org.starloco.locos.command.administration;

import java.util.HashMap;

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
