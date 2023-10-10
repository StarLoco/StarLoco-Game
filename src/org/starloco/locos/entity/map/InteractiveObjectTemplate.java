package org.starloco.locos.entity.map;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class InteractiveObjectTemplate {
    private final int id;
    private final Set<Integer> skills = new HashSet<>();
    private final boolean walkable;

    public InteractiveObjectTemplate(int id, Collection<Integer> skills, boolean walkable) {
        this.id = id;
        this.skills.addAll(skills);
        this.walkable = walkable;
    }

    public int getId() {
        return id;
    }
    public boolean allowSkill(int sk) {
        return skills.contains(sk);
    }
    public boolean isWalkable() {
        return walkable;
    }
}