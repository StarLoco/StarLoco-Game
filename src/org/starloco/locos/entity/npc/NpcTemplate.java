package org.starloco.locos.entity.npc;

import org.classdump.luna.Table;
import org.starloco.locos.client.Player;
import org.starloco.locos.database.data.game.SaleOffer;
import org.starloco.locos.database.data.game.SaleOffer.Currency;
import org.starloco.locos.game.world.World;
import org.starloco.locos.object.ObjectTemplate;
import org.starloco.locos.quest.Quest;
import org.starloco.locos.script.NpcScriptVM;

import java.util.*;
import java.util.stream.Collectors;

import static org.starloco.locos.script.ScriptVM.*;

public class NpcTemplate {
    private final int id, gfxId, scaleX, scaleY, sex, color1, color2, color3;
    private final int[] accessories;
    private final int customArtWork;
    // private Quest quest;
    private final Table scriptVal;
    private final byte flags;

    private final LegacyData legacy;

    public NpcTemplate(int id, int bonus, int gfxId, int scaleX, int scaleY, int sex, int color1, int color2, int color3, String accessories, int extraClip, int customArtWork, String questions,
                       String sales, String quest, String exchanges, String path, byte flags) {
        this.scriptVal = null;
        this.legacy = new LegacyData(id, sales, quest, exchanges, path);
        this.id = id;
        this.gfxId = gfxId;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.sex = sex;
        this.color1 = color1;
        this.color2 = color2;
        this.color3 = color3;
        this.accessories = Arrays.stream(accessories.split(",")).mapToInt(Integer::parseInt).toArray();
        this.customArtWork = customArtWork;
        this.flags = flags;
    }

    public NpcTemplate(Table val) {
        this.scriptVal = val;
        this.legacy = null;

        this.id = rawInt(val, "id");
        this.gfxId = rawInt(val, "gfxID");
        this.sex = rawInt(val, "gender");
        this.scaleX = rawInt(val, "scaleX");
        this.scaleY = rawInt(val, "scaleY");
        Table colors = (Table)val.rawget("colors");
        this.color1 = rawInt(colors,1);
        this.color2 = rawInt(colors,2);
        this.color3 = rawInt(colors,3);
        Table accessories = (Table)val.rawget("accessories");
        this.accessories = intArrayFromLuaTable(accessories);
        this.customArtWork = rawInt(val,"customArtwork");
        this.flags = (byte) rawInt(val, "flags");
    }


    public int getId() {
        return id;
    }

    public int getGfxId() {
        return gfxId;
    }

    public int getScaleX() {
        return scaleX;
    }

    public int getScaleY() {
        return scaleY;
    }

    public int getSex() {
        return sex;
    }

    public int getColor1() {
        return color1;
    }

    public int getColor2() {
        return color2;
    }

    public int getColor3() {
        return color3;
    }

    public String encodeAccessories() {
        return Arrays.stream(accessories).mapToObj(Integer::toHexString).collect(Collectors.joining(","));
    }

    public int getCustomArtWork() {
        return customArtWork;
    }

    public Quest getQuest() {
        return null; // FIXME Diabu
        //return quest;
    }

    public boolean isBankClerk() {
        switch(id) {
            case 100:
            case 520:
            case 522:
            case 691:
            case 692:
                return true;
            default:
                return false;
        }
    }

    public void onCreateDialog(Player player) { this.onDialog(player, 0);}
    public void onDialog(Player player, int response) {
        if(scriptVal == null) {
            // TODO: fallback to legacy system
            return;
        }
        Object onTalk = recursiveGet(scriptVal,"onTalk");
        if(onTalk == null) return;
        NpcScriptVM.getInstance().call(onTalk, scriptVal, player.Scripted(), response);
    }
    public List<SaleOffer> salesList(Player player) {
        if(scriptVal == null) {
            return legacy.sales;
        }
        Object salesList = recursiveGet(scriptVal,"salesList");
        if(salesList == null) return Collections.emptyList();

        Object[] ret = NpcScriptVM.getInstance().call(salesList, scriptVal, player.Scripted());

        if(ret == null || ret.length == 0) return Collections.emptyList();
        List<Object> offers = fromLuaTable((Table) ret[0]);

        return offers.stream().map(o -> {
            Table t = (Table) o;
            int itemID = rawInt(t,"item");
            ObjectTemplate item = World.world.getObjTemplate(itemID);
            if(item==null) throw new IllegalArgumentException(String.format("unknown item template #%d", itemID));

            int price = rawOptionalInt(t, "price", item.getPrice());
            int currencyID = rawOptionalInt(t, "currency", 0);

            // Not specified defaults to kamas
            if(currencyID==0) currencyID = -1; // currency -1 -> Kamas

            Currency currency;
            if(currencyID<0) currency = Currency.nonItemCurrency(-currencyID);
            else currency = Currency.itemCurrency(currencyID);

            return new SaleOffer(item, price, currency);
        }).collect(Collectors.toList());
    }

    public byte getFlags() {
        return flags;
    }

    private static class LegacyData {
        final List<SaleOffer> sales = new ArrayList<>();

        public LegacyData(int npcID, String sales, String quest, String exchanges, String path) {
            if (!sales.equals("")) {
                for (String obj : sales.split(",")) {
                    try {
                        ObjectTemplate template = World.world.getObjTemplate(Integer.parseInt(obj));
                        if (template != null)
                            this.sales.add(new SaleOffer(template, template.getPrice()));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        World.world.logger.error("#2# Erreur sur un item en vente sur le PNJ d'id : " + npcID);
                    }
                }
            }
        }
    }
}
