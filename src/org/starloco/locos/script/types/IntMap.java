package org.starloco.locos.script.types;

import org.classdump.luna.Table;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class IntMap extends Table implements Map<String, Integer> {
    private final Map<String,Integer> m = new HashMap<>();

    @Override
    public int size() {
        return m.size();
    }

    @Override
    public boolean isEmpty() {
        return m.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return m.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return m.containsValue(value);
    }

    @Override
    public Integer get(Object key) {
        return m.get(key);
    }

    @Override
    public Integer put(String key, Integer value) {
        return m.put(key, value);
    }

    @Override
    public Integer remove(Object key) {
        return m.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Integer> o) {
        m.putAll(o);
    }

    @Override
    public void clear() {
        m.clear();
    }

    @Override
    public Set<String> keySet() {
        return m.keySet();
    }

    @Override
    public Collection<Integer> values() {
        return m.values();
    }

    @Override
    public Set<Entry<String, Integer>> entrySet() {
        return m.entrySet();
    }

    @Override
    public Object rawget(Object key) {
        return m.get((String)key);
    }

    @Override
    public void rawset(Object key, Object value) {
        m.put((String)key, (Integer)value);
    }

    @Override
    public Object initialKey() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object successorKeyOf(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void setMode(boolean weakKeys, boolean weakValues) {
        throw new UnsupportedOperationException();
    }
}
