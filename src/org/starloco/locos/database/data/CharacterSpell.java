package org.starloco.locos.database.data;

import java.util.HashMap;
import java.util.Map;

public class CharacterSpell {
    public int availablePoints = 0;
    public int spendPoints = 0;
    public final Map<Long, Integer> levels = new HashMap<>();
}
