package org.starloco.locos.entity.npc;

import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.client.Player;
import org.starloco.locos.game.world.World;
import org.starloco.locos.quest.Quest;
import org.starloco.locos.quest.QuestPlayer;

public class Npc {
    private int id, templateID,cellId;
    private byte orientation;

    public Npc(int id, int cellId, byte orientation, int templateID) {
        this.id = id;
        this.templateID = templateID;
        this.cellId = cellId;
        this.orientation = orientation;
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
        return World.world.getNPCTemplate(templateID);
    }

    public void talk(GameMap map, String key, String... msg) {
        map.getPlayers().forEach((p) -> p.send("cMK|-1|Jacquie|" + p.getLang().trans(key) + (msg.length > 0 ? msg[0] : "") + "|"));
    }

    public String encodeGM(boolean alter, Player p) {
        NpcTemplate template = getTemplate();
        StringBuilder sock = new StringBuilder();
        sock.append((alter ? "~" : "+"));
        sock.append(this.cellId).append(";");
        sock.append(this.orientation).append(";");
        sock.append("0").append(";");
        sock.append(this.id).append(";");
        sock.append(template.getId()).append(";");
        sock.append("-4").append(";");//type = NPC
        sock.append(template.getGfxId()).append("^");

        if (template.getScaleX() == template.getScaleY())
            sock.append(template.getScaleY()).append(";");
        else
            sock.append(template.getScaleX()).append("x").append(template.getScaleY()).append(";");

        sock.append(template.getSex()).append(";");
        sock.append((template.getColor1() != -1 ? Integer.toHexString(template.getColor1()) : "-1")).append(";");
        sock.append((template.getColor2() != -1 ? Integer.toHexString(template.getColor2()) : "-1")).append(";");
        sock.append((template.getColor3() != -1 ? Integer.toHexString(template.getColor3()) : "-1")).append(";");
        sock.append(template.encodeAccessories()).append(";");

        sock.append(template.getExtraClip(p)).append(";");
        sock.append(template.getCustomArtWork());
        return sock.toString();
    }
}
