package org.starloco.locos.anims;

import org.classdump.luna.Table;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Animation {
    // Layer2 gfx ID
    public final int id;
    public final String defaultState;
    public final Map<String, KeyFrame> frames;


    public Animation(int id, String defaultState, Map<String, KeyFrame> frames) {
        this.id = id;
        this.defaultState = defaultState;
        this.frames = Collections.unmodifiableMap(frames);
    }

    public KeyFrame getFrame(String name) { return this.frames.get(name); }
}
