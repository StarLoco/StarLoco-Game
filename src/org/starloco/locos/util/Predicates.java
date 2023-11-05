package org.starloco.locos.util;

import java.util.function.Predicate;

public class Predicates {
    public static <T> Predicate<T> not(Predicate<T> p) {
        return p.negate();
    }
}
