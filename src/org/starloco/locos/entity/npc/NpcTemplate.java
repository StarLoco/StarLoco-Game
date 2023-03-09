package org.starloco.locos.entity.npc;

import org.starloco.locos.area.map.entity.InteractiveDoor;
import org.starloco.locos.game.world.World;
import org.starloco.locos.game.world.World.Couple;
import org.starloco.locos.object.ObjectTemplate;
import org.starloco.locos.quest.Quest;

import java.util.*;
import java.util.stream.Collectors;

public class NpcTemplate {
    private int id, bonus, gfxId, scaleX, scaleY, sex, color1, color2, color3;
    private String accessories;
    private int extraClip, customArtWork;
    private String path;
    private Quest quest;
    private byte informations;

    private Map<Integer, Integer> initQuestions = new HashMap<>();
    private ArrayList<ObjectTemplate> sales = new ArrayList<>();
    private List<Couple<ArrayList<Couple<Integer, Integer>>, ArrayList<Couple<Integer, Integer>>>> exchanges;

    public NpcTemplate(int id, int bonus, int gfxId, int scaleX, int scaleY, int sex, int color1, int color2, int color3, String accessories, int extraClip, int customArtWork, String questions,
                       String sales, String quest, String exchanges, String path, byte informations) {
        this.id = id;
        this.bonus = bonus;
        this.gfxId = gfxId;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.sex = sex;
        this.color1 = color1;
        this.color2 = color2;
        this.color3 = color3;
        this.accessories = accessories;
        this.extraClip = extraClip;
        this.customArtWork = customArtWork;
        this.path = path;
        this.informations = informations;

        if (!quest.equalsIgnoreCase(""))
            this.quest = Quest.getQuestById(Integer.parseInt(quest));

        if (questions.split("\\|").length > 1) {
            for (String question : questions.split("\\|")) {
                try {
                    initQuestions.put(Integer.parseInt(question.split(",")[0]), Integer.parseInt(question.split(",")[1]));
                } catch (Exception e) {
                    e.printStackTrace();
                    World.world.logger.error("#1# Erreur sur une question id sur le PNJ d'id : " + id);
                }
            }
        } else {
            if (questions.equalsIgnoreCase("")) this.initQuestions.put(-1, -1);
            else this.initQuestions.put(-1, Integer.parseInt(questions));
        }

        if (!sales.equals("")) {
            for (String obj : sales.split(",")) {
                try {
                    int money = -1;
                    if(obj.contains(";")){
                        String[] temp = obj.split(";");
                        money = Integer.parseInt(temp[0]);
                        obj = temp[1];
                    }

                    ObjectTemplate template = World.world.getObjTemplate(Integer.parseInt(obj));

                    if (template != null){
                        this.sales.add(template);
                        template.setMoney(money);
                    }

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    World.world.logger.error("#2# Erreur sur un item en vente sur le PNJ d'id : " + id);
                }
            }
        }

        if(!exchanges.equals("")) {
            try	{
                this.exchanges = new ArrayList<>();
                for(String data : exchanges.split("\\~")) {
                    ArrayList<Couple<Integer, Integer>> gives = new ArrayList<>(), gets = new ArrayList<>();

                    String[] split = data.split("\\|");
                    String give = split[1], get = split[0];

                    for(String obj : give.split("\\,")) {
                        split = obj.split("\\:");
                        gives.add(new Couple<>(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
                    }

                    for(String obj : get.split("\\,")) {
                        split = obj.split("\\:");
                        gets.add(new Couple<>(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
                    }
                    this.exchanges.add(new Couple<>(gets, gives));
                }
            } catch(Exception e) {
                e.printStackTrace();
                World.world.logger.error("#3# Erreur sur l'exchanges sur le PNJ d'id : " + id);
            }
        }
    }

    public void setInfos(int id, int bonusValue, int gfxID, int scaleX, int scaleY, int sex, int color1, int color2, int color3, String access, int extraClip, int customArtWork, String questions, String ventes, String quests, String exchanges, String path, byte informations) {
        this.id = id;
        this.bonus = bonusValue;
        this.gfxId = gfxID;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.sex = sex;
        this.color1 = color1;
        this.color2 = color2;
        this.color3 = color3;
        this.accessories = access;
        this.extraClip = extraClip;
        this.customArtWork = customArtWork;
        this.path = path;
        this.sales.clear();
        this.informations = informations;
        this.initQuestions.clear();

        if (quests.equalsIgnoreCase("")) this.quest = null;
        else this.quest = Quest.getQuestById(Integer.parseInt(quests));

        if (questions.split("\\|").length > 1) {
            for (String question : questions.split("\\|")) {
                try {
                    this.initQuestions.put(Integer.parseInt(question.split(",")[0]), Integer.parseInt(question.split(",")[1]));
                } catch (Exception e) {
                    e.printStackTrace();
                    World.world.logger.error("#2# Erreur sur une question id sur le PNJ d'id : " + this.id);
                }
            }
        } else {
            this.initQuestions.put(-1, Integer.parseInt(questions));
        }

        if (!ventes.equals("")) {
            for (String obj : ventes.split(",")) {
                try {
                    ObjectTemplate template = World.world.getObjTemplate(Integer.parseInt(obj));
                    if (template != null)
                        this.sales.add(template);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    World.world.logger.error("#2# Erreur sur un item en vente sur le PNJ d'id : " + id);
                }
            }
        }

        if(!exchanges.equals("")) {
            try	{
                this.exchanges = new ArrayList<>();
                for(String data : exchanges.split("~")) {
                    String[] split = data.split("\\|");
                    String give = split[1], get = split[0];
                    ArrayList<Couple<Integer, Integer>> gives = new ArrayList<>(), gets = new ArrayList<>();

                    for(String obj : give.split(",")) {
                        split = obj.split(":");
                        gives.add(new Couple<>(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
                    }

                    for(String obj : get.split(",")) {
                        split = obj.split(":");
                        gets.add(new Couple<>(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
                    }
                    this.exchanges.add(new Couple<>(gets, gives));
                }
            } catch(Exception e) {
                e.printStackTrace();
                World.world.logger.error("#3# Erreur sur l'exchanges sur le PNJ d'id : " + id);
            }
        }
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

    public String getAccessories() {
        return accessories;
    }

    public int getExtraClip() {
        return extraClip;
    }

    public void setExtraClip(int extraClip) {
        this.extraClip = extraClip;
    }

    public int getCustomArtWork() {
        return customArtWork;
    }

    public String getPath() {
        return path;
    }

    public Quest getQuest() {
        return quest;
    }

    public void setQuest(Quest quest) {
        this.quest = quest;
    }

    public byte getInformations() {
        return informations;
    }

    public int getInitQuestionId(int id) {
        if (this.initQuestions.get(id) == null)
            for (Integer entry : this.initQuestions.values())
                return entry;
        return this.initQuestions.get(id);
    }

    public String getItemVendorList() {
        StringBuilder items = new StringBuilder();
        if (this.sales.isEmpty()) return "";
        for (ObjectTemplate obj : this.sales)
            items.append(obj.parseItemTemplateStats()).append("|");
        return items.toString();
    }

    public ArrayList<ObjectTemplate> getAllItem() {
        return sales;
    }

    public boolean addItemVendor(ObjectTemplate template) {
        if (this.sales.contains(template))
            return false;
        this.sales.add(template);
        return true;
    }

    public boolean removeItemVendor(int id) {
        Iterator<ObjectTemplate> iterator = this.sales.iterator();

        while(iterator.hasNext()) {
            ObjectTemplate template = iterator.next();
            if(template.getId() == id) iterator.remove();
        }

        return true;
    }

    public boolean haveItem(int id) {
        for (ObjectTemplate template : sales)
            if (template.getId() == id)
                return true;
        return false;
    }

    public ArrayList<Couple<Integer,Integer>> checkGetObjects(ArrayList<Couple<Integer,Integer>> objects) {
        if(this.exchanges == null) return null;
        boolean ok;
        int multiple = 0, newMultiple = 0;

        for(Couple<ArrayList<Couple<Integer, Integer>>, ArrayList<Couple<Integer, Integer>>> entry0 : this.exchanges) {
            ok = true;
            for(Couple<Integer, Integer> entry1 : entry0.first) {
                boolean ok1 = false;

                for(Couple<Integer, Integer> entry2 : objects) {
                    if (entry1.first == World.world.getGameObject(entry2.first).getTemplate().getId() && (int) (entry2.second) % entry1.second == 0) {
                        ok1 = true;
                        newMultiple = entry2.second / entry1.second;

                        if(multiple == 0 || newMultiple == multiple) {
                            multiple = newMultiple;
                        } else {
                            ok1 = false;
                        }
                    }
                }

                if(!ok1) {
                    ok = false;
                    break;
                }
            }

            final int fMultiple = multiple;

            if(ok && objects.size() == entry0.first.size()) {
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
