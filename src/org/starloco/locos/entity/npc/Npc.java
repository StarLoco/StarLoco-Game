package org.starloco.locos.entity.npc;

import org.starloco.locos.client.BasePlayer;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.game.action.ExchangeAction;
import org.starloco.locos.game.action.type.NpcDialogActionData;
import org.starloco.locos.game.world.World;

public class Npc {
    private final int id, templateId;
    private int cellId;
    private byte orientation;

    public Npc(int id, int cellId, byte orientation, int templateId) {
        this.id = id;
        this.templateId = templateId;
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
        return World.world.getNPCTemplate(templateId);
    }

    public void onCreateDialog(BasePlayer player) {
        NpcTemplate template = this.getTemplate();

        NpcDialogActionData data = new NpcDialogActionData(template, -1);
        ExchangeAction<NpcDialogActionData> action = new ExchangeAction<>(ExchangeAction.TALKING_WITH, data);
        player.setExchangeAction(action);

        SocketManager.GAME_SEND_DCK_PACKET(player.getGameClient(), id);
        template.onCreateDialog(player);
    }

    public String encodeGM(boolean alter, BasePlayer p) {
        NpcTemplate template = getTemplate();
        StringBuilder packet = new StringBuilder();
        packet.append((alter ? "~" : "+"));
        packet.append(this.cellId).append(";");
        packet.append(this.orientation).append(";");
        packet.append("0").append(";");
        packet.append(this.id).append(";");
        packet.append(template.getId()).append(";");
        packet.append("-4").append(";");//type = NPC
        packet.append(template.getGfxId()).append("^");

        if (template.getScaleX() == template.getScaleY())
            packet.append(template.getScaleY()).append(";");
        else
            packet.append(template.getScaleX()).append("x").append(template.getScaleY()).append(";");

        packet.append(template.getSex()).append(";");
        packet.append((template.getColor1() != -1 ? Integer.toHexString(template.getColor1()) : "-1")).append(";");
        packet.append((template.getColor2() != -1 ? Integer.toHexString(template.getColor2()) : "-1")).append(";");
        packet.append((template.getColor3() != -1 ? Integer.toHexString(template.getColor3()) : "-1")).append(";");
        packet.append(template.encodeAccessories()).append(";");

        packet.append(template.getExtraClip(p)).append(";");
        packet.append(template.getCustomArtWork());
        return packet.toString();
    }
}
