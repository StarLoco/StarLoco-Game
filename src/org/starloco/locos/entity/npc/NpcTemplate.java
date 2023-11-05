package org.starloco.locos.entity.npc;

import org.classdump.luna.Conversions;
import org.classdump.luna.Table;
import org.classdump.luna.impl.DefaultTable;
import org.starloco.locos.client.Player;
import org.starloco.locos.database.data.game.SaleOffer;
import org.starloco.locos.database.data.game.SaleOffer.Currency;
import org.starloco.locos.game.world.World;
import org.starloco.locos.game.world.World.Couple;
import org.starloco.locos.object.ObjectTemplate;
import org.starloco.locos.script.DataScriptVM;
import org.starloco.locos.script.ScriptVM;

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

    public final LegacyData legacy;

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

    public void onCreateDialog(Player player) {
        this.onDialog(player,  0,0);
    }
    public void onDialog(Player player, int question, int response) {
        Objects.requireNonNull(scriptVal);
        DataScriptVM.getInstance().handlers.onDialog(player, this.id, response);
    }

    public List<SaleOffer> salesList(Player player) {
        if(scriptVal == null) {
            return legacy.sales;
        }
        Object salesList = recursiveGet(scriptVal,"salesList");
        if(salesList == null) return Collections.emptyList();

        Object[] ret = DataScriptVM.getInstance().call(salesList, scriptVal, player.scripted());

        if(ret == null || ret.length == 0) return Collections.emptyList();
        List<Object> offers = listFromLuaTable((Table) ret[0]);

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

    public int getExtraClip(Player player) {
        if(this.legacy!=null) {
            return -1;
        }

        Object extraClip = recursiveGet(scriptVal,"extraClip");
        if(extraClip == null) return -1;

        Object[] ret = DataScriptVM.getInstance().call(extraClip, scriptVal, player.scripted());
        if(ret == null || ret.length == 0 || ret[0] == null) return -1;
        if(ret.length > 1) throw new RuntimeException(String.format("unexpected count(%d) in extraClip", ret.length));

        return Conversions.integerValueOf(ret[0]).intValue();
    }

    public Couple<Integer,Integer> barterOutcome(Player player, List<Couple<Integer,Integer>> objects) {
        if(this.legacy != null) {
            List<Couple<Integer,Integer>> out = this.legacy.checkGetObjects(objects);
            if(out.size() != 1) throw new RuntimeException(String.format("unexpected count(%d) in legacy barterOutcome", out.size()));
            return out.get(0);
        }

        Object barterOutcome = recursiveGet(scriptVal,"barterOutcome");
        if(barterOutcome == null) return null;


        DefaultTable offer = new DefaultTable();
        for(int i=0;i<objects.size();i++){
            Table stack = ScriptVM.ItemStack(objects.get(i));
            offer.rawset(i+1, stack);
        }

        Object[] ret = DataScriptVM.getInstance().call(barterOutcome, scriptVal, player.scripted(), offer);
        if(ret == null || ret.length == 0 || ret[0] == null) return null;
        if(ret.length > 1) throw new RuntimeException(String.format("unexpected count(%d) in barterOutcome", ret.length));

        return ScriptVM.ItemStackFromLua((Table) ret[0]);
    }

    public static class LegacyData {
        private final String path;
        private final Map<Integer, Integer> initQuestions = new HashMap<>();
        private final List<SaleOffer> sales = new ArrayList<>();
        private List<Couple<ArrayList<Couple<Integer, Integer>>, ArrayList<Couple<Integer, Integer>>>> exchanges;

        public LegacyData(int npcID, String questions, String sales, String exchanges, String path) {
            this.path = path;

            if (questions.split("\\|").length > 1) {
                for (String question : questions.split("\\|")) {
                    try {
                        initQuestions.put(Integer.parseInt(question.split(",")[0]), Integer.parseInt(question.split(",")[1]));
                    } catch (Exception e) {
                        e.printStackTrace();
                        World.world.logger.error("#1# Erreur sur une question id sur le PNJ d'id : " + npcID);
                    }
                }
            } else {
                if (questions.equalsIgnoreCase("")) this.initQuestions.put(-1, -1);
                else this.initQuestions.put(-1, Integer.parseInt(questions));
            }

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

            if (!exchanges.equals("")) {
                try {
                    this.exchanges = new ArrayList<>();
                    for (String data : exchanges.split("\\~")) {
                        ArrayList<Couple<Integer, Integer>> gives = new ArrayList<>(), gets = new ArrayList<>();

                        String[] split = data.split("\\|");
                        String give = split[1], get = split[0];

                        for (String obj : give.split("\\,")) {
                            split = obj.split("\\:");
                            gives.add(new Couple<>(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
                        }

                        for (String obj : get.split("\\,")) {
                            split = obj.split("\\:");
                            gets.add(new Couple<>(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
                        }
                        this.exchanges.add(new Couple<>(gets, gives));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    World.world.logger.error("#3# Erreur sur l'exchanges sur le PNJ d'id : " + npcID);
                }
            }
        }

        public int getInitQuestionId(int id) {
            if (initQuestions.get(id) == null) {
                // Just return the 1st one
                for (Integer entry : initQuestions.values()) return entry;
            }
            return initQuestions.get(id);
        }

        public String getPath() {
            return path;
        }

        public List<ObjectTemplate> getAllItem() {
            return sales.stream().map(o -> o.itemTemplate).collect(Collectors.toList());
        }

        public boolean addItemVendor(ObjectTemplate template) {
            if (sales.stream().anyMatch(o -> o.itemTemplate == template))
                return false;
            sales.add(new SaleOffer(template));
            return true;
        }

        public boolean haveItem(int id) {
            return sales.stream().anyMatch(o -> o.itemTemplate.getId() == id);
        }


        public ArrayList<Couple<Integer, Integer>> checkGetObjects(List<Couple<Integer, Integer>> objects) {
            if (this.exchanges == null) return null;
            boolean ok;
            int multiple = 0, newMultiple = 0;

            for (Couple<ArrayList<Couple<Integer, Integer>>, ArrayList<Couple<Integer, Integer>>> entry0 : this.exchanges) {
                ok = true;
                for (Couple<Integer, Integer> entry1 : entry0.first) {
                    boolean ok1 = false;

                    for (Couple<Integer, Integer> entry2 : objects) {
                        if (Objects.equals(entry1.first, entry2.first) && (int) (entry2.second) % entry1.second == 0) {
                            ok1 = true;
                            newMultiple = entry2.second / entry1.second;

                            if (multiple == 0 || newMultiple == multiple) {
                                multiple = newMultiple;
                            } else {
                                ok1 = false;
                            }
                        }
                    }

                    if (!ok1) {
                        ok = false;
                        break;
                    }
                }

                final int fMultiple = multiple;

                if (ok && objects.size() == entry0.first.size()) {
                    if (fMultiple != 1) {
                        return entry0.second.stream().map(give -> new Couple<>(give.first, give.second * fMultiple))
                                .collect(Collectors.toCollection(ArrayList::new));
                    } else {
                        return entry0.second;
                    }
                } else {
                    multiple = newMultiple = 0;
                }
            }
            return null;
        }
    }
}
