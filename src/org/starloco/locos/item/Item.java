package org.starloco.locos.item;

import org.starloco.locos.game.world.World;


public interface Item {
    int templateID();
    default ItemTemplate template() {
        return World.world.getItemTemplate(templateID());
    }

    default ItemType type() {  return template().type;  }
    default int typeID() {  return template().type.clientID; }

    default int level() { return template().level; }

}
