package org.starloco.locos.command.administration;

import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.login.GroupData;

import java.util.ArrayList;
import java.util.List;

public class Group {

    private final static List<Group> groups = new ArrayList<>();

    private final int id;
    private final String name;
    private final boolean isPlayer;
    private List<Command> commands = new ArrayList<>();

    public Group(int id, String name, boolean isPlayer, String commands) {
        this.id = id;
        this.name = name;
        this.isPlayer = isPlayer;

        if (commands.equalsIgnoreCase("all")) {
            this.commands = Command.commands;
        } else {
            if (commands.contains(",")) {
                for (String str : commands.split(","))
                    this.commands.add(Command.getCommandById(Integer.parseInt(str)));
            } else {
                this.commands.add(Command.getCommandById(Integer.parseInt(commands)));
            }
        }

        Group.groups.add(this);
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public boolean isPlayer() {
        return isPlayer;
    }

    public List<Command> getCommands() {
        return commands;
    }

    public boolean haveCommand(String name) {
        for (Command command : this.commands)
            if (command.getArguments()[0].equalsIgnoreCase(name))
                return true;
        return false;
    }

    public static void reload() {
        Group.groups.clear();
        DatabaseManager.get(GroupData.class).loadFully();
    }

    public static Group getGroupeById(int id) {
        for(Group group : Group.groups)
            if(group.id == id)
                return group;
        return null;
    }

    public static List<Group> getGroups() {
        return groups;
    }
}
