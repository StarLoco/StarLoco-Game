package org.starloco.locos.anims;

import org.classdump.luna.Table;
import org.starloco.locos.script.DataScriptVM;

import java.security.InvalidParameterException;

public class KeyFrame {
    public final int frame;
    public final int duration;

    private KeyFrame(int frame, int duration) {
        this.frame = frame;
        this.duration = duration;
    }

    public static KeyFrame fromScriptValue(Table t) {
        int frame = DataScriptVM.rawInt(t, 1L);
        int duration = DataScriptVM.rawOptionalInt(t, 2L, 0);
        if(frame <0) throw new InvalidParameterException("frame must be positive");
        if(duration <0) throw new InvalidParameterException("duration must be strictly positive");
        return new KeyFrame(frame, duration);
    }

    public boolean hasDuration() { return duration != 0; }
}
