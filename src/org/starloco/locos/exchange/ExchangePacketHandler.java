package org.starloco.locos.exchange;

import org.starloco.locos.client.Account;
import org.starloco.locos.command.CommandPlayer;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.login.AccountData;
import org.starloco.locos.database.data.login.PlayerData;
import org.starloco.locos.game.GameClient;
import org.starloco.locos.game.GameServer;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Main;

import java.text.SimpleDateFormat;
import java.util.Date;

class ExchangePacketHandler {

    static void parser(String packet) {
            if(packet.isEmpty()) return;
            try {
                switch (packet.charAt(0)) {
                    case 'F': //Free places
                        if (packet.charAt(1) == '?') { //Required
                            int i = GameServer.MAX_PLAYERS - World.world.getOnlinePlayers().size();
                            Config.exchangeClient.send("F" + i);
                        }
                        break;

                    case 'S': //Server
                        switch (packet.charAt(1)) {
                            case 'H': //Host
                                if (packet.charAt(2) == 'K') { //Ok
                                    ExchangeClient.logger.info("The login server has validated the connection.");
                                    GameServer.setState(1);
                                }
                                break;

                            case 'K': //Key
                                switch (packet.charAt(2)) {
                                    case '?': //Required
                                        int i = 50000 - Config.gameServer.getClients().size();
                                        Config.exchangeClient.send("SK" + Config.gameServerId + ";" + Config.gameServerKey + ";" + i);
                                        break;

                                    case 'K': //Ok
                                        ExchangeClient.logger.info("The login server has accepted the connection.");
                                        Config.exchangeClient.send("SH" + Config.gameIp + ";" + Config.gamePort);
                                        break;

                                    case 'R': //Refused
                                        ExchangeClient.logger.info("The login server has refused the connection.");
                                        Main.stop("Connection refused by the login");
                                        break;
                                }
                                break;
                        }
                        break;

                    case 'W': //Waiting
                        switch (packet.charAt(1)) {
                            case 'A': //Add
                                int id = Integer.parseInt(packet.substring(2));
                                Account account = World.world.getAccount(id);

                                if (account == null) {
                                    ((AccountData) DatabaseManager.get(AccountData.class)).load(id);
                                    account = World.world.getAccount(id);
                                }

                                if (account != null) {
                                    if (account.getCurrentPlayer() != null)
                                        account.getGameClient().kick();
                                    account.setSubscribe();
                                    Config.gameServer.addWaitingAccount(account);
                                }
                                break;
                            case 'K': //Kick
                                id = Integer.parseInt(packet.substring(2));
                                ((PlayerData) DatabaseManager.get(PlayerData.class)).updateAllLogged(id, 0);
                                ((AccountData) DatabaseManager.get(AccountData.class)).setLogged(id, 0);
                                account = World.world.getAccount(id);

                                if (account != null) {
                                    GameClient client;
                                    if ((client = account.getGameClient()) != null) {
                                        client.disconnect();
                                        client.kick();
                                    }
                                }
                                break;
                        }
                        break;

                    case 'D': // Data
                        if (packet.charAt(1) == 'M') { // Message
                            String[] split = packet.substring(2).split(";");
                            if (split.length > 1) {
                                String prefix = "<font color='#C35617'>[" + (new SimpleDateFormat("HH:mm").format(new Date(System.currentTimeMillis()))) + "] (" + CommandPlayer.canal + ") (" + split[1] + ") <b>" + split[0] + "</b>";
                                final String message = "Im116;" + prefix + "~" + split[2] + "</font>";

                                World.world.getOnlinePlayers().stream().filter(p -> p != null && !p.noall).forEach(p -> p.send(message.replace("%20", " ")));
                            }
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
