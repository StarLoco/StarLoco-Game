package org.starloco.locos.database.data;

import java.util.HashMap;
import java.util.Map;

public class CharacterSettings {
    public final Map<Integer, String> itemShortcuts = new HashMap<>(); // V: ItemHash
    public final Map<Integer, Long> spellShortcuts = new HashMap<>(); // V: SpellID
    public String channels = ""; // Chat channels. TODO: Store with a better Java type: EnumSet ?
    public boolean notifyFriendConnection = true;

}
