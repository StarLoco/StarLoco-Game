package org.starloco.locos.util;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;

/**
 * Created by Locos on 13/06/2018.
 */
public class Pair<K, V> implements Serializable {
    public final K first;
    public final V second;

    public K getFirst() {
        return this.first;
    }

    public V getSecond() {
        return this.second;
    }

    public Pair(K first, V second) {
        this.first = first;
        this.second = second;
    }

    public String toString() {
        return this.first + "/" + this.second;
    }
    public String toString(String sep) {
        return this.first + sep + this.second;
    }

    public int hashCode() {
        return this.first.hashCode() * 13 + (this.second == null ? 0 : this.second.hashCode());
    }

    public boolean equals(Object var1) {
        if (this == var1)  return true;
        if (!(var1 instanceof Pair))   return false;

        Pair var2 = (Pair)var1;

        if(!Objects.equals(this.first, var2.first))  return false;

        return Objects.equals(this.second, var2.second);
    }

    public <OK,OV> Pair<OK,OV> map(Function<K,OK> km, Function<V,OV> vm) {
        return new Pair<>(km.apply(first), vm.apply(second));
    }
}
