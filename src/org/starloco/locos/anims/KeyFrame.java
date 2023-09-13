package org.starloco.locos.anims;

import com.singularsys.jep.functions.IllegalParameterException;
import org.classdump.luna.Table;
import org.starloco.locos.script.DataScriptVM;

import java.security.InvalidParameterException;

public class KeyFrame {
    private final double FPS = 60F;

    public final int frame;
    private final int duration;
    public final String nextFrame;

    private KeyFrame(int frame, int duration, String nextFrame) {
        if(duration > 0 && nextFrame.isEmpty()) throw new IllegalParameterException("nextFrame is mandatory when duration is set")
        this.frame = frame;
        this.duration = duration;
        this.nextFrame = nextFrame;
    }

    public static KeyFrame fromScriptValue(Table t) {
        int frame = DataScriptVM.rawInt(t, 1L);
        int duration = DataScriptVM.rawOptionalInt(t, 2L, 0);
        String nextFrame = DataScriptVM.rawOptionalString(t, 3L);
        if(frame <0) throw new InvalidParameterException("frame must be positive");
        if(duration <0) throw new InvalidParameterException("duration must be strictly positive");
        if(duration !=0 && nextFrame.isEmpty()) throw new InvalidParameterException("nextFrame is required when duration >0");
        return new KeyFrame(frame, duration, nextFrame);
    }

    public boolean hasDuration() { return duration != 0; }

    public int durationMillis() { return (int) (duration*1000D/FPS); }
}
