package org.starloco.locos.client.other;

import java.util.HashMap;
import java.util.Map;

public class Restriction {

    private static Map<Integer, Restriction> restrictions = new HashMap<>();
    //region
    public Map<String, Long> aggros = new HashMap<>();
    public boolean command = true;

    public static Restriction get(int id) {
        if (restrictions.get(id) != null)
            return restrictions.get(id);
        return new Restriction();
    }

    //endregion
}