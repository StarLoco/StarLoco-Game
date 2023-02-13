package org.starloco.locos.object;

import org.starloco.locos.area.Area;
import org.starloco.locos.area.SubArea;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.area.map.entity.Animation;
import org.starloco.locos.area.map.entity.House;
import org.starloco.locos.area.map.entity.MountPark;
import org.starloco.locos.client.Player;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.game.PrismData;
import org.starloco.locos.database.data.game.SubAreaData;
import org.starloco.locos.database.data.login.GuildData;
import org.starloco.locos.database.data.login.PlayerData;
import org.starloco.locos.dynamic.Noel;
import org.starloco.locos.entity.Prism;
import org.starloco.locos.entity.mount.Mount;
import org.starloco.locos.entity.pet.PetEntry;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.spells.SpellEffect;
import org.starloco.locos.game.action.ExchangeAction;
import org.starloco.locos.game.world.World;
import org.starloco.locos.guild.Guild;
import org.starloco.locos.job.JobStat;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.object.entity.Fragment;
import org.starloco.locos.object.entity.SoulStone;
import org.starloco.locos.other.Action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.starloco.locos.kernel.Constant.*;

public class ObjectAction {

    private String type;
    private String args;
    private String cond;
    private boolean send = true;

    public ObjectAction(String type, String args, String cond) {
        this.type = type;
        this.args = args;
        this.cond = cond;
    }

    public void apply(Player player0, Player target, int objet, int cellid) {
        GameObject object = World.world.getGameObject(objet);

        if (object == null) {
            SocketManager.GAME_SEND_MESSAGE(player0, "Error object null. Merci de prévenir un administrateur est d'indiquer le message.");
            return;
        }
        if (player0 == null || !player0.isOnline() || player0.getDoAction() || player0.getGameClient() == null)
            return;

        if(object.getTemplate().getType() != 116) {// EPO Fami, this is fucked but, condition is dead
            if (!this.cond.equalsIgnoreCase("") && !this.cond.equalsIgnoreCase("-1") && !World.world.getConditionManager().validConditions(player0, this.cond)) {
                SocketManager.GAME_SEND_Im_PACKET(player0, "119");
                return;
            }
        }
        if (player0.getLevel() < World.world.getGameObject(objet).getTemplate().getLevel()) {
            SocketManager.GAME_SEND_Im_PACKET(player0, "119");
            return;
        }

        Player player = target != null ? target : player0;
        Fight fight = player.getFight();

        if(fight != null && !(fight.getState() == Constant.FIGHT_STATE_PLACE))
            return;

        boolean sureIsOk = false, isOk = true;
        int turn = 0;
        String arg = "";
        try {
            for (String type : this.type.split(";")) {
                String[] split = args.split("\\|", 2);
                if (!this.args.isEmpty() && split.length > turn)
                    arg = split[turn];

                switch (Integer.parseInt(type)) {
                    case -1:
                        if (player0.getFight() != null) return;
                        isOk = true;
                        send = false;
                        break;

                    case 0://T�l�portation.
                        if (player0.getFight() != null) return;
                        short mapId = Short.parseShort(arg.split(",", 2)[0]);
                        int cellId = Integer.parseInt(arg.split(",", 2)[1]);
                        if(mapId == 8978) {
                            isOk = false;
                            send = false;
                            return;
                        }
                        if (!player.cantTP())
                            player.teleport(mapId, cellId);
                        else if (player.getCurCell().getId() == 268)
                            player.teleport(mapId, cellId);
                        break;

                    case 1://T�l�portation au point de sauvegarde.
                        if (player0.getFight() != null) return;
                        if (!player.cantTP())
                            player.warpToSavePos();
                        break;

                    case 2://Don de Kamas.
                        if (player0.getFight() != null) return;
                        int count = Integer.parseInt(arg);
                        long curKamas = player.getKamas();
                        long newKamas = curKamas + count;
                        if (newKamas < 0)
                            newKamas = 0;
                        player.setKamas(newKamas);
                        if (player.isOnline())
                            SocketManager.GAME_SEND_STATS_PACKET(player);
                        break;

                    case 3://Don de vie.
                        if(this.type.split(";").length > 1 && player.getFight() != null) return;
                        boolean isOk1 = true,
                                isOk2 = true;
                        for (String arg0 : arg.split(",")) {
                            int val, statId1;
                            if (arg.contains(";")) {
                                statId1 = Integer.parseInt(arg.split(";")[0]);
                                val = World.world.getGameObject(objet).getRandomValue(World.world.getGameObject(objet).parseStatsString(), Integer.parseInt(arg.split(";")[0]));
                            } else {
                                statId1 = Integer.parseInt(arg0);
                                val = World.world.getGameObject(objet).getRandomValue(World.world.getGameObject(objet).parseStatsString(), Integer.parseInt(arg0));
                            }
                            switch (statId1) {
                                case 110://Vie.
                                    if (player.getCurPdv() == player.getMaxPdv()) {
                                        isOk1 = false;
                                        continue;
                                    }
                                    if (player.getCurPdv() + val > player.getMaxPdv())
                                        val = player.getMaxPdv() - player.getCurPdv();
                                    player.setPdv(player.getCurPdv() + val);
                                    if(player.getFight() != null)
                                        player.getFight().getFighterByPerso(player).setPdv(player.getCurPdv());
                                    SocketManager.GAME_SEND_STATS_PACKET(player);
                                    SocketManager.GAME_SEND_Im_PACKET(player, "01;" + val);
                                    sureIsOk = true;
                                    break;
                                case 139://Energie.
                                    if (player.getEnergy() == 10000) {
                                        isOk2 = false;
                                        continue;
                                    }
                                    if (player.getEnergy() + val > 10000)
                                        val = 10000 - player.getEnergy();
                                    player.setEnergy(player.getEnergy() + val);
                                    SocketManager.GAME_SEND_STATS_PACKET(player);
                                    SocketManager.GAME_SEND_Im_PACKET(player, "07;" + val);
                                    sureIsOk = true;
                                    break;
                                case 605://Exp�rience.
                                    player.addXp((long) (val * Config.rateXp));
                                    SocketManager.GAME_SEND_STATS_PACKET(player);
                                    SocketManager.GAME_SEND_Im_PACKET(player, "08;" + val);
                                    break;
                                case 614://Exp�rience m�tier.
                                    JobStat job = player.getMetierByID(Integer.parseInt(arg0.split(";")[1]));
                                    if (job == null) {
                                        isOk1 = false;
                                        isOk2 = false;
                                        continue;
                                    }

                                    boolean can = object.getTemplate().getId() >= 10382 && object.getTemplate().getId() <= 10407;
                                    val = val * (can ? Config.rateJob : 1);
                                    job.addXp(player, val);
                                    SocketManager.GAME_SEND_JX_PACKET(player, new ArrayList<>(Collections.singletonList(job)));
                                    SocketManager.GAME_SEND_Im_PACKET(player, "017;" + val + "~" + Integer.parseInt(arg0.split(";")[1]));
                                    sureIsOk = true;
                                    break;
                            }
                        }
                        if (arg.split(",").length == 1)
                            if (!isOk1 || !isOk2)
                                isOk = false;
                            else if (!isOk1 && !isOk2)
                                isOk = false;
                        send = false;
                        break;

                    case 4://Don de Stats.
                        if (player0.getFight() != null) return;
                        for (String arg0 : arg.split(",")) {
                            int statId = Integer.parseInt(arg0.split(";")[0]);
                            int val = Integer.parseInt(arg0.split(";")[1]);
                            switch (statId) {
                                case 1://Vitalit�.
                                    for (int i = 0; i < val; i++) {
                                        player.boostStat(11, false);
                                        player.getStatsParcho().addOneStat(Constant.STATS_ADD_VITA, 1);
                                    }
                                    break;
                                case 2://Sagesse.
                                    for (int i = 0; i < val; i++) {
                                        player.getStatsParcho().addOneStat(Constant.STATS_ADD_SAGE, 1);
                                        player.boostStat(12, false);
                                    }
                                    break;
                                case 3://Force.
                                    for (int i = 0; i < val; i++) {
                                        player.boostStat(10, false);
                                        player.getStatsParcho().addOneStat(Constant.STATS_ADD_FORC, 1);
                                    }
                                    break;
                                case 4://Intelligence.
                                    for (int i = 0; i < val; i++) {
                                        player.boostStat(15, false);
                                        player.getStatsParcho().addOneStat(Constant.STATS_ADD_INTE, 1);
                                    }
                                    break;
                                case 5://Chance.
                                    for (int i = 0; i < val; i++) {
                                        player.boostStat(13, false);
                                        player.getStatsParcho().addOneStat(Constant.STATS_ADD_CHAN, 1);
                                    }
                                    break;
                                case 6://Agilit�.
                                    for (int i = 0; i < val; i++) {
                                        player.boostStat(14, false);
                                        player.getStatsParcho().addOneStat(Constant.STATS_ADD_AGIL, 1);
                                    }
                                    break;
                                case 7://Point de Sort.
                                    player.setSpellPoints(player.get_spellPts()
                                            + val);
                                    break;
                            }
                        }
                        sureIsOk = true;
                        SocketManager.GAME_SEND_STATS_PACKET(player);
                        break;

                    case 5://F�e d'artifice.
                        if (player0.getFight() != null) {
                            isOk = false;
                            return;
                        }
                        int id0 = Integer.parseInt(arg);
                        Animation anim = World.world.getAnimation(id0);
                        if (player.getFight() != null) {
                            isOk = false;
                            return;
                        }
                        player.changeOrientation(1);
                        SocketManager.GAME_SEND_GA_PACKET_TO_MAP(player.getCurMap(), "0", 228, player.getId() + ";" + cellid + "," + anim.prepareToGA(), "");
                        break;

                    case 6://Apprendre un sort.
                        if (player0.getFight() != null) return;
                        id0 = Integer.parseInt(arg);
                        if (World.world.getSort(id0) == null) {
                            isOk = false;
                            return;
                        }
                        if (!player.learnSpell(id0, (player.getCurMap().getId() == 10129 ? 6 : 1), true, true, true)) {
                            isOk = false;
                            return;
                        }
                        send = false;
                        break;

                    case 7://D�sapprendre un sort.
                        if (player0.getFight() != null) {
                            isOk = false;
                            return;
                        }
                        id0 = Integer.parseInt(arg);
                        int oldLevel = player.getSortStatBySortIfHas(id0).getLevel();
                        if (player.getSortStatBySortIfHas(id0) == null) {
                            isOk = false;
                            return;
                        }
                        if (oldLevel <= 1) {
                            isOk = false;
                            return;
                        }
                        player.unlearnSpell(player, id0, 1, oldLevel, true, true);
                        break;

                    case 8://D�sapprendre un sort � un percepteur.
                        final Guild guild = player0.getGuild();
                        if (player0.getFight() != null || guild == null || player0.getGuildMember() == null)  {
                            isOk = false;
                            return;
                        }

                        GameObject obj = World.world.getGameObject(objet);

                        if(obj != null) {
                            int spell = obj.getStats().get(Constant.STATS_FORGET_ONE_LEVEL_SPELL);

                            if(spell != 0) {
                                if (spell <= 4) {
                                    int quantity = -1;
                                    switch (spell) {
                                        case 1: // Pods
                                            quantity = guild.resetStats(158);
                                            break;
                                        case 2: // Nb collectors
                                            quantity = guild.getNbCollectors();
                                            guild.setNbCollectors(0);
                                            guild.setCapital(guild.getCapital() + quantity * 10);
                                            quantity = -1;
                                            break;
                                        case 3: // Prospection
                                            quantity = guild.resetStats(176);
                                            break;
                                        case 4: // Sagesse
                                            quantity = guild.resetStats(124);
                                            break;
                                    }
                                    if (quantity != -1) {
                                        guild.setCapital(guild.getCapital() + quantity);
                                    }
                                } else {
                                    guild.unBoostSpell(spell);
                                }
                                isOk = true;
                                send = true;
                                ((GuildData) DatabaseManager.get(GuildData.class)).update(guild);
                                SocketManager.GAME_SEND_gIB_PACKET(player0, guild.parseCollectorToGuild());
                                break;
                            }
                        }
                        isOk = false;
                        send = false;
                        break;

                    case 9://Oubli� un m�tier.
                        if (player0.getFight() != null)  {
                            isOk = false;
                            return;
                        }
                        int job = Integer.parseInt(arg);
                        JobStat jobStats = player.getMetierByID(job);

                        if (jobStats == null) {
                            player.send("Im149" + job);
                            return;
                        }

                        player.unlearnJob(jobStats.getId());
                        SocketManager.GAME_SEND_STATS_PACKET(player);
                        ((PlayerData) DatabaseManager.get(PlayerData.class)).update(player);
                        player.send("JR" + job);
                        break;

                    case 10://EPO.
                        if (player0.getFight() != null)
                            return;

                        obj = World.world.getGameObject(objet);
                        if (obj == null)
                            return;
                        GameObject object0 = player.getObjetByPos(Constant.ITEM_POS_FAMILIER);
                        if (object0 == null)
                            return;
                        PetEntry pets = World.world.getPetsEntry(object0.getGuid());
                        if (pets == null)
                            return;
                        if (obj.getTemplate().getConditions().contains(object0.getTemplate().getId() + ""))
                            pets.giveEpo(player);
                        else
                            isOk = false;
                        break;

                    case 11://Chang� de Sexe.
                        if (player0.getFight() != null) return;
                        if (player.getSexe() == 0)
                            player.setSexe(1);
                        else
                            player.setSexe(0);

                        SocketManager.GAME_SEND_ALTER_GM_PACKET(player.getCurMap(), player);
                        ((PlayerData) DatabaseManager.get(PlayerData.class)).updateInfos(player);
                        break;

                    case 12://Chang� de nom.
                        if (player0.getFight() != null) return;
                        player.setChangeName(true);
                        isOk = false;
                        send = false;
                        break;

                    case 13://Apprendre une �mote.
                        if (player0.getFight() != null) return;
                        int emote = Integer.parseInt(arg);

                        if (player.getEmotes().contains(emote)) {
                            isOk = false;
                            return;
                        }

                        player.addStaticEmote(emote);
                        break;

                    case 14://Apprendre un m�tier.
                        if (player0.getFight() != null) return;
                        job = Integer.parseInt(arg);
                        if (World.world.getMetier(job) == null)
                            return;
                        if (player.getMetierByID(job) != null)//M�tier d�j� appris
                        {
                            SocketManager.GAME_SEND_Im_PACKET(player, "111");
                            return;
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
                            SocketManager.GAME_SEND_Im_PACKET(player, "18;30");
                            return;
                        }
                        if (player.totalJobBasic() > 2) {
                            SocketManager.GAME_SEND_Im_PACKET(player, "19");
                            return;
                        } else {
                            if (job == 27) {
                                if (!player.hasItemTemplate(966, 1, false))
                                    return;
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + 966 + "~" + 1);
                                player.learnJob(World.world.getMetier(job));
                            } else {
                                player.learnJob(World.world.getMetier(job));
                            }
                        }
                        break;

                    case 15://TP au foyer.
                        if (player0.getFight() != null) return;
                        boolean tp = false;
                        for (House i : World.world.getHouses().values()) {
                            if (i.getOwnerId() == player.getAccount().getId()) {
                                player.teleport((short) i.getHouseMapId(), i.getHouseCellId());
                                tp = true;
                                break;
                            }
                        }
                        if(!tp) {
                            player.send("Im161");
                            return;
                        }
                        break;

                    case 16://Pnj Follower.
                        if (player0.getFight() != null) return;
                        // Petite larve dor�e = 7425
                        player.setMascotte(Integer.parseInt(this.args));
                        break;

                    case 17://B�n�diction.
                        if (player0.getFight() != null) return;
                        player.setBenediction(World.world.getGameObject(objet).getTemplate().getId());
                        break;

                    case 18://Mal�diction.
                        if (player0.getFight() != null) return;
                        player.setMalediction(World.world.getGameObject(objet).getTemplate().getId());
                        break;

                    case 19://RolePlay Buff.
                        if (player0.getFight() != null) return;
                        player.setRoleplayBuff(World.world.getGameObject(objet).getTemplate().getId());
                        break;

                    case 20://Bonbon.
                        if (player0.getFight() != null) return;
                        player.setCandy(World.world.getGameObject(objet).getTemplate().getId());
                        break;

                    case 21://Poser un objet d'�levage.
                        if (player0.getFight() != null) return;
                        GameMap map0 = player.getCurMap();
                        object0 = World.world.getGameObject(objet);
                        id0 = object0.getTemplate().getId();

                        int resist = object0.getResistance(object0.parseStatsString());
                        int resistMax = object0.getResistanceMax(object0.getTemplate().getStrTemplate());
                        if (map0.getMountPark() == null)
                            return;
                        MountPark MP = map0.getMountPark();
                        if (player.getGuild() == null) {
                            SocketManager.GAME_SEND_BN(player);
                            return;
                        }
                        if (!player.getGuildMember().canDo(Constant.G_AMENCLOS)) {
                            SocketManager.GAME_SEND_Im_PACKET(player, "193");
                            return;
                        }
                        if (MP.getCellOfObject().size() == 0 || !MP.getCellOfObject().contains(cellid) || MP.getCellAndObject().containsKey(cellid)) {
                            SocketManager.GAME_SEND_BN(player);
                            return;
                        }
                        if (MP.getObject().size() < MP.getMaxObject()) {
                            MP.addObject(cellid, id0, player.getId(), resistMax, resist);
                            SocketManager.SEND_GDO_PUT_OBJECT_MOUNT(map0, cellid + ";" + id0 + ";1;" + resist + ";" + resistMax);
                        } else {
                            SocketManager.GAME_SEND_Im_PACKET(player, "1107");
                            return;
                        }
                        break;

                    case 22://Poser un prisme.
                        int cellId1 = player.getCurCell().getId();
                        if (player0.getFight() != null || cellId1 <= 0)
                            return;
                        map0 = player.getCurMap();
                        SubArea subArea = map0.getSubArea();
                        Area area = subArea.getArea();
                        int alignement = player.getAlignment();

                        if (player.getLevel() < 10 || alignement == 0 || alignement == 3) {
                            player.send("Im1155");
                            //SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("object.objectaction.apply.prisme.noalign"));
                            return;
                        }
                        if (!player.is_showWings()) {
                            player.send("Im1148");
                            //SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("object.objectaction.apply.prisme.wing.noactive"));
                            return;
                        }
                        if(!subArea.ownNearestSubArea(player)) {
                            player.send("Im1147");
                            return;
                        }
                        if (map0.getPlaces().equalsIgnoreCase("|") || map0.getPlaces().isEmpty() || map0.noPrisms
                                || area.getId() == 42 || (subArea != null && (subArea.getId() == 9
                                || subArea.getId() == 95)) || map0.haveMobFix()) {
                            player.send("Im1146");
                            //SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("object.objectaction.apply.prisme.error.map"));
                            return;
                        }
                        if (subArea.getAlignment() != 0 || !subArea.getConquerable()) {
                            player.send("Im1149");
                            //SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("object.objectaction.apply.prisme.error.area"));
                            return;
                        }
                        if(subArea.isMoreThanEnemies(player)) {
                            player.send("Im1153");
                            return;
                        }

                        Prism prism = new Prism(World.world.getNextIDPrisme(), (byte) alignement, 1, map0.getId(), cellId1, player.get_honor(), -1);
                        subArea.setAlignment((byte) alignement);
                        subArea.setPrism(prism);

                        for (Player z : World.world.getOnlinePlayers()) {
                            if (z == null)
                                continue;
                            if (z.getAlignment() == 0) {
                                SocketManager.GAME_SEND_am_ALIGN_PACKET_TO_SUBAREA(z, subArea.getId() + "|" + alignement + "|1");
                                if (area.getAlignement() == 0)
                                    SocketManager.GAME_SEND_aM_ALIGN_PACKET_TO_AREA(z, area.getId() + "|" + alignement);
                                continue;
                            }
                            SocketManager.GAME_SEND_am_ALIGN_PACKET_TO_SUBAREA(z, subArea.getId() + "|" + alignement + "|0");
                            if (area.getAlignement() == 0)
                                SocketManager.GAME_SEND_aM_ALIGN_PACKET_TO_AREA(z, area.getId() + "|" + alignement);
                        }
                        if (area.getAlignement() == 0) {
                            area.setPrismId(prism.getId());
                            area.setAlignement(alignement);
                            prism.setConquestArea(area.getId());
                        }
                        World.world.addPrisme(prism);
                        ((PrismData) DatabaseManager.get(PrismData.class)).insert(prism);
                        player.getCurMap().getSubArea().setAlignment(player.getAlignment());
                        ((SubAreaData) DatabaseManager.get(SubAreaData.class)).update(player.getCurMap().getSubArea());
                        SocketManager.GAME_SEND_PRISME_TO_MAP(map0, prism);
                        break;

                    case 23://Rappel Prismatique.
                        if (player0.getFight() != null) return;
                        int dist = 99999, alea;
                        mapId = 0;
                        cellId = 0;
                        for (Prism i : World.world.AllPrisme()) {
                            if (i.getAlignment() != player.getAlignment())
                                continue;
                            alea = (World.world.getMap(i.getMap()).getX() - player.getCurMap().getX())
                                    * (World.world.getMap(i.getMap()).getX() - player.getCurMap().getX())
                                    + (World.world.getMap(i.getMap()).getY() - player.getCurMap().getY())
                                    * (World.world.getMap(i.getMap()).getY() - player.getCurMap().getY());
                            if (alea < dist) {
                                dist = alea;
                                mapId = i.getMap();
                                cellId = i.getCell();
                            }
                        }
                        if (mapId != 0)
                            player.teleport(mapId, cellId);
                        break;

                    case 24://TP Village align�.
                        if (player0.getFight() != null || player0.getAlignment() == 0 || player0.getAlignment() == 3) {
                            isOk = false;
                            send = false;
                            return;
                        }
                        mapId = (short) Integer.parseInt(arg.split(",")[0]);
                        cellId = Integer.parseInt(arg.split(",")[1]);
                        if (World.world.getMap(mapId).getSubArea().getAlignment() == player.getAlignment())
                            player.teleport(mapId, cellId);
                        break;

                    case 25://Spawn groupe.
                        if (player0.getFight() != null || player0.getCurMap().haveMobFix()) return;
                        boolean inArena = arg.split(";")[0].equals("true");
                        String groupData = "";
                        if (inArena && !SoulStone.isInArenaMap(player.getCurMap().getId()))
                            return;
                        if (arg.split(";")[1].charAt(0) == '1') {
                            groupData = arg.split("@")[1];
                        } else {
                            SoulStone soulStone = (SoulStone) World.world.getGameObject(objet);
                            groupData = soulStone.parseGroupData();
                        }
                        String condition = "MiS = " + player.getId();
                        player.getCurMap().spawnNewGroup(true, player.getCurCell().getId(), groupData, condition);
                        break;

                    case 26://Ajout d'objet.
                        if (player0.getFight() != null) return;
                        for (String i : arg.split(";")) {
                            obj = World.world.getObjTemplate(Integer.parseInt(i.split(",")[0])).createNewItem(Integer.parseInt(i.split(",")[1]), false);
                            if (player.addObjet(obj, true))
                                World.world.addGameObject(obj);
                        }
                        SocketManager.GAME_SEND_Ow_PACKET(player);
                        break;

                    case 27://Ajout de titre.
                        if (player0.getFight() != null) return;
                        player.setAllTitle(arg);
                        break;

                    case 28://Ajout de zaap.
                        if (player0.getFight() != null) return;
                        player.verifAndAddZaap((short) Integer.parseInt(arg));
                        break;

                    case 29://Panel d'oubli de sort.
                        if (player0.getFight() != null) return;
                        player.setExchangeAction(new ExchangeAction<>(ExchangeAction.FORGETTING_SPELL, 0));
                        SocketManager.GAME_SEND_FORGETSPELL_INTERFACE('+', player);
                        break;

                    case 31://Cadeau bworker.
                        if (player0.getFight() != null) return;
                        new Action(511, "", "", null).apply(player, null, objet, -1);
                        break;

                    case 32://G�oposition traque.
                        if (player0.getFight() != null) return;
                        String traque = World.world.getGameObject(objet).getTraquedName();

                        if (traque == null)
                            break;

                        Player cible = World.world.getPlayerByName(traque);

                        if (cible == null || cible.getAlignment() == 0 || (cible.getAlignment() == player0.getAlignment())) {
                            isOk = true;
                            send = true;
                        } else {
                            if (!cible.isOnline()) {
                                SocketManager.GAME_SEND_Im_PACKET(player, "1198");
                                break;
                            }

                            SocketManager.GAME_SEND_FLAG_PACKET(player, cible);
                        }
                        break;

                    case 33://Ajout de points boutique.
                        if (player0.getFight() != null) return;
                        player.getAccount().setPoints(player.getAccount().getPoints() + Integer.parseInt(arg));
                        player.sendTypeMessage("Shop", "Points : " + player.getAccount().getPoints());
                        break;

                    case 34://Fm cac
                        GameObject gameObject = player.getObjetByPos(Constant.ITEM_POS_ARME);

                        if(gameObject == null) {
                            player.sendMessage(player.getLang().trans("objet.objectaction.fmcac.noequip"));
                            isOk = false;
                            send = false;
                            return;
                        }

                        boolean containNeutre = false;

                        for(SpellEffect effect : gameObject.getEffects())
                            if(effect.getEffectID() == 100 || effect.getEffectID() == 95)
                                containNeutre = true;

                        if(containNeutre) {
                            for(int i = 0; i < gameObject.getEffects().size(); i++) {
                                if(gameObject.getEffects().get(i).getEffectID() == 100) {
                                    switch(this.args.toUpperCase()) {
                                        case "EAU": gameObject.getEffects().get(i).setEffectID(96); break;
                                        case "TERRE": gameObject.getEffects().get(i).setEffectID(97); break;
                                        case "AIR": gameObject.getEffects().get(i).setEffectID(98); break;
                                        case "FEU": gameObject.getEffects().get(i).setEffectID(99); break;
                                    }
                                }
                                if(gameObject.getEffects().get(i).getEffectID() == 95) {
                                    switch(this.args.toUpperCase()) {
                                        case "EAU": gameObject.getEffects().get(i).setEffectID(91); break;
                                        case "TERRE": gameObject.getEffects().get(i).setEffectID(92); break;
                                        case "AIR": gameObject.getEffects().get(i).setEffectID(93); break;
                                        case "FEU": gameObject.getEffects().get(i).setEffectID(94); break;
                                    }
                                }
                            }

                            SocketManager.GAME_SEND_STATS_PACKET(player);
                            SocketManager.GAME_SEND_UPDATE_ITEM(player, gameObject);
                            player.sendMessage(player.getLang().trans("objet.objectaction.fmcac.succes"));
                        } else {
                            player.sendMessage(player.getLang().trans("objet.objectaction.fmcac.noneutre"));
                            isOk = false;
                            send = false;
                        }
                        break;

                    case 35: // Mount cameleon
                        if(player.getMount() != null) {
                            player.getMount().getCapacitys().add(9);
                            player.getMount().setCastrated();
                            if(player.isOnMount()) {
                                SocketManager.GAME_SEND_ALTER_GM_PACKET(player.getCurMap(), player);
                            }
                            SocketManager.GAME_SEND_MOUNT_DESCRIPTION_PACKET(player, player.getMount());
                            player.sendMessage(player.getLang().trans("objet.objectaction.mountcameleon"));
                            sureIsOk = true;
                            send = true;
                        } else {
                            player.sendMessage(player.getLang().trans("objet.objectaction.mountcameleon.none"));
                            return;
                        }
                        break;
                    case 36://Coffre
                        if (player0.getFight() != null || player0.getLevel() == 1) return;
                        int tour = 0;
                        List<ObjectTemplate> objects = new ArrayList<>();
                        int nbrMaxItem = 0;
                        for (String i : arg.split(";")) {
                            tour ++;
                            switch (tour){
                                case 1:
                                    List<ObjectTemplate> templates = new ArrayList<>();
                                    final int maxLvl = player.getLevel() > 150 ? 150 : player.getLevel();
                                    final int minLvl = player.getLevel() > 150 ? 120 : (player.getLevel()-30 <= 0) ? 1 : player.getLevel()-30;

                                    for (int j = 0; j < Integer.parseInt(i); j++){
                                        do {
                                            templates.clear();
                                            World.world.getObjTemplates().stream().filter(t -> t.isAnEquipment(false, Arrays.asList(Constant.ITEM_TYPE_FAMILIER, Constant.ITEM_TYPE_CERTIF_MONTURE)) && t.getLevel() == Formulas.getRandomValue(minLvl, maxLvl)).forEach(templates::add);
                                        } while (templates.size() == 0);
                                        objects.add(templates.get(Formulas.getRandomValue(0, templates.size()-1)));
                                    }
                                    break;
                                case 2:
                                    String[] size = i.split("-");
                                    player.addKamas(Formulas.getRandomValue(Integer.parseInt(size[0]), Integer.parseInt(size[1])));
                                    break;
                                case 3:
                                    nbrMaxItem = Integer.parseInt(i);
                                    break;
                            }
                        }

                        for (ObjectTemplate template : objects) {
                            if (nbrMaxItem > 0){
                                obj = template.createNewItem(1, true);
                                nbrMaxItem--;
                            } else {
                                obj = template.createNewItem(1, false);
                            }
                            if (player.addObjet(obj, true))
                                World.world.addGameObject(obj);
                            SocketManager.GAME_SEND_Im_PACKET(player, "021;1~" + template.getId());
                        }
                        SocketManager.GAME_SEND_Ow_PACKET(player);
                        SocketManager.GAME_SEND_STATS_PACKET(player);
                        break;
                    case 37: // Coffre divers
                        if (player0.getFight() != null) return;
                        ObjectTemplate template = null;
                        List<ObjectTemplate> templates = new ArrayList<>();
                        boolean max = false;
                        switch (Integer.parseInt(arg)){
                            case 2: //Sort
                                World.world.getObjTemplates().stream().filter(t -> t.getType() == ITEM_TYPE_PARCHEMIN_SORT).forEach(templates::add);
                                template = templates.get(Formulas.random.nextInt(templates.size()));
                                break;
                            case 3: //Maitrise
                                World.world.getObjTemplates().stream().filter(t -> t.getType() == ITEM_TYPE_MAITRISE).forEach(templates::add);
                                template = templates.get(Formulas.random.nextInt(templates.size()));
                                break;
                            case 4: //Obji
                                World.world.getObjTemplates().stream().filter(t -> t.getType() == ITEM_TYPE_OBJET_VIVANT).forEach(templates::add);
                                template = templates.get(Formulas.random.nextInt(templates.size()));
                                break;
                            case 5: //Fami
                                World.world.getObjTemplates().stream().filter(t -> t.getType() == ITEM_TYPE_FAMILIER).forEach(templates::add);
                                template = templates.get(Formulas.random.nextInt(templates.size()));
                                max = true;
                                break;
                            case 6:
                                int[] item = new int[3];
                                item[0] = 7493; item[1] = 7494; item[2] = 7495;
                                template = World.world.getObjTemplate(item[Formulas.getRandomValue(0, 2)]);
                                break;
                        }
                        obj = template.createNewItem(1, max);
                        if (player.addObjet(obj, true))
                            World.world.addGameObject(obj);
                        SocketManager.GAME_SEND_Im_PACKET(player, "021;1~" + template.getId());
                        break;
                    case 38: // Coffre dragondinde
                        if (player0.getFight() != null) return;
                        templates = new ArrayList<>();
                        final List<Integer> acceptedMount = Arrays.asList(7808,7810,7811,7812,7813,7814,7815,7816,7817,7818,7819,7820,7821,7822);
                        World.world.getObjTemplates().stream().filter(t -> t.getType() == ITEM_TYPE_CERTIF_MONTURE && acceptedMount.contains(t.getId()) ).forEach(templates::add);
                        template = templates.get(Formulas.random.nextInt(templates.size()));

                        obj = template.createNewItem(1, false);
                        Mount mount = new Mount(Constant.getMountColorByParchoTemplate(template.getId()), player.getId(), false);
                        obj.clearStats();
                        obj.getStats().addOneStat(995, (mount.getId()));
                        obj.getTxtStat().put(996, player.getName());
                        obj.getTxtStat().put(997, mount.getName());
                        mount.setCastrated();
                        mount.setToMax();

                        if (player.addObjet(obj, true))
                            World.world.addGameObject(obj);
                        SocketManager.GAME_SEND_Im_PACKET(player, "021;1~" + template.getId());
                        break;
                    case 39: // Changer de couleur
                        player.send("bC");
                        send = false;
                        isOk = false;
                        break;

                }
                turn++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean effect = this.haveEffect(World.world.getGameObject(objet).getTemplate().getId(), World.world.getGameObject(objet), player);
        if (effect)
            isOk = true;
        if (isOk)
            effect = true;
        if (this.type.split(";").length > 1)
            isOk = true;
        if (objet != -1) {
            if (send)
                SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + World.world.getGameObject(objet).getTemplate().getId());
            if (sureIsOk || (isOk && effect && World.world.getGameObject(objet).getTemplate().getId() != 7799)) {
                if (World.world.getGameObject(objet) != null) {
                    player0.removeItem(objet, 1, true, true);
                }
            }
        }
    }

    private boolean haveEffect(int id, GameObject gameObject, Player player) {
        if (player.getFight() != null) return true;
        switch (id) {
            case 8378://Fragment magique.
                for (World.Couple<Integer, Integer> couple : ((Fragment) gameObject).getRunes()) {
                    ObjectTemplate objectTemplate = World.world.getObjTemplate(couple.first);

                    if (objectTemplate == null)
                        continue;

                    GameObject newGameObject = objectTemplate.createNewItem(couple.second, true);

                    if (newGameObject == null)
                        continue;

                    if (!player.addObjetSimiler(newGameObject, true, -1)) {
                        World.world.addGameObject(newGameObject);
                        player.addObjet(newGameObject);
                    }
                }
                send = true;
                return true;
            case 7799://Le Saut Sifflard
                player.toogleOnMount();
                send = false;
                return false;

            case 10832://Craqueloroche
                if (player.getFight() != null || player.getCurMap().haveMobFix()) return false;
                player.getCurMap().spawnNewGroup(true, player.getCurCell().getId(), "483,1,1000", "MiS="
                        + player.getId());
                return true;

            case 10664://Abragland
                if (player.getFight() != null || player.getCurMap().haveMobFix()) return false;
                player.getCurMap().spawnNewGroup(true, player.getCurCell().getId(), "47,1,1000", "MiS="
                        + player.getId());
                return true;

            case 10665://Coffre de Jorbak
                player.setCandy(10688);
                return true;

            case 10670://Parchemin de persimol
                player.setBenediction(10682);
                return true;

            case 8435://Ballon Rouge Magique
                SocketManager.sendPacketToMap(player.getCurMap(), "GA;208;"
                        + player.getId() + ";" + player.getCurCell().getId()
                        + ",2906,11,8,1");
                return true;

            case 8624://Ballon Bleu Magique
                SocketManager.sendPacketToMap(player.getCurMap(), "GA;208;"
                        + player.getId() + ";" + player.getCurCell().getId()
                        + ",2907,11,8,1");
                return true;

            case 8625://Ballon Vert Magique
                SocketManager.sendPacketToMap(player.getCurMap(), "GA;208;"
                        + player.getId() + ";" + player.getCurCell().getId()
                        + ",2908,11,8,1");
                return true;

            case 8430://Ballon Jaune Magique
                SocketManager.sendPacketToMap(player.getCurMap(), "GA;208;"
                        + player.getId() + ";" + player.getCurCell().getId()
                        + ",2909,11,8,1");
                return true;

            case 8621://Cawotte Maudite
                player.setGfxId(1109);
                player.set_orientation(1);
                SocketManager.GAME_SEND_ALTER_GM_PACKET(player.getCurMap(), player);
                SocketManager.GAME_SEND_eD_PACKET_TO_MAP(player.getCurMap(), player.getId(), 1);
                return true;

            case 8626://Nisitik Miditik
                player.setGfxId(1046);
                player.set_orientation(1);
                SocketManager.GAME_SEND_ALTER_GM_PACKET(player.getCurMap(), player);
                SocketManager.GAME_SEND_eD_PACKET_TO_MAP(player.getCurMap(), player.getId(), 1);
                return true;

            case 10833://Chapain
                player.setGfxId(9001);
                player.set_orientation(1);
                SocketManager.GAME_SEND_ALTER_GM_PACKET(player.getCurMap(), player);
                SocketManager.GAME_SEND_eD_PACKET_TO_MAP(player.getCurMap(), player.getId(), 1);
                return true;

            case 10839://Monstre Pain
                if (player.getFight() != null || player.getCurMap().haveMobFix()) return false;
                player.getCurMap().spawnNewGroup(true, player.getCurCell().getId(), "2787,1,1000", "MiS="
                        + player.getId());
                return true;

            case 8335://Cadeau 1
                Noel.getRandomObjectOne(player);
                return true;
            case 8336://Cadeau 2
                Noel.getRandomObjectTwo(player);
                return true;
            case 8337://Cadeau 3
                Noel.getRandomObjectTree(player);
                return true;
            case 8339://Cadeau 4
                Noel.getRandomObjectFour(player);
                return true;
            case 8340://Cadeau 5
                Noel.getRandomObjectFive(player);
                return true;
            case 10912://Cadeau nowel 1
                return false;
            case 10913://Cadeau nowel 2
                return false;
            case 10914://Cadeau nowel 3
                return false;

        }
        return false;
    }
}
