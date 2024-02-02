package org.starloco.locos.item;

import org.starloco.locos.client.other.Stats;
import org.starloco.locos.game.world.World;

import java.util.Optional;


public interface Item {
    int templateID();
    Optional<Stats> stats();

    default ItemTemplate template() {
        return World.world.getItemTemplate(templateID());
    }
    default ItemType type() {  return template().type;  }
    default int typeID() {  return template().type.clientID; }
    default int level() { return template().level; }
}
