package org.starloco.locos.game.action.type;

import org.starloco.locos.client.Player;
import org.starloco.locos.script.DataScriptVM;

public class DocumentActionData implements ActionDataInterface {
    private final int id;

    public DocumentActionData(int id) {
        this.id = id;
    }

    public void onQuestHRef(Player player, int questID) {
        player.setExchangeAction(null);
        DataScriptVM.getInstance().handlers.onDocQuestHref(player, id, questID);
    }


}
