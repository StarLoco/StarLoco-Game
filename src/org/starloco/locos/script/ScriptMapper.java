package org.starloco.locos.script;

public interface ScriptMapper<T> {
    T from(Class<T> c, Object o);
    Object to(T v);
}
