package org.starloco.locos.client;

import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.util.Pair;
import org.starloco.locos.command.administration.Group;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.game.BankData;
import org.starloco.locos.database.data.game.GiftData;
import org.starloco.locos.database.data.game.HdvObjectData;
import org.starloco.locos.database.data.login.AccountData;
import org.starloco.locos.database.data.login.MountData;
import org.starloco.locos.database.data.login.PlayerData;
import org.starloco.locos.game.GameClient;
import org.starloco.locos.game.world.World;
import org.starloco.locos.hdv.HdvEntry;
import org.starloco.locos.kernel.Main;
import org.starloco.locos.object.GameObject;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Account {
	
    private int id;
    private String name;
    private String pseudo;
    private String answer;
    private String currentIp = "";
    private String lastIP = "";
    private String lastConnectionDate = "";
    private int points;
    private long muteTime = 0;
    private String mutePseudo = "";
    private boolean banned = false;
    private long subscriber = 1;
    private long bankKamas = 0;
    private Player currentPlayer;
    private GameClient gameClient;
    private byte state;
    private String lastVoteIP;
    private long heureVote;
    private List<GameObject> bank = new ArrayList<>();
    private List<Integer> friends = new ArrayList<>();
    private List<Integer> enemys = new ArrayList<>();
    private Map<Integer, ArrayList<HdvEntry>> hdvsItems;

    public Account(int guid, String name, String pseudo,
                   String answer, boolean banned,
                   String lastIp, String lastConnectionDate, String friends,
                   String enemy, int points, long subscriber, long muteTime, String mutePseudo,
                   String lastVoteIP, String heureVote) {
        this.id = guid;
        this.name = name;
        this.pseudo = pseudo;
        this.answer = answer;
        this.banned = banned;
        this.lastIP = lastIp;
        this.lastConnectionDate = lastConnectionDate;
        this.hdvsItems = World.world.getMyItems(guid);
        this.points = points;
        this.subscriber = subscriber;
        this.muteTime = muteTime;
        this.mutePseudo = mutePseudo;
        this.lastVoteIP = lastVoteIP;

        if (heureVote.equalsIgnoreCase("")) this.heureVote = 0;
        else this.heureVote = Long.parseLong(heureVote);

        //Chargement de la liste d'amie
        if(friends != null && !friends.equalsIgnoreCase("")) {
            for (String f : friends.split(";")) {
                try {
                    this.friends.add(Integer.parseInt(f));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        //Chargement de la liste d'Enemy
        if (enemy != null && !enemy.equalsIgnoreCase("")) {
            for (String e : enemy.split(";")) {
                try {
                    this.enemys.add(Integer.parseInt(e));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }

        if (((GiftData) DatabaseManager.get(GiftData.class)).load(guid).getValue() == null)
            ((GiftData) DatabaseManager.get(GiftData.class)).insert(new Pair<>(this, ""));
    }

    public long getHeureVote() {
        return this.heureVote;
    }

    public String getLastVoteIP() {
        return this.lastVoteIP;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPseudo() {
        return pseudo;
    }

    public String getAnswer() {
        return answer;
    }

    public String getCurrentIp() {
        return currentIp;
    }

    public void setCurrentIp(String i) {
        currentIp = i;
    }

    public String getLastIP() {
        return lastIP;
    }

    public void setLastIP(String i) {
        lastIP = i;
    }

    public String getLastConnectionDate() {
        return lastConnectionDate;
    }

    public void setLastConnectionDate(String i) {
        lastConnectionDate = i;
    }

    public int getPoints() {
        points = ((AccountData) DatabaseManager.get(AccountData.class)).loadPoints(name);
        return points;
    }

    public void setPoints(int i) {
        points = i;
        ((AccountData) DatabaseManager.get(AccountData.class)).updatePoints(id, points);
    }

    public void addPoints(int i) {
        this.getPoints();
        points += i;
        ((AccountData) DatabaseManager.get(AccountData.class)).updatePoints(id, points);
    }

    public void mute(short minutes, String pseudo) {
        if (minutes <= 0) return;
        muteTime = System.currentTimeMillis() + minutes * 60000;
        mutePseudo = pseudo;
        ((AccountData) DatabaseManager.get(AccountData.class)).update(this);
        if (this.currentPlayer != null) this.currentPlayer.send("Im117;" + pseudo + "~" + minutes);
    }

    public void unMute() {
        if (muteTime == 0) return;
        muteTime = 0;
        mutePseudo = "";
        ((AccountData) DatabaseManager.get(AccountData.class)).update(this);
    }

    public boolean isMuted() {
        if (muteTime == 0)
            return false;
        if (muteTime >= System.currentTimeMillis())
            return true;
        muteTime = 0;
        mutePseudo = "";
        ((AccountData) DatabaseManager.get(AccountData.class)).update(this);
        return false;
    }

    public long getMuteTime() {
        if (!isMuted())
            return 0;
        return muteTime;
    }

    public String getMutePseudo() {
        if (!isMuted())
            return "";
        return mutePseudo;
    }

    public List<GameObject> getBank() {
        return bank;
    }

    public String parseBankObjectsToDB() {
        StringBuilder str = new StringBuilder();
        if (this.bank.isEmpty())
            return "";
        for (GameObject gameObject : this.bank)
            str.append(gameObject.getGuid()).append("|");
        return str.toString();
    }

    public long getBankKamas() {
        return this.bankKamas;
    }

    public void setBankKamas(long i) {
        this.bankKamas = i;
        ((BankData) DatabaseManager.get(BankData.class)).update(this);
    }

    public GameClient getGameClient() {
        return this.gameClient;
    }

    public void setGameClient(GameClient t) {
        this.gameClient = t;
    }

    public Map<Integer, Player> getPlayers() {
        Map<Integer, Player> players = new HashMap<>();
        new CopyOnWriteArrayList<>(World.world.getPlayers()).stream().filter(player -> player != null).filter(player -> player.getAccount() != null)
                .filter(player -> player.getAccount().getId() == this.getId()).forEach(player -> {
            if (player.getGameClient() == null)
                player.setAccount(this);
            players.put(player.getId(), player);
        });
        return players;
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }

    public boolean isBanned() {
        return this.banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public boolean isOnline() {
        return this.gameClient != null;
    }

    public void setState(int state) {
        this.state = (byte) state;
        ((AccountData) DatabaseManager.get(AccountData.class)).update(this);
    }

    public byte getState() {
        return state;
    }

    public void setSubscribe() {
        this.subscriber = ((AccountData) DatabaseManager.get(AccountData.class)).getSubscribe(this.id);
    }

    public long getSubscribeRemaining() {
        long remaining = this.subscriber - System.currentTimeMillis();
        if (!Config.subscription)
            return remaining > 0L ? remaining : 525600L;
        return remaining <= 0L ? 0L : remaining;
    }

    public boolean isSubscribe() {
        if (!Config.subscription)
            return true;
        long remaining = this.subscriber - System.currentTimeMillis();
        return remaining > 0L;
    }

    public boolean isSubscribeWithoutCondition() {
        long remaining = this.subscriber - System.currentTimeMillis();
        return remaining > 0L;
    }

    public boolean createPlayer(String name, int sexe, int classe, int color1, int color2, int color3) {
        Player perso = Player.create(name, sexe, classe, color1, color2, color3, this);
        return perso != null;
    }

    public void deletePlayer(int guid) {
        if (this.getPlayers().containsKey(guid))
            World.world.removePlayer(this.getPlayers().get(guid));
    }

    public void sendOnline() {
        for (int id : this.friends) {
            Player player = World.world.getPlayer(id);
            if (player != null && player.is_showFriendConnection() && player.isOnline() && player.getAccount().isFriendWith(this.id))
                SocketManager.GAME_SEND_FRIEND_ONLINE(this.currentPlayer, player);
        }
    }

    public void addFriend(int id) {
        if (this.id == id) {
            SocketManager.GAME_SEND_FA_PACKET(this.currentPlayer, "Ey");
            return;
        }

        Account account = World.world.getAccount(id);

        if (account == null) {
            SocketManager.GAME_SEND_MESSAGE(this.currentPlayer, this.currentPlayer.getLang().trans("client.account.addfriend.notexist.account"));
            return;
        }

        Player player = account.getCurrentPlayer(); // Il est arriv� que le personnage soit null alors que ... non !

        if (player == null) {
            SocketManager.GAME_SEND_MESSAGE(this.currentPlayer, this.currentPlayer.getLang().trans("client.account.addfriend.notexist.player"));
            return;
        }

        Group group = player.getGroupe();

        if (group != null && !group.isPlayer()) {
            SocketManager.GAME_SEND_MESSAGE(this.currentPlayer, this.currentPlayer.getLang().trans("client.account.addfriend.staff"));
            return;
        }
        if (!this.friends.contains(id)) {
            this.friends.add(id);
            SocketManager.GAME_SEND_FA_PACKET(this.currentPlayer, "K" + account.getPseudo() + player.parseToFriendList(id));
            ((AccountData) DatabaseManager.get(AccountData.class)).update(this);
        } else {
            SocketManager.GAME_SEND_FA_PACKET(this.currentPlayer, "Ea");
        }
    }

    public void removeFriend(int id) {
        if (this.friends.contains(id)) {
            Iterator<Integer> iterator = this.friends.iterator();
            while(iterator.hasNext())
                if(iterator.next() == id)
                    iterator.remove();
            ((AccountData) DatabaseManager.get(AccountData.class)).update(this);
        }
        SocketManager.GAME_SEND_FD_PACKET(this.currentPlayer, "K");
    }

    public boolean isFriendWith(int id) {
        return friends.contains(id);
    }

    public String parseFriendListToDB() {
        String str = "";
        for (int i : this.friends) {
            if (!str.equalsIgnoreCase(""))
                str += ";";
            str += i + "";
        }
        return str;
    }

    public String parseFriendList() {
        StringBuilder str = new StringBuilder();
        if (this.friends.isEmpty())
            return "";
        for (int i : this.friends) {
            Account C = World.world.getAccount(i);
            if (C == null)
                continue;
            str.append("|").append(C.getPseudo());
            //on s'arrete la si aucun perso n'est connect�
            if (!C.isOnline())
                continue;
            Player P = C.getCurrentPlayer();
            if (P == null)
                continue;
            str.append(P.parseToFriendList(id));
        }
        return str.toString();
    }

    public void addEnemy(String packet, int guid) {
        if (this.id == guid) {
            SocketManager.GAME_SEND_FA_PACKET(this.currentPlayer, "Ey");
            return;
        }
        if (!this.enemys.contains(guid)) {
            this.enemys.add(guid);
            Player Pr = World.world.getPlayerByName(packet);
            SocketManager.GAME_SEND_ADD_ENEMY(this.currentPlayer, Pr);
            ((AccountData) DatabaseManager.get(AccountData.class)).update(this);
        } else
            SocketManager.GAME_SEND_iAEA_PACKET(this.currentPlayer);
    }

    public void removeEnemy(int id) {
        if (this.enemys.contains(id)) {
            Iterator<Integer> iterator = this.enemys.iterator();
            while(iterator.hasNext())
                if(iterator.next() == id)
                    iterator.remove();
            ((AccountData) DatabaseManager.get(AccountData.class)).update(this);
        }
        SocketManager.GAME_SEND_iD_COMMANDE(this.currentPlayer, "K");
    }

    public boolean isEnemyWith(int id) {
        return enemys.contains(id);
    }

    public String parseEnemyListToDB() {
        String str = "";
        for (int i : this.enemys) {
            if (!str.equalsIgnoreCase(""))
                str += ";";
            str += i + "";
        }
        return str;
    }

    public String parseEnemyList() {
        StringBuilder str = new StringBuilder();
        if (this.enemys.isEmpty())
            return "";
        for (int i : this.enemys) {
            Account C = World.world.getAccount(i);
            if (C == null)
                continue;
            str.append("|").append(C.getPseudo());
            //on s'arrete la si aucun perso n'est connect�
            if (!C.isOnline())
                continue;
            Player P = C.getCurrentPlayer();
            if (P == null)
                continue;
            str.append(P.parseToEnemyList(id));
        }
        return str.toString();
    }

    public boolean recoverItem(int lineId) {
        if (this.currentPlayer == null || this.currentPlayer.getExchangeAction() == null)
            return false;
        if ((Integer) this.currentPlayer.getExchangeAction().getValue() >= 0)
            return false;

        int hdvID = Math.abs((Integer) this.currentPlayer.getExchangeAction().getValue());//R�cup�re l'ID de l'HDV

        HdvEntry entry = null;
        try {
            ArrayList<HdvEntry> entries = this.hdvsItems.get(hdvID);
            if (entries == null || entries.isEmpty())
                return false;
            for (HdvEntry tempEntry : entries) {//Boucle dans la liste d'entry de l'HDV pour trouver un entry avec le meme cheapestID que sp�cifi�
                if (tempEntry.getLineId() == lineId) {//Si la boucle trouve un objet avec le meme cheapestID, arrete la boucle
                    entry = tempEntry;
                    break;
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }

        if (entry == null)//Si entry == null cela veut dire que la boucle s'est effectu� sans trouver d'item avec le meme cheapestID
            return false;
        if(entry.buy)
            return false;

        this.hdvsItems.get(hdvID).remove(entry);//Retire l'item de la liste des objets a vendre du compte
        GameObject obj = entry.getGameObject();

        if (this.currentPlayer.addObjetSimiler(obj, true, -1)) {
            World.world.removeGameObject(obj.getGuid());
        } else {
            this.currentPlayer.addObjet(obj);
        }
        ((HdvObjectData) DatabaseManager.get(HdvObjectData.class)).delete(entry);
        World.world.getHdv(hdvID).delEntry(entry);//Retire l'item de l'HDV

        ((PlayerData) DatabaseManager.get(PlayerData.class)).update(this.currentPlayer);
        return true;
    }

    public HdvEntry[] getHdvEntries(int id) {
        if (this.hdvsItems.get(id) == null) return new HdvEntry[1];
        HdvEntry[] entries = new HdvEntry[this.hdvsItems.get(id).size()];

        for (int i = 0; i < this.hdvsItems.get(id).size(); i++)
            entries[i] = this.hdvsItems.get(id).get(i);
        return entries;
    }

    public int countHdvEntries(int id) {
        ArrayList<HdvEntry> hdvEntry = this.hdvsItems.get(id);
        return hdvEntry == null ? 0 : hdvEntry.size();
    }

    public void resetAllChars() {
        for (Player player : this.getPlayers().values()) {
            if (player.getFight() != null) {
                if (player.getParty() != null)
                    player.getParty().leave(player);
                player.setOnline(true);
            }

            if (player.getExchangeAction() != null)
                GameClient.leaveExchange(player);
            if (player.getParty() != null)
                player.getParty().leave(player);
            if (player.getCurCell() != null)
                player.getCurCell().removePlayer(player);
            if (player.getCurMap() != null && player.isOnline())
                SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(player.getCurMap(), player.getId());

            player.setOnline(false);
        }
    }

    public void disconnect(Player player) {
        ((PlayerData) DatabaseManager.get(PlayerData.class)).updateAllLogged(this.getId(), 0);

        if (player.getExchangeAction() != null)
            GameClient.leaveExchange(player);
        if (player.getParty() != null)
            player.getParty().leave(player);
        if (player.getMount() != null)
            ((MountData) DatabaseManager.get(MountData.class)).update(player.getMount());
        if (player.getFight() != null) {
            if (player.getFight().onPlayerDisconnection(player, false)) {
                ((PlayerData) DatabaseManager.get(PlayerData.class)).update(player);
                return;
            }
        }
        if(Config.modeHeroic) {
            if(player.getAlignment() == Constant.ALIGNEMENT_BONTARIEN) Main.angels--;
            else if(player.getAlignment() == Constant.ALIGNEMENT_BRAKMARIEN) Main.demons--;
            player.setAlignment(Constant.ALIGNEMENT_NEUTRE);
        }
        this.currentPlayer = null;
        this.gameClient = null;
        this.currentIp = "";

        for (Player character : this.getPlayers().values())
            ((PlayerData) DatabaseManager.get(PlayerData.class)).update(character);

        player.resetVars();
        this.resetAllChars();
        ((AccountData) DatabaseManager.get(AccountData.class)).update(this);
        World.world.logger.info("The player " + player.getName() + " come to disconnect.");
    }

    public void updateVote(String hour, String ip) {
        if (hour.equalsIgnoreCase("")) this.heureVote = 0;
        else this.heureVote = Long.parseLong(hour);
        this.lastVoteIP = ip;
    }

    public void parseBank(int kamas, String items) {
        if (kamas == -1 && items == null) {
            ((BankData) DatabaseManager.get(BankData.class)).insert(this);
        } else {
            this.bankKamas = kamas;

            if (!items.equals("")) {
                for (String item : items.split("\\|")) {
                    if (!item.equals("")) {
                        GameObject obj = World.world.getGameObject(Integer.parseInt(item));
                        if (obj != null)
                            this.bank.add(obj);
                    }
                }
            }
        }
    }

    public void addGift(int template, short quantity, byte jp) {
        String gift = template + "," + quantity + "," + jp;
        String gifts = ((GiftData) DatabaseManager.get(GiftData.class)).load(this.getId()).getValue();
        if (gifts.isEmpty()) {
            ((GiftData) DatabaseManager.get(GiftData.class)).update(new Pair<>(this, gift));
        } else {
            ((GiftData) DatabaseManager.get(GiftData.class)).update(new Pair<>(this, gifts + ";" + gift));
        }
    }
}