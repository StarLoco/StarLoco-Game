package org.starloco.locos.game.action.type;

import org.starloco.locos.client.BasePlayer;
import org.starloco.locos.entity.npc.Npc;
import org.starloco.locos.entity.npc.NpcTemplate;

import java.util.ArrayList;
import java.util.List;

public class NpcDialogActionData implements ActionDataInterface {

    private final NpcTemplate npcTemplate;

    private int questionId;

    private List<Integer> answers = new ArrayList<>();

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

    public boolean hasAnswer(int answer) {
        return answers.contains(answer);
    }

    public void setAnswers(List<Integer> answers) {
        this.answers = answers;
    }

    public Npc getNpc(BasePlayer player) {
        return player.getCurMap().getNpcByTemplateId(npcTemplate.getId());
    }

    public boolean isValid(BasePlayer player, int npcId) {
        return getNpc(player).getId() == npcId;
    }
}
