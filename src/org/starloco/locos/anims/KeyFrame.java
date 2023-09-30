package org.starloco.locos.anims;

import org.classdump.luna.Table;
import org.starloco.locos.script.DataScriptVM;

import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.Map;

public class KeyFrame {
    private final double FPS = 60F;

    public final int frame;
    public final String nextFrame;
    private final int durationMillis;
    private final boolean interactive;
    private final Map<String, Integer> cellOverrides;

    private KeyFrame(int frame, int duration, String nextFrame, boolean interactive, Map<String, Integer> cellOverrides) {
        if(duration > 0 && nextFrame.isEmpty()){
            throw new InvalidParameterException("nextFrame is mandatory when duration is set");
        }
        this.frame = frame;
        this.durationMillis = duration;
        this.interactive = interactive;
        this.nextFrame = nextFrame;
        this.cellOverrides = Collections.unmodifiableMap(cellOverrides);
    }

    public static KeyFrame fromScriptValue(Table t) {
        int frame = DataScriptVM.rawInt(t, "frame");
        int duration = DataScriptVM.rawOptionalInt(t, "duration", 0);
        String nextFrame = DataScriptVM.rawOptionalString(t, "next");
        boolean interactive = DataScriptVM.rawOptional(t, "interactive").map(Boolean.class::cast).orElse(false);

        Map<String,Integer> cellOverrides = DataScriptVM.rawOptional(t, "overrides")
            .map(Table.class::cast)
            .map(tv -> DataScriptVM.mapFromScript(tv, Object::toString, v -> ((Long)v).intValue()))
            .orElse(Collections.emptyMap());

        if(frame <0) throw new InvalidParameterException("frame must be positive");
        if(duration <0) throw new InvalidParameterException("duration must be strictly positive");
        if(duration !=0 && nextFrame.isEmpty()) throw new InvalidParameterException("nextFrame is required when duration >0");

        return new KeyFrame(frame, duration, nextFrame, interactive, Collections.unmodifiableMap(cellOverrides));
    }

    public boolean hasDuration() { return durationMillis != 0; }

    public int durationMillis() { return this.durationMillis; }

    public boolean isObjectInteractive() { return this.interactive; }

    public Map<String, Integer> getCellOverrides() {
        return cellOverrides;
    }
}
