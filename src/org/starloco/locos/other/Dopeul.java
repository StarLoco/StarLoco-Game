package org.starloco.locos.other;

import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.client.Player;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.login.PlayerData;
import org.starloco.locos.game.action.ExchangeAction;
import org.starloco.locos.game.world.World;
import org.starloco.locos.game.world.World.Couple;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.object.ObjectTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Dopeul {

    private static Map<Integer, Couple<Integer, Integer>> donjons = new HashMap<>();

    public static Map<Integer, Couple<Integer, Integer>> getDonjons() {
        return donjons;
    }

    public static void getReward(Player player, int type) {
        GameMap curMap = player.getCurMap();
        int idMap = World.world.getTempleByClasse(player.getClasse());
        switch (type) {
            case 1://Sort sp�cial
                if (!player.hasItemTemplate(getDoplonByClasse(player.getClasse()), 1, false)) { // Si on a pas le doplon de classe
                    SocketManager.GAME_SEND_Im_PACKET(player, "14");
                    return;
                } else if (curMap.getId() != (short) idMap) // Si on est pas dans le temple de notre classe
                {
                    SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.dopeul.getreward.notempleclass"));
                    return;
                } else if (player.hasSpell(Constant.getSpecialSpellByClasse(player.getClasse()))) // Si on a d�j� le sort
                {
                    SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.dopeul.getreward.spellexist"));
                    return;
                }

                player.learnSpell(Constant.getSpecialSpellByClasse(player.getClasse()), 1, true, true, true);
                removeObject(player, getDoplonByClasse(player.getClasse()), 1);
                break;

            case 2://Trousseau de cl�s
                if (player.hasItemTemplate(10207, 1, false)) {
                    SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.dopeul.getreward.bunchofkeys"));
                    return;
                }
                int doplon = hasOneDoplon(player);
                if (doplon == -1) {
                    SocketManager.GAME_SEND_Im_PACKET(player, "14");
                    return;
                }
                GameObject obj = World.world.getObjTemplate(10207).createNewItem(1, true);
                if (player.addObjet(obj, false))
                    World.world.addGameObject(obj);
                removeObject(player, doplon, 1);
                break;

            case 3://Reset spell
                ArrayList<Integer> doplons = hasQuaDoplon(player, 7);

                if(doplons.contains(Dopeul.getDoplonByClasse(player.getClasse()))) {
                    removeObject(player, Dopeul.getDoplonByClasse(player.getClasse()), 7);
                } else {
                    doplons = Dopeul.hasQuaDoplon(player, 1);
                    if(doplons.size() == 12) {
                        for (int id : doplons) removeObject(player, id, 1);
                    } else {
                        SocketManager.GAME_SEND_Im_PACKET(player, "14");
                        return;
                    }
                }

                player.setExchangeAction(new ExchangeAction<>(ExchangeAction.FORGETTING_SPELL, 0));
                SocketManager.GAME_SEND_FORGETSPELL_INTERFACE('+', player);
                break;

            case 4://Reset caract�ristiques
                if (!player.hasItemTemplate(getDoplonByClasse(player.getClasse()), 1, false)) {
                    SocketManager.GAME_SEND_Im_PACKET(player, "14");
                    return;
                } else if (curMap.getId() != (short) idMap) // Si on est pas dans le temple de notre classe
                {
                    SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.dopeul.getreward.notempleclass"));
                    return;
                } else if (player.hasItemTemplate(10601, 1, false)) {
                    SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.dopeul.getreward.recons.again"));
                    return;
                }
                player.getStats().addOneStat(125, -player.getStats().getEffect(125));
                player.getStats().addOneStat(124, -player.getStats().getEffect(124));
                player.getStats().addOneStat(118, -player.getStats().getEffect(118));
                player.getStats().addOneStat(123, -player.getStats().getEffect(123));
                player.getStats().addOneStat(119, -player.getStats().getEffect(119));
                player.getStats().addOneStat(126, -player.getStats().getEffect(126));
                player.addCapital((player.getLevel() - 1) * 5
                        - player.getCapital());

                ObjectTemplate OT = World.world.getObjTemplate(10601); // On lui donne un certificat de restat
                GameObject obj2 = OT.createNewItem(1, false);
                if (player.addObjet(obj2, true)) //Si le joueur n'avait pas d'item similaire
                    World.world.addGameObject(obj2);
                obj2.refreshStatsObjet("325" + System.currentTimeMillis());
                SocketManager.GAME_SEND_STATS_PACKET(player);
                removeObject(player, getDoplonByClasse(player.getClasse()), 1);
                break;

            case 5://Guildalogemme
                doplons = hasQuaDoplon(player, 1);
                if (doplons == null) {
                    SocketManager.GAME_SEND_Im_PACKET(player, "14");
                    return;
                }
                obj = World.world.getObjTemplate(1575).createNewItem(1, true);
                if (player.addObjet(obj, false))
                    World.world.addGameObject(obj);
                for (int id : doplons)
                    removeObject(player, id, 1);
                break;

            case 6://Parchemin de caract�ristique
                SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("other.dopeul.getreward.shortly"));
                break;
        }
        SocketManager.GAME_SEND_Ow_PACKET(player);
        ((PlayerData) DatabaseManager.get(PlayerData.class)).update(player);
    }

    public static Integer getDoplonByClasse(int classe) {
        switch (classe) {
            case Constant.CLASS_FECA:
                return 10306;
            case Constant.CLASS_OSAMODAS:
                return 10308;
            case Constant.CLASS_ENUTROF:
                return 10305;
            case Constant.CLASS_SRAM:
                return 10312;
            case Constant.CLASS_XELOR:
                return 10313;
            case Constant.CLASS_ECAFLIP:
                return 10303;
            case Constant.CLASS_ENIRIPSA:
                return 10304;
            case Constant.CLASS_IOP:
                return 10307;
            case Constant.CLASS_CRA:
                return 10302;
            case Constant.CLASS_SADIDA:
                return 10311;
            case Constant.CLASS_SACRIEUR:
                return 10310;
            case Constant.CLASS_PANDAWA:
                return 10309;
        }
        return -1;
    }

    public static int hasOneDoplon(Player perso) {
        if (perso.hasItemTemplate(10306, 1, false))
            return 10306;
        else if (perso.hasItemTemplate(10308, 1, false))
            return 10308;
        else if (perso.hasItemTemplate(10305, 1, false))
            return 10305;
        else if (perso.hasItemTemplate(10312, 1, false))
            return 10312;
        else if (perso.hasItemTemplate(10313, 1, false))
            return 10313;
        else if (perso.hasItemTemplate(10303, 1, false))
            return 10303;
        else if (perso.hasItemTemplate(10304, 1, false))
            return 10304;
        else if (perso.hasItemTemplate(10307, 1, false))
            return 10307;
        else if (perso.hasItemTemplate(10302, 1, false))
            return 10302;
        else if (perso.hasItemTemplate(10311, 1, false))
            return 10311;
        else if (perso.hasItemTemplate(10310, 1, false))
            return 10310;
        else if (perso.hasItemTemplate(10309, 1, false))
            return 10309;
        else
            return -1;
    }

    private static ArrayList<Integer> hasQuaDoplon(Player perso, int qua) {
        ArrayList<Integer> doplons = new ArrayList<>();

        if (perso.hasItemTemplate(10306, qua, false))
            doplons.add(10306);
        if (perso.hasItemTemplate(10308, qua, false))
            doplons.add(10308);
        if (perso.hasItemTemplate(10305, qua, false))
            doplons.add(10305);
        if (perso.hasItemTemplate(10312, qua, false))
            doplons.add(10312);
        if (perso.hasItemTemplate(10313, qua, false))
            doplons.add(10313);
        if (perso.hasItemTemplate(10303, qua, false))
            doplons.add(10303);
        if (perso.hasItemTemplate(10304, qua, false))
            doplons.add(10304);
        if (perso.hasItemTemplate(10307, qua, false))
            doplons.add(10307);
        if (perso.hasItemTemplate(10302, qua, false))
            doplons.add(10302);
        if (perso.hasItemTemplate(10311, qua, false))
            doplons.add(10311);
        if (perso.hasItemTemplate(10310, qua, false))
            doplons.add(10310);
        if (perso.hasItemTemplate(10309, qua, false))
            doplons.add(10309);
        return doplons;
    }

    private static void removeObject(Player perso, int id, int qua) {
        perso.removeByTemplateID(id, qua);
        SocketManager.GAME_SEND_Ow_PACKET(perso);
        SocketManager.GAME_SEND_Im_PACKET(perso, "022;" + qua + "~" + id);
    }

    /**
     * Trousseau de clef *
     */
    public static boolean parseConditionTrousseau(String stats, int npc, int map) {
        Couple<Integer, Integer> couple = donjons.get(map);

        if (couple != null)
            if (couple.first == npc && Integer.toHexString(couple.second).startsWith(stats))
                return true;
        return false;
    }

    private static String generateStats() {
        StringBuilder stats = new StringBuilder();

        for (Couple<Integer, Integer> couple : donjons.values()) {
            if (!stats.toString().isEmpty())
                stats.append(",");
            stats.append(Integer.toHexString(couple.second));
        }
        return stats.toString();
    }

    public static Map<Integer, String> generateStatsTrousseau() {
        Map<Integer, String> txtStat = new HashMap<>();
        txtStat.put(Constant.STATS_NAME_DJ, generateStats());
        txtStat.put(Constant.STATS_DATE, String.valueOf(System.currentTimeMillis()));
        return txtStat;
    }
}