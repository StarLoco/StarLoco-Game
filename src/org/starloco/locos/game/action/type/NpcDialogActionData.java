package org.starloco.locos.game.action.type;

import org.starloco.locos.client.Player;
import org.starloco.locos.entity.npc.Npc;
import org.starloco.locos.entity.npc.NpcTemplate;

public class NpcDialogActionData implements ActionDataInterface {

    private final NpcTemplate npcTemplate;

    private int questionId;

    public NpcDialogActionData(NpcTemplate npcTemplate, int questionId) {
        this.npcTemplate = npcTemplate;
        this.questionId = questionId;
    }

    public NpcTemplate getNpcTemplate() {
        return npcTemplate;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public Npc getNpc(Player player) {
        return player.getCurMap().getNpcByTemplateId(npcTemplate.getId());
    }

    public boolean isValid(Player player, int npcId) {
        return getNpc(player).getId() == npcId;
    }
}
