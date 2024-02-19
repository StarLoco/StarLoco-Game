package org.starloco.locos.player;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;

public class ShortcutSettings {
    protected final Int2IntArrayMap spellShortcuts = new Int2IntArrayMap();
    protected final Int2IntArrayMap itemShortcuts = new Int2IntArrayMap();

    public synchronized void updateSpellShortcut(int slotID, int spellID) {

    }

    public static class ReadOnlyShortcutSettings extends ShortcutSettings {

    }
}
