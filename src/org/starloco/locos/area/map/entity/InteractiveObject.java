package org.starloco.locos.area.map.entity;

import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.area.map.labyrinth.Minotoror;
import org.starloco.locos.client.Player;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.entity.monster.Monster;
import org.starloco.locos.game.scheduler.Updatable;
import org.starloco.locos.game.world.World;
import org.starloco.locos.job.JobConstant;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.other.Dopeul;
import org.starloco.locos.quest.Quest;
import org.starloco.locos.quest.QuestPlayer;
import org.starloco.locos.quest.QuestObjective;

import java.util.ArrayList;

public class InteractiveObject {

    public final static Updatable updatable = new Updatable(0) {
        private final ArrayList<InteractiveObject> queue = new ArrayList<>();

        @Override
        public void update() {
            if(this.queue.isEmpty()) return;
            long time = System.currentTimeMillis();
            new ArrayList<>(this.queue).stream().filter(interactiveObject -> interactiveObject.getTemplate() != null && time - interactiveObject.lastTime >
                    interactiveObject.getTemplate().getRespawnTime()).forEach(interactiveObject -> {
                interactiveObject.enable();
                this.queue.remove(interactiveObject);
            });
        }

        @Override
        public ArrayList<InteractiveObject> get() {
            return queue;
        }
    };

    private int id, state;
    private GameMap map;
    private GameCase cell;
    private boolean interactive = true, walkable;
    private long lastTime = 0;
    private InteractiveObjectTemplate template;

    public InteractiveObject(int id, final GameMap iMap, GameCase iCell) {
        this.id = id;
        this.map = iMap;
        this.cell = iCell;
        this.state = JobConstant.IOBJECT_STATE_FULL;
        this.template = World.world.getIOTemplate(this.id);
        this.walkable = this.getTemplate() != null && this.getTemplate().isWalkable() && this.state == JobConstant.IOBJECT_STATE_FULL;
    }

    public static void getActionIO(final Player player, GameCase cell, int id) {
        switch(id) {
            case 7041:
            case 7042:
            case 7043:
            case 7044:
            case 7045:
            case 1748:
                if(InteractiveDoor.tryActivate(player, cell))
                    return;
                break;
        }
        switch (id) {
            case 1524:
            case 542://Statue Phoenix.
                if (player.isGhost()) {
                    player.setAlive();
                    Quest q = Quest.getQuestById(190);
                    if (q != null) {
                        QuestPlayer qp = player.getQuestPersoByQuest(q);
                        if (qp != null) {
                            QuestObjective qe = q.getCurrentQuestStep(qp);
                            if (qe != null)
                                if(qe.getId() == 783)
                                    q.updateQuestData(player, true, qe.getValidationType());
                        }
                    }
                }
                break;

            case 684://Portillon donjon squelette.
                if (player.hasItemTemplate(10207, 1, false)) {
                    String stats, statsReplace = "";
                    GameObject object = player.getItemTemplate(10207);
                    stats = object.getTxtStat().get(Constant.STATS_NAME_DJ);
                    try {
                        for (String i : stats.split(",")) {
                            if (Dopeul.parseConditionTrousseau(i.replace(" ", ""), -1, player.getCurMap().getId())) {
                                player.teleport((short) 2110, 118);
                                statsReplace = i;
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
                        break;
                    }
                }

                if (!player.hasItemTemplate(1570, 1, false)) {
                    SocketManager.GAME_SEND_MESSAGE(player, "Vous ne possedez pas la clef nécessaire.", "009900");
                } else {
                    player.removeByTemplateID(1570, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 1570);
                    player.teleport((short) 2110, 118);
                }
                break;

            case 1330://Pierre de kwak
                player.getCurMap().startFightVersusProtectors(player, new Monster.MobGroup(player.getCurMap().nextObjectId, player.getCurMap(), cell.getId(), getKwakere(player.getCurMap().getId())
                        + "," + 40 + "," + 40));
                break;

            case 1679:
                player.warpToSavePos();
                break;

            case 3000://Ep�e Crocoburio
                if (player.hasEquiped(1718)
                        && player.hasEquiped(1719)
                        && player.hasEquiped(1720)
                        && player.getStats().getEffect(Constant.STATS_ADD_VITA) == 120
                        && player.getStats().getEffect(Constant.STATS_ADD_SAGE) == 0
                        && player.getStats().getEffect(Constant.STATS_ADD_FORC) == 60
                        && player.getStats().getEffect(Constant.STATS_ADD_INTE) == 50
                        && player.getStats().getEffect(Constant.STATS_ADD_AGIL) == 0
                        && player.getStats().getEffect(Constant.STATS_ADD_CHAN) == 0) {
                    SocketManager.GAME_SEND_ACTION_TO_DOOR(player.getCurMap(), 237, true);
                    SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("area.map.entity.interactiveobject.crocoburio.unavailable"));
                    /*perso.getWaiter().addNext(new Runnable()
					{
						public void run()
						{
							perso.setFullMorph(10, false, false);
						}
					}, 3000);*/
                } else {
                    SocketManager.GAME_SEND_Im_PACKET(player, "119");
                }

                break;

            case 7546://Foire au troll
            case 7547:
                SocketManager.send(player, "GDF|" + cell.getId() + ";3");
                break;

            case 1324:// Plot Rouge des �motes
                switch (player.getCurMap().getId()) {
                    case 2196:
                        if (player.isAway())
                            return;
                        if (player.getGuild() == null && player.getGuildMember() == null && player.hasItemTemplate(1575, 1, false)) {
                            SocketManager.GAME_SEND_gn_PACKET(player);
                        } else {
                            if (player.getGuild() != null && player.getGuildMember() != null) SocketManager.GAME_SEND_gC_PACKET(player, "Ea");
                            else SocketManager.GAME_SEND_Im_PACKET(player, "14");
                        }
                        break;
                    case 2037://Emote Faire signe
                        player.addStaticEmote(2);
                        break;
                    case 2025://Emote Applaudir
                        player.addStaticEmote(3);
                        break;
                    case 2039://Emote Se mettre en Col�re
                        player.addStaticEmote(4);
                        break;
                    case 2047://Emote Peur
                        player.addStaticEmote(5);
                        break;
                    case 8254://Emote Montrer son Arme
                        player.addStaticEmote(6);
                        break;
                    case 2099://Emote Saluer
                        player.addStaticEmote(9);
                        break;
                    case 8539://Emote Croiser les bras
                        player.addStaticEmote(14);
                        break;
                }
                break;
            case 1694://Village brigandin tire �olienne
                SocketManager.GAME_SEND_GA_PACKET(player.getGameClient(), "", "2", player.getId()
                        + "", "4");
                player.teleport((short) 6848, 390);
                break;
            case 1695://Village brigandin tire �olienne
                SocketManager.GAME_SEND_GA_PACKET(player.getGameClient(), "", "2", player.getId()
                        + "", "3");
                player.teleport((short) 6844, 268);
                break;
            case 7041: // Bas
                SocketManager.GAME_SEND_ACTION_TO_DOOR(player.getCurMap(), cell.getId(), true);
                Minotoror.ouvrirBas(player.getCurMap());
                break;
            case 7042: // Haut
                SocketManager.GAME_SEND_ACTION_TO_DOOR(player.getCurMap(), cell.getId(), true);
                Minotoror.ouvrirHaut(player.getCurMap());
                break;
            case 7043: // Gauche
                SocketManager.GAME_SEND_ACTION_TO_DOOR(player.getCurMap(), cell.getId(), true);
                Minotoror.ouvrirGauche(player.getCurMap());
                break;
            case 7044: // Droite
                SocketManager.GAME_SEND_ACTION_TO_DOOR(player.getCurMap(), cell.getId(), true);
                Minotoror.ouvrirDroite(player.getCurMap());
                break;
            default:
                break;
        }
    }

    public static void getSignIO(Player perso, int cell, int id) {
        switch (perso.getCurMap().getId()) {
            case 7460:
                for (String[] hunt : Constant.HUNTING_QUESTS) {
                    if (Integer.parseInt(hunt[1]) == cell && Integer.parseInt(hunt[0]) == id) {
                        SocketManager.send(perso, "dCK" + hunt[2]);
                        break;
                    }
                }
                break;

            case 7411:
                if (id == 1531 && cell == 230)
                    SocketManager.send(perso, "dCK139_0612131303");
                break;

            case 7543:
                if (id == 1528 && cell == 262)
                    SocketManager.send(perso, "dCK75_0603101710");
                if (id == 1533 && cell == 169)
                    SocketManager.send(perso, "dCK74_0603101709");
                if (id == 1528 && cell == 169)
                    SocketManager.send(perso, "dCK73_0706211414");
                break;

            case 7314:
                if (id == 1531 && cell == 93)
                    SocketManager.send(perso, "dCK78_0706221019");
                if (id == 1532 && cell == 256)
                    SocketManager.send(perso, "dCK76_0603091219");
                if (id == 1533 && cell == 415)
                    SocketManager.send(perso, "dCK77_0603091218");
                break;

            case 7417:
                if (id == 1532 && cell == 264)
                    SocketManager.send(perso, "dCK79_0603101711");
                if (id == 1528 && cell == 211)
                    SocketManager.send(perso, "dCK80_0510251009");
                if (id == 1532 && cell == 212)
                    SocketManager.send(perso, "dCK77_0603091218");
                if (id == 1529 && cell == 212)
                    SocketManager.send(perso, "dCK81_0510251010");
                break;

            case 2698:
                if (id == 1531 && cell == 93)
                    SocketManager.send(perso, "dCK51_0706211150");
                if (id == 1528 && cell == 109)
                    SocketManager.send(perso, "dCK41_0706221516");
                break;

            case 2814:
                if (id == 1533 && cell == 415)
                    SocketManager.send(perso, "dCK43_0706201719");
                if (id == 1532 && cell == 326)
                    SocketManager.send(perso, "dCK50_0706211149");
                if (id == 1529 && cell == 325)
                    SocketManager.send(perso, "dCK41_0706221516");
                break;

            case 3087:
                if (id == 1529 && cell == 89)
                    SocketManager.send(perso, "dCK41_0706221516");
                break;

            case 3018:
                if (id == 1530 && cell == 354)
                    SocketManager.send(perso, "dCK52_0706211152");
                if (id == 1532 && cell == 256)
                    SocketManager.send(perso, "dCK50_0706211149");
                if (id == 1528 && cell == 255)
                    SocketManager.send(perso, "dCK41_0706221516");
                break;

            case 3433:
                if (id == 1533 && cell == 282)
                    SocketManager.send(perso, "dCK53_0706211407");
                if (id == 1531 && cell == 179)
                    SocketManager.send(perso, "dCK50_0706211149");
                if (id == 1529 && cell == 178)
                    SocketManager.send(perso, "dCK41_0706221516");
                break;

            case 4493:
                if (id == 1533 && cell == 415)
                    SocketManager.send(perso, "dCK43_0706201719");
                if (id == 1532 && cell == 326)
                    SocketManager.send(perso, "dCK50_0706211149");
                if (id == 1529 && cell == 325)
                    SocketManager.send(perso, "dCK41_0706221516");
                break;

            case 4876:
                if (id == 1532 && cell == 316)
                    SocketManager.send(perso, "dCK54_0706211408");
                if (id == 1531 && cell == 283)
                    SocketManager.send(perso, "dCK51_0706211150");
                if (id == 1530 && cell == 282)
                    SocketManager.send(perso, "dCK52_0706211152");
                break;
        }
    }

    private static int getKwakere(int i) {
        switch (i) {
            case 2072:
                return 270;
            case 2071:
                return 269;
            case 2067:
                return 272;
            case 2068:
                return 271;
        }
        return 269;
    }

    public int getId() {
        return this.id;
    }

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isInteractive() {
        return this.interactive;
    }

    public void setInteractive(boolean interactive) {
        this.interactive = interactive;
    }

    public int getUseDuration() {
        int duration = 1500;
        if (this.getTemplate() != null)
            duration = this.getTemplate().getDuration();
        return duration;
    }

    public int getUnknowValue() {
        int unk = 4;
        if (this.getTemplate() != null)
            unk = this.getTemplate().getUnk();
        return unk;
    }

    public boolean isWalkable() {
        return this.walkable;
    }

    public InteractiveObjectTemplate getTemplate() {
        return template;
    }

    public void setTemplate(InteractiveObjectTemplate template) {
        this.template = template;
    }

    private void enable() {
        this.state = JobConstant.IOBJECT_STATE_FULLING;
        this.interactive = true;
        SocketManager.GAME_SEND_GDF_PACKET_TO_MAP(this.map, this.cell);
        this.state = JobConstant.IOBJECT_STATE_FULL;
    }

    public void disable() {
        this.lastTime = System.currentTimeMillis();
        ArrayList<InteractiveObject> array = (ArrayList<InteractiveObject>) InteractiveObject.updatable.get();
        array.add(this);
    }

    public static class InteractiveObjectTemplate {

        private int id;
        private int respawnTime;
        private int duration;
        private int unk;
        private boolean walkable;

        public InteractiveObjectTemplate(int id, int respawnTime, int duration, int unk, boolean walkable) {
            this.id = id;
            this.respawnTime = respawnTime;
            this.duration = duration;
            this.unk = unk;
            this.walkable = walkable;
        }

        public int getId() {
            return id;
        }

        public boolean isWalkable() {
            return walkable;
        }

        int getRespawnTime() {
            return respawnTime;
        }

        int getDuration() {
            return duration;
        }

        int getUnk() {
            return unk;
        }
    }
}