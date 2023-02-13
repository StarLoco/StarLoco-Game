package org.starloco.locos.area.map.entity;

import org.starloco.locos.client.Player;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.game.action.ExchangeAction;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Constant;

import java.util.Map;
import java.util.TreeMap;

public class House {
    private int id;
    private short mapId;
    private int cellId;
    private int ownerId;
    private int sale;
    private int guildId;
    private int guildRights;
    private int access;
    private String key;
    private int houseMapId;
    private int houseCellId;
    //Droits de chaques maisons
    private Map<Integer, Boolean> haveRight = new TreeMap<>();

    public House(int id, short mapId, int cellId, int houseMapId, int houseCellId) {
        this.id = id;
        this.mapId = mapId;
        this.cellId = cellId;
        this.houseMapId = houseMapId;
        this.houseCellId = houseCellId;
    }

    public void open(Player P, String packet, boolean isHome)//Ouvrir une maison ;o
    {
        if ((!this.canDo(Constant.H_OCANTOPEN) && (packet.compareTo(this.getKey()) == 0))
                || isHome)//Si c'est chez lui ou que le mot de passe est bon
        {
            P.teleport((short) this.getHouseMapId(), this.getHouseCellId());
            World.world.getHouseManager().closeCode(P);
        } else if ((packet.compareTo(this.getKey()) != 0)
                || this.canDo(Constant.H_OCANTOPEN))//Mauvais code
        {
            SocketManager.GAME_SEND_KODE(P, "KE");
            SocketManager.GAME_SEND_KODE(P, "V");
        }
    }

    public void setGuildRightsWithParse(int guildRights) {
        this.guildRights = guildRights;
        parseIntToRight(guildRights);
    }

    public int getId() {
        return this.id;
    }

    public short getMapId() {
        return this.mapId;
    }

    public int getCellId() {
        return this.cellId;
    }

    public int getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerId(int id) {
        this.ownerId = id;
    }

    public int getSale() {
        return this.sale;
    }

    public void setSale(int price) {
        this.sale = price;
    }

    public int getGuildId() {
        return this.guildId;
    }

    public void setGuildId(int guildId) {
        this.guildId = guildId;
    }

    public int getGuildRights() {
        return this.guildRights;
    }

    public void setGuildRights(int guildRights) {
        this.guildRights = guildRights;
    }

    public int getAccess() {
        return this.access;
    }

    public void setAccess(int access) {
        this.access = access;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getHouseMapId() {
        return this.houseMapId;
    }

    public int getHouseCellId() {
        return this.houseCellId;
    }

    public void enter(Player P) {//Entrer dans la maison
        if (P.getFight() != null || P.getExchangeAction() != null)
            return;
        if (this.getOwnerId() == P.getAccID() || (P.getGuild() != null && P.getGuild().getId() == this.getGuildId() && canDo(Constant.H_GNOCODE)))//C'est sa maison ou m�me guilde + droits entrer sans pass
            open(P, "-", true);
        else if (this.getOwnerId() > 0) //Une personne autre la acheter, il faut le code pour rentrer
            SocketManager.GAME_SEND_KODE(P, "CK0|8");//8 �tant le nombre de chiffre du code
        else if (this.getOwnerId() == 0)//Maison non acheter, mais achetable, on peut rentrer sans code
            open(P, "-", false);
    }

    public void buyIt(Player P)//Acheter une maison
    {
        House h = P.getInHouse();
        String str = "CK" + h.getId() + "|" + h.getSale();//ID + Prix
        SocketManager.GAME_SEND_hOUSE(P, str);
    }

    public void sellIt(Player P)//Vendre une maison
    {
        House h = P.getInHouse();
        if (isHouse(P, h)) {
            String str = "CK" + h.getId() + "|" + h.getSale();//ID + Prix
            SocketManager.GAME_SEND_hOUSE(P, str);
        }
    }

    public boolean isHouse(Player P, House h)//Savoir si c'est sa maison
    {
        return h.getOwnerId() == P.getAccID();
    }

    public void lock(Player player) {
        player.setExchangeAction(new ExchangeAction<>(ExchangeAction.LOCK_HOUSE, this));
        SocketManager.GAME_SEND_KODE(player, "CK1|8");
    }

    public boolean canDo(int rightValue) {
        return haveRight.get(rightValue);
    }

    private void initRight() {
        haveRight.put(Constant.H_GBLASON, false);
        haveRight.put(Constant.H_OBLASON, false);
        haveRight.put(Constant.H_GNOCODE, false);
        haveRight.put(Constant.H_OCANTOPEN, false);
        haveRight.put(Constant.C_GNOCODE, false);
        haveRight.put(Constant.C_OCANTOPEN, false);
        haveRight.put(Constant.H_GREPOS, false);
        haveRight.put(Constant.H_GTELE, false);
    }

    public void parseIntToRight(int total) {
        if (haveRight.isEmpty()) {
            initRight();
        }
        if (total == 1)
            return;

        if (haveRight.size() > 0) //Si les droits contiennent quelque chose -> Vidage (M�me si le HashMap supprimerais les entr�es doublon lors de l'ajout)
            haveRight.clear();

        initRight(); //Remplissage des droits

        Integer[] mapKey = haveRight.keySet().toArray(new Integer[haveRight.size()]); //R�cup�re les clef de map dans un tableau d'Integer

        while (total > 0) {
            for (int i = haveRight.size() - 1; i < haveRight.size(); i--) {
                int map = mapKey[i];
                if (map <= total) {
                    total ^= map;
                    haveRight.put(map, true);
                    break;
                }
            }
        }
    }
}