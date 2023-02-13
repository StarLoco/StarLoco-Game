package org.starloco.locos.command.administration;

import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.login.CommandData;

import java.util.ArrayList;
import java.util.List;

public class Command {

    public static List<Command> commands = new ArrayList<>();

    private final int id;
    private final String[] arguments = new String[3];

    public Command(int id, String command, String args, String description) {
        this.id = id;
        this.arguments[0] = command;
        this.arguments[1] = args == null ? "" : args;
        this.arguments[2] = description == null ? "" : description;

        Command.commands.add(this);
    }

    public int getId() {
        return id;
    }

    public String[] getArguments() {
        return arguments;
    }

    public static Command getCommandById(int id) {
        for(Command command : Command.commands)
            if(command.id == id)
                return command;
        return null;
    }

    public static void reload() {
        Command.commands.clear();
        DatabaseManager.get(CommandData.class).loadFully();
    }
}
