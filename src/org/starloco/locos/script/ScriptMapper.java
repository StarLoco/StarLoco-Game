package org.starloco.locos.script;

public interface ScriptMapper<T> {
    T from(Object o);
    Object to(T v);
}
