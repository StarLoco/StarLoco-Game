package org.starloco.locos.command;

import org.starloco.locos.auction.AuctionManager;
import org.starloco.locos.client.Player;
import org.starloco.locos.client.other.Party;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.event.EventManager;
import org.starloco.locos.game.GameClient;
import org.starloco.locos.game.action.ExchangeAction;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.kernel.Logging;
import org.starloco.locos.lang.LangEnum;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.util.TimerWaiter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CommandPlayer {

    public final static String canal = "Général";
    static boolean canalMute = false;

    public static boolean analyse(Player player, String msg) {
        msg = msg.replace("|", "");
        if (msg.charAt(0) == '.') {
            if(command(msg, "help")) {
                return commandHelp(player, msg);
            } else if (command(msg, "all") && msg.length() > 5) {
                return commandAll(player, msg);
            } else if (command(msg, "noall")) {
                return commandNoAll(player, msg);
            } else if (command(msg, "staff") || command(msg, "admin")) {
                return commandStaff(player, msg);
            } else if (command(msg, "deblo")) {
                return commandDeblo(player, msg);
            } else if (command(msg, "infos")) {
                return commandInfos(player, msg);
            }else if (command(msg, "master") || command(msg, "maitre") || command(msg, "maître") || command(msg, "maestro")) {
                return commandMaster(player, msg);
            } else if (command(msg, "pass")) {
                return commandPass(player, msg);
            } else  if (command(msg, "interval")) {
                return commandInterval(player, msg);
            } else if(command(msg, "start") || command(msg, "astrub")) {
                return commandAstrub(player, msg);
            } else if(command(msg, "walkfast")) {
                player.walkFast = !player.walkFast;
                return true;
            } else  if(command(msg, "vip")) {
                player.sendMessage(player.getLang().trans("command.commandplayer.vip"));
                return true;
            } else if(command(msg, "savepos")) {
                return commandStart(player, msg);
            } else  if (command(msg, "transfert")) {
                return commandTransfert(player, msg);
            }else if (command(msg, "banque")) {
                if (!player.getAccount().isSubscribeWithoutCondition()) {
                    player.sendMessage(player.getLang().trans("command.commandplayer.life.nosubscribe"));
                    return true;
                }
                if (player.isInPrison() || player.getFight() != null || player.getExchangeAction() != null)
                    return true;
                player.openBank();
                return true;
            }else if (command(msg, "groupe")) {
                if (player.isInPrison() || player.getFight() != null)
                    return true;
                final byte[] count = {0};
                World.world.getOnlinePlayers().stream().filter(p -> !p.equals(player) && p.getParty() == null && p.getAccount().getCurrentIp().equals(player.getAccount().getCurrentIp()) && p.getFight() == null && !p.isInPrison()).forEach(p -> {
                    if(count[0] <= 8) {
                        if (player.getParty() == null) {
                            Party party = new Party(player, p);
                            SocketManager.GAME_SEND_GROUP_CREATE(player.getGameClient(), party);
                            SocketManager.GAME_SEND_PL_PACKET(player.getGameClient(), party);
                            SocketManager.GAME_SEND_GROUP_CREATE(p.getGameClient(), party);
                            SocketManager.GAME_SEND_PL_PACKET(p.getGameClient(), party);
                            player.setParty(party);
                            p.setParty(party);
                            SocketManager.GAME_SEND_ALL_PM_ADD_PACKET(player.getGameClient(), party);
                            SocketManager.GAME_SEND_ALL_PM_ADD_PACKET(p.getGameClient(), party);
                        } else {
                            SocketManager.GAME_SEND_GROUP_CREATE(p.getGameClient(), player.getParty());
                            SocketManager.GAME_SEND_PL_PACKET(p.getGameClient(), player.getParty());
                            SocketManager.GAME_SEND_PM_ADD_PACKET_TO_GROUP(player.getParty(), p);
                            player.getParty().addPlayer(p);
                            p.setParty(player.getParty());
                            SocketManager.GAME_SEND_ALL_PM_ADD_PACKET(p.getGameClient(), player.getParty());
                            SocketManager.GAME_SEND_PR_PACKET(p);
                        }
                    }
                    count[0] = (byte) (count[0] + 1);
                });

                return true;
            } else if(command(msg, "event")) {
                return player.cantTP() || EventManager.getInstance().subscribe(player) == 1;
            } else if(command(msg, "auction")) {
                if(player.cantTP() || player.isDead() != 0 || player.isGhost() || player.isAway() || player.getFight() != null)
                    return true;
                AuctionManager.getInstance().onPlayerCommand(player, msg.split(" "));
                return true;
            } else {
                player.sendMessage(player.getLang().trans("command.commandplayer.default"));
                return true;
            }
        }
        return false;
    }

    private static boolean commandPass(Player player, String msg) {
        if(player.getParty() != null && player.getParty().getMaster() != null) {
            Party.MasterOption option = player.getParty().getOptionByPlayer(player);
            if(option != null) {
                option.togglePass();
                player.sendMessage(player.getLang().trans("command.commandplayer.pass"));
                return true;
            }
        } else {
            player.sendMessage(player.getLang().trans("command.commandplayer.pass.error"));
        }
        return false;
    }

    private static boolean commandInterval(Player player, String msg) {
        if(player.getParty() != null && player.getParty().getMaster() != null) {
            Party.MasterOption option = player.getParty().getOptionByPlayer(player);
            if(option != null) {
                try {
                    byte second = Byte.parseByte(msg.split(" ")[1]);
                    for(Party.MasterOption opt : player.getParty().getOptions()) {
                        if (opt != null && opt.getSecond() == second) {
                            player.sendMessage(player.getLang().trans("command.commandplayer.interval.error"));
                            return true;
                        }
                    }

                    option.setSecond(second);
                } catch(Exception ignored) {
                    player.sendMessage(player.getLang().trans("command.commandplayer.interval.error"));
                }

                if(option.getSecond() < 1) option.setSecond((byte)1);
                else if(option.getSecond() > 29) option.setSecond((byte)29);

                player.sendMessage(player.getLang().trans("command.commandplayer.interval"));
                return true;
            }
        } else {
            player.sendMessage(player.getLang().trans("command.commandplayer.interval.master"));
        }
        return false;
    }

    private static List<Integer> bannedItemJob = Arrays.asList(491, 493, 494, 495, 496);

    private static boolean commandTransfert(Player player, String msg) {
        if (player.isInPrison() || player.getFight() != null)
            return true;

        if(commandTransfertWithMaster(player, msg)) {
            return true;
        }

        if(player.getExchangeAction() == null || player.getExchangeAction().getType() != ExchangeAction.IN_BANK) {
            player.sendMessage(player.getLang().trans("command.commandplayer.transfer.noinbank"));
            return true;
        }
        String[] info = msg.split(" ");
        int map = player.getCurMap().getId();
        SocketManager.GAME_SEND_EV_PACKET(player.getGameClient());
        player.sendTypeMessage("Bank", player.getLang().trans("command.commandplayer.transfer.waitting"));
        int count = 0;

        boolean bank = info.length >= 2 && info[1].equalsIgnoreCase("bank");

        for (GameObject object : new ArrayList<>(bank ? player.getAccount().getBank() : player.getItems().values())) {
            if(info.length == 2) {
                if (object == null || object.getTemplate() == null || !object.getTemplate().getStrTemplate().isEmpty())
                    continue;
                if (object.getTemplate().isAnEquipment(true, null))
                    continue;
                switch (object.getTemplate().getType()) {
                    case Constant.ITEM_TYPE_OBJET_VIVANT:
                    case Constant.ITEM_TYPE_PRISME:
                    case Constant.ITEM_TYPE_FILET_CAPTURE:
                    case Constant.ITEM_TYPE_CERTIF_MONTURE:
                    case Constant.ITEM_TYPE_OBJET_UTILISABLE:
                    case Constant.ITEM_TYPE_OBJET_ELEVAGE:
                    case Constant.ITEM_TYPE_CADEAUX:
                    case Constant.ITEM_TYPE_PARCHO_RECHERCHE:
                    case Constant.ITEM_TYPE_PIERRE_AME:
                    case Constant.ITEM_TYPE_BOUCLIER:
                    case Constant.ITEM_TYPE_SAC_DOS:
                    case Constant.ITEM_TYPE_OBJET_MISSION:
                    case Constant.ITEM_TYPE_BOISSON:
                    case Constant.ITEM_TYPE_CERTIFICAT_CHANIL:
                    case Constant.ITEM_TYPE_FEE_ARTIFICE:
                    case Constant.ITEM_TYPE_MAITRISE:
                    case Constant.ITEM_TYPE_POTION_SORT:
                    case Constant.ITEM_TYPE_POTION_METIER:
                    case Constant.ITEM_TYPE_POTION_OUBLIE:
                    case Constant.ITEM_TYPE_BONBON:
                    case Constant.ITEM_TYPE_PERSO_SUIVEUR:
                    case Constant.ITEM_TYPE_RP_BUFF:
                    case Constant.ITEM_TYPE_MALEDICTION:
                    case Constant.ITEM_TYPE_BENEDICTION:
                    case Constant.ITEM_TYPE_TRANSFORM:
                    case Constant.ITEM_TYPE_DOCUMENT:
                    case Constant.ITEM_TYPE_QUETES:
                        continue;
                }
            }
            if(object.getPosition() != -1)
                continue;
            switch (object.getTemplate().getType()) {
                case Constant.ITEM_TYPE_BONBON:
                case Constant.ITEM_TYPE_PERSO_SUIVEUR:
                case Constant.ITEM_TYPE_RP_BUFF:
                case Constant.ITEM_TYPE_MALEDICTION:
                case Constant.ITEM_TYPE_BENEDICTION:
                case Constant.ITEM_TYPE_TRANSFORM:
                case Constant.ITEM_TYPE_DOCUMENT:
                case Constant.ITEM_TYPE_QUETES:
                    continue;
            }
            if (bannedItemJob.contains(object.getTemplate().getId()))
                continue;
            count++;
            if(!bank) {
                player.addInBank(object.getGuid(), object.getQuantity(), false);
            } else {
                player.removeFromBank(object.getGuid(), object.getQuantity());
            }
        }

        player.sendTypeMessage("Bank", player.getLang().trans("command.commandplayer.transfer.good", count));
        player.setExchangeAction(null);
        if(player.getCurMap().getId() == map)
            player.openBank();
        return true;
    }

    private static boolean commandTransfertWithMaster(Player player, String msg) {
        String[] info = msg.split(" ");
        if(info.length == 1 && player.getParty() != null && player.getParty().getMaster() != null && player.getParty().getMaster().getId() == player.getId()) {
            final List<GameObject> objects = new ArrayList<>();
            player.getParty().getPlayers().stream()
                    .filter(follower -> follower.getFight() == null && follower.getGameClient() != null && player.getParty().isWithTheMaster(follower, false, false))
                    .forEach(follower -> {
                        follower.getGameClient().clearAllPanels(null);
                        for(GameObject object : new ArrayList<>(follower.getItems().values())) {
                            if(object != null) {
                                if (object.getPosition() != -1 || object.getTemplate().isAnEquipment(true, null))
                                    continue;
                                switch (object.getTemplate().getType()) {
                                    case Constant.ITEM_TYPE_OBJET_VIVANT: case Constant.ITEM_TYPE_PRISME:
                                    case Constant.ITEM_TYPE_FILET_CAPTURE: case Constant.ITEM_TYPE_CERTIF_MONTURE:
                                    case Constant.ITEM_TYPE_OBJET_UTILISABLE: case Constant.ITEM_TYPE_OBJET_ELEVAGE:
                                    case Constant.ITEM_TYPE_CADEAUX: case Constant.ITEM_TYPE_PARCHO_RECHERCHE: case Constant.ITEM_TYPE_PIERRE_AME:
                                    case Constant.ITEM_TYPE_BOUCLIER: case Constant.ITEM_TYPE_SAC_DOS: case Constant.ITEM_TYPE_OBJET_MISSION:
                                    case Constant.ITEM_TYPE_BOISSON: case Constant.ITEM_TYPE_CERTIFICAT_CHANIL: case Constant.ITEM_TYPE_FEE_ARTIFICE:
                                    case Constant.ITEM_TYPE_MAITRISE: case Constant.ITEM_TYPE_POTION_SORT:
                                    case Constant.ITEM_TYPE_POTION_METIER: case Constant.ITEM_TYPE_POTION_OUBLIE:
                                    case Constant.ITEM_TYPE_BONBON: case Constant.ITEM_TYPE_PERSO_SUIVEUR:
                                    case Constant.ITEM_TYPE_RP_BUFF: case Constant.ITEM_TYPE_MALEDICTION:
                                    case Constant.ITEM_TYPE_BENEDICTION: case Constant.ITEM_TYPE_TRANSFORM:
                                    case Constant.ITEM_TYPE_DOCUMENT: case Constant.ITEM_TYPE_QUETES:
                                    case Constant.ITEM_TYPE_OUTIL:
                                        continue;
                                }
                                follower.removeItem(object.getGuid(), object.getQuantity(), true, false);
                                objects.add(object);
                            }
                        }
                    });
            TimerWaiter.addNext(() -> {
                for(GameObject object : objects) {
                    if(!player.addObjet(object, true))
                        World.world.removeGameObject(object.getGuid());
                }
                player.sendTypeMessage("Transfert", objects.size()  + " objets récupérés.");
            }, 1000);
            return true;
        }
        return false;
    }

    private static boolean commandStart(Player player, String msg) {
        int mapId = player.getCurMap().getId();
        if (player.isInPrison() || player.cantTP() || player.getFight() != null)
            return true;
        player.warpToSavePos();

        final Party party = player.getParty();
        if(party != null && party.getMaster() != null && party.getMaster().getName().equals(player.getName())) {
            for (Player slave : player.getParty().getPlayers()) {
                if (slave.getCurMap().getId() == mapId) {
                    if (!player.isInPrison())
                        if (!player.cantTP())
                            if (player.getFight() == null)
                                slave.warpToSavePos();
                }
            }
        }
        return true;
    }

    private static boolean commandAstrub(Player player, String msg) {
        int mapId = player.getCurMap().getId();
        if (player.isInPrison() || player.cantTP() || player.getFight() != null || Config.gameServerId == 22)
            return true;
        player.teleport((short) 10111, 151);

        final Party party = player.getParty();
        if(party != null && party.getMaster() != null && party.getMaster().getName().equals(player.getName())) {
            player.getParty().getPlayers().stream().filter(p -> party.isWithTheMaster(p, false, true)).forEach(slave -> {
                if (slave.getCurMap().getId() == mapId) {
                    if (!player.isInPrison() && !player.cantTP())
                        if (player.getFight() == null)
                            slave.teleport((short) 10111, 151);
                }
            });
        }
        return true;
    }

    private static boolean commandMaster(Player player, String msg) {
        String[] split = msg.split(" ");

        if(split.length == 2) {
            String name = split[1];
            final Player target = World.world.getPlayerByName(name);

            if(target != null && target != player) {
                if(target.getParty() == player.getParty()) {
                    final Party party = target.getParty();
                    party.setChief(target);
                    party.setMaster(target);

                    for(Player member : party.getPlayers())
                        member.send("PL" + target.getId());
                } else {
                    player.sendMessage(player.getLang().trans("command.commandplayer.master.nogroup.name", name));
                }
            }
            return true;
        } else {
            if (player.cantTP()) return true;

            final Party party = player.getParty();

            if (party == null) {
                player.sendMessage(player.getLang().trans("command.commandplayer.master.nogroup"));
                return true;
            }

            final List<Player> players = player.getParty().getPlayers();

            if (!party.getChief().getName().equals(player.getName())) {
                player.sendMessage(player.getLang().trans("command.commandplayer.master.noking"));
                return true;
            }

            if (msg.length() <= 8 && party.getMaster() != null) {
                player.sendMessage(player.getLang().trans("command.commandplayer.master.disabled"));
                players.stream().filter(follower -> follower != party.getMaster())
                        .forEach(follower -> SocketManager.GAME_SEND_MESSAGE(follower, follower.getLang().trans("command.commandplayer.master.nofollow", party.getMaster().getName())));
                party.setMaster(null);
                return true;
            }

            Player target = player;

            if (msg.length() > 8) {
                String name = msg.substring(8, msg.length() - 1);
                target = World.world.getPlayerByName(name);
            }

            if (target == null) {
                player.sendMessage(player.getLang().trans("command.commandplayer.master.noavailable"));
                return true;
            }
            if (target.getParty() == null || !target.getParty().getPlayers().contains(player)) {
                player.sendMessage(player.getLang().trans("command.commandplayer.master.nogroup.atuser"));
                return true;
            }

            party.setMaster(target);

            final String message = player.getLang().trans("command.commandplayer.master.follow", target.getName());
            for (Player follower : players)
                if (follower != target)
                    SocketManager.GAME_SEND_MESSAGE(follower, message);

            party.moveAllPlayersToMaster(null, false);
            SocketManager.GAME_SEND_MESSAGE(target, target.getLang().trans("command.commandplayer.master.master"));
            return true;
        }
    }

    private static boolean commandHelp(Player player, String msg) {
        player.sendMessage(player.getLang().trans("command.commandplayer.default"));
        ExchangeAction<Integer> exchangeAction = new ExchangeAction<>(ExchangeAction.TALKING_WITH, player.getId());
        player.setExchangeAction(exchangeAction);
        return true;
    }

    //region Commands

    private static boolean commandAll(Player player, String msg) {
        if (player.isInPrison())
            return true;
        if(canalMute && player.getGroupe() == null) {
            player.sendMessage(player.getLang().trans("command.commandplayer.commandall.unvailable"));
            return true;
        }
        if (player.noall) {
            player.sendMessage(player.getLang().trans("command.commandplayer.noall"));
            return true;
        }
        if (player.getGroupe() == null && System.currentTimeMillis() - player.getGameClient().timeLastTaverne < 10000) {
            player.sendMessage(player.getLang().trans("command.commandplayer.allwait").replace("#1", String.valueOf(10 - ((System.currentTimeMillis() - player.getGameClient().timeLastTaverne) / 1000))));
            return true;
        }

        player.getGameClient().timeLastTaverne = System.currentTimeMillis();

        String prefix = "<font color='#C35617'>[" + (new SimpleDateFormat("HH:mm").format(new Date(System.currentTimeMillis()))) + "] (" + canal + ") (" + (Config.gameServerKey.isEmpty() ? getNameServerById(Config.gameServerId) : Config.gameServerKey) + ") <b><a href='asfunction:onHref,ShowPlayerPopupMenu," + player.getName() + "'>" + player.getName() + "</a></b>";

        Logging.getInstance().write("AllMessage", "[" + (new SimpleDateFormat("HH:mm").format(new Date(System.currentTimeMillis()))) + "] : " + player.getName() + " : " + msg.substring(5, msg.length() - 1));

        final String message = "Im116;" + prefix + "~" + msg.substring(5, msg.length()).replace(";", ":").replace("~", "").replace("|", "").replace("<", "").replace(">", "") + "</font>";

        World.world.getOnlinePlayers().stream().filter(p -> !p.noall).forEach(p -> p.send(message));
        Config.exchangeClient.send("DM" + player.getName() + ";" + getNameServerById(Config.gameServerId) + ";" + msg.substring(5, msg.length()).replace("\n", "").replace("\r", "").replace(";", ":").replace("~", "").replace("|", "").replace("<", "").replace(">", ""));
        return true;
    }

    private static boolean commandNoAll(Player player, String msg) {
        if (player.noall) {
            player.noall = false;
            player.sendMessage(player.getLang().trans("command.commandplayer.all.on"));
        } else {
            player.noall = true;
            player.sendMessage(player.getLang().trans("command.commandplayer.all.off"));
        }
        return true;
    }

    private static boolean commandDeblo(Player player, String msg) {
        if (player.cantTP())
            return true;
        if (player.getFight() != null)
            return true;
        if (player.getCurCell().isWalkable(true)) {
            player.sendMessage(player.getLang().trans("command.commandplayer.deblo.no"));
            return true;
        }
        player.teleport(player.getCurMap().getId(), player.getCurMap().getRandomFreeCellId());
        return true;
    }

    private static boolean commandInfos(Player player, String msg) {
        long uptime = System.currentTimeMillis()
                - Config.startTime;
        int jour = (int) (uptime / (1000 * 3600 * 24));
        uptime %= (1000 * 3600 * 24);
        int hour = (int) (uptime / (1000 * 3600));
        uptime %= (1000 * 3600);
        int min = (int) (uptime / (1000 * 60));
        uptime %= (1000 * 60);
        int sec = (int) (uptime / (1000));
        int nbPlayer = (int) Config.gameServer.getClients().stream().filter(gc -> gc != null && gc.getPlayer() != null).count();

        String mess = player.getLang().trans("command.commandplayer.info.uptime", String.valueOf(jour), String.valueOf(hour), String.valueOf(min), String.valueOf(sec));
        if (nbPlayer > 0)
            mess += player.getLang().trans("command.commandplayer.info.online", String.valueOf(nbPlayer));
        player.sendMessage(mess);
        return true;
    }

    private static boolean commandStaff(Player player, String msg) {
        String message = player.getLang().trans("command.commandplayer.staff");
        boolean vide = true;
        for (Player target : World.world.getOnlinePlayers()) {
            if (target == null)
                continue;
            if (target.getGroupe() == null || target.isInvisible())
                continue;

            message += "\n- <b><a href='asfunction:onHref,ShowPlayerPopupMenu," + target.getName() + "'>[" + target.getGroupe().getName() + "] " + target.getName() + "</a></b>";
            vide = false;
        }
        if (vide)
            message = player.getLang().trans("command.commandplayer.staff.none");
        player.sendMessage(message);
        return true;
    }

    private static boolean command(String msg, String command) {
        return msg.length() > command.length() && msg.substring(1, command.length() + 1).equalsIgnoreCase(command);
    }
    //endregion

    private static String getNameServerById(int id) {
        switch (id) {
            case 13:
                return "Silouate";
            case 19:
                return "Allister";
            case 22:
                return "Oto Mustam";
            case 1:
                return "Jiva";
            case 2:
                return "Zeus";
            case 37:
                return "Nostalgy";

            case 4001:
                return "Alma";
            case 4002:
                return "Aguabrial";
            case 4005:
                return "Bolgrot";
        }
        return "Unknown";
    }
}