package org.starloco.locos.entity.npc;

import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.client.Player;
import org.starloco.locos.quest.Quest;
import org.starloco.locos.quest.QuestPlayer;

public class Npc {
    private int id, cellId;
    private byte orientation;
    private NpcTemplate template;

    public Npc(int id, int cellId, byte orientation, NpcTemplate template) {
        this.id = id;
        this.cellId = cellId;
        this.orientation = orientation;
        this.template = template;
    }

    public int getId() {
        return id;
    }

    public int getCellId() {
        return cellId;
    }

    public void setCellId(int cellId) {
        this.cellId = cellId;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(byte orientation) {
        this.orientation = orientation;
    }

    public NpcTemplate getTemplate() {
        return this.template;
    }

    public void talk(GameMap map, String key, String... msg) {
        map.getPlayers().forEach((p) -> p.send("cMK|-1|Jacquie|" + p.getLang().trans(key) + (msg.length > 0 ? msg[0] : "") + "|"));
    }

    public String parse(boolean alter, Player p) {
        StringBuilder sock = new StringBuilder();
        sock.append((alter ? "~" : "+"));
        sock.append(this.cellId).append(";");
        sock.append(this.orientation).append(";");
        sock.append("0").append(";");
        sock.append(this.id).append(";");
        sock.append(this.template.getId()).append(";");
        sock.append("-4").append(";");//type = NPC
        sock.append(this.template.getGfxId()).append("^");

        if (this.template.getScaleX() == this.template.getScaleY())
            sock.append(this.template.getScaleY()).append(";");
        else
            sock.append(this.template.getScaleX()).append("x").append(this.template.getScaleY()).append(";");

        sock.append(this.template.getSex()).append(";");
        sock.append((this.template.getColor1() != -1 ? Integer.toHexString(this.template.getColor1()) : "-1")).append(";");
        sock.append((this.template.getColor2() != -1 ? Integer.toHexString(this.template.getColor2()) : "-1")).append(";");
        sock.append((this.template.getColor3() != -1 ? Integer.toHexString(this.template.getColor3()) : "-1")).append(";");
        sock.append(this.template.getAccessories()).append(";");

        Quest q = this.template.getQuest();
        QuestPlayer questPlayer = q == null ? null : p.getQuestPersoByQuest(q);

        if (q == null)
            sock.append(-1).append(";");
        else if (questPlayer == null)
            sock.append(4).append(";");
        else
            sock.append(-1).append(";");

        sock.append(this.template.getCustomArtWork());
        return sock.toString();
    }
}