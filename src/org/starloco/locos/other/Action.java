package org.starloco.locos.other;

import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.area.map.entity.Animation;
import org.starloco.locos.area.map.entity.House;
import org.starloco.locos.area.map.entity.MountPark;
import org.starloco.locos.area.map.entity.Tutorial;
import org.starloco.locos.area.map.labyrinth.PigDragon;
import org.starloco.locos.client.Player;
import org.starloco.locos.client.other.Stalk;
import org.starloco.locos.client.other.Stats;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.game.NpcQuestionData;
import org.starloco.locos.database.data.login.ObjectData;
import org.starloco.locos.database.data.login.PlayerData;
import org.starloco.locos.entity.monster.Monster;
import org.starloco.locos.entity.npc.Npc;
import org.starloco.locos.entity.npc.NpcQuestion;
import org.starloco.locos.entity.pet.PetEntry;
import org.starloco.locos.game.GameClient;
import org.starloco.locos.game.GameServer;
import org.starloco.locos.game.action.ExchangeAction;
import org.starloco.locos.game.world.World;
import org.starloco.locos.game.world.World.Couple;
import org.starloco.locos.job.Job;
import org.starloco.locos.job.JobStat;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.object.ObjectTemplate;
import org.starloco.locos.object.entity.SoulStone;
import org.starloco.locos.quest.QuestPlayer;
import org.starloco.locos.quest.Quest;
import org.starloco.locos.util.TimerWaiter;

import java.util.*;
import java.util.Map.Entry;

public class Action {

    private int id;
    private String args;
    private String cond;
    private GameMap map;

    public Action(int id, String args, String cond, GameMap map) {
        this.setId(id);
        this.setArgs(args);
        this.setCond(cond);
        this.setMap(map);
    }

    public static java.util.Map<Integer, Couple<Integer, Integer>> getDopeul() {
        java.util.Map<Integer, Couple<Integer, Integer>> changeDopeul = new HashMap<Integer, Couple<Integer, Integer>>();
        changeDopeul.put(1549, Couple(167, 460)); // Dopeul iop
        changeDopeul.put(1466, Couple(169, 465)); // Dopeul sadida
        changeDopeul.put(1558, Couple(168, 458)); // Dopeul cra
        changeDopeul.put(1470, Couple(162, 464)); // Dopeul enu
        changeDopeul.put(1469, Couple(164, 468)); // Dopeul xelor
        changeDopeul.put(1546, Couple(161, 461)); // Dopeul osa
        changeDopeul.put(1554, Couple(160, 469)); // Dopeul feca
        changeDopeul.put(6928, Couple(166, 462)); // Dopeul eni
        changeDopeul.put(8490, Couple(2691, 466)); // Dopeul panda
        changeDopeul.put(6926, Couple(163, 467)); // Dopeul sram
        changeDopeul.put(1544, Couple(165, 459)); // Dopeul eca
        changeDopeul.put(6949, Couple(455, 463)); // Dopeul sacri
        return changeDopeul;
    }

    private static Couple<Integer, Integer> Couple(int i, int j) {
        return new Couple<Integer, Integer>(i, j);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public void setCond(String cond) {
        this.cond = cond;
    }

    public GameMap getMap() {
        return map;
    }

    public void setMap(GameMap map) {
        this.map = map;
    }

    public boolean apply(final Player player, Player target, int itemID,
                         int cellid) {

        if (player == null)
            return true;
        if (player.getFight() != null) {
            SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("ther.action.apply.impossible"), "000000");
            return true;
        }

        if (!cond.equalsIgnoreCase("") && !cond.equalsIgnoreCase("-1") && !World.world.getConditionManager().validConditions(player, cond)) {

            if(cond.contains(";")){
                String[] test = cond.split(";");
                for (int i =0;i<test.length;i++){
                    if(!World.world.getConditionManager().validConditions(player, test[i])){
                        SocketManager.GAME_SEND_Im_PACKET(player, "119");
                        return true;
                    }
                }
            }
            else {
                if(!World.world.getConditionManager().validConditions(player, cond)){
                    SocketManager.GAME_SEND_Im_PACKET(player, "119");
                    return true;
                }
            }
        }

        GameClient client = player.getGameClient();
        switch (id) {
            case -22: //Remettre prisonnier
                if (player.getObjetByPos(Constant.ITEM_POS_PNJ_SUIVEUR) != null) {
                    int skinFollower = player.getObjetByPos(Constant.ITEM_POS_PNJ_SUIVEUR).getTemplate().getId();
                    int questId = Constant.getQuestByMobSkin(skinFollower);
                    if (questId != -1) {
                        //perso.upgradeQuest(questId);
                        player.setMascotte(1);
                        int itemFollow = Constant.getItemByMobSkin(skinFollower);
                        player.removeByTemplateID(itemFollow, 1);
                    }
                }
                break;
            case -12: //TP with Unmorphfull and delete weapon Gladia
                if (player.getObjetByPos(Constant.ITEM_POS_PNJ_SUIVEUR) != null) {
                    int skinFollower = player.getObjetByPos(Constant.ITEM_POS_PNJ_SUIVEUR).getTemplate().getId();
                    int questId = Constant.getQuestByMobSkin(skinFollower);
                    if (questId != -1) {
                        //perso.upgradeQuest(questId);
                        player.setMascotte(1);
                        int itemFollow = Constant.getItemByMobSkin(skinFollower);
                        player.removeByTemplateID(itemFollow, 1);
                    }
                }
                break;
            case -11: //T�l�portation map d�part � la cr�ation d'un personnage (?)
                player.teleport(Constant.getStartMap(player.getClasse()), Constant.getStartCell(player.getClasse()));
                SocketManager.GAME_SEND_WELCOME(player);
                break;
            case -10: //Alignement ange si plus de demon et vice-versa sinon random
                if (player.getAlignment() == 1 || player.getAlignment() == 2
                        || player.getAlignment() == 3)
                    return true;
                int ange = 0;
                int demon = 0;
                int total = 0;
                for (Player i : World.world.getPlayers()) {
                    if (i == null)
                        continue;
                    if (i.getAlignment() == 1)
                        ange++;
                    if (i.getAlignment() == 2)
                        demon++;
                    total++;
                }
                ange = ange / total;
                demon = demon / total;
                if (ange > demon)
                    player.modifAlignement(2);
                else if (demon > ange)
                    player.modifAlignement(1);
                else if (demon == ange)
                    player.modifAlignement(Formulas.getRandomValue(1, 2));
                break;
            case -9: //Mettre un titre
                player.setAllTitle(args);
                break;

            case -8: //Ajouter un zaap
                player.verifAndAddZaap(Short.parseShort(args));
                break;

            case -7://Echange doplon		
                Dopeul.getReward(player, Integer.parseInt(args));
                break;

            case -6://Dopeuls
                GameMap mapActuel = player.getCurMap();
                java.util.Map<Integer, Couple<Integer, Integer>> dopeuls = Action.getDopeul();
                Integer IDmob = null;
                if (dopeuls.containsKey((int) mapActuel.getId())) {
                    IDmob = dopeuls.get((int) mapActuel.getId()).first;
                } else {
                    SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.error.dopeul"));
                    return true;
                }

                int LVLmob = Formulas.getLvlDopeuls(player.getLevel());
                if (player.getLevel() < 11) {
                    SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.error.dopeul.join.lvl"));
                    return true;
                }
                int certificat = Constant.getCertificatByDopeuls(IDmob);
                if (certificat == -1)
                    return true;
                if (player.hasItemTemplate(certificat, 1, false)) {
                    String date = player.getItemTemplate(certificat, 1).getTxtStat().get(Constant.STATS_DATE);
                    try {
                        long timeStamp = Long.parseLong(date);
                        if (System.currentTimeMillis() - timeStamp <= 86400000) {
                            SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.error.dopeul.join.day"));
                            return true;
                        } else
                            player.removeByTemplateID(certificat, 1);
                    } catch (Exception ignored) {
                        player.removeByTemplateID(certificat, 1);
                    }
                }
                boolean b = true;
                if (player.getQuestPerso() != null
                        && !player.getQuestPerso().isEmpty()) {
                    for (Entry<Integer, QuestPlayer> entry : new HashMap<>(player.getQuestPerso()).entrySet()) {
                        QuestPlayer qa = entry.getValue();
                        if (qa.getQuest().getId() == dopeuls.get((int) mapActuel.getId()).second) {
                            b = false;
                            if (qa.isFinish()) {
                                player.delQuestPerso(entry.getKey());
                                if (qa.removeQuestPlayer()) {
                                    Quest q = Quest.getQuestById(dopeuls.get((int) mapActuel.getId()).second);
                                    q.applyQuest(player);
                                }
                            }
                        }
                    }
                }
                if (b) {
                    Quest q = Quest.getQuestById(dopeuls.get((int) mapActuel.getId()).second);
                    q.applyQuest(player);
                }
                String grp = IDmob + "," + LVLmob + "," + LVLmob + ";";
                Monster.MobGroup MG = new Monster.MobGroup(player.getCurMap().nextObjectId, map, player.getCurCell().getId(), grp);
                player.getCurMap().startFigthVersusDopeuls(player, MG);
                break;
            case -5://Apprendre un sort
                try {
                    int sID = Integer.parseInt(args);
                    if (World.world.getSort(sID) == null)
                        return true;
                    player.learnSpell(sID, 1, true, true, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case -4://Prison
                switch (Short.parseShort(args)) {
                    case 1://Payer
                        player.leaveEnnemyFactionAndPay(player);
                        break;
                    case 2: //Attendre 10minutes
                        player.leaveEnnemyFaction();
                        break;
                }
                break;
            case -3://Mascotte
                int idMascotte = Integer.parseInt(args);

                if (player.hasItemTemplate(itemID, 1, false)) {
                    player.removeByTemplateID(itemID, 1);
                    player.setMascotte(idMascotte);
                    ((PlayerData) DatabaseManager.get(PlayerData.class)).update(player);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + itemID);
                    SocketManager.GAME_SEND_ALTER_GM_PACKET(player.getCurMap(), player);
                }
                break;
            case -2://cr�er guilde
                if (player.isAway())
                    return true;
                if (player.getGuild() != null || player.getGuildMember() != null) {
                    SocketManager.GAME_SEND_gC_PACKET(player, "Ea");
                    return true;
                }
                if (player.hasItemTemplate(1575, 1, false)) {
                    SocketManager.GAME_SEND_gn_PACKET(player);
                    player.removeByTemplateID(1575, -1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + -1 + "~"
                            + 1575);
                } else {
                    SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.error.guild.create.noguildalogemme"));
                }
                break;

            case -1://Ouvrir banque
                for (Npc npc : player.getCurMap().getNpcs().values()) {
                    if (npc.getTemplate().getGfxId() == 9048) {
                        player.openBank();
                        break;
                    }
                }
                break;

            case 0://T�l�portation
                try {
                    short newMapID = Short.parseShort(args.split(",", 2)[0]);
                    int newCellID = Integer.parseInt(args.split(",", 2)[1]);
                    if (!player.isInPrison()) {
                        player.teleport(newMapID, newCellID);
                    } else {
                        if (player.getCurCell().getId() == 268) {
                            player.teleport(newMapID, newCellID);
                        }
                    }
                } catch (Exception e) {
                    // Pas ok, mais il y a trop de dialogue de PNJ bugg� pour laisser cette erreur flood.
                    // e.printStackTrace();
                    return true;
                }
                break;

            case 1://Discours NPC
                if(client == null) return true;
                if (args.equalsIgnoreCase("DV")) {
                    SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                    player.setExchangeAction(null);
                } else {
                    int qID = -1;
                    try {
                        qID = Integer.parseInt(args);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    NpcQuestion quest = World.world.getNPCQuestion(qID);
                    if (quest == null) {
                        SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                        player.setExchangeAction(null);
                        return true;
                    }
                    try {
                        SocketManager.GAME_SEND_QUESTION_PACKET(client, quest.parse(player));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case 2://T�l�portation
                try {
                    short newMapID = Short.parseShort(args.split(",")[0]);
                    int newCellID = Integer.parseInt(args.split(",")[1]);
                    int verifMapID = Integer.parseInt(args.split(",")[2]);
                    if (player.getCurMap().getId() == verifMapID)
                        player.teleport(newMapID, newCellID);
                } catch (Exception e) {
                    e.printStackTrace();
                    return true;
                }
                break;
            case 3://Equip CAC - Gladiatroll
                try {
                    int position = Constant.ITEM_POS_ARME;
                    int quantity = 1;
                    int newCacID = Integer.parseInt(args.split(",")[0]);
                    int VerifCertificatID = Integer.parseInt(args.split(",")[1]);
                    if (player.hasItemTemplate(VerifCertificatID, 1, false)) {
                        String date = player.getItemTemplate(VerifCertificatID, 1).getTxtStat().get(Constant.STATS_DATE);
                        try {
                            long timeStamp = Long.parseLong(date);
                            if (System.currentTimeMillis() - timeStamp <= 60000) {
                                SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.error.gladiatroll.join.min"));
                                return true;
                            } else
                                player.removeByTemplateID(VerifCertificatID, 1);
                        } catch (Exception ignored) {
                            player.removeByTemplateID(VerifCertificatID, 1);
                        }
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1
                                + "~" + VerifCertificatID);
                    }

                    ObjectTemplate T = World.world.getObjTemplate(newCacID);
                    if (T == null)
                        return true;

                    player.unsetFullMorph();

                    GameObject exObj = player.getObjetByPos(Constant.ITEM_POS_ARME);//Objet a l'ancienne position
                    if (exObj != null)//S'il y avait d?ja un objet sur cette place on d?s?quipe
                    {
                        GameObject obj2;
                        ObjectTemplate exObjTpl = exObj.getTemplate();

                        if(Constant.isGladiatroolWeapon(exObjTpl.getId())) {
                            player.removeByTemplateID(player.getObjetByPos(Constant.ITEM_POS_ARME).getTemplate().getId(),1);
                        }
                        else {
                            int idSetExObj = exObj.getTemplate().getPanoId();
                            if ((obj2 = player.getSimilarItem(exObj)) != null)//On le poss?de deja
                            {
                                obj2.setQuantity(obj2.getQuantity()
                                        + exObj.getQuantity());
                                SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(player, obj2);
                                World.world.removeGameObject(exObj.getGuid());
                                player.removeItem(exObj.getGuid());
                                SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, exObj.getGuid());
                            } else
                            //On ne le poss?de pas
                            {
                                exObj.setPosition(Constant.ITEM_POS_NO_EQUIPED);
                                if ((idSetExObj >= 81 && idSetExObj <= 92)
                                        || (idSetExObj >= 201 && idSetExObj <= 212)) {
                                    String[] stats = exObjTpl.getStrTemplate().split(",");
                                    for (String stat : stats) {
                                        String[] val = stat.split("#");
                                        String modifi = Integer.parseInt(val[0], 16)
                                                + ";" + Integer.parseInt(val[1], 16)
                                                + ";0";
                                        SocketManager.SEND_SB_SPELL_BOOST(player, modifi);
                                        player.removeObjectClassSpell(Integer.parseInt(val[1], 16));
                                    }
                                    player.removeObjectClass(exObjTpl.getId());
                                }
                                SocketManager.GAME_SEND_OBJET_MOVE_PACKET(player, exObj);
                            }
                            if (player.getObjetByPos(Constant.ITEM_POS_ARME) == null)
                                SocketManager.GAME_SEND_OT_PACKET(player.getGameClient(), -1);

                            //Si objet de panoplie
                            if (exObj.getTemplate().getPanoId() > 0)
                                SocketManager.GAME_SEND_OS_PACKET(player, exObj.getTemplate().getPanoId());
                        }
                    }

                        GameObject object = T.createNewItem(quantity, false);
                        object.setPosition(position);
                        //Si retourne true, on l'ajoute au monde
                        if (player.addObjet(object, true))
                        World.world.addGameObject(object);
                        //SocketManager.GAME_SEND_OBJET_MOVE_PACKET(player, object);
                        SocketManager.GAME_SEND_Ow_PACKET(player);
                        SocketManager.GAME_SEND_ON_EQUIP_ITEM(player.getCurMap(), player);
                        ((ObjectData) DatabaseManager.get(ObjectData.class)).update(object);

                    int morphid = 0;
                    if (position == Constant.ITEM_POS_ARME) {
                        morphid = object.getTemplate().getId() - 12681;
                        player.setFullMorph(morphid, false, false);
                        Map<String, String> fullMorph = World.world.getFullMorph(morphid);


                        if (player.getObjetByPos(Constant.ITEM_POS_TONIQUE_EQUILIBRAGE) != null) {
                            int guid = player.getObjetByPos(Constant.ITEM_POS_TONIQUE_EQUILIBRAGE).getGuid();
                            SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, guid);
                            player.deleteItem(guid);
                        }
                        player.setToniqueEquilibrage(player.generateStatsTonique(fullMorph));

                    } else {// Tourmenteur ; on d?morphe
                        if (Constant.isIncarnationWeapon(object.getTemplate().getId()))
                            player.unsetFullMorph();
                    }


                    ObjectTemplate t2 = World.world.getObjTemplate(VerifCertificatID);
                    GameObject obj2 = t2.createNewItem(1, false);
                    obj2.refreshStatsObjet("325#0#0#"
                            + System.currentTimeMillis());
                    if (player.addObjet(obj2, false)) {
                        SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1
                                + "~" + obj2.getTemplate().getId());
                        World.world.addGameObject(obj2);
                        ((ObjectData) DatabaseManager.get(ObjectData.class)).update(object);
                    }

                    SocketManager.GAME_SEND_STATS_PACKET(player);




                } catch (Exception e) {
                    e.printStackTrace();
                    return true;
                }
                break;
            case 4://Kamas
                try {
                    int count = Integer.parseInt(args);
                    long curKamas = player.getKamas();
                    long newKamas = curKamas + count;
                    if (newKamas < 0) {
                        SocketManager.GAME_SEND_Im_PACKET(player, "084;1");
                        return true;
                    } else {
                        player.setKamas(newKamas);
                        SocketManager.GAME_SEND_Im_PACKET(player, "046;" + count);
                        if (player.isOnline())
                            SocketManager.GAME_SEND_STATS_PACKET(player);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;
            case 5://objet
                try {
                    int tID = Integer.parseInt(args.split(",")[0]);
                    int count = Integer.parseInt(args.split(",")[1]);
                    boolean send = true;
                    if (args.split(",").length > 2)
                        send = args.split(",")[2].equals("1");

                    //Si on ajoute
                    if (count > 0) {
                        ObjectTemplate T = World.world.getObjTemplate(tID);
                        if (T == null)
                            return true;
                        GameObject O = T.createNewItem(count, false);
                        //Si retourne true, on l'ajoute au monde
                        if (player.addObjet(O, true))
                            World.world.addGameObject(O);
                    } else {
                        player.removeByTemplateID(tID, -count);
                    }
                    //Si en ligne (normalement oui)
                    if (player.isOnline())//on envoie le packet qui indique l'ajout//retrait d'un item
                    {
                        SocketManager.GAME_SEND_Ow_PACKET(player);
                        if (send) {
                            if (count >= 0) {
                                SocketManager.GAME_SEND_Im_PACKET(player, "021;"
                                        + count + "~" + tID);
                            } else if (count < 0) {
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + -count + "~" + tID);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;
            case 6://Apprendre un m�tier
                try {
                    if(client == null) return true;
                    player.setIsOnDialogAction(1);
                    int mID = Integer.parseInt(args.split(",")[0]);
                    int mapId = Integer.parseInt(args.split(",")[1]);
                    int sucess = Integer.parseInt(args.split(",")[2]);
                    int fail = Integer.parseInt(args.split(",")[3]);
                    if (World.world.getMetier(mID) == null)
                        return true;
                    // Si c'est un m�tier 'basic' :
                    if (mID == 2 || mID == 11 || mID == 13 || mID == 14
                            || mID == 15 || mID == 16 || mID == 17 || mID == 18
                            || mID == 19 || mID == 20 || mID == 24 || mID == 25
                            || mID == 26 || mID == 27 || mID == 28 || mID == 31
                            || mID == 36 || mID == 41 || mID == 56 || mID == 58
                            || mID == 60 || mID == 65 || mID == 47 ) { // Ajouter métiers qui bug pnj ici !
                        if (player.getMetierByID(mID) != null)//M�tier d�j� appris
                        {
                            SocketManager.GAME_SEND_Im_PACKET(player, "111");
                            SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                            player.setExchangeAction(null);
                            player.setIsOnDialogAction(-1);
                            return true;
                        }
                        if (player.getMetierByID(2) != null
                                && player.getMetierByID(2).get_lvl() < 30
                                || player.getMetierByID(11) != null
                                && player.getMetierByID(11).get_lvl() < 30
                                || player.getMetierByID(13) != null
                                && player.getMetierByID(13).get_lvl() < 30
                                || player.getMetierByID(14) != null
                                && player.getMetierByID(14).get_lvl() < 30
                                || player.getMetierByID(15) != null
                                && player.getMetierByID(15).get_lvl() < 30
                                || player.getMetierByID(16) != null
                                && player.getMetierByID(16).get_lvl() < 30
                                || player.getMetierByID(17) != null
                                && player.getMetierByID(17).get_lvl() < 30
                                || player.getMetierByID(18) != null
                                && player.getMetierByID(18).get_lvl() < 30
                                || player.getMetierByID(19) != null
                                && player.getMetierByID(19).get_lvl() < 30
                                || player.getMetierByID(20) != null
                                && player.getMetierByID(20).get_lvl() < 30
                                || player.getMetierByID(24) != null
                                && player.getMetierByID(24).get_lvl() < 30
                                || player.getMetierByID(25) != null
                                && player.getMetierByID(25).get_lvl() < 30
                                || player.getMetierByID(26) != null
                                && player.getMetierByID(26).get_lvl() < 30
                                || player.getMetierByID(27) != null
                                && player.getMetierByID(27).get_lvl() < 30
                                || player.getMetierByID(28) != null
                                && player.getMetierByID(28).get_lvl() < 30
                                || player.getMetierByID(31) != null
                                && player.getMetierByID(31).get_lvl() < 30
                                || player.getMetierByID(36) != null
                                && player.getMetierByID(36).get_lvl() < 30
                                || player.getMetierByID(41) != null
                                && player.getMetierByID(41).get_lvl() < 30
                                || player.getMetierByID(56) != null
                                && player.getMetierByID(56).get_lvl() < 30
                                || player.getMetierByID(58) != null
                                && player.getMetierByID(58).get_lvl() < 30
                                || player.getMetierByID(60) != null
                                && player.getMetierByID(60).get_lvl() < 30
                                || player.getMetierByID(65) != null
                                && player.getMetierByID(65).get_lvl() < 30) {
                            if (sucess == -1 || fail == -1) {
                                SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                                player.setExchangeAction(null);
                                player.setIsOnDialogAction(-1);
                                SocketManager.GAME_SEND_Im_PACKET(player, "18;30");
                            } else
                                SocketManager.send(client, "DQ" + fail + "|4840");
                            return true;
                        }
                        if (player.totalJobBasic() > 2)//On compte les m�tiers d�ja acquis si c'est sup�rieur a 2 on ignore
                        {
                            SocketManager.GAME_SEND_Im_PACKET(player, "19");
                            SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                            player.setExchangeAction(null);
                            player.setIsOnDialogAction(-1);
                            return true;
                        } else
                        //Si c'est < ou = � 2 on apprend
                        {
                            if (mID == 27) {
                                if (!player.hasItemTemplate(966, 1, false))
                                    return true;
                                player.removeByTemplateID(966, 1);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + 966 + "~" + 1);
                                player.learnJob(World.world.getMetier(mID));
                            } else {
                                if (player.getCurMap().getId() != mapId)
                                    return true;
                                player.learnJob(World.world.getMetier(mID));
                                if (sucess == -1 || fail == -1) {
                                    SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                                    player.setExchangeAction(null);
                                    player.setIsOnDialogAction(-1);
                                } else
                                    SocketManager.send(client, "DQ" + sucess
                                            + "|4840");
                            }
                        }
                    }
                    // Si c'est une specialisations 'FM' :
                    /*
					 * if(mID == 43 || mID == 44 || mID == 45 || mID == 46 ||
					 * mID == 47 || mID == 48 || mID == 49 || mID == 50 || mID
					 * == 62 || mID == 63 || mID == 64) { //Si necessaire lvl
					 * 65, enlev� les hide si ankalike
					 * if(perso.getMetierByID(17) != null &&
					 * perso.getMetierByID(17).get_lvl() < 65 && mID == 43 ||
					 * perso.getMetierByID(11) != null &&
					 * perso.getMetierByID(11).get_lvl() < 65 && mID == 44 ||
					 * perso.getMetierByID(14) != null &&
					 * perso.getMetierByID(14).get_lvl() < 65 && mID == 45 ||
					 * perso.getMetierByID(20) != null &&
					 * perso.getMetierByID(20).get_lvl() < 65 && mID == 46 ||
					 * perso.getMetierByID(31) != null &&
					 * perso.getMetierByID(31).get_lvl() < 65 && mID == 47 ||
					 * perso.getMetierByID(13) != null &&
					 * perso.getMetierByID(13).get_lvl() < 65 && mID == 48 ||
					 * perso.getMetierByID(19) != null &&
					 * perso.getMetierByID(19).get_lvl() < 65 && mID == 49 ||
					 * perso.getMetierByID(18) != null &&
					 * perso.getMetierByID(18).get_lvl() < 65 && mID == 50 ||
					 * perso.getMetierByID(15) != null &&
					 * perso.getMetierByID(15).get_lvl() < 65 && mID == 62 ||
					 * perso.getMetierByID(16) != null &&
					 * perso.getMetierByID(16).get_lvl() < 65 && mID == 63 ||
					 * perso.getMetierByID(27) != null &&
					 * perso.getMetierByID(27).get_lvl() < 65 && mID == 64) {
					 * //On compte les specialisations d�ja acquis si c'est
					 * sup�rieur a 2 on ignore if(perso.getMetierByID(mID) !=
					 * null)//M�tier d�j� appris
					 * SocketManager.GAME_SEND_Im_PACKET(perso, "111");
					 * if(perso.totalJobFM() > 2)//On compte les m�tiers d�ja
					 * acquis si c'est sup�rieur a 2 on ignore
					 * SocketManager.GAME_SEND_Im_PACKET(perso, "19"); else//Si
					 * c'est < ou = � 2 on apprend
					 * perso.learnJob(World.world.getMetier(mID)); }else {
					 * SocketManager.GAME_SEND_Im_PACKET(perso, "12"); } }
					 */
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 7://retour au point de sauvegarde
                if (!player.isInPrison())
                    player.warpToSavePos();
                break;

            case 8://Ajouter une Stat
                try {
                    int statID = Integer.parseInt(args.split(",", 2)[0]);
                    int number = Integer.parseInt(args.split(",", 2)[1]);
                    player.getStats().addOneStat(statID, number);
                    SocketManager.GAME_SEND_STATS_PACKET(player);
                    int messID = 0;
                    switch (statID) {
                        case Constant.STATS_ADD_INTE:
                            messID = 14;
                            break;
                    }
                    if (messID > 0)
                        SocketManager.GAME_SEND_Im_PACKET(player, "0" + messID
                                + ";" + number);
                } catch (Exception e) {
                    e.printStackTrace();
                    return true;
                }
                break;

            case 9://Apprendre un sort
                try {
                    int sID = Integer.parseInt(args.split(",", 2)[0]);
                    int mapId = Integer.parseInt(args.split(",", 2)[1]);
                    if (World.world.getSort(sID) == null)
                        return true;
                    if (player.getCurMap().getId() != mapId)
                        return true;
                    player.learnSpell(sID, 1, true, true, true);
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 10://Pain/potion/viande/poisson
                try {
                    int min = Integer.parseInt(args.split(",", 2)[0]);
                    int max = Integer.parseInt(args.split(",", 2)[1]);
                    if (max == 0)
                        max = min;
                    int val = Formulas.getRandomValue(min, max);
                    if (target != null) {
                        if (target.getCurPdv() + val > target.getMaxPdv())
                            val = target.getMaxPdv() - target.getCurPdv();
                        target.setPdv(target.getCurPdv() + val);
                        SocketManager.GAME_SEND_STATS_PACKET(target);
                    } else {
                        if (player.getCurPdv() + val > player.getMaxPdv())
                            val = player.getMaxPdv() - player.getCurPdv();
                        player.setPdv(player.getCurPdv() + val);
                        SocketManager.GAME_SEND_STATS_PACKET(player);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 11://Definir l'alignement
                try {
                    byte newAlign = Byte.parseByte(args.split(",", 2)[0]);
                    boolean replace = Integer.parseInt(args.split(",", 2)[1]) == 1;
                    if (player.getAlignment() != Constant.ALIGNEMENT_NEUTRE && !replace)
                        return true;
                    player.modifAlignement(newAlign);
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 12://Spawn d'un groupe de monstre
                try {
                    boolean delObj = args.split(",")[0].equals("true");
                    boolean inArena = args.split(",")[1].equals("true");

                    if (inArena && SoulStone.isInArenaMap(player.getCurMap().getId()))
                        return true;

                    SoulStone pierrePleine = (SoulStone) World.world.getGameObject(itemID);

                    String groupData = pierrePleine.parseGroupData();
                    String condition = "MiS = " + player.getId(); //Condition pour que le groupe ne soit lan�able que par le personnage qui � utiliser l'objet
                    player.getCurMap().spawnNewGroup(true, player.getCurCell().getId(), groupData, condition);

                    if (delObj)
                        player.removeItem(itemID, 1, true, true);
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 13: //Reset Carac incarnam
                if (player.getLevel() <= 30 || Config.resetLimit) {
                    try {
                        
                        if (player.getStatsParcho().getEffect(125) != 0 || player.getStatsParcho().getEffect(124) != 0 || player.getStatsParcho().getEffect(118) != 0
                                || player.getStatsParcho().getEffect(119) != 0 || player.getStatsParcho().getEffect(126) != 0 || player.getStatsParcho().getEffect(123) != 0) {
                            player.getStats().addOneStat(125, -player.getStats().getEffect(125) + player.getStatsParcho().getEffect(125));
                            player.getStats().addOneStat(124, -player.getStats().getEffect(124) + player.getStatsParcho().getEffect(124));
                            player.getStats().addOneStat(118, -player.getStats().getEffect(118) + player.getStatsParcho().getEffect(118));
                            player.getStats().addOneStat(123, -player.getStats().getEffect(123) + player.getStatsParcho().getEffect(123));
                            player.getStats().addOneStat(119, -player.getStats().getEffect(119) + player.getStatsParcho().getEffect(119));
                            player.getStats().addOneStat(126, -player.getStats().getEffect(126) + player.getStatsParcho().getEffect(126));
                            player.addCapital((player.getLevel() - 1) * 5 - player.getCapital());
                            SocketManager.GAME_SEND_STATS_PACKET(player);
                            return true;
                        } else if (player.getStats().getEffect(125) == 101 && player.getStats().getEffect(124) == 101 && player.getStats().getEffect(118) == 101
                                && player.getStats().getEffect(123) == 101 && player.getStats().getEffect(119) == 101 && player.getStats().getEffect(126) == 101) {
                            player.getStats().addOneStat(125, -player.getStats().getEffect(125) + 101);
                            player.getStats().addOneStat(124, -player.getStats().getEffect(124) + 101);
                            player.getStats().addOneStat(118, -player.getStats().getEffect(118) + 101);
                            player.getStats().addOneStat(123, -player.getStats().getEffect(123) + 101);
                            player.getStats().addOneStat(119, -player.getStats().getEffect(119) + 101);
                            player.getStats().addOneStat(126, -player.getStats().getEffect(126) + 101);
                            player.getStatsParcho().getEffects().clear();
                            player.getStatsParcho().addOneStat(125, 101);
                            player.getStatsParcho().addOneStat(124, 101);
                            player.getStatsParcho().addOneStat(118, 101);
                            player.getStatsParcho().addOneStat(123, 101);
                            player.getStatsParcho().addOneStat(119, 101);
                            player.getStatsParcho().addOneStat(126, 101);

                            player.addCapital((player.getLevel() - 1) * 5 - player.getCapital());
                            SocketManager.GAME_SEND_STATS_PACKET(player);
                            return true;
                        }
                    player.getStats().addOneStat(125, -player.getStats().getEffect(125));
                    player.getStats().addOneStat(124, -player.getStats().getEffect(124));
                    player.getStats().addOneStat(118, -player.getStats().getEffect(118));
                    player.getStats().addOneStat(123, -player.getStats().getEffect(123));
                    player.getStats().addOneStat(119, -player.getStats().getEffect(119));
                    player.getStats().addOneStat(126, -player.getStats().getEffect(126));
                    player.addCapital((player.getLevel() - 1) * 5 - player.getCapital());
                    player.getStatsParcho().getEffects().clear();
                    SocketManager.GAME_SEND_STATS_PACKET(player);
                } catch (Exception e) {
                        e.printStackTrace();
                        GameServer.a();
                    }
                } else {
                    SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.error.reset.lvl.30"));
                }
                break;

            case 14://Ouvrir l'interface d'oublie de sort incarnam
                if (player.getLevel() <= 30 || Config.resetLimit) {
                    player.setExchangeAction(new ExchangeAction<>(ExchangeAction.FORGETTING_SPELL, 0));
                    SocketManager.GAME_SEND_FORGETSPELL_INTERFACE('+', player);
                } else {
                    SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.error.reset.lvl.30"));
                }
                break;

            case 15://T�l�portation donjon
                try {
                    short newMapID = Short.parseShort(args.split(",")[0]);
                    int newCellID = Integer.parseInt(args.split(",")[1]);
                    int ObjetNeed = Integer.parseInt(args.split(",")[2]);
                    int MapNeed = Integer.parseInt(args.split(",")[3]);



                    if (ObjetNeed == 0) {
                        //T�l�portation sans objets
                        player.teleport(newMapID, newCellID);
                    } else if (ObjetNeed > 0) {
                        if (MapNeed == 0) {
                            //T�l�portation sans map
                            player.teleport(newMapID, newCellID);
                        } else if (MapNeed > 0) {
                            if (player.hasItemTemplate(ObjetNeed, 1, false)
                                    && player.getCurMap().getId() == MapNeed) {
                                //Le perso a l'item
                                //Le perso est sur la bonne map
                                //On t�l�porte, on supprime apr�s
                                if(ObjetNeed == 12804){
                                    if(Constant.isGladiatroolWeapon(player.getObjetByPos(Constant.ITEM_POS_ARME).getTemplate().getId())){
                                        player.teleport(newMapID, newCellID);
                                        player.removeByTemplateID(ObjetNeed, 1);
                                        SocketManager.GAME_SEND_Ow_PACKET(player);
                                    }
                                    else{
                                        SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.error.gladiatroll.join.cac"), "009900");
                                    }
                                }
                                else {
                                    player.teleport(newMapID, newCellID);
                                    player.removeByTemplateID(ObjetNeed, 1);
                                    SocketManager.GAME_SEND_Ow_PACKET(player);
                                }
                            } else if (player.getCurMap().getId() != MapNeed) {
                                //Le perso n'est pas sur la bonne map
                                SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.dj.map"), "009900");
                            } else {
                                //Le perso ne poss�de pas l'item
                                SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.dj.nokey"), "009900");
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 16://T�l�portation donjon sans perte de clef
                try {
                    short newMapID = Short.parseShort(args.split(",")[0]);
                    int newCellID = Integer.parseInt(args.split(",")[1]);
                    int ObjetNeed = Integer.parseInt(args.split(",")[2]);
                    int MapNeed = Integer.parseInt(args.split(",")[3]);
                    if (ObjetNeed == 0) {
                        //T�l�portation sans objets
                        player.teleport(newMapID, newCellID);
                    } else if (ObjetNeed > 0) {
                        if (MapNeed == 0) {
                            //T�l�portation sans map
                            player.teleport(newMapID, newCellID);
                        } else if (MapNeed > 0) {
                            if (player.hasItemTemplate(ObjetNeed, 1, false)
                                    && player.getCurMap().getId() == MapNeed) {
                                //Le perso a l'item
                                //Le perso est sur la bonne map
                                //On t�l�porte
                                player.teleport(newMapID, newCellID);
                                SocketManager.GAME_SEND_Ow_PACKET(player);
                            } else if (player.getCurMap().getId() != MapNeed) {
                                //Le perso n'est pas sur la bonne map
                                SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.dj.map"), "009900");
                            } else {
                                //Le perso ne poss�de pas l'item
                                SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.dj.nokey"), "009900");
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 17://Xp m�tier JobID,XpValue
                try {
                    int JobID = Integer.parseInt(args.split(",")[0]);
                    int XpValue = Integer.parseInt(args.split(",")[1]);
                    if (player.getMetierByID(JobID) != null) {
                        player.getMetierByID(JobID).addXp(player, XpValue);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 18://T�l�portation chez soi
                if (World.world.getHouseManager().alreadyHaveHouse(player))//Si il a une maison
                {
                    GameObject obj2 = World.world.getGameObject(itemID);
                    if (player.hasItemTemplate(obj2.getTemplate().getId(), 1, false)) {
                        player.removeByTemplateID(obj2.getTemplate().getId(), 1);
                        House h = World.world.getHouseManager().getHouseByPerso(player);
                        if (h == null)
                            return true;
                        player.teleport((short) h.getHouseMapId(), h.getHouseCellId());
                    }
                }
                break;

            case 19://T�l�portation maison de guilde (ouverture du panneau de guilde)
                SocketManager.GAME_SEND_GUILDHOUSE_PACKET(player);
                break;

            case 20://+Points de sorts
                try {
                    int pts = Integer.parseInt(args);
                    if (pts < 1)
                        return true;
                    player.addSpellPoint(pts);
                    SocketManager.GAME_SEND_STATS_PACKET(player);
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 21://+Energie
                try {
                    int energyMin = Integer.parseInt(args.split(",", 2)[0]);
                    int energyMax = Integer.parseInt(args.split(",", 2)[1]);
                    if (energyMax == 0)
                        energyMax = energyMin;
                    int val = Formulas.getRandomValue(energyMin, energyMax);
                    int EnergyTotal = player.getEnergy() + val;
                    if (EnergyTotal > 10000)
                        EnergyTotal = 10000;
                    player.setEnergy(EnergyTotal);
                    SocketManager.GAME_SEND_STATS_PACKET(player);
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 22://+Xp
                try {
                    long XpAdd = Integer.parseInt(args);
                    if (XpAdd < 1)
                        return true;

                    long TotalXp = player.getExp() + XpAdd;
                    player.setExp(TotalXp);
                    SocketManager.GAME_SEND_STATS_PACKET(player);
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 23://UnlearnJob
                int Job = Integer.parseInt(args.split(",", 2)[0]);
                int mapId = Integer.parseInt(args.split(",", 2)[1]);
                if (player.getCurMap().getId() != mapId)
                    return true;
                if (Job < 1)
                    return true;
                JobStat m2 = player.getMetierByID(Job);
                if (m2 == null)
                    return true;
                player.unlearnJob(m2.getId());
                SocketManager.GAME_SEND_STATS_PACKET(player);
                ((PlayerData) DatabaseManager.get(PlayerData.class)).update(player);
                break;

            case 24://Morph
                try {
                    int morphID = Integer.parseInt(args);
                    if (morphID < 0)
                        return true;
                    player.setGfxId(morphID);
                    SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(player.getCurMap(), player.getId());
                    SocketManager.GAME_SEND_ADD_PLAYER_TO_MAP(player.getCurMap(), player);
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 25://SimpleUnMorph
                int UnMorphID = player.getClasse() * 10 + player.getSexe();
                player.setGfxId(UnMorphID);
                SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(player.getCurMap(), player.getId());
                SocketManager.GAME_SEND_ADD_PLAYER_TO_MAP(player.getCurMap(), player);
                break;

            case 26://T�l�portation enclos de guilde (ouverture du panneau de guilde)
                SocketManager.GAME_SEND_GUILDENCLO_PACKET(player);
                break;

            case 27://Lancement de combat : startFigthVersusMonstres args : monsterID,monsterLevel| ...
                // id,lvl|id,lvl:mapid
                String ValidMobGroup = "";
                if (player.getFight() != null)
                    return true;
                try {
                    int mapId1 = Integer.parseInt(args.split(":", 2)[1]);
                    if (player.getCurMap().getId() != mapId1)
                        return true;
                    for (String MobAndLevel : args.split(":", 2)[0].split("\\|")) {
                        int monsterID = -1;
                        int monsterLevel = -1;
                        String[] MobOrLevel = MobAndLevel.split(",");
                        monsterID = Integer.parseInt(MobOrLevel[0]);
                        monsterLevel = Integer.parseInt(MobOrLevel[1]);

                        if (World.world.getMonstre(monsterID) == null
                                || World.world.getMonstre(monsterID).getGradeByLevel(monsterLevel) == null) {
                            continue;
                        }
                        ValidMobGroup += monsterID + "," + monsterLevel + ","
                                + monsterLevel + ";";
                    }
                    if (ValidMobGroup.isEmpty())
                        return true;
                    Monster.MobGroup group = new Monster.MobGroup(player.getCurMap().nextObjectId, map, player.getCurCell().getId(), ValidMobGroup);
                    player.getCurMap().startFightVersusMonstres(player, group); // Si bug startfight, voir "//Respawn d'un groupe fix" dans fight.java
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 28://Desapprendre un sort
                try {
                    int sID = Integer.parseInt(args);
                    int AncLevel = player.getSortStatBySortIfHas(sID).getLevel();
                    if (player.getSortStatBySortIfHas(sID) == null)
                        return true;
                    if (AncLevel <= 1)
                        return true;
                    player.unlearnSpell(player, sID, 1, AncLevel, true, true);
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 29://Desapprendre un sort avec kamas
                long pKamas3 = player.getKamas();
                int payKamas = player.getLevel() * player.getLevel() * 25;

                if (pKamas3 >= payKamas) {
                    long pNewKamas3 = pKamas3 - payKamas;
                    if (pNewKamas3 < 0)
                        pNewKamas3 = 0;
                    int sID = Integer.parseInt(args);
                    int AncLevel = player.getSortStatBySortIfHas(sID).getLevel();
                    if (player.getSortStatBySortIfHas(sID) == null)
                        return true;
                    if (AncLevel <= 1)
                        return true;
                    player.unlearnSpell(player, sID, 1, AncLevel, true, true);
                    SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.error.unlearnspell", payKamas));
                } else {
                    SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.error.nokamas"));
                    return true;
                }
                break;

            case 30: //Change la taille d'un personnage size
                int size = Integer.parseInt(args);
                player.set_size(size);
                SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(player.getCurMap(), player.getId());
                SocketManager.GAME_SEND_ADD_PLAYER_TO_MAP(player.getCurMap(), player);
                break;

            case 31:// change classe(zobal)
                /*
				 * try { int classe = Integer.parseInt(args); if (classe ==
				 * perso.getClasse()) { SocketManager.GAME_SEND_MESSAGE(perso,
				 * "Vous �tes d�j� de cette classe."); return true; } int level
				 * = perso.getLevel(); perso.setClasse(classe); Stats baseStats
				 * = perso.getStats(); baseStats.addOneStat(125,
				 * -perso.getStats().getEffect(125)); baseStats.addOneStat(124,
				 * -perso.getStats().getEffect(124)); baseStats.addOneStat(118,
				 * -perso.getStats().getEffect(118)); baseStats.addOneStat(123,
				 * -perso.getStats().getEffect(123)); baseStats.addOneStat(119,
				 * -perso.getStats().getEffect(119)); baseStats.addOneStat(126,
				 * -perso.getStats().getEffect(126)); perso.setCapital(0);
				 * perso.setSpellPoints(0);
				 * perso.setSpells(Constant.getStartSorts(classe));
				 * perso.setLevel(1); while (perso.getLevel() < level) {
				 * perso.levelUp(false, false); } int morph = classe * 10 +
				 * perso.getSexe(); perso.setGfxId(morph);
				 * SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP
				 * (perso.getCurMap(), perso.getId());
				 * SocketManager.GAME_SEND_ADD_PLAYER_TO_MAP(perso.getCurMap(),
				 * perso); SocketManager.GAME_SEND_STATS_PACKET(perso);
				 * SocketManager.GAME_SEND_ASK(client, perso);
				 * SocketManager.GAME_SEND_SPELL_LIST(perso);
				 * SqlPlayer.updateInfos(perso); } catch (Exception e) {
				 * e.printStackTrace(); }
				 */
                break;

            case 33:// Stat max a un obj pos
                int posItem = Integer.parseInt(args);
                GameObject itemPos = player.getObjetByPos(posItem);
                if (itemPos != null) {
                    itemPos.clearStats();
                    Stats maxStats = itemPos.generateNewStatsFromTemplate(itemPos.getTemplate().getStrTemplate(), true);
                    itemPos.setStats(maxStats);
                    int idObjPos = itemPos.getGuid();
                    player.removeItem(itemID, 1, true, true);
                    SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos);
                    SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos);
                    SocketManager.GAME_SEND_STATS_PACKET(player);
                } else {
                    SocketManager.GAME_SEND_Im_PACKET(player, player.getLang().trans("other.action.apply.JP.noitem"));
                }
                break;

            case 34://Loterie
                int idLot = Integer.parseInt(args.split(",", 2)[0]);
                //int mapId1 = Integer.parseInt(args.split(",", 2)[1]);
                Loterie.startLoterie(player, idLot);
                break;

            case 35: //Reset Carac condition : Map x�lor 741 et l'obre de recons 10563
                try {
                    if (player.getCurMap().getId() != 741
                            || !player.hasItemTemplate(10563, 1, false))
                        return true;
                    player.removeByTemplateID(10563, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 10563
                            + "~" + 1);
                    player.getStats().addOneStat(125, -player.getStats().getEffect(125));
                    player.getStats().addOneStat(124, -player.getStats().getEffect(124));
                    player.getStats().addOneStat(118, -player.getStats().getEffect(118));
                    player.getStats().addOneStat(123, -player.getStats().getEffect(123));
                    player.getStats().addOneStat(119, -player.getStats().getEffect(119));
                    player.getStats().addOneStat(126, -player.getStats().getEffect(126));
                    player.addCapital((player.getLevel() - 1) * 5
                            - player.getCapital());
                    SocketManager.GAME_SEND_STATS_PACKET(player);
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 36: //Cout d'un jeu
                try {
                    long price = Integer.parseInt(args.split(";")[0]);
                    int tutorial = Integer.parseInt(args.split(";")[1]);
                    if (tutorial == 30) {
                        int random = Formulas.getRandomValue(1, 200);
                        if (random == 100)
                            tutorial = 31;
                        else
                            ((NpcQuestionData) DatabaseManager.get(NpcQuestionData.class)).updateLot();
                    }
                    final Tutorial tuto = World.world.getTutorial(tutorial);
                    if (tuto == null)
                        return true;
                    if (player.getKamas() >= price) {
                        if (price != 0L) {
                            player.setKamas(player.getKamas() - price);
                            if (player.isOnline())
                                SocketManager.GAME_SEND_STATS_PACKET(player);
                            SocketManager.GAME_SEND_Im_PACKET(player, "046;"
                                    + price);
                        }
                        try {
                            tuto.getStart().apply(player, null, -1, (short) -1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        TimerWaiter.addNext(() -> {
                            SocketManager.send(player, "TC" + tuto.getId() + "|7001010000");
                            player.setExchangeAction(new ExchangeAction<>(ExchangeAction.IN_TUTORIAL, tuto));
                            player.setAway(true);
                        }, 1500);
                        return true;
                    }
                    SocketManager.GAME_SEND_Im_PACKET(player, "182");
                } catch (Exception e23) {
                    e23.printStackTrace();
                }
                break;

            case 37://Loterie pioute
                Loterie.startLoteriePioute(player);
                break;

            case 38://Apprendre une �mote
                player.addStaticEmote(Integer.parseInt(args));
                break;

            case 40: //Donner une qu�te
                int QuestID = Integer.parseInt(args);
                boolean problem = false;
                Quest quest0 = Quest.getQuestById(QuestID);
                if (quest0 == null) {
                    SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.error.quest"));
                    problem = true;
                }
                for (QuestPlayer qPerso : player.getQuestPerso().values()) {
                    if (qPerso.getQuest().getId() == QuestID) {
                        SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.error.quest.known"));
                        problem = true;
                        break;
                    }
                }

                if (!problem) {
                    quest0.applyQuest(player);
                    if(player.getExchangeAction().getType() == ExchangeAction.TALKING_WITH) {
                        Npc npc = player.getCurMap().getNpc(((ExchangeAction<Integer>) player.getExchangeAction()).getValue());
                        player.send("GM|" + npc.parse(true, player));
                    }
                }
                break;

            case 41: //Confirm objective
                break;

            case 42: //Monte prochaine étape quete ou termine
                String[] split = args.split(";");
                int questId = Integer.parseInt(split[0]), objectifId = Integer.parseInt(split[1]);

                player.getQuestPerso().values().stream().filter(quest -> quest.getQuest().getId() == questId).forEach(quest -> {
                    quest.getQuest().getQuestObjectives().stream().filter(step -> step.getObjectif() == objectifId).forEach(step -> {
                        step.getQuestData().updateQuestData(player, true, step.getValidationType());
                    });
                });
                break;

            case 43: //T�l�portation de qu�te
                split = args.split(";");
                int mapid = Integer.parseInt(split[0].split(",")[0]);
                cellid = Integer.parseInt(split[0].split(",")[1]);
                int mapsecu = Integer.parseInt(split[1]);
                questId = Integer.parseInt(split[2]);

                if (player.getCurMap().getId() != mapsecu)
                    return true;
                QuestPlayer questt = player.getQuestPersoByQuestId(questId);
                if (questt == null || !questt.isFinish())
                    return true;
                player.teleport((short) mapid, cellid);
                break;
            case 49: // Echange Gladiatroc
                try {
                    int tID = Integer.parseInt(args.split(",")[0]);
                    int count = Integer.parseInt(args.split(",")[1]);
                    int tMoney = Integer.parseInt(args.split(",")[2]);
                    int price = Integer.parseInt(args.split(",")[3]);

                    if (!player.hasItemTemplate(tMoney,price,false)) {
                        SocketManager.GAME_SEND_BUY_ERROR_PACKET(player.getGameClient());
                        return false;
                    }
                    player.removeByTemplateID(tMoney,price);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + price + "~" + tMoney);

                    boolean send = true;

                    //Si on ajoute
                    if (count > 0) {
                        ObjectTemplate T = World.world.getObjTemplate(tID);
                        if (T == null)
                            return true;
                        GameObject O = T.createNewItem(count, false);
                        //Si retourne true, on l'ajoute au monde
                        if (player.addObjet(O, true))
                            World.world.addGameObject(O);
                    } else {
                        player.removeByTemplateID(tID, -count);
                    }
                    //Si en ligne (normalement oui)
                    if (player.isOnline())//on envoie le packet qui indique l'ajout//retrait d'un item
                    {
                        SocketManager.GAME_SEND_Ow_PACKET(player);
                        if (send) {
                            if (count >= 0) {
                                SocketManager.GAME_SEND_Im_PACKET(player, "021;"
                                        + count + "~" + tID);
                            } else if (count < 0) {
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + -count + "~" + tID);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;
            case 50: //Traque
                if (player.getAlignment() == 0 || player.getAlignment() == 3)
                    return true;

                if (player.getStalk() != null && player.getStalk().getTime() == -2) {
                    long xp = Formulas.getXpStalk(player.getLevel());
                    player.addXp(xp);
                    player.setStalk(null);//On supprime la traque
                    SocketManager.GAME_SEND_STATS_PACKET(player);
                    SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.traque.exp", xp), "000000");
                    return true;
                } else if(player.getStalk() != null && player.getStalk().getTarget() != null && player.getStalk().getTarget().isOnline()) {
                    SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.traque.nkill"), "000000");
                    return true;
                } else {
                    player.setStalk(null);
                }

                if (player.getStalk() == null) {
                    Stalk t = new Stalk(0, null);
                    player.setStalk(t);
                }
                if (player.getStalk().getTime() < System.currentTimeMillis() - 600000 || player.getStalk().getTime() == 0) {
                    Player tempP = null;
                    ArrayList<Player> victimes = new ArrayList<Player>();
                    for (Player victime : World.world.getOnlinePlayers()) {
                        if (victime == null || victime == player)
                            continue;
						if (victime.getAccount().getCurrentIp().compareTo(player.getAccount().getCurrentIp()) == 0)
                            continue;
                        if (victime.getAlignment() == player.getAlignment() || victime.getAlignment() == 0 || victime.getAlignment() == 3 || !victime.is_showWings())
                            continue;
                        if (((player.getLevel() + 20) >= victime.getLevel()) && ((player.getLevel() - 20) <= victime.getLevel()))
                            victimes.add(victime);
                    }
                    if (victimes.size() == 0) {
                        SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.traque.notarget"), "000000");
                        player.setStalk(null);
                        return true;
                    }
                    if (victimes.size() == 1)
                        tempP = victimes.get(0);
                    else
                        tempP = victimes.get(Formulas.getRandomValue(0, victimes.size() - 1));
                    SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.traque.target", tempP.getName()), "000000");
                    player.getStalk().setTarget(tempP);
                    player.getStalk().setTime(System.currentTimeMillis());
                    GameObject object = player.getItemTemplate(10085);
                    if(object != null)
                        player.removeItem(object.getGuid(), player.getNbItemTemplate(10085), true, true);
                    ObjectTemplate T = World.world.getObjTemplate(10085);
                    GameObject newObj = T.createNewItem(20, false);
                    newObj.addTxtStat(Constant.STATS_NAME_TRAQUE, tempP.getName());
                    newObj.addTxtStat(Constant.STATS_ALIGNEMENT_TRAQUE, Integer.toHexString(tempP.getAlignment()) + "");
                    newObj.addTxtStat(Constant.STATS_GRADE_TRAQUE, Integer.toHexString(tempP.getGrade()) + "");
                    newObj.addTxtStat(Constant.STATS_NIVEAU_TRAQUE, Integer.toHexString(tempP.getLevel()) + "");

                    if (player.addObjet(newObj, true))
                        World.world.addGameObject(newObj);
                } else {
                    SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.traque.sleep"), "000000");
                }
                break;

            case 53: //Suivre le d�placement pour une map pr�sice
                if (args == null)
                    break;
                if (World.world.getMap(Short.parseShort(args)) == null)
                    break;
                GameMap CurMap = World.world.getMap(Short.parseShort(args));
                if (player.getFight() == null) {
                    SocketManager.GAME_SEND_FLAG_PACKET(player, CurMap);
                }
                break;

            case 60: //Combattre un protecteur
                String ValidMobGroup1 = "";
                if (player.getFight() != null)
                    return true;
                try {
                    for (String MobAndLevel : args.split("\\|")) {
                        int monsterID = -1;
                        int lvlMin = -1;
                        int lvlMax = -1;
                        String[] MobOrLevel = MobAndLevel.split(",");
                        monsterID = Integer.parseInt(MobOrLevel[0]);
                        lvlMin = Integer.parseInt(MobOrLevel[1]);
                        lvlMax = Integer.parseInt(MobOrLevel[2]);

                        if (World.world.getMonstre(monsterID) == null
                                || World.world.getMonstre(monsterID).getGradeByLevel(lvlMin) == null
                                || World.world.getMonstre(monsterID).getGradeByLevel(lvlMax) == null) {
                            continue;
                        }
                        ValidMobGroup1 += monsterID + "," + lvlMin + ","
                                + lvlMax + ";";
                    }
                    if (ValidMobGroup1.isEmpty())
                        return true;
                    Monster.MobGroup group = new Monster.MobGroup(player.getCurMap().nextObjectId, map, player.getCurCell().getId(), ValidMobGroup1);
                    player.getCurMap().startFightVersusProtectors(player, group);
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 70://Park incarnam
                final MountPark park = (player.getCurMap().getId() != 10332 ? World.world.getMap((short) 8743) : World.world.getMap((short) 8848)).getMountPark();

                if (park != null) {
                    try {
                        park.getEtable().stream().filter(Objects::nonNull).forEach(mount -> mount.checkBaby(player, park));
                        park.getListOfRaising().stream().filter(integer -> World.world.getMountById(integer) != null).forEach(integer -> World.world.getMountById(integer).checkBaby(player, park));
                    } catch (Exception e) { e.printStackTrace(); }
                    player.openMountPark(park);
                }
                break;

			/*
			 * case 100://Donner l'abilit� 'args' � une dragodinde Dragodinde
			 * mount = perso.getMount(); World.world.addDragodinde(new
			 * Dragodinde(mount
			 * .getId(),mount.get_color(),mount.get_sexe(),mount
			 * .get_amour(),mount
			 * .get_endurance(),mount.get_level(),mount.get_exp
			 * (),mount.get_nom()
			 * ,mount.get_fatigue(),mount.get_energie(),mount.get_reprod
			 * (),mount.
			 * get_maturite(),mount.get_serenite(),mount.getItemsId(),mount
			 * .get_ancetres(),args));
			 * perso.setMount(World.world.getMountById(mount.getId()));
			 * SocketManager.GAME_SEND_Re_PACKET(perso, "+",
			 * World.world.getMountById(mount.getId()));
			 * SQLManager.UPDATE_MOUNT_INFOS(mount, false); break;
			 */

            case 100: //Donner l'abilit� 'args' � une dragodinde
                if (player.hasItemTemplate(361, 100, false)) {
                    player.removeByTemplateID(361, 100);
                    GameObject newObjAdded = World.world.getObjTemplate(9201).createNewItem(1, false);
                    if (!player.addObjetSimiler(newObjAdded, true, -1)) {
                        World.world.addGameObject(newObjAdded);
                        player.addObjet(newObjAdded);
                    }
                }
                break;

            case 102: //Marier des personnages
                GameMap map0 = player.getCurMap();
                if(map0.getCase(297).getPlayers() != null && map0.getCase(282).getPlayers() != null) {
                    if (map0.getCase(297).getPlayers().size() == 1 && map0.getCase(282).getPlayers().size() == 1) {
                        Player boy = (Player) map0.getCase(282).getPlayers().toArray()[0], girl = (Player) map0.getCase(297).getPlayers().toArray()[0];
                        boy.setBlockMovement(true);
                        girl.setBlockMovement(true);
                        World.world.priestRequest(boy, girl, player);
                    }
                }
                break;

            case 103: //Divorce
                if (player.getKamas() < 50000) {
                    return true;
                } else {
                    player.setKamas(player.getKamas() - 50000);
                    if (player.isOnline())
                        SocketManager.GAME_SEND_STATS_PACKET(player);
                    Player wife = World.world.getPlayer(player.getWife());
                    wife.Divorce();
                    player.Divorce();
                }
                break;

            case 104: //T�l�portation mine + Animation
                if (player.getCurMap().getId() != (short) 10257)
                    return true;

                ArrayList<Couple<Short, Integer>> arrays = new ArrayList<>();
                for (String i : args.split(";"))
                    arrays.add(new Couple<>(Short.parseShort(i.split(",")[0]), Integer.parseInt(i.split(",")[1])));

                Couple<Short, Integer> couple = arrays.get(Formulas.random.nextInt(arrays.size()));
                SocketManager.GAME_SEND_GA_PACKET(player.getGameClient(), "", "2", player.getId()
                        + "", "6");
                player.teleport(couple.first, couple.second);
                break;

            case 105: //Restat carac second pouvoir
                if (!player.hasItemTemplate(10563, 1, false)) {
                    player.sendMessage(player.getLang().trans("other.action.restat.carac"));
                    return true;
                } else {
                    player.removeByTemplateID(10563, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 10563 + "~" + 1);
                }

                if (player.getStatsParcho().getEffect(125) != 0
                        || player.getStatsParcho().getEffect(124) != 0
                        || player.getStatsParcho().getEffect(118) != 0
                        || player.getStatsParcho().getEffect(119) != 0
                        || player.getStatsParcho().getEffect(126) != 0
                        || player.getStats().getEffect(123) != 0) {
                    player.getStats().addOneStat(125, -player.getStats().getEffect(125)
                            + player.getStatsParcho().getEffect(125));
                    player.getStats().addOneStat(124, -player.getStats().getEffect(124)
                            + player.getStatsParcho().getEffect(124));
                    player.getStats().addOneStat(118, -player.getStats().getEffect(118)
                            + player.getStatsParcho().getEffect(118));
                    player.getStats().addOneStat(123, -player.getStats().getEffect(123)
                            + player.getStatsParcho().getEffect(123));
                    player.getStats().addOneStat(119, -player.getStats().getEffect(119)
                            + player.getStatsParcho().getEffect(119));
                    player.getStats().addOneStat(126, -player.getStats().getEffect(126)
                            + player.getStatsParcho().getEffect(126));
                    player.addCapital((player.getLevel() - 1) * 5
                            - player.getCapital());
                } else if (player.getStats().getEffect(125) == 101
                        && player.getStats().getEffect(124) == 101
                        && player.getStats().getEffect(118) == 101
                        && player.getStats().getEffect(123) == 101
                        && player.getStats().getEffect(119) == 101
                        && player.getStats().getEffect(126) == 101) {
                    player.getStats().addOneStat(125, -player.getStats().getEffect(125) + 101);
                    player.getStats().addOneStat(124, -player.getStats().getEffect(124) + 101);
                    player.getStats().addOneStat(118, -player.getStats().getEffect(118) + 101);
                    player.getStats().addOneStat(123, -player.getStats().getEffect(123) + 101);
                    player.getStats().addOneStat(119, -player.getStats().getEffect(119) + 101);
                    player.getStats().addOneStat(126, -player.getStats().getEffect(126) + 101);

                    player.getStatsParcho().addOneStat(125, 101);
                    player.getStatsParcho().addOneStat(124, 101);
                    player.getStatsParcho().addOneStat(118, 101);
                    player.getStatsParcho().addOneStat(123, 101);
                    player.getStatsParcho().addOneStat(119, 101);
                    player.getStatsParcho().addOneStat(126, 101);

                    player.addCapital((player.getLevel() - 1) * 5
                            - player.getCapital());
                } else {
                    player.sendMessage(player.getLang().trans("other.action.restat.carac.secondskill"));
                    return true;
                }

                SocketManager.GAME_SEND_STATS_PACKET(player);
                player.sendMessage(player.getLang().trans("other.action.restat.ok"));
                break;

            case 106:
                switch(this.args) {
                    case "1"://Remove spell
                        if(player.hasItemTemplate(15004, 1, false)) {
                            player.removeByTemplateID(15004, 1);
                            player.setExchangeAction(new ExchangeAction<>(ExchangeAction.FORGETTING_SPELL, 0));
                            SocketManager.GAME_SEND_FORGETSPELL_INTERFACE('+', player);
                        }
                        break;
                    case "2"://Restat without
                        if(player.hasItemTemplate(15006, 1, false)) {
                            player.removeByTemplateID(15006, 1);
                            player.getStats().addOneStat(125, -player.getStats().getEffect(125));
                            player.getStats().addOneStat(124, -player.getStats().getEffect(124));
                            player.getStats().addOneStat(118, -player.getStats().getEffect(118));
                            player.getStats().addOneStat(123, -player.getStats().getEffect(123));
                            player.getStats().addOneStat(119, -player.getStats().getEffect(119));
                            player.getStats().addOneStat(126, -player.getStats().getEffect(126));
                            player.addCapital((player.getLevel() - 1) * 5 - player.getCapital());
                            player.getStatsParcho().getEffects().clear();
                            SocketManager.GAME_SEND_STATS_PACKET(player);
                        }
                        break;
                    case "3"://Restat with
                        if(player.hasItemTemplate(15005, 1, false)) {
                            player.removeByTemplateID(15005, 1);
                            if (player.getStatsParcho().getEffect(125) != 0 || player.getStatsParcho().getEffect(124) != 0 || player.getStatsParcho().getEffect(118) != 0
                                    || player.getStatsParcho().getEffect(119) != 0 || player.getStatsParcho().getEffect(126) != 0 || player.getStatsParcho().getEffect(123) != 0) {
                                player.getStats().addOneStat(125, -player.getStats().getEffect(125) + player.getStatsParcho().getEffect(125));
                                player.getStats().addOneStat(124, -player.getStats().getEffect(124) + player.getStatsParcho().getEffect(124));
                                player.getStats().addOneStat(118, -player.getStats().getEffect(118) + player.getStatsParcho().getEffect(118));
                                player.getStats().addOneStat(123, -player.getStats().getEffect(123) + player.getStatsParcho().getEffect(123));
                                player.getStats().addOneStat(119, -player.getStats().getEffect(119) + player.getStatsParcho().getEffect(119));
                                player.getStats().addOneStat(126, -player.getStats().getEffect(126) + player.getStatsParcho().getEffect(126));
                                player.addCapital((player.getLevel() - 1) * 5 - player.getCapital());
                                SocketManager.GAME_SEND_STATS_PACKET(player);
                            } else if (player.getStats().getEffect(125) == 101 && player.getStats().getEffect(124) == 101 && player.getStats().getEffect(118) == 101
                                    && player.getStats().getEffect(123) == 101 && player.getStats().getEffect(119) == 101 && player.getStats().getEffect(126) == 101) {
                                player.getStats().addOneStat(125, -player.getStats().getEffect(125) + 101);
                                player.getStats().addOneStat(124, -player.getStats().getEffect(124) + 101);
                                player.getStats().addOneStat(118, -player.getStats().getEffect(118) + 101);
                                player.getStats().addOneStat(123, -player.getStats().getEffect(123) + 101);
                                player.getStats().addOneStat(119, -player.getStats().getEffect(119) + 101);
                                player.getStats().addOneStat(126, -player.getStats().getEffect(126) + 101);
                                player.getStatsParcho().getEffects().clear();
                                player.getStatsParcho().addOneStat(125, 101);
                                player.getStatsParcho().addOneStat(124, 101);
                                player.getStatsParcho().addOneStat(118, 101);
                                player.getStatsParcho().addOneStat(123, 101);
                                player.getStatsParcho().addOneStat(119, 101);
                                player.getStatsParcho().addOneStat(126, 101);
                                player.addCapital((player.getLevel() - 1) * 5 - player.getCapital());
                                SocketManager.GAME_SEND_STATS_PACKET(player);
                            } else {
                                player.removeByTemplateID(15000, 500);
                                player.getStats().addOneStat(125, -player.getStats().getEffect(125));
                                player.getStats().addOneStat(124, -player.getStats().getEffect(124));
                                player.getStats().addOneStat(118, -player.getStats().getEffect(118));
                                player.getStats().addOneStat(123, -player.getStats().getEffect(123));
                                player.getStats().addOneStat(119, -player.getStats().getEffect(119));
                                player.getStats().addOneStat(126, -player.getStats().getEffect(126));
                                player.addCapital((player.getLevel() - 1) * 5 - player.getCapital());
                                player.getStatsParcho().getEffects().clear();
                                SocketManager.GAME_SEND_STATS_PACKET(player);
                            }
                        }
                        break;
                }
                break;

            case 116://EPO donner de la nourriture � son familier
                GameObject EPO = World.world.getGameObject(itemID);
                if (EPO == null)
                    return true;
                GameObject pets = player.getObjetByPos(Constant.ITEM_POS_FAMILIER);
                if (pets == null)
                    return true;
                PetEntry MyPets = World.world.getPetsEntry(pets.getGuid());
                if (MyPets == null)
                    return true;
                if (EPO.getTemplate().getConditions().contains(pets.getTemplate().getId()
                        + ""))
                    MyPets.giveEpo(player);
                break;

            case 170:// Donner titre
                try {
                    byte title1 = (byte) Integer.parseInt(args);
                    target = World.world.getPlayerByName(player.getName());
                    target.setCurrentTitle(title1);
                    SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.title"));
                    SocketManager.GAME_SEND_STATS_PACKET(player);
                    ((PlayerData) DatabaseManager.get(PlayerData.class)).update(player);
                    if (target.getFight() == null)
                        SocketManager.GAME_SEND_ALTER_GM_PACKET(player.getCurMap(), player);
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 171: // Alignement avec condition
                short type2 = (short) Integer.parseInt(args.split(",")[0]);
                int mapId2 = Integer.parseInt(args.split(",")[1]);
                if (player.getAlignment() > 0)
                    return true;
                if (type2 == 1 && (player.getCurMap().getId() == mapId2)) {
                    //if (player.hasItemTemplate(42, 10, false)) {
                    //    player.removeByTemplateID(42, 10);
                    //    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 10 + "~" + 42);
                        player.modifAlignement((byte) 1);
                    //}
                }
                if (type2 == 2 && (player.getCurMap().getId() == mapId2)) {
                    //if (player.hasItemTemplate(95, 10, false)) {
                    //    player.removeByTemplateID(95, 10);
                    //    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 10 + "~" + 95);
                        player.modifAlignement((byte) 2);
                    //}
                }
                break;

            case 172: //Bricoleur avec condition
                int mapId4 = Integer.parseInt(args);
                if (player.getCurMap().getId() != mapId4)
                    return true;
                if (player.totalJobBasic() > 2)//On compte les m�tiers d�ja acquis si c'est sup�rieur a 2 on ignore
                {
                    SocketManager.GAME_SEND_Im_PACKET(player, "19");
                    return true;
                }
                if (player.hasItemTemplate(459, 20, false)
                        && player.hasItemTemplate(7657, 15, false)) {
                    player.removeByTemplateID(459, 20);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 459 + "~"
                            + 20);
                    player.removeByTemplateID(7657, 15);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 7657
                            + "~" + 15);
                    player.learnJob(World.world.getMetier(65));
                } else {
                    SocketManager.GAME_SEND_Im_PACKET(player, "14|43");
                    return true;
                }
                break;

            case 200: //Animation + Condition voyage Ile Minotor
                long k = player.getKamas();
                int cost = player.getCurMap().getId() == 9520 ? 100 : 200;
                if(k > cost) {
                    player.addKamas(- cost);
                    SocketManager.GAME_SEND_STATS_PACKET(player);
                    SocketManager.GAME_SEND_Im_PACKET(player, "046;" + 100);
                    if(cost == 100) player.teleport((short) 9541, 407);
                    else player.teleport((short) 9520, 282);
                } else {
                    SocketManager.GAME_SEND_Im_PACKET(player, "182");
                }
                break;

            case 219://Sortie donjon wabbit
                if (player.getCurMap().getId() != 1780)
                    return true;
                int type11 = Integer.parseInt(args);
                if (type11 == 1) {
                    GameObject newObjAdded = World.world.getObjTemplate(970).createNewItem(1, false);
                    if (!player.addObjetSimiler(newObjAdded, true, -1)) {
                        World.world.addGameObject(newObjAdded);
                        player.addObjet(newObjAdded);
                    }
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                            + 970);
                    player.teleport((short) 844, 212);
                } else if (type11 == 2) {
                    GameObject newObjAdded = World.world.getObjTemplate(969).createNewItem(1, false);
                    if (!player.addObjetSimiler(newObjAdded, true, -1)) {
                        World.world.addGameObject(newObjAdded);
                        player.addObjet(newObjAdded);
                    }
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                            + 969);
                    player.teleport((short) 844, 212);
                } else if (type11 == 3) {
                    GameObject newObjAdded = World.world.getObjTemplate(971).createNewItem(1, false);
                    if (!player.addObjetSimiler(newObjAdded, true, -1)) {
                        World.world.addGameObject(newObjAdded);
                        player.addObjet(newObjAdded);
                    }
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                            + 971);
                    player.teleport((short) 844, 212);
                }
                break;

            case 220:// IDitem,quantit� pour IDitem's,quantit� pour teleport
                try {
                    String remove0 = args.split(";")[0];
                    String add0 = args.split(";")[1];
                    String add1 = args.split(";")[4];
                    int obj0 = Integer.parseInt(remove0.split(",")[0]);
                    int qua0 = Integer.parseInt(remove0.split(",")[1]);
                    int newObj1 = Integer.parseInt(add0.split(",")[0]);
                    int newQua1 = Integer.parseInt(add0.split(",")[1]);
                    int newObj2 = Integer.parseInt(add1.split(",")[0]);
                    int newQua2 = Integer.parseInt(add1.split(",")[1]);
                    if (player.hasItemTemplate(obj0, qua0, false)) {
                        player.removeByTemplateID(obj0, qua0);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua0
                                + "~" + obj0);
                        SocketManager.GAME_SEND_Im_PACKET(player, "021;"
                                + newQua1 + "~" + newObj1);
                        SocketManager.GAME_SEND_Im_PACKET(player, "021;"
                                + newQua2 + "~" + newObj2);
                        GameObject newObjAdded = World.world.getObjTemplate(newObj1).createNewItem(newQua1, false);
                        if (!player.addObjetSimiler(newObjAdded, true, -1)) {
                            World.world.addGameObject(newObjAdded);
                            player.addObjet(newObjAdded);
                        }
                        GameObject newObjAdded1 = World.world.getObjTemplate(newObj2).createNewItem(newQua2, false);
                        if (!player.addObjetSimiler(newObjAdded1, true, -1)) {
                            World.world.addGameObject(newObjAdded1);
                            player.addObjet(newObjAdded1);
                        }
                    } else {
                        SocketManager.GAME_SEND_Im_PACKET(player, "14|43");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                return true;

            case 221:// IDitem,quantit� pour IDitem's,quantit� pour teleport
                try {
                    String remove0 = args.split(";")[0];
                    String remove1 = args.split(";")[1];
                    String add = args.split(";")[4];
                    int obj0 = Integer.parseInt(remove0.split(",")[0]);
                    int qua0 = Integer.parseInt(remove0.split(",")[1]);
                    int obj1 = Integer.parseInt(remove1.split(",")[0]);
                    int qua1 = Integer.parseInt(remove1.split(",")[1]);
                    int newObj1 = Integer.parseInt(add.split(",")[0]);
                    int newQua1 = Integer.parseInt(add.split(",")[1]);
                    if (player.hasItemTemplate(obj0, qua0, false)
                            && player.hasItemTemplate(obj1, qua1, false)) {
                        player.removeByTemplateID(obj0, qua0);
                        player.removeByTemplateID(obj1, qua1);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua0
                                + "~" + obj0);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua1
                                + "~" + obj1);
                        SocketManager.GAME_SEND_Im_PACKET(player, "021;"
                                + newQua1 + "~" + newObj1);
                        GameObject newObjAdded = World.world.getObjTemplate(newObj1).createNewItem(newQua1, false);
                        if (!player.addObjetSimiler(newObjAdded, true, -1)) {
                            World.world.addGameObject(newObjAdded);
                            player.addObjet(newObjAdded);
                        }
                    } else {
                        SocketManager.GAME_SEND_Im_PACKET(player, "14|43");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                return true;

            case 222:// IDitem,quantit� pour IDitem's,quantit� pour teleport
                try {
                    String remove0 = args.split(";")[0];
                    String remove1 = args.split(";")[1];
                    String remove2 = args.split(";")[2];
                    String remove3 = args.split(";")[3];
                    String add = args.split(";")[4];
                    int verifMapId = Integer.parseInt(args.split(";")[5]);
                    int obj0 = Integer.parseInt(remove0.split(",")[0]);
                    int qua0 = Integer.parseInt(remove0.split(",")[1]);
                    int obj1 = Integer.parseInt(remove1.split(",")[0]);
                    int qua1 = Integer.parseInt(remove1.split(",")[1]);
                    int obj2 = Integer.parseInt(remove2.split(",")[0]);
                    int qua2 = Integer.parseInt(remove2.split(",")[1]);
                    int obj3 = Integer.parseInt(remove3.split(",")[0]);
                    int qua3 = Integer.parseInt(remove3.split(",")[1]);
                    int mapID = Integer.parseInt(add.split(",")[0]);
                    int cellID = Integer.parseInt(add.split(",")[1]);

                    if (player.hasItemTemplate(obj0, qua0, false)
                            && player.hasItemTemplate(obj1, qua1, false)
                            && player.hasItemTemplate(obj2, qua2, false)
                            && player.hasItemTemplate(obj3, qua3, false)) {
                        player.removeByTemplateID(obj0, qua0);
                        player.removeByTemplateID(obj1, qua1);
                        player.removeByTemplateID(obj2, qua2);
                        player.removeByTemplateID(obj3, qua3);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua0
                                + "~" + obj0);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua1
                                + "~" + obj1);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua2
                                + "~" + obj2);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua3
                                + "~" + obj3);
                        if (player.getFight() != null
                                || player.getCurMap().getId() != verifMapId)
                            return true;
                        player.teleport((short) mapID, cellID);
                    } else {
                        SocketManager.GAME_SEND_Im_PACKET(player, "14|43");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                return true;

            case 223:// IDitem,quantit� pour IDitem's,quantit�
                try {
                    String remove0 = args.split(";")[0];
                    String remove1 = args.split(";")[1];
                    String remove2 = args.split(";")[2];
                    String remove3 = args.split(";")[3];
                    String remove4 = args.split(";")[4];
                    String add = args.split(";")[5];
                    int obj0 = Integer.parseInt(remove0.split(",")[0]);
                    int qua0 = Integer.parseInt(remove0.split(",")[1]);
                    int obj1 = Integer.parseInt(remove1.split(",")[0]);
                    int qua1 = Integer.parseInt(remove1.split(",")[1]);
                    int obj2 = Integer.parseInt(remove2.split(",")[0]);
                    int qua2 = Integer.parseInt(remove2.split(",")[1]);
                    int obj3 = Integer.parseInt(remove3.split(",")[0]);
                    int qua3 = Integer.parseInt(remove3.split(",")[1]);
                    int obj4 = Integer.parseInt(remove4.split(",")[0]);
                    int qua4 = Integer.parseInt(remove4.split(",")[1]);
                    int newItem = Integer.parseInt(add.split(",")[0]);
                    int quaNewItem = Integer.parseInt(add.split(",")[1]);
                    if (player.hasItemTemplate(obj0, qua0, false)
                            && player.hasItemTemplate(obj1, qua1, false)
                            && player.hasItemTemplate(obj2, qua2, false)
                            && player.hasItemTemplate(obj3, qua3, false)
                            && player.hasItemTemplate(obj4, qua4, false)) {
                        player.removeByTemplateID(obj0, qua0);
                        player.removeByTemplateID(obj1, qua1);
                        player.removeByTemplateID(obj2, qua2);
                        player.removeByTemplateID(obj3, qua3);
                        player.removeByTemplateID(obj4, qua4);
                        GameObject newObjAdded = World.world.getObjTemplate(newItem).createNewItem(quaNewItem, false);
                        if (!player.addObjetSimiler(newObjAdded, true, -1)) {
                            World.world.addGameObject(newObjAdded);
                            player.addObjet(newObjAdded);
                        }
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua0
                                + "~" + obj0);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua1
                                + "~" + obj1);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua2
                                + "~" + obj2);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua3
                                + "~" + obj3);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua4
                                + "~" + obj4);
                        SocketManager.GAME_SEND_Im_PACKET(player, "021;"
                                + quaNewItem + "~" + newItem);
                    } else {
                        SocketManager.GAME_SEND_Im_PACKET(player, "14|43");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                return true;

            case 224:// IDitem,quantit� pour IDitem's,quantit�
                try {
                    String remove0 = args.split(";")[0];
                    String remove1 = args.split(";")[1];
                    String remove2 = args.split(";")[2];
                    String remove3 = args.split(";")[3];
                    String add = args.split(";")[4];
                    int obj0 = Integer.parseInt(remove0.split(",")[0]);
                    int qua0 = Integer.parseInt(remove0.split(",")[1]);
                    int obj1 = Integer.parseInt(remove1.split(",")[0]);
                    int qua1 = Integer.parseInt(remove1.split(",")[1]);
                    int obj2 = Integer.parseInt(remove2.split(",")[0]);
                    int qua2 = Integer.parseInt(remove2.split(",")[1]);
                    int obj3 = Integer.parseInt(remove3.split(",")[0]);
                    int qua3 = Integer.parseInt(remove3.split(",")[1]);
                    int newItem = Integer.parseInt(add.split(",")[0]);
                    int quaNewItem = Integer.parseInt(add.split(",")[1]);
                    if (player.hasItemTemplate(obj0, qua0, false)
                            && player.hasItemTemplate(obj1, qua1, false)
                            && player.hasItemTemplate(obj2, qua2, false)
                            && player.hasItemTemplate(obj3, qua3, false)) {
                        player.removeByTemplateID(obj0, qua0);
                        player.removeByTemplateID(obj1, qua1);
                        player.removeByTemplateID(obj2, qua2);
                        player.removeByTemplateID(obj3, qua3);
                        GameObject newObjAdded = World.world.getObjTemplate(newItem).createNewItem(quaNewItem, false);
                        if (!player.addObjetSimiler(newObjAdded, true, -1)) {
                            World.world.addGameObject(newObjAdded);
                            player.addObjet(newObjAdded);
                        }
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua0
                                + "~" + obj0);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua1
                                + "~" + obj1);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua2
                                + "~" + obj2);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua3
                                + "~" + obj3);
                        SocketManager.GAME_SEND_Im_PACKET(player, "021;"
                                + quaNewItem + "~" + newItem);
                    } else {
                        SocketManager.GAME_SEND_Im_PACKET(player, "14|43");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                return true;

            case 225:// IDitem,quantit� pour IDitem's,quantit�
                try {
                    String remove0 = args.split(";")[0];
                    String remove1 = args.split(";")[1];
                    String remove2 = args.split(";")[2];
                    String add = args.split(";")[3];
                    int obj0 = Integer.parseInt(remove0.split(",")[0]);
                    int qua0 = Integer.parseInt(remove0.split(",")[1]);
                    int obj1 = Integer.parseInt(remove1.split(",")[0]);
                    int qua1 = Integer.parseInt(remove1.split(",")[1]);
                    int obj2 = Integer.parseInt(remove2.split(",")[0]);
                    int qua2 = Integer.parseInt(remove2.split(",")[1]);
                    int newItem = Integer.parseInt(add.split(",")[0]);
                    int quaNewItem = Integer.parseInt(add.split(",")[1]);
                    if (player.hasItemTemplate(obj0, qua0, false)
                            && player.hasItemTemplate(obj1, qua1, false)
                            && player.hasItemTemplate(obj2, qua2, false)) {
                        player.removeByTemplateID(obj0, qua0);
                        player.removeByTemplateID(obj1, qua1);
                        player.removeByTemplateID(obj2, qua2);
                        GameObject newObjAdded = World.world.getObjTemplate(newItem).createNewItem(quaNewItem, false);
                        if (!player.addObjetSimiler(newObjAdded, true, -1)) {
                            World.world.addGameObject(newObjAdded);
                            player.addObjet(newObjAdded);
                        }
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua0
                                + "~" + obj0);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua1
                                + "~" + obj1);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua2
                                + "~" + obj2);
                        SocketManager.GAME_SEND_Im_PACKET(player, "021;"
                                + quaNewItem + "~" + newItem);
                    } else {
                        SocketManager.GAME_SEND_Im_PACKET(player, "14|43");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 226://Animation + Condition voyage Ile Moon
                if (player.hasItemTemplate(1089, 1, false) && player.hasEquiped(1021)
                        && player.hasEquiped(1019)
                        && player.getCurMap().getId() == 1014) {
                    player.removeByTemplateID(1019, 1);
                    player.removeByTemplateID(1021, 1);
                    player.removeByTemplateID(1089, 1);
                    GameObject newObj1 = World.world.getObjTemplate(1020).createNewItem(1, false);
                    if (!player.addObjetSimiler(newObj1, true, -1)) {
                        World.world.addGameObject(newObj1);
                        player.addObjet(newObj1);
                    }
                    GameObject newObj2 = World.world.getObjTemplate(1022).createNewItem(1, false);
                    if (!player.addObjetSimiler(newObj2, true, -1)) {
                        World.world.addGameObject(newObj2);
                        player.addObjet(newObj2);
                    }
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 1089);
                    SocketManager.GAME_SEND_GA_PACKET(player.getGameClient(), "", "2", player.getId()
                            + "", "1");
                    player.teleport((short) 437, 411);
                } else {
                    SocketManager.GAME_SEND_Im_PACKET(player, "14|43");
                    return true;
                }
                break;

            case 227://Animation + Condition voyage Ile Wabbit
                long pKamas = player.getKamas();
                if (pKamas >= 500 && player.getCurMap().getId() == 167) {
                    long pNewKamas = pKamas - 500;
                    if (pNewKamas < 0)
                        pNewKamas = 0;
                    player.setKamas(pNewKamas);
                    if (player.isOnline())
                        SocketManager.GAME_SEND_STATS_PACKET(player);
                    SocketManager.GAME_SEND_Im_PACKET(player, "046;" + 500);
                    SocketManager.GAME_SEND_GA_PACKET(player.getGameClient(), "", "2", player.getId()
                            + "", "2");
                    player.teleport((short) 833, 141);
                } else {
                    SocketManager.GAME_SEND_Im_PACKET(player, "182");
                }
                break;

            case 228://Faire animation Hors Combat
                try {
                    int AnimationId = Integer.parseInt(args);
                    Animation animation = World.world.getAnimation(AnimationId);
                    if (player.getFight() != null)
                        return true;
                    player.changeOrientation(1);
                    SocketManager.GAME_SEND_GA_PACKET_TO_MAP(player.getCurMap(), "0", 228, player.getId()
                            + ";" + cellid + "," + animation.prepareToGA(), "");
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 229://Animation d'incarnam � astrub
                short map = Constant.getClassStatueMap(player.getClasse());
                int cell = Constant.getClassStatueCell(player.getClasse());
                SocketManager.GAME_SEND_GA_PACKET(player.getGameClient(), "", "2", player.getId() + "", "7");
                player.teleport(map, cell);
                player.set_savePos(map + "," + cell);
                SocketManager.GAME_SEND_Im_PACKET(player, "06");
                break;

            case 230://Point Boutique  
                try {
                    int pts = Integer.parseInt(args);
                    int ptsTotal = player.getAccount().getPoints() + pts;
                    if (ptsTotal < 0)
                        ptsTotal = 0;
                    if (ptsTotal > 50000)
                        ptsTotal = 50000;
                    player.getAccount().setPoints(ptsTotal);
                    if (player.isOnline())
                        SocketManager.GAME_SEND_STATS_PACKET(player);
                    SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.shop", pts, ptsTotal));
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 231:// IDitem,quantit� pour IDitem's,quantit�
                try {
                    String remove = args.split(";")[0];
                    String add = args.split(";")[1];
                    int obj = Integer.parseInt(remove.split(",")[0]);
                    int qua = Integer.parseInt(remove.split(",")[1]);
                    int newItem = Integer.parseInt(add.split(",")[0]);
                    int quaNewItem = Integer.parseInt(add.split(",")[1]);
                    if (player.hasItemTemplate(obj, qua, false)) {
                        player.removeByTemplateID(obj, qua);
                        GameObject newObjAdded = World.world.getObjTemplate(newItem).createNewItem(quaNewItem, false);
                        if (!player.addObjetSimiler(newObjAdded, true, -1)) {
                            World.world.addGameObject(newObjAdded);
                            player.addObjet(newObjAdded);
                        }
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua
                                + "~" + obj);
                        SocketManager.GAME_SEND_Im_PACKET(player, "021;"
                                + quaNewItem + "~" + newItem);
                    } else {
                        SocketManager.GAME_SEND_Im_PACKET(player, "14|43");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 232://startFigthVersusMonstres args : monsterID,monsterLevel| ...
                if (player.getFight() != null)
                    return true;
                String ValidMobGroup2 = "";
                int pMap = player.getCurMap().getId();
                if (pMap == 10131 || pMap == 10132 || pMap == 10133
                        || pMap == 10134 || pMap == 10135 || pMap == 10136
                        || pMap == 10137 || pMap == 10138) {
                    try {
                        for (String MobAndLevel : args.split("\\|")) {
                            int monsterID = -1;
                            int monsterLevel = -1;
                            String[] MobOrLevel = MobAndLevel.split(",");
                            monsterID = Integer.parseInt(MobOrLevel[0]);
                            monsterLevel = Integer.parseInt(MobOrLevel[1]);

                            if (World.world.getMonstre(monsterID) == null
                                    || World.world.getMonstre(monsterID).getGradeByLevel(monsterLevel) == null) {
                                continue;
                            }
                            ValidMobGroup2 += monsterID + "," + monsterLevel
                                    + "," + monsterLevel + ";";
                        }
                        if (ValidMobGroup2.isEmpty())
                            return true;
                        Monster.MobGroup group = new Monster.MobGroup(player.getCurMap().nextObjectId, player.getCurMap(), player.getCurCell().getId(), ValidMobGroup2);
                        player.getCurMap().startFightVersusMonstres(player, group);// Si bug startfight, voir "//Respawn d'un groupe fix" dans fight.java
                    } catch (Exception e) {
                        e.printStackTrace();
                        GameServer.a();
                    }
                } else {
                    SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.startfightversusmonster"));
                }
                break;

            case 233: //Pierre d'ame en ar�ne
                try {
                    int tID = Integer.parseInt(args.split(",")[0]);
                    int count = Integer.parseInt(args.split(",")[1]);
                    boolean send = true;
                    if (args.split(",").length > 2)
                        send = args.split(",")[2].equals("1");
                    int pMap2 = player.getCurMap().getId();
                    if (pMap2 == 10131 || pMap2 == 10132 || pMap2 == 10133
                            || pMap2 == 10134 || pMap2 == 10135
                            || pMap2 == 10136 || pMap2 == 10137
                            || pMap2 == 10138) {
                        //Si on ajoute
                        if (count > 0) {
                            ObjectTemplate T = World.world.getObjTemplate(tID);
                            if (T == null)
                                return true;
                            GameObject O = T.createNewItem(count, false);
                            //Si retourne true, on l'ajoute au monde
                            if (player.addObjet(O, true))
                                World.world.addGameObject(O);
                        } else {
                            player.removeByTemplateID(tID, -count);
                        }
                        //Si en ligne (normalement oui)
                        if (player.isOnline())//on envoie le packet qui indique l'ajout//retrait d'un item
                        {
                            SocketManager.GAME_SEND_Ow_PACKET(player);
                            if (send) {
                                if (count >= 0) {
                                    SocketManager.GAME_SEND_Im_PACKET(player, "021;"
                                            + count + "~" + tID);
                                } else if (count < 0) {
                                    SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                            + -count + "~" + tID);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 234: //Ajout d'un objet en fonction de la map
                int IdObj = Short.parseShort(args.split(";")[0]);
                int MapId = Integer.parseInt(args.split(";")[1]);
                if (player.getCurMap().getId() != MapId)
                    return true;
                if (!player.hasItemTemplate(IdObj, 1, false)) {
                    GameObject newObjAdded = World.world.getObjTemplate(IdObj).createNewItem(1, false);
                    if (!player.addObjetSimiler(newObjAdded, true, -1)) {
                        World.world.addGameObject(newObjAdded);
                        player.addObjet(newObjAdded);
                    }
                } else {
                    SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.objalereadyexit"));
                }
                break;

            case 235:// IDitem,quantit� pour IDitem's,quantit�
                if (player.getCurMap().getId() == 713) {
                    if (player.hasItemTemplate(757, 1, false)
                            && player.hasItemTemplate(368, 1, false)
                            && player.hasItemTemplate(369, 1, false)
                            && !player.hasItemTemplate(960, 1, false)) {
                        player.removeByTemplateID(757, 1);
                        player.removeByTemplateID(368, 1);
                        player.removeByTemplateID(369, 1);

                        GameObject newObjAdded = World.world.getObjTemplate(960).createNewItem(1, false);
                        if (!player.addObjetSimiler(newObjAdded, true, -1)) {
                            World.world.addGameObject(newObjAdded);
                            player.addObjet(newObjAdded);
                        }
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1
                                + "~" + 757);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1
                                + "~" + 368);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1
                                + "~" + 369);
                        SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1
                                + "~" + 960);
                    } else {
                        SocketManager.GAME_SEND_Im_PACKET(player, "14|43");
                    }
                }
                break;

            case 300://Sort arakne
                if (player.getCurMap().getId() == 1559 && player.hasItemTemplate(973, 1, false)) {
                    player.removeByTemplateID(973, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 973);
                    player.learnSpell(370, 1, true, true, true);
                }
                break;

			/*
			 * case 92://bonbon try { if(perso.getCandyId() != 0)
			 * SocketManager.send(perso, "OR1"); int t =
			 * World.world.getGameObject(itemID).getTemplate().getId();
			 * perso.removeByTemplateID(t, 1); perso.setCandy(t);
			 * SocketManager.GAME_SEND_STATS_PACKET(perso); }catch (Exception e)
			 * {} break;
			 */

            case 239://Ouvrir l'interface d'oublie de sort
                player.setExchangeAction(new ExchangeAction<>(ExchangeAction.FORGETTING_SPELL, 0));
                SocketManager.GAME_SEND_FORGETSPELL_INTERFACE('+', player);
                break;

            case 241:
                if (player.getKamas() >= 10
                        && player.getCurMap().getId() == 6863) {
                    if (player.hasItemTemplate(6653, 1, false)) {
                        String date = player.getItemTemplate(6653, 1).getTxtStat().get(Constant.STATS_DATE);
                        try {
                            long timeStamp = Long.parseLong(date.split("#")[3]);

                            if (System.currentTimeMillis() - timeStamp <= 86400000) {
                                SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.ticket.good"));
                                return true;
                            } else {
                                SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.ticket.outdated"));
                                player.removeByTemplateID(6653, 1);
                            }
                        } catch (Exception e) {
                            SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.ticket.outdated"));
                            player.removeByTemplateID(6653, 1);
                        }
                    }
                    long rK = player.getKamas() - 10;
                    if (rK < 0)
                        rK = 0;
                    player.setKamas(rK);
                    if (player.isOnline())
                        SocketManager.GAME_SEND_STATS_PACKET(player);
                    ObjectTemplate OT = World.world.getObjTemplate(6653);
                    GameObject obj = OT.createNewItem(1, false);
                    if (player.addObjet(obj, true))//Si le joueur n'avait pas d'item similaire
                        World.world.addGameObject(obj);
                    obj.refreshStatsObjet("325#0#0#"
                            + System.currentTimeMillis());
                    ((PlayerData) DatabaseManager.get(PlayerData.class)).update(player);
                    SocketManager.GAME_SEND_Ow_PACKET(player);
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                            + 6653);
                }
                break;

            /** Ile Moon **/
            case 450: //Ortimus
                if (player.getCurMap().getId() == 1844
                        && player.getKamas() >= 5000
                        && player.hasItemTemplate(363, 5, false)) {
                    player.removeByTemplateID(363, 5);
                    long rK = player.getKamas() - 5000;
                    if (rK < 0)
                        rK = 0;
                    player.setKamas(rK);
                    if (player.isOnline())
                        SocketManager.GAME_SEND_STATS_PACKET(player);
                    GameObject newObjAdded = World.world.getObjTemplate(998).createNewItem(1, false);
                    if (!player.addObjetSimiler(newObjAdded, true, -1)) {
                        World.world.addGameObject(newObjAdded);
                        player.addObjet(newObjAdded);
                    }
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                            + 998);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 5 + "~"
                            + 363);
                } else {
                    SocketManager.GAME_SEND_Im_PACKET(player, "14|43");
                }
                break;

            case 451: //Employ� de l'agence Touriste
                if (player.getKamas() >= 200
                        && player.getCurMap().getId() == 436) {
                    long rK = player.getKamas() - 200;
                    if (rK < 0)
                        rK = 0;
                    player.setKamas(rK);
                    if (player.isOnline())
                        SocketManager.GAME_SEND_STATS_PACKET(player);
                    GameObject newObjAdded = World.world.getObjTemplate(1004).createNewItem(1, false);
                    if (!player.addObjetSimiler(newObjAdded, true, -1)) {
                        World.world.addGameObject(newObjAdded);
                        player.addObjet(newObjAdded);
                    }
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                            + 1004);
                } else {
                    SocketManager.GAME_SEND_Im_PACKET(player, "14|43");
                }
                break;

            case 452: //Kib Roche
                if (player.hasItemTemplate(1000, 6, false)
                        && player.hasItemTemplate(1003, 1, false)
                        && player.hasItemTemplate(1018, 10, false)
                        && player.hasItemTemplate(998, 1, false)
                        && player.hasItemTemplate(1002, 1, false)
                        && player.hasItemTemplate(999, 1, false)
                        && player.hasItemTemplate(1004, 4, false)
                        && player.hasItemTemplate(1001, 2, false)
                        && player.getCurMap().getId() == 437) {
                    player.removeByTemplateID(1000, 6);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 6 + "~"
                            + 1000);
                    player.removeByTemplateID(1003, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 1003);
                    player.removeByTemplateID(1018, 10);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 10 + "~"
                            + 1018);
                    player.removeByTemplateID(998, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 998);
                    player.removeByTemplateID(1002, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 1002);
                    player.removeByTemplateID(999, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 99);
                    player.removeByTemplateID(1004, 4);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 4 + "~"
                            + 1004);
                    player.removeByTemplateID(1001, 2);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 2 + "~"
                            + 1001);
                    GameObject newObjAdded = World.world.getObjTemplate(6716).createNewItem(1, false);
                    if (!player.addObjetSimiler(newObjAdded, true, -1)) {
                        World.world.addGameObject(newObjAdded);
                        player.addObjet(newObjAdded);
                    }
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                            + 6716);
                    player.teleport((short) 1701, 247);
                } else {
                    SocketManager.GAME_SEND_Im_PACKET(player, "14|43");
                }
                break;

            case 453: //Kanniboul cuisininer : fonctionnel avec les 4 carapaces
                if (player.hasItemTemplate(1010, 1, false)
                        && player.hasItemTemplate(1011, 1, false)
                        && player.hasItemTemplate(1012, 1, false)
                        && player.hasItemTemplate(1013, 1, false)
                        && player.getCurMap().getId() == 1714) {
                    player.removeByTemplateID(1010, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 1010);
                    player.removeByTemplateID(1011, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 1011);
                    player.removeByTemplateID(1012, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 1012);
                    player.removeByTemplateID(1013, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 1013);
                    player.teleport((short) 1766, 332);
                } else {
                    SocketManager.GAME_SEND_Im_PACKET(player, "14|43");
                }
                break;

            case 454: //masque kanniboul : fonctionnel
                if (player.hasEquiped(1088) && player.getCurMap().getId() == 1764) {
                    player.teleport((short) 1765, 226);
                } else {
                    SocketManager.GAME_SEND_Im_PACKET(player, "14|43");
                }
                break;

            case 455://Iss� Heau : peinture noire : fonctionnel
                if (player.hasItemTemplate(1006, 1, false)
                        && player.hasItemTemplate(1007, 1, false)
                        && player.hasItemTemplate(1008, 1, false)
                        && player.hasItemTemplate(1009, 1, false)
                        && player.getCurMap().getId() == 1838) {
                    player.removeByTemplateID(1006, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 1006);
                    player.removeByTemplateID(1007, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 1007);
                    player.removeByTemplateID(1008, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 1008);
                    player.removeByTemplateID(1009, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 1009);
                    GameObject newObjAdded = World.world.getObjTemplate(1086).createNewItem(1, false);
                    if (!player.addObjetSimiler(newObjAdded, true, -1)) {
                        World.world.addGameObject(newObjAdded);
                        player.addObjet(newObjAdded);
                    }
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                            + 1086);
                } else {
                    SocketManager.GAME_SEND_Im_PACKET(player, "14|43");
                }
                break;

            case 456: //Obtention du masque kanniboul : fonctionnel
                if (player.hasItemTemplate(1014, 1, false)
                        && player.hasItemTemplate(1015, 1, false)
                        && player.hasItemTemplate(1016, 1, false)
                        && player.hasItemTemplate(1017, 1, false)
                        && player.hasItemTemplate(1086, 1, false)
                        && player.getCurMap().getId() == 425) {
                    player.removeByTemplateID(1014, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 1014);
                    player.removeByTemplateID(1015, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 1015);
                    player.removeByTemplateID(1016, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 1016);
                    player.removeByTemplateID(1017, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 1017);
                    player.removeByTemplateID(1086, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 1086);
                    GameObject newObjAdded = World.world.getObjTemplate(1088).createNewItem(1, false);
                    if (!player.addObjetSimiler(newObjAdded, true, -1)) {
                        World.world.addGameObject(newObjAdded);
                        player.addObjet(newObjAdded);
                    }
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                            + 1088);

                    NpcQuestion quest = World.world.getNPCQuestion(577);
                    if (quest == null) {
                        SocketManager.GAME_SEND_END_DIALOG_PACKET(player.getGameClient());
                        player.setExchangeAction(null);
                        return true;
                    }
                    try {
                        SocketManager.GAME_SEND_QUESTION_PACKET(player.getGameClient(), quest.parse(player));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    SocketManager.GAME_SEND_Im_PACKET(player, "14|43");
                }
                break;

            case 457://Vente ticket �le moon
                if (player.getKamas() >= 1000
                        && player.getCurMap().getId() == 1014) {
                    player.setKamas(player.getKamas() - 1000);
                    GameObject newObjAdded11 = World.world.getObjTemplate(1089).createNewItem(1, false);
                    if (!player.addObjetSimiler(newObjAdded11, true, -1)) {
                        World.world.addGameObject(newObjAdded11);
                        player.addObjet(newObjAdded11);
                    }
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                            + 1089);
                    SocketManager.GAME_SEND_STATS_PACKET(player);
                }
                break;
            /** Fin Ile Moon **/

            /** Donjon Condition **/
            case 500:// Donjon Bouftou : R�compense.
                if (player.getCurMap().getId() != 2084)
                    return true;
                player.teleport((short) 1856, 226);
                GameObject newObjAdded = World.world.getObjTemplate(1728).createNewItem(1, false);
                if (!player.addObjetSimiler(newObjAdded, true, -1)) {
                    World.world.addGameObject(newObjAdded);
                    player.addObjet(newObjAdded);
                }
                SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                        + 1728);
                break;
            case 528:// Gladiatroll : R�compense.
                int nbjeton = Integer.parseInt(args);
                if (!Constant.isInGladiatorDonjon(player.getCurMap().getId()) && player.getCurMap().getId() != 15080)
                    return true;
                player.teleport((short) 3451, 267);
                if(nbjeton>0){
                    GameObject Jetons = World.world.getObjTemplate(16000).createNewItem(nbjeton, false);
                    if (!player.addObjetSimiler(Jetons, true, -1)) {
                        World.world.addGameObject(Jetons);
                        player.addObjet(Jetons);
                    }
                    if(nbjeton==1250){
                        GameObject medals = World.world.getObjTemplate(16001).createNewItem(1, false);
                        if (player.addObjet(medals, true))
                            World.world.addGameObject(medals);
                    }
                }
                break;
            case 501:// Donjon Bwork : R�compense.
                if (player.getCurMap().getId() != 9767)
                    return true;
                player.teleport((short) 9470, 198);
                GameObject newObjAdded1 = World.world.getObjTemplate(8000).createNewItem(1, false);
                if (!player.addObjetSimiler(newObjAdded1, true, -1)) {
                    World.world.addGameObject(newObjAdded1);
                    player.addObjet(newObjAdded1);
                }
                SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                        + 8000);
                break;

            case 502://Entr� Donjon Cawotte
                if (player.hasEquiped(969) && player.hasEquiped(970)
                        && player.hasEquiped(971)
                        && player.getCurMap().getId() == 1781) {
                    player.teleport((short) 1783, 114);
                } else {
                    SocketManager.GAME_SEND_Im_PACKET(player, "114;");
                }
                break;

            case 503://Sortie Donjon Cawotte
                if (player.getCurMap().getId() != 1795)
                    return true;
                if (player.hasItemTemplate(969, 1, false)
                        && player.hasItemTemplate(970, 1, false)
                        && player.hasItemTemplate(971, 1, false)) {
                    player.removeByTemplateID(969, 1);
                    player.removeByTemplateID(970, 1);
                    player.removeByTemplateID(971, 1);
                    GameObject newObjAdded11 = World.world.getObjTemplate(972).createNewItem(1, false);
                    if (!player.addObjetSimiler(newObjAdded11, true, -1)) {
                        World.world.addGameObject(newObjAdded11);
                        player.addObjet(newObjAdded11);
                    }
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                            + 972);
                    player.teleport((short) 1781, 227);
                }
                break;

            case 504://Sortie Donjon Koulosse
                if (player.getCurMap().getId() == 9717) {
                    int type111 = 0;
                    try {
                        type111 = Integer.parseInt(args);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (type111 == 1) {
                        GameObject newObjAdded11 = World.world.getObjTemplate(7890).createNewItem(1, false);
                        if (!player.addObjetSimiler(newObjAdded11, true, -1)) {
                            World.world.addGameObject(newObjAdded11);
                            player.addObjet(newObjAdded11);
                        }
                        SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1
                                + "~" + 7890);
                    }
                    if (type111 == 2) {
                        GameObject newObjAdded11 = World.world.getObjTemplate(7889).createNewItem(1, false);
                        if (!player.addObjetSimiler(newObjAdded11, true, -1)) {
                            World.world.addGameObject(newObjAdded11);
                            player.addObjet(newObjAdded11);
                        }
                        SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1
                                + "~" + 7889);
                    }
                    if (type111 == 3) {
                        GameObject newObjAdded11 = World.world.getObjTemplate(7888).createNewItem(1, false);
                        if (!player.addObjetSimiler(newObjAdded11, true, -1)) {
                            World.world.addGameObject(newObjAdded11);
                            player.addObjet(newObjAdded11);
                        }
                        SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1
                                + "~" + 7888);
                    }
                    if (type111 == 4) {
                        GameObject newObjAdded11 = World.world.getObjTemplate(7887).createNewItem(1, false);
                        if (!player.addObjetSimiler(newObjAdded11, true, -1)) {
                            World.world.addGameObject(newObjAdded11);
                            player.addObjet(newObjAdded11);
                        }
                        SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1
                                + "~" + 7887);
                    }
                    player.teleport((short) 8905, 431);
                }
                break;

            case 505://Learn spell Apprivoisement
                if (player.getCurMap().getId() == 9717) {
                    if (player.hasItemTemplate(7904, 50, false)
                            && player.hasItemTemplate(7903, 50, false)) {
                        player.removeByTemplateID(7904, 50);
                        player.removeByTemplateID(7903, 50);
                        player.learnSpell(414, 1, true, true, true);
                    }
                }
                break;

            case 506://Entr� Donjon Koulosse
                if (player.getCurMap().getId() == 8905
                        && player.getCurCell().getId() == 213) {
                    if (player.hasItemTemplate(7908, 1, false)) {
                        player.removeByTemplateID(7908, 1);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1
                                + "~" + 7908);
                        player.teleport((short) 8950, 408);
                    } else {
                        SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.dj.nokey"));
                    }
                } else {
                    SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.dj.nopnj"));
                }
                break;

            case 507://Donjon Gel�e
                int type3 = 0;
                if (player.getCurMap().getId() == 6823) {
                    try {
                        type3 = Integer.parseInt(args);
                        if (type3 < 6) {
                            if (player.hasItemTemplate(2433, 15, false)
                                    && player.hasItemTemplate(2432, 15, false)
                                    && player.hasItemTemplate(2431, 15, false)
                                    && player.hasItemTemplate(2430, 15, false)) {
                                type3 = 6;
                            } else if (player.hasItemTemplate(2433, 10, false)
                                    && player.hasItemTemplate(2432, 10, false)
                                    && player.hasItemTemplate(2431, 10, false)
                                    && player.hasItemTemplate(2430, 10, false)) {
                                type3 = 5;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    switch (type3) {
                        case 1://Menthe -> 2433
                            if (player.hasItemTemplate(2433, 10, false)) {
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2433) + "~"
                                        + 2433);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2432) + "~"
                                        + 2432);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2431) + "~"
                                        + 2431);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2430) + "~"
                                        + 2430);
                                player.removeByTemplateID(2430, player.getNbItemTemplate(2430));
                                player.removeByTemplateID(2431, player.getNbItemTemplate(2431));
                                player.removeByTemplateID(2432, player.getNbItemTemplate(2432));
                                player.removeByTemplateID(2433, player.getNbItemTemplate(2433));
                                player.teleport((short) 6834, 422);
                            } else {
                                SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.dj.noflaque.menthe"));
                            }
                            break;
                        case 2://Fraise -> 2432
                            if (player.hasItemTemplate(2432, 10, false)) {
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2433) + "~"
                                        + 2433);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2432) + "~"
                                        + 2432);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2431) + "~"
                                        + 2431);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2430) + "~"
                                        + 2430);
                                player.removeByTemplateID(2430, player.getNbItemTemplate(2430));
                                player.removeByTemplateID(2431, player.getNbItemTemplate(2431));
                                player.removeByTemplateID(2432, player.getNbItemTemplate(2432));
                                player.removeByTemplateID(2433, player.getNbItemTemplate(2433));
                                player.teleport((short) 6833, 422);
                            } else {
                                SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.dj.noflaque.fraise"));
                            }
                            break;
                        case 3://Citron -> 2431
                            if (player.hasItemTemplate(2431, 10, false)) {
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2433) + "~"
                                        + 2433);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2432) + "~"
                                        + 2432);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2431) + "~"
                                        + 2431);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2430) + "~"
                                        + 2430);
                                player.removeByTemplateID(2430, player.getNbItemTemplate(2430));
                                player.removeByTemplateID(2431, player.getNbItemTemplate(2431));
                                player.removeByTemplateID(2432, player.getNbItemTemplate(2432));
                                player.removeByTemplateID(2433, player.getNbItemTemplate(2433));
                                player.teleport((short) 6832, 422);
                            } else {
                                SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.dj.gele.noflaque.citron"));
                            }
                            break;
                        case 4://Bleue -> 2430
                            if (player.hasItemTemplate(2430, 10, false)) {
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2433) + "~"
                                        + 2433);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2432) + "~"
                                        + 2432);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2431) + "~"
                                        + 2431);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2430) + "~"
                                        + 2430);
                                player.removeByTemplateID(2430, player.getNbItemTemplate(2430));
                                player.removeByTemplateID(2431, player.getNbItemTemplate(2431));
                                player.removeByTemplateID(2432, player.getNbItemTemplate(2432));
                                player.removeByTemplateID(2433, player.getNbItemTemplate(2433));
                                player.teleport((short) 6831, 422);
                            } else {
                                SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.dj.gele.noflaque.bleue"));
                            }
                            break;
                        case 5://Gel�e x10 => 2433,2432,2431,2430
                            if (player.hasItemTemplate(2433, 10, false)
                                    && player.hasItemTemplate(2432, 10, false)
                                    && player.hasItemTemplate(2431, 10, false)
                                    && player.hasItemTemplate(2430, 10, false)) {
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2433) + "~"
                                        + 2433);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2432) + "~"
                                        + 2432);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2431) + "~"
                                        + 2431);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2430) + "~"
                                        + 2430);
                                player.removeByTemplateID(2430, player.getNbItemTemplate(2430));
                                player.removeByTemplateID(2431, player.getNbItemTemplate(2431));
                                player.removeByTemplateID(2432, player.getNbItemTemplate(2432));
                                player.removeByTemplateID(2433, player.getNbItemTemplate(2433));
                                player.teleport((short) 6835, 422);
                            } else {
                                SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.dj.gele.noflaque.royal.2"));
                            }
                            break;
                        case 6://Menthe -> 2433
                            if (player.hasItemTemplate(2433, 15, false)
                                    && player.hasItemTemplate(2432, 15, false)
                                    && player.hasItemTemplate(2431, 15, false)
                                    && player.hasItemTemplate(2430, 15, false)) {
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2433) + "~"
                                        + 2433);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2432) + "~"
                                        + 2432);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2431) + "~"
                                        + 2431);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2430) + "~"
                                        + 2430);
                                player.removeByTemplateID(2430, player.getNbItemTemplate(2430));
                                player.removeByTemplateID(2431, player.getNbItemTemplate(2431));
                                player.removeByTemplateID(2432, player.getNbItemTemplate(2432));
                                player.removeByTemplateID(2433, player.getNbItemTemplate(2433));
                                player.teleport((short) 6836, 422);
                            } else {
                                SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.dj.gele.noflaque.royal.4"));
                            }
                            break;
                    }
                }
                break;

            case 508://Donjon Kitsoune : R�compense
                if (player.getCurMap().getId() == 8317) {
                    player.teleport((short) 8236, 370);
                    GameObject newObjAdded11 = World.world.getObjTemplate(7415).createNewItem(1, false);
                    if (!player.addObjetSimiler(newObjAdded11, true, -1)) {
                        World.world.addGameObject(newObjAdded11);
                        player.addObjet(newObjAdded11);
                    }
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                            + 7415);
                }
                break;

            case 509://Donjon Bworker : R�compense
                player.teleport((short) 4786, 300);
                GameObject newObjAdded11 = World.world.getObjTemplate(6885).createNewItem(1, false);
                if (!player.addObjetSimiler(newObjAdded11, true, -1)) {
                    World.world.addGameObject(newObjAdded11);
                    player.addObjet(newObjAdded11);
                }
                SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                        + 6885);
                GameObject newObjAdded12 = World.world.getObjTemplate(8388).createNewItem(1, false);
                if (!player.addObjetSimiler(newObjAdded12, true, -1)) {
                    World.world.addGameObject(newObjAdded12);
                    player.addObjet(newObjAdded12);
                }
                SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                        + 8388);
                break;

            case 510://Jeton Bwroker Vs Packet
                if (player.getCurMap().getId() == 3373
                        && player.hasItemTemplate(6885, 1, false)) {
                    player.removeByTemplateID(6885, 1);
                    GameObject newObjAdded121 = World.world.getObjTemplate(6887).createNewItem(1, false);
                    if (!player.addObjetSimiler(newObjAdded121, true, -1)) {
                        World.world.addGameObject(newObjAdded121);
                        player.addObjet(newObjAdded121);
                    }
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 6885);
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                            + 6887);
                }
                break;

            case 511://Cadeau Bworker
                int cadeau = Loterie.getCadeauBworker();
                GameObject newObjAdded121 = World.world.getObjTemplate(cadeau).createNewItem(1, false);
                if (!player.addObjetSimiler(newObjAdded121, true, -1)) {
                    World.world.addGameObject(newObjAdded121);
                    player.addObjet(newObjAdded121);
                }
                SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                        + cadeau);
                break;

            case 512://Rat Blanc : R�compense
                if (player.getCurMap().getId() == 10213) {
                    player.teleport((short) 6536, 273);
                    GameObject newObjAdded111 = World.world.getObjTemplate(8476).createNewItem(1, false);
                    if (!player.addObjetSimiler(newObjAdded111, true, -1)) {
                        World.world.addGameObject(newObjAdded111);
                        player.addObjet(newObjAdded111);
                    }
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                            + 8476);
                }
                break;

            case 513://Rat noir : R�compense
                if (player.getCurMap().getId() == 10199) {
                    player.teleport((short) 6738, 213);
                    GameObject newObjAdded111 = World.world.getObjTemplate(8477).createNewItem(1, false);
                    if (!player.addObjetSimiler(newObjAdded111, true, -1)) {
                        World.world.addGameObject(newObjAdded111);
                        player.addObjet(newObjAdded111);
                    }
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                            + 8477);
                }
                break;

            case 514://Splinter Cell : Entr�
                if (player.getCurMap().getId() == 9638
                        && player.hasItemTemplate(8476, 1, false)
                        && player.hasItemTemplate(8477, 1, false)) {
                    player.teleport((short) 10141, 448);
                    player.removeByTemplateID(8476, 1);
                    player.removeByTemplateID(8477, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 8476);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 8477);
                }
                break;

            case 515://R�compense Pandikaze
                if (player.getCurMap().getId() == 8497) {
                    player.teleport((short) 8167, 252);
                    GameObject newObjAdded111 = World.world.getObjTemplate(7414).createNewItem(1, false);
                    if (!player.addObjetSimiler(newObjAdded111, true, -1)) {
                        World.world.addGameObject(newObjAdded111);
                        player.addObjet(newObjAdded111);
                    }
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                            + 7414);
                }
                break;

            case 516://Chenil achat poudre eniripsa
                if (player.getCurMap().getId() == 1140
                        && player.getKamas() >= 1000) {
                    GameObject newObjAdded111 = World.world.getObjTemplate(2239).createNewItem(1, false);
                    if (!player.addObjetSimiler(newObjAdded111, true, -1)) {
                        World.world.addGameObject(newObjAdded111);
                        player.addObjet(newObjAdded111);
                    }
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                            + 2239);
                }
                break;

            case 517://Entr�e donjon familier 9052,268 - O: 7 - Transformation donjon abra
                mapId = Integer.parseInt(args.split(";")[0].split(",")[0]);
                int cellId = Integer.parseInt(args.split(";")[0].split(",")[1]);
                short mapSecu = Short.parseShort(args.split(";")[1]);
                int id = Integer.parseInt(args.split(";")[2]);
                if (player.getCurMap().getId() != mapSecu)
                    return true;
                if (player.getCurMap().getId() == 9052) {
                    if (player.getCurCell().getId() != 268
                            || player.get_orientation() != 7)
                        return true;
                    if (!player.hasItemType(90)) {
                        SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.dj.fami"));
                        return true;
                    }
                }
                player.teleport((short) mapId, cellId);
                player.setFullMorph(id, false, false);
                break;

            case 518://D�morph + TP : Donjon abra, familier
                mapId = Integer.parseInt(args.split(";")[0].split(",")[0]);
                cellId = Integer.parseInt(args.split(";")[0].split(",")[1]);
                mapSecu = Short.parseShort(args.split(";")[1]);
                if (player.getCurMap().getId() != mapSecu)
                    return true;
                player.unsetFullMorph();
                player.teleport((short) mapId, cellId);
                break;

            case 519://Donjon Grotte Hesque, Arche, Rasboul, Tynril
                mapId = Integer.parseInt(args.split(";")[0].split(",")[0]);
                cellId = Integer.parseInt(args.split(";")[0].split(",")[1]);
                mapSecu = Short.parseShort(args.split(";")[1]);
                if (player.getCurMap().getId() != mapSecu)
                    return true;
                GameObject obj1 = World.world.getObjTemplate(Integer.parseInt(args.split(";")[2])).createNewItem(1, false);
                if (obj1 != null)
                    if (player.addObjet(obj1, true))
                        World.world.addGameObject(obj1);
                player.send("Im021;1~" + args.split(";")[2]);
                obj1 = World.world.getObjTemplate(Integer.parseInt(args.split(";")[3])).createNewItem(1, false);
                if (obj1 != null)
                    if (player.addObjet(obj1, true))
                        World.world.addGameObject(obj1);
                player.send("Im021;1~" + args.split(";")[3]);
                player.teleport((short) mapId, cellId);
                break;

            case 520://Dj pandikaze
                if (player.getCurMap().getId() != 8497)
                    return true;

                obj1 = World.world.getObjTemplate(7414).createNewItem(1, false);
                if (player.addObjet(obj1, true))
                    World.world.addGameObject(obj1);
                player.send("Im021;1~7414");
                if (!player.getEmotes().contains(15)) {
                    obj1 = World.world.getObjTemplate(7413).createNewItem(1, false);
                    if (player.addObjet(obj1, true))
                        World.world.addGameObject(obj1);
                    player.send("Im021;1~7413");
                }

                player.teleport((short) 8167, 252);
                break;

            case 521://Echange clef skeunk
                if (player.getCurMap().getId() != 9248)
                    return true;

                if (player.hasItemTemplate(7887, 1, false) && player.hasItemTemplate(7888, 1, false) && player.hasItemTemplate(7889, 1, false) && player.hasItemTemplate(7890, 1, false)) {
                    player.removeByTemplateID(7887, 1);
                    player.removeByTemplateID(7888, 1);
                    player.removeByTemplateID(7889, 1);
                    player.removeByTemplateID(7890, 1);
                    player.send("Im022;1~7887");
                    player.send("Im022;1~7888");
                    player.send("Im022;1~7889");
                    player.send("Im022;1~7890");

                    obj1 = World.world.getObjTemplate(8073).createNewItem(1, false);
                    if (player.addObjet(obj1, true))
                        World.world.addGameObject(obj1);
                    player.send("Im021;1~8073");
                } else {
                    player.send("Im119|45");
                }
                break;

            case 522://P�ki p�ki
                if (player.getCurMap().getId() != 8349)
                    return true;

                obj1 = World.world.getObjTemplate(6978).createNewItem(1, false);
                if (player.addObjet(obj1, true))
                    World.world.addGameObject(obj1);
                player.send("Im021;1~6978");
                player.teleport((short) 8467, 227);
                break;

            case 523://Cawotte vs spell
                if (player.getCurMap().getId() != 1779)
                    return true;
                if(client == null) return true;

                if (player.hasItemTemplate(361, 100, false)) {
                    player.removeByTemplateID(361, 100);
                    player.send("Im022;100~361");
                    player.learnSpell(367, 1, true, true, true);

                    NpcQuestion quest = World.world.getNPCQuestion(473);
                    if (quest == null) {
                        SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                        player.setExchangeAction(null);
                        return true;
                    }
                    SocketManager.GAME_SEND_QUESTION_PACKET(client, quest.parse(player));
                    return false;
                } else {
                    player.send("Im14");
                }
                break;

            case 524://R�ponse Maitre corbac
                if(client == null) return true;
                int qID = Monster.MobGroup.MAITRE_CORBAC.check();
                NpcQuestion quest = World.world.getNPCQuestion(qID);
                if (quest == null) {
                    SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                    player.setExchangeAction(null);
                    return true;
                }
                SocketManager.GAME_SEND_QUESTION_PACKET(client, quest.parse(player));
                return false;

            case 525://EndFight Action Maitre Corbac
                Monster.MobGroup group = player.hasMobGroup();

                split = args.split(";");

                for (Monster.MobGrade mb : group.getMobs().values()) {
                    switch (mb.getTemplate().getId()) {
                        case 289:
                            player.teleport((short) 9604, 403);
                            return true;
                        case 819:
                            player.teleport(Short.parseShort(split[0].split(",")[0]), Integer.parseInt(split[0].split(",")[1]));
                            return true;
                        case 820:
                            player.teleport(Short.parseShort(split[1].split(",")[0]), Integer.parseInt(split[1].split(",")[1]));
                            return true;
                    }
                }
                break;

            case 526://Fin donjon maitre corbac
                if (player.getCurMap().getId() != 9604)
                    return true;

                obj1 = World.world.getObjTemplate(7703).createNewItem(1, false);
                if (player.addObjet(obj1, true))
                    World.world.addGameObject(obj1);
                player.send("Im021;1~7703");
                player.teleport((short) 2985, 279);
                break;

            case 527://Donjon ensabl� fin
                if (player.getCurMap().getId() != 10165)
                    return true;

                player.addStaticEmote(19);
                player.teleport((short) 10155, 210);
                break;

            case 964://Signer le registre
                if(client == null) return true;
                if (player.getCurMap().getId() != 10255)
                    return true;
                if (player.getAlignment() != 1 && player.getAlignment() != 2)
                    return true;
                if (player.hasItemTemplate(9487, 1, false)) {
                    String date = player.getItemTemplate(9487, 1).getTxtStat().get(Constant.STATS_DATE);
                    long timeStamp = Long.parseLong(date);
                    if (System.currentTimeMillis() - timeStamp <= 1209600000) // 14 jours
                    {
                        return true;
                    } else {
                        player.removeByTemplateID(9487, 1);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 9487);
                    }
                }

                if (player.hasItemTemplate(9811, 1, false)) // Formulaire de neutralit�
                {
                    player.removeByTemplateID(9811, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 9811);
                    player.modifAlignement(0);
                } else if (player.hasItemTemplate(9812, 1, false)) // Formulaire de d�sertion
                {
                    if (player.hasItemTemplate(9488, 1, false)) {
                        player.removeByTemplateID(9488, 1);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 9488);
                        player.modifAlignement(1);
                    } else if (player.hasItemTemplate(9489, 1, false)) {
                        player.removeByTemplateID(9489, 1);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 9489);
                        player.modifAlignement(2);
                    }
                    player.removeByTemplateID(9812, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 9812);
                }

                ObjectTemplate t2 = World.world.getObjTemplate(9487);
                GameObject obj2 = t2.createNewItem(1, false);
                obj2.refreshStatsObjet("325#0#0#"
                        + System.currentTimeMillis());
                if (player.addObjet(obj2, false)) {
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1
                            + "~" + obj2.getTemplate().getId());
                    World.world.addGameObject(obj2);
                }

                quest = World.world.getNPCQuestion(Integer.parseInt(this.args));
                if (quest == null) {
                    SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                    player.setExchangeAction(null);
                    return true;
                }
                try {
                    SocketManager.GAME_SEND_QUESTION_PACKET(client, quest.parse(player));
                    return false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 965://Signer le document officiel
                if(client == null) return true;
                if (player.getCurMap().getId() != 10255)
                    return true;
                if (player.getAlignment() != 1 && player.getAlignment() != 2)
                    return true;
                if (player.hasItemTemplate(9487, 1, false)) {
                    String date = player.getItemTemplate(9487, 1).getTxtStat().get(Constant.STATS_DATE);
                    long timeStamp = Long.parseLong(date);
                    if (System.currentTimeMillis() - timeStamp <= 1209600000) // 14 jours
                    {
                        return true;
                    }
                }

                boolean next = false;
                if (player.hasItemTemplate(9811, 1, false)) // Formulaire de neutralit�
                {
                    next = true;
                } else if (player.hasItemTemplate(9812, 1, false)) // Formulaire de d�sertion
                {
                    int idTemp = -1;
                    if (player.getAlignment() == 2) // Brak, donc passer bont
                        idTemp = 9488;
                    else
                        idTemp = 9489;

                    ObjectTemplate t = World.world.getObjTemplate(idTemp);
                    GameObject obj = t.createNewItem(1, false);
                    obj.refreshStatsObjet("325#0#0#"
                            + System.currentTimeMillis());
                    if (player.addObjet(obj, false)) {
                        SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1
                                + "~" + obj.getTemplate().getId());
                        World.world.addGameObject(obj);
                    }
                    next = true;
                }

                if (next) {
                    quest = World.world.getNPCQuestion(Integer.parseInt(this.args));
                    if (quest == null) {
                        SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                        player.setExchangeAction(null);
                        return true;
                    }
                    try {
                        SocketManager.GAME_SEND_QUESTION_PACKET(client, quest.parse(player));
                        return false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case 963://Formulaire de d�sertion
                if(client == null) return true;
                if (player.getCurMap().getId() != 10255)
                    return true;
                if (player.getAlignment() != 1 && player.getAlignment() != 2)
                    return true;
                if (player.hasItemTemplate(9487, 1, false)) {
                    String date = player.getItemTemplate(9487, 1).getTxtStat().get(Constant.STATS_DATE);
                    long timeStamp = Long.parseLong(date);
                    if (System.currentTimeMillis() - timeStamp <= 1209600000) // 14 jours
                    {
                        return true;
                    }
                }

                t2 = World.world.getObjTemplate(9812);
                obj2 = t2.createNewItem(1, false);
                obj2.refreshStatsObjet("325#0#0#"
                        + System.currentTimeMillis());
                if (player.addObjet(obj2, false)) {
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1
                            + "~" + obj2.getTemplate().getId());
                    World.world.addGameObject(obj2);
                }

                quest = World.world.getNPCQuestion(Integer.parseInt(this.args));
                if (quest == null) {
                    SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                    player.setExchangeAction(null);
                    return true;
                }
                try {
                    SocketManager.GAME_SEND_QUESTION_PACKET(client, quest.parse(player));
                    return false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 966://Formulaire de neutralit�
                if(client == null) return true;
                if (player.getCurMap().getId() != 10255)
                    return true;
                if (player.getAlignment() != 1 && player.getAlignment() != 2)
                    return true;
                if (player.hasItemTemplate(9487, 1, false)) {
                    String date = player.getItemTemplate(9487, 1).getTxtStat().get(Constant.STATS_DATE);
                    long timeStamp = Long.parseLong(date);
                    if (System.currentTimeMillis() - timeStamp <= 1209600000) // 14 jours
                    {
                        return true;
                    }
                }

                int kamas = 256000;
                if (player.getALvl() <= 10)
                    kamas = 500;
                else if (player.getALvl() <= 20)
                    kamas = 1000;
                else if (player.getALvl() <= 30)
                    kamas = 2000;
                else if (player.getALvl() <= 40)
                    kamas = 4000;
                else if (player.getALvl() <= 50)
                    kamas = 8000;
                else if (player.getALvl() <= 60)
                    kamas = 16000;
                else if (player.getALvl() <= 70)
                    kamas = 32000;
                else if (player.getALvl() <= 80)
                    kamas = 64000;
                else if (player.getALvl() <= 90)
                    kamas = 128000;
                else if (player.getALvl() <= 100)
                    kamas = 256000;

                if (player.getKamas() < kamas) {
                    SocketManager.GAME_SEND_MESSAGE_SERVER(player, "10|" + kamas);
                    return true;
                } else {
                    player.setKamas(player.getKamas() - kamas);
                    SocketManager.GAME_SEND_STATS_PACKET(player);
                    SocketManager.GAME_SEND_Im_PACKET(player, "046;" + kamas);

                    if (player.hasItemTemplate(9811, 1, false)) {
                        player.removeByTemplateID(9811, 1);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1
                                + "~" + 9811);
                    }

                    ObjectTemplate t = World.world.getObjTemplate(9811);
                    GameObject obj = t.createNewItem(1, false);
                    obj.refreshStatsObjet("325#0#0#"
                            + System.currentTimeMillis());
                    if (player.addObjet(obj, false)) {
                        SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1
                                + "~" + obj.getTemplate().getId());
                        World.world.addGameObject(obj);
                    }

                    quest = World.world.getNPCQuestion(Integer.parseInt(this.args));
                    if (quest == null) {
                        SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                        player.setExchangeAction(null);
                        return true;
                    }
                    try {
                        SocketManager.GAME_SEND_QUESTION_PACKET(client, quest.parse(player));
                        return false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case 967://Apprendre bricoleur
                if(client == null) return true;
                if (player.getCurMap().getId() != 8736 && player.getCurMap().getId() != 8737) return true;

                Job job = World.world.getMetier(65);
                if (job == null) return true;

                if (player.getMetierByID(job.getId()) != null) {
                    SocketManager.GAME_SEND_Im_PACKET(player, "111");
                    SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                    player.setExchangeAction(null);
                    player.setIsOnDialogAction(-1);
                    return true;
                }

                if (player.getMetierByID(2) != null && player.getMetierByID(2).get_lvl() < 30 || player.getMetierByID(11) != null && player.getMetierByID(11).get_lvl() < 30 || player.getMetierByID(13) != null && player.getMetierByID(13).get_lvl() < 30 || player.getMetierByID(14) != null && player.getMetierByID(14).get_lvl() < 30 || player.getMetierByID(15) != null && player.getMetierByID(15).get_lvl() < 30 || player.getMetierByID(16) != null && player.getMetierByID(16).get_lvl() < 30 || player.getMetierByID(17) != null && player.getMetierByID(17).get_lvl() < 30 || player.getMetierByID(18) != null && player.getMetierByID(18).get_lvl() < 30 || player.getMetierByID(19) != null && player.getMetierByID(19).get_lvl() < 30 || player.getMetierByID(20) != null && player.getMetierByID(20).get_lvl() < 30 || player.getMetierByID(24) != null && player.getMetierByID(24).get_lvl() < 30 || player.getMetierByID(25) != null && player.getMetierByID(25).get_lvl() < 30 || player.getMetierByID(26) != null && player.getMetierByID(26).get_lvl() < 30 || player.getMetierByID(27) != null && player.getMetierByID(27).get_lvl() < 30 || player.getMetierByID(28) != null && player.getMetierByID(28).get_lvl() < 30 || player.getMetierByID(31) != null && player.getMetierByID(31).get_lvl() < 30 || player.getMetierByID(36) != null && player.getMetierByID(36).get_lvl() < 30 || player.getMetierByID(41) != null && player.getMetierByID(41).get_lvl() < 30 || player.getMetierByID(56) != null && player.getMetierByID(56).get_lvl() < 30 || player.getMetierByID(58) != null && player.getMetierByID(58).get_lvl() < 30 || player.getMetierByID(60) != null && player.getMetierByID(60).get_lvl() < 30) {
                    SocketManager.send(client, "DQ336|4840");
                    return false;
                }

                if (player.totalJobBasic() > 2) {
                    SocketManager.GAME_SEND_Im_PACKET(player, "19");
                    SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                    player.setExchangeAction(null);
                    player.setIsOnDialogAction(-1);
                } else {
                    if (player.hasItemTemplate(459, 20, false) && player.hasItemTemplate(7657, 15, false)) {
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 20 + "~" + 459);
                        player.removeByTemplateID(459, 20);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 15 + "~" + 7657);
                        player.removeByTemplateID(7657, 15);
                        player.learnJob(job);
                        SocketManager.send(client, "DQ3153|4840");
                        return false;
                    } else {
                        SocketManager.send(client, "DQ3151|4840");
                        return false;
                    }
                }
                return true;

            case 968://Fin de donjon toror & tot
                if (player.getCurMap().getId() == (short) 9877
                        || player.getCurMap().getId() == (short) 9881) {
                    player.teleport((short) 9538, 186);
                }
                break;

            case 969://Transformation donjon abra & CM
                if (player.getCurMap().getId() == (short) 8715) // Abra
                {
                    player.teleport((short) 8716, 366);
                    player.setFullMorph(11, false, false);
                } else if (player.getCurMap().getId() == (short) 9120) // CM
                {
                    player.teleport((short) 9121, 69);
                    player.setFullMorph(11, false, false);
                }
                break;

            case 970://D�morph + TP : Donjon abra & CM
                if (player.getCurMap().getId() == (short) 8719) // Abra
                {
                    player.unsetFullMorph();
                    player.teleport((short) 10154, 335);
                } else if (player.getCurMap().getId() == (short) 9123) // CM
                {
                    player.unsetFullMorph();
                    player.teleport((short) 9125, 71);
                }
                break;

            case 971://Entr�e du donjon dragoeufs
                if (player.getCurMap().getId() == (short) 9788) {
                    boolean key0 = player.hasItemTemplate(8342, 1, false) && player.hasItemTemplate(8343, 1, false), key1 = false, key2 = false;
                    if (key0 || player.hasItemTemplate(10207, 1, false)) {

                        if(player.hasItemTemplate(10207, 1, false)) {
                            String stats = player.getItemTemplate(10207).getTxtStat().get(Constant.STATS_NAME_DJ);
                            for(String key : stats.split(",")) {
                                id = Integer.parseInt(key, 16);
                                if (id == 8342) key1 = true;
                                if (id == 8343) key2 = true;
                            }

                            if(key1 && key2){
                                String replace1 = Integer.toHexString(8342), replace2 = Integer.toHexString(8343), newStats = "";
                                for (String i : stats.split(","))
                                    if (!i.equals(replace1) || !i.equals(replace2))
                                        newStats += (newStats.isEmpty() ? i : "," + i);
                                player.getItemTemplate(10207).getTxtStat().remove(Constant.STATS_NAME_DJ);
                                player.getItemTemplate(10207).getTxtStat().put(Constant.STATS_NAME_DJ, newStats);
                                SocketManager.GAME_SEND_UPDATE_ITEM(player, player.getItemTemplate(10207));
                            }
                        }
                        if(key0 && (!key1 || !key2)) {
                            player.removeByTemplateID(8342, 1);
                            player.removeByTemplateID(8343, 1);
                        }
                        if(key0 || (key1 && key2)) {
                            SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 8342);
                            SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 8343);
                            player.teleport((short) 10098, 407);
                            return true;
                        }
                    }
                }
                SocketManager.GAME_SEND_Im_PACKET(player, "119");
                break;

            case 972://Sortir du donjon skeunk avec le trousseau
                if (player.getCurMap().getId() != (short) 8978)
                    return true;
                if (!player.hasItemTemplate(7935, 1, false))
                    return true;
                if (!player.hasItemTemplate(7936, 1, false))
                    return true;
                if (!player.hasItemTemplate(7937, 1, false))
                    return true;
                if (!player.hasItemTemplate(7938, 1, false))
                    return true;

                boolean key0 = false;
                if(player.hasItemTemplate(10207, 1, false)) {
                    String stats = player.getItemTemplate(10207).getTxtStat().get(Constant.STATS_NAME_DJ);
                    for(String key : stats.split(",")) {
                        if (Integer.parseInt(key, 16) == 8073) key0 = true;
                    }

                    if(key0){
                        String replace = Integer.toHexString(8073), newStats = "";
                        for (String i : stats.split(","))
                            if (!i.equals(replace))
                                newStats += (newStats.isEmpty() ? i : "," + i);
                        player.getItemTemplate(10207).getTxtStat().remove(Constant.STATS_NAME_DJ);
                        player.getItemTemplate(10207).getTxtStat().put(Constant.STATS_NAME_DJ, newStats);
                        SocketManager.GAME_SEND_UPDATE_ITEM(player, player.getItemTemplate(10207));
                    }
                } else return true;

                SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 7935);
                player.removeByTemplateID(7935, 1);
                SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 7936);
                player.removeByTemplateID(7936, 1);
                SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 7937);
                player.removeByTemplateID(7937, 1);
                SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 7938);
                player.removeByTemplateID(7938, 1);
                SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 8073);

                GameObject object = World.world.getObjTemplate(8072).createNewItem(1, false);

                if (player.addObjet(object, false)) {
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~" + object.getTemplate().getId());
                    World.world.addGameObject(object);
                }

                player.teleport((short) 9503, 357);
                break;

            case 973://Sortir du donjon skeunk avec la clef
                if (player.getCurMap().getId() != (short) 8978)
                    return true;
                if (!player.hasItemTemplate(7935, 1, false))
                    return true;
                if (!player.hasItemTemplate(7936, 1, false))
                    return true;
                if (!player.hasItemTemplate(7937, 1, false))
                    return true;
                if (!player.hasItemTemplate(7938, 1, false))
                    return true;
                if (!player.hasItemTemplate(8073, 1, false))
                    return true;

                SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 7935);
                player.removeByTemplateID(7935, 1);
                SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 7936);
                player.removeByTemplateID(7936, 1);
                SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 7937);
                player.removeByTemplateID(7937, 1);
                SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 7938);
                player.removeByTemplateID(7938, 1);
                SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 8073);
                player.removeByTemplateID(8073, 1);

                ObjectTemplate dofus = World.world.getObjTemplate(8072);
                GameObject obj = dofus.createNewItem(1, false);
                if (player.addObjet(obj, false)) {
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~" + obj.getTemplate().getId());
                    World.world.addGameObject(obj);
                }

                player.teleport((short) 9503, 357);
                break;

            case 974://Sort boomerang perfide
                if (player.getCurMap().getId() != (short) 8978)
                    return true;
                if (!player.hasItemTemplate(8075, 10, false))
                    return true;
                if (!player.hasItemTemplate(8076, 10, false))
                    return true;
                if (!player.hasItemTemplate(8077, 10, false))
                    return true;
                if (!player.hasItemTemplate(8064, 10, false))
                    return true;
                if (player.hasSpell(364))
                    return true;

                SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 10 + "~"
                        + 8075);
                player.removeByTemplateID(8075, 10);
                SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 10 + "~"
                        + 8076);
                player.removeByTemplateID(8076, 10);
                SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 10 + "~"
                        + 8077);
                player.removeByTemplateID(8077, 10);
                SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 10 + "~"
                        + 8064);
                player.removeByTemplateID(8064, 10);

                player.learnSpell(364, 1, true, true, true);
                break;

            case 975://Entr�e salle skeunk
                if (player.getCurMap().getId() != (short) 8973)
                    return true;
                if (!player.hasItemTemplate(7935, 1, false) || !player.hasItemTemplate(7936, 1, false) || !player.hasItemTemplate(7937, 1, false) || !player.hasItemTemplate(7938, 1, false))
                    return true;

                player.teleport((short) 8977, 448);
                break;
            case 976://T�l�portation en Minotoror
                try {
                    if (player.getCurMap().getId() != (short) 9557)
                        return true;
                    if (!player.hasItemTemplate(8305, 1, false))
                        return true;
                    if (!player.hasItemTemplate(8306, 1, false))
                        return true;
                    boolean ok = false;
                    if (player.hasItemTemplate(10207, 1, false)) {
                        String stats, statsReplace = "";
                        object = player.getItemTemplate(10207);
                        stats = object.getTxtStat().get(Constant.STATS_NAME_DJ);
                        try {
                            for (String i : stats.split(",")) {
                                if (Dopeul.parseConditionTrousseau(i.replace(" ", ""), 783, player.getCurMap().getId())) {
                                    ok = true;
                                    statsReplace = i;
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (!statsReplace.isEmpty()) {
                            String newStats = "";
                            for (String i : stats.split(","))
                                if (!i.equals(statsReplace))
                                    newStats += (newStats.isEmpty() ? i : "," + i);
                            object.getTxtStat().remove(Constant.STATS_NAME_DJ);
                            object.getTxtStat().put(Constant.STATS_NAME_DJ, newStats);
                            SocketManager.GAME_SEND_UPDATE_ITEM(player, player.getItemTemplate(10207));
                        }
                    }
                    if (!ok && !player.hasItemTemplate(7924, 1, false))
                        return true;

                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 8305);
                    player.removeByTemplateID(8305, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 8306);
                    player.removeByTemplateID(8306, 1);
                    if(!ok) {
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 7924);
                        player.removeByTemplateID(7924, 1);
                    }
                    player.teleport((short) 9880, 399);
                } catch (Exception e) {
                    return true;
                }
                break;

            case 977://T�l�portation en salle des dalles Toror
                try {
                    switch (player.getCurMap().getId()) {
                        case 9553:
                        case 9554:
                        case 9555:
                        case 9556:
                        case 9557:
                        case 9558:
                        case 9559:
                        case 9560:
                        case 9561:
                        case 9562:
                        case 9563:
                        case 9564:
                        case 9565:
                        case 9566:
                        case 9567:
                        case 9568:
                        case 9569:
                        case 9570:
                        case 9571:
                        case 9572:
                        case 9573:
                        case 9574:
                        case 9575:
                        case 9576:
                        case 9577:
                            player.teleport((short) 9876, 287);
                            break;
                    }
                } catch (Exception e) {
                    return true;
                }
                break;

            case 978://T�l�portation en salle des dalles DC
                try {
                    switch (player.getCurMap().getId()) {
                        case 9372:
                        case 9384:
                        case 9380:
                        case 9381:
                        case 9382:
                        case 9383:
                        case 9393:
                        case 9374:
                        case 9394:
                        case 9390:
                        case 9391:
                        case 9392:
                        case 9373:
                        case 9389:
                        case 9385:
                        case 9386:
                        case 9387:
                        case 9388:
                        case 9371:
                        case 9375:
                        case 9376:
                        case 9377:
                        case 9378:
                        case 9379:
                            player.teleport((short) 9396, 387);
                            break;
                    }
                } catch (Exception e) {
                    return true;
                }
                break;
            case 979://T�l�portation labyrinth DC
                try {
                    short newMapID = Short.parseShort(args.split(",", 2)[0]);
                    final GameMap newMap = World.world.getMap(newMapID);
                    int newCellID = Integer.parseInt(args.split(",", 2)[1]);
                    final GameCase curCase = player.getCurCell();
                    final GameMap curMap = player.getCurMap();
                    int idCurCase = curCase.getId();
                    if (idCurCase < 52 || idCurCase > 412) // On monte ou on descend
                    {
                        player.teleportLaby(newMapID, newCellID);
                        TimerWaiter.addNext(() -> {
                            PigDragon.open(curMap, curCase);
                            PigDragon.open(newMap, PigDragon.getDownCell(newMap));
                            PigDragon.open(newMap, PigDragon.getUpCell(newMap));
                            PigDragon.open(newMap, PigDragon.getRightCell(newMap));
                            PigDragon.open(newMap, PigDragon.getLeftCell(newMap));
                        }, 1000);
                    } else if (idCurCase == 262 || idCurCase == 320 || idCurCase == 144 || idCurCase == 216 || idCurCase == 231 || idCurCase == 274) // A gauche ou a droite
                    {
                        player.teleportLaby(newMapID, newCellID);

                        TimerWaiter.addNext(() -> {
                            PigDragon.open(curMap, curCase);
                            PigDragon.open(newMap, PigDragon.getLeftCell(newMap));
                            PigDragon.open(newMap, PigDragon.getRightCell(newMap));
                            PigDragon.open(newMap, PigDragon.getUpCell(newMap));
                            PigDragon.open(newMap, PigDragon.getDownCell(newMap));
                        }, 1000);
                    } else {
                        SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.door.error"));
                        return true;
                    }
                } catch (Exception e) {
                    return true;
                }
                break;

            case 980: // t�l�portation avec mapsecu, et deux itemssecu supprim�s : donjon dc
                try {
                    mapId = Integer.parseInt(args.split(",")[0]);
                    cellId = Integer.parseInt(args.split(",")[1]);
                    int item = Integer.parseInt(args.split(",")[2]);
                    int item2 = Integer.parseInt(args.split(",")[3]);
                    mapSecu = Short.parseShort(args.split(",")[4]);

                    if (player.getCurMap().getId() != mapSecu)
                        return true;
                    boolean ok = false;
                    if(mapSecu == 9395 && player.hasItemTemplate(10207, 1, false)) {// DC
                        String stats, statsReplace1 = "", statsReplace2 = "";
                        object = player.getItemTemplate(10207);
                        stats = object.getTxtStat().get(Constant.STATS_NAME_DJ);
                        try {
                            boolean ok1 = false, ok2 = false;
                            for (String i : stats.split(",")) {
                                if(Integer.toHexString(7511).equals(i)) {
                                    statsReplace1 = i;
                                    ok1 = true;
                                }
                                if(Integer.toHexString(8320).equals(i)) {
                                    statsReplace2 = i;
                                    ok2 = true;
                                }
                            }
                            ok = ok1 && ok2;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if(ok) {
                            if (!statsReplace1.isEmpty()) {
                                String newStats = "";
                                for (String i : stats.split(","))
                                    if (!i.equals(statsReplace1))
                                        newStats += (newStats.isEmpty() ? i : "," + i);
                                object.getTxtStat().remove(Constant.STATS_NAME_DJ);
                                object.getTxtStat().put(Constant.STATS_NAME_DJ, newStats);
                                SocketManager.GAME_SEND_UPDATE_ITEM(player, player.getItemTemplate(10207));
                            }
                            if (!statsReplace2.isEmpty()) {
                                String newStats = "";
                                for (String i : stats.split(","))
                                    if (!i.equals(statsReplace2))
                                        newStats += (newStats.isEmpty() ? i : "," + i);
                                object.getTxtStat().remove(Constant.STATS_NAME_DJ);
                                object.getTxtStat().put(Constant.STATS_NAME_DJ, newStats);
                                SocketManager.GAME_SEND_UPDATE_ITEM(player, player.getItemTemplate(10207));
                            }
                        }
                    }

                    if(!ok) {
                        if (!player.hasItemTemplate(item, 1, false) && !player.hasItemTemplate(item2, 1, false))
                            return true;
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + item);
                        player.removeByTemplateID(item, 1);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + item2);
                        player.removeByTemplateID(item2, 1);
                    }

                    player.teleport((short) mapId, cellId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 982: // Mort
                try {
                    player.setFuneral();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 983:
                try {
                    Quest q = Quest.getQuestById(193);
                    if (q == null)
                        return true;
                    GameMap curMap = player.getCurMap();
                    if (curMap.getId() != (short) 10332)
                        return true;
                    if (player.getQuestPersoByQuest(q) == null)
                        q.applyQuest(player);
                    else if (q.getCurrentQuestStep(player.getQuestPersoByQuest(q)).getId() != 793)
                        return true;

                    Monster petitChef = World.world.getMonstre(984);
                    if (petitChef == null)
                        return true;
                    Monster.MobGrade mg = petitChef.getGradeByLevel(10);
                    if (mg == null)
                        return true;
                    Monster.MobGroup _mg = new Monster.MobGroup(player.getCurMap().nextObjectId, player.getCurMap(), player.getCurCell().getId(), petitChef.getId() + "," + mg.getLevel() + "," + mg.getLevel() + ";");
                    player.getCurMap().startFightVersusMonstres(player, _mg);// Si bug startfight, voir "//Respawn d'un groupe fix" dans fight.java
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 984:
                try {
                    int xp = Integer.parseInt(args.split(",")[0]);
                    int mapCurId = Integer.parseInt(args.split(",")[1]);
                    int idQuest = Integer.parseInt(args.split(",")[2]);

                    if (player.getCurMap().getId() != (short) mapCurId)
                        return true;

                    QuestPlayer qp = player.getQuestPersoByQuestId(idQuest);
                    if (qp == null)
                        return true;
                    if (qp.isFinish())
                        return true;

                    player.addXp((long) xp);
                    SocketManager.GAME_SEND_Im_PACKET(player, "08;" + xp);
                    qp.setFinish(true);
                    SocketManager.GAME_SEND_Im_PACKET(player, "055;" + idQuest);
                    SocketManager.GAME_SEND_Im_PACKET(player, "056;" + idQuest);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 985:
                if(client == null) return true;
                try {
                    int item = Integer.parseInt(args.split(",")[0]);
                    int item2 = Integer.parseInt(args.split(",")[1]);
                    int mapCurId = Integer.parseInt(args.split(",")[2]);
                    int metierId = Integer.parseInt(args.split(",")[3]);

                    if (player.getCurMap().getId() != (short) mapCurId)
                        return true;
                    Job metierArgs = World.world.getMetier(metierId);
                    if (metierArgs == null)
                        return true;

                    if (player.getMetierByID(metierId) != null) {
                        SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                        player.setExchangeAction(null);
                        player.setIsOnDialogAction(-1);
                        SocketManager.GAME_SEND_Im_PACKET(player, "111");
                        return true; // Si on a d�j� le m�tier
                    }

                    ObjectTemplate t = World.world.getObjTemplate(item2);
                    if (t == null)
                        return true;

                    if (player.hasItemTemplate(item, 1, false)) {

                        for (Entry<Integer, JobStat> entry : player.getMetiers().entrySet()) {
                            if (entry.getValue().get_lvl() < 30
                                    && !entry.getValue().getTemplate().isMaging()) {
                                SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                                player.setExchangeAction(null);
                                player.setIsOnDialogAction(-1);
                                SocketManager.GAME_SEND_Im_PACKET(player, "18;30");
                                return true;
                            }
                        }

                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1
                                + "~" + item);
                        player.removeByTemplateID(item, 1);
                        obj = t.createNewItem(1, false);
                        obj.refreshStatsObjet("325#0#0#"
                                + System.currentTimeMillis());
                        if (player.addObjet(obj, false)) {
                            SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1
                                    + "~" + obj.getTemplate().getId());
                            World.world.addGameObject(obj);
                        }

                        player.learnJob(World.world.getMetier(metierId));
                        ((PlayerData) DatabaseManager.get(PlayerData.class)).update(player);
                        SocketManager.GAME_SEND_Ow_PACKET(player);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 986:
                if(client == null) return true;
                try {
                    int mapCurId = Integer.parseInt(args.split(",")[0]);
                    int item = Integer.parseInt(args.split(",")[1]);
                    int item2 = Integer.parseInt(args.split(",")[2]);
                    int metierId = Integer.parseInt(args.split(",")[3]);

                    if (player.getCurMap().getId() != (short) mapCurId)
                        return true;
                    Job metierArgs = World.world.getMetier(metierId);
                    if (metierArgs == null)
                        return true;

                    if (player.getMetierByID(metierId) != null) {
                        SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                        player.setExchangeAction(null);
                        player.setIsOnDialogAction(-1);
                        SocketManager.GAME_SEND_Im_PACKET(player, "111");
                        return true; // Si on a d�j� le m�tier
                    }

                    if (player.hasItemTemplate(item, 1, false)) {
                        player.removeByTemplateID(item, 1);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1
                                + "~" + item);
                        ObjectTemplate t = World.world.getObjTemplate(item2);
                        if (t != null) {
                            obj = t.createNewItem(1, false);
                            obj.refreshStatsObjet("325#0#0#"
                                    + System.currentTimeMillis());
                            if (player.addObjet(obj, false)) {
                                SocketManager.GAME_SEND_Im_PACKET(player, "021;"
                                        + 1 + "~" + obj.getTemplate().getId());
                                World.world.addGameObject(obj);
                                ((PlayerData) DatabaseManager.get(PlayerData.class)).update(player);
                                SocketManager.GAME_SEND_Ow_PACKET(player);
                                return false;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 987:
                if(client == null) return true;
                try {
                    int item = Integer.parseInt(args.split(",")[0]);
                    int item2 = Integer.parseInt(args.split(",")[1]);
                    int item3 = Integer.parseInt(args.split(",")[2]);
                    int mapCurId = Integer.parseInt(args.split(",")[3]);
                    int metierId = Integer.parseInt(args.split(",")[4]);

                    if (player.getCurMap().getId() != (short) mapCurId)
                        return true;
                    Job metierArgs = World.world.getMetier(metierId);
                    if (metierArgs == null)
                        return true;

                    if (player.getMetierByID(metierId) != null) {
                        SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                        player.setExchangeAction(null);
                        player.setIsOnDialogAction(-1);
                        SocketManager.GAME_SEND_Im_PACKET(player, "111");
                        return true; // Si on a d�j� le m�tier
                    }

                    if (player.hasItemTemplate(item, 1, false)
                            && player.hasItemTemplate(item2, 1, false)) {
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1
                                + "~" + item);
                        player.removeByTemplateID(item, 1);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1
                                + "~" + item2);
                        player.removeByTemplateID(item2, 1);

                        ObjectTemplate t = World.world.getObjTemplate(item3);
                        if (t != null) {
                            obj = t.createNewItem(1, false);
                            if (player.addObjet(obj, false)) {
                                SocketManager.GAME_SEND_Im_PACKET(player, "021;"
                                        + 1 + "~" + obj.getTemplate().getId());
                                World.world.addGameObject(obj);
                                ((PlayerData) DatabaseManager.get(PlayerData.class)).update(player);
                                SocketManager.GAME_SEND_Ow_PACKET(player);
                                return false;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 988: // devenir p�cheur
                if(client == null) return true;
                try {
                    if (player.hasItemTemplate(2107, 1, false)) {
                        long timeStamp = Long.parseLong(player.getItemTemplate(2107, 1).getTxtStat().get(Constant.STATS_DATE));
                        boolean success = (System.currentTimeMillis()
                                - timeStamp <= 2 * 60 * 1000);
                        NpcQuestion qQuest = World.world.getNPCQuestion(success ? 1171 : 1172);

                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1
                                + "~" + 2107);
                        player.removeByTemplateID(2107, 1);

                        if (qQuest == null) {
                            SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                            player.setExchangeAction(null);
                            return true;
                        }

                        if (success) {
                            Job metierArgs = World.world.getMetier(36);
                            if (metierArgs == null)
                                return true; // Si le m�tier n'existe pas
                            if (player.getMetierByID(36) != null) {
                                SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                                player.setExchangeAction(null);
                                player.setIsOnDialogAction(-1);
                                SocketManager.GAME_SEND_Im_PACKET(player, "111");
                                return true; // Si on a d�j� le m�tier
                            }

                            for (Entry<Integer, JobStat> entry : player.getMetiers().entrySet()) {
                                if (entry.getValue().get_lvl() < 30
                                        && !entry.getValue().getTemplate().isMaging()) {
                                    SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                                    player.setExchangeAction(null);
                                    player.setIsOnDialogAction(-1);
                                    SocketManager.GAME_SEND_Im_PACKET(player, "18;30");
                                    return true;
                                }
                            }

                            player.learnJob(World.world.getMetier(36));
                            ((PlayerData) DatabaseManager.get(PlayerData.class)).update(player);
                            SocketManager.GAME_SEND_Ow_PACKET(player);
                        }

                        SocketManager.GAME_SEND_QUESTION_PACKET(client, qQuest.parse(player));
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 989:
                if(client == null) return true;
                try {
                    int mapCurId = Integer.parseInt(args.split(",")[0]);
                    int item = Integer.parseInt(args.split(",")[1]);
                    int item2 = Integer.parseInt(args.split(",")[2]);
                    int metierId = Integer.parseInt(args.split(",")[3]);

                    if (player.getCurMap().getId() != (short) mapCurId)
                        return true;
                    Job metierArgs = World.world.getMetier(metierId);
                    if (metierArgs == null)
                        return true;

                    if (player.getMetierByID(metierId) != null) {
                        SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                        player.setExchangeAction(null);
                        player.setIsOnDialogAction(-1);
                        SocketManager.GAME_SEND_Im_PACKET(player, "111");
                        return true; // Si on a d�j� le m�tier
                    }

                    if (player.hasItemTemplate(item, 1, false)) {
                        ObjectTemplate t = World.world.getObjTemplate(item2);
                        if (t != null) {
                            obj = t.createNewItem(1, false);
                            if (player.addObjet(obj, false)) {
                                SocketManager.GAME_SEND_Im_PACKET(player, "021;"
                                        + 1 + "~" + obj.getTemplate().getId());
                                World.world.addGameObject(obj);
                                ((PlayerData) DatabaseManager.get(PlayerData.class)).update(player);
                                SocketManager.GAME_SEND_Ow_PACKET(player);
                                return false;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 990:
                if(client == null) return true;
                try {
                    if (player.getCurMap().getId() == (short) 7388) {
                        if (player.hasItemTemplate(2039, 1, false)
                                && player.hasItemTemplate(2041, 1, false)) {
                            long timeStamp = Long.parseLong(player.getItemTemplate(2039, 1).getTxtStat().get(Constant.STATS_DATE));
                            boolean success = (System.currentTimeMillis()
                                    - timeStamp <= 2 * 60 * 1000);
                            NpcQuestion qQuest = World.world.getNPCQuestion(success ? 2364 : 1175);

                            SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1
                                    + "~" + 2039);
                            player.removeByTemplateID(2039, 1);
                            SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1
                                    + "~" + 2041);
                            player.removeByTemplateID(2041, 1);

                            if (qQuest == null) {
                                SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                                player.setExchangeAction(null);
                                return true;
                            }

                            if (success) {
                                Job metierArgs = World.world.getMetier(41);
                                if (metierArgs == null)
                                    return true; // Si le m�tier n'existe pas
                                if (player.getMetierByID(41) != null) {
                                    SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                                    player.setExchangeAction(null);
                                    player.setIsOnDialogAction(-1);
                                    SocketManager.GAME_SEND_Im_PACKET(player, "111");
                                    return true; // Si on a d�j� le m�tier
                                }

                                for (Entry<Integer, JobStat> entry : player.getMetiers().entrySet()) {
                                    if (entry.getValue().get_lvl() < 30
                                            && !entry.getValue().getTemplate().isMaging()) {
                                        SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                                        player.setExchangeAction(null);
                                        player.setIsOnDialogAction(-1);
                                        SocketManager.GAME_SEND_Im_PACKET(player, "18;30");
                                        return true;
                                    }
                                }

                                player.learnJob(World.world.getMetier(41));
                                ((PlayerData) DatabaseManager.get(PlayerData.class)).update(player);
                                SocketManager.GAME_SEND_Ow_PACKET(player);
                            }

                            SocketManager.GAME_SEND_QUESTION_PACKET(client, qQuest.parse(player));
                            return false;
                        } else {
                            player.send("Im14");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 991: // Poss�der un item, lancer un combat contre un monstre
                try {
                    int mapCurId = Integer.parseInt(args.split(",")[0]);
                    int item = Integer.parseInt(args.split(",")[1]);
                    int monstre = Integer.parseInt(args.split(",")[2]);
                    int grade = Integer.parseInt(args.split(",")[3]);
                    if (player.getCurMap().getId() == (short) mapCurId) {
                        if (player.hasItemTemplate(item, 1, false)) {
                            String groupe = monstre + "," + grade + "," + grade
                                    + ";";
                            Monster.MobGroup Mgroupe = new Monster.MobGroup(player.getCurMap().nextObjectId, player.getCurMap(), player.getCurCell().getId(), groupe);
                            player.getCurMap().startFightVersusMonstres(player, Mgroupe); // Si bug startfight, voir "//Respawn d'un groupe fix" dans fight.java
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 992: // Supprime deux items & apprends un m�tier
                if(client == null) return true;
                try {
                    int item1 = Integer.parseInt(args.split(",")[0]);
                    int item2 = Integer.parseInt(args.split(",")[1]);
                    int mapCurId = Integer.parseInt(args.split(",")[2]);
                    int mId = Integer.parseInt(args.split(",")[3]);
                    if (player.getCurMap().getId() == (short) mapCurId) {
                        if (player.hasItemTemplate(item1, 1, false)) {
                            SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1
                                    + "~" + item1);
                            player.removeByTemplateID(item1, 1);
                        }
                        if (player.hasItemTemplate(item2, 1, false)) {
                            SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1
                                    + "~" + item2);
                            player.removeByTemplateID(item2, 1);
                        }

                        Job metierArgs = World.world.getMetier(mId);
                        if (metierArgs == null)
                            return true;
                        if (player.getMetierByID(mId) != null) {
                            SocketManager.GAME_SEND_Im_PACKET(player, "111");
                            return true;
                        }

                        for (Entry<Integer, JobStat> entry : player.getMetiers().entrySet()) {
                            if (entry.getValue().get_lvl() < 30
                                    && !entry.getValue().getTemplate().isMaging()) {
                                SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                                player.setExchangeAction(null);
                                player.setIsOnDialogAction(-1);
                                SocketManager.GAME_SEND_Im_PACKET(player, "18;30");
                                return true;
                            }
                        }

                        player.learnJob(World.world.getMetier(mId));
                        ((PlayerData) DatabaseManager.get(PlayerData.class)).update(player);
                        SocketManager.GAME_SEND_Ow_PACKET(player);
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 993: // Supprime deux items
                try {
                    int item1 = Integer.parseInt(args.split(",")[0]);
                    int item2 = Integer.parseInt(args.split(",")[1]);
                    if (player.hasItemTemplate(item1, 1, false)) {
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1
                                + "~" + item1);
                        player.removeByTemplateID(item1, 1);
                    }
                    if (player.hasItemTemplate(item2, 1, false)) {
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1
                                + "~" + item2);
                        player.removeByTemplateID(item2, 1);
                    }
                    ((PlayerData) DatabaseManager.get(PlayerData.class)).update(player);
                    SocketManager.GAME_SEND_Ow_PACKET(player);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 994: // donn� un item si on ne l'a pas d�j�
                try {
                    int mapID = Integer.parseInt(args.split(",")[0]);
                    int item = Integer.parseInt(args.split(",")[1]);
                    int metierId = Integer.parseInt(args.split(",")[2]);
                    Job metierArgs = World.world.getMetier(metierId);

                    if (metierArgs == null)
                        return true;
                    if (player.getMetierByID(metierId) != null) {
                        SocketManager.GAME_SEND_Im_PACKET(player, "111");
                        return true;
                    }

                    GameMap curMapP = player.getCurMap();
                    if (curMapP.getId() == (short) mapID) {
                        if (!player.hasItemTemplate(item, 1, false)) {
                            if (player.getMetierByID(41) != null) {
                                SocketManager.GAME_SEND_Im_PACKET(player, "182");
                                return true;
                            }
                            ObjectTemplate t = World.world.getObjTemplate(item);
                            if (t != null) {
                                obj = t.createNewItem(1, false);
                                obj.refreshStatsObjet("325#0#0#"
                                        + System.currentTimeMillis());
                                if (player.addObjet(obj, false)) {
                                    SocketManager.GAME_SEND_Im_PACKET(player, "021;"
                                            + 1
                                            + "~"
                                            + obj.getTemplate().getId());
                                    World.world.addGameObject(obj);
                                    ((PlayerData) DatabaseManager.get(PlayerData.class)).update(player);
                                    SocketManager.GAME_SEND_Ow_PACKET(player);
                                    return true;
                                }
                            }
                        }
                    }
                    return false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 995: // t�l�portation passage vers brakmar
                GameMap curMap2 = player.getCurMap();
                if (!player.isInPrison()) {
                    if (curMap2.getId() == (short) 11866) {
                        SocketManager.GAME_SEND_GA_PACKET(player.getGameClient(), "", "2", player.getId()
                                + "", "6");
                        player.teleport((short) 11862, 253);
                    } else if (curMap2.getId() == (short) 11862) {
                        SocketManager.GAME_SEND_GA_PACKET(player.getGameClient(), "", "2", player.getId()
                                + "", "6");
                        player.teleport((short) 11866, 344);
                    } else {
                        SocketManager.GAME_SEND_Im_PACKET(player, "182");
                        return true;
                    }
                }
                break;

            case 996: // t�l�portation mine chariot + Animation
                GameMap curMap = player.getCurMap();
                ArrayList<Integer> mapSecure = new ArrayList<Integer>();
                for (String i : args.split("\\,"))
                    mapSecure.add(Integer.parseInt(i));

                if (!mapSecure.contains((int) curMap.getId())) {
                    SocketManager.GAME_SEND_Im_PACKET(player, "182");
                    return true;
                }

                long pKamas4 = player.getKamas();
                if (pKamas4 < 50) {
                    player.teleport((short) 11862, 253);
                    return true;
                }

                if (!player.isInPrison()) {
                    SocketManager.GAME_SEND_GA_PACKET(player.getGameClient(), "", "2", player.getId()
                            + "", "6");
                    long pNewKamas4 = pKamas4 - 50;
                    if (pNewKamas4 < 0)
                        pNewKamas4 = 0;
                    player.setKamas(pNewKamas4);
                    if (player.isOnline())
                        SocketManager.GAME_SEND_STATS_PACKET(player);
                    SocketManager.GAME_SEND_Im_PACKET(player, "046;" + 50);
                    player.teleport((short) 10256, 211);
                }
                break;

            case 997: // Apprendre un m�tier de forgemagie
                try {
                    int metierID = Integer.parseInt(args.split(",")[0]);
                    int mapIdargs = Integer.parseInt(args.split(",")[1]);
                    Job metierArgs = World.world.getMetier(metierID);

                    if (metierArgs == null)
                        return true; // Si le m�tier n'existe pas
                    if (player.getMetierByID(metierID) != null) {
                        SocketManager.GAME_SEND_Im_PACKET(player, "111");
                        return true; // Si on a d�j� le m�tier
                    }

                    GameMap curMapPerso = player.getCurMap();
                    if (curMapPerso.getId() != (short) mapIdargs)
                        return true; // Map secure

                    if (metierArgs.isMaging()) // Si c'est du FM
                    {
                        JobStat metierBase = player.getMetierByID(World.world.getMetierByMaging(metierID));
                        if (metierBase == null)
                            return true; // Si la base n'existe pas
                        if (metierBase.get_lvl() < 65) {
                            SocketManager.GAME_SEND_Im_PACKET(player, "111");
                            return true; // Si la base n'est pas assez hl
                        } else if (player.totalJobFM() > 2) {
                            SocketManager.GAME_SEND_Im_PACKET(player, "19");
                            return true; // On compte les m�tiers d�ja acquis si c'est sup�rieur a 2 on ignore
                        } else {
                            player.learnJob(World.world.getMetier(metierID));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 998://Donjon abraknyde salle des cases
                if (player.getCurMap().getId() == 10154
                        && player.getCurCell().getId() == 142) {
                    player.teleport((short) 8721, 395);
                } else {
                    SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.action.apply.nofacetopnj"));
                }
                break;

            /** Fin Donjon **/
            case 999:
                player.teleport(this.map.getId(), Integer.parseInt(this.args));
                break;

            case 1000:
                map = Short.parseShort(this.args.split(",")[0]);
                cell = Integer.parseInt(this.args.split(",")[1]);
                player.teleport(map, cell);
                player.set_savePos(map+","+cell);
                SocketManager.GAME_SEND_Im_PACKET(player, "06");
                break;

            case 1001:
                map = Short.parseShort(this.args.split(",")[0]);
                cell = Integer.parseInt(this.args.split(",")[1]);
                player.teleport(map, cell);
                break;

            case 1002: // Add Multiple objects
                for(String s : this.args.split(";")) {
                    String[] s1 = s.split(",");
                    ObjectTemplate template = World.world.getObjTemplate(Integer.parseInt(s1[0]));
                    if(template != null) {
                        GameObject o = template.createNewItem(Integer.parseInt(s1[1]), false);
                        if(player.addObjet(o, true))
                            World.world.addGameObject(o);
                    }
                }
                break;

            default:
                break;
        }
        return true;
    }
}
