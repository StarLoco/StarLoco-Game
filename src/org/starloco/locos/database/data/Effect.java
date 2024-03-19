package org.starloco.locos.database.data;

public record Effect(int id, int val1, int val2, int val3) {
    public Effect {
        if(id <= 0) throw new IllegalArgumentException("effectID must be >0");
    }
}
