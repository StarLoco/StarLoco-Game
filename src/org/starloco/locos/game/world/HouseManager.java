package org.starloco.locos.game.world;

import org.starloco.locos.area.map.entity.House;
import org.starloco.locos.area.map.entity.Trunk;
import org.starloco.locos.client.Account;
import org.starloco.locos.client.Player;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.game.HouseData;
import org.starloco.locos.database.data.game.TrunkData;
import org.starloco.locos.database.data.login.AccountData;
import org.starloco.locos.database.data.login.PlayerData;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.guild.Guild;

import java.util.Map;

/**
 * Created by Locos on 30/10/2016.
 */
public class HouseManager {

    public House getHouseIdByCoord(int map_id, int cell_id) {
        for (Map.Entry<Integer, House> house : World.world.getHouses().entrySet())
            if (house.getValue().getMapId() == map_id
                    && house.getValue().getCellId() == cell_id)
                return house.getValue();
        return null;
    }

    public void load(Player player, int newMapID) {
        World.world.getHouses().entrySet().stream().filter(house -> house.getValue().getMapId() == newMapID).forEach(house -> {
            StringBuilder packet = new StringBuilder();
            packet.append("P").append(house.getValue().getId()).append("|");
            if (house.getValue().getOwnerId() > 0) {
                Account C = World.world.getAccount(house.getValue().getOwnerId());
                if (C == null)//Ne devrait pas arriver
                    packet.append("undefined;");
                else
                    packet.append(World.world.getAccount(house.getValue().getOwnerId()).getPseudo()).append(";");
            } else {
                packet.append(";");
            }

            if (house.getValue().getSale() > 0)//Si prix > 0
                packet.append("1");//Achetable
            else
                packet.append("0");//Non achetable

            if (house.getValue().getGuildId() > 0) //Maison de guilde
            {
                Guild G = World.world.getGuild(house.getValue().getGuildId());
                if (G != null) {
                    String Gname = G.getName();
                    String Gemblem = G.getEmblem();
                    if (G.getPlayers().size() < 10 && G.getId() > 2)//Ce n'est plus une maison de guilde
                    {
                        ((HouseData) DatabaseManager.get(HouseData.class)).updateGuild(house.getValue(), 0, 0);
                    } else {
                        //Affiche le blason pour les membre de guilde OU Affiche le blason pour les non membre de guilde
                        if (player.getGuild() != null
                                && player.getGuild().getId() == house.getValue().getGuildId()
                                && house.getValue().canDo(Constant.H_GBLASON))//meme guilde
                        {
                            packet.append(";").append(Gname).append(";").append(Gemblem);
                        } else if (house.getValue().canDo(Constant.H_OBLASON))//Pas de guilde/guilde-diff�rente
                        {
                            packet.append(";").append(Gname).append(";").append(Gemblem);
                        }
                    }
                }
            }
            SocketManager.GAME_SEND_hOUSE(player, packet.toString());

            if (house.getValue().getOwnerId() == player.getAccID()) {
                StringBuilder packet1 = new StringBuilder();
                packet1.append("L+|").append(house.getValue().getId()).append(";").append(house.getValue().getAccess()).append(";");

                if (house.getValue().getSale() <= 0) {
                    packet1.append("0;").append(house.getValue().getSale());
                } else if (house.getValue().getSale() > 0) {
                    packet1.append("1;").append(house.getValue().getSale());
                }
                SocketManager.GAME_SEND_hOUSE(player, packet1.toString());
            }
        });
    }

    public void buy(Player player)//Acheter une maison
    {
        House house = player.getInHouse();

        if (World.world.getHouseManager().alreadyHaveHouse(player)) {
            SocketManager.GAME_SEND_Im_PACKET(player, "132;1");
            return;
        }

        if (player.getKamas() < house.getSale())
            return;

        player.setKamas(player.getKamas() - house.getSale());

        int kamas = 0;
        for (Trunk trunk : Trunk.getTrunksByHouse(house)) {
            if (house.getOwnerId() > 0)
                trunk.moveTrunkToBank(World.world.getAccount(house.getOwnerId()));//D�placement des items vers la banque

            kamas += trunk.getKamas();
            trunk.setKamas(0);//Retrait kamas
            trunk.setKey("-");//ResetPass
            trunk.setOwnerId(player.getAccID());//ResetOwner
            ((TrunkData) DatabaseManager.get(TrunkData.class)).update(trunk);
        }

        //Ajoute des kamas dans la banque du vendeur
        if (house.getOwnerId() > 0) {
            Account seller = World.world.getAccount(house.getOwnerId());
            seller.setBankKamas(seller.getBankKamas() + house.getSale() + kamas);

            if (seller.getCurrentPlayer() != null)//FIXME: change the packet (Im)
                SocketManager.GAME_SEND_MESSAGE(seller.getCurrentPlayer(), seller.getCurrentPlayer().getLang().trans("game.game.world.housemanager.sell"));
            ((AccountData) DatabaseManager.get(AccountData.class)).update(seller);
        }

        closeBuy(player);
        SocketManager.GAME_SEND_STATS_PACKET(player);
        ((HouseData) DatabaseManager.get(HouseData.class)).buy(player, house);

        for (Player viewer : player.getCurMap().getPlayers())
            World.world.getHouseManager().load(viewer, viewer.getCurMap().getId());

        ((PlayerData) DatabaseManager.get(PlayerData.class)).update(player);
    }

    public void sell(Player P, String packet)//Vendre une maison
    {
        House h = P.getInHouse();
        int price = Integer.parseInt(packet);
        if (h.isHouse(P, h)) {
            SocketManager.GAME_SEND_hOUSE(P, "V");
            SocketManager.GAME_SEND_hOUSE(P, "SK" + h.getId() + "|" + price);
            //Vente de la maison
            ((HouseData) DatabaseManager.get(HouseData.class)).sell(h, price);
            //Rafraichir la map apr�s la mise en vente
            for (Player z : P.getCurMap().getPlayers())
                load(z, z.getCurMap().getId());
        }
    }

    public void closeCode(Player P) {
        SocketManager.GAME_SEND_KODE(P, "V");
        P.setInHouse(null);
    }

    public void closeBuy(Player P) {
        SocketManager.GAME_SEND_hOUSE(P, "V");
    }

    public void lockIt(Player P, String packet) {
        House h = P.getInHouse();
        if (h.isHouse(P, h)) {
            ((HouseData) DatabaseManager.get(HouseData.class)).updateCode(P, h, packet);//Change le code
            closeCode(P);
        } else {
            closeCode(P);
        }
    }

    public String parseHouseToGuild(Player P) {
        boolean isFirst = true;
        String packet = "+";
        for (Map.Entry<Integer, House> house : World.world.getHouses().entrySet()) {
            if (house.getValue().getGuildId() == P.getGuild().getId()
                    && house.getValue().getGuildRights() > 0) {
                String name = "";
                int id = house.getValue().getOwnerId();
                if (id != -1) {
                    Account a = World.world.getAccount(id);
                    if (a != null) {
                        name = a.getPseudo();
                    }
                }
                if (isFirst) {
                    packet += house.getKey() + ";";
                    if (World.world.getPlayer(house.getValue().getOwnerId()) == null)
                        packet += name + ";";
                    else
                        packet += World.world.getPlayer(house.getValue().getOwnerId()).getAccount().getPseudo()
                                + ";";
                    packet += World.world.getMap((short) house.getValue().getHouseMapId()).getX()
                            + ","
                            + World.world.getMap((short) house.getValue().getHouseMapId()).getY()
                            + ";";
                    packet += "0;";
                    packet += house.getValue().getGuildRights();
                    isFirst = false;
                } else {
                    packet += "|";
                    packet += house.getKey() + ";";
                    if (World.world.getPlayer(house.getValue().getOwnerId()) == null)
                        packet += name + ";";
                    else
                        packet += World.world.getPlayer(house.getValue().getOwnerId()).getAccount().getPseudo()
                                + ";";
                    packet += World.world.getMap((short) house.getValue().getHouseMapId()).getX()
                            + ","
                            + World.world.getMap((short) house.getValue().getHouseMapId()).getY()
                            + ";";
                    packet += "0;";
                    packet += house.getValue().getGuildRights();
                }
            }
        }
        return packet;
    }

    public boolean alreadyHaveHouse(Player P) {
        for (Map.Entry<Integer, House> house : World.world.getHouses().entrySet())
            if (house.getValue().getOwnerId() == P.getAccID())
                return true;
        return false;
    }

    public void parseHG(Player P, String packet) {
        House h = P.getInHouse();
        if (P.getGuild() == null)
            return;
        if (packet != null) {
            if (packet.charAt(0) == '+') {
                //Ajoute en guilde
                byte HouseMaxOnGuild = (byte) Math.floor(P.getGuild().getLvl() / 10);
                if (houseOnGuild(P.getGuild().getId()) >= HouseMaxOnGuild && P.getGuild().getId() > 2) {
                    P.send("Im1151");
                    return;
                }
                if (P.getGuild().getPlayers().size() < 10 && P.getGuild().getId() > 2) {
                    return;
                }
                ((HouseData) DatabaseManager.get(HouseData.class)).updateGuild(h, P.getGuild().getId(), 0);
                parseHG(P, null);
            } else if (packet.charAt(0) == '-') {
                //Retire de la guilde
                ((HouseData) DatabaseManager.get(HouseData.class)).updateGuild(h, 0, 0);
                parseHG(P, null);
            } else {
                ((HouseData) DatabaseManager.get(HouseData.class)).updateGuild(h, h.getGuildId(), Integer.parseInt(packet));
                h.parseIntToRight(Integer.parseInt(packet));
            }
        } else {
            if (h.getGuildId() <= 0) {
                SocketManager.GAME_SEND_hOUSE(P, "G" + h.getId());
            } else if (h.getGuildId() > 0) {
                SocketManager.GAME_SEND_hOUSE(P, "G" + h.getId() + ";"
                        + P.getGuild().getName() + ";"
                        + P.getGuild().getEmblem() + ";" + h.getGuildRights());
            }
        }
    }

    public byte houseOnGuild(int GuildID) {
        byte i = 0;
        for (Map.Entry<Integer, House> house : World.world.getHouses().entrySet())
            if (house.getValue().getGuildId() == GuildID)
                i++;
        return i;
    }

    public void leave(Player player, String packet) {
        House h = player.getInHouse();
        if (!h.isHouse(player, h))
            return;
        int Pguid = Integer.parseInt(packet);
        Player Target = World.world.getPlayer(Pguid);
        if (Target == null || !Target.isOnline() || Target.getFight() != null
                || Target.getCurMap().getId() != player.getCurMap().getId())
            return;
        Target.teleport(h.getMapId(), h.getCellId());
        SocketManager.GAME_SEND_Im_PACKET(Target, "018;" + player.getName());
    }

    public House getHouseByPerso(Player player)  {
        for (Map.Entry<Integer, House> house : World.world.getHouses().entrySet())
            if (house.getValue().getOwnerId() == player.getAccID())
                return house.getValue();
        return null;
    }

    public void removeHouseGuild(int guildId) {
        World.world.getHouses().entrySet().stream().filter(h -> h.getValue().getGuildId() == guildId).forEach(h -> {
            h.getValue().setGuildRights(0);
            h.getValue().setGuildId(0);
        });
        ((HouseData) DatabaseManager.get(HouseData.class)).removeGuild(guildId); //Supprime les maisons de guilde
    }
}
