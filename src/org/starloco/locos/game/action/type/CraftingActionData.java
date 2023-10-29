package org.starloco.locos.game.action.type;

import org.starloco.locos.client.Player;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.script.DataScriptVM;
import org.starloco.locos.script.EventHandlers;

import javax.xml.crypto.Data;
import java.util.HashMap;

public class CraftingActionData implements ActionDataInterface {
    private final Player player;
    private final int skillID;
    private final int maxIngredients;

    private final HashMap<Integer,Integer> ingredients = new HashMap<>();

    private int remainingCrafts = 0;

    public CraftingActionData(Player player, int skillID, int maxIngredients) {
        this.player = player;
        this.skillID = skillID;
        this.maxIngredients = maxIngredients;
    }

    public void modIngredient(int itemGUID, int modQuantity) {
        int quantity = ingredients.getOrDefault(itemGUID, 0) + modQuantity;

        if(quantity<0)
            throw new IllegalStateException("trying to remove more ingredients than available");

        GameObject obj = player.getItems().get(itemGUID);

        if(obj == null || obj.getObvijevanLook() != 0 || obj.getQuantity() < quantity) {
            throw new IllegalStateException("hack attempt");
        }

        if(quantity==0) {
            ingredients.remove(itemGUID);
            SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(player, 'O', "-", itemGUID + "");
            return;
        }

        ingredients.put(itemGUID, quantity);
        SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(player, 'O', "+", itemGUID + "|" + quantity);
    }

    public void craft() {
        if(!DataScriptVM.getInstance().handlers.onCraft(player, skillID, ingredients)) {
            return;
        }
        ingredients.clear();
    }

    public void replayLastCraft() {
        // TODO
    }

    public void setCraftCount(int i) {
        // TODO: Should we check anything there ?
        remainingCrafts = i;
    }
}
