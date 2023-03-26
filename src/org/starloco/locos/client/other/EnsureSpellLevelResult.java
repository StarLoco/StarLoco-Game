package org.starloco.locos.client.other;

public class EnsureSpellLevelResult {
    public final boolean changed;
    public final int ptsDelta, oldLevel;
    public final boolean worked; // can only be false when spell/level doesn't exist, or modPoints is true

    public EnsureSpellLevelResult(boolean changed, int ptsDelta, int oldLevel, boolean worked) {
        this.changed = changed;
        this.ptsDelta = ptsDelta;
        this.oldLevel = oldLevel;
        this.worked = worked;
    }
}
