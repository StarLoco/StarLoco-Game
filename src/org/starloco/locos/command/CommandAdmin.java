package org.starloco.locos.command;

import org.starloco.locos.util.Pair;
import org.starloco.locos.area.SubArea;
import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.area.map.entity.MountPark;
import org.starloco.locos.client.Account;
import org.starloco.locos.client.Player;
import org.starloco.locos.command.administration.AdminUser;
import org.starloco.locos.command.administration.Command;
import org.starloco.locos.command.administration.Group;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.game.*;
import org.starloco.locos.database.data.login.*;
import org.starloco.locos.entity.Collector;
import org.starloco.locos.entity.monster.Monster;
import org.starloco.locos.entity.monster.Monster.MobGrade;
import org.starloco.locos.entity.monster.Monster.MobGroup;
import org.starloco.locos.entity.mount.Mount;
import org.starloco.locos.entity.npc.Npc;
import org.starloco.locos.entity.npc.NpcTemplate;
import org.starloco.locos.entity.pet.PetEntry;
import org.starloco.locos.event.EventManager;
import org.starloco.locos.event.type.Event;
import org.starloco.locos.event.type.EventFindMe;
import org.starloco.locos.fight.Challenge;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.game.GameClient;
import org.starloco.locos.game.GameServer;
import org.starloco.locos.game.action.ExchangeAction;
import org.starloco.locos.game.scheduler.entity.WorldSave;
import org.starloco.locos.game.world.World;
import org.starloco.locos.job.JobAction;
import org.starloco.locos.job.JobStat;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.kernel.Main;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.object.ObjectSet;
import org.starloco.locos.object.ObjectTemplate;
import org.starloco.locos.quest.Quest;
import org.starloco.locos.quest.QuestPlayer;
import org.starloco.locos.quest.QuestObjective;
import org.starloco.locos.util.TimerWaiter;

import java.util.*;
import java.util.Map.Entry;

public class CommandAdmin extends AdminUser {

    public CommandAdmin(Player player) {
        super(player);
    }

    public void apply(String packet) {
        String msg = packet.substring(2);
        String[] infos = msg.split(" ");

        if (infos.length == 0) return;
        String command = infos[0];

        try {
            Group groupe = this.getPlayer().getGroupe();
            if (groupe == null) {
                this.getClient().kick();
                return;
            }
            if (!groupe.haveCommand(command)) {
                this.sendMessage("Commande invalide !");
                return;
            }

            this.command(command, infos, msg);
        } catch (Exception ignored) {}
    }

    public void command(String command, String[] infos, String msg) {
        if (command.equalsIgnoreCase("LOG")) {
            Config.debug = !Config.debug;
            this.sendMessage("Les logs console sont : " + (Config.debug ? "active" : "disable"));
            return;
        } else if (command.equalsIgnoreCase("CHALL")) {
            Challenge challenge = new Challenge(this.getPlayer().getFight(), Integer.parseInt(infos[1]), 0, 0);
            this.getPlayer().getFight().getAllChallenges().put(Integer.parseInt(infos[1]), challenge);
            challenge.fightStart();
            SocketManager.GAME_SEND_CHALLENGE_FIGHT(this.getPlayer().getFight(), 1, challenge.parseToPacket());
            return;
        } else if (command.equalsIgnoreCase("HELP")) {
            String cmd = infos.length == 2 ? infos[1] : "";

            if (cmd.equalsIgnoreCase("")) {
                this.sendMessage("\nVous avez actuellement le groupe GM " + this.getPlayer().getGroupe().getName() + ".\nCommandes disponibles :\n");
                for (Command commande : this.getPlayer().getGroupe().getCommands()) {
                    String args = (commande.getArguments()[1] != null && !commande.getArguments()[1].equalsIgnoreCase("")) ? (" + " + commande.getArguments()[1]) : ("");
                    String desc = (commande.getArguments()[2] != null && !commande.getArguments()[2].equalsIgnoreCase("")) ? (commande.getArguments()[2]) : ("");
                    this.sendMessage("<u>" + commande.getArguments()[0] + args + "</u> - " + desc);
                }
            } else {
                this.sendMessage("\nVous avez actuellement le groupe GM " + this.getPlayer().getGroupe().getName() + ".\nCommandes recherches :\n");
                for (Command commande : this.getPlayer().getGroupe().getCommands()) {
                    if (commande.getArguments()[0].contains(cmd.toUpperCase())) {
                        String args = (commande.getArguments()[1] != null && !commande.getArguments()[1].equalsIgnoreCase("")) ? (" + " + commande.getArguments()[1]) : ("");
                        String desc = (commande.getArguments()[2] != null && !commande.getArguments()[2].equalsIgnoreCase("")) ? (commande.getArguments()[2]) : ("");
                        this.sendMessage("<u>" + commande.getArguments()[0] + args + "</u> - " + desc);
                    }
                }
            }
            return;
        }
        else if (command.equalsIgnoreCase("STARTBOUFBOWL"))
		{
			if(this.getPlayer().getCurMap().getId() != 9862)
				return;
			
			Player init1 = null;
			Player init2 = null;
			try
			{
				init1 = World.world.getPlayerByName(infos[1]);
				init2 = World.world.getPlayerByName(infos[2]);
			}
			catch (Exception e)
			{
				// ok
			}
			if (init1 == null || init2 == null)
			{
				sendMessage("Le nom du personnage n'est pas bon.");
				return;
			}
			if(init1.getCurMap().getId() != 9862 || init2.getCurMap().getId() != 9862)
			{
				sendMessage("un perso n'est pas sur la map. (9862)");
				return;
			}
			SocketManager.GAME_SEND_MAP_START_DUEL_TO_MAP(this.getPlayer().getCurMap(), init2.getId(), init1.getId());
			Fight fight = null;
			fight = this.getPlayer().getCurMap().newFightbouf(init1, init2, Constant.FIGHT_TYPE_CHALLENGE);
			init1.setFight(fight);
			init2.setFight(fight);	
			return;
		} else if (command.equalsIgnoreCase("ONLINE")) {
            Player perso = this.getPlayer();
            if (infos.length > 1) {//Si un nom de perso est specifie
                try {
                    perso = World.world.getPlayerByName(infos[1]);
                } catch (Exception e) {
                    // ok
                }
                if (perso == null) {
                    this.sendMessage("Le personnage n'a pas ete trouve");
                    return;
                }
            }
            if (perso.getGameClient() != null)
                perso.getGameClient().kick();
            perso.setOnline(false);
            perso.resetVars();
            ((PlayerData) DatabaseManager.get(PlayerData.class)).update(perso);
            SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(perso.getCurMap(), perso.getId());
            World.world.unloadPerso(perso);
            ((PlayerData) DatabaseManager.get(PlayerData.class)).load(perso.getId());
            World.world.ReassignAccountToChar(perso.getAccount());
            String str = "Le joueur " + perso.getName() + " a ete reinitialise de ces variables.";
            this.sendMessage(str);
            return;
        } else if (command.equalsIgnoreCase("ANAME")) {
            infos = msg.split(" ", 2);
            String prefix = "<b><a href='asfunction:onHref,ShowPlayerPopupMenu," + this.getPlayer().getName() + "'>[" + this.getPlayer().getGroupe().getName() + "] " + this.getPlayer().getName() + "</a></b>";
            if(infos.length > 1) {
                String suffix = infos[1];
                if (suffix.contains("<") && (!suffix.contains(">") || !suffix.contains("</"))) // S'il n'y a pas de balise fermante
                    suffix = suffix.replace("<", "").replace(">", "");
                if (suffix.contains("<") && suffix.contains(">") && !suffix.contains("</")) // S'il n'y a pas de balise fermante
                    suffix = suffix.replace("<", "").replace(">", "");
                SocketManager.GAME_SEND_Im_PACKET_TO_ALL("116;" + prefix + "~" + suffix);
            }
            return;
        } else if (command.equalsIgnoreCase("GONAME")
                || command.equalsIgnoreCase("JOIN")
                || command.equalsIgnoreCase("GON")) {
            Player P = World.world.getPlayerByName(infos[1]);
            if (P == null) {
                String str = "Le personnage de destination n'existe pas.";
                this.sendMessage(str);
                return;
            }
            short mapID = P.getCurMap().getId();
            int cellID = P.getCurCell().getId();

            Player perso = this.getPlayer();
            if (infos.length > 2)//Si un nom de perso est specifie
            {
                perso = World.world.getPlayerByName(infos[2]);
                if (perso == null) {
                    String str = "Le personnage e teleporter n'a pas ete trouve.";
                    this.sendMessage(str);
                    return;
                }
                if (perso.getFight() != null) {
                    String str = "La cible e teleporter est en combat.";
                    this.sendMessage(str);
                    return;
                }
            }
            perso.teleport(mapID, cellID);
            String str = "Le joueur " + perso.getName()
                    + " a ete teleporte vers " + P.getName() + ".";
            this.sendMessage(str);
            return;
        } else if (command.equalsIgnoreCase("KICKFIGHT")) {
            Player P = World.world.getPlayerByName(infos[1]);
            if (P == null || P.getFight() == null) {
                this.sendMessage("Le personnage n'a pas ete trouve ou il n'est pas en combat.");
                return;
            }
            SocketManager.GAME_SEND_GV_PACKET(P);
            if (P.getFight() != null) {
                P.getFight().leftFight(P, null);
                P.setFight(null);
            }
            SocketManager.GAME_SEND_GV_PACKET(P);
            this.sendMessage("Le personnage "
                    + P.getName() + " a ete expulse de son combat.");
            return;
        } else if (command.equalsIgnoreCase("DEBUG")) {
            Player perso = this.getPlayer();
            if (infos.length > 1)//Si un nom de perso est specifie
            {
                perso = World.world.getPlayerByName(infos[1]);
                if (perso == null) {
                    String str = "Le personnage n'a pas ete trouve.";
                    this.sendMessage(str);
                    return;
                }
                if (perso.getFight() != null) {
                    String str = "La cible est en combat.";
                    this.sendMessage(str);
                    return;
                }
            } else {
                return;
            }
            perso.warpToSavePos();
            String str = "Le joueur " + perso.getName()
                    + " a ete teleporte e son point de sauvegarde.";
            this.sendMessage(str);
            return;
        } else if (command.equalsIgnoreCase("JOBLEFT")) {
            Player perso = this.getPlayer();
            try {
                perso = World.world.getPlayerByName(infos[1]);
            } catch (Exception e) {
                // ok
            }
            if (perso == null)
                perso = this.getPlayer();
            perso.setDoAction(false);
            perso.setExchangeAction(null);
            this.sendMessage("L'action de metier e ete annule.");
            return;
        } else if (command.equalsIgnoreCase("WHO")) {
            String mess = "\n<u>Liste des joueurs en ligne :</u>";
            this.sendMessage(mess);
            int i = 0;

            for (Player player : World.world.getOnlinePlayers()) {
                if (i == 30)
                    break;
                if (player == null)
                    continue;
                i++;
                mess = player.getName() + " (" + player.getId() + ") ";
                mess += returnClasse(player.getClasse());
                mess += " ";
                mess += (player.getSexe() == 0 ? "M" : "F") + " ";
                mess += player.getLevel() + " ";
                mess += player.getCurMap().getId() + "("
                        + player.getCurMap().getX() + "/"
                        + player.getCurMap().getY() + ") ";
                mess += player.getFight() == null ? "" : "Combat ";
                mess += player.getAccount().getCurrentIp();

                this.sendMessage(mess);
            }

            if (Config.gameServer.getClients().size() - 30 > 0) {
                mess = "Et " + (Config.gameServer.getClients().size() - 30)
                        + " autres personnages";
                this.sendMessage(mess);
            }
            mess = "\n";
            this.sendMessage(mess);
            return;
        } else if (command.equalsIgnoreCase("WHOALL")) {
            String mess = "\n<u>Liste des joueurs en ligne :</u>";
            this.sendMessage(mess);
            for (GameClient client : Config.gameServer.getClients()) {
                Player player = client.getPlayer();

                if (player == null)
                    continue;

                mess = player.getName() + " (" + player.getId() + ") ";
                mess += returnClasse(player.getClasse());
                mess += " ";
                mess += (player.getSexe() == 0 ? "M" : "F") + " ";
                mess += player.getLevel() + " ";
                mess += player.getCurMap().getId() + "("
                        + player.getCurMap().getX() + "/"
                        + player.getCurMap().getY() + ") ";
                mess += player.getFight() == null ? "" : "Combat ";
                mess += player.getAccount().getCurrentIp();

                this.sendMessage(mess);
            }
            mess = "\n";
            this.sendMessage(mess);
            return;
        } else if (command.equalsIgnoreCase("WHOFIGHT")) {
            String mess = "";
            this.sendMessage("\n<u>Liste des joueurs en ligne et en combat :</u>");
            for (GameClient client : Config.gameServer.getClients()) {
                Player player = client.getPlayer();

                if (player == null)
                    continue;

                if (player.getFight() == null)
                    continue;

                mess = player.getName() + " (" + player.getId() + ") ";
                mess += returnClasse(player.getClasse());
                mess += " ";
                mess += (player.getSexe() == 0 ? "M" : "F") + " ";
                mess += player.getLevel() + " ";
                mess += player.getCurMap().getId() + "("
                        + player.getCurMap().getX() + "/"
                        + player.getCurMap().getY() + ") ";
                mess += player.getFight() == null ? "" : "Combat ";
                mess += player.getAccount().getCurrentIp();

                this.sendMessage(mess);
            }
            if (mess.equalsIgnoreCase("")) {
                this.sendMessage("Aucun joueur en combat.");
            } else {
                mess = "\n";
                this.sendMessage(mess);
            }
            return;
        } else if (command.equalsIgnoreCase("NAMEGO")
                || command.equalsIgnoreCase("NGO")) {
            Player perso = World.world.getPlayerByName(infos[1]);
            if (perso == null) {
                String str = "Le personnage e teleporter n'existe pas.";
                this.sendMessage(str);
                return;
            }
            if (perso.getFight() != null) {
                String str = "Le personnage e teleporter est en combat.";
                this.sendMessage(str);
                return;
            }
            Player P = this.getPlayer();
            if (infos.length > 2)//Si un nom de perso est specifie
            {
                P = World.world.getPlayerByName(infos[2]);
                if (P == null) {
                    String str = "Le personnage de destination n'a pas ete trouve.";
                    this.sendMessage(str);
                    return;
                }
            }
            if (P.isOnline()) {
                short mapID = P.getCurMap().getId();
                int cellID = P.getCurCell().getId();
                perso.teleport(mapID, cellID);
                String str = "Le joueur " + perso.getName()
                        + " a ete teleporte vers " + P.getName() + ".";
                this.sendMessage(str);
            } else {
                String str = "Le joueur " + P.getName()
                        + " n'est pas en ligne.";
                this.sendMessage(str);
            }
            return;
        } else if (command.equalsIgnoreCase("TP")) {
            short mapID = -1;
            int cellID = -1;
            try {
                mapID = Short.parseShort(infos[1]);
                cellID = Integer.parseInt(infos[2]);
            } catch (Exception e) {
                // ok
            }

            if (mapID == -1 || cellID == -1 || World.world.getMap(mapID) == null) {
                String str = "";
                if (mapID == -1 || World.world.getMap(mapID) == null)
                    str = "MapID invalide.";
                else
                    str = "cellID invalide.";
                this.sendMessage(str);
                return;
            }
            if (World.world.getMap(mapID).getCase(cellID) == null) {
                String str = "cellID invalide.";
                this.sendMessage(str);
                return;
            }
            Player perso = this.getPlayer();
            if (infos.length > 3)//Si un nom de perso est specifie
            {
                perso = World.world.getPlayerByName(infos[3]);
                if (perso == null || perso.getFight() != null) {
                    String str = "Le personnage n'a pas ete trouve ou est en combat";
                    this.sendMessage(str);
                    return;
                }
                if(!perso.isOnline()) {
                    perso.setCurMap(World.world.getMap(mapID));
                    perso.setCurCell(World.world.getMap(mapID).getCase(cellID));
                }
            }
            perso.teleport(mapID, cellID);
            String str = "Le joueur " + perso.getName() + " a ete teleporte.";
            this.sendMessage(str);
            return;
        } else if (command.equalsIgnoreCase("SIZE")) {
            int size = -1;
            try {
                size = Integer.parseInt(infos[1]);
            } catch (Exception e) {
                // ok
            }
            if (size == -1) {
                String str = "Taille invalide.";
                this.sendMessage(str);
                return;
            }
            Player perso = this.getPlayer();
            if (infos.length > 2)//Si un nom de perso est specifie
            {
                perso = World.world.getPlayerByName(infos[2]);
                if (perso == null) {
                    String str = "Le personnage n'a pas ete trouve.";
                    this.sendMessage(str);
                    return;
                }
            }
            perso.set_size(size);
            SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(perso.getCurMap(), perso.getId());
            SocketManager.GAME_SEND_ADD_PLAYER_TO_MAP(perso.getCurMap(), perso);
            String str = "La taille du joueur " + perso.getName()
                    + " a ete modifiee.";
            this.sendMessage(str);
            return;
        } else if (command.equalsIgnoreCase("FREEZE")) {
            Player perso = this.getPlayer();
            if (infos.length > 1) {
                perso = World.world.getPlayerByName(infos[1]);
            }
            if (perso == null) {
                String mess = "Le personnage n'existe pas.";
                this.sendMessage(mess);
                return;
            }
            if (perso.getBlockMovement())
                this.sendMessage("Le joueur n'est plus bloque.");
            else
                this.sendMessage("Le joueur est bloque.");
            perso.setBlockMovement(!perso.getBlockMovement());
            return;
        } else if (command.equalsIgnoreCase("BLOCKMAP")) {
            int i = -1;
            try {
                i = Short.parseShort(infos[1]);
            } catch (Exception e) {
                // ok
            }
            if (i == 0) {
                Main.mapAsBlocked = false;
                this.sendMessage("Map deblocke.");
            } else if (i == 1) {
                Main.mapAsBlocked = true;
                this.sendMessage("Map blocke.");
            } else {
                this.sendMessage("Aucune information.");
            }
            return;
        } else if (command.equalsIgnoreCase("BLOCKFIGHT")) {
            int i = -1;
            try {
                i = Short.parseShort(infos[1]);
            } catch (Exception e) {
                // ok
            }
            if (i == 0) {
                Main.fightAsBlocked = false;
                for(Player player : World.world.getOnlinePlayers())
                    player.sendServerMessage(player.getLang().trans("command.commandadmin.fightblock0"));
                this.sendMessage("Les combats ont etes debloques.");
            } else if (i == 1) {
                for(Player player : World.world.getOnlinePlayers())
                    player.sendServerMessage(player.getLang().trans("command.commandadmin.fightblock1"));
                this.sendMessage("Les combats ont etes bloques.");
                Main.fightAsBlocked = true;
            } else {
                this.sendMessage("Aucune information.");
            }
            return;
        } else if (command.equalsIgnoreCase("MUTE")) {
            Player player;
            String name;
            short time;

            try {
                name = infos[1];

                if(name.equals("*")) {
                    CommandPlayer.canalMute = !CommandPlayer.canalMute;
                    this.sendSuccessMessage("The main channel has been " + (CommandPlayer.canalMute ? "closed." : "opened."));
                    return;
                }

                time = Short.parseShort(infos[2]);
            } catch (Exception e) {
                this.sendErrorMessage("The name/time you've enter is/are invalid ! (max time : " + Short.MAX_VALUE + " in minutes)");
                return;
            }

            player = World.world.getPlayerByName(name);

            if (player == null || time <= 0) {
                this.sendErrorMessage("The player wasn't found or the time is negative. (max time : " + Short.MAX_VALUE + " in minutes)");
                return;
            }

            if (player.getAccount() == null) {
                this.sendErrorMessage("The account of the player wasn't found, please check the name.");
                return;
            }

            player.getAccount().mute(time, this.getPlayer().getName());
            this.sendSuccessMessage("You've mute the player " + player.getName() + " for " + time + "minute(s) effective for all players of this account !");

            if (!player.isOnline())
                this.sendErrorMessage("The player is not online, are you sure it is the correct player ?");
            return;
        } else if (command.equalsIgnoreCase("MUTEIP")) {
            Player player;
            String name;
            short time;

            try {
                name = infos[1];
                time = Short.parseShort(infos[2]);
            } catch (Exception e) {
                this.sendErrorMessage("The name/time you've enter is/are invalid ! (max time : " + Short.MAX_VALUE + " in minutes)");
                return;
            }

            player = World.world.getPlayerByName(name);

            if (player == null || time <= 0) {
                this.sendErrorMessage("The player wasn't found or the time is negative. (max time : " + Short.MAX_VALUE + " in minutes)");
                return;
            }

            if (player.getAccount() == null) {
                this.sendErrorMessage("The account of the player wasn't found, please check the name.");
                return;
            }

            String ip = player.getAccount().getLastIP();

            if (ip.equalsIgnoreCase("")) {
                this.sendErrorMessage("Sorry but the server don't have any IP of this account, check another account please.");
                return;
            }

            World.world.getAccountsByIp(ip).forEach(account -> {
                account.mute(time, this.getPlayer().getName());
                if (account.getCurrentPlayer() != null)
                    this.sendMessage("You've mute the account " + account.getName() + ".");
            });

            this.sendSuccessMessage("All the accounts of the IP (" + ip + ") have been mute for " + time + " minute(s) successfully !");
            return;
        } else if (command.equalsIgnoreCase("UNMUTEIP")) {
            Player player;
            String name;

            try {
                name = infos[1];
            } catch (Exception e) {
                this.sendErrorMessage("The name/time you've enter is/are invalid ! (max time : " + Short.MAX_VALUE + " in minutes)");
                return;
            }

            player = World.world.getPlayerByName(name);

            if (player == null) {
                this.sendErrorMessage("The player wasn't found or the time is negative. (max time : " + Short.MAX_VALUE + " in minutes)");
                return;
            }

            if (player.getAccount() == null) {
                this.sendErrorMessage("The account of the player wasn't found, please check the name.");
                return;
            }

            String ip = player.getAccount().getLastIP();

            if (ip.equalsIgnoreCase("")) {
                this.sendErrorMessage("Sorry but the server don't have any IP of this account, check another account please.");
                return;
            }

            World.world.getAccountsByIp(ip).forEach(account -> {
                account.unMute();
                if (account.getCurrentPlayer() != null)
                    this.sendMessage("The account " + account.getName() + " is free to talk.");
            });

            this.sendSuccessMessage("All the accounts of the IP (" + ip + ") are free to talk successfully !");
            return;
        } else if (command.equalsIgnoreCase("UNMUTE")) {
            Player player;
            String name;

            try {
                name = infos[1];
            } catch (Exception e) {
                this.sendErrorMessage("The name/time you've enter is/are invalid ! (max time : " + Short.MAX_VALUE + " in minutes)");
                return;
            }

            player = World.world.getPlayerByName(name);

            if (player == null) {
                this.sendErrorMessage("The player wasn't found or the time is negative. (max time : " + Short.MAX_VALUE + " in minutes)");
                return;
            }

            if (player.getAccount() == null) {
                this.sendErrorMessage("The account of the player wasn't found, please check the name.");
                return;
            }

            player.getAccount().unMute();
            this.sendSuccessMessage("You've unmute the player " + player.getName() + " effective for all players of this account !");

            if (!player.isOnline())
                this.sendErrorMessage("The player is not online, are you sure it is the correct player ?");
            return;
        } else if (command.equalsIgnoreCase("MUTEMAP")) {
            if (this.getPlayer().getCurMap() == null)
                return;
            this.getPlayer().getCurMap().mute();
            String mess = "";
            if (this.getPlayer().getCurMap().isMute())
                mess = "Vous venez de muter la MAP.";
            else
                mess = "Vous venez de demuter la MAP.";
            this.sendMessage(mess);
            return;
        } else if (command.equalsIgnoreCase("KICK")) {
            /*
            1 : Tu es resté trop longtemps inactif.
            2 : Ton personnage a atteint le niveau maximum autorisé
            3 : Pour des raisons de maintenance, le serveur va être coupé d'ici quelques minutes.
            4 : Votre connexion a été coupée pour des raisons de maintenance.
            5 : Retry connection (Oui ou Non)
            6 : Le nombre d'objets pour cet inventaire est déjà atteint.
            7 : Cette opération n'est pas autorisée ici.
            8 : Cet objetn 'est plus disponible.
             */

            Player player;
            String name, reason = "";

            try {
                name = infos[1];
            } catch (Exception ignored) {
                this.sendErrorMessage("You need to give the name of the player !");
                return;
            }

            try {
                reason = msg.substring(infos[0].length() + infos[1].length() + 1);
            } catch (Exception ignored) {}

            player = World.world.getPlayerByName(name);

            if (player == null) {
                this.sendErrorMessage("The name of the player is invalid or non-existent !");
                return;
            }

            if (player.isOnline()) {
                if (reason.isEmpty()) {
                    player.send("M018|" + this.getPlayer().getName() + ";");
                } else {
                    player.send("M018|" + this.getPlayer().getName() + ";<br>" + reason);
                }
                player.getGameClient().kick();
                this.sendSuccessMessage("The player have been kicked successfully.");
            } else {
                this.sendErrorMessage("The player isn't connected, check the name please.");
            }
            return;
        } else if (command.equalsIgnoreCase("JAIL")) {
            short mapID = 666;
            int cellID = getCellJail();
            if (mapID == -1 || cellID == -1 || World.world.getMap(mapID) == null) {
                String str = "MapID ou cellID invalide.";
                if (cellID == -1)
                    str = "cellID invalide.";
                else
                    str = "MapID invalide.";
                this.sendMessage(str);
                return;
            }
            if (World.world.getMap(mapID).getCase(cellID) == null) {
                String str = "cellID invalide.";
                this.sendMessage(str);
                return;
            }
            try {
                if (infos.length > 1)//Si un nom de perso est specifie
                {
                    Player perso = World.world.getPlayerByName(infos[1]);
                    if (perso.getGroupe() != null) {
                        String str = "Il est interdit d'emprisonner un personnage ayant des droits.";
                        this.sendMessage(str);
                        return;
                    }
                    if (perso == null || perso.getFight() != null) {
                        String str = "Le personnage n'a pas ete trouve ou est en combat.";
                        this.sendMessage(str);
                        return;
                    }
                    if (perso.isOnline())
                        perso.teleport(mapID, cellID);
                    else
                        perso.teleportD(mapID, cellID);
                    String str = "Le joueur " + perso.getName()
                            + " a ete teleporte emprisonne.";
                    this.sendMessage(str);
                }
            } catch (Exception e) {
                this.sendMessage("Introuvable.");
                // ok
                return;
            }
            return;
        } else if (command.equalsIgnoreCase("UNJAIL")) {
            Player perso = World.world.getPlayerByName(infos[1]);
            if (perso == null || perso.getFight() != null) {
                String str = "Le personnage n'a pas ete trouve ou est en combat.";
                this.sendMessage(str);
                return;
            }
            if (infos.length > 1 && perso.isInPrison())//Si un nom de perso est specifie
            {
                perso.warpToSavePos();
                String str = "Le joueur " + perso.getName()
                        + " a ete teleporte e son point de sauvegarde.";
                this.sendMessage(str);
                return;
            }
            return;
        } else if (command.equalsIgnoreCase("BAN")) {
            Player player = World.world.getPlayerByName(infos[1]);
            short days = 0;

            try {
                days = Short.parseShort(infos[2]);
            } catch(Exception ignored) {
                this.sendMessage("You've not enter a day value (the time while the account is banned), the default value is unlimited.");
            }

            if (player == null) {
                this.sendErrorMessage("The player was not found, check the name please.");
                return;
            }
            if (player.getAccount() == null)
                ((AccountData) DatabaseManager.get(AccountData.class)).load(player.getAccID());
            if (player.getAccount() == null) {
                this.sendErrorMessage("The account of the player was not found, contact a supervisor.");
                return;
            }

            player.getAccount().setBanned(true);
            ((AccountData) DatabaseManager.get(AccountData.class)).updateBannedTime(player.getAccount(), System.currentTimeMillis() + 86400000 * (days == 0 ? 999 : days));

            if (player.getFight() == null) {
                if (player.getGameClient() != null)
                    player.getGameClient().kick();
            } else {
                SocketManager.send(player, "Im1201;" + this.getPlayer().getName());
            }
            this.sendSuccessMessage("You've kick and ban the player " + player.getName() + "(Acc: " + player.getAccount().getName() + ") for " + (days == 0 ? "unlimited" : days) + " day(s).");
            return;
        } else if (command.equalsIgnoreCase("BANACCOUNT")) {
            String mess = "Le compte est introuvable";
            String A = "";
            try {
                A = infos[1];
            } catch (Exception e) {
                // ok
            }
            if (A.equalsIgnoreCase("")) {
                this.sendMessage("Il faut le nom de compte.");
                return;
            }
            for (Account account : World.world.getAccounts()) {
                if (account == null)
                    continue;
                if (!account.getName().equalsIgnoreCase(A))
                    continue;
                account.setBanned(true);
                ((AccountData) DatabaseManager.get(AccountData.class)).update(account);
                mess = "Vous avez banni le compte " + A;
                Player p = account.getCurrentPlayer();
                if (p != null) {
                    if (p.isOnline()) {
                        mess += " dont le joueur est " + p.getName();
                        if (p.getFight() == null) {
                            if (p.getGameClient() != null)
                                p.getGameClient().kick();
                        } else {
                            SocketManager.send(p, "Im1201;"
                                    + this.getPlayer().getName());
                        }
                    }
                }
            }
            this.sendMessage(mess
                    + ".");
            return;
        } else if (command.equalsIgnoreCase("BANBYID")) {
            int ID = -1;
            String mess = "Aucun personnage n'a ete trouve.";
            try {
                ID = Integer.parseInt(infos[1]);
            } catch (Exception e) {
                // ok
            }

            if (ID <= 0) {
                this.sendMessage("Une IP est necessaire.");
                return;
            }
            for (Player player : World.world.getPlayers()) {
                if (player == null)
                    continue;
                if (player.getId() == ID) {
                    if (player.getAccount() == null)
                        ((AccountData) DatabaseManager.get(AccountData.class)).load(player.getAccID());
                    if (player.getAccount() == null) {
                        this.sendMessage("Le personnage n'a pas de compte.");
                        if (player.getGameClient() != null)
                            player.getGameClient().kick();
                        return;
                    }
                    player.getAccount().setBanned(true);
                    ((AccountData) DatabaseManager.get(AccountData.class)).update(player.getAccount());
                    if (player.getFight() == null) {
                        if (player.getGameClient() != null)
                            player.getGameClient().kick();
                    } else {
                        SocketManager.send(player, "Im1201;"
                                + this.getPlayer().getName());
                    }
                    mess = "Vous avez banni " + player.getName() + ".";
                }
            }
            this.sendMessage(mess);
            return;
        } else if (command.equalsIgnoreCase("BANBYIP")) {
            String IP = "";
            try {
                IP = infos[1];
            } catch (Exception e) {
                // ok
            }

            if (IP.equalsIgnoreCase("")) {
                this.sendMessage("Une IP est necessaire.");
                return;
            }
            for (Account a : World.world.getAccountsByIp(IP)) {
                if (a == null)
                    continue;
                if (!a.getLastIP().equalsIgnoreCase(IP))
                    continue;

                a.setBanned(true);
                ((AccountData) DatabaseManager.get(AccountData.class)).update(a);
                this.sendMessage("Le compte "
                        + a.getName() + " a ete banni.");
                if (a.isOnline()) {
                    GameClient gc = a.getGameClient();
                    if (gc == null)
                        continue;
                    this.sendMessage("Le joueur "
                            + gc.getPlayer().getName() + " a ete kick.");
                    gc.kick();
                }
            }
            Config.exchangeClient.send("SB" + IP);
            ((BanIpData) DatabaseManager.get(BanIpData.class)).insert(IP);
            this.sendMessage("L'IP " + IP + " a ete banni.");
            return;
        } else if (command.equalsIgnoreCase("BANIP")) {
            Player P = null;
            try {
                P = World.world.getPlayerByName(infos[1]);
            } catch (Exception e) {
                // ok
            }

            if (P == null) {
                this.sendMessage("Le personnage n'a pas ete trouve.");
                return;
            }
            String IP = P.getAccount().getLastIP();
            if (IP.equalsIgnoreCase("")) {
                this.sendMessage("L'IP est invalide.");
                return;
            }
            ((BanIpData) DatabaseManager.get(BanIpData.class)).delete(IP);
            ((BanIpData) DatabaseManager.get(BanIpData.class)).insert(IP);
            for (Account a : World.world.getAccountsByIp(IP)) {
                if (!a.getLastIP().equalsIgnoreCase(IP))
                    continue;

                a.setBanned(true);
                ((AccountData) DatabaseManager.get(AccountData.class)).update(a);
                this.sendMessage("Le compte "
                        + a.getName() + " a ete banni.");
                if (a.isOnline()) {
                    GameClient gc = a.getGameClient();
                    if (gc == null)
                        continue;
                    this.sendMessage("Le joueur "
                            + gc.getPlayer().getName() + " a ete kick.");
                    gc.kick();
                }
            }
            Config.exchangeClient.send("SB" + IP);
            ((BanIpData) DatabaseManager.get(BanIpData.class)).insert(IP);
            this.sendMessage("L'IP " + IP + " a ete banni.");
            return;
        } else if (command.equalsIgnoreCase("SHOWITEM")) {
            Player perso = this.getPlayer();
            String name = null;
            try {
                name = infos[1];
            } catch (Exception e) {
                // ok
            }

            perso = World.world.getPlayerByName(name);
            if (perso == null) {
                String mess = "Le personnage n'a pas ete trouve.";
                this.sendMessage(mess);
                return;
            }
            String mess = "==========\n"
                    + "Liste d'items sur le personnage :\n";
            this.sendMessage(mess);
            for (Entry<Integer, GameObject> entry : perso.getItems().entrySet()) {
                mess = entry.getValue().getGuid() + " || "
                        + entry.getValue().getTemplate().getName() + " || "
                        + entry.getValue().getQuantity();
                this.sendMessage(mess);
            }

            this.sendMessage("Le personnage possede : "
                    + perso.getKamas() + " Kamas.\n");
            mess = "==========";
            this.sendMessage(mess);
            return;
        } else if (command.equalsIgnoreCase("SHOWBANK")) {
            Player perso = this.getPlayer();
            String name = null;
            try {
                name = infos[1];
            } catch (Exception e) {
                // ok
            }

            perso = World.world.getPlayerByName(name);
            if (perso == null) {
                String mess = "Le personnage n'a pas ete trouve.";
                this.sendMessage(mess);
                return;
            }
            Account cBank = perso.getAccount();
            String mess = "==========\n" + "Liste d'items dans la banque :";
            this.sendMessage(mess);
            for (GameObject entry : cBank.getBank()) {
                mess = entry.getGuid() + " || "
                        + entry.getTemplate().getName() + " || "
                        + entry.getQuantity();
                this.sendMessage(mess);
            }
            this.sendMessage("Le personnage possede : "
                    + cBank.getBankKamas() + " Kamas en banque.");
            mess = "==========";
            this.sendMessage(mess);
            return;
        } else if (command.equalsIgnoreCase("SHOWSTORE")) {
            Player perso = this.getPlayer();
            String name = null;
            try {
                name = infos[1];
            } catch (Exception e) {
                // ok
            }

            perso = World.world.getPlayerByName(name);
            if (perso == null) {
                String mess = "Le personnage n'a pas ete trouve.";
                this.sendMessage(mess);
                return;
            }
            String mess = "==========\n" + "Liste d'items dans le Store :";
            this.sendMessage(mess);
            for (Entry<Integer, Integer> obj : perso.getStoreItems().entrySet()) {
                GameObject entry = World.world.getGameObject(obj.getKey());
                mess = entry.getGuid() + " || " + entry.getTemplate().getName()
                        + " || " + entry.getQuantity();
                this.sendMessage(mess);
            }

            mess = "==========";
            this.sendMessage(mess);
            return;
        } else if (command.equalsIgnoreCase("SHOWMOUNT")) {
            Player perso = this.getPlayer();
            String name = null;
            try {
                name = infos[1];
            } catch (Exception e) {
                // ok
            }

            perso = World.world.getPlayerByName(name);
            if (perso == null) {
                String mess = "Le personnage n'a pas ete trouve.";
                this.sendMessage(mess);
                return;
            }
            String mess = "==========\n" + "Liste d'items dans la banque :";
            this.sendMessage(mess);
            if(perso.getMount() != null) {
                for (Entry<Integer, GameObject> entry : perso.getMount().getObjects().entrySet()) {
                    mess = entry.getValue().getGuid() + " || "
                            + entry.getValue().getTemplate().getName() + " || "
                            + entry.getValue().getQuantity();
                    this.sendMessage(mess);
                }
            }
            mess = "==========";
            this.sendMessage(mess);
            return;
        } else if (command.equalsIgnoreCase("BLOCKTRADE")) {
            int i = -1;
            try {
                i = Short.parseShort(infos[1]);
            } catch (Exception e) {
                // ok
            }

            if (i == 0) {
                Main.tradeAsBlocked = false;
                this.sendMessage("Les échanges ont été débloqués.");
            } else if (i == 1) {
                Main.tradeAsBlocked = true;
                this.sendMessage("Tous les échanges sont bloqués.");
            } else {
                this.sendMessage("Aucune information.");
            }
            return;
        } else if (command.equalsIgnoreCase("ERASEALLMAP")) {
            for (GameMap map : World.world.getMaps())
                map.delAllDropItem();
            this.sendMessage("Tous les objets sur toutes les maps ont été supprimés.");
            return;
        } else if (command.equalsIgnoreCase("ERASEMAP")) {
            this.getPlayer().getCurMap().delAllDropItem();
            this.sendMessage("Les objets de la map ont été supprimés.");
            return;
        } else if (command.equalsIgnoreCase("MORPH")) {
            int morphID = -9;
            try {
                morphID = Integer.parseInt(infos[1]);
            } catch (Exception e) {
                // ok
            }

            if (morphID == -9) {
                String str = "MorphID invalide.";
                this.sendMessage(str);
                return;
            }
            Player target = this.getPlayer();
            if (infos.length > 2)//Si un nom de perso est specifie
            {
                target = World.world.getPlayerByName(infos[2]);
                if (target == null) {
                    String str = "Le personnage n'a pas ete trouve.";
                    this.sendMessage(str);
                    return;
                }
            }
            if (morphID == -1) {
                morphID = target.getClasse() * 10 + target.getSexe();
                target.setGfxId(morphID);
                SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(target.getCurMap(), target.getId());
                SocketManager.GAME_SEND_ADD_PLAYER_TO_MAP(target.getCurMap(), target);
                String str = "Le joueur " + target.getName()
                        + " a son apparence originale.";
                this.sendMessage(str);
                return;
            } else {
                target.setGfxId(morphID);
                SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(target.getCurMap(), target.getId());
                SocketManager.GAME_SEND_ADD_PLAYER_TO_MAP(target.getCurMap(), target);
                String str = "Le joueur " + target.getName()
                        + " a ete transforme.";
                this.sendMessage(str);
                return;
            }
        } else if (command.equalsIgnoreCase("DEMORPHALL")) {
            for (Player player : World.world.getOnlinePlayers()) {
                player.setGfxId(player.getClasse() * 10 + player.getSexe());
            }
            this.sendMessage("Tous les joueurs connectes ont leur apparence originale.");
            return;
        } else if (command.equalsIgnoreCase("ADDHONOR")) {
            int honor = 0;
            try {
                honor = Integer.parseInt(infos[1]);
            } catch (Exception e) {
                // ok
            }

            Player perso = this.getPlayer();
            if (infos.length > 2)//Si un nom de perso est specifie
            {
                perso = World.world.getPlayerByName(infos[2]);
                if (perso == null) {
                    String str = "Le personnage n'a pas ete trouve.";
                    this.sendMessage(str);
                    return;
                }
            }
            String str = "Vous avez ajoute " + honor + " points d'honneur e "
                    + perso.getName() + ".";
            if (perso.getAlignment() != Constant.ALIGNEMENT_MERCENAIRE) {
                str = "Le joueur n'est pas mercenaire ... l'action a ete annulee.";
                this.sendMessage(str);
                return;
            }
            perso.addHonor(honor);
            this.sendMessage(str);
            return;
        } else if (command.equalsIgnoreCase("HONOR")) {
            int honor = 0;
            try {
                honor = Integer.parseInt(infos[1]);
            } catch (Exception e) {
                // ok
            }

            Player perso = this.getPlayer();
            if (infos.length > 2)//Si un nom de perso est specifie
            {
                perso = World.world.getPlayerByName(infos[2]);
                if (perso == null) {
                    String str = "Le personnage n'a pas ete trouve";
                    this.sendMessage(str);
                    return;
                }
            }
            String str = "Vous avez ajoute " + honor + " points d'honneur e "
                    + perso.getName() + ".";
            if (perso.getAlignment() == Constant.ALIGNEMENT_NEUTRE) {
                str = "Le joueur est neutre ... l'action a ete annulee.";
                this.sendMessage(str);
                return;
            }
            perso.addHonor(honor);
            this.sendMessage(str);
            return;
        } else if (command.equalsIgnoreCase("NOAGRO")) {
            Player perso = this.getPlayer();
            String name = null;
            try {
                name = infos[1];
            } catch (Exception e) {
                // ok
            }

            perso = World.world.getPlayerByName(name);
            if (perso == null) {
                String mess = "Le personnage n'existe pas.";
                this.sendMessage(mess);
                return;
            }
            perso.setCanAggro(!perso.canAggro());
            String mess = perso.getName();
            if (perso.canAggro())
                mess += " peut maintenant etre aggresse.";
            else
                mess += " ne peut plus etre agresse.";
            this.sendMessage(mess);
            if (!perso.isOnline()) {
                mess = "Le personnage " + perso.getName()
                        + " n'etait pas connecte.";
                this.sendMessage(mess);
            }
            return;
        } else if (command.equalsIgnoreCase("WHOIS")) {
            String name = "";
            Player perso = null;
            try {
                name = infos[1];
            } catch (Exception e) {
                // ok
            }

            if (name == "")
                return;

            perso = World.world.getPlayerByName(name);
            if (perso == null) {
                String mess = "Le personnage n'existe pas.";
                this.sendMessage(mess);
                return;
            } else if (perso.getAccount().getLastIP().equalsIgnoreCase("")) {
                String mess = "Aucune IP.";
                this.sendMessage(mess);
                return;
            }
            List<Account> accounts = World.world.getAccountsByIp(perso.getAccount().getLastIP());
            String mess = "Whois sur le joueur : " + name + "\n";
            mess += "Derniere IP : " + perso.getAccount().getLastIP() + "\n";
            int i = 1;
            for (Account a : accounts) {
                String persos = "";
                for (Entry<Integer, Player> entry2 : a.getPlayers().entrySet()) {
                    perso = entry2.getValue();
                    if (perso != null) {
                        if (persos.equalsIgnoreCase(""))
                            persos += perso.getName()
                                    + ((perso.getGroupe() != null) ? ":"
                                    + perso.getGroupe().getName() : "");
                        else
                            persos += ", "
                                    + perso.getName()
                                    + ((perso.getGroupe() != null) ? ":"
                                    + perso.getGroupe().getName() : "");
                    }
                }
                if (!persos.equalsIgnoreCase("")) {
                    mess += "[" + i + "] " + a.getName() + " - " + persos
                            + ((a.isBanned()) ? " : banni" : "") + "\n";
                    i++;
                }
            }
            this.sendMessage(mess);
            return;
        } else if (command.equalsIgnoreCase("CLEANFIGHT")) {
            this.getPlayer().getCurMap().getFights().clear();
            this.sendMessage("Tous les combats de la map ont etes supprimes.");
            return;
        } else if (command.equalsIgnoreCase("ETATSERVER")) {
            int etat = 1;
            try {
                etat = Integer.parseInt(infos[1]);
            } catch (Exception e) {
                // ok
            }
            GameServer.setState(etat);
            this.sendMessage("Vous avez change l'etat du serveur en "
                    + etat + ".");
            return;
        } else if (command.equalsIgnoreCase("MPTOTP")) {
            this.getPlayer().mpToTp = !this.getPlayer().mpToTp;
            String mess = "";
            if (this.getPlayer().mpToTp)
                mess = "Vous venez d'activer le MP to TP.";
            else
                mess = "Vous venez de desactiver le MP to TP.";
            this.sendMessage(mess);
            return;
        } else if (command.equalsIgnoreCase("RETURNTP")) {
            for (Player perso : World.world.getOnlinePlayers()) {
                if (perso.thatMap == -1 || perso.getFight() != null)
                    continue;
                perso.teleport((short) perso.thatMap, perso.thatCell);
                perso.thatMap = -1;
                perso.thatCell = -1;
            }
            this.sendMessage("Vous venez de renvoyer tous les joueurs e leur ancienne position.");
            return;
        } else if (command.equalsIgnoreCase("GETCASES")) {
            if (this.getPlayer().getCases) {
                this.sendMessage("Le getCases viens d'etre disable :");
                String i = "";
                for (Integer c : this.getPlayer().thisCases)
                    i += ";" + c;
                this.sendMessage(i.substring(1));
                this.getPlayer().thisCases.clear();
            } else
                this.sendMessage("Le getCases viens d'etre active. Deplacez-vous sur la map pour capturer les cellules.");
            this.getPlayer().getCases = !this.getPlayer().getCases;
            return;
        } else if (command.equalsIgnoreCase("WALKFAST")) {
            if (this.getPlayer().walkFast)
                this.sendMessage("La marche instantanne viens d'etre disable.");
            else
                this.sendMessage("La marche instantanne viens d'etre active.");
            this.getPlayer().walkFast = !this.getPlayer().walkFast;
            return;
        } else if (command.equalsIgnoreCase("LISTMAP")) {
            String data = "";
            ArrayList<GameMap> i = World.world.getMapByPosInArray(this.getPlayer().getCurMap().getX(), this.getPlayer().getCurMap().getY());
            for (GameMap map : i)
                data += map.getId() + " | ";
            this.sendMessage(data);
            return;
        } else if (command.equalsIgnoreCase("DELINVENTORY")) {
            Player perso = null;
            infos = msg.split(" ", 3);
            try {
                perso = World.world.getPlayerByName(infos[1]);
            } catch (Exception e) {
                // ok
            }

            if (perso == null) {
                this.sendMessage("Le nom du personnage est incorrect.");
                return;
            }
            int i = 0;
            ArrayList<GameObject> list = new ArrayList<GameObject>();

            list.addAll(perso.getItems().values());
            for (GameObject obj : list) {
                int guid = obj.getGuid();
                SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(perso, guid);
                perso.deleteItem(guid);
                i++;
            }

            this.sendMessage("Vous venez de supprimer "
                    + i + " objets au joueur " + perso.getName() + ".");
            return;
        } else if (command.equalsIgnoreCase("RMOBS")) {
            this.getPlayer().getCurMap().refreshSpawns();
            String mess = "Les spawns de monstres sur la map ont etes rafraichit.";
            this.sendMessage(mess);
            return;
        } else if (command.equalsIgnoreCase("DELJOB")) {
            Player perso = this.getPlayer();
            infos = msg.split(" ", 3);
            int job = -1;
            try {
                job = Integer.parseInt(infos[1]);
            } catch (Exception e) {
                // ok
            }

            try {
                perso = World.world.getPlayerByName(infos[2]);
            } catch (Exception e) {
                // ok
            }

            if (perso == null) {
                this.sendMessage("Le nom du personnage est incorrect.");
                return;
            }
            if (job < 1)
                return;
            JobStat jobStats = perso.getMetierByID(job);
            if (jobStats == null)
                return;
            perso.unlearnJob(jobStats.getId());
            SocketManager.GAME_SEND_STATS_PACKET(perso);
            ((PlayerData) DatabaseManager.get(PlayerData.class)).update(perso);
            SocketManager.GAME_SEND_MESSAGE(perso, perso.getLang().trans("command.commandadmin.unlearn.spell"));
            this.sendMessage("Vous avez supprimé le métier "
                    + job + " sur le personnage " + perso.getName() + ".");
            return;
        } else if (command.equalsIgnoreCase("ADDTRIGGER")) {
            String args = "";
            try {
                args = infos[1];
            } catch (Exception e) {
                // ok
            }

            if (args.equals("")) {
                String str = "Valeur invalide.";
                this.sendMessage(str);
                return;
            }

            this.getPlayer().getCurCell().addOnCellStopAction(0, args, "-1", null);
            boolean success = ((ScriptedCellData) DatabaseManager.get(ScriptedCellData.class)).update(this.getPlayer().getCurMap().getId(), this.getPlayer().getCurCell().getId(), 0, 1, args, "-1");
            String str = "";
            if (success)
                str = "Le trigger a ete ajoute.";
            else
                str = "Le trigger n'a pas ete ajoute.";
            this.sendMessage(str);
            return;
        } else if (command.equalsIgnoreCase("DELTRIGGER")) {
            int cellID = -1;
            try {
                cellID = Integer.parseInt(infos[1]);
            } catch (Exception e) {
                // ok
            }

            if (cellID == -1
                    || this.getPlayer().getCurMap().getCase(cellID) == null) {
                String str = "CellID invalide.";
                this.sendMessage(str);
                return;
            }
            this.getPlayer().getCurMap().getCase(cellID).clearOnCellAction();
            boolean success = ((ScriptedCellData) DatabaseManager.get(ScriptedCellData.class)).delete(this.getPlayer().getCurMap().getId(), cellID);
            String str = "";
            if (success)
                str = "Le trigger a ete retire.";
            else
                str = "Le trigger n'a pas ete retire.";
            this.sendMessage(str);
            return;
        } else if (command.equalsIgnoreCase("SAVETHAT")) {
            this.getPlayer().thatMap = this.getPlayer().getCurMap().getId();
            this.getPlayer().thatCell = this.getPlayer().getCurCell().getId();
            this.sendMessage("Vous avez sauvegarde la map "
                    + this.getPlayer().thatMap
                    + " et la cellule "
                    + this.getPlayer().thatCell + ".");
            return;
        } else if (command.equalsIgnoreCase("APPLYTHAT")) {
            if (this.getPlayer().thatMap == -1 || this.getPlayer().thatCell == -1) {
                this.sendMessage("Impossible d'ajouter le trigger, veuillez utiliser la commande SAVETHAT avant.");
                return;
            }
            this.getPlayer().getCurCell().addOnCellStopAction(0, this.getPlayer().thatMap + "," + this.getPlayer().thatCell, "-1", null);
            ((ScriptedCellData) DatabaseManager.get(ScriptedCellData.class)).update(this.getPlayer().getCurMap().getId(), this.getPlayer().getCurCell().getId(), 0, 1, this.getPlayer().thatMap + "," + this.getPlayer().thatCell, "-1");
            this.sendMessage("REPLACE INTO `scripted_cells` VALUES ('" + this.getPlayer().getCurMap().getId() + "', '" + this.getPlayer().getCurCell().getId() + "','0','1','" + this.getPlayer().thatMap + "," + this.getPlayer().thatCell + "','-1');" +
                    "\nVous avez applique le trigger.");
            this.getPlayer().thatMap = -1;
            this.getPlayer().thatCell = -1;

            return;
        } else if (command.equalsIgnoreCase("STRIGGER")) {
            this.getPlayer().thatMap = this.getPlayer().getCurMap().getId();
            this.getPlayer().thatCell = this.getPlayer().getCurCell().getId();
            this.sendMessage("Vous avez sauvegarde la map "
                    + this.getPlayer().thatMap
                    + " et la cellule "
                    + this.getPlayer().thatCell + ".");
            return;
        } else if (command.equalsIgnoreCase("APTRIGGER")) {
            if (this.getPlayer().thatMap == -1
                    || this.getPlayer().thatCell == -1) {
                this.sendMessage("Impossible d'ajouter le trigger, veuillez utiliser la commande STRIGGER avant.");
                return;
            }
            World.world.getMap((short) this.getPlayer().thatMap).getCase(this.getPlayer().thatCell).addOnCellStopAction(0, this.getPlayer().getCurMap().getId()
                    + "," + this.getPlayer().getCurCell().getId(), "-1", null);
            ((ScriptedCellData) DatabaseManager.get(ScriptedCellData.class)).update(this.getPlayer().thatMap, this.getPlayer().thatCell, 0, 1, this.getPlayer().getCurMap().getId()
                    + "," + this.getPlayer().getCurCell().getId(), "-1");
            this.getPlayer().thatMap = -1;
            this.getPlayer().thatCell = -1;
            this.sendMessage("Vous avez applique le trigger.");
            return;
        } else if (command.equalsIgnoreCase("INFOS")) {
            long uptime = System.currentTimeMillis() - Config.startTime;
            int day = (int) (uptime / (1000 * 3600 * 24));
            uptime %= (1000 * 3600 * 24);
            int hour = (int) (uptime / (1000 * 3600));
            uptime %= (1000 * 3600);
            int min = (int) (uptime / (1000 * 60));
            uptime %= (1000 * 60);
            int sec = (int) (uptime / (1000));

            String message = "\n<u><b>Global informations system of the emulator :</b></u>\n\n<u>Uptime :</u> " + day + "j " + hour + "h " + min + "m " + sec + "s.\n";
            message += "Online players         : " + Config.gameServer.getClients().size() + "\n";
            message += "Unique online players  : " + Config.gameServer.getPlayersNumberByIp() + "\n";
            message += "Online clients         : " + Config.gameServer.getClients().size() + "\n";


            int mb = 1024 * 1024;
            Runtime instance = Runtime.getRuntime();

            message += "\n<u>Heap utilization statistics :</u>";
            message += "\nTotal Memory : " + instance.totalMemory() / mb + " Mo.";
            message += "\nFree Memory  : " + instance.freeMemory() / mb + " Mo.";
            message += "\nUsed Memory  : " + (instance.totalMemory() - instance.freeMemory()) / mb + " Mo.";
            message += "\nMax Memory   : " + instance.maxMemory() / mb + " Mo.";
            message += "\n\n<u>Available processor :</u> " + instance.availableProcessors();
            Set<Thread> list = Thread.getAllStackTraces().keySet();
            int news = 0, running = 0, blocked = 0, waiting = 0, sleeping = 0, terminated = 0;
            for(Thread thread : list) {
                switch(thread.getState()) {
                    case NEW: news++; break;
                    case RUNNABLE: running++; break;
                    case BLOCKED: blocked++; break;
                    case WAITING: waiting++; break;
                    case TIMED_WAITING: sleeping++; break;
                    case TERMINATED: news++; break;
                }
            }

            message +="\n\n<u>Informations of " + list.size() + " threads :</u> ";
            message += "\nNEW           : " + news;
            message += "\nRUNNABLE      : " + running;
            message += "\nBLOCKED       : " + blocked;
            message += "\nWAITING       : " + waiting;
            message += "\nTIMED_WAITING : " + sleeping;
            message += "\nTERMINATED    : " + terminated;

            this.sendMessage(message + "\n");

            if(infos.length > 1) {
                message = "List of all threads :\n";
                for(Thread thread : list)
                    message += "- " + thread.getId() + " -> " + thread.getName() + " -> " + thread.getState().name().toUpperCase() + "" + (thread.isDaemon() ? " (Daemon)" : "") + ".\n";
                this.sendMessage(message);
            }
            return;
        } else if (command.equalsIgnoreCase("STARTFIGHT")) {
            if (this.getPlayer().getFight() == null) {
                this.sendMessage("Vous devez etre dans un combat.");
                return;
            }
            this.getPlayer().getFight().startFight();
            this.sendMessage("Le combat a ete demarre.");
            return;
        } else if (command.equalsIgnoreCase("ENDFIGHTNULL")) {
            String name = infos.length > 2 ? infos[2] : "";
            Player target = !name.isEmpty() ? World.world.getPlayerByName(name) : this.getPlayer();

            //team 1 = red, team 0 = blue
            if(target != null && target.getFight() != null) {
                target.getFight().endFight((byte) 0);
                this.sendSuccessMessage("You've ended the fight successfully.");
            } else {
                if(target == null)
                    this.sendErrorMessage("The player name you've indicate is invalid");
                else
                    this.sendErrorMessage("The player you've indicate is not in a fight (if he's bugued, try KICKFIGHT command).");
            }
            return;
        } else if (command.equalsIgnoreCase("ENDFIGHT")) {
            byte i;
            String name = infos.length > 2 ? infos[2] : "";
            Player target = !name.isEmpty() ? World.world.getPlayerByName(name) : this.getPlayer();

            try {
                i = Byte.parseByte(infos[1]);
            } catch (Exception e) {
                this.sendErrorMessage("Please, specify a type (0: no-winners/loosers, 1: red team win, 2: blue team win).");
                return;
            }

            //team 1 = red, team 0 = blue
            if(target != null && target.getFight() != null) {
                target.getFight().endFight(i);
                this.sendSuccessMessage("You've ended the fight successfully.");
            } else {
                if(target == null)
                    this.sendErrorMessage("The player name you've indicate is invalid");
                else
                    this.sendErrorMessage("The player you've indicate is not in a fight (if he's bugued, try KICKFIGHT command).");
            }
            return;
        } else if (command.equalsIgnoreCase("ENDFIGHTALL")) {
            try {
                for (GameClient client : Config.gameServer.getClients()) {
                    Player player = client.getPlayer();
                    if (player == null)
                        continue;
                    Fight f = player.getFight();
                    if (f == null)
                        continue;
                    try {
                        if (f.getLaunchTime() > 1)
                            continue;
                        f.endFight((byte) 1);
                        this.sendMessage("Le combat de "
                                + player.getName() + " a ete termine.");
                    } catch (Exception e) {
                        // ok
                        this.sendMessage("Le combat de "
                                + player.getName() + " a deje ete termine.");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                this.sendMessage("Erreur lors de la commande endfightall : "
                        + e.getMessage() + ".");
            } finally {
                this.sendMessage("Tous les combats ont ete termines.");
            }
            return;
        } else if (command.equalsIgnoreCase("MAPINFO")) {
            String mess = "==========\n" + "Liste des PNJs de la Map :";
            this.sendMessage(mess);
            GameMap map = this.getPlayer().getCurMap();
            for (Entry<Integer, Npc> entry : map.getNpcs().entrySet()) {
                mess = entry.getKey()
                        + " | "
                        + entry.getValue().getTemplate().getId()
                        + " | "
                        + entry.getValue().getCellId()
                        + " | "
                        + entry.getValue().getTemplate().getInitQuestionId(this.getPlayer().getCurMap().getId());
                this.sendMessage(mess);
            }
            mess = "Liste des groupes de monstres :";
            this.sendMessage(mess);
            for (Entry<Integer, MobGroup> entry : map.getMobGroups().entrySet()) {
                mess = entry.getKey() + " | " + entry.getValue().getCellId()
                        + " | " + entry.getValue().getAlignement() + " | "
                        + entry.getValue().getMobs().size();
                this.sendMessage(mess);
            }
            mess = "==========";
            this.sendMessage(mess);
            return;
        } else if (command.equalsIgnoreCase("UNBANIP")) {
            Player perso = null;
            try {
                perso = World.world.getPlayerByName(infos[1]);
            } catch (Exception e) {
                // ok
            }
            if (perso == null) {
                this.sendMessage("Le nom du personnage n'est pas bon.");
                return;
            }
            ((BanIpData) DatabaseManager.get(BanIpData.class)).delete(perso.getAccount().getCurrentIp());
            this.sendMessage("L'IP a ete debanni.");
            return;
        } else if (command.equalsIgnoreCase("UNBAN")) {
            Player P = World.world.getPlayerByName(infos[1]);
            if (P == null) {
                this.sendMessage("Personnage non trouve.");
                return;
            }
            if (P.getAccount() == null)
                ((AccountData) DatabaseManager.get(AccountData.class)).load(P.getAccID());
            if (P.getAccount() == null) {
                this.sendMessage("Le personnage n'a pas de compte.");
                return;
            }
            P.getAccount().setBanned(false);
            ((AccountData) DatabaseManager.get(AccountData.class)).update(P.getAccount());
            this.sendMessage("Vous avez debanni "
                    + P.getName() + ".");
            return;
        } else if (command.equalsIgnoreCase("EXIT")) {
            this.sendMessage("Lancement du reboot.");
            Main.runnables.add(() -> Main.stop("Exit by administrator"));
            return;
        } else  if (command.equalsIgnoreCase("SETMAX")) {
            short i = Short.parseShort(infos[1]);
            this.sendMessage("Le maximum de joueur a été fixer à : " + i);
            GameServer.MAX_PLAYERS = i;
            return;
        } else if (command.equalsIgnoreCase("SAVE") && !Config.isSaving) {
            TimerWaiter.addNext(() -> WorldSave.cast(1), 1000);
            String mess = "Sauvegarde lancee!";
            this.sendMessage(mess);
            return;
        } else if (command.equalsIgnoreCase("LEVEL")) {
            int count;
            try {
                count = Integer.parseInt(infos[1]);
                if (count < 1) count = 1;
                if (count > World.world.getExpLevelSize()) count = World.world.getExpLevelSize();

                Player player = this.getPlayer();

                if (infos.length == 3) {
                    player = World.world.getPlayerByName(infos[2]);
                    if (player == null) player = this.getPlayer();
                }

                if (player.getLevel() < count) {
                    while (player.getLevel() < count)
                        player.levelUp(false, true);
                    SocketManager.GAME_SEND_NEW_LVL_PACKET(player.getGameClient(), player.getLevel());
                }

                this.sendSuccessMessage("You've fix the level of '" + player.getName() + "' to " + count + ".");
                SocketManager.GAME_SEND_STATS_PACKET(player);
            } catch (Exception e) {
                this.sendMessage("You've send an invalid level.");
            }
            return;
        } else if (command.equalsIgnoreCase("KAMAS")) {
            int count = 0;
            try {
                count = Integer.parseInt(infos[1]);
            } catch (Exception e) {
                // ok
                this.sendMessage("Valeur incorecte.");
                return;
            }
            if (count == 0) {
                this.sendMessage("Valeur inutile.");
                return;
            }
            Player perso = this.getPlayer();
            if (infos.length == 3)//Si le nom du perso est specifie
            {
                String name = infos[2];
                perso = World.world.getPlayerByName(name);
                if (perso == null)
                    perso = this.getPlayer();
            }
            long curKamas = perso.getKamas();
            long newKamas = curKamas + count;
            if (newKamas < 0)
                newKamas = 0;
            if (newKamas > 1000000000)
                newKamas = 1000000000;
            perso.setKamas(newKamas);
            if (perso.isOnline())
                SocketManager.GAME_SEND_STATS_PACKET(perso);
            String mess = "Vous avez ";
            mess += (count < 0 ? "retire" : "ajoute") + " ";
            mess += Math.abs(count) + " kamas e " + perso.getName() + ".";
            this.sendMessage(mess);
            return;
        } else if (command.equalsIgnoreCase("ITEMSET")) {
            int tID = 0;
            try {
                tID = Integer.parseInt(infos[1]);
            } catch (Exception e) {
                // ok
            }

            ObjectSet IS = World.world.getItemSet(tID);
            if (tID == 0 || IS == null) {
                String mess = "La panoplie " + tID + " n'existe pas.";
                this.sendMessage(mess);
                return;
            }
            boolean useMax = false;
            if (infos.length == 3)
                useMax = infos[2].equals("MAX");//Si un jet est specifie

            for (ObjectTemplate t : IS.getItemTemplates()) {
                GameObject obj = t.createNewItem(1, useMax);
                if (this.getPlayer().addObjet(obj, true))//Si le joueur n'avait pas d'item similaire
                    World.world.addGameObject(obj);
            }
            String str = "Creation de la panoplie " + tID + " reussie";
            if (useMax)
                str += " avec des stats maximums";
            str += ".";
            this.sendMessage(str);
            return;
        } else if (command.equalsIgnoreCase("ITEM") || command.equalsIgnoreCase("!getitem")) {
            int tID = 0;
            try {
                tID = Integer.parseInt(infos[1]);
            } catch (Exception e) {
                // ok
            }

            if (tID == 0) {
                String mess = "Le template " + tID + " n'existe pas.";
                this.sendMessage(mess);
                return;
            }
            int qua = 1;
            if (infos.length == 3)//Si une quantite est specifiee
            {
                try {
                    qua = Integer.parseInt(infos[2]);
                } catch (Exception e) {
                    // ok
                }
            }
            boolean lier = infos.length == 5;
            boolean useMax = false;
            if (infos.length == 4)//Si un jet est specifie
            {
                if (infos[3].equalsIgnoreCase("MAX"))
                    useMax = true;
            }
            ObjectTemplate t = World.world.getObjTemplate(tID);
            if (t == null) {
                String mess = "Le template " + tID + " n'existe pas.";
                this.sendMessage(mess);
                return;
            }
            if (t.getType() == Constant.ITEM_TYPE_OBJET_ELEVAGE
                    && (t.getStrTemplate().isEmpty() || t.getStrTemplate().equalsIgnoreCase(""))) {
                this.sendMessage("Impossible de creer l'item d'elevage. Le StrTemplate ("
                        + tID + ") est vide.");
                return;
            }
            if (qua < 1)
                qua = 1;


            GameObject obj = t.createNewItem(qua, useMax);
            System.out.println("Ici ?" + obj);

            if(t.getType() == Constant.ITEM_TYPE_CERTIF_MONTURE) {
                //obj.setMountStats(this.getPlayer(), null);
                Mount mount = new Mount(Constant.getMountColorByParchoTemplate(obj.getTemplate().getId()), this.getPlayer().getId(), false);
                obj.clearStats();
                obj.getStats().addOneStat(995, (mount.getId()));
                obj.getTxtStat().put(996, this.getPlayer().getName());
                obj.getTxtStat().put(997, mount.getName());
                mount.setToMax();
            }
            System.out.println("la");
            if(lier) {
                Player player = World.world.getPlayerByName(infos[4]);
                obj.attachToPlayer(player);
                if (player.addObjet(obj, true))//Si le joueur n'avait pas d'item similaire
                    World.world.addGameObject(obj);
            } else {
                    if (this.getPlayer().addObjet(obj, true))//Si le joueur n'avait pas d'item similaire
                        World.world.addGameObject(obj);
            }
            System.out.println("et la");
            String str = "Creation de l'item " + tID + " reussie";
            if (useMax)
                str += " avec des stats maximums";
            str += ".";
            this.sendMessage(str);
            System.out.println("pui la");
            SocketManager.GAME_SEND_Ow_PACKET(this.getPlayer());
            return;
        } else if (command.equalsIgnoreCase("SPELLPOINT")) {
            int pts = -1;
            try {
                pts = Integer.parseInt(infos[1]);
            } catch (Exception e) {
                // ok
            }

            if (pts == -1) {
                String str = "Valeur invalide.";
                this.sendMessage(str);
                return;
            }
            Player perso = this.getPlayer();
            if (infos.length > 2)//Si un nom de perso est specifie
            {
                perso = World.world.getPlayerByName(infos[2]);
                if (perso == null) {
                    String str = "Le personnage n'a pas ete trouve.";
                    this.sendMessage(str);
                    return;
                }
            }
            perso.addSpellPoint(pts);
            SocketManager.GAME_SEND_STATS_PACKET(perso);
            String str = "Vous avez ajoute " + pts + " points de sorts e "
                    + perso.getName() + ".";
            this.sendMessage(str);
            return;
        } else if(command.equalsIgnoreCase("RELOAD")) {
            switch (infos.length > 0 ? infos[1].toUpperCase() : "") {
                case "HOUSES":
                    World.world.getHouses().clear();
                    DatabaseManager.get(BaseHouseData.class).loadFully();
                    DatabaseManager.get(HouseData.class).loadFully();
                    break;
                case "ENDFIGHTACTIONS":
                    DatabaseManager.get(EndFightActionData.class).loadFully();
                    break;
                case "QUESTS":
                    DatabaseManager.get(QuestData.class).loadFully();
                    DatabaseManager.get(QuestObjectiveData.class).loadFully();
                    DatabaseManager.get(QuestStepData.class).loadFully();
                    break;
                case "SHOP":
                    DatabaseManager.get(ShopObjectData.class).loadFully();
                    break;
                case "DROPS":
                    DatabaseManager.get(DropData.class).loadFully();
                    break;
                case "ITEMS":
                    DatabaseManager.get(ObjectTemplateData.class).loadFully();
                    DatabaseManager.get(ObjectActionData.class).loadFully();
                    break;
                case "NPCS":
                    DatabaseManager.get(NpcTemplateData.class).loadFully();
                    World.world.getNpcQuestions().clear();
                    DatabaseManager.get(NpcQuestionData.class).loadFully();
                    World.world.getNpcAnswers().clear();
                    DatabaseManager.get(NpcAnswerData.class).loadFully();
                    break;
                case "ADMIN":
                    Command.reload();
                    Group.reload();
                    Config.gameServer.getClients().stream().filter(client -> client != null && client.getPlayer() != null).forEach(client -> ((PlayerData) DatabaseManager.get(PlayerData.class)).reloadGroup(client.getPlayer()));
                    break;
                case "MAPS":
                    ((MapData) DatabaseManager.get(MapData.class)).reload();
                    break;
                case "MONSTERS":
                    DatabaseManager.get(MonsterData.class).loadFully();
                    break;
                case "SPELLS":
                    DatabaseManager.get(SpellData.class).loadFully();
                    break;
                default:
                    this.sendErrorMessage("Please enter a valid reload data :\nhouses, endfightactions, quests, drops, items, npcs, admin, monsters, spells, battle-type, battle-area");
                    return;
            }
            this.sendSuccessMessage("You've successfully reload the data type '" + infos[1] + "' !");
        } else if(command.equalsIgnoreCase("UTILITY")) {
            switch(infos[1].toUpperCase()) {

                case "CHECK-PA":
                    int pa = Integer.parseInt(infos[2]);
                    StringBuilder players = new StringBuilder();
                    for(Player player : World.world.getPlayers()) {
                        if(player.getStuffStats().getEffect(Constant.STATS_ADD_PA) > pa || player.getStuffStats().getEffect(Constant.STATS_ADD_PA2) > pa)
                            players.append(player.getName()).append("(").append(player.getStuffStats().getEffect(Constant.STATS_ADD_PA)).append("-").append(player.getStuffStats().getEffect(Constant.STATS_ADD_PA2)).append("), ");
                    }
                    this.sendSuccessMessage(players.toString());
                    break;
                case "SPEC":
                    Player player1 = World.world.getPlayerByName(infos[2]);
                    if(player1 != null) {
                        SocketManager.GAME_SEND_ASK(this.getClient(), player1);
                    } else {
                        this.sendErrorMessage("Invalid player name");
                    }
                    break;
                case "UP-STARS":
                    World.world.getMaps().stream().filter(map -> map != null && map.getMobGroups() != null)
                            .forEach(map -> {
                                map.getMobGroups().values().stream().filter(Objects::nonNull).forEach((m) -> m.setStarBonus((short) 150));
                                map.getFixMobGroups().values().stream().filter(Objects::nonNull).forEach((m) -> m.setStarBonus((short) 150));
                            });
                    break;
                case "COMPENSATIONS":
                    boolean ok = infos[2].equalsIgnoreCase("true");
                    int level = Integer.parseInt(infos[3]);
                    List<String> ips = new ArrayList<>();
                    List<Account> affected = new ArrayList<>();

                    for(Account account : World.world.getAccounts()) {
                        if(account == null || account.getLastConnectionDate() == null || account.getLastConnectionDate().isEmpty() || account.getPlayers().size() == 0) continue;

                        String[] date = account.getLastConnectionDate().split("~");//2018~05~31~18~16
                        boolean okk = false;
                        for(Player player : account.getPlayers().values())
                            if(player.getLevel() > level)
                                okk = true;

                        if(okk && (date[1].equals("05") || date[1].equals("06")) && Integer.parseInt(date[2]) >= 1 && !account.getLastIP().isEmpty() && !ips.contains(account.getLastIP())) {
                            if(ok)
                                account.addGift(26012, (short) 10, (byte) 1);
                            affected.add(account);
                            ips.add(account.getLastIP());
                        }
                    }
                    this.sendMessage(affected.size() + " accounts affected");
                    StringBuilder accounts = new StringBuilder();
                    for(Account account : affected)
                        accounts.append(account.getName()).append(", ");
                    this.sendMessage(accounts.toString());
                    break;
                case "EVENT-POS":
                    Event eee = EventManager.getInstance().getCurrentEvent();
                    if(eee instanceof EventFindMe) {
                        EventFindMe ee = (EventFindMe) eee;
                        this.sendSuccessMessage(ee.getMap().getId() + "," + ee.getCell().getId());
                    }
                    break;
                case "EVENT":
                    String name = infos[2];
                    Event event = null;
                    for(Event e : EventManager.getInstance().getEvents())
                        if(e.getEventName().equalsIgnoreCase(name))
                            event = e;
                    if(event != null) {
                        EventManager.getInstance().startNewEvent(event);
                        this.sendSuccessMessage("The event has started successfully.");
                    } else this.sendErrorMessage("The event with the name '" + name + "' was not found.");
                    break;
                case "ENCRYPTION":
                    Config.encryption = !Config.encryption;
                    this.sendSuccessMessage("The encryption is : " + Config.encryption);
                    break;
                case "MVM":
                    String group1 = infos[2], group2 = infos[3];
                    GameCase cell = this.getPlayer().getCurCell();
                    GameMap map = this.getPlayer().getCurMap();
                    MobGroup mg1 = map.spawnGroupOnCommand(cell.getId() - map.getW(), group1, true);
                    MobGroup mg2 = map.spawnGroupOnCommand(cell.getId() - map.getW() + 1, group2, true);
                    Map<Integer, MobGrade> hashMap = new HashMap<>();
                    int guid = -20;
                    for(MobGrade f : new ArrayList<>(mg2.getMobs().values())) {
                        hashMap.put(guid, f);
                        guid--;
                    }
                    mg2.setMobs(hashMap);
                    map.startFightMonsterVersusMonster(mg1, mg2);

                    break;
                case "FM":
                    float coef = Float.parseFloat(infos[2]);
                    JobAction.coefExo = coef;
                    this.sendSuccessMessage("Exotic coefficient set to " + coef);
                    break;
                case "LISTMAP":
                    map = this.getPlayer().getCurMap();
                    final StringBuilder list = new StringBuilder();
                    switch(infos[2].toUpperCase()) {
                        case "AREA":
                            this.sendSuccessMessage("List maps of area " + map.getArea().getId() + " (total: " + map.getArea().getMaps().size() + ") :");
                            if(map.getArea() != null) {
                                for(GameMap tmp : map.getArea().getMaps())
                                    list.append(tmp.getId()).append(",");
                                this.sendMessage(list.toString());
                            }
                            break;
                        case "SUBAREA":
                            this.sendSuccessMessage("List maps of sub-area " + map.getSubArea().getId() + " (total: " + map.getSubArea().getMaps().size() + ") :");
                            if(map.getSubArea() != null) {
                                for(GameMap tmp : map.getSubArea().getMaps())
                                    list.append(tmp.getId()).append(",");
                                this.sendMessage(list.toString());
                            }
                            break;
                        default:
                            this.sendErrorMessage("Invalid command, please give arguments [area|subarea].");
                            break;
                    }
                    break;
                case "COLLECTOR":
                    switch(infos[2].toUpperCase()) {
                        case "GET":
                            Collector collector = World.world.getCollector(Integer.parseInt(infos[3]));
                            if (collector == null || collector.getInFight() > 0 || collector.getExchange() || collector.getMap() != this.getPlayer().getCurMap().getId())
                                return;
                            collector.setExchange(true);
                            SocketManager.GAME_SEND_ECK_PACKET(this.getClient(), 8, collector.getId() + "");
                            SocketManager.GAME_SEND_ITEM_LIST_PACKET_PERCEPTEUR(this.getClient(), collector);
                            this.getPlayer().setExchangeAction(new ExchangeAction<>(ExchangeAction.TRADING_WITH_COLLECTOR, collector.getId()));
                            this.getPlayer().DialogTimer();
                            break;

                        default:
                            final StringBuilder message = new StringBuilder("All id of collectors present on the map " + this.getPlayer().getCurMap().getId() + " :<br>");
                            World.world.getCollectors().values().stream().filter(collector1 -> collector1.getMap() == this.getPlayer().getCurMap().getId()).forEach(collector1 ->
                                    message.append("> ").append(collector1.getId()).append(" | ").append(collector1.getDate()).append(" | ")
                                            .append(World.world.getGuild(collector1.getGuildId()) != null ? World.world.getGuild(collector1.getGuildId()).getName() : "Unknow").append("<br>"));
                            this.sendMessage(message.toString());
                            break;
                    }
                    break;
                case "RECEIVE":
                    try {
                        this.getPlayer().getGameClient().parsePacket(infos[2]);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        this.sendErrorMessage("You've fail the structure of the command. Please retry.");
                        break;
                    }
                    this.sendSuccessMessage("You send to server this packet : " + infos[2]);
                    break;
                case "DEBUG":
                    int position = Integer.parseInt(infos[2]);
                    GameObject object = this.getPlayer().getObjetByPos(position);
                    this.sendSuccessMessage("The id of position " + position + " is " + object.getGuid());
                    break;
            }
        } else  if(command.equalsIgnoreCase("MAPSTATE") || command.equalsIgnoreCase("STATE")) {
            String name = "";
            byte value = -1;

            if(infos.length > 1) {
                name = infos[1].toUpperCase();
            }
            if(infos.length > 2) {
                value = (byte) (infos[2].equalsIgnoreCase("true") || infos[2].equalsIgnoreCase("1") ? 1 : 0);
            }

            final GameMap map = this.getPlayer().getCurMap();
            switch(name) {
                case "SELLER":
                    map.noSellers = value == -1 ? !map.noSellers : value == 1;
                    if(map.noSellers)
                        this.sendSuccessMessage("You've enabled sellers on the map (" + map.getId() + ").");
                    else
                        this.sendSuccessMessage("You've disabled sellers on the map (" + map.getId() + ").");
                    break;
                case "COLLECTOR":
                    map.noCollectors = value == -1 ? !map.noCollectors : value == 1;
                    if(map.noCollectors)
                        this.sendSuccessMessage("You've enabled collectors on the map (" + map.getId() + ").");
                    else
                        this.sendSuccessMessage("You've disabled collectors on the map (" + map.getId() + ").");
                    break;
                case "PRISM":
                    map.noPrisms = value == -1 ? !map.noPrisms : value == 1;
                    if(map.noPrisms)
                        this.sendSuccessMessage("You've enabled prisms on the map (" + map.getId() + ").");
                    else
                        this.sendSuccessMessage("You've disabled prisms on the map (" + map.getId() + ").");

                    break;
                case "TP":
                    map.noTp = value == -1 ? !map.noTp : value == 1;
                    if(map.noTp)
                        this.sendSuccessMessage("You've enabled sellers on the map (" + map.getId() + ").");
                    else
                        this.sendSuccessMessage("You've disabled sellers on the map (" + map.getId() + ").");

                    break;
                case "DEFY":
                    map.noDefy = value == -1 ? !map.noDefy : value == 1;
                    if(map.noDefy)
                        this.sendSuccessMessage("You've enabled defies on the map (" + map.getId() + ").");
                    else
                        this.sendSuccessMessage("You've disabled defies on the map (" + map.getId() + ").");
                    break;

                case "AGGRESSION":
                    map.noAgro = value == -1 ? !map.noAgro : value == 1;
                    if(map.noAgro)
                        this.sendSuccessMessage("You've enabled aggressions on the map (" + map.getId() + ").");
                    else
                        this.sendSuccessMessage("You've disabled aggressions on the map (" + map.getId() + ").");

                    break;
                case "CHANNEL":
                    map.noCanal = value == -1 ? !map.noCanal : value == 1;
                    if(map.noCanal)
                        this.sendSuccessMessage("You've enabled the channel on the map (" + map.getId() + ").");
                    else
                        this.sendSuccessMessage("You've disabled the channel on the map (" + map.getId() + ").");
                    break;
                default:
                    this.sendErrorMessage("You've specified an invalid state, known types :\n- DUBG\n- SURVIVAL \n- DUNGEONS\n- SELLER\n- COLLECTOR\n- PRISM\n- TP\n- DEFY\n- AGGRESSION\n- CHANNEL\nYou can add a second argument to specify exactly the value 'true' or 'false'.\nExample : MAPSTATE DEFY TRUE");
                    break;
            }
            ((MapData)DatabaseManager.get(MapData.class)).update(map);
        } else if (command.equalsIgnoreCase("LSPELL")) {
            int spell = -1;
            try {
                spell = Integer.parseInt(infos[1]);
            } catch (Exception e) {
                // ok
            }

            if (spell == -1) {
                String str = "Valeur invalide.";
                this.sendMessage(str);
                return;
            }
            Player perso = this.getPlayer();
            if (infos.length > 2)//Si un nom de perso est specifie
            {
                perso = World.world.getPlayerByName(infos[2]);
                if (perso == null) {
                    String str = "Le personnage n'a pas ete trouve.";
                    this.sendMessage(str);
                    return;
                }
            }
            perso.learnSpell(spell, 1, true, true, true);
            String str = "Le sort " + spell + " a ete appris e "
                    + perso.getName() + ".";
            this.sendMessage(str);
            return;
        } else if (command.equalsIgnoreCase("CAPITAL")) {
            int pts = -1;
            try {
                pts = Integer.parseInt(infos[1]);
            } catch (Exception e) {
                // ok
            }

            if (pts == -1) {
                String str = "Valeur invalide.";
                this.sendMessage(str);
                return;
            }
            Player perso = this.getPlayer();
            if (infos.length > 2)//Si un nom de perso est specifie
            {
                perso = World.world.getPlayerByName(infos[2]);
                if (perso == null) {
                    String str = "Le personnage n'a pas ete trouve.";
                    this.sendMessage(str);
                    return;
                }
            }
            perso.addCapital(pts);
            SocketManager.GAME_SEND_STATS_PACKET(perso);
            String str = "Vous avez ajoute " + pts + " points de capital e "
                    + perso.getName() + ".";
            this.sendMessage(str);
            return;
        } else if (command.equalsIgnoreCase("ALIGN")) {
            byte align = -1;
            try {
                align = Byte.parseByte(infos[1]);
            } catch (Exception e) {
                // ok
            }

            if (align < Constant.ALIGNEMENT_NEUTRE
                    || align > Constant.ALIGNEMENT_MERCENAIRE) {
                String str = "Valeur invalide.";
                this.sendMessage(str);
                return;
            }
            Player perso = this.getPlayer();
            if (infos.length > 2)//Si un nom de perso est specifie
            {
                perso = World.world.getPlayerByName(infos[2]);
                if (perso == null) {
                    String str = "Le personnage n'a pas ete trouve.";
                    this.sendMessage(str);
                    return;
                }
            }
            perso.modifAlignement(align);
            String a = "";
            if (align == 0)
                a = "neutre";
            else if (align == 1)
                a = "bontarien";
            else if (align == 2)
                a = "brakmarien";
            else if (align == 3)
                a = "serianne";
            String str = "L'alignement du joueur a ete modifie en " + a + ".";
            this.sendMessage(str);
            return;
        } else if (command.equalsIgnoreCase("LIFE")) {
            int count = 0;
            try {
                count = Integer.parseInt(infos[1]);
                if (count < 0)
                    count = 0;
                if (count > 100)
                    count = 100;
                Player perso = this.getPlayer();
                if (infos.length == 3)//Si le nom du perso est specifie
                {
                    String name = infos[2];
                    perso = World.world.getPlayerByName(name);
                    if (perso == null)
                        perso = this.getPlayer();
                }
                int newPDV = perso.getMaxPdv() * count / 100;
                perso.setPdv(newPDV);
                if (perso.isOnline())
                    SocketManager.GAME_SEND_STATS_PACKET(perso);
                String mess = "Vous avez fixe le pourcentage de vitalite de "
                        + perso.getName() + " e " + count + "%.";
                this.sendMessage(mess);
            } catch (Exception e) {
                // ok
                this.sendMessage("Valeur incorecte.");
                return;
            }
            return;
        } else if (command.equalsIgnoreCase("XPJOB")) {
            int job = -1;
            int xp = -1;
            try {
                job = Integer.parseInt(infos[1]);
                xp = Integer.parseInt(infos[2]);
            } catch (Exception e) {
                // ok
            }

            if (job == -1 || xp < 0) {
                String str = "Valeurs invalides.";
                this.sendMessage(str);
                return;
            }
            Player perso = this.getPlayer();
            if (infos.length > 3)//Si un nom de perso est specifie
            {
                perso = World.world.getPlayerByName(infos[3]);
                if (perso == null) {
                    String str = "Le personnage n'a pas ete trouv.e";
                    this.sendMessage(str);
                    return;
                }
            }
            JobStat SM = perso.getMetierByID(job);
            if (SM == null) {
                String str = "Le joueur ne possede pas le metier demande.";
                this.sendMessage(str);
                return;
            }
            SM.addXp(perso, xp);
            ArrayList<JobStat> SMs = new ArrayList<JobStat>();
            SMs.add(SM);
            SocketManager.GAME_SEND_JX_PACKET(perso, SMs);
            String str = "Vous avez ajoute " + xp
                    + " points d'experience au metier " + job + " de "
                    + perso.getName() + ".";
            this.sendMessage(str);
            return;
        } else if (command.equalsIgnoreCase("LJOB")) {
            int job = -1;
            try {
                job = Integer.parseInt(infos[1]);
            } catch (Exception e) {
                // ok
            }

            if (job == -1 || World.world.getMetier(job) == null) {
                String str = "Valeur invalide.";
                this.sendMessage(str);
                return;
            }
            Player perso = this.getPlayer();
            if (infos.length > 2)//Si un nom de perso est specifie
            {
                perso = World.world.getPlayerByName(infos[2]);
                if (perso == null) {
                    String str = "Le personnage n'a pas ete trouve.";
                    this.sendMessage(str);
                    return;
                }
            }
            perso.learnJob(World.world.getMetier(job));
            String str = "Le metier " + job + " a ete appris e "
                    + perso.getName() + ".";
            this.sendMessage(str);
            return;
        } else if (command.equalsIgnoreCase("UNLSPELL")) {
            Player perso = this.getPlayer();
            if (infos.length > 2)//Si un nom de perso est specifie
            {
                perso = World.world.getPlayerByName(infos[2]);
                if (perso == null) {
                    String str = "Le personnage n'a pas ete trouve.";
                    this.sendMessage(str);
                    return;
                }
            }
            perso.setExchangeAction(new ExchangeAction<>(ExchangeAction.FORGETTING_SPELL, 0));
            SocketManager.GAME_SEND_FORGETSPELL_INTERFACE('+', perso);
            return;
        } else if (command.equalsIgnoreCase("SPAWN")) {
            String Mob = null;
            try {
                Mob = infos[1];
            } catch (Exception e) {
                // ok
            }

            if (Mob == null) {
                this.sendMessage("Les parametres sont invalides.");
                return;
            }
            this.getPlayer().getCurMap().spawnGroupOnCommand(this.getPlayer().getCurCell().getId(), Mob, true);
            this.sendMessage("Vous avez ajoute un groupe de monstres.");
            return;
        } else if (command.equalsIgnoreCase("SHUTDOWN")) {
            int time = 30, OffOn = 0;
            try {
                OffOn = Integer.parseInt(infos[1]);
                time = Integer.parseInt(infos[2]);
            } catch (Exception e) {
                // ok
            }

            if (OffOn == 1 && this.isTimerStart())// demande de demarer le reboot
            {
                this.sendMessage("Un reboot est déjà programmé.");
            } else if (OffOn == 1 && !this.isTimerStart()) {
                if (time <= 15) {
                    for(Player player : World.world.getOnlinePlayers()) {
                        player.sendServerMessage(player.getLang().trans("command.commandadmin.fightblock1"));
                        player.send("M13");
                    }
                    Main.fightAsBlocked = true;
                }
                this.setTimer(createTimer(time));
                this.getTimer().start();
                this.setTimerStart(true);
                String timeMSG = "minutes";
                if (time <= 1)
                    timeMSG = "minute";
                SocketManager.GAME_SEND_Im_PACKET_TO_ALL("115;" + time + " " + timeMSG);
                this.sendMessage("Reboot programmé.");
            } else if (OffOn == 0 && this.isTimerStart()) {
                this.getTimer().stop();
                this.setTimerStart(false);
                for(Player player : World.world.getOnlinePlayers())
                    player.sendServerMessage(player.getLang().trans("command.commandadmin.fightblock0"));
                Main.fightAsBlocked = true;
                this.sendMessage("Reboot arrêté.");
            } else if (OffOn == 0 && !this.isTimerStart()) {
                this.sendMessage("Aucun reboot n'est lancé.");
            }
            return;
        } else if (command.equalsIgnoreCase("LINEM")) {
            String line = "|";
            for(String split : infos[1].split(",")) {
                int id = Integer.parseInt(split);
                Monster monster = World.world.getMonstre(id);


                for (MobGrade mobGrade : monster.getGrades().values())
                    line += monster.getId() + "," + mobGrade.getLevel() + "|";
            }
            this.sendMessage(line);
            return;
        } else if (command.equalsIgnoreCase("ENERGIE")) {
            try {
                Player perso = this.getPlayer();
                String name = null;
                name = infos[2];
                perso = World.world.getPlayerByName(name);
                int jet = Integer.parseInt(infos[1]);
                int EnergyTotal = perso.getEnergy() + jet;
                if (EnergyTotal > 10000)
                    EnergyTotal = 10000;
                perso.setEnergy(EnergyTotal);
                SocketManager.GAME_SEND_STATS_PACKET(perso);
                this.sendMessage("Vous avez fixe l'energie de "
                        + perso.getName() + " e " + EnergyTotal + ".");
                return;
            } catch (Exception e) {

            }
            return;
        } else if (command.equalsIgnoreCase("RES")) {
            Player player = this.getPlayer();

            if(infos.length > 1) {
                player = World.world.getPlayerByName(infos[1]);
            }
            if (player == null) {
                this.sendErrorMessage("The player you've specified was not found.");
                return;
            }
            if (player.getFight() != null) {
                this.sendErrorMessage("The player's currently in fight.");
                return;
            }

            if (player.isOnline()) {
                if(player.isDead() == 1) {
                    player.setGhost(false);
                    player.setDead((byte) 0);
                    player.setEnergy(1000);
                    player.setGfxId(Integer.parseInt(player.getClasse() + "" + player.getSexe()));
                    player.setCanAggro(true);
                    player.setAway(false);
                    player.setSpeed(0);
                    SocketManager.GAME_SEND_ALTER_GM_PACKET(player.getCurMap(), player);
                } else if(player.isGhost()) {
                    player.setAlive();
                    player.setPdv(player.getMaxPdv());
                }
                this.sendMessage("The player '" + player.getName() + "' has been revived successfully.");
            } else {
                this.sendMessage("The player's not connected.");
            }
            return;
        } else if (command.equalsIgnoreCase("KICKALL")) {
            this.sendMessage("Tout le monde va etre kicke.");
            Config.gameServer.kickAll(true);
            return;
        } else if (command.equalsIgnoreCase("RESET")) {
            Player perso = this.getPlayer();
            if (infos.length > 1) {
                perso = World.world.getPlayerByName(infos[1]);
            }
            if (perso == null) {
                String mess = "Le personnage n'existe pas.";
                this.sendMessage(mess);
                return;
            }
            perso.getStats().addOneStat(125, -perso.getStats().getEffect(125));
            perso.getStats().addOneStat(124, -perso.getStats().getEffect(124));
            perso.getStats().addOneStat(118, -perso.getStats().getEffect(118));
            perso.getStats().addOneStat(123, -perso.getStats().getEffect(123));
            perso.getStats().addOneStat(119, -perso.getStats().getEffect(119));
            perso.getStats().addOneStat(126, -perso.getStats().getEffect(126));
            perso.getStatsParcho().getEffects().clear();
            perso.addCapital((perso.getLevel() - 1) * 5 - perso.getCapital());
            SocketManager.GAME_SEND_STATS_PACKET(perso);
            this.sendMessage("Vous avez restat "
                    + perso.getName() + ".");
            return;
        }else if (command.equalsIgnoreCase("RESETALL")) {
            for (Player perso: World.world.getPlayers()) {
                perso.getStats().addOneStat(125, -perso.getStats().getEffect(125));
                perso.getStats().addOneStat(124, -perso.getStats().getEffect(124));
                perso.getStats().addOneStat(118, -perso.getStats().getEffect(118));
                perso.getStats().addOneStat(123, -perso.getStats().getEffect(123));
                perso.getStats().addOneStat(119, -perso.getStats().getEffect(119));
                perso.getStats().addOneStat(126, -perso.getStats().getEffect(126));
                perso.getStatsParcho().getEffects().clear();
                perso.addCapital((perso.getLevel() - 1) * 5 - perso.getCapital());
                SocketManager.GAME_SEND_STATS_PACKET(perso);

            }
            this.sendMessage("Vous avez restat tout le monde");
            return;
        } else if (command.equalsIgnoreCase("RENAMEPERSO")) {
            Player perso = this.getPlayer();
            if (infos.length > 1)
                perso = World.world.getPlayerByName(infos[1]);
            if (perso == null) {
                String mess = "Le personnage n'existe pas.";
                this.sendMessage(mess);
                return;
            }
            if(World.world.getPlayerByName(infos[2]) != null) {
                String mess = "Le personnage " + infos[2] + " existe déjà.";
                this.sendMessage(mess);
                return;
            }
            String name = perso.getName();
            perso.setName(infos[2]);
            ((PlayerData) DatabaseManager.get(PlayerData.class)).update(perso);
            SocketManager.GAME_SEND_STATS_PACKET(perso);
            SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(perso.getCurMap(), perso.getId());
            SocketManager.GAME_SEND_ADD_PLAYER_TO_MAP(perso.getCurMap(), perso);
            this.sendMessage("Vous avez renomme "
                    + name + " en " + perso.getName() + ".");
            return;
        } else if (command.equalsIgnoreCase("RENAMEGUILDE")) {
            String ancName = "";
            String newName = "";
            int idGuild = -1;
            if (infos.length > 1)
                ancName = infos[1];
            newName = infos[2];
            idGuild = World.world.getGuildByName(ancName);
            if (idGuild == -1) {
                String mess = "La guilde n'existe pas.";
                this.sendMessage(mess);
                return;
            }
            World.world.getGuild(idGuild).setName(newName);
            this.sendMessage("Vous avez renomme la guilde en "
                    + newName + ".");
            return;
        } else if (command.equalsIgnoreCase("A")) {
            infos = msg.split(" ", 2);
            String prefix = "<b>Server</b>";
            SocketManager.GAME_SEND_Im_PACKET_TO_ALL("116;" + prefix + "~"
                    + infos[1]);
            this.sendMessage("Vous avez envoye un message e tout le serveur.");
            return;
        } else if (command.equalsIgnoreCase("MOVEMOB")) {
            this.getPlayer().getCurMap().onMapMonsterDeplacement();
            this.sendMessage("Vous avez deplace un groupe de monstres.");
            return;
        } else if (command.equalsIgnoreCase("ALLGIFTS")) {
            int template = -1, quantity = 0, jp = 0;

            try {
                template = Integer.parseInt(infos[1]);
                quantity = Integer.parseInt(infos[2]);
                jp = Integer.parseInt(infos[3]);
            } catch (Exception e) {
                // ok
                this.sendMessage("Parametre incorrect : ALLGIFTS [templateid] [quantity] [jp= 1 ou 0]");
                return;
            }

            String gift = template + "," + quantity + "," + jp;

            for (Account account : World.world.getAccounts()) {
                String gifts = ((GiftData) DatabaseManager.get(GiftData.class)).load(account.getId()).getValue();
                if (gifts.isEmpty()) {
                    ((GiftData) DatabaseManager.get(GiftData.class)).update(new Pair<>(account, gift));
                } else {
                    ((GiftData) DatabaseManager.get(GiftData.class)).update(new Pair<>(account, gifts + ";" + gift));
                }
            }
            this.sendMessage(World.world.getAccounts().size()
                    + " ont reeu le cadeau : " + gift + ".");
            return;
        } else if (command.equalsIgnoreCase("GIFTS")) {
            String name = "";
            int template = -1, quantity = 0, jp = 0;

            try {
                name = infos[1];
                template = Integer.parseInt(infos[2]);
                quantity = Integer.parseInt(infos[3]);
                jp = Integer.parseInt(infos[4]);
            } catch (Exception e) {
                // ok
                this.sendMessage("Parametre incorrect : GIFTS [account] [templateid] [quantity] [jp= 1 ou 0]");
                return;
            }

            Player player = World.world.getPlayerByName(name);

            if (player == null) {
                this.sendMessage("Personnage inexistant.");
                return;
            }

            player.getAccount().addGift(template, (short) quantity, (byte) jp);
            this.sendMessage(name
                    + " a reeu le cadeau : " + template + ".");
            return;
        } else if (command.equalsIgnoreCase("SHOWPOINTS")) {
            Player perso = this.getPlayer();
            if (infos.length > 1)
                perso = World.world.getPlayerByName(infos[1]);
            if (perso == null) {
                String mess = "Le personnage n'existe pas.";
                this.sendMessage(mess);
                return;
            }
            this.sendMessage(perso.getName()
                    + " possede "
                    + perso.getAccount().getPoints()
                    + " points boutique.");
            return;
        } else if (command.equalsIgnoreCase("ADDNPC")) {
            int id = 0;
            try {
                id = Integer.parseInt(infos[1]);
            } catch (Exception e) {
                // ok
            }

            if (id == 0 || World.world.getNPCTemplate(id) == null) {
                String str = "NpcID invalide.";
                this.sendMessage(str);
                return;
            }
            Npc npc = this.getPlayer().getCurMap().addNpc(id, this.getPlayer().getCurCell().getId(), this.getPlayer().get_orientation());
            SocketManager.GAME_SEND_ADD_NPC_TO_MAP(this.getPlayer().getCurMap(), npc);
            String str = "Le PNJ a ete ajoute";
            if (this.getPlayer().get_orientation() == 0
                    || this.getPlayer().get_orientation() == 2
                    || this.getPlayer().get_orientation() == 4
                    || this.getPlayer().get_orientation() == 6)
                str += " mais est invisible (orientation diagonale invalide)";
            str += ".";
            if (((NpcData) DatabaseManager.get(NpcData.class)).insert(new Pair<>(npc, (int) this.getPlayer().getCurMap().getId())))
                this.sendMessage(str);
            else
                this.sendMessage("Erreur lors de la sauvegarde de la position.");
            return;
        } else if (command.equalsIgnoreCase("DELNPC")) {
            int id = 0;
            try {
                id = Integer.parseInt(infos[1]);
            } catch (Exception e) {
                // ok
            }

            Npc npc = this.getPlayer().getCurMap().getNpc(id);
            if (id == 0 || npc == null) {
                String str = "Npc GUID invalide.";
                this.sendMessage(str);
                return;
            }
            int exC = npc.getCellId();
            //on l'efface de la map
            SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(this.getPlayer().getCurMap(), id);
            this.getPlayer().getCurMap().removeNpcOrMobGroup(id);

            String str = "Le PNJ a ete supprime.";
            ((NpcData) DatabaseManager.get(NpcData.class)).delete(new Pair<>(npc, (int) this.getPlayer().getCurMap().getId()));
            this.sendMessage(str);
            return;
        } else if (command.equalsIgnoreCase("SETSTATS")) {
            int obj = -1;
            String stats = "";
            try {
                obj = Integer.parseInt(infos[1]);
                stats = infos[2];
            } catch (Exception e) {
                // ok
            }
            if (obj == -1 || stats.equals("")) {
                this.sendMessage("Les parametres sont invalides.");
                return;
            }
            GameObject object = World.world.getGameObject(obj);
            if (object == null) {
                this.sendMessage("L'objet n'existe pas.");
                return;
            }
            if (stats.equals("-1")) {
                object.clearStats();
                SocketManager.GAME_SEND_UPDATE_ITEM(this.getPlayer(), object);
            } else {
                object.refreshStatsObjet(stats);
                SocketManager.GAME_SEND_UPDATE_ITEM(this.getPlayer(), object);
            }
            this.sendMessage("L'objet a ete modifie avec succes.");
            return;
        } else if (command.equalsIgnoreCase("ADDCELLPARK")) {
            if (this.getPlayer().getCurMap().getMountPark() == null) {
                this.sendMessage("Pas d'enclos sur votre map.");
                return;
            }
            this.getPlayer().getCurMap().getMountPark().addCellObject(this.getPlayer().getCurCell().getId());
            ((BaseMountParkData) DatabaseManager.get(BaseMountParkData.class)).update(this.getPlayer().getCurMap().getMountPark());
            this.sendMessage("Vous avez ajoute la cellule e l'enclos.");
            return;
        } else if (command.equalsIgnoreCase("O")) {
            MountPark mp = this.getPlayer().getCurMap().getMountPark();

            for (GameCase c : this.getPlayer().getCurMap().getCases()) {
                if (c.getObject() != null) {
                    switch (c.getObject().getTemplate().getId()) {
                        case 6766:
                        case 6767:
                        case 6763:
                        case 6772:
                            mp.setDoor(c.getId());
                            this.sendMessage("Vous avez ajoute une porte e l'enclos.");
                            return;
                    }
                }
            }
            this.sendMessage("Vous ne vous situez pas sur la porte.");
        } else if (command.equalsIgnoreCase("A1")) {
            this.getPlayer().getCurMap().getMountPark().setMountCell(this.getPlayer().getCurCell().getId());
            this.sendMessage("Vous avez modifie la cellule de spawn de l'enclos.");
        } else if (command.equalsIgnoreCase("B1")) {
            this.getPlayer().getCases = true;
            this.sendMessage("Vous avez active le getCases.");
        } else if (command.equalsIgnoreCase("C1")) {
            this.getPlayer().getCases = false;
            this.getPlayer().getCurMap().getMountPark().setCellObject(this.getPlayer().thisCases);
            this.getPlayer().thisCases.clear();
            ((BaseMountParkData) DatabaseManager.get(BaseMountParkData.class)).update(this.getPlayer().getCurMap().getMountPark());
            this.sendMessage("Vous avez applique les nouvelles cases e l'enclos.");
        } else if (command.equalsIgnoreCase("CONVERT")) {
            try {
                this.sendMessage(Long.toHexString(Long.parseLong(infos[1])));
                this.sendMessage(Long.parseLong(infos[1], 16) + "");
            } catch (Exception e) {
                this.sendMessage(Long.parseLong(infos[1], 16) + "");
            }
            return;
        } else if (command.equalsIgnoreCase("LISTTYPE")) {
            String s = "";
            for (ObjectTemplate obj : World.world.getObjTemplates())
                if (obj.getType() == Integer.parseInt(infos[1]))
                    s += obj.getId() + ",";
            this.sendMessage(s);
            return;
        } else if (command.equalsIgnoreCase("EMOTE")) {
            Player perso = this.getPlayer();
            byte emoteId = 0;
            try {
                emoteId = Byte.parseByte(infos[1]);
                perso = World.world.getPlayerByName(infos[2]);
            } catch (Exception e) {
                // ok
            }
            if (perso == null)
                perso = this.getPlayer();
            this.getPlayer().addStaticEmote(emoteId);
            this.sendMessage("L'emote "
                    + emoteId
                    + " a ete ajoute au joueur "
                    + perso.getName()
                    + ".");
            return;
        } else if (command.equalsIgnoreCase("DELNPCITEM")) {
            int npcGUID = 0;
            int itmID = -1;
            try {
                npcGUID = Integer.parseInt(infos[1]);
                itmID = Integer.parseInt(infos[2]);
            } catch (Exception e) {
                // ok
            }

            GameMap map = this.getPlayer().getCurMap();
            Npc npc = map.getNpc(npcGUID);
            NpcTemplate npcTemplate = null;
            if (npc == null)
                npcTemplate = World.world.getNPCTemplate(npcGUID);
            else
                npcTemplate = npc.getTemplate();
            if (npcGUID == 0 || itmID == -1 || npcTemplate == null) {
                String str = "NpcGUID ou itemID invalide.";
                this.sendMessage(str);
                return;
            }
            String str = "";
            if (npcTemplate.removeItemVendor(itmID))
                str = "L'objet a ete retire.";
            else
                str = "L'objet n'a pas ete retire.";
            this.sendMessage(str);
            ((NpcTemplateData) DatabaseManager.get(NpcTemplateData.class)).update(npcTemplate);
            return;
        } else if (command.equalsIgnoreCase("ADDNPCITEM")) {
            int npcGUID = 0;
            int itmID = -1;
            try {
                npcGUID = Integer.parseInt(infos[1]);
                itmID = Integer.parseInt(infos[2]);
            } catch (Exception e) {
                // ok
            }

            GameMap map = this.getPlayer().getCurMap();
            Npc npc = map.getNpc(npcGUID);
            NpcTemplate npcTemplate = null;
            if (npc == null)
                npcTemplate = World.world.getNPCTemplate(npcGUID);
            else
                npcTemplate = npc.getTemplate();
            ObjectTemplate item = World.world.getObjTemplate(itmID);
            if (npcGUID == 0 || itmID == -1 || npcTemplate == null
                    || item == null) {
                String str = "NpcGUID ou itemID invalide.";
                this.sendMessage(str);
                return;
            }
            String str = "";
            if (npcTemplate.addItemVendor(item))
                str = "L'objet a ete rajoute.";
            else
                str = "L'objet n'a pas ete rajoute.";
            this.sendMessage(str);
            ((NpcTemplateData) DatabaseManager.get(NpcTemplateData.class)).update(npcTemplate);
            return;
        } else if (command.equalsIgnoreCase("LISTEXTRA")) {
            String mess = "Liste des Extra Monstres :";
            for (Entry<Integer, GameMap> i : World.world.getExtraMonsterOnMap().entrySet())
                mess += "\n- " + i.getKey() + " est sur la map : "
                        + i.getValue().getId();
            if (World.world.getExtraMonsterOnMap().size() <= 0)
                mess = "Aucun Extra Monstres existe.";
            this.sendMessage(mess);
            return;
        } else if (command.equalsIgnoreCase("CREATEGUILD")) {
            Player perso = this.getPlayer();
            if (infos.length > 1) {
                perso = World.world.getPlayerByName(infos[1]);
            }
            if (perso == null) {
                String mess = "Le personnage n'existe pas.";
                this.sendMessage(mess);
                return;
            }

            if (!perso.isOnline()) {
                String mess = "Le personnage " + perso.getName()
                        + " n'est pas connecte.";
                this.sendMessage(mess);
                return;
            }
            if (perso.getGuild() != null || perso.getGuildMember() != null) {
                String mess = "Le personnage " + perso.getName()
                        + " possede deje une guilde.";
                this.sendMessage(mess);
                return;
            }
            SocketManager.GAME_SEND_gn_PACKET(perso);
            String mess = perso.getName()
                    + ": Panneau de creation de guilde ouvert.";
            this.sendMessage(mess);
            return;
        } else if (command.equalsIgnoreCase("SEND")) {
            SocketManager.send(this.getClient(), msg.substring(5));
            this.sendMessage("Le paquet a ete envoye : "
                    + msg.substring(5));
            return;
        } else if (command.equalsIgnoreCase("SENDTOMAP")) {
            SocketManager.sendPacketToMap(this.getPlayer().getCurMap(), infos[1]);
            this.sendMessage("Le paquet a ete envoye : "
                    + msg.substring(10));
            return;
        } else if (command.equalsIgnoreCase("SENDTO")) {
            Player perso = null;
            try {
                perso = World.world.getPlayerByName(infos[1]);
            } catch (Exception e) {
                // ok
            }
            if (perso == null) {
                this.sendMessage("Le nom du personnage est incorrect.");
                return;
            }
            SocketManager.send(World.world.getPlayerByName(infos[1]), msg.substring(8 + infos[1].length()));
            this.sendMessage("Le paquet a ete envoye : "
                    + msg.substring(8 + infos[1].length()) + " e " + infos[1] + ".");
            return;
        } else if (command.equalsIgnoreCase("TITRE")) {
            Player perso = this.getPlayer();
            byte TitleID = 0;
            try {
                TitleID = Byte.parseByte(infos[1]);
                perso = World.world.getPlayerByName(infos[2]);
            } catch (Exception e) {
                // ok
            }

            if (perso == null) {
                perso = this.getPlayer();
            }

            perso.setCurrentTitle(TitleID);
            this.sendMessage("Vous avez modifie le titre de "
                    + perso.getName() + ".");
            ((PlayerData) DatabaseManager.get(PlayerData.class)).update(perso);
            if (perso.getFight() == null)
                SocketManager.GAME_SEND_ALTER_GM_PACKET(perso.getCurMap(), perso);
            return;
        } else if (command.equalsIgnoreCase("POINTS")) {
            int count = 0;
            try {
                count = Integer.parseInt(infos[1]);
            } catch (Exception e) {
                // ok
                this.sendMessage("Valeur incorrecte.");
                return;
            }
            if (count == 0) {
                this.sendMessage("Valeur inutile.");
                return;
            }
            Player perso = this.getPlayer();
            if (infos.length == 3)//Si le nom du perso est specifie
            {
                String name = infos[2];
                perso = World.world.getPlayerByName(name);
                if (perso == null)
                    perso = this.getPlayer();
            }
            int pointtotal = perso.getAccount().getPoints() + count;
            if (pointtotal < 0)
                pointtotal = 0;
            if (pointtotal > 50000)
                pointtotal = 50000;
            perso.getAccount().setPoints(pointtotal);
            if (perso.isOnline())
                SocketManager.GAME_SEND_STATS_PACKET(perso);
            String mess = "Vous venez de donner " + count
                    + " points boutique e " + perso.getName() + ".";
            this.sendMessage(mess);
            return;
        } else if (command.equalsIgnoreCase("ITEMTYPE")) {
            String type = "";
            try {
                type = infos[1];
            } catch (Exception e) {
                // ok
            }
            String data = "";
            int count = 0;
            for (ObjectTemplate obj : World.world.getObjTemplates()) {
                if (type.contains(String.valueOf(obj.getType()) + ",") && obj.getLevel() >= 2 && obj.getLevel() <= 50 && obj.isAnEquipment(false, Arrays.asList(Constant.ITEM_TYPE_DOFUS, Constant.ITEM_TYPE_FAMILIER))) {
                    /*GameObject addObj = obj.createNewItem(1, true);
                    if (this.getPlayer().addObjet(addObj, true))//Si le joueur n'avait pas d'item similaire
                        World.world.addGameObject(addObj);*/
                    data += "," + obj.getId();
                    count++;
                }
            }
            this.sendSuccessMessage(data);
            this.sendMessage("Vous avez tous les objets de type "
                    + type + " dans votre inventaire." + count);
            return;
        } else if (command.equalsIgnoreCase("FULLMORPH")) {
            this.getPlayer().setFullMorph(Integer.parseInt(infos[1]), false, false);
            this.sendMessage("Vous avez ete transforme en crocoburio.");
            return;
        } else if (command.equalsIgnoreCase("UNFULLMORPH")) {
            String pseudo = "";
            try {
                pseudo = infos[1];
            } catch (Exception e) {
                // ok
            }
            Player p = World.world.getPlayerByName(pseudo);
            if (p == null)
                p = this.getPlayer();
            p.unsetFullMorph();
            this.sendMessage("Vous avez transforme dans la forme originale "
                    + p.getName() + ".");
            return;
        } else if (command.equalsIgnoreCase("PETSRES")) {
            int objID = 1;
            try {
                objID = Integer.parseInt(infos[1]);
            } catch (Exception e) {
                // ok
            }

            PetEntry p = World.world.getPetsEntry(objID);
            if (p == null) {
                this.sendMessage("Le familier n'existe pas.");
                return;
            }
            p.resurrection();
            this.sendMessage("Vous avez ressuscite le familier.");
            SocketManager.GAME_SEND_UPDATE_OBJECT_DISPLAY_PACKET(this.getPlayer(), World.world.getGameObject(objID));
            return;
        } else if (command.equalsIgnoreCase("SETGROUPE")) {
            int id;
            try {
                id = Integer.parseInt(infos[1]);
            } catch (Exception e) {
                this.sendErrorMessage("The group you've specified is invalid (it's a number).");
                return;
            }

            Group group = Group.getGroupeById(id);

            if(id == -1) {
                if (infos.length > 2) {
                    Player player = World.world.getPlayerByName(infos[2]);
                    if (player != null) {
                        player.setGroupe(null, true);
                        player.send("BAIC");
                        ((PlayerData) DatabaseManager.get(PlayerData.class)).updateGroupe(id, infos[2]);
                        this.sendSuccessMessage("The player " + infos[2] + " has been remove to his group admin successfully.");
                    }
                } else {
                    this.sendErrorMessage("No player specified, can't change anything.");
                }
            } else
            if(group == null) {
                this.sendErrorMessage("The group you've specified is invalid :");
                for(Group gp : Group.getGroups()) {
                    this.sendMessage("-> " + gp.getId() + " - " + gp.getName());
                }
            } else {
                if (infos.length > 2) {
                    Player player = World.world.getPlayerByName(infos[2]);
                    if (player != null) {
                        player.setGroupe(group, true);
                        player.send("BAIO");
                        ((PlayerData) DatabaseManager.get(PlayerData.class)).updateGroupe(id, infos[2]);
                        this.sendSuccessMessage("The player " + infos[2] + " has been assigned to group " + group.getName() + " successfully.");
                    }
                } else {
                    this.sendErrorMessage("No player specified, can't change anything.");
                }
            }
            return;
        } else  if (command.equalsIgnoreCase("SETFREEPLACE")) {
            //GameServer.freePlace = Integer.parseInt(infos[1]);
            this.sendMessage("");
            return;
        } else if (command.equalsIgnoreCase("SHOWRIGHTGROUPE")) {
            int groupe = -1;
            String cmd = "";
            try {
                groupe = Integer.parseInt(infos[1]);
                cmd = infos[2];
            } catch (Exception e) {
                // ok
            }

            Group g = null;
            if (groupe > 0)
                g = Group.getGroupeById(groupe);

            if (g == null) {
                String str = "Le groupe est invalide.";
                this.sendMessage(str);
                return;
            }

            List<Command> c = g.getCommands();

            if (cmd.equalsIgnoreCase("")) {
                this.sendMessage("\nCommandes disponibles pour le groupe "
                        + g.getName() + " :\n");
                for (Command co : c) {
                    String args = (co.getArguments()[1] != null && !co.getArguments()[1].equalsIgnoreCase("")) ? (" + " + co.getArguments()[1]) : ("");
                    String desc = (co.getArguments()[2] != null && !co.getArguments()[2].equalsIgnoreCase("")) ? (co.getArguments()[2]) : ("");
                    this.sendMessage("<u>"
                            + co.getArguments()[0]
                            + args
                            + "</u> - "
                            + desc);
                }
            } else {
                this.sendMessage("\nCommandes recherches pour le groupe "
                        + g.getName() + " :\n");
                for (Command co : c) {
                    if (co.getArguments()[0].contains(cmd.toUpperCase())) {
                        String args = (co.getArguments()[1] != null && !co.getArguments()[1].equalsIgnoreCase("")) ? (" + " + co.getArguments()[1]) : ("");
                        String desc = (co.getArguments()[2] != null && !co.getArguments()[2].equalsIgnoreCase("")) ? (co.getArguments()[2]) : ("");
                        this.sendMessage("<u>"
                                + co.getArguments()[0]
                                + args
                                + "</u> - "
                                + desc);
                    }
                }
            }
            return;
        } else if (command.equalsIgnoreCase("INV")) {
            int size = this.getPlayer().get_size();
            Player perso = this.getPlayer();
            if (size == 0) {
                if (perso.getGfxId() == 8008)
                    perso.set_size(150);
                else
                    perso.set_size(100);
                perso.setInvisible(false);
                SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(perso.getCurMap(), perso.getId());
                SocketManager.GAME_SEND_ADD_PLAYER_TO_MAP(perso.getCurMap(), perso);
                this.sendMessage("Vous etes visible.");
            } else {
                perso.setInvisible(true);
                perso.set_size(0);
                SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(perso.getCurMap(), perso.getId());
                SocketManager.GAME_SEND_ADD_PLAYER_TO_MAP(perso.getCurMap(), perso);
                this.sendMessage("Vous etes invisible.");
            }
            return;
        } else if (command.equalsIgnoreCase("INCARNAM")) {
            Player perso = this.getPlayer();
            perso.teleport((short) 10292, 284);
            this.sendMessage("Vous avez ete teleporte e Incarnam.");
            return;
        } else if (command.equalsIgnoreCase("ASTRUB")) {
            Player perso = this.getPlayer();
            perso.teleport((short) 7411, 311);
            this.sendMessage("Vous avez ete teleporte e Astrub.");
            return;
        } else if (command.equalsIgnoreCase("DELQUEST")) {
            int id = -1;
            String perso = "";
            try {
                id = Integer.parseInt(infos[1]);
                perso = infos[2];
            } catch (Exception e) {
                // ok
            }

            if (id == -1 || perso.equalsIgnoreCase("")) {
                this.sendMessage("Un des parametres est invalide.");
                return;
            }
            Player p = World.world.getPlayerByName(perso);
            Quest q = Quest.getQuestById(id);
            if (p == null || q == null) {
                this.sendMessage("La quete ou le joueur est introuvable.");
                return;
            }
            QuestPlayer qp = p.getQuestPersoByQuest(q);
            if (qp == null) {
                this.sendMessage("Le personnage n'a pas la quete.");
                return;
            }
            p.delQuestPerso(qp.getId());
            if (qp.removeQuestPlayer()) {
                ((PlayerData) DatabaseManager.get(PlayerData.class)).update(p);
                this.sendMessage("La quete a ete supprime sur le personnage " + perso + ".");
            } else
                this.sendMessage("Un probleme est survenu.");
            return;
        } else if (command.equalsIgnoreCase("ADDQUEST")) {
            int id = -1;
            String perso = "";
            try {
                id = Integer.parseInt(infos[1]);
                perso = infos[2];
            } catch (Exception e) {
                // ok
            }

            if (id == -1 || perso.equalsIgnoreCase("")) {
                this.sendMessage("Un des parametres est invalide.");
                return;
            }
            Player p = World.world.getPlayerByName(perso);
            Quest q = Quest.getQuestById(id);
            if (p == null || q == null) {
                this.sendMessage("La quete ou le joueur est introuvable.");
                return;
            }
            QuestPlayer qp = p.getQuestPersoByQuest(q);
            if (qp != null) {
                this.sendMessage("Le personnage a deje la quete.");
                return;
            }
            q.applyQuest(p);
            qp = p.getQuestPersoByQuest(q);
            if (qp == null) {
                this.sendMessage("Une erreur est survenue.");
                return;
            }
            this.sendMessage("La quete a ete ajoute sur le personnage "
                    + perso + ".");
            return;
        } else if (command.equalsIgnoreCase("FINISHQUEST")) {
            int id = -1;
            String perso = "";
            try {
                id = Integer.parseInt(infos[1]);
                perso = infos[2];
            } catch (Exception e) {
                // ok
            }

            if (id == -1 || perso.equalsIgnoreCase("")) {
                this.sendMessage("Un des parametres est invalide.");
                return;
            }
            Player p = World.world.getPlayerByName(perso);
            Quest q = Quest.getQuestById(id);
            if (p == null || q == null) {
                this.sendMessage("La quete ou le joueur est introuvable.");
                return;
            }
            QuestPlayer qp = p.getQuestPersoByQuest(q);
            if (qp == null) {
                this.sendMessage("Le personnage n'a pas la quete.");
                return;
            }
            for (QuestObjective e : q.getQuestObjectives()) {
                q.updateQuestData(p, true, e.getValidationType());
            }
            ((PlayerData) DatabaseManager.get(PlayerData.class)).update(p);
            this.sendMessage("La quete a ete termine sur le personnage "
                    + perso + ".");
            return;
        } else if (command.equalsIgnoreCase("SKIPQUEST")) {
            int id = -1;
            String perso = "";
            try {
                id = Integer.parseInt(infos[1]);
                perso = infos[2];
            } catch (Exception e) {
                // ok
            }

            if (id == -1 || perso.equalsIgnoreCase("")) {
                this.sendMessage("Un des parametres est invalide.");
                return;
            }
            Player p = World.world.getPlayerByName(perso);
            Quest q = Quest.getQuestById(id);
            if (p == null || q == null) {
                this.sendMessage("La quete ou le joueur est introuvable.");
                return;
            }
            QuestPlayer qp = p.getQuestPersoByQuest(q);
            if (qp == null) {
                this.sendMessage("Le personnage n'a pas la quete.");
                return;
            }
            for (QuestObjective e : q.getQuestObjectives()) {
                if (qp.isQuestObjectiveIsValidate(e))
                    continue;

                q.updateQuestData(p, true, e.getValidationType());
                break;
            }
            ((PlayerData) DatabaseManager.get(PlayerData.class)).update(p);
            this.sendMessage("La quete est passe e l'etape suivante sur le personnage "
                    + perso + ".");
            return;
        } else if (command.equalsIgnoreCase("ITEMQUEST")) {
            int id = -1;
            try {
                id = Integer.parseInt(infos[1]);
            } catch (Exception e) {
                // ok
            }

            if (id == -1) {
                this.sendMessage("Le parametre est invalide.");
                return;
            }
            Quest q = Quest.getQuestById(id);
            if (q == null) {
                this.sendMessage("La quete est introuvable.");
                return;
            }

            for (QuestObjective e : q.getQuestObjectives()) {
                for (Entry<Integer, Integer> entry : e.getItemNecessaryList().entrySet()) {
                    ObjectTemplate objT = World.world.getObjTemplate(entry.getKey());
                    int qua = entry.getValue();
                    GameObject obj = objT.createNewItem(qua, false);
                    if (this.getPlayer().addObjet(obj, true))
                        World.world.addGameObject(obj);
                    SocketManager.GAME_SEND_Im_PACKET(this.getPlayer(), "021;"
                            + qua + "~" + objT.getId());
                    if (objT.getType() == 32) // Si le drop est une mascotte, on l'ajoute ! :)
                    {
                        this.getPlayer().setMascotte(entry.getKey());
                    }
                }
            }
            this.sendMessage("Vous avez reeu tous les items necessaire e la quete.");
            return;
        } else if (command.equalsIgnoreCase("SHOWFIGHTPOS")) {
            String mess = "Liste des StartCell [teamID][cellID]:";
            this.sendMessage(mess);
            String places = this.getPlayer().getCurMap().getPlaces();
            if (places.indexOf('|') == -1 || places.length() < 2) {
                mess = "Les places n'ont pas ete definies";
                this.sendMessage(mess);
                return;
            }
            String team0 = "", team1 = "";
            String[] p = places.split("\\|");
            try {
                team0 = p[0];
            } catch (Exception e) {
                // ok
            }

            try {
                team1 = p[1];
            } catch (Exception e) {
                // ok
            }

            mess = "Team 0 : ";
            for (int a = 0; a <= team0.length() - 2; a += 2) {
                String code = team0.substring(a, a + 2);
                mess += World.world.getCryptManager().cellCode_To_ID(code) + ",";
            }
            this.sendMessage(mess);
            mess = "Team 1 : ";
            for (int a = 0; a <= team1.length() - 2; a += 2) {
                String code = team1.substring(a, a + 2);
                mess += World.world.getCryptManager().cellCode_To_ID(code) + ",";
            }
            this.sendMessage(mess);
        } else if (command.equalsIgnoreCase("ADDFIGHTPOS")) {
            int team = -1;
            int cell = -1;
            try {
                team = Integer.parseInt(infos[1]);
                cell = Integer.parseInt(infos[2]);
            } catch (Exception e) {
                // ok
            }

            if (team < 0 || team > 1) {
                String str = "Team ou cellID incorects";
                this.sendMessage(str);
                return;
            }
            if (cell < 0
                    || this.getPlayer().getCurMap().getCase(cell) == null
                    || !this.getPlayer().getCurMap().getCase(cell).isWalkable(true)) {
                cell = this.getPlayer().getCurCell().getId();
            }
            String places = this.getPlayer().getCurMap().getPlaces();
            String[] p = places.split("\\|");
            boolean already = false;
            String team0 = "", team1 = "";
            try {
                team0 = p[0];
            } catch (Exception e) {
                // ok
            }

            try {
                team1 = p[1];
            } catch (Exception e) {
                // ok
            }

            for (int a = 0; a <= team0.length() - 2; a += 2)
                if (cell == World.world.getCryptManager().cellCode_To_ID(team0.substring(a, a + 2)))
                    already = true;
            for (int a = 0; a <= team1.length() - 2; a += 2)
                if (cell == World.world.getCryptManager().cellCode_To_ID(team1.substring(a, a + 2)))
                    already = true;
            if (already) {
                this.sendMessage("La case est deja dans la liste");
                return;
            }
            if (team == 0)
                team0 += World.world.getCryptManager().cellID_To_Code(cell);
            else if (team == 1)
                team1 += World.world.getCryptManager().cellID_To_Code(cell);
            String newPlaces = team0 + "|" + team1;
            this.getPlayer().getCurMap().setPlaces(newPlaces);
            ((MapData) DatabaseManager.get(MapData.class)).update(this.getPlayer().getCurMap());
            this.sendMessage("Les places ont ete modifiees (" + newPlaces + ")");
        } else if (command.equalsIgnoreCase("DELFIGHTPOS")) {
            int cell = -1;
            try {
                cell = Integer.parseInt(infos[2]);
            } catch (Exception e) {
                // ok
            }

            if (cell < 0 || this.getPlayer().getCurMap().getCase(cell) == null) {
                cell = this.getPlayer().getCurCell().getId();
            }
            String places = this.getPlayer().getCurMap().getPlaces();
            String[] p = places.split("\\|");
            String newPlaces = "";
            String team0 = "", team1 = "";
            try {
                team0 = p[0];
            } catch (Exception e) {
                // ok
            }

            try {
                team1 = p[1];
            } catch (Exception e) {
                // ok
            }

            for (int a = 0; a <= team0.length() - 2; a += 2) {
                String c = p[0].substring(a, a + 2);
                if (cell == World.world.getCryptManager().cellCode_To_ID(c))
                    continue;
                newPlaces += c;
            }
            newPlaces += "|";
            for (int a = 0; a <= team1.length() - 2; a += 2) {
                String c = p[1].substring(a, a + 2);
                if (cell == World.world.getCryptManager().cellCode_To_ID(c))
                    continue;
                newPlaces += c;
            }
            this.getPlayer().getCurMap().setPlaces(newPlaces);
            ((MapData) DatabaseManager.get(MapData.class)).update(this.getPlayer().getCurMap());
            this.sendMessage("Les places ont ete modifiees (" + newPlaces + ")");
        } else if (command.equalsIgnoreCase("DELALLFIGHTPOS")) {
            this.getPlayer().getCurMap().setPlaces("");
            ((MapData) DatabaseManager.get(MapData.class)).update(this.getPlayer().getCurMap());
            this.sendMessage("Les places ont ete mis a zero !");
        } else if (command.equalsIgnoreCase("ADDMOBSUBAREA")) {
            String monsters = "";
            String mess = "";
            if (infos.length > 1)
                monsters = infos[1];
            else {
                mess = "Il manque le premier argument.";
                this.sendMessage(mess);
                return;
            }

            Player perso = this.getPlayer();
            GameMap map = perso.getCurMap();

            SubArea subArea = map.getSubArea();
            List<GameMap> maps = subArea.getMaps();
            int i = 0;
            int y = 0;
            for (GameMap m : maps) {
                if (m.getPlaces().equalsIgnoreCase("") || m.getPlaces().equalsIgnoreCase("|")) {
                    m.setMobPossibles("");
                    ((MapData) DatabaseManager.get(MapData.class)).updateMonster(m, "");
                    y++;
                } else {
                    m.setMobPossibles(monsters);
                    ((MapData) DatabaseManager.get(MapData.class)).updateMonster(m, monsters);
                    i++;
                }
                m.refreshSpawns();
            }

            mess = i + " maps ont etes modifies et refresh. " + y + "maps ont etes modifies sans monstres et refresh.";
            this.sendMessage(mess);
        } else if (command.equalsIgnoreCase("GSMOBSUBAREA")) {
            byte maxGroup = 0;
            byte minSize = 0;
            byte fixSize = 0;
            byte maxSize = 0;
            byte def = -1;
            String mess = "";
            if (infos.length > 4) {
                maxGroup = Byte.parseByte(infos[1]);
                minSize = Byte.parseByte(infos[2]);
                fixSize = Byte.parseByte(infos[3]);
                maxSize = Byte.parseByte(infos[4]);
            } else {
                mess = "Il manque les arguments.";
                this.sendMessage(mess);
                return;
            }

            Player perso = this.getPlayer();
            GameMap map = perso.getCurMap();

            SubArea subArea = map.getSubArea();
            List<GameMap> maps = subArea.getMaps();
            int i = 0;
            int y = 0;
            for (GameMap m : maps) {
                if (m.getPlaces().equalsIgnoreCase("")
                        || m.getPlaces().equalsIgnoreCase("|")) {
                    m.setGs(def, def, def, def);
                    ((MapData) DatabaseManager.get(MapData.class)).updateGs(m);
                    y++;
                } else {
                    m.setGs(maxGroup, minSize, fixSize, maxSize);
                    ((MapData) DatabaseManager.get(MapData.class)).updateGs(m);
                    i++;
                }
                m.refreshSpawns();
            }

            mess = i + " maps ont etes modifies et refresh. " + y
                    + " maps ont etes modifies e -1 partout et refresh.";
            this.sendMessage(mess);
        } else if (command.equalsIgnoreCase("FINDEXTRAMONSTER")) {
            java.util.Map<Integer, java.util.Map<String, java.util.Map<String, Integer>>> extras = World.world.getExtraMonsters();

            for (Entry<Integer, java.util.Map<String, java.util.Map<String, Integer>>> entry : extras.entrySet()) {
                Integer idMob = entry.getKey();
                for (GameMap map : World.world.getMaps())
                    map.getMobPossibles().stream().filter(mob -> mob.getTemplate().getId() == idMob).forEach(mob -> this.sendMessage("Map avec extraMonster : " + map.getId() + " -> " + idMob + "."));
            }
            this.sendMessage("Recherche termine et affiche en console.");
        } else if (command.equalsIgnoreCase("GETAREA")) {
            int subArea = -1, area = -1, superArea = -1;
            try {
                subArea = this.getPlayer().getCurMap().getSubArea().getId();
                area = this.getPlayer().getCurMap().getSubArea().getArea().getId();
                superArea = this.getPlayer().getCurMap().getSubArea().getArea().getSuperArea();
            } catch (Exception e) {
                // ok
            }
            this.sendMessage("subArea : "
                    + subArea
                    + "\nArea : "
                    + area
                    + "\nsuperArea : "
                    + superArea);
        } else {
            this.sendMessage("Commande invalide !");
        }
    }

    private static int getCellJail() {
        switch (Formulas.random.nextInt(4) + 1) {
            case 1:
                return 148;
            case 2:
                return 156;
            case 3:
                return 380;
            case 4:
                return 388;
            default:
                return 148;
        }
    }

    private static String returnClasse(int id) {
        switch (id) {
            case Constant.CLASS_FECA:
                return "Fec";
            case Constant.CLASS_OSAMODAS:
                return "Osa";
            case Constant.CLASS_ENUTROF:
                return "Enu";
            case Constant.CLASS_SRAM:
                return "Sra";
            case Constant.CLASS_XELOR:
                return "Xel";
            case Constant.CLASS_ECAFLIP:
                return "Eca";
            case Constant.CLASS_ENIRIPSA:
                return "Eni";
            case Constant.CLASS_IOP:
                return "Iop";
            case Constant.CLASS_CRA:
                return "Cra";
            case Constant.CLASS_SADIDA:
                return "Sad";
            case Constant.CLASS_SACRIEUR:
                return "Sac";
            case Constant.CLASS_PANDAWA:
                return "Pan";
            default:
                return "Unk";
        }
    }
}