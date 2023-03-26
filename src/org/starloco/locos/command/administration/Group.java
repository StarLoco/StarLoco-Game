package org.starloco.locos.command.administration;

import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.login.GroupData;

import java.util.*;
import java.util.stream.Collectors;

public class Group {

    private final static HashMap<Integer, Group> groups = new HashMap<>();

    private final int id;
    private final String name;
    private final boolean isPlayer;
    private final List<String> commands;
    private final boolean allCommands;

    public Group(int id, String name, boolean isPlayer, boolean allCommands, List<String> commands) {
        this.id = id;
        this.name = name;
        this.isPlayer = isPlayer;
        this.commands = new ArrayList<>(commands);
        this.allCommands = allCommands;
        Group.groups.put(id, this);
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
        if(allCommands) return new LinkedList<>(Command.commands.values());
        return commands.stream().map(Command.commands::get).collect(Collectors.toList());
    }

    public boolean haveCommand(String name) {
        if(allCommands) return true;
        return commands.contains(name);
    }

//    public static void reload() {
//        Group.groups.clear();
//        DatabaseManager.get(GroupData.class).loadFully();
//    }

    public static Group byId(int id) {
        return groups.get(id);
    }

    public static Collection<Group> getGroups() {
        return groups.values();
    }
}
