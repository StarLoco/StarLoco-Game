package org.starloco.locos.client;

import org.starloco.locos.database.data.game.QuestProgressData;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.quest.QuestProgress;
import org.starloco.locos.script.proxy.SAccount;
import org.starloco.locos.script.proxy.SPlayer;
import org.starloco.locos.util.Pair;
import org.starloco.locos.command.administration.Group;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.game.BankData;
import org.starloco.locos.database.data.game.GiftData;
import org.starloco.locos.database.data.login.AccountData;
import org.starloco.locos.database.data.login.MountData;
import org.starloco.locos.database.data.login.PlayerData;
import org.starloco.locos.game.GameClient;
import org.starloco.locos.game.world.World;
import org.starloco.locos.hdv.BigStoreListing;
import org.starloco.locos.kernel.Main;
import org.starloco.locos.object.GameObject;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Account {
    private final SAccount scriptVal;
    private final int id;
    private final String name;
    private final String pseudo;
    private final String answer;
    private String currentIp = "";
    private String lastIP = "";
    private String lastConnectionDate = "";
    private long points;
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
    private final List<GameObject> bank = new ArrayList<>();
    private final List<Integer> friends = new ArrayList<>();
    private final List<Integer> enemys = new ArrayList<>();
    private final Map<Integer, List<BigStoreListing>> hdvsItems;
    private final Map<Integer, Map<Integer, QuestProgress>> questsProgression = new HashMap<>();

    public Account(int guid, String name, String pseudo,
                   String answer, boolean banned,
                   String lastIp, String lastConnectionDate, String friends,
                   String enemy, int points, long subscriber, long muteTime, String mutePseudo,
                   String lastVoteIP, String heureVote) {
        this.scriptVal = new SAccount(this);
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

        if (DatabaseManager.get(GiftData.class).load(guid).getSecond() == null)
            DatabaseManager.get(GiftData.class).insert(new Pair<>(this, ""));
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

    public long getPoints() {
        points = DatabaseManager.get(AccountData.class).loadPoints(name);
        return points;
    }

    public boolean modPoints(long d) {
        World.Couple<Long,Boolean> ret = DatabaseManager.get(AccountData.class).modPoints(id, d);
        if(!ret.second) return false;
        points = ret.first;
        return true;
    }


    public void mute(short minutes, String pseudo) {
        if (minutes <= 0) return;
        muteTime = System.currentTimeMillis() + minutes * 60000;
        mutePseudo = pseudo;
        DatabaseManager.get(AccountData.class).update(this);
        if (this.currentPlayer != null) this.currentPlayer.send("Im117;" + pseudo + "~" + minutes);
    }

    public void unMute() {
        if (muteTime == 0) return;
        muteTime = 0;
        mutePseudo = "";
        DatabaseManager.get(AccountData.class).update(this);
    }

    public boolean isMuted() {
        if (muteTime == 0)
            return false;
        if (muteTime >= System.currentTimeMillis())
            return true;
        muteTime = 0;
        mutePseudo = "";
        DatabaseManager.get(AccountData.class).update(this);
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
        DatabaseManager.get(BankData.class).update(this);
    }

    public GameClient getGameClient() {
        return this.gameClient;
    }

    public void setGameClient(GameClient t) {
        this.gameClient = t;
    }

    public Map<Integer, Player> getPlayers() {
        return World.world.getPlayers().stream()
            .filter(Objects::nonNull)
            .filter(player -> player.getAccount() != null && player.getAccount().getId() == this.getId())
            .collect(Collectors.toMap(Player::getId, Function.identity()));
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
        DatabaseManager.get(AccountData.class).update(this);
    }

    public byte getState() {
        return state;
    }

    public void setSubscribe() {
        this.subscriber = DatabaseManager.get(AccountData.class).getSubscribe(this.id);
    }

    public long getSubscribeRemaining() {
        long remaining = this.subscriber - System.currentTimeMillis();
        if (!Config.subscription)
            return remaining > 0L ? remaining : 525600L;
        return Math.max(remaining, 0L);
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

        Account account = World.world.ensureAccountLoaded(id);

        if (account == null) {
            SocketManager.GAME_SEND_MESSAGE(this.currentPlayer, this.currentPlayer.getLang().trans("client.account.addfriend.notexist.account"));
            return;
        }

        Player player = account.getCurrentPlayer(); // Il est arriv� que le personnage soit null alors que ... non !

        if (player == null) {
            SocketManager.GAME_SEND_MESSAGE(this.currentPlayer, this.currentPlayer.getLang().trans("client.account.addfriend.notexist.player"));
            return;
        }

        Group group = player.getGroup();

        if (group != null && !group.isPlayer()) {
            SocketManager.GAME_SEND_MESSAGE(this.currentPlayer, this.currentPlayer.getLang().trans("client.account.addfriend.staff"));
            return;
        }
        if (!this.friends.contains(id)) {
            this.friends.add(id);
            SocketManager.GAME_SEND_FA_PACKET(this.currentPlayer, "K" + account.getPseudo() + player.parseToFriendList(id));
            DatabaseManager.get(AccountData.class).update(this);
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
            DatabaseManager.get(AccountData.class).update(this);
        }
        SocketManager.GAME_SEND_FD_PACKET(this.currentPlayer, "K");
    }

    public boolean isFriendWith(int id) {
        return friends.contains(id);
    }

    public Stream<Integer> getFriendIds() {
        return friends.stream();
    }

    public String parseFriendListToDB() {
        String str = "";
        for (int i : this.friends) {
            if (!str.equalsIgnoreCase(""))
                str += ";";
            str += String.valueOf(i);
        }
        return str;
    }

    public String parseFriendList() {
        StringBuilder str = new StringBuilder();
        if (this.friends.isEmpty())
            return "";
        for (int i : this.friends) {
            Account C = World.world.ensureAccountLoaded(i);
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
            DatabaseManager.get(AccountData.class).update(this);
        } else
            SocketManager.GAME_SEND_iAEA_PACKET(this.currentPlayer);
    }

    public void removeEnemy(int id) {
        if (this.enemys.contains(id)) {
            Iterator<Integer> iterator = this.enemys.iterator();
            while(iterator.hasNext())
                if(iterator.next() == id)
                    iterator.remove();
            DatabaseManager.get(AccountData.class).update(this);
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
            str += String.valueOf(i);
        }
        return str;
    }

    public String parseEnemyList() {
        StringBuilder str = new StringBuilder();
        if (this.enemys.isEmpty())
            return "";
        for (int i : this.enemys) {
            Account C = World.world.ensureAccountLoaded(i);
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

    public List<BigStoreListing> getHdvEntries(int id) {
        return Collections.unmodifiableList(Optional.ofNullable(this.hdvsItems.get(id)).orElse(Collections.emptyList()));
    }

    public int countHdvEntries(int id) {
        return Optional.ofNullable(this.hdvsItems.get(id)).orElse(Collections.emptyList()).size();
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
        DatabaseManager.get(PlayerData.class).updateAllLogged(this.getId(), 0);

        if (player.getExchangeAction() != null)
            GameClient.leaveExchange(player);
        if (player.getParty() != null)
            player.getParty().leave(player);
        if (player.getMount() != null)
            DatabaseManager.get(MountData.class).update(player.getMount());
        if (player.getFight() != null) {
            if (player.getFight().onPlayerDisconnection(player, false)) {
                DatabaseManager.get(PlayerData.class).update(player);
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
            DatabaseManager.get(PlayerData.class).update(character);

        player.resetVars();
        this.resetAllChars();
        DatabaseManager.get(AccountData.class).update(this);
        World.world.logger.info("The player " + player.getName() + " come to disconnect.");
    }

    public void updateVote(String hour, String ip) {
        if (hour.equalsIgnoreCase("")) this.heureVote = 0;
        else this.heureVote = Long.parseLong(hour);
        this.lastVoteIP = ip;
    }

    public void parseBank(int kamas, String items) {
        if (kamas == -1 && items == null) {
            DatabaseManager.get(BankData.class).insert(this);
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
        GiftData gd = DatabaseManager.get(GiftData.class);
        if(gd == null) {
            throw new NullPointerException("GiftData DAO is null");
        }
        String gift = template + "," + quantity + "," + jp;
        String gifts = gd.load(this.getId()).getSecond();
        if (gifts == null || gifts.isEmpty()) {
            gd.update(new Pair<>(this, gift));
        } else {
            gd.update(new Pair<>(this, gifts + ";" + gift));
        }
    }

    public void addQuestProgression(QuestProgress qProgress) {
        questsProgression.computeIfAbsent(qProgress.playerId, k -> new HashMap<>()).put(qProgress.questId, qProgress);
    }

    public void delQuestProgress(QuestProgress qProgress) {
        questsProgression.computeIfAbsent(qProgress.playerId, k -> new HashMap<>()).remove(qProgress.questId, qProgress);

        // Update DB
        QuestProgressData dao = Objects.requireNonNull(DatabaseManager.get(QuestProgressData.class));
        dao.delete(qProgress);
    }

    public QuestProgress getQuestProgress(int playerId, int questId) {
        QuestProgress qp = questsProgression.computeIfAbsent(playerId, k -> new HashMap<>()).get(questId);
        if(qp != null) return qp;
        return questsProgression.computeIfAbsent(QuestProgress.NO_PLAYER_ID, k -> new HashMap<>()).get(questId);
    }

    public Stream<QuestProgress> getQuestProgressions(int playerId) {
        return Stream.of(QuestProgress.NO_PLAYER_ID, playerId)
            .flatMap(id -> questsProgression.computeIfAbsent(id, k -> new HashMap<>()).values().stream());
    }

    public void saveQuestProgress() {
        QuestProgressData dao = Objects.requireNonNull(DatabaseManager.get(QuestProgressData.class));
        this.questsProgression.values().stream().flatMap(m -> m.values().stream()).forEach(dao::update);
    }

    public SAccount scripted() {
        return this.scriptVal;
    }
}