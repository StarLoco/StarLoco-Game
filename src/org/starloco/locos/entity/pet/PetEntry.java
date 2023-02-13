package org.starloco.locos.entity.pet;

import org.starloco.locos.client.Player;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.login.ObjectData;
import org.starloco.locos.database.data.login.PetData;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.object.GameObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

public class PetEntry {

    private final byte RATIO_FEED = 1;//3 official

    private int objectId;
    private int template;
    private long lastEatDate;
    private int quaEat;
    private int pdv;
    private int Poids;
    private int corpulence;
    private boolean isEupeoh;

    public PetEntry(int Oid, int template, long lastEatDate, int quaEat,
                    int pdv, int corpulence, boolean isEPO) {
        this.objectId = Oid;
        this.template = template;
        this.lastEatDate = lastEatDate;
        this.quaEat = quaEat;
        this.pdv = pdv;
        this.corpulence = corpulence;
        getCurrentStatsPoids();
        this.isEupeoh = isEPO;
    }

    public int getObjectId() {
        return this.objectId;
    }

    public int getTemplate() {
        return template;
    }

    public long getLastEatDate() {
        return this.lastEatDate;
    }

    public int getQuaEat() {
        return this.quaEat;
    }

    public int getPdv() {
        return this.pdv;
    }

    public int getCorpulence() {
        return this.corpulence;
    }

    public boolean getIsEupeoh() {
        return this.isEupeoh;
    }

    public String parseLastEatDate() {
        String hexDate = "#";
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = formatter.format(this.lastEatDate);

        String[] split = date.split("\\s");

        String[] split0 = split[0].split("-");
        hexDate += Integer.toHexString(Integer.parseInt(split0[0])) + "#";
        int mois = Integer.parseInt(split0[1]) - 1;
        int jour = Integer.parseInt(split0[2]);
        hexDate += Integer.toHexString(Integer.parseInt((mois < 10 ? "0" + mois : mois)
                + "" + (jour < 10 ? "0" + jour : jour)))
                + "#";

        String[] split1 = split[1].split(":");
        String heure = split1[0] + split1[1];
        hexDate += Integer.toHexString(Integer.parseInt(heure));

        return hexDate;
    }

    public int parseCorpulence() {
        if (corpulence > 0 || corpulence < 0)
            return 7;
        return 0;
    }

    public int getCurrentStatsPoids() {
        /*
		 * d6,d5,d4,d3,d2 = 4U de poids 8a = 2U de poids 7c = 2U de poids POUR
		 * PETIT WABBIT = 3U de poids b2 = 8U de poids 70 = 8U de poids le reste
		 * a 1U de poids
		 */
        GameObject obj = World.world.getGameObject(this.objectId);
        if (obj == null)
            return 0;
        int cumul = 0;
        for (Entry<Integer, Integer> entry : obj.getStats().getEffects().entrySet()) {
            if (entry.getKey() == Integer.parseInt("320", 16)) // Vita du familier
            {
            }
            else if (entry.getKey() == Integer.parseInt("326", 16)) // Poids du familier
            {
            }
            else if (entry.getKey() == Integer.parseInt("328", 16)) // Date du familier
            {
            }
            else if (entry.getKey() == Integer.parseInt("8a", 16)) // %dom
                cumul = cumul + (2 * entry.getValue());
            else if (entry.getKey() == Integer.parseInt("7c", 16)) // sagesse
                cumul = cumul + (3 * entry.getValue());
            else if (entry.getKey() == Integer.parseInt("d2", 16)
                    || entry.getKey() == Integer.parseInt("d3", 16)
                    || entry.getKey() == Integer.parseInt("d4", 16)
                    || entry.getKey() == Integer.parseInt("d5", 16)
                    || entry.getKey() == Integer.parseInt("d6", 16)) // %resist
                cumul = cumul + (4 * entry.getValue());
            else if (entry.getKey() == Integer.parseInt("b2", 16)
                    || entry.getKey() == Integer.parseInt("70", 16)) // soin et dommages
                cumul = cumul + (8 * entry.getValue());
            else
                cumul = cumul + (entry.getValue());

        }
        this.Poids = cumul;
        return this.Poids;
    }

    public int getMaxStat() {
        return World.world.getPets(this.template).getMax();
    }

    public void looseFight(Player player) {
        GameObject obj = World.world.getGameObject(this.objectId);
        if (obj == null)
            return;
        Pet pets = World.world.getPets(obj.getTemplate().getId());
        if (pets == null)
            return;

        this.pdv--;
        obj.getTxtStat().remove(Constant.STATS_PETS_PDV);
        obj.getTxtStat().put(Constant.STATS_PETS_PDV, Integer.toHexString((this.pdv > 0 ? (this.pdv) : 0)));

        if (this.pdv <= 0) {
            this.pdv = 0;
            obj.getTxtStat().remove(Constant.STATS_PETS_PDV);
            obj.getTxtStat().put(Constant.STATS_PETS_PDV, Integer.toHexString(0));//Mise a 0 des pdv

            if (pets.getDeadTemplate() == 0 || World.world.getObjTemplate(pets.getDeadTemplate()) == null)// Si Pets DeadTemplate = 0 remove de l'item et pet entry
            {
                World.world.removeGameObject(obj.getGuid());
                player.removeItem(obj.getGuid());
                SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, obj.getGuid());
                if (player.addObjet(obj, true))//Si le joueur n'avait pas d'item similaire
                    World.world.addGameObject(obj);
            } else {
                obj.setTemplate(pets.getDeadTemplate());
                if (obj.getPosition() == Constant.ITEM_POS_FAMILIER) {
                    obj.setPosition(Constant.ITEM_POS_NO_EQUIPED);
                    SocketManager.GAME_SEND_OBJET_MOVE_PACKET(player, obj);
                }
            }
            SocketManager.GAME_SEND_Im_PACKET(player, "154");
        }
        SocketManager.GAME_SEND_UPDATE_OBJECT_DISPLAY_PACKET(player, obj);
        ((PetData) DatabaseManager.get(PetData.class)).update(this);
    }

    public void eat(Player p, int min, int max, int statsID, GameObject feed) {
        GameObject obj = World.world.getGameObject(this.objectId);
        if (obj == null)
            return;
        Pet pets = World.world.getPets(obj.getTemplate().getId());
        if (pets == null)
            return;

        if (this.corpulence <= 0)//Si il est maigrichon (X repas rat�s) on peu le nourrir plusieurs fois
        {
            //Update du petsEntry
            this.lastEatDate = System.currentTimeMillis();
            this.corpulence++;
            this.quaEat++;
            //Update de l'item
            obj.getTxtStat().remove(Constant.STATS_PETS_POIDS);
            obj.getTxtStat().put(Constant.STATS_PETS_POIDS, Integer.toString(this.corpulence));
            SocketManager.GAME_SEND_Im_PACKET(p, "029");
            if (this.quaEat >= RATIO_FEED) {//3 normalement
                //Update de l'item
                if ((this.getIsEupeoh() ? pets.getMax() * 1.1 : pets.getMax()) > this.getCurrentStatsPoids())//Si il est sous l'emprise d'EPO on augmente de +10% le jet maximum
                {
                    if (obj.getStats().getEffects().containsKey(statsID)) {
                        int value = obj.getStats().getEffects().get(statsID)
                                + World.world.getPets(World.world.getGameObject(this.objectId).getTemplate().getId()).getGain();
                        if (value > this.getMaxStat())
                            value = this.getMaxStat();
                        obj.getStats().getEffects().remove(statsID);
                        obj.getStats().addOneStat(statsID, value);
                    } else
                        obj.getStats().addOneStat(statsID, pets.getGain());
                }
                this.quaEat = 0;
            }
        } else if (((this.lastEatDate + (min * 3600000)) > System.currentTimeMillis())
                && this.corpulence >= 0)//Si il n'est pas maigrichon, et on le nourri trop rapidement
        {
            //Update du petsEntry
            this.lastEatDate = System.currentTimeMillis();
            this.corpulence++;
            //Update de l'item
            obj.getTxtStat().remove(Constant.STATS_PETS_POIDS);
            obj.getTxtStat().put(Constant.STATS_PETS_POIDS, Integer.toString(this.corpulence));
            if (corpulence == 1) {
                this.quaEat++;
                SocketManager.GAME_SEND_Im_PACKET(p, "026");
            } else {
                this.pdv--;
                obj.getTxtStat().remove(Constant.STATS_PETS_PDV);
                obj.getTxtStat().put(Constant.STATS_PETS_PDV, Integer.toHexString((this.pdv > 0 ? (this.pdv) : 0)));
                SocketManager.GAME_SEND_Im_PACKET(p, "027");
            }
            if (this.quaEat >= RATIO_FEED) {
                //Update de l'item
                if ((this.getIsEupeoh() ? pets.getMax() * 1.1 : pets.getMax()) > this.getCurrentStatsPoids())//Si il est sous l'emprise d'EPO on augmente de +10% le jet maximum
                {
                    if (obj.getStats().getEffects().containsKey(statsID)) {
                        int value = obj.getStats().getEffects().get(statsID)
                                + World.world.getPets(World.world.getGameObject(this.objectId).getTemplate().getId()).getGain();
                        if (value > this.getMaxStat())
                            value = this.getMaxStat();
                        obj.getStats().getEffects().remove(statsID);
                        obj.getStats().addOneStat(statsID, value);
                    } else
                        obj.getStats().addOneStat(statsID, pets.getGain());
                }
                this.quaEat = 0;
            }
        } else if (((this.lastEatDate + (min * 3600000)) < System.currentTimeMillis())
                && this.corpulence >= 0)//Si il n'est pas maigrichon, et que le temps minimal est �coul�
        {
            //Update du petsEntry
            this.lastEatDate = System.currentTimeMillis();

            if (statsID != 0)
                this.quaEat++;
            else
                return;
            if (this.quaEat >= RATIO_FEED) {
                //Update de l'item
                if ((this.getIsEupeoh() ? pets.getMax() * 1.1 : pets.getMax()) > this.getCurrentStatsPoids())//Si il est sous l'emprise d'EPO on augmente de +10% le jet maximum
                {
                    if (obj.getStats().getEffects().containsKey(statsID)) {
                        int value = obj.getStats().getEffects().get(statsID)
                                + World.world.getPets(World.world.getGameObject(this.objectId).getTemplate().getId()).getGain();
                        if (value > this.getMaxStat())
                            value = this.getMaxStat();
                        obj.getStats().getEffects().remove(statsID);
                        obj.getStats().addOneStat(statsID, value);
                    } else
                        obj.getStats().addOneStat(statsID, pets.getGain());
                }
                this.quaEat = 0;
            }
            SocketManager.GAME_SEND_Im_PACKET(p, "032");
        }

        if (this.pdv <= 0) {
            this.pdv = 0;
            obj.getTxtStat().remove(Constant.STATS_PETS_PDV);
            obj.getTxtStat().put(Constant.STATS_PETS_PDV, Integer.toHexString((this.pdv > 0 ? (this.pdv) : 0)));//Mise a 0 des pdv
            if (pets.getDeadTemplate() == 0)// Si Pets DeadTemplate = 0 remove de l'item et pet entry
            {
                World.world.removeGameObject(obj.getGuid());
                p.removeItem(obj.getGuid());
                SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(p, obj.getGuid());
            } else {
                obj.setTemplate(pets.getDeadTemplate());

                if (obj.getPosition() == Constant.ITEM_POS_FAMILIER) {
                    obj.setPosition(Constant.ITEM_POS_NO_EQUIPED);
                    SocketManager.GAME_SEND_OBJET_MOVE_PACKET(p, obj);
                }
            }
            SocketManager.GAME_SEND_Im_PACKET(p, "154");
        }
        if (obj.getTxtStat().containsKey(Constant.STATS_PETS_REPAS)) {
            obj.getTxtStat().remove(Constant.STATS_PETS_REPAS);
            obj.getTxtStat().put(Constant.STATS_PETS_REPAS, Integer.toHexString(feed.getTemplate().getId()));
        } else {
            obj.getTxtStat().put(Constant.STATS_PETS_REPAS, Integer.toHexString(feed.getTemplate().getId()));
        }
        SocketManager.GAME_SEND_UPDATE_OBJECT_DISPLAY_PACKET(p, obj);
        ((ObjectData) DatabaseManager.get(ObjectData.class)).update(obj);
        ((PetData) DatabaseManager.get(PetData.class)).update(this);
    }

    public void eatSouls(Player p, Map<Integer, Integer> souls) {
        GameObject obj = World.world.getGameObject(this.objectId);
        if (obj == null)
            return;
        Pet pet = World.world.getPets(obj.getTemplate().getId());
        if (pet == null || pet.getType() != 1)
            return;
        //Ajout a l'item les SoulStats tu�s
        try {
            for (Entry<Integer, Integer> entry : souls.entrySet()) {
                int soul = entry.getKey();
                int count = entry.getValue();
                if (pet.canEat(-1, -1, soul)) {
                    int statsID = pet.statsIdByEat(-1, -1, soul);
                    if (statsID == 0)
                        return;
                    int soulCount = (obj.getSoulStat().get(soul) != null ? obj.getSoulStat().get(soul) : 0);
                    if (soulCount > 0) {
                        obj.getSoulStat().remove(soul);
                        obj.getSoulStat().put(soul, count + soulCount);
                    } else {
                        obj.getSoulStat().put(soul, count);
                    }
                }
            }
            //Re-Calcul des points gagn�s
            for (Entry<Integer, ArrayList<Map<Integer, Integer>>> ent : pet.getMonsters().entrySet()) {
                for (Map<Integer, Integer> entry : ent.getValue()) {
                    for (Entry<Integer, Integer> monsterEntry : entry.entrySet()) {
                        if (pet.getNumbMonster(ent.getKey(), monsterEntry.getKey()) != 0) {
                            int pts = 0;
                            for (Entry<Integer, Integer> list : obj.getSoulStat().entrySet()){
                            	int x = pet.getNumbMonster(ent.getKey(), list.getKey());
                            	if(x == 0)
                            		x = 1;                         		
                                pts += ((int) Math.floor(list.getValue() / x) * pet.getGain());
                            //System.out.println(pts);
                            }
                            if (pts > 0) {
                                if (pts > this.getMaxStat())
                                    pts = this.getMaxStat();
                                if (obj.getStats().getEffects().containsKey(ent.getKey())) {
                                    int nbr = obj.getStats().getEffects().get(ent.getKey());
                                    if(nbr - pts > 0)
                                        pts += (nbr - pts);
                                    obj.getStats().getEffects().remove(ent.getKey());
                                }
                                obj.getStats().getEffects().put(ent.getKey(), pts);
                            }
                        }
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Error : " + e.getMessage());
        }
        SocketManager.GAME_SEND_UPDATE_OBJECT_DISPLAY_PACKET(p, obj);
        ((ObjectData) DatabaseManager.get(ObjectData.class)).update(obj);
        ((PetData) DatabaseManager.get(PetData.class)).update(this);
    }

    public void updatePets(Player p, int max) {
        GameObject obj = World.world.getGameObject(this.objectId);
        if (obj == null)
            return;
        Pet pets = World.world.getPets(obj.getTemplate().getId());
        if (pets == null)
            return;
        if (this.pdv <= 0
                && obj.getTemplate().getId() == pets.getDeadTemplate())
            return;//Ne le met pas a jour si deja mort

        if (this.lastEatDate + (max * 3600000) < System.currentTimeMillis())//Oublier de le nourrir
        {
            //On calcul le nombre de repas oublier arrondi au sup�rieur :
            int nbrepas = (int) Math.floor((System.currentTimeMillis() - this.lastEatDate)
                    / (max * 3600000));
            //Perte corpulence
            this.corpulence = this.corpulence - nbrepas;

            if (nbrepas != 0) {
                obj.getTxtStat().remove(Constant.STATS_PETS_POIDS);
                obj.getTxtStat().put(Constant.STATS_PETS_POIDS, Integer.toString(this.corpulence));
            }
            //Perte pdv
            this.pdv--;
            obj.getTxtStat().remove(Constant.STATS_PETS_PDV);
            obj.getTxtStat().put(Constant.STATS_PETS_PDV, Integer.toHexString((this.pdv > 0 ? (this.pdv) : 0)));
            this.lastEatDate = System.currentTimeMillis();
        } else {
            if (this.pdv > 0)
                SocketManager.GAME_SEND_Im_PACKET(p, "025");
        }

        if (this.pdv <= 0) {
            this.pdv = 0;
            obj.getTxtStat().remove(Constant.STATS_PETS_PDV);
            obj.getTxtStat().put(Constant.STATS_PETS_PDV, Integer.toHexString((this.pdv > 0 ? (this.pdv) : 0)));

            if (pets.getDeadTemplate() == 0)//Si Pets DeadTemplate = 0 remove de l'item et pet entry
            {
                World.world.removeGameObject(obj.getGuid());
                p.removeItem(obj.getGuid());
                SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(p, obj.getGuid());
            } else {
                obj.setTemplate(pets.getDeadTemplate());
                if (obj.getPosition() == Constant.ITEM_POS_FAMILIER) {
                    obj.setPosition(Constant.ITEM_POS_NO_EQUIPED);
                    SocketManager.GAME_SEND_OBJET_MOVE_PACKET(p, obj);
                }
            }
            SocketManager.GAME_SEND_Im_PACKET(p, "154");
        }
        SocketManager.GAME_SEND_UPDATE_OBJECT_DISPLAY_PACKET(p, obj);
        ((ObjectData) DatabaseManager.get(ObjectData.class)).update(obj);
        ((PetData) DatabaseManager.get(PetData.class)).update(this);
    }

    public void resurrection() {
        GameObject obj = World.world.getGameObject(this.objectId);
        if (obj == null)
            return;

        obj.setTemplate(this.template);

        this.pdv = 1;
        this.corpulence = 0;
        this.quaEat = 0;
        this.lastEatDate = System.currentTimeMillis();

        obj.getTxtStat().remove(Constant.STATS_PETS_PDV);
        obj.getTxtStat().put(Constant.STATS_PETS_PDV, Integer.toHexString(this.pdv));
        ((ObjectData) DatabaseManager.get(ObjectData.class)).update(obj);
        ((PetData) DatabaseManager.get(PetData.class)).update(this);
    }

    public void restoreLife(Player p) {
        GameObject obj = World.world.getGameObject(this.objectId);
        if (obj == null)
            return;
        Pet pets = World.world.getPets(obj.getTemplate().getId());
        if (pets == null)
            return;

        if (this.pdv >= 10) {
            //Il la mange pas de pdv en plus
            SocketManager.GAME_SEND_Im_PACKET(p, "032");
        } else if (this.pdv > 0) {
            this.pdv++;

            obj.getTxtStat().remove(Constant.STATS_PETS_PDV);
            obj.getTxtStat().put(Constant.STATS_PETS_PDV, Integer.toHexString(this.pdv));

            //this.lastEatDate = System.currentTimeMillis();
            SocketManager.GAME_SEND_Im_PACKET(p, "032");
        } else {
            return;
        }
        ((ObjectData) DatabaseManager.get(ObjectData.class)).update(obj);
        ((PetData) DatabaseManager.get(PetData.class)).update(this);
    }

    public void giveEpo(Player p) {
        GameObject obj = World.world.getGameObject(this.objectId);
        if (obj == null)
            return;
        Pet pets = World.world.getPets(obj.getTemplate().getId());
        if (pets == null)
            return;
        if (this.isEupeoh)
            return;
        obj.getTxtStat().put(Constant.STATS_PETS_EPO, Integer.toHexString(1));
        SocketManager.GAME_SEND_Im_PACKET(p, "032");
        SocketManager.GAME_SEND_UPDATE_OBJECT_DISPLAY_PACKET(p, obj);
        ((PetData) DatabaseManager.get(PetData.class)).update(this);
    }
}