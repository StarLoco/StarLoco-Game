package org.starloco.locos.effects;

import jdk.nashorn.internal.ir.annotations.Immutable;
import org.starloco.locos.common.Formulas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Immutable
public class Effect {
//    interface EffectBuilder {
//        Effect build(int id, Integer min, Integer max, Integer ref, String txt);
//    }
//    private static final Map<Integer, EffectBuilder> builders = new HashMap<>();
//    static {
//
//    }
    public final int id;

    // Values can be null/empty
    public final Integer min;
    public final Integer max;
    public final Integer ref; // Usually used to store an id for something else
    public final String txt;

    protected Effect(int id, Integer min, Integer max, Integer ref, String txt) {
        // Check basic assumptions
        if(txt != null && (min != null || max != null || ref != null)) {
            // Txt effects don't have numerical values
            throw new IllegalArgumentException("A text effect cannot have numerical/ref values");
        }
        if(ref != null && (min != null || max != null)) {
            // Ref effects don't have max values
            throw new IllegalArgumentException("A ref effect cannot have a max value");
        }

        this.id = id;
        this.min = min;
        this.max = max;
        this.ref = ref;
        this.txt = txt;
    }

    public static Effect buildHex(String[] vals) {
        int id = Integer.parseInt(vals[0], 16);
        Integer min = null;
        Integer max = null;
        Integer ref = null;
        String txt = vals[4].isEmpty()?null:vals[4];

        if(!vals[1].isEmpty()) {
            min = Integer.parseInt(vals[1], 16);
        }

        if(!vals[2].isEmpty()) {
            max = Integer.parseInt(vals[2], 16);
        }

        if(!vals[3].isEmpty()) {
            ref = Integer.parseInt(vals[3], 16);
        }

        return new Effect(id, min, max, ref, txt);
    }

    public String[] toHexVals() {
        return new String[]{
            Integer.toHexString(this.id),
            Integer.toHexString(this.min),
            Integer.toHexString(this.max),
            Integer.toHexString(this.ref),
            this.txt==null?"":this.txt,
        };
    }

    public static List<Effect> parseEffects(String effectSeparator, String valSeparator, String str) {
        String[] parts = str.split(effectSeparator);
        ArrayList<Effect> effects = new ArrayList<>(parts.length);

        for(String part : parts) {
            effects.add(Effect.buildHex(part.split(valSeparator,5)));
        }

        return effects;
    }

    public static String encodeEffects(String effectSeparator, String valSeparator, List<Effect> effects) {
        return effects.stream().map(
                e -> String.join(valSeparator, e.toHexVals())
        ).collect(Collectors.joining(effectSeparator));
    }

    public Effect finalizeMaxValue() {
        // If there is no min or no max, it's not a random effect
        if(this.min == null || this.max == null) return this;

        return new Effect(id, max, null, null, null);
    }

    public Effect finalizeValue() {
        // If there is no min or no max, it's not a random effect
        if(this.min == null || this.max == null) return this;

        int val = Formulas.getRandomValue(min, max);
        return new Effect(id, val, null, null, null);
    }
}
