package org.starloco.locos.game;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.LoggerFactory;
import org.starloco.locos.api.AbstractDofusMessage;
import org.starloco.locos.common.CryptManager;
import org.starloco.locos.entity.exchange.NpcExchange;
import org.starloco.locos.entity.map.*;
import org.starloco.locos.game.action.type.BigStoreActionData;
import org.starloco.locos.game.action.type.DocumentActionData;
import org.starloco.locos.game.action.type.NpcDialogActionData;
import org.starloco.locos.game.action.type.ScenarioActionData;
import org.starloco.locos.hdv.BigStore;
import org.starloco.locos.hdv.BigStoreListingLotSize;
import org.starloco.locos.script.DataScriptVM;
import org.starloco.locos.util.Pair;
import org.apache.mina.core.session.IoSession;
import org.starloco.locos.area.Area;
import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.auction.AuctionManager;
import org.starloco.locos.client.Account;
import org.starloco.locos.client.Player;
import org.starloco.locos.client.other.Party;
import org.starloco.locos.command.CommandAdmin;
import org.starloco.locos.command.CommandPlayer;
import org.starloco.locos.command.administration.AdminUser;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.common.PathFinding;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.game.*;
import org.starloco.locos.database.data.login.*;
import org.starloco.locos.entity.Collector;
import org.starloco.locos.entity.Prism;
import org.starloco.locos.entity.exchange.CraftSecure;
import org.starloco.locos.entity.exchange.Exchange;
import org.starloco.locos.entity.exchange.PlayerExchange;
import org.starloco.locos.entity.mount.Mount;
import org.starloco.locos.entity.npc.Npc;
import org.starloco.locos.entity.npc.NpcTemplate;
import org.starloco.locos.entity.pet.Pet;
import org.starloco.locos.entity.pet.PetEntry;
import org.starloco.locos.event.EventManager;
import org.starloco.locos.event.type.Event;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.spells.Spell;
import org.starloco.locos.game.action.ExchangeAction;
import org.starloco.locos.game.action.GameAction;
import org.starloco.locos.game.world.World;
import org.starloco.locos.game.world.World.Couple;
import org.starloco.locos.guild.Guild;
import org.starloco.locos.guild.GuildMember;
import org.starloco.locos.hdv.BigStoreListing;
import org.starloco.locos.job.Job;
import org.starloco.locos.job.JobAction;
import org.starloco.locos.job.JobConstant;
import org.starloco.locos.job.JobStat;
import org.starloco.locos.job.maging.BreakingObject;
import org.starloco.locos.job.maging.Rune;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.kernel.Logging;
import org.starloco.locos.kernel.Main;
import org.starloco.locos.lang.LangEnum;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.object.ObjectTemplate;
import org.starloco.locos.object.entity.Fragment;
import org.starloco.locos.object.entity.SoulStone;
import org.starloco.locos.util.TimerWaiter;
import org.starloco.locos.util.generator.NameGenerator;

public class GameClient {

    private final IoSession session;
    private Account account;
    private Player player;
    private boolean walk = false;
    private AdminUser adminUser;
    private LangEnum language = LangEnum.ENGLISH;
    private final Map<Integer, GameAction> actions = new HashMap<>();
    public long timeLastTradeMsg = 0, timeLastRecrutmentMsg = 0, timeLastAlignMsg = 0, timeLastChatMsg = 0, timeLastIncarnamMsg = 0, timeLastTaverne, lastPacketTime = 0, action = 0;

    private String preparedKeys;

    public GameClient(IoSession session) {
        this.session = session;
        this.session.write("HG");
    }
    
    public IoSession getSession() {
        return session;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Account getAccount() {
        return account;
    }

    public LangEnum getLanguage() {
        return language;
    }

    public String getPreparedKeys(){
        return preparedKeys;
    }

    public void parsePacket(String packet) throws InterruptedException {
        this.lastPacketTime = System.currentTimeMillis();

        if (packet.length() > 3 && packet.substring(0, 4).equalsIgnoreCase("ping")) {
            this.send("pong");
            return;
        }
        if(Logging.USE_LOG) {
            if (this.player != null) {
                Logging.getInstance().write("RecvPacket", this.player.getName() + " : " + this.player.getAccount().getCurrentIp() + " : " + packet);
            } else {
                String IP = ((InetSocketAddress) (this.getSession().getRemoteAddress())).getAddress().getHostAddress();
                Logging.getInstance().write("RecvPacket", IP + " : " + packet);
            }
        }

        if(Config.modeEvent) {
            EventManager manager = EventManager.getInstance();
            if(manager.getState() == EventManager.State.STARTED) {
                Event event = manager.getCurrentEvent();
                try {
                    if (event != null && event.onReceivePacket(manager, this.player, packet)) {
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    player.sendMessage(player.getLang().trans("game.gameclient.event.error"));
                }
            }
        }
        if(packet.isEmpty()) return;

        switch (packet.charAt(0)) {
            case 'ù':
                // Ignore for now
                break;
            case 'A':
                parseAccountPacket(packet);
                break;
            case 'H':
                parseAuthPackets(packet);
                break;
            case 'B':
                parseBasicsPacket(packet);
                break;
            case 'C':
                parseConquestPacket(packet);
                break;
            case 'c':
                parseChanelPacket(packet);
                break;
            case 'D':
                parseDialogPacket(packet);
                break;
            case 'd':
                parseDocumentPacket(packet);
                break;
            case 'E':
                parseExchangePacket(packet);
                break;
            case 'e':
                parseEnvironementPacket(packet);
                break;
            case 'F':
                parseFrienDDacket(packet);
                break;
            case 'f':
                parseFightPacket(packet);
                break;
            case 'G':
                parseGamePacket(packet);
                break;
            case 'g':
                parseGuildPacket(packet);
                break;
            case 'h':
                parseHousePacket(packet);
                break;
            case 'i':
                parseEnemyPacket(packet);
                break;
            case 'J':
                parseJobOption(packet);
                break;
            case 'K':
                parseHouseKodePacket(packet);
                break;
            case 'O':
                parseObjectPacket(packet);
                break;
            case 'P':
                parseGroupPacket(packet);
                break;
            case 'R':
                parseMountPacket(packet);
                break;
            case 'Q':
                parseQuestData(packet);
                break;
            case 'S':
                parseSpellPacket(packet);
                break;
            case 'T':
                parseTutorialsPacket(packet);
                break;
            case 'W':
                parseWaypointPacket(packet);
                break;
            default:
                if(this.player != null)
                    if(this.player.isChangeName())
                        this.changeName(packet);                
                break;
        }
    }

    /**
     * AccountPacket *
     */
    private void parseAccountPacket(String packet) {
        switch (packet.charAt(1)) {
            case 'A':
                addCharacter(packet);
                break;
            case 'B':
                boost(packet);
                break;
            case 'D':
                deleteCharacter(packet);
                break;
            case 'f':
                getQueuePosition();
                break;
            case 'g':
                getGifts(packet.substring(2));
                break;
            case 'G':
                attributeGiftToCharacter(packet.substring(2));
                break;
            case 'i':
                sendIdentity(packet);
                break;
            case 'k':
                //setKeyIndex(Byte.parseByte(packet.substring(2), 16));
                break;
            case 'L':
                getCharacters(/*(packet.length() == 2)*/);
                break;
            case 'R':
                hardcodeRevive(Integer.parseInt(packet.substring(2)));
                break;
            case 'S':
                setCharacter(packet);
                break;
            case 'T':
                parseTicket(packet);
                break;
            case 'V':
                requestRegionalVersion();
                break;
            case 'P':
                String name = NameGenerator.nameGenerator
                        .compose((int)(
                                Math.random() * 3 +
                                        Formulas.getRandomValue(1, 5)));
                SocketManager.send(this, "APK" + name);
                break;
            case 'E':
                mimibiote(packet);
                break;
        }
    }

    private void switchCharacter() {
        // 1.39.8 Switch character
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(30); // TODO Make that a config
        String jws = Jwts.builder()
                .setIssuer("StarLocoGameServer")
                .setSubject(String.valueOf(account.getName()))
                .claim("ip", account.getCurrentIp())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .signWith(
                    Keys.hmacShaKeyFor(Base64.getDecoder().decode(Config.exchangeKey)),
                    SignatureAlgorithm.HS256
                )
                .compact();

        send("HS"+jws);
    }

    private void parseAuthPackets(String packet) {
        switch (packet.charAt(1)) {
            case 'S':
                switchCharacter();
                break;
            default:
                break;
        }
    }

    private void mimibiote(String packet) {
        //TODO: Implement
        if (packet.charAt(2) == 'i') {
            if (packet.charAt(3) == '1') {
                createMimibiote(packet);
            } else if (packet.charAt(3) == '0') {
                dissociateMimibiote(packet);
            }
        }
    }

    private void createMimibiote(String packet)
    {
        if(this.player.getFight() != null) return;
        final String[] datas = packet.split("\\|");
        if(datas.length < 3) return;

        int idItemToKeep;
        int idItemToDelete;

        try {
            idItemToKeep = Integer.parseInt(datas[1]);
            idItemToDelete = Integer.parseInt(datas[2]);
        }catch(NumberFormatException e) {
            return;
        }

        final GameObject mimibiote = this.player.getItemTemplate(Constant.ID_TEMPLATE_MIMIBIOTE);
        if(mimibiote == null) return;

        final GameObject itemToKeep = World.world.getGameObject(idItemToKeep);
        final GameObject itemToDelete = World.world.getGameObject(idItemToDelete);

        if(itemToKeep == null || itemToDelete == null) return;
        if(!this.player.hasItemGuid(idItemToKeep) || !this.player.hasItemGuid(idItemToDelete)) return;
        if(itemToDelete.getPosition() != Constant.ITEM_POS_NO_EQUIPED || itemToKeep.getPosition() != Constant.ITEM_POS_NO_EQUIPED) return;
        if(itemToKeep.isMimibiote() || itemToDelete.isMimibiote()) return;
        if(itemToKeep.getTemplate().getLevel() < itemToDelete.getTemplate().getLevel()) return;
        if(itemToKeep.getTemplate().getType() != itemToDelete.getTemplate().getType()) return;
        if(!Constant.isTypeForMimibiote(itemToKeep.getTemplate().getType())) return;


        // OK
        final String guid = Integer.toHexString(itemToDelete.getGuid());
        final String id = Integer.toHexString(itemToDelete.getTemplate().getId());
        itemToKeep.addTxtStat(Constant.STATS_MIMIBIOTE, guid+";"+id); // setModification est dedans
        this.player.removeItem(idItemToDelete, 1, true, false);
        this.player.removeItem(mimibiote.getGuid(), 1, true, true);
        SocketManager.GAME_SEND_UPDATE_ITEM(player, itemToKeep);
        SocketManager.GAME_SEND_Im_PACKET(this.player, "022;" + 1 + "~" + itemToDelete.getTemplate().getId());
        SocketManager.GAME_SEND_Im_PACKET(this.player, "022;" + 1 + "~" + mimibiote.getTemplate().getId());

    }

    private void dissociateMimibiote(String packet)
    {
        if(this.player.getFight() != null) return;
        final String[] datas = packet.split("\\|");
        if(datas.length < 2) return;

        int idItem;

        try {
            idItem = Integer.parseInt(datas[1]);
        }catch(NumberFormatException e) {
            return;
        }

        final GameObject item = World.world.getGameObject(idItem);
        if(item == null) return;
        if(!this.player.hasItemGuid(idItem)) return;
        if(!item.isMimibiote()) return;

        final GameObject mimibiote = World.world.getObjTemplate(Constant.ID_TEMPLATE_MIMIBIOTE).createNewItem(1, false);
        final int idApparat = Integer.parseInt(item.getTxtStat().get(Constant.STATS_MIMIBIOTE).split(";")[0], 16);
        final GameObject apparat = World.world.getGameObject(idApparat);

        if(apparat == null)
        {
            this.player.sendMessage("Merci de contacter un administrateur. L'objet avec comme ID " + idApparat + " a disparu ...");
            return;
        }

        if(this.player.addItem(mimibiote, true, false))
            World.world.addGameObject(mimibiote);
        this.player.addItem(apparat, true, false);
        item.getTxtStat().remove(Constant.STATS_MIMIBIOTE); // setModification est dedans
        SocketManager.GAME_SEND_UPDATE_ITEM(player, item);
        if(item.getPosition() != Constant.ITEM_POS_NO_EQUIPED)
            SocketManager.GAME_SEND_ON_EQUIP_ITEM(this.player.getCurMap(), this.player);

        SocketManager.GAME_SEND_Im_PACKET(this.player, "021;" + 1 + "~" + apparat.getTemplate().getId());
        SocketManager.GAME_SEND_Im_PACKET(this.player, "021;" + 1 + "~" + mimibiote.getTemplate().getId());
    }

    private void addCharacter(String packet) {
        String[] infos = packet.substring(2).split("\\|");
        if (DatabaseManager.get(PlayerData.class).exist(infos[0])) {
            SocketManager.GAME_SEND_NAME_ALREADY_EXIST(this);
            return;
        }
        //Validation du nom du this.playernnage
        boolean isValid = true;
        String name = infos[0].toLowerCase();
        //V?rifie d'abord si il contient des termes d?finit
        if (name.length() > 20 || name.length() < 3 || name.contains("modo")
                || name.contains("admin") || name.contains("putain")
                || name.contains("administrateur") || name.contains("puta")) {
            isValid = false;
        }

        //Si le nom passe le test, on v?rifie que les caract?re entr? sont correct.
        if (isValid) {
            int tiretCount = 0;
            char exLetterA = ' ';
            char exLetterB = ' ';
            for (char curLetter : name.toCharArray()) {
                if (!((curLetter >= 'a' && curLetter <= 'z') || curLetter == '-')) {
                    isValid = false;
                    break;
                }
                if (curLetter == exLetterA && curLetter == exLetterB) {
                    isValid = false;
                    break;
                }
                if (curLetter >= 'a') {
                    exLetterA = exLetterB;
                    exLetterB = curLetter;
                }
                if (curLetter == '-') {
                    if (tiretCount >= 6) {
                        isValid = false;
                        break;
                    } else {
                        tiretCount++;
                    }
                }
            }
        }
        //Si le nom est invalide
        if (!isValid) {
            SocketManager.GAME_SEND_NAME_ALREADY_EXIST(this);
            return;
        }
        if (this.account.getPlayers().size() >= 5) {
            SocketManager.GAME_SEND_CREATE_PERSO_FULL(this);
            return;
        }
        if (this.account.createPlayer(infos[0], Integer.parseInt(infos[2]), Integer.parseInt(infos[1]), Integer.parseInt(infos[3]), Integer.parseInt(infos[4]), Integer.parseInt(infos[5]))) {
            SocketManager.GAME_SEND_CREATE_OK(this);
            SocketManager.GAME_SEND_PERSO_LIST(this, this.account.getPlayers(), this.account.getSubscribeRemaining());
        } else {
            SocketManager.GAME_SEND_CREATE_FAILED(this);
        }
    }

    private void boost(String packet) {
        try {
            if (this.player.getMorphMode()) {
                this.player.sendMessage(this.player.getLang().trans("game.gameclient.boost.incarne"));
                return;
            }

            String[] packetSplit = packet.split(Pattern.quote("|"));
            boolean isMultiple = packetSplit.length > 1;
            if(isMultiple){
                int quantity = Integer.parseInt(packetSplit[1]);
                int stat = Integer.parseInt(packetSplit[0].substring(2));
                this.player.boostStatFixedCount(stat, quantity);
            } else {
                int stat = Integer.parseInt(packet.substring(2).split("/u000A")[0]);
                this.player.boostStat(stat, true);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void deleteCharacter(String packet) {
        String[] split = packet.substring(2).split("\\|");
        int GUID = Integer.parseInt(split[0]);
        String answer = split.length > 1 ? split[1] : "";
        if (this.account.getPlayers().containsKey(GUID) && !this.account.getPlayers().get(GUID).isOnline()) {
            if (this.account.getPlayers().get(GUID).getLevel() < 20 || (this.account.getPlayers().get(GUID).getLevel() >= 20 && answer.equals(this.account.getAnswer().replace(" ", "%20")))) {
                this.account.deletePlayer(GUID);
                SocketManager.GAME_SEND_PERSO_LIST(this, this.account.getPlayers(), this.account.getSubscribeRemaining());
            } else {
                SocketManager.GAME_SEND_DELETE_PERSO_FAILED(this);
            }
        } else {
            SocketManager.GAME_SEND_DELETE_PERSO_FAILED(this);
        }
    }

    private void getQueuePosition() {
        SocketManager.MULTI_SEND_Af_PACKET(this, 1, 1, 1, 1);
        //SocketManager.MULTI_SEND_Af_PACKET(this, this.queuePlace.getPlace(), QueueThreadPool.executor.getQueue().size(), 0, 1);
    }

    private void getGifts(String packet) {
        for(LangEnum lang : LangEnum.values()) {
            if (lang.getFlag().equals(packet.toLowerCase())) {
                this.language = lang;
                break;
            }
        }
        if(this.language == null) this.language = LangEnum.ENGLISH;

        String gifts = DatabaseManager.get(GiftData.class).load(this.account.getId()).second;
        if (gifts == null)
            return;
        if (!gifts.isEmpty()) {
            String data = "";
            int item = -1;
            for (String object : gifts.split(";")) {
                if(object.isEmpty()) continue;
                int id = Integer.parseInt(object.split(",")[0]), qua = Integer.parseInt(object.split(",")[1]);
                ObjectTemplate template = World.world.getObjTemplate(id);
                if(template != null) {
                    if (data.isEmpty()) {
                        data = "1~" + Integer.toString(id, 16) + "~" + Integer.toString(qua, 16) + "~~" + template.getStrTemplate();
                    } else {
                        data += ";1~" + Integer.toString(id, 16) + "~" + Integer.toString(qua, 16) + "~~" + template.getStrTemplate();
                    }
                    if (item == -1) item = id;
                } else {
                    System.err.println("ERROR BOUTIQUE TEMPLATE OBJECT NOT FOUND : " + id);
                }
            }
            SocketManager.GAME_SEND_Ag_PACKET(this, item, data);
        }
    }

    private void attributeGiftToCharacter(String packet) {
        String[] infos = packet.split("\\|");

        int template = Integer.parseInt(infos[0]);
        Player player = World.world.getPlayer(Integer.parseInt(infos[1]));

        if (player == null)
            return;

        String gifts = DatabaseManager.get(GiftData.class).load(this.account.getId()).getSecond();

        if (gifts.isEmpty())
            return;

        for (String data : gifts.split(";")) {
            if(data.isEmpty()) continue;
            String[] split = data.split(",");
            int id = Integer.parseInt(split[0]);

            if (id == template) {
                int qua = Integer.parseInt(split[1]), jp = Integer.parseInt(split[2]);
                GameObject obj;

                List<Integer> objNeedAttach = Arrays.asList(26001, 26002, 26003, 26004, 26005);
                if (qua == 1) {
                    obj = World.world.getObjTemplate(template).createNewItem(qua, (jp == 1));
                    if (objNeedAttach.contains(obj.getTemplate().getId()))
                        obj.attachToPlayer(player);
                    if (player.addItem(obj, true, false))
                        World.world.addGameObject(obj);
                    if(obj.getTemplate().getType() == Constant.ITEM_TYPE_CERTIF_MONTURE)
                        obj.setMountStats(player, null, true).setToMax();
                    String str1 = id + "," + qua + "," + jp, str2 = id + "," + qua + "," + jp + ";", str3 = ";" + id + "," + qua + "," + jp;

                    gifts = gifts.replace(str2, "").replace(str3, "").replace(str1, "");
                } else {
                    obj = World.world.getObjTemplate(template).createNewItem(1, (jp == 1));
                    if (objNeedAttach.contains(obj.getTemplate().getId()))
                        obj.attachToPlayer(player);
                    if (player.addItem(obj, true, false))
                        World.world.addGameObject(obj);
                    if(obj.getTemplate().getType() == Constant.ITEM_TYPE_CERTIF_MONTURE)
                        obj.setMountStats(player, null, true).setToMax();

                    String str1 = id + "," + qua + "," + jp, str2 = id + "," + qua + "," + jp + ";", str3 = ";" + id + "," + qua + "," + jp;
                    String cstr1 = id + "," + (qua - 1) + "," + jp, cstr2 = id + "," + (qua - 1) + "," + jp + ";", cstr3 = ";" + id + "," + (qua - 1) + "," + jp;

                    gifts = gifts.replace(str2, cstr2).replace(str3, cstr3).replace(str1, cstr1);
                }
                DatabaseManager.get(GiftData.class).update(new Pair<>(this.account, gifts));
            }
        }

        DatabaseManager.get(PlayerData.class).update(player);

        if (gifts.isEmpty())
            player.send("AG");
        else {
            this.getGifts("");
            player.send("AG");
        }
    }

    private void sendIdentity(String packet) {}

    private void getCharacters() {
        this.account.setGameClient(this);
        for (Player player : this.account.getPlayers().values()) {
            if (player != null)
                if (player.getFight() != null && player.getFight().getFighterByPerso(player) != null) {
                    this.player = player;
                    this.player.OnJoinGame();
                    return;
                }
        }

        SocketManager.GAME_SEND_PERSO_LIST(this, this.account.getPlayers(), this.account.getSubscribeRemaining());
    }

    private void hardcodeRevive(int id) {
        final Player player = this.account.getPlayers().get(id);

        this.getSession().write("BN");

        if(player != null) {
            player.revive();
            SocketManager.GAME_SEND_PERSO_LIST(this, this.account.getPlayers(), this.account.getSubscribeRemaining());
        } else {
            this.getSession().write("BN");
        }
    }

    private void setCharacter(String packet) {
        int id = Integer.parseInt(packet.substring(2));

        if (this.account.getPlayers().get(id) != null) {
            this.player = this.account.getPlayers().get(id);
            if (this.player != null) {
                if(this.player.isDead() == 1 && Config.modeHeroic)
                    this.getSession().write("BN");
                else
                    this.player.OnJoinGame();
                return;
            }
        }
        SocketManager.GAME_SEND_PERSO_SELECTION_FAILED(this);
    }

    private void parseTicket(String packet) {
        try {
            int id = Integer.parseInt(packet.substring(2));
            this.account = Config.gameServer.getWaitingAccount(id);

            if (this.account == null) {
                SocketManager.GAME_SEND_ATTRIBUTE_FAILED(this);
                this.kick();
            } else {
                Config.gameServer.deleteWaitingAccount(this.account);

                String ip = this.session.getRemoteAddress().toString().substring(1).split(":")[0];
                Fight fight = null;
                for(Player p : account.getPlayers().values())
                    if((fight = p.getFight()) != null)
                        break;
                if(fight == null && Config.limitByIp != -1 && new ArrayList<>(World.world.getOnlinePlayers()).stream().filter(p -> p != null &&
                        p.getAccount() != null && p.getAccount().getCurrentIp().equalsIgnoreCase(ip))
                        .count() >= Config.limitByIp) {
                    this.send("M034|" + Config.limitByIp + ";" + ip);
                    this.kick();
                    return;
                }

                if(this.account.getGameClient() != null)
                    this.account.getGameClient().kick();

                this.account.setGameClient(this);
                this.account.setCurrentIp(ip);
                DatabaseManager.get(AccountData.class).setLogged(this.account.getId(), 1);

                if (Logging.USE_LOG) Logging.getInstance().write("AccountIpConnect", this.account.getName() + " > " + ip);

                if(Config.encryption){
                    //String key = generateKey();
                    this.getSession().write("ATK18fd8ad4a38cdd0432248a76f8f148ceb");
                    this.preparedKeys = World.world.getCryptManager().prepareKey("8fd8ad4a38cdd0432248a76f8f148ceb");
                } else {
                    this.getSession().write("ATK0");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            SocketManager.GAME_SEND_ATTRIBUTE_FAILED(this);
            this.kick();
        }
    }

    private void requestRegionalVersion() {
        SocketManager.GAME_SEND_AV0(this);
    }
    /** Fin Account Packet **/

    /**
     * Basics Packet *
     */
    private void parseBasicsPacket(String packet) throws InterruptedException {
        switch (packet.charAt(1)) {
            case 'A'://Console
                authorisedCommand(packet);
                break;
            case 'D':
                getDate();
                break;
            case 'M':
                tchat(packet);
                break;
            case 'W': // Whois
                whoIs(packet);
                break;
            case 'S':
                this.player.useSmiley(packet.substring(2));
                break;
            case 'Y':
                chooseState(packet);
                break;
            case 'a':
                if (packet.charAt(2) == 'M')
                    goToMap(packet);
                break;
        }
    }

    private void authorisedCommand(String packet) {
        if (this.adminUser == null) this.adminUser = new CommandAdmin(this.player);
        if (this.player.getGroup() == null || this.getPlayer() == null) {
            this.getAccount().getGameClient().kick();
            return;
        }

        if (Logging.USE_LOG)
            Logging.getInstance().write("CommandAdmin", this.getAccount().getCurrentIp() + " : " + this.getAccount().getName() + " > " + this.getPlayer().getName() + " > " + packet.substring(2));

        this.adminUser.apply(packet);
    }

    private void getDate() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());

        this.send("BD" + calendar.get(Calendar.YEAR) + "|" + calendar.get(Calendar.MONTH) + "|" + calendar.get(Calendar.DAY_OF_MONTH));
        this.send("BT" + (calendar.getTime().getTime() + 3600000));
    }

    private void tchat(String packet) {
        String msg;
        String lastMsg = "";

        if (this.player.getAccount() != null && this.player.isMuted()) {
            short remaining = (short) ((this.getAccount().getMuteTime() - System.currentTimeMillis()) / 60000);
            this.player.send("Im117;" + this.getAccount().getMutePseudo() + "~" + remaining);
            return;
        }

        if (this.player.getCurMap() != null) {
            if (this.player.getCurMap().isMute() && this.player.getGroup() == null) {
                this.player.sendServerMessage("The map is currently mute.");
                return;
            }
        }

        packet = packet.replace("<", "");
        packet = packet.replace(">", "");
        if (packet.length() < 6)
            return;

        switch (packet.charAt(2)) {
            case '¤':// Unknow
                break;
            case '*'://Defaut
                if (System.currentTimeMillis() - timeLastChatMsg < 500) {
                    this.send("M10");
                    return;
                }
                timeLastChatMsg = System.currentTimeMillis();
                if (!this.player.get_canaux().contains(packet.charAt(2) + ""))
                    return;

                msg = packet.split("\\|", 2)[1];
                if (CommandPlayer.analyse(this.player, msg)) {
                    this.player.send("BN");
                    return;
                }
                if (msg.equals(lastMsg)) {
                    SocketManager.GAME_SEND_Im_PACKET(this.player, "184");
                    return;
                }
                if (this.player.isSpec() && this.player.getFight() != null) {
                    int team = this.player.getFight().getTeamId(this.player.getId());
                    if (team == -1)
                        return;
                    SocketManager.GAME_SEND_cMK_PACKET_TO_FIGHT(this.player.getFight(), team, "#", this.player.getId(), this.player.getName(), msg);
                    return;
                }
                if (Logging.USE_LOG)
                    Logging.getInstance().write("DefaultMessage", this.player.getName() + " > Map " + this.player.getCurMap().getId() + " > " + msg);
                if (this.player.getObjetByPos(Constant.ITEM_POS_ROLEPLAY_BUFF) != null)
                    if (this.player.getObjetByPos(Constant.ITEM_POS_ROLEPLAY_BUFF).getTemplate().getId() == 10844)
                        msg = Formulas.translateMsg(msg);
                if (this.player.getFight() == null) {
                    SocketManager.GAME_SEND_cMK_PACKET_TO_MAP(this.player.getCurMap(), "", this.player.getId(), this.player.getName(), msg);
                    // Broken: AuctionManager.getInstance().onPlayerChat(this.player, msg);
                } else
                    SocketManager.GAME_SEND_cMK_PACKET_TO_FIGHT(this.player.getFight(), 7, "", this.player.getId(), this.player.getName(), msg);
                break;
            case '^':// Canal Incarnam
                msg = packet.split("\\|", 2)[1];
                long x;
                if ((x = System.currentTimeMillis() - timeLastIncarnamMsg) < 30000) {
                    x = (30000 - x) / 1000;//Chat antiflood
                    SocketManager.GAME_SEND_Im_PACKET(this.player, "0115;" + ((int) Math.ceil(x) + 1));
                    return;
                }
                if (msg.equals(lastMsg)) {
                    SocketManager.GAME_SEND_Im_PACKET(this.player, "184");
                    return;
                }

                timeLastIncarnamMsg = System.currentTimeMillis();
                msg = packet.split("\\|", 2)[1];
                lastMsg = msg;
                if (this.player.getObjetByPos(Constant.ITEM_POS_ROLEPLAY_BUFF) != null)
                    if (this.player.getObjetByPos(Constant.ITEM_POS_ROLEPLAY_BUFF).getTemplate().getId() == 10844)
                        msg = Formulas.translateMsg(msg);
                SocketManager.GAME_SEND_cMK_PACKET_INCARNAM_CHAT("^", this.player.getId(), this.player.getName(), msg);

                break;
            case '#'://Canal Equipe
                if (!this.player.get_canaux().contains(packet.charAt(2) + ""))
                    return;
                if (this.player.getFight() != null) {
                    msg = packet.split("\\|", 2)[1];
                    int team = this.player.getFight().getTeamId(this.player.getId());
                    if (team == -1)
                        return;
                    if (Logging.USE_LOG)
                        Logging.getInstance().write("TeamMessage", this.player.getName()
                                + " > " + this.player.getFight() + " > " + msg);
                    if (this.player.getObjetByPos(Constant.ITEM_POS_ROLEPLAY_BUFF) != null)
                        if (this.player.getObjetByPos(Constant.ITEM_POS_ROLEPLAY_BUFF).getTemplate().getId() == 10844)
                            msg = Formulas.translateMsg(msg);
                    SocketManager.GAME_SEND_cMK_PACKET_TO_FIGHT(this.player.getFight(), team, "#", this.player.getId(), this.player.getName(), msg);
                }
                break;

            case '$'://Canal groupe
                if (!this.player.get_canaux().contains(packet.charAt(2) + ""))
                    return;
                if (this.player.getParty() == null)
                    break;
                msg = packet.split("\\|", 2)[1];
                if (Logging.USE_LOG)
                    Logging.getInstance().write("PartyMessage", this.player.getName()
                            + " > " + this.player.getParty() + " > " + msg);
                if (this.player.getObjetByPos(Constant.ITEM_POS_ROLEPLAY_BUFF) != null)
                    if (this.player.getObjetByPos(Constant.ITEM_POS_ROLEPLAY_BUFF).getTemplate().getId() == 10844)
                        msg = Formulas.translateMsg(msg);
                SocketManager.GAME_SEND_cMK_PACKET_TO_GROUP(this.player.getParty(), "$", this.player.getId(), this.player.getName(), msg);
                break;

            case ':'://Canal commerce
                if (!this.player.get_canaux().contains(packet.charAt(2) + ""))
                    return;
                long l;
                if (this.player.isMissingSubscription()) {
                    SocketManager.GAME_SEND_EXCHANGE_REQUEST_ERROR(this.player.getGameClient(), 'S');
                    return;
                }
                if (this.player.cantCanal()) {
                    SocketManager.GAME_SEND_MESSAGE(this.player, "Vous n'avez pas la permission de parler dans ce canal !", "B9121B");
                } else if (this.player.isInPrison()) {
                    SocketManager.GAME_SEND_MESSAGE(this.player, "Vous ?tes en prison, impossible de parler dans ce canal !", "B9121B");
                } else {
                    if (this.player.getGroup() == null) {
                        if ((l = System.currentTimeMillis() - timeLastTradeMsg) < 50000) {
                            l = (50000 - l) / 1000;//On calcul la diff?rence en secondes
                            SocketManager.GAME_SEND_Im_PACKET(this.player, "0115;"
                                    + ((int) Math.ceil(l) + 1));
                            return;
                        }
                    }
                    timeLastTradeMsg = System.currentTimeMillis();
                    msg = packet.split("\\|", 2)[1];
                    if (Logging.USE_LOG)
                        Logging.getInstance().write("TradeMessage", this.player.getName() + " > " + msg);
                    if (this.player.getObjetByPos(Constant.ITEM_POS_ROLEPLAY_BUFF) != null)
                        if (this.player.getObjetByPos(Constant.ITEM_POS_ROLEPLAY_BUFF).getTemplate().getId() == 10844)
                            msg = Formulas.translateMsg(msg);
                    SocketManager.GAME_SEND_cMK_PACKET_TO_ALL(this.player, ":", this.player.getId(), this.player.getName(), msg);
                }
                break;
            case '@'://Canal Admin
                if (this.player.getGroup() == null)
                    return;
                msg = packet.split("\\|", 2)[1];
                if (Logging.USE_LOG)
                    Logging.getInstance().write("AdminMessage", this.player.getName()
                            + " > " + msg);
                SocketManager.GAME_SEND_cMK_PACKET_TO_ADMIN("@", this.player.getId(), this.player.getName(), msg);
                break;
            case '?'://Canal recrutement
                if (!this.player.get_canaux().contains(packet.charAt(2) + ""))
                    return;
                if (this.player.isMissingSubscription()) {
                    SocketManager.GAME_SEND_EXCHANGE_REQUEST_ERROR(this.player.getGameClient(), 'S');
                    return;
                }
                long j;
                if (this.player.cantCanal()) {
                    SocketManager.GAME_SEND_MESSAGE(this.player, "Vous n'avez pas la permission de parler dans ce canal !", "B9121B");
                } else if (this.player.isInPrison()) {
                    SocketManager.GAME_SEND_MESSAGE(this.player, "Vous ?tes en prison, impossible de parler dans ce canal !", "B9121B");
                } else {
                    if (this.player.getGroup() == null) {
                        if ((j = System.currentTimeMillis()
                                - timeLastRecrutmentMsg) < 40000) {
                            j = (40000 - j) / 1000;//On calcul la diff?rence en secondes
                            SocketManager.GAME_SEND_Im_PACKET(this.player, "0115;"
                                    + ((int) Math.ceil(j) + 1));
                            return;
                        }
                    }
                    timeLastRecrutmentMsg = System.currentTimeMillis();
                    msg = packet.split("\\|", 2)[1];
                    if (Logging.USE_LOG)
                        Logging.getInstance().write("RecruitmentMessage", this.player.getName()
                                + " > " + msg);
                    if (this.player.getObjetByPos(Constant.ITEM_POS_ROLEPLAY_BUFF) != null)
                        if (this.player.getObjetByPos(Constant.ITEM_POS_ROLEPLAY_BUFF).getTemplate().getId() == 10844)
                            msg = Formulas.translateMsg(msg);
                    SocketManager.GAME_SEND_cMK_PACKET_TO_ALL(this.player, "?", this.player.getId(), this.player.getName(), msg);
                }
                break;
            case '%'://Canal guilde
                if (!this.player.get_canaux().contains(packet.charAt(2) + ""))
                    return;
                if (this.player.getGuild() == null)
                    return;
                msg = packet.split("\\|", 2)[1];
                if (Logging.USE_LOG)
                    Logging.getInstance().write("GuildMessage", this.player.getName()
                            + " > " + this.player.getGuild().getName() + " > " + msg);
                if (this.player.getObjetByPos(Constant.ITEM_POS_ROLEPLAY_BUFF) != null)
                    if (this.player.getObjetByPos(Constant.ITEM_POS_ROLEPLAY_BUFF).getTemplate().getId() == 10844)
                        msg = Formulas.translateMsg(msg);
                SocketManager.GAME_SEND_cMK_PACKET_TO_GUILD(this.player.getGuild(), "%", this.player.getId(), this.player.getName(), msg);
                break;
            case '!'://Alignement
                if (!this.player.get_canaux().contains(packet.charAt(2) + ""))
                    return;
                if (this.player.isMissingSubscription()) {
                    SocketManager.GAME_SEND_EXCHANGE_REQUEST_ERROR(this.player.getGameClient(), 'S');
                    return;
                }
                if (this.player.getAlignment() == 0)
                    return;
                if (this.player.getDeshonor() >= 1) {
                    SocketManager.GAME_SEND_Im_PACKET(this.player, "183");
                    return;
                }
                long k;
                if ((k = System.currentTimeMillis() - timeLastAlignMsg) < 30000) {
                    k = (30000 - k) / 1000;//On calcul la diff?rence en secondes
                    SocketManager.GAME_SEND_Im_PACKET(this.player, "0115;" + ((int) Math.ceil(k) + 1));
                    return;
                }
                timeLastAlignMsg = System.currentTimeMillis();
                msg = packet.split("\\|", 2)[1];
                if (Logging.USE_LOG)
                    Logging.getInstance().write("AlignMessage", this.player.getName()
                            + " > " + msg);
                if (this.player.getObjetByPos(Constant.ITEM_POS_ROLEPLAY_BUFF) != null)
                    if (this.player.getObjetByPos(Constant.ITEM_POS_ROLEPLAY_BUFF).getTemplate().getId() == 10844)
                        msg = Formulas.translateMsg(msg);
                SocketManager.GAME_SEND_cMK_PACKET_TO_ALIGN("!", this.player.getId(), this.player.getName(), msg, this.player);
                break;
            default:
                String nom = packet.substring(2).split("\\|")[0];
                msg = packet.split("\\|", 2)[1];
                if (!(nom.length() <= 1)) {
                    Player target = World.world.getPlayerByName(nom);
                    if (target == null || target.getAccount() == null || target.getGameClient() == null) {
                        SocketManager.GAME_SEND_CHAT_ERROR_PACKET(this, nom);
                        return;
                    }
                    if (target.getAccount().isEnemyWith(this.player.getAccount().getId()) || !target.isDispo(this.player)) {
                        SocketManager.GAME_SEND_Im_PACKET(this.player, "114;" + target.getName());
                        return;
                    }
                    if (msg.equals(lastMsg)) {
                        SocketManager.GAME_SEND_Im_PACKET(this.player, "184");
                        return;
                    }
                    if (this.player.getGroup() == null && target.isInvisible()) {
                        SocketManager.GAME_SEND_CHAT_ERROR_PACKET(this, nom);
                        return;
                    }
                    if (target.mpToTp) {
                        if (this.player.getFight() != null)
                            return;
                        this.player.thatMap = this.player.getCurMap().getId();
                        this.player.thatCell = this.player.getCurCell().getId();
                        this.player.teleport(target.getCurMap().getId(), target.getCurCell().getId());
                        return;
                    }

                    if (Logging.USE_LOG)
                        Logging.getInstance().write("PrivateMessage", this.player.getName() + " ? " + target.getName() + " > " + msg);
                    if (this.player.getObjetByPos(Constant.ITEM_POS_ROLEPLAY_BUFF) != null)
                        if (this.player.getObjetByPos(Constant.ITEM_POS_ROLEPLAY_BUFF).getTemplate().getId() == 10844)
                            msg = Formulas.translateMsg(msg);

                    SocketManager.GAME_SEND_cMK_PACKET(this.player, "T", target.getId(), target.getName(), msg);
                    SocketManager.GAME_SEND_cMK_PACKET(target, "F", this.player.getId(), this.player.getName(), msg);

                    if(target.getAccount().isMuted())
                        this.send("Im0168;" + target.getName() + "~" + target.getAccount().getMuteTime());
                }
                break;
        }
    }

    private void whoIs(String packet) {
        packet = packet.substring(2);
        Player player = World.world.getPlayerByName(packet);
        if (player == null) {
            if (packet.isEmpty())
                SocketManager.GAME_SEND_BWK(this.player, this.player.getAccount().getPseudo()
                        + "|1|"
                        + this.player.getName()
                        + "|"
                        + (this.player.getCurMap().getSubArea() != null ? this.player.getCurMap().getSubArea().getArea().getId() : "-1"));
            else
                this.player.send("PIEn" + packet);

        } else {
            if (!player.isOnline()) {
                this.player.send("PIEn" + player.getName());
                return;
            }
            if (this.player.getAccount().isFriendWith(player.getId()))
                SocketManager.GAME_SEND_BWK(this.player, player.getAccount().getPseudo()
                        + "|1|"
                        + player.getName()
                        + "|"
                        + (player.getCurMap().getSubArea() != null ? player.getCurMap().getSubArea().getArea().getId() : "-1"));
            else if (player == this.player)
                SocketManager.GAME_SEND_BWK(this.player, this.player.getAccount().getPseudo()
                        + "|1|"
                        + this.player.getName()
                        + "|"
                        + (this.player.getCurMap().getSubArea() != null ? this.player.getCurMap().getSubArea().getArea().getId() : "-1"));
            else
                SocketManager.GAME_SEND_BWK(this.player, player.getAccount().getPseudo()
                        + "|1|" + player.getName() + "|-1");
        }
    }

    private void chooseState(String packet) {
        switch (packet.charAt(2)) {
            case 'A': //Absent
                if (this.player._isAbsent) {
                    SocketManager.GAME_SEND_Im_PACKET(this.player, "038");
                    this.player._isAbsent = false;
                } else {
                    SocketManager.GAME_SEND_Im_PACKET(this.player, "037");
                    this.player._isAbsent = true;
                }
                break;
            case 'I': //Invisible
                if (this.player._isInvisible) {
                    SocketManager.GAME_SEND_Im_PACKET(this.player, "051");
                    this.player._isInvisible = false;
                } else {
                    SocketManager.GAME_SEND_Im_PACKET(this.player, "050");
                    this.player._isInvisible = true;
                }
                break;
        }
    }

    // T?l?portation de MJ
    private void goToMap(String packet) {
        if (this.player.getGroup() == null)
            return;
        if (this.player.getGroup().isPlayer())
            return;

        String datas = packet.substring(3);
        if (datas.isEmpty())
            return;
        int MapX = Integer.parseInt(datas.split(",")[0]);
        int MapY = Integer.parseInt(datas.split(",")[1]);
        int sa = Optional.ofNullable(this.player.getCurMap()).map(GameMap::getArea).map(Area::getSuperArea).orElse(-1);

        List<Integer> i = World.world.getMapIdByPosInSuperArea(MapX, MapY, sa);

        if (i.isEmpty())
            return;

        GameMap map = World.world.getMap(i.get(Formulas.getRandomValue(0, i.size() - 1)));
        if (map == null)
            return;
        int CellId = map.getRandomFreeCellId();
        if (map.getCase(CellId) == null)
            return;
        if (this.player.getFight() != null)
            return;

        this.player.teleport(map.getId(), CellId);
    }

    /** Fin Basics Packet **/

    /**
     * Conquest Packet *
     */
    private void parseConquestPacket(String packet) {
        switch (packet.charAt(1)) {
            case 'b':
                requestBalance();
                break;
            case 'B':
                getAlignedBonus();
                break;
            case 'W':
                worldInfos(packet);
                break;
            case 'I':
                prismInfos(packet);
                break;
            case 'F':
                prismFight(packet);
                break;
        }
    }

    public void requestBalance() {
        GameMap map = this.player.getCurMap();;
        if(map != null && map.getSubArea() != null) {
            SocketManager.SEND_Cb_BALANCE_CONQUETE(this.player, World.world.getBalanceWorld(this.player.getAlignment()) + ";" + World.world.getBalanceArea(map.getSubArea().getArea(), this.player.getAlignment()));
        }
    }

    public void getAlignedBonus() {
        double porc = World.world.getBalanceWorld(this.player.getAlignment());
        double porcN = Math.rint((this.player.getGrade() / 2.5) + 1);
        SocketManager.SEND_CB_BONUS_CONQUETE(this.player, porc + "," + porc + ","
                + porc + ";" + porcN + "," + porcN + "," + porcN + ";" + porc
                + "," + porc + "," + porc);
    }

    private void worldInfos(String packet) {
        switch (packet.charAt(2)) {
            case 'J':
                SocketManager.SEND_CW_INFO_WORLD_CONQUETE(this.player, World.world.PrismesGeoposition(player, 1));
                SocketManager.SEND_CW_INFO_WORLD_CONQUETE(this.player, World.world.PrismesGeoposition(player, 2));
                break;
            case 'V':
                SocketManager.SEND_CW_INFO_WORLD_CONQUETE(this.player, World.world.PrismesGeoposition(player, 1));
                SocketManager.SEND_CW_INFO_WORLD_CONQUETE(this.player, World.world.PrismesGeoposition(player, 2));
                break;
        }
    }

    private void prismInfos(String packet) {
        if (packet.charAt(2) == 'J' || packet.charAt(2) == 'V') {
            switch (packet.charAt(2)) {
                case 'J':
                    if(this.player.getCurMap() != null && this.player.getCurMap().getSubArea() != null) {
                        Prism prism = this.player.getCurMap().getSubArea().getPrism();
                        if (prism != null) {
                            Prism.parseAttack(this.player);
                            Prism.parseDefense(this.player);
                        }
                        SocketManager.SEND_CIJ_INFO_JOIN_PRISME(this.player, this.player.parsePrism());
                    }
                    break;
            }
        }
    }

    private void prismFight(String packet) {
        switch (packet.charAt(2)) {
            case 'J':
                if (this.player.isInPrison())
                    return;

                final Prism prism = this.player.getCurMap().getSubArea().getPrism();

                if (prism == null)
                    return;

                int FightID = -1, cellID = -1;
                int MapID = -1;
                try {
                    FightID = prism.getFight().getId();
                    MapID = prism.getMap();
                    cellID = prism.getCell();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (FightID == -1 || MapID == -1 || cellID == -1)
                    return;
                if (this.player.getFight() != null  || prism.getAlignment() != this.player.getAlignment() || this.player.isDead() == 1 || World.world.getMap(MapID) == null){
                    SocketManager.GAME_SEND_BN(this.player);
                    return;
                }

                final int map = MapID;
                final int cell = cellID;
                final Fight fight = World.world.getMap(map).getFight(FightID);

                if(fight == null) {
                    SocketManager.GAME_SEND_BN(this.player);
                    return;
                }

                if (this.player.getCurMap().getId() != MapID) {
                    this.player.setCurMap(this.player.getCurMap());
                    this.player.setCurCell(this.player.getCurCell());
                    this.player.teleport(map, cell);
                }

                TimerWaiter.addNext(() -> {
                    fight.joinPrismFight(this.player, (fight.getInit0().getPrism() != null ? fight.getInit0() : fight.getInit1()).getTeam());
                    World.world.getOnlinePlayers().stream().filter(Objects::nonNull).filter(player -> player.getAlignment() == player.getAlignment()).forEach(Prism::parseDefense);
                }, 2, TimeUnit.SECONDS);
                break;
        }
    }

    /** Fin Conquest Packet **/

    /**
     * Chat Packet *
     */
    private void parseChanelPacket(String packet) {
        switch (packet.charAt(1)) {
            case 'C'://Changement des Canaux
                subscribeChannels(packet);
                break;
        }
    }

    private void subscribeChannels(String packet) {
        String chan = packet.charAt(3) + "";
        switch (packet.charAt(2)) {
            case '+'://Ajthis du Canal
                this.player.addChanel(chan);
                break;
            case '-'://Desactivation du canal
                this.player.removeChanel(chan);
                break;
        }
        DatabaseManager.get(PlayerData.class).update(this.player);
    }

    /** Fin Chat Packet **/

    /**
     * Dialog Packet *
     */
    private void parseDialogPacket(String packet) {
        switch (packet.charAt(1)) {
            case 'C'://Demande de l'initQuestion
                npcCreateDialog(packet);
                break;

            case 'R'://R?ponse du joueur
                npcResponse(packet);
                break;

            case 'V'://Fin du dialog
                quitDialog();
                break;
        }

        final Party party = this.player.getParty();

        if(party != null && this.player.getFight() == null && party.getMaster() != null && party.getMaster().getName().equals(this.player.getName())) {
            TimerWaiter.addNext(() -> party.getPlayers().stream().filter((follower1) -> party.isWithTheMaster(follower1, false, false))
                    .forEach(follower -> follower.getGameClient().parseDialogPacket(packet)), 0, TimeUnit.SECONDS);
        }
    }

    private void npcCreateDialog(String packet) {
        int id = Integer.parseInt(packet.substring(2).split((char) 0x0A + "")[0]);

        if (player.isMissingSubscription() || player.getExchangeAction() != null) {
            SocketManager.GAME_SEND_EXCHANGE_REQUEST_ERROR(player.getGameClient(), 'S');
            return;
        }

        Collector collector = World.world.getCollector(id);

        if (collector != null && collector.getMap() == player.getCurMap().getId()) {
            SocketManager.GAME_SEND_DIALOG_CREATE_PACKET(this, id);
            send(World.world.getGuild(collector.getGuildId()).encodeTaxCollectorDQ());
            return;
        }

        Npc npc = player.getCurMap().getNpc(id);

        if (npc != null) {
            npc.onCreateDialog(player);
        }
    }

    @SuppressWarnings("unchecked")
    private void npcResponse(String packet) {
        ExchangeAction<?> action = this.player.getExchangeAction();

        if (action == null || action.getType() != ExchangeAction.TALKING_WITH)
            return;

        String[] infos = packet.substring(2).split("\\|");

        NpcDialogActionData data = ((ExchangeAction<NpcDialogActionData>) this.player.getExchangeAction()).getValue();
        Npc npc = data.getNpc(player);

        if (npc != null && infos.length >= 2) {
            int question = Integer.parseInt(infos[0]);
            int answer = Integer.parseInt(infos[1]);

            if(data.getQuestionId() == question && data.hasAnswer(answer)) {
                npc.getTemplate().onDialog(this.player, question, answer);
                return;
            }
        }

        this.player.setExchangeAction(null);
        SocketManager.GAME_SEND_END_DIALOG_PACKET(this);
    }

    private void quitDialog() {
        ExchangeAction<?> action = this.player.getExchangeAction();

        if (action == null || action.getType() != ExchangeAction.TALKING_WITH)
            return;

        this.walk = false;
        this.player.setAway(false);
        this.player.setExchangeAction(null);
        SocketManager.GAME_SEND_END_DIALOG_PACKET(this);
    }

    /** Fin Dialog Packet **/

    /**
     * Document Packet *
     */
    private void parseDocumentPacket(String packet) {
        switch (packet.charAt(1)) {
            case 'V':
                if(player.getExchangeAction() != null
                && player.getExchangeAction().getType() != ExchangeAction.READING_DOCUMENT) {
                    player.setExchangeAction(null);
                }

                SocketManager.GAME_SEND_DOCUMENT_CLOSE_PACKET(this);
                break;
        }
    }

    /** Fin Document Packet **/

    /**
     * Exchange Packet *
     */
    private synchronized void parseExchangePacket(String packet) {
        if (this.player.isDead() == 1)
            return;
        switch (packet.charAt(1)) {
            case 'A'://Accepter demande d'?change
                accept();
                break;
            case 'B'://Achat
                buy(packet);
                break;
            case 'H'://Demande prix moyen + cat?gorie
                bigStore(packet);
                break;
            case 'K'://Ok
                ready();
                break;
            case 'L'://jobAction : Refaire le craft pr?cedent
                replayCraft();
                break;
            case 'M'://Move (Ajthiser//retirer un objet a l'?change)
                movementItemOrKamas(packet);
                break;
            case 'P':
                movementItemOrKamasDons(packet.substring(2));
                break;
            case 'q'://Mode marchand (demande de la taxe)
                askOfflineExchange();
                break;
            case 'Q'://Mode marchand (Si valider apr?s la taxe)
                offlineExchange();
                break;
            case 'r'://Rides => Monture
                putInInventory(packet);
                break;
            case 'f'://Etable => Enclos
                putInMountPark(packet);
                break;
            case 'R'://liste d'achat NPC
                request(packet);
                break;
            case 'S'://Vente
                sell(packet);
                break;
            case 'J'://Livre artisant
                bookOfArtisant(packet);
                break;
            case 'W'://Metier public
                setPublicMode(packet);
                break;
            case 'V'://Fin de l'?change
                leaveExchange(this.player);
                break;
        }
    }

    private void accept() {
        ExchangeAction<?> checkExchangeAction = this.player.getExchangeAction();

        if (Main.tradeAsBlocked || this.player.isDead() == 1 || checkExchangeAction == null || !(checkExchangeAction.getValue() instanceof Integer) || (checkExchangeAction.getType() != ExchangeAction.TRADING_WITH_PLAYER && checkExchangeAction.getType() != ExchangeAction.CRAFTING_SECURE_WITH))
            return;

        ExchangeAction<Integer> exchangeAction = (ExchangeAction<Integer>) this.player.getExchangeAction();
        Player target = World.world.getPlayer(exchangeAction.getValue());
        if(target == null) return;

        checkExchangeAction = target.getExchangeAction();

        if (target.isDead() == 1 || checkExchangeAction == null || !(checkExchangeAction.getValue() instanceof Integer) || (checkExchangeAction.getType() != ExchangeAction.TRADING_WITH_PLAYER && checkExchangeAction.getType() != ExchangeAction.CRAFTING_SECURE_WITH))
            return;

        int type = this.player.getIsCraftingType().get(0);

        switch (type) {
            case 1: // Echange PlayerVsPlayer
                SocketManager.GAME_SEND_EXCHANGE_CONFIRM_OK(this, 1);
                SocketManager.GAME_SEND_EXCHANGE_CONFIRM_OK(target.getGameClient(), 1);
                PlayerExchange exchange = new PlayerExchange(target, this.player);
                ExchangeAction newExchangeAction = new ExchangeAction<>(ExchangeAction.TRADING_WITH_PLAYER, exchange);
                this.player.setExchangeAction(newExchangeAction);
                target.setExchangeAction(newExchangeAction);
                this.player.getIsCraftingType().clear();
                target.getIsCraftingType().clear();
                break;
            case 12: // Craft s?ucirs?
            case 13:
                Player player1 = (target.getIsCraftingType().get(0) == 12 ? target : this.player);
                Player player2 = (target.getIsCraftingType().get(0) == 13 ? target : this.player);

                CraftSecure craftSecure = new CraftSecure(player1, player2);
                SocketManager.GAME_SEND_ECK_PACKET(this, type, craftSecure.getMaxCase() + ";" + this.player.getIsCraftingType().get(1));
                SocketManager.GAME_SEND_ECK_PACKET(target.getGameClient(), target.getIsCraftingType().get(0), craftSecure.getMaxCase() + ";" + this.player.getIsCraftingType().get(1));

                newExchangeAction = new ExchangeAction<>(ExchangeAction.CRAFTING_SECURE_WITH, craftSecure);
                this.player.setExchangeAction(newExchangeAction);
                target.setExchangeAction(newExchangeAction);
                break;
        }
    }

    private void buy(String packet) {
        String[] infos = packet.substring(2).split("\\|");

        ExchangeAction<?> checkExchangeAction = this.player.getExchangeAction();
        if(checkExchangeAction == null || !(checkExchangeAction.getValue() instanceof Integer) || (checkExchangeAction.getType() != ExchangeAction.TRADING_WITH_OFFLINE_PLAYER && checkExchangeAction.getType() != ExchangeAction.TRADING_WITH_NPC)) return;

        ExchangeAction<Integer> exchangeAction = (ExchangeAction<Integer>) this.player.getExchangeAction();

        if (exchangeAction.getType() == ExchangeAction.TRADING_WITH_OFFLINE_PLAYER) {
            Player seller = World.world.getPlayer(exchangeAction.getValue());
            if (seller != null && seller != this.player) {
                int itemID = 0;
                int qua = 0;
                int price = 0;
                try {
                    itemID = Integer.valueOf(infos[0]);
                    qua = Integer.valueOf(infos[1]);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                    if (!seller.getStoreItems().containsKey(itemID) || qua <= 0) {
                        SocketManager.GAME_SEND_BUY_ERROR_PACKET(this);
                        return;
                    }
                    price = seller.getStoreItems().get(itemID) * qua;
                    int price2 = seller.getStoreItems().get(itemID);
                    GameObject itemStore = World.world.getGameObject(itemID);
                    if (itemStore == null)
                        return;
                    if (price > this.player.getKamas())
                        return;
                    if (qua <= 0 || qua > 100000)
                        return;
                    if (qua > itemStore.getQuantity())
                        qua = itemStore.getQuantity();
                    if (qua == itemStore.getQuantity()) {
                        seller.getStoreItems().remove(itemStore.getGuid());
                        this.player.addItem(itemStore, true, false);
                    } else if (itemStore.getQuantity() > qua) {
                        seller.getStoreItems().remove(itemStore.getGuid());
                        itemStore.setQuantity(itemStore.getQuantity() - qua);
                        seller.addStoreItem(itemStore.getGuid(), price2);

                        GameObject clone = itemStore.getClone(qua, true);
                        if (this.player.addItem(clone, true, false))
                            World.world.addGameObject(clone);
                    } else {
                        SocketManager.GAME_SEND_BUY_ERROR_PACKET(this);
                        return;
                    }

                    //remove kamas
                    this.player.addKamas(-price);
                    //add seller kamas
                    seller.addKamas(price);
                    DatabaseManager.get(PlayerData.class).update(seller);
                    //send packets
                    SocketManager.GAME_SEND_STATS_PACKET(this.player);
                    SocketManager.GAME_SEND_ITEM_LIST_PACKET_SELLER(seller, this.player);
                    SocketManager.GAME_SEND_BUY_OK_PACKET(this);
                    if (seller.getStoreItems().isEmpty()) {
                        if (World.world.getSeller(seller.getCurMap().getId()) != null
                                && World.world.getSeller(seller.getCurMap().getId()).contains(seller.getId())) {
                            World.world.removeSeller(seller.getId(), seller.getCurMap().getId());
                            SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(seller.getCurMap(), seller.getId());
                            leaveExchange(this.player);
                        }
                    }
            }
        } else {

            try {
                int id = Integer.parseInt(infos[0]), qua = Integer.parseInt(infos[1]);

                if (qua <= 0 || qua > 100000)
                    return;

                Npc npc = this.player.getCurMap().getNpc(exchangeAction.getValue());
                if (npc == null) return;
                NpcTemplate npcTemplate = npc.getTemplate();

                Optional<SaleOffer> optOffer = npcTemplate.salesList(this.player).stream().filter(o -> o.itemTemplate.getId() == id).findFirst();
                if (!optOffer.isPresent()) {
                    SocketManager.GAME_SEND_BUY_ERROR_PACKET(this);
                    return;
                }

                SaleOffer offer = optOffer.get();
                if (offer.itemTemplate.getType() == 18 && qua > 1) {
                    this.player.sendMessage(this.player.getLang().trans("game.gameclient.buy.fami"));
                    return;
                }

                long totalPrice = qua * offer.unitPrice;


                if(!player.consumeCurrency(offer.currency, totalPrice)) {
                    SocketManager.GAME_SEND_BUY_ERROR_PACKET(this);
                    // FIXME Make a notEnoughCurrency(offer.currency) function to call here
                    return;
                }

                player.addItem(offer.itemTemplate, qua,(npcTemplate.getFlags() & 0x1) != 0, true);
                SocketManager.GAME_SEND_BUY_OK_PACKET(this);
            } catch (Exception e) {
                e.printStackTrace();
                SocketManager.GAME_SEND_BUY_ERROR_PACKET(this);
            }
        }
    }

    private void bigStore(String packet) {
        if (this.player.getFight() != null || this.player.isAway())
            return;

        // Check current exchange action
        BigStoreActionData exchangeAction = Optional.ofNullable(this.player.getExchangeAction()).filter(ea -> ea.getType() == ExchangeAction.AUCTION_HOUSE_BUYING)
                .map(ExchangeAction::getValue).map(BigStoreActionData.class::cast).orElse(null);
        if(exchangeAction == null) {
            return;
        }
        BigStore bigStore = World.world.getHdv(exchangeAction.hdvId);

        int templateID;
        switch (packet.charAt(2)) {
            case 'B': //Confirmation d'achat
                String[] info = packet.substring(3).split("\\|");//ligneID|amount|price
                int ligneID = Integer.parseInt(info[0]);
                int amount = Integer.parseInt(info[1]);
                int price = Integer.parseInt(info[2]);

                // Client amount is [1,3], our enum is 0-2
                BigStoreListingLotSize lotSize = BigStoreListingLotSize.fromValue(amount-1);
                BigStoreListing entry = bigStore.buyItem(exchangeAction.getCategoryId(), exchangeAction.getTemplateId(), ligneID, lotSize, price, this.player).orElse(null);

                if (entry == null) {
                    SocketManager.GAME_SEND_Im_PACKET(this.player, "172");//Envoie un message d'erreur d'achat
                    return;
                }

                Optional<Account> seller = Optional.ofNullable(World.world.ensureAccountLoaded(entry.getOwner()));

                String name = seller.map(Account::getName).orElse("undefined");
                GameObject obj = entry.getGameObject();

                try {
                    Logging.getInstance().write("Object", "BuyHdv : "
                        + player.getName() + " : achat de "
                        + obj.getTemplate().getName() + "(" + obj.getGuid() + ") x"
                        + obj.getQuantity() + " venant du compte "
                        + name);
                } catch(Exception ex) { ex.printStackTrace(); }

                seller.map(Account::getCurrentPlayer).ifPresent(p -> {
                    SocketManager.GAME_SEND_Im_PACKET(p, "065;"
                        + price
                        + "~"
                        + obj.getTemplate().getId()
                        + "~" + obj.getTemplate().getId() + "~1");
                    DatabaseManager.get(PlayerData.class).update(p);
                });

                Optional<BigStore.CheapestListings> updated = bigStore.getCheapestListings(exchangeAction.getCategoryId(), exchangeAction.getTemplateId(), ligneID);
                SocketManager.GAME_SEND_EHm_DEL_PACKET(this.player, ligneID);
                // Refresh line if there is still one
                updated.ifPresent(c -> SocketManager.GAME_SEND_EHm_ADD_PACKET(this.player, c));
                this.player.refreshStats();
                SocketManager.GAME_SEND_Ow_PACKET(this.player);
                SocketManager.GAME_SEND_Im_PACKET(this.player, "068");//Envoie le message "Lot achet?"

                break;
            case 'l'://Demande listage d'un template (les prix)
                templateID = Integer.parseInt(packet.substring(3));


                exchangeAction.setTemplateId(templateID);
                SocketManager.GAME_SEND_EHl(this.player, bigStore, exchangeAction.getCategoryId(), templateID);

                break;
            case 'P'://Demande des prix moyen
                templateID = Integer.parseInt(packet.substring(3));
                SocketManager.GAME_SEND_EHP_PACKET(this.player, templateID);
                break;
            case 'T'://Demande des template de la categorie
                int categ = Integer.parseInt(packet.substring(3));
                List<Integer> catContent = bigStore.getCategoryContent(categ);
                exchangeAction.setCategoryId(categ);
                SocketManager.GAME_SEND_EHL_PACKET(this.player, categ, catContent);
                break;
            case 'S': //search
                String[] infos = packet.substring(3).split("\\|");//type | templateId
                int template = Integer.parseInt(infos[1]);
                int category = Integer.parseInt(infos[0]);

                catContent = bigStore.getCategoryContent(category);

                if(catContent == null || catContent.isEmpty()) {
                    this.player.send("EHS");
                } else {
                    this.player.send("EHSK");
                    SocketManager.GAME_SEND_EHL_PACKET(this.player, category, catContent);
                    SocketManager.GAME_SEND_EHP_PACKET(this.player, template);
                    SocketManager.GAME_SEND_EHl(this.player, bigStore, category, template);
                }
                break;
        }
    }

    private void ready() {
        if(this.player.getExchangeAction() == null) return;

        ExchangeAction<?> exchangeAction = this.player.getExchangeAction();
        Object value = exchangeAction.getValue();

        if (exchangeAction.getType() == ExchangeAction.CRAFTING && value instanceof JobAction) {
            if (((JobAction) value).isCraft()) {
                ((JobAction) value).startCraft(this.player);
            }
            return;
        }

        if (exchangeAction.getType() == ExchangeAction.TRADING_WITH_NPC_EXCHANGE && value instanceof NpcExchange)
            ((NpcExchange) value).toogleOK(false);

        if (exchangeAction.getType() == ExchangeAction.TRADING_WITH_NPC_PETS && value instanceof PlayerExchange.NpcExchangePets)
            ((PlayerExchange.NpcExchangePets) value).toogleOK(false);

        if (exchangeAction.getType() == ExchangeAction.TRADING_WITH_NPC_PETS_RESURRECTION && value instanceof PlayerExchange.NpcRessurectPets)
            ((PlayerExchange.NpcRessurectPets) value).toogleOK(false);

        if ((exchangeAction.getType() == ExchangeAction.TRADING_WITH_PLAYER || exchangeAction.getType() == ExchangeAction.CRAFTING_SECURE_WITH) && value instanceof Exchange)
            if (((Exchange) value).toogleOk(this.player.getId()))
                ((Exchange) value).apply();

        if (exchangeAction.getType() == ExchangeAction.BREAKING_OBJECTS && value instanceof BreakingObject) {
            if (((BreakingObject) value).getObjects().isEmpty())
                return;

            Fragment fragment = new Fragment("");

            for (Couple<Integer, Integer> couple : ((BreakingObject) value).getObjects()) {
                GameObject object = this.player.getItems().get(couple.first);

                if (object == null || couple.second < 1 || object.getQuantity() < couple.second) {
                    this.player.send("Ea3");
                    break;
                }

                for (int k = couple.second; k > 0; k--) {
                    int type = object.getTemplate().getType();
                    if (type > 11 && type < 16 && type > 23 && type != 81 && type != 82)
                        continue;
                    for (Map.Entry<Integer, Integer> entry1 : object.getStats().getEffects().entrySet()) {
                        int jet = entry1.getValue();
                        for (Rune rune : Rune.runes) {
                            if (entry1.getKey() == rune.getCharacteristic()) {
                                if (rune.getId() == 1557 || rune.getId() == 1558 || rune.getId() == 7438) {
                                    double puissance = 1.5 * (Math.pow(object.getTemplate().getLevel(), 2.0) / Math.pow(rune.getWeight(), (5.0 / 4.0))) + ((jet - 1) / rune.getWeight()) * (66.66 - 1.5 * (Math.pow(object.getTemplate().getLevel(), 2.0) / Math.pow(rune.getWeight(), (55.0 / 4.0))));
                                    int chance = (int) Math.ceil(puissance);

                                    if (chance > 66) chance = 66;
                                    else if (chance <= 0) chance = 1;
                                    if (Formulas.getRandomValue(1, 100) <= chance)
                                        fragment.addRune(rune.getId());
                                } else {
                                    double val = rune.getBonus();
                                    if (rune.getId() == 7451 || rune.getId() == 10662) val *= 3.0;

                                    double tauxGetMin = World.world.getTauxObtentionIntermediaire(val, true, (val != 30)), tauxGetMax = (tauxGetMin / (2.0 / 3.0)) / 0.9;
                                    int tauxMax = (int) Math.ceil(tauxGetMax), tauxGet = (int) Math.ceil(tauxGetMin), tauxMin = 2 * (tauxMax - tauxGet) - 2;

                                    if (rune.getId() == 7433 || rune.getId() == 7434 || rune.getId() == 7435 || rune.getId() == 7441)
                                        tauxMax++;
                                    if (jet < tauxMin) continue;

                                    for (int i = jet; i > 0; i -= tauxMax) {
                                        int j = 0;
                                        if (i > tauxMax) j = tauxMax;
                                        else j = i;
                                        if (j == tauxMax) fragment.addRune(rune.getId());
                                        else if (Formulas.getRandomValue(1, 100) < (100 * (tauxMax - j) / (tauxMax - tauxMin)))
                                            fragment.addRune(rune.getId());
                                    }
                                }
                            }
                        }
                    }
                }

                if (couple.second == object.getQuantity()) {
                    this.player.deleteItem(object.getGuid());
                    World.world.removeGameObject(object.getGuid());
                    SocketManager.SEND_OR_DELETE_ITEM(this, object.getGuid());
                } else {
                    object.setQuantity(object.getQuantity() - couple.second);
                    SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player, object);
                }
            }

            World.world.addGameObject(fragment);
            this.player.addItem(fragment, true);
            SocketManager.GAME_SEND_Ec_PACKET(this.player, "K;8378");
            SocketManager.GAME_SEND_Ow_PACKET(this.player);
            SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), "+8378");
            this.player.startActionOnCell(this.player.getGameAction());
            ((BreakingObject) value).getObjects().clear();
        }
    }

    private void replayCraft() {
        if (this.player.getExchangeAction() != null && this.player.getExchangeAction().getType() == ExchangeAction.CRAFTING)
            if (((JobAction) this.player.getExchangeAction().getValue()).getJobCraft() == null)
                ((JobAction) this.player.getExchangeAction().getValue()).putLastCraftIngredients();
    }

    private synchronized void movementItemOrKamas(String packet) {
        if(this.player.getExchangeAction() == null) return;
        if(packet.contains("NaN")) {
            this.player.sendMessage("Error : StartExchange : (" + this.player.getExchangeAction().getType() + ") : " + packet + "\n send at administreur.");
            return;
        }
        switch(this.player.getExchangeAction().getType()) {
            case ExchangeAction.TRADING_WITH_ME:
                switch (packet.charAt(2)) {
                    case 'O'://Objets
                        if (packet.charAt(3) == '+') {
                            String[] infos = packet.substring(4).split("\\|");
                            try {
                                int guid = Integer.parseInt(infos[0]);
                                int qua = Integer.parseInt(infos[1]);
                                int price = Integer.parseInt(infos[2]);

                                GameObject obj = this.player.getItems().get(guid);
                                if (obj == null)
                                    return;
                                if (qua <= 0 || obj.isAttach())
                                    return;
                                if (price <= 0)
                                    return;

                                if (qua > obj.getQuantity())
                                    qua = obj.getQuantity();
                                this.player.addInStore(obj.getGuid(), price, qua);
                            } catch (NumberFormatException e) {
                                World.world.logger.error("Error Echange Store '" + packet + "' => " + e.getMessage());
                                e.printStackTrace();
                                return;
                            }
                        } else {
                            String[] infos = packet.substring(4).split("\\|");
                            try {
                                int guid = Integer.parseInt(infos[0]);
                                int qua = Integer.parseInt(infos[1]);

                                if (qua <= 0)
                                    return;
                                GameObject obj = World.world.getGameObject(guid);
                                if (obj == null)
                                    return;
                                if (qua < 0)
                                    return;
                                if (qua > obj.getQuantity())
                                    return;
                                if (qua < obj.getQuantity())
                                    qua = obj.getQuantity();
                                this.player.removeFromStore(obj.getGuid(), qua);
                            } catch (NumberFormatException e) {
                                World.world.logger.error("Error Echange Store '" + packet + "' => " + e.getMessage());
                                e.printStackTrace();
                                return;
                            }
                        }
                        break;
                }
                break;

            case ExchangeAction.TRADING_WITH_COLLECTOR:
                Collector Collector = World.world.getCollector((Integer) this.player.getExchangeAction().getValue());
                if (Collector == null || Collector.getInFight() > 0)
                    return;
                switch (packet.charAt(2)) {
                    case 'G'://Kamas
                        if (packet.charAt(3) == '-') //On retire
                        {
                            long P_Kamas = -1;
                            try {
                                P_Kamas = Integer.parseInt(packet.substring(4));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                World.world.logger.error("Error Echange CC '" + packet + "' => " + e.getMessage());
                            }
                            if (P_Kamas < 0)
                                return;
                            if (Collector.getKamas() >= P_Kamas) {//Faille non connu ! :p
                                long P_Retrait = Collector.getKamas() - P_Kamas;
                                Collector.setKamas(Collector.getKamas() - P_Kamas);
                                if (P_Retrait < 0) {
                                    P_Retrait = 0;
                                    P_Kamas = Collector.getKamas();
                                }
                                Collector.setKamas(P_Retrait);
                                this.player.addKamas(P_Kamas);
                                SocketManager.GAME_SEND_STATS_PACKET(this.player);
                                SocketManager.GAME_SEND_EsK_PACKET(this.player, "G"
                                        + Collector.getKamas());
                            }
                        }
                        break;
                    case 'O'://Objets
                        if (packet.charAt(3) == '-') //On retire
                        {
                            String[] infos = packet.substring(4).split("\\|");
                            int guid = 0;
                            int qua = 0;
                            try {
                                guid = Integer.parseInt(infos[0]);
                                qua = Integer.parseInt(infos[1]);
                            } catch (NumberFormatException e) {
                                // ok
                                return;
                            }

                            if (guid <= 0 || qua <= 0)
                                return;

                            GameObject obj = World.world.getGameObject(guid);
                            if (obj == null)
                                return;

                            if (Collector.haveObjects(guid)) {
                                Collector.removeFromCollector(this.player, guid, qua);
                            }
                            Collector.addLogObjects(guid, obj);
                        }
                        break;
                }
                DatabaseManager.get(GuildData.class).update(this.player.getGuild());
                break;
            case ExchangeAction.BREAKING_OBJECTS:
                final BreakingObject breakingObject = ((BreakingObject) this.player.getExchangeAction().getValue());

                if (packet.charAt(2) == 'O') {
                    if (packet.charAt(3) == '+') {
                        if (breakingObject.getObjects().size() >= 8)
                            return;

                        String[] infos = packet.substring(4).split("\\|");

                        try {
                            int id = Integer.parseInt(infos[0]), qua = Integer.parseInt(infos[1]);

                            if (!this.player.hasItemGuid(id))
                                return;

                            GameObject object = this.player.getItems().get(id);

                            if (object == null || object.isAttach())
                                return;
                            if (qua < 1)
                                return;
                            if (qua > object.getQuantity())
                                qua = object.getQuantity();

                            int type = object.getTemplate().getType();

                            SocketManager.SEND_EMK_MOVE_ITEM(this, 'O', "+", id + "|" + breakingObject.addObject(id, qua));
                        } catch (NumberFormatException e) {
                            World.world.logger.error("Error Echange CC '" + packet + "' => " + e.getMessage());
                            e.printStackTrace();
                            return;
                        }
                    } else if (packet.charAt(3) == '-') {
                        String[] infos = packet.substring(4).split("\\|");
                        try {
                            int id = Integer.parseInt(infos[0]);
                            int qua = Integer.parseInt(infos[1]);

                            GameObject object = World.world.getGameObject(id);

                            if (object == null)
                                return;
                            if (qua < 1)
                                return;

                            final int quantity = breakingObject.removeObject(id, qua);

                            if (quantity <= 0)
                                SocketManager.SEND_EMK_MOVE_ITEM(this, 'O', "-", id + "");
                            else
                                SocketManager.SEND_EMK_MOVE_ITEM(this, 'O', "+", id + "|" + quantity);
                        } catch (NumberFormatException e) {
                            World.world.logger.error("Error Echange CC '" + packet + "' => " + e.getMessage());
                            e.printStackTrace();
                            return;
                        }
                    }
                } else if(packet.charAt(2) == 'R') {
                    final int count = Integer.parseInt(packet.substring(3));
                    breakingObject.setCount(count);
                    TimerWaiter.addNext(() -> {
                        this.recursiveBreakingObject(breakingObject, 0, count);

                    }, 0);
                } else if(packet.charAt(2) == 'r') {
                    breakingObject.setStop(true);
                }
                break;
            case ExchangeAction.IN_MOUNT:
                Mount mount = this.player.getMount();
                if (mount == null) return;
                switch (packet.charAt(2)) {
                    case 'O':// Objet
                        int id = 0;
                        int cant = 0;
                        try {
                            id = Integer.parseInt(packet.substring(4).split("\\|")[0]);
                            cant = Integer.parseInt(packet.substring(4).split("\\|")[1]);
                        } catch (Exception e) {
                            World.world.logger.error("Error Echange DD '" + packet + "' => " + e.getMessage());
                            e.printStackTrace();
                            return;
                        }
                        if (id == 0 || cant <= 0)
                            return;
                        if (World.world.getGameObject(id) == null) {
                            SocketManager.GAME_SEND_MESSAGE(this.player, this.player.getLang().trans("game.gameclient.movemenitemorkamas.error1"));
                            return;
                        }
                        switch (packet.charAt(3)) {
                            case '+':
                                mount.addObject(id, cant, this.player);
                                break;
                            case '-':
                                mount.removeObject(id, cant, this.player);
                                break;
                            case ',':
                                break;
                        }
                        break;
                }
                break;

            case ExchangeAction.TRADING_WITH_NPC_EXCHANGE:
                switch (packet.charAt(2)) {
                    case 'O'://Objet ?
                        if (packet.charAt(3) == '+') {
                            String[] infos = packet.substring(4).split("\\|");
                            try {
                                int guid = Integer.parseInt(infos[0]);
                                int qua = Integer.parseInt(infos[1]);
                                int quaInExch = ((NpcExchange) this.player.getExchangeAction().getValue()).getQuaItem(guid, false);

                                if (!this.player.hasItemGuid(guid)) return;
                                GameObject obj = this.player.getItems().get(guid);
                                if (obj == null) return;

                                if (qua > obj.getQuantity() - quaInExch)
                                    qua = obj.getQuantity() - quaInExch;
                                if (qua <= 0)
                                    return;
                                if(AuctionManager.getInstance().onPlayerChangeItemInNpcExchange(this.player, obj))
                                    return;

                                ((NpcExchange) this.player.getExchangeAction().getValue()).addItem(guid, qua);
                            } catch (NumberFormatException e) {
                                World.world.logger.error("Error Echange NPC '" + packet + "' => " + e.getMessage());
                                e.printStackTrace();
                                return;
                            }
                        } else {
                            String[] infos = packet.substring(4).split("\\|");
                            try {
                                int guid = Integer.parseInt(infos[0]);
                                int qua = Integer.parseInt(infos[1]);

                                if (qua <= 0)
                                    return;
                                if (!this.player.hasItemGuid(guid))
                                    return;

                                GameObject obj = World.world.getGameObject(guid);
                                if (obj == null)
                                    return;
                                if (qua > ((NpcExchange) this.player.getExchangeAction().getValue()).getQuaItem(guid, false))
                                    return;

                                ((NpcExchange) this.player.getExchangeAction().getValue()).removeItem(guid, qua);
                            } catch (NumberFormatException e) {
                                World.world.logger.error("Error Echange NPC '" + packet + "' => " + e.getMessage());
                                e.printStackTrace();
                                return;
                            }
                        }
                        break;
                    case 'G'://Kamas
                        try {
                            long numb = Integer.parseInt(packet.substring(3));
                            if (this.player.getKamas() < numb)
                                numb = this.player.getKamas();
                            ((NpcExchange) this.player.getExchangeAction().getValue()).setKamas(false, numb);
                        } catch (NumberFormatException e) {
                            World.world.logger.error("Error Echange NPC '" + packet + "' => " + e.getMessage());
                            e.printStackTrace();
                            return;
                        }
                        break;
                }
                break;
            case ExchangeAction.TRADING_WITH_NPC_PETS:
                switch (packet.charAt(2)) {
                    case 'O'://Objet ?
                        if (packet.charAt(3) == '+') {
                            String[] infos = packet.substring(4).split("\\|");
                            try {
                                int guid = Integer.parseInt(infos[0]);
                                int qua = Integer.parseInt(infos[1]);
                                int quaInExch = ((PlayerExchange.NpcExchangePets) this.player.getExchangeAction().getValue()).getQuaItem(guid, false);

                                if (!this.player.hasItemGuid(guid))
                                    return;
                                GameObject obj = this.player.getItems().get(guid);
                                if (obj == null)
                                    return;

                                if (qua > obj.getQuantity() - quaInExch)
                                    qua = obj.getQuantity() - quaInExch;

                                if (qua <= 0)
                                    return;

                                ((PlayerExchange.NpcExchangePets) this.player.getExchangeAction().getValue()).addItem(guid, qua);
                            } catch (NumberFormatException e) {
                                World.world.logger.error("Error Echange Pets '" + packet + "' => " + e.getMessage());
                                e.printStackTrace();
                                return;
                            }
                        } else {
                            String[] infos = packet.substring(4).split("\\|");
                            try {
                                int guid = Integer.parseInt(infos[0]);
                                int qua = Integer.parseInt(infos[1]);

                                if (qua <= 0)
                                    return;
                                if (!this.player.hasItemGuid(guid))
                                    return;

                                GameObject obj = World.world.getGameObject(guid);
                                if (obj == null)
                                    return;
                                if (qua > ((PlayerExchange.NpcExchangePets) this.player.getExchangeAction().getValue()).getQuaItem(guid, false))
                                    return;

                                ((PlayerExchange.NpcExchangePets) this.player.getExchangeAction().getValue()).removeItem(guid, qua);
                            } catch (NumberFormatException e) {
                                World.world.logger.error("Error Echange Pets '" + packet + "' => " + e.getMessage());
                                e.printStackTrace();
                                return;
                            }
                        }
                        break;
                    case 'G'://Kamas
                        try {
                            long numb = Integer.parseInt(packet.substring(3));
                            if (numb < 0)
                                return;
                            if (this.player.getKamas() < numb)
                                numb = this.player.getKamas();
                            ((PlayerExchange.NpcExchangePets) this.player.getExchangeAction().getValue()).setKamas(false, numb);
                        } catch (NumberFormatException e) {
                            World.world.logger.error("Error Echange Pets '" + packet + "' => " + e.getMessage());
                            e.printStackTrace();
                            return;
                        }
                        break;
                }
                break;

            case ExchangeAction.TRADING_WITH_NPC_PETS_RESURRECTION:
                switch (packet.charAt(2)) {
                    case 'O'://Objet ?
                        if (packet.charAt(3) == '+') {
                            String[] infos = packet.substring(4).split("\\|");
                            try {

                                int guid = Integer.parseInt(infos[0]);
                                int qua = Integer.parseInt(infos[1]);
                                int quaInExch = ((PlayerExchange.NpcRessurectPets) this.player.getExchangeAction().getValue()).getQuaItem(guid, false);

                                if (!this.player.hasItemGuid(guid))
                                    return;
                                GameObject obj = World.world.getGameObject(guid);
                                if (obj == null)
                                    return;

                                if (qua > obj.getQuantity() - quaInExch)
                                    qua = obj.getQuantity() - quaInExch;

                                if (qua <= 0)
                                    return;

                                ((PlayerExchange.NpcRessurectPets) this.player.getExchangeAction().getValue()).addItem(guid, qua);
                            } catch (NumberFormatException e) {
                                World.world.logger.error("Error Echange RPets '" + packet + "' => " + e.getMessage());
                                e.printStackTrace();
                                return;
                            }
                        } else {
                            String[] infos = packet.substring(4).split("\\|");
                            try {
                                int guid = Integer.parseInt(infos[0]);
                                int qua = Integer.parseInt(infos[1]);

                                if (qua <= 0)
                                    return;
                                if (!this.player.hasItemGuid(guid))
                                    return;

                                GameObject obj = World.world.getGameObject(guid);
                                if (obj == null)
                                    return;
                                if (qua > ((PlayerExchange.NpcRessurectPets) this.player.getExchangeAction().getValue()).getQuaItem(guid, false))
                                    return;

                                ((PlayerExchange.NpcRessurectPets) this.player.getExchangeAction().getValue()).removeItem(guid, qua);
                            } catch (NumberFormatException e) {
                                World.world.logger.error("Error Echange RPets '" + packet + "' => " + e.getMessage());
                                e.printStackTrace();
                                return;
                            }
                        }
                        break;
                    case 'G'://Kamas
                        try {
                            long numb = Integer.parseInt(packet.substring(3));
                            if (numb < 0)
                                return;
                            if (this.player.getKamas() < numb)
                                numb = this.player.getKamas();
                            ((PlayerExchange.NpcRessurectPets) this.player.getExchangeAction().getValue()).setKamas(false, numb);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            return;
                        }
                        break;
                }
                break;

            case ExchangeAction.AUCTION_HOUSE_SELLING:
                BigStoreActionData exchangeAction = Optional.ofNullable(this.player.getExchangeAction()).filter(ea -> ea.getType() == ExchangeAction.AUCTION_HOUSE_SELLING)
                        .map(ExchangeAction::getValue).map(BigStoreActionData.class::cast).orElse(null);
                if(exchangeAction == null) {
                    return;
                }
                BigStore curBigStore = World.world.getHdv(exchangeAction.hdvId);

                switch (packet.charAt(3)) {
                    case '-'://Retirer un objet de l'HDV
                        int count = 0;
                        int  lineId = 0;
                        try {
                            lineId = Integer.parseInt(packet.substring(4).split("\\|")[0]);
                            count = Integer.parseInt(packet.substring(4).split("\\|")[1]);
                        } catch (Exception e) {
                            World.world.logger.error("Error Echange HDV '" + packet + "' => " + e.getMessage());
                            e.printStackTrace();
                            return;
                        }
                        if (count <= 0)
                            return;

                        if(!curBigStore.removeListing(this.player.getAccount(), lineId)) {
                            return;
                        }

                        SocketManager.GAME_SEND_EXCHANGE_OTHER_MOVE_OK(this, '-', "", lineId + "");
                        break;
                    case '+'://Mettre un objet en vente
                        if (Integer.parseInt(packet.substring(4).split("\\|")[1]) > 127) {
                            SocketManager.GAME_SEND_MESSAGE(this.player, this.player.getLang().trans("game.gameclient.movemenitemorkamas.limit"));
                            return;
                        }

                        int itmID;
                        int price = 0;
                        byte amount = 0;

                        try {
                            itmID = Integer.parseInt(packet.substring(4).split("\\|")[0]);
                            amount = Byte.parseByte(packet.substring(4).split("\\|")[1]);
                            price = Integer.parseInt(packet.substring(4).split("\\|")[2]);
                        } catch (Exception e) {
                            World.world.logger.error("Error Echange HDV '" + packet + "' => " + e.getMessage());
                            // Arrive quand price n'est pas dans le pacquet. C'est que le joueur ne veut pas mettre dans un hdv, mais dans autre chose ... Un paquet qui est MO+itmID|qt?
                            // Peeut-?tre apr?sa voir utilis? le concasseur ...
                            e.printStackTrace();
                            SocketManager.GAME_SEND_MESSAGE(this.player, "Une erreur s'est produite lors de la mise en vente de votre objet. Veuillez vous reconnectez pour corriger l'erreur. Personnage "
                                    + this.getPlayer().getName()
                                    + " et paquet " + packet + ".");
                            return;
                        }

                        if (amount <= 0 || price <= 0)
                            return;
                        if (packet.substring(1).split("\\|")[2] == "0"
                                || packet.substring(2).split("\\|")[2] == "0"
                                || packet.substring(3).split("\\|")[2] == "0")
                            return;

                        int taxe = (int) (price * (curBigStore.getTaxe() / 100));

                        if (taxe < 0)
                            return;

                        if (!this.player.hasItemGuid(itmID))//V?rifie si le this.playernnage a bien l'item sp?cifi? et l'argent pour payer la taxe
                            return;
                        if (this.player.getAccount().countHdvEntries(curBigStore.getHdvId()) >= curBigStore.getMaxAccountItem()) {
                            SocketManager.GAME_SEND_Im_PACKET(this.player, "058");
                            return;
                        }
                        if (this.player.getKamas() < taxe) {
                            SocketManager.GAME_SEND_Im_PACKET(this.player, "176");
                            return;
                        }

                        GameObject obj = World.world.getGameObject(itmID);//R?cup?re l'item
                        if (obj == null || obj.isAttach()) return;

                        this.player.addKamas(taxe * -1);//Retire le montant de la taxe au this.playernnage
                        SocketManager.GAME_SEND_STATS_PACKET(this.player);//Met a jour les kamas du client

                        int qua = (amount == 1 ? 1 : (amount == 2 ? 10 : 100));

                        if (qua > obj.getQuantity())//S'il veut mettre plus de cette objet en vente que ce qu'il poss?de
                            return;
                        int rAmount = (int) (Math.pow(10, amount) / 10);
                        int newQua = (obj.getQuantity() - rAmount);

                        if (newQua <= 0)//Si c'est plusieurs objets ensemble enleve seulement la quantit? de mise en vente
                        {
                            this.player.removeItem(itmID);//Enl?ve l'item de l'inventaire du this.playernnage
                            SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(this.player, itmID);//Envoie un packet au client pour retirer l'item de son inventaire
                        } else {
                            obj.setQuantity(obj.getQuantity() - rAmount);
                            SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player, obj);
                            GameObject newObj = obj.getClone(rAmount, true);
                            World.world.addGameObject(newObj);
                            obj = newObj;
                        }
                        // Client amount is 1,2,3, we need 0,1,2
                        BigStoreListing toAdd = new BigStoreListing(price, (byte) (amount-1), this.player.getAccount().getId(), obj);
                        if(!curBigStore.addEntry(toAdd)) {
                            return;
                        }
                        SocketManager.GAME_SEND_EXCHANGE_OTHER_MOVE_OK(this, '+', "", toAdd.parseToEmK()); //Envoie un packet pour ajthiser l'item dans la fenetre de l'HDV du client
                        SocketManager.GAME_SEND_HDVITEM_SELLING(this.player, toAdd.getHdvId());
                        DatabaseManager.get(PlayerData.class).update(this.player);
                        break;
                }
                break;

            case ExchangeAction.CRAFTING:
                int skillID = (Integer) this.player.getExchangeAction().getValue();

                switch(packet.charAt(2)) {
                    case 'O':
                        break;
                    case 'R':
                        break;
                    case 'r':
                        break;
                }

//                if (packet.charAt(2) == 'O' && ((JobAction) this.player.getExchangeAction().getValue()).getJobCraft() == null) {
//                    packet = packet.replace("-", ";-").replace("+", ";+").substring(4);
//
//                    for(String part : packet.split(";")) {
//                        try {
//                            char c = part.charAt(0);
//                            String[] infos = part.substring(1).split("\\|");
//                            int id = Integer.parseInt(infos[0]), quantity = 1;
//                            try {
//                                quantity = Integer.parseInt(infos[1]);
//                            } catch (Exception ignored) {}
//
//                            if (quantity <= 0) return;
//                            if (c == '+') {
//                                if (!this.player.hasItemGuid(id))
//                                    return;
//
//                                GameObject obj = this.player.getItems().get(id);
//
//                                if (obj == null || obj.getObvijevanLook() != 0) {
//                                    player.send("BN");
//                                    return;
//                                }
//                                if (obj.getQuantity() < quantity)
//                                    quantity = obj.getQuantity();
//
//                                ((JobAction) this.player.getExchangeAction().getValue()).addIngredient(this.player, id, quantity);
//                            } else if (c == '-') {
//                                ((JobAction) this.player.getExchangeAction().getValue()).addIngredient(this.player, id, -quantity);
//                            }
//                        } catch(Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                } else if (packet.charAt(2) == 'R') {
//                    if (((JobAction) this.player.getExchangeAction().getValue()).getJobCraft() == null) {
//                        ((JobAction) this.player.getExchangeAction().getValue()).setJobCraft(((JobAction) this.player.getExchangeAction().getValue()).oldJobCraft);
//                    }
//                    ((JobAction) this.player.getExchangeAction().getValue()).getJobCraft().setAction(Integer.parseInt(packet.substring(3)));
//                } else if (packet.charAt(2) == 'r') {
//                    if (this.player.getExchangeAction().getValue() != null) {
//                        if (((JobAction) this.player.getExchangeAction().getValue()).getJobCraft() != null) {
//                            ((JobAction) this.player.getExchangeAction().getValue()).broken = true;
//                        }
//                    }
//                }
                break;

            case ExchangeAction.IN_BANK:
                switch (packet.charAt(2)) {
                    case 'G'://Kamas
                        if (Main.tradeAsBlocked)
                            return;
                        long kamas = 0;
                        try {
                            kamas = Integer.parseInt(packet.substring(3));
                        } catch (Exception e) {
                            World.world.logger.error("Error Echange Banque '" + packet + "' => " + e.getMessage());
                            e.printStackTrace();
                            return;
                        }
                        if (kamas == 0)
                            return;

                        if (kamas > 0)//Si On ajoute des kamas a la banque
                        {
                            if (this.player.getKamas() < kamas)
                                kamas = this.player.getKamas();
                            this.player.setBankKamas(this.player.getBankKamas() + kamas);//On ajthise les kamas a la banque
                            this.player.setKamas(this.player.getKamas() - kamas);//On retire les kamas du this.playernnage
                            SocketManager.GAME_SEND_STATS_PACKET(this.player);
                            SocketManager.GAME_SEND_EsK_PACKET(this.player, "G"
                                    + this.player.getBankKamas());
                        } else {
                            kamas = -kamas;//On repasse en positif
                            if (this.player.getBankKamas() < kamas)
                                kamas = this.player.getBankKamas();
                            this.player.setBankKamas(this.player.getBankKamas() - kamas);//On retire les kamas de la banque
                            this.player.setKamas(this.player.getKamas() + kamas);//On ajthise les kamas du this.playernnage
                            SocketManager.GAME_SEND_STATS_PACKET(this.player);
                            SocketManager.GAME_SEND_EsK_PACKET(this.player, "G"
                                    + this.player.getBankKamas());
                        }
                        break;

                    case 'O'://Objet
                        if (Main.tradeAsBlocked)
                            return;
                        int guid = 0;
                        int qua = 0;
                        try {
                            guid = Integer.parseInt(packet.substring(4).split("\\|")[0]);
                            qua = Integer.parseInt(packet.substring(4).split("\\|")[1]);
                        } catch (Exception e) {
                            World.world.logger.error("Error Echange Banque '" + packet + "' => " + e.getMessage());
                            e.printStackTrace();
                            return;
                        }

                        if (guid == 0 || qua <= 0)
                            return;

                        switch (packet.charAt(3)) {
                            case '+'://Ajouter a la banque
                                this.player.addInBank(guid, qua, false);
                                break;

                            case '-'://Retirer de la banque
                                GameObject object = World.world.getGameObject(guid);
                                if(object != null) {
                                    if (object.getTxtStat().containsKey(Constant.STATS_OWNER_1)) {
                                        Player player = World.world.getPlayerByName(object.getTxtStat().get(Constant.STATS_OWNER_1));
                                        if (player != null) {
                                            if (!player.getName().equals(this.player.getName()))
                                                return;
                                        }
                                    }
                                    this.player.removeFromBank(guid, qua);
                                }
                                break;
                        }
                        break;
                }
                break;

            case ExchangeAction.IN_TRUNK:
                if (Main.tradeAsBlocked)
                    return;
                Trunk t = (Trunk) this.player.getExchangeAction().getValue();

                switch (packet.charAt(2)) {
                    case 'G'://Kamas
                        long kamas = 0;
                        try {
                            kamas = Integer.parseInt(packet.substring(3));
                        } catch (Exception e) {
                            World.world.logger.error("Error Echange Coffre '" + packet + "' => " + e.getMessage());
                            e.printStackTrace();
                            return;
                        }

                        if (kamas == 0)
                            return;

                        if (kamas > 0)//Si On ajthise des kamas au coffre
                        {
                            if (this.player.getKamas() < kamas)
                                kamas = this.player.getKamas();
                            t.setKamas(t.getKamas() + kamas);//On ajthise les kamas au coffre
                            this.player.setKamas(this.player.getKamas() - kamas);//On retire les kamas du this.playernnage
                            SocketManager.GAME_SEND_STATS_PACKET(this.player);
                        } else {
                            kamas = -kamas;//On repasse en positif
                            if (t.getKamas() < kamas)
                                kamas = t.getKamas();
                            t.setKamas(t.getKamas() - kamas);//On retire les kamas de la banque
                            this.player.setKamas(this.player.getKamas() + kamas);//On ajthise les kamas du this.playernnage
                            SocketManager.GAME_SEND_STATS_PACKET(this.player);
                        }
                        World.world.getOnlinePlayers().stream().filter(player -> player.getExchangeAction() != null &&
                                player.getExchangeAction().getType() == ExchangeAction.IN_TRUNK &&
                                ((Trunk) this.player.getExchangeAction().getValue()).getId() == ((Trunk) player.getExchangeAction().getValue()).getId())
                                .forEach(P -> SocketManager.GAME_SEND_EsK_PACKET(P, "G" + t.getKamas()));
                        DatabaseManager.get(TrunkData.class).update(t);
                        break;

                    case 'O'://Objet
                        int guid = 0;
                        int qua = 0;
                        try {
                            guid = Integer.parseInt(packet.substring(4).split("\\|")[0]);
                            qua = Integer.parseInt(packet.substring(4).split("\\|")[1]);
                        } catch (Exception e) {
                            World.world.logger.error("Error Echange Coffre '" + packet + "' => " + e.getMessage());
                            e.printStackTrace();
                            return;
                        }

                        if (guid == 0 || qua <= 0)
                            return;

                        switch (packet.charAt(3)) {
                            case '+'://Ajthiser a la banque
                                t.addInTrunk(guid, qua, this.player);
                                break;

                            case '-'://Retirer de la banque
                                t.removeFromTrunk(guid, qua, this.player);
                                break;
                        }
                        break;
                }
                break;

            case ExchangeAction.CRAFTING_SECURE_WITH:
            case ExchangeAction.TRADING_WITH_PLAYER:
                switch (packet.charAt(2)) {
                    case 'O'://Objet ?
                        if (packet.charAt(3) == '+') {
                            for(String arg : packet.substring(4).split("\\+")) {
                                String[] infos = arg.split("\\|");
                                try {
                                    int guid = Integer.parseInt(infos[0]);
                                    int qua = Integer.parseInt(infos[1]);
                                    int quaInExch = ((PlayerExchange) this.player.getExchangeAction().getValue()).getQuaItem(guid, this.player.getId());

                                    if (!this.player.hasItemGuid(guid))
                                        return;
                                    GameObject obj = this.player.getItems().get(guid);
                                    if (obj == null)
                                        return;
                                    if (qua > obj.getQuantity() - quaInExch)
                                        qua = obj.getQuantity() - quaInExch;

                                    if (qua <= 0 || obj.isAttach())
                                        return;

                                    ((PlayerExchange) this.player.getExchangeAction().getValue()).addItem(guid, qua, this.player.getId());
                                } catch (NumberFormatException e) {
                                    this.player.sendMessage("Error : PlayerExchange : " + packet + "\n" + e.getMessage());
                                    e.printStackTrace();
                                    return;
                                }
                            }
                        } else {
                            String[] infos = packet.substring(4).split("\\|");
                            try {
                                int guid = Integer.parseInt(infos[0]);
                                int qua = Integer.parseInt(infos[1]);

                                if (qua <= 0)
                                    return;
                                if (!this.player.hasItemGuid(guid))
                                    return;

                                GameObject obj = this.player.getItems().get(guid);
                                if (obj == null)
                                    return;
                                if (qua > ((PlayerExchange) this.player.getExchangeAction().getValue()).getQuaItem(guid, this.player.getId()))
                                    return;

                                ((PlayerExchange) this.player.getExchangeAction().getValue()).removeItem(guid, qua, this.player.getId());
                            } catch (NumberFormatException e) {
                                this.player.sendMessage("Error : PlayerExchange : " + packet + "\n" + e.getMessage());
                                e.printStackTrace();
                                return;
                            }
                        }
                        break;
                    case 'G'://Kamas
                        try {
                            if(packet.substring(3).contains("NaN")) return;
                            long numb = Integer.parseInt(packet.substring(3));
                            if (this.player.getKamas() < numb)
                                numb = this.player.getKamas();
                            if (numb < 0)
                                return;
                            ((PlayerExchange) this.player.getExchangeAction().getValue()).setKamas(this.player.getId(), numb);
                        } catch (NumberFormatException e) {
                            World.world.logger.error("Error Echange PvP '" + packet + "' => " + e.getMessage());
                            e.printStackTrace();
                            return;
                        }
                        break;
                }
                break;
        }
    }

    private void recursiveBreakingObject(BreakingObject breakingObject, final int i, int count) {
        if (breakingObject.isStop() || !(i < count)) {
            if (breakingObject.isStop()) this.player.send("Ea2");
            else this.player.send("Ea1");
            breakingObject.setStop(false);
            return;
        }

        TimerWaiter.addNext(() -> {
            this.player.send("EA" + (breakingObject.getCount() - i));
            ArrayList<Couple<Integer, Integer>> objects = new ArrayList<>(breakingObject.getObjects());
            this.ready();
            breakingObject.setObjects(objects);
            this.recursiveBreakingObject(breakingObject, i + 1, count);
        }, 1000, TimeUnit.MILLISECONDS);
    }

    private synchronized void movementItemOrKamasDons(String packet) {
        if (this.player.getExchangeAction() != null && this.player.getExchangeAction().getType() == ExchangeAction.CRAFTING_SECURE_WITH) {
            if (((CraftSecure) this.player.getExchangeAction().getValue()).getNeeder() == this.player) {
                byte type = Byte.parseByte(String.valueOf(packet.charAt(0)));
                switch (packet.charAt(1)) {
                    case 'O':
                        String[] split = packet.substring(3).split("\\|");
                        boolean adding = packet.charAt(2) == '+';
                        int guid = Integer.parseInt(split[0]), quantity = Integer.parseInt(split[1]);

                        ((CraftSecure) this.player.getExchangeAction().getValue()).setPayItems(type, adding, guid, quantity);
                        break;
                    case 'G':
                        ((CraftSecure) this.player.getExchangeAction().getValue()).setPayKamas(type, Integer.parseInt(packet.substring(2)));
                        break;
                }
            }
        }
    }

    private void askOfflineExchange() {
        if(EventManager.isInEvent(this.player))
            return;
        if (this.player.getExchangeAction() != null || this.player.getFight() != null || this.player.isAway())
            return;
        if (this.player.parseStoreItemsList().isEmpty()) {
            SocketManager.GAME_SEND_Im_PACKET(this.player, "123");
            return;
        }
        if (SoulStone.isInArenaMap(this.player.getCurMap().getId()) || this.player.getCurMap().data.noSellers) {
            SocketManager.GAME_SEND_Im_PACKET(this.player, "113");
            return;
        }
        if (this.player.getCurMap().getId() == 33 || this.player.getCurMap().getId() == 38 || this.player.getCurMap().getId() == 4601 || this.player.getCurMap().getId() == 4259 || this.player.getCurMap().getId() == 8036 || this.player.getCurMap().getId() == 10301) {
            if (this.player.getCurMap().getStoreCount() >= 25) {
                SocketManager.GAME_SEND_Im_PACKET(this.player, "125;25");
                return;
            }
        } else if (this.player.getCurMap().getStoreCount() >= 6) {
            SocketManager.GAME_SEND_Im_PACKET(this.player, "125;6");
            return;
        }
        for (Map.Entry<Integer, Integer> entry : this.player.getStoreItems().entrySet()) {
            if (entry.getValue() <= 0) {
                this.kick();
                return;
            }
        }


        long taxe = this.player.storeAllBuy() / 1000;

        if (taxe < 0) {
            this.kick();
            return;
        }

        SocketManager.GAME_SEND_Eq_PACKET(this.player, taxe);
    }

    private void offlineExchange() {
        if(EventManager.isInEvent(this.player))
            return;
        if (SoulStone.isInArenaMap(this.player.getCurMap().getId()) || this.player.getCurMap().data.noSellers) {
            SocketManager.GAME_SEND_Im_PACKET(this.player, "113");
            return;
        }
        if (this.player.getCurMap().getId() == 33 || this.player.getCurMap().getId() == 38 || this.player.getCurMap().getId() == 4601 || this.player.getCurMap().getId() == 4259 || this.player.getCurMap().getId() == 8036 || this.player.getCurMap().getId() == 10301) {
            if (this.player.getCurMap().getStoreCount() >= 25) {
                SocketManager.GAME_SEND_Im_PACKET(this.player, "125;25");
                return;
            }
        } else if (this.player.getCurMap().getStoreCount() >= 6) {
            SocketManager.GAME_SEND_Im_PACKET(this.player, "125;6");
            return;
        }
        long taxe = this.player.storeAllBuy() / 1000;
        if (this.player.getKamas() < taxe) {
            SocketManager.GAME_SEND_Im_PACKET(this.player, "176");
            return;
        }
        if (taxe < 0) {
            SocketManager.GAME_SEND_MESSAGE(this.player, this.player.getLang().trans("game.gameclient.offlineexchange.negative"));
            return;
        }
        int orientation = Formulas.getRandomValue(1, 3);
        this.player.setKamas(this.player.getKamas() - taxe);
        this.player.set_orientation(orientation);
        GameMap map = this.player.getCurMap();
        this.player.setShowSeller(true);
        World.world.addSeller(this.player);
        this.kick();
        map.getPlayers().stream().filter(player -> player != null && player.isOnline()).forEach(SocketManager::GAME_SEND_MERCHANT_LIST);
    }

    private synchronized void putInInventory(String packet) {
        if(this.player.getExchangeAction() != null && this.player.getExchangeAction().getType() == ExchangeAction.IN_MOUNTPARK) {
            int id = -1;
            MountPark park = this.player.getCurMap().getMountPark();

            park = park == null ? (player.getCurMap().getId() != 10332 ? World.world.getMap( 8743) : World.world.getMap( 8848)).getMountPark() : park;
            if(park == null) return;

            try { id = Integer.parseInt(packet.substring(3)); } catch (Exception ignored) {}

            switch (packet.charAt(2)) {
                case 'C':// Certificats -> Etable
                    if(id == -1 || !this.player.hasItemGuid(id))
                        return;
                    if(park.hasEtableFull(this.player.getId())) {
                        this.player.send("Im1105");
                        return;
                    }

                    GameObject object = World.world.getGameObject(id);
                    Mount mount = World.world.getMountById(object.getStats().getEffect(995));

                    if(mount == null){
                        /*int color = Constant.getMountColorByParchoTemplate(object.getTemplate().getId());
						if (color < 1)
							return;
						mount = new Mount(color, this.player.getId(), false);*/
                        return;
                    }
                    mount.setOwner(this.player.getId());
                    this.player.removeItem(id);
                    World.world.removeGameObject(id);

                    if(!park.getEtable().contains(mount))
                        park.getEtable().add(mount);

                    SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(this.player, object.getGuid());

                    DatabaseManager.get(MountData.class).update(mount);
                    DatabaseManager.get(PlayerData.class).update(this.player);
                    SocketManager.GAME_SEND_Ee_PACKET(this.player, mount.getSize() == 50 ? '~' : '+', mount.parse());
                    break;

                case 'c':// Etable -> Certificats
                    mount = World.world.getMountById(id);

                    if(((mount = park.containsMountInList(park.getEtable(), mount)) == null) || mount == null)
                        return;

                    park.getEtable().remove(mount);
                    mount.setOwner(this.player.getId());

                    object = Constant.getParchoTemplateByMountColor(mount.getColor()).createNewItem(1, false);
                    object.setMountStats(this.player, mount, false);

                    World.world.addGameObject(object);
                    this.player.addItem(object, true);

                    SocketManager.GAME_SEND_Ee_PACKET(this.player, '-', mount.getId() + "");
                    DatabaseManager.get(MountData.class).update(mount);
                    DatabaseManager.get(PlayerData.class).update(this.player);
                    break;

                case 'g':// Equiper une dinde
                    mount = World.world.getMountById(id);

                    if(((mount = park.containsMountInList(park.getEtable(), mount)) == null)  || mount == null) {
                        SocketManager.GAME_SEND_Im_PACKET(this.player, "1104");
                        return;
                    }
                    if(this.player.getMount() != null) {
                        SocketManager.GAME_SEND_BN(this);
                        return;
                    }
                    if(mount.getFecundatedDate() != -1) {
                        SocketManager.GAME_SEND_BN(this);
                        return;
                    }

                    mount.setOwner(this.player.getId());
                    park.getEtable().remove(mount);
                    this.player.setMount(mount);

                    SocketManager.GAME_SEND_Re_PACKET(this.player, "+", mount);
                    SocketManager.GAME_SEND_Ee_PACKET(this.player, '-', mount.getId() + "");
                    SocketManager.GAME_SEND_Rx_PACKET(this.player);
                    DatabaseManager.get(MountData.class).update(mount);
                    DatabaseManager.get(PlayerData.class).update(this.player);
                    break;

                case 'p':// Equipe -> Etable
                    if(this.player.getMount() != null && this.player.getMount().getId() == id) {
                        if(park.hasEtableFull(this.player.getId())) {
                            this.player.send("Im1105");
                            return;
                        }

                        mount = this.player.getMount();
                        if(mount.getObjects().size() == 0) {
                            if(this.player.isOnMount())
                                this.player.toogleOnMount();

                            if(!park.getEtable().contains(mount))
                                park.getEtable().add(mount);

                            mount.setOwner(this.player.getId());
                            this.player.setMount(null);

                            DatabaseManager.get(MountData.class).update(mount);
                            SocketManager.GAME_SEND_Ee_PACKET(this.player, mount.getSize() == 50 ? '~' : '+', mount.parse());
                            SocketManager.GAME_SEND_Re_PACKET(this.player, "-", null);
                            SocketManager.GAME_SEND_Rx_PACKET(this.player);
                        } else {
                            SocketManager.GAME_SEND_Im_PACKET(this.player, "1106");
                        }
                        DatabaseManager.get(MountData.class).update(mount);
                        DatabaseManager.get(PlayerData.class).update(this.player);
                    }
                    break;
            }
            DatabaseManager.get(BaseMountParkData.class).update(park);
        }
    }

    private synchronized void putInMountPark(String packet) {
        if(this.player.getExchangeAction() != null && this.player.getExchangeAction().getType() == ExchangeAction.IN_MOUNTPARK) {
            int id = -1;
            GameMap map = this.player.getCurMap();
            MountPark park = this.player.getCurMap().getMountPark();
            if(park == null) return;
            try { id = Integer.parseInt(packet.substring(3));
            } catch (Exception ignored) {}

            switch (packet.charAt(2)) {
                case 'g':// Enclos -> Etable
                    if(park.hasEtableFull(this.player.getId())) {
                        this.player.send("Im1105");
                        return;
                    }

                    Mount mount = World.world.getMountById(id);
                    if(!park.getEtable().contains(mount) && park.getListOfRaising().contains(id)) {
                        park.getEtable().add(mount);
                        park.delRaising(mount.getId());

                        mount.setOwner(this.player.getId());
                        this.player.getCurMap().getMountPark().delRaising(id);
                        SocketManager.GAME_SEND_Ef_MOUNT_TO_ETABLE(this.player, '-', mount.getId() + "");

                        SocketManager.GAME_SEND_Ee_PACKET(this.player, mount.getSize() == 50 ? '~' : '+', mount.parse());
                        SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(this.player.getCurMap(), id);
                        mount.setMapId((short) -1);
                        mount.setCellId(-1);

                        DatabaseManager.get(MountData.class).update(mount);
                        DatabaseManager.get(PlayerData.class).update(this.player);
                    } else return;
                    break;

                case 'p':// Etable -> Enclos

                    if(this.player.getMount() != null) {
                        if(this.player.getMount().getObjects().size() != 0) {
                            SocketManager.GAME_SEND_Im_PACKET(this.player, "1106");
                            return;
                        }
                    }

                    if(park.hasEnclosFull(this.player.getId())) {
                        this.player.send("Im1107");
                        return;
                    }

                    if(this.player.getMount() != null && this.player.getMount().getId() == id) {
                        if (this.player.isOnMount())
                            this.player.toogleOnMount();
                        if(this.player.isOnMount())
                            return;
                        this.player.setMount(null);
                    }

                    mount = World.world.getMountById(id);


                    if((mount = park.containsMountInList(park.getEtable(), mount)) != null) {
                        mount.setOwner(this.player.getId());
                        mount.setMapId(park.getMap());
                        mount.setCellId(park.getPlaceOfSpawn());
                        park.getEtable().remove(mount);
                        park.addRaising(id);
                        SocketManager.GAME_SEND_Ef_MOUNT_TO_ETABLE(this.player, '+', mount.parse());
                        SocketManager.GAME_SEND_Ee_PACKET(this.player, '-', mount.getId() + "");
                        SocketManager.GAME_SEND_GM_MOUNT_TO_MAP(map, mount);

                        DatabaseManager.get(MountData.class).update(mount);
                        DatabaseManager.get(PlayerData.class).update(this.player);
                    } else return;
                    break;
            }
            DatabaseManager.get(BaseMountParkData.class).update(park);
        }
    }

    private void request(String packet) {
        if (this.player.getExchangeAction() != null && this.player.getExchangeAction().getType() != ExchangeAction.AUCTION_HOUSE_BUYING && this.player.getExchangeAction().getType() != ExchangeAction.AUCTION_HOUSE_SELLING) {
            SocketManager.GAME_SEND_EXCHANGE_REQUEST_ERROR(this, 'O');
            return;
        }

        if (packet.substring(2, 4).equals("13") && this.player.getExchangeAction() == null) { // Craft s?curis? : celui qui n'a pas le job ( this.player ) souhaite invit? player
            try {
                String[] split = packet.split("\\|");
                int id = Integer.parseInt(split[1]);
                int skill = Integer.parseInt(split[2]);

                Player player = World.world.getPlayer(id);

                if (player == null) {
                    SocketManager.GAME_SEND_EXCHANGE_REQUEST_ERROR(this, 'E');
                    return;
                }
                if (player.getCurMap() != this.player.getCurMap() || !player.isOnline()) {
                    SocketManager.GAME_SEND_EXCHANGE_REQUEST_ERROR(this, 'E');
                    return;
                }
                if (player.isAway() || this.player.isAway()) {
                    SocketManager.GAME_SEND_EXCHANGE_REQUEST_ERROR(this, 'O');
                    return;
                }

                List<Job> jobs = player.getJobs();

                if (jobs == null || jobs.isEmpty())
                    return;

                GameObject object = player.getObjetByPos(Constant.ITEM_POS_ARME);

                if (object == null) {
                    this.player.send("BN");
                    return;
                }
                boolean ok = false;

                for (Job job : jobs) {
                    if (job.getSkills().isEmpty())
                        continue;
                    if (!job.isValidTool(object.getTemplate().getId()))
                        continue;

                    for (GameCase cell : this.player.getCurMap().getCases()) {
//                        if (cell.getObject() != null) {
//                            if (cell.getObject().getTemplate() != null) {
//                                int io = cell.getObject().getTemplate().getId();
//                                ArrayList<Integer> skills = job.getSkills().get(io);
//
//                                if (skills != null) {
//                                    for (int arg : skills) {
//                                        if (arg == skill
//                                                && PathFinding.getDistanceBetween(player.getCurMap(), player.getCurCell().getId(), cell.getId()) < 4) {
//                                            ok = true;
//                                            break;
//                                        }
//                                    }
//                                }
//                            }
//                        }
                    }

                    if (ok)
                        break;
                }

                if (!ok) {
                    this.player.send("ERET");
                    return;
                }

                ExchangeAction<Integer> exchangeAction = new ExchangeAction<>(ExchangeAction.CRAFTING_SECURE_WITH, id);
                this.player.setExchangeAction(exchangeAction);
                ExchangeAction<Integer> exchangeAction1 = new ExchangeAction<>(ExchangeAction.CRAFTING_SECURE_WITH, this.player.getId());
                player.setExchangeAction(exchangeAction1);

                this.player.getIsCraftingType().add(13);
                player.getIsCraftingType().add(12);
                this.player.getIsCraftingType().add(skill);
                player.getIsCraftingType().add(skill);

                SocketManager.GAME_SEND_EXCHANGE_REQUEST_OK(this, this.player.getId(), id, 12);
                SocketManager.GAME_SEND_EXCHANGE_REQUEST_OK(player.getGameClient(), this.player.getId(), id, 12);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return;
        } else if (packet.substring(2, 4).equals("12") && this.player.getExchangeAction() == null) { // Craft s?curis? : celui qui ? le job ( this.player ) souhaite invit? player
            try {
                String[] split = packet.split("\\|");
                int id = Integer.parseInt(split[1]);
                int skill = Integer.parseInt(split[2]);

                Player player = World.world.getPlayer(id);

                if (player == null) {
                    SocketManager.GAME_SEND_EXCHANGE_REQUEST_ERROR(this, 'E');
                    return;
                }
                if (player.getCurMap() != this.player.getCurMap() || !player.isOnline()) {
                    SocketManager.GAME_SEND_EXCHANGE_REQUEST_ERROR(this, 'E');
                    return;
                }
                if (player.isAway() || this.player.isAway()) {
                    SocketManager.GAME_SEND_EXCHANGE_REQUEST_ERROR(this, 'O');
                    return;
                }

                List<Job> jobs = this.player.getJobs();
                if (jobs == null || jobs.isEmpty()) return;

                GameObject object = this.player.getObjetByPos(Constant.ITEM_POS_ARME);
                if (object == null) return;

                boolean ok = false;

                for (Job job : jobs) {
                    if (job.getSkills().isEmpty() || !job.isValidTool(object.getTemplate().getId())) continue;
//                    for (GameCase cell : this.player.getCurMap().getCases()) {
//                        if (cell.getObject() != null) {
//                            if (cell.getObject().getTemplate() != null) {
//                                int io = cell.getObject().getTemplate().getId();
//                                ArrayList<Integer> skills = job.getSkills().get(io);
//
//                                if (skills != null) {
//                                    for (int arg : skills) {
//                                        if (arg == skill && PathFinding.getDistanceBetween(this.player.getCurMap(), this.player.getCurCell().getId(), cell.getId()) < 4) {
//                                            ok = true;
//                                            break;
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
                    if (ok) break;
                }

                if (!ok) {
                    this.player.sendMessage(player.getLang().trans("game.gameclient.atelier.tofar"));
                    return;
                }

                ExchangeAction<Integer> exchangeAction = new ExchangeAction<>(ExchangeAction.CRAFTING_SECURE_WITH, id);
                this.player.setExchangeAction(exchangeAction);
                exchangeAction = new ExchangeAction<>(ExchangeAction.CRAFTING_SECURE_WITH, this.player.getId());
                player.setExchangeAction(exchangeAction);

                this.player.getIsCraftingType().add(12);
                player.getIsCraftingType().add(13);
                this.player.getIsCraftingType().add(skill);
                player.getIsCraftingType().add(skill);

                SocketManager.GAME_SEND_EXCHANGE_REQUEST_OK(this, this.player.getId(), id, 12);
                SocketManager.GAME_SEND_EXCHANGE_REQUEST_OK(player.getGameClient(), this.player.getId(), id, 13);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return;
        } else if (packet.substring(2, 4).equals("11")) {//Ouverture HDV achat
            if(this.player.getExchangeAction() != null) leaveExchange(this.player);
            if (this.player.getDeshonor() >= 5) {
                SocketManager.GAME_SEND_Im_PACKET(this.player, "183");
                return;
            }

            BigStore bigStore = World.world.getHdv(this.player.getCurMap().getId());
            if (bigStore != null) {
                String info = "1,10,100;" + bigStore.getStrCategory() + ";" + bigStore.parseTaxe() + ";" + bigStore.getLvlMax() + ";" + bigStore.getMaxAccountItem() + ";-1;" + bigStore.getDuration();
                SocketManager.GAME_SEND_ECK_PACKET(this.player, 11, info);
                ExchangeAction<BigStoreActionData> exchangeAction = new ExchangeAction<>(ExchangeAction.AUCTION_HOUSE_BUYING, new BigStoreActionData(this.player.getCurMap().getId()));
                this.player.setExchangeAction(exchangeAction);
            }
            return;
        } else if (packet.substring(2, 4).equals("15") && this.player.getExchangeAction() == null) {
            Mount mount = this.player.getMount();

            if(mount != null) {
                ExchangeAction<Integer> exchangeAction = new ExchangeAction<>(ExchangeAction.IN_MOUNT, mount.getId());
                this.player.setExchangeAction(exchangeAction);

                SocketManager.GAME_SEND_ECK_PACKET(this, 15, String.valueOf(mount.getId()));
                SocketManager.GAME_SEND_EL_MOUNT_PACKET(this.player, mount);
                SocketManager.GAME_SEND_Ew_PACKET(this.player, mount.getActualPods(), mount.getMaxPods());
            }
            return;
        } else if (packet.substring(2, 4).equals("17") && this.player.getExchangeAction() == null) {//Ressurection famillier
            int id = Integer.parseInt(packet.substring(5));

            if (this.player.getCurMap().getNpc(id) != null) {
                PlayerExchange.NpcRessurectPets ech = new PlayerExchange.NpcRessurectPets(this.player, this.player.getCurMap().getNpc(id).getTemplate());
                ExchangeAction<PlayerExchange.NpcRessurectPets> exchangeAction = new ExchangeAction<>(ExchangeAction.TRADING_WITH_NPC_PETS_RESURRECTION, ech);
                this.player.setExchangeAction(exchangeAction);
                SocketManager.GAME_SEND_ECK_PACKET(this.player, 9, String.valueOf(id));
            }
        } else if (packet.substring(2, 4).equals("10")) {//Ouverture HDV vente
            if(this.player.getExchangeAction() != null) leaveExchange(this.player);
            if (this.player.getDeshonor() >= 5) {
                SocketManager.GAME_SEND_Im_PACKET(this.player, "183");
                return;
            }

            BigStore bigStore = World.world.getHdv(this.player.getCurMap().getId());
            if (bigStore != null) {
                String infos = "1,10,100;" + bigStore.getStrCategory() + ";" + bigStore.parseTaxe() + ";" + bigStore.getLvlMax() + ";" + bigStore.getMaxAccountItem() + ";-1;" + bigStore.getDuration();
                SocketManager.GAME_SEND_ECK_PACKET(this.player, 10, infos);
                BigStoreActionData data = new BigStoreActionData(this.player.getCurMap().getId());
                ExchangeAction<BigStoreActionData> exchangeAction = new ExchangeAction<>(ExchangeAction.AUCTION_HOUSE_SELLING, data);
                this.player.setExchangeAction(exchangeAction);
                SocketManager.GAME_SEND_HDVITEM_SELLING(this.player, data.hdvId);
            }
            return;
        } else if(packet.substring(2, 4).equals("18")) {
            int id = Integer.parseInt(packet.split("\\|")[1]);
            if (this.player.getCurMap().getNpc(id) != null) {
                NpcExchange ech = new NpcExchange(this.player, this.player.getCurMap().getNpc(id).getTemplate());
                ExchangeAction<NpcExchange> exchangeAction = new ExchangeAction<>(ExchangeAction.TRADING_WITH_NPC_EXCHANGE, ech);
                this.player.setExchangeAction(exchangeAction);
                SocketManager.GAME_SEND_ECK_PACKET(this.player, 2, String.valueOf(id));
            }
            return;
        }
        if (this.player.getExchangeAction() != null) {
            SocketManager.GAME_SEND_EXCHANGE_REQUEST_ERROR(this, 'O');
            return;
        }
        switch (packet.charAt(2)) {
            case '0'://Si NPC
                int id = Integer.parseInt(packet.substring(4));
                Npc npc = this.player.getCurMap().getNpc(id);

                if (npc != null) {
                    ExchangeAction<Integer> exchangeAction = new ExchangeAction<>(ExchangeAction.TRADING_WITH_NPC, id);
                    this.player.setExchangeAction(exchangeAction);

                    SocketManager.GAME_SEND_ECK_PACKET(this, 0, String.valueOf(id));
                    SocketManager.GAME_SEND_ITEM_VENDOR_LIST_PACKET(this, npc.getTemplate().salesList(this.player));
                }
                break;
            case '1'://Si joueur
                try {
                    id = Integer.parseInt(packet.substring(4));
                    Player target = World.world.getPlayer(id);

                    if (target == null || target.getCurMap() != this.player.getCurMap() || !target.isOnline()) {
                        SocketManager.GAME_SEND_EXCHANGE_REQUEST_ERROR(this, 'E');
                        return;
                    }
                    if (target.isAway() || this.player.isAway() || target.getExchangeAction() != null || this.player.getExchangeAction() != null) {
                        SocketManager.GAME_SEND_EXCHANGE_REQUEST_ERROR(this, 'O');
                        return;
                    }
                    if (target.getGroup() != null && this.player.getGroup() == null) {
                        if (!target.getGroup().isPlayer()) {
                            SocketManager.GAME_SEND_EXCHANGE_REQUEST_ERROR(this, 'E');
                            return;
                        }
                    }
                    ExchangeAction<Integer> exchangeAction = new ExchangeAction<>(ExchangeAction.TRADING_WITH_PLAYER, id);
                    this.player.setExchangeAction(exchangeAction);
                    exchangeAction = new ExchangeAction<>(ExchangeAction.TRADING_WITH_PLAYER, this.player.getId());
                    target.setExchangeAction(exchangeAction);

                    this.player.getIsCraftingType().add(1);
                    target.getIsCraftingType().add(1);

                    SocketManager.GAME_SEND_EXCHANGE_REQUEST_OK(this, this.player.getId(), id, 1);
                    SocketManager.GAME_SEND_EXCHANGE_REQUEST_OK(target.getGameClient(), this.player.getId(), id, 1);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                break;
            case '2'://Npc Exchange
                id = Integer.parseInt(packet.substring(4));
                if (this.player.getCurMap().getNpc(id) != null) {
                    NpcExchange ech = new NpcExchange(this.player, this.player.getCurMap().getNpc(id).getTemplate());

                    ExchangeAction<NpcExchange> exchangeAction = new ExchangeAction<>(ExchangeAction.TRADING_WITH_NPC_EXCHANGE, ech);
                    this.player.setExchangeAction(exchangeAction);
                    SocketManager.GAME_SEND_ECK_PACKET(this.player, 2, String.valueOf(id));
                }
                break;
            case '4'://StorePlayer
                id = Integer.valueOf(packet.split("\\|")[1]);

                Player seller = World.world.getPlayer(id);
                if (seller == null || !seller.isShowSeller() || seller.getCurMap() != this.player.getCurMap()) return;

                ExchangeAction<Integer> exchangeAction = new ExchangeAction<>(ExchangeAction.TRADING_WITH_OFFLINE_PLAYER, id);
                this.player.setExchangeAction(exchangeAction);

                SocketManager.GAME_SEND_ECK_PACKET(this.player, 4, String.valueOf(seller.getId()));
                SocketManager.GAME_SEND_ITEM_LIST_PACKET_SELLER(seller, this.player);
                break;
            case '6'://StoreItems
                exchangeAction = new ExchangeAction<>(ExchangeAction.TRADING_WITH_ME, this.player.getId());
                this.player.setExchangeAction(exchangeAction);

                SocketManager.GAME_SEND_ECK_PACKET(this.player, 6, "");
                SocketManager.GAME_SEND_ITEM_LIST_PACKET_SELLER(this.player, this.player);
                break;
            case '8'://Si Collector
                Collector collector = World.world.getCollector(Integer.parseInt(packet.substring(4)));
                if (collector == null || collector.getInFight() > 0 || collector.getExchange() || collector.getGuildId() != this.player.getGuild().getId() || collector.getMap() != this.player.getCurMap().getId())
                    return;
                if (!this.player.getGuildMember().canDo(Constant.G_COLLPERCO)) {
                    SocketManager.GAME_SEND_Im_PACKET(this.player, "1101");
                    return;
                }
                collector.setExchange(true);
                exchangeAction = new ExchangeAction<>(ExchangeAction.TRADING_WITH_COLLECTOR, collector.getId());
                this.player.setExchangeAction(exchangeAction);
                this.player.DialogTimer();

                SocketManager.GAME_SEND_ECK_PACKET(this, 8, String.valueOf(collector.getId()));
                SocketManager.GAME_SEND_ITEM_LIST_PACKET_PERCEPTEUR(this, collector);
                break;
            case '9'://D?pos?/Retir? un familier
                id = Integer.parseInt(packet.substring(4));

                if (this.player.getCurMap().getNpc(id) != null) {
                    PlayerExchange.NpcExchangePets ech = new PlayerExchange.NpcExchangePets(this.player, this.player.getCurMap().getNpc(id).getTemplate());
                    this.player.setExchangeAction(new ExchangeAction<>(ExchangeAction.TRADING_WITH_NPC_PETS, ech));
                    SocketManager.GAME_SEND_ECK_PACKET(this.player, 9, String.valueOf(id));
                }
                break;
        }
    }

    private void sell(String packet) {
        try {
            String[] infos = packet.substring(2).split("\\|");
            int id = Integer.parseInt(infos[0]), quantity = Integer.parseInt(infos[1]);

            if (!this.player.hasItemGuid(id)) {
                SocketManager.GAME_SEND_SELL_ERROR_PACKET(this);
                return;
            }

            this.player.sellItem(id, quantity);
        } catch (Exception e) {
            e.printStackTrace();
            SocketManager.GAME_SEND_SELL_ERROR_PACKET(this);
        }
    }

    private void bookOfArtisant(String packet) {
        switch (packet.charAt(2)) {
            case 'F':
                int Metier = Integer.parseInt(packet.substring(3));
                int cant = 0;
                for (Player artissant : World.world.getOnlinePlayers()) {
                    if (!artissant.getMetierPublic() || artissant.getMetiers().isEmpty())
                        continue;
                    String send = "";
                    int id = artissant.getId();
                    String name = artissant.getName();
                    String color = artissant.getColor1() + "," + artissant.getColor2() + "," + artissant.getColor3();
                    String accesoire = artissant.getGMStuffString();
                    int sex = artissant.getSexe();
                    int map = artissant.getCurMap().getId();
                    int inJob = (map == 8731 || map == 8732) ? 1 : 0;
                    int classe = artissant.getClasse();
                    for (JobStat SM : artissant.getMetiers().values()) {
                        if (SM.getTemplate().getId() != Metier)
                            continue;
                        cant++;
                        send = "+" + SM.getTemplate().getId() + ";" + id + ";"
                                + name + ";" + SM.get_lvl() + ";" + map + ";"
                                + inJob + ";" + classe + ";" + sex + ";"
                                + color + ";" + accesoire + ";"
                                + SM.getOptBinValue() + ","
                                + SM.getSlotsPublic();
                        SocketManager.SEND_EJ_LIVRE(this.player, send);
                    }
                }
                if (cant == 0)
                    SocketManager.GAME_SEND_MESSAGE(this.player, this.player.getLang().trans("game.gameclient.bookofartisant.noartisan"));
                break;
        }
    }

    private void setPublicMode(String packet) {
        switch (packet.charAt(2)) {
            case '+':
                this.player.setMetierPublic(true);
                String metier = "";
                boolean first = false;
                for (JobStat SM : this.player.getMetiers().values()) {
                    SocketManager.SEND_Ej_LIVRE(this.player, "+" + SM.getTemplate().getId());
                    if (first)
                        metier += ";";
                    metier += JobConstant.actionMetier(SM.getTemplate().getId());
                    first = true;
                }
                SocketManager.SEND_EW_METIER_PUBLIC(this.player, "+");
                SocketManager.SEND_EW_METIER_PUBLIC(this.player, "+" + this.player.getId() + "|" + metier);
                break;

            case '-':
                this.player.setMetierPublic(false);
                for (JobStat metiers : this.player.getMetiers().values()) {
                    SocketManager.SEND_Ej_LIVRE(this.player, "-"
                            + metiers.getTemplate().getId());
                }
                SocketManager.SEND_EW_METIER_PUBLIC(this.player, "-");
                SocketManager.SEND_EW_METIER_PUBLIC(this.player, "-" + this.player.getId());
                break;
        }
    }

    public static void leaveExchange(Player player) {
        ExchangeAction<?> exchangeAction = player.getExchangeAction();

        if (exchangeAction == null)
            return;

        switch(exchangeAction.getType()) {
            case ExchangeAction.TRADING_WITH_PLAYER:
                if(exchangeAction.getValue() instanceof Integer) {
                    Player target = World.world.getPlayer((Integer) exchangeAction.getValue());
                    if(target != null && target.getExchangeAction() != null && target.getExchangeAction().getType() == ExchangeAction.TRADING_WITH_PLAYER) {
                        target.send("EV");
                        target.setExchangeAction(null);
                    }
                } else {
                    ((PlayerExchange) exchangeAction.getValue()).cancel();
                }
                break;
            case ExchangeAction.TRADING_WITH_NPC_PETS:
                ((PlayerExchange.NpcExchangePets) exchangeAction.getValue()).cancel();
                break;
            case ExchangeAction.TRADING_WITH_NPC_EXCHANGE:
                ((NpcExchange) exchangeAction.getValue()).cancel();
                break;
            case ExchangeAction.CRAFTING_SECURE_WITH:
                if(exchangeAction.getValue() instanceof Integer) {
                    Player target = World.world.getPlayer((Integer) exchangeAction.getValue());
                    if(target != null && target.getExchangeAction() != null && target.getExchangeAction().getType() == ExchangeAction.CRAFTING_SECURE_WITH) {
                        target.send("EV");
                        target.setExchangeAction(null);
                    }
                } else {
                    ((CraftSecure) exchangeAction.getValue()).cancel();
                }
                break;
            case ExchangeAction.CRAFTING:
                player.send("EV");
                player.setDoAction(false);
                break;

            case ExchangeAction.BREAKING_OBJECTS:
                ((BreakingObject) exchangeAction.getValue()).setStop(true);
                player.send("EV");
                break;
            case ExchangeAction.TRADING_WITH_NPC:
                player.send("EV");
                if (player.getCurMap().getId() == 10129){
                    player.setExchangeAction(null);
                    try {
                        player.getGameClient().parsePacket("DC-1");
                        return;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case ExchangeAction.IN_MOUNT:
                player.send("EV");
                break;
            case ExchangeAction.IN_MOUNTPARK:
                player.send("EV");
                ArrayList<GameObject> objects = new ArrayList<>();
                for(GameObject object : player.getItems().values()) {
                    Mount mount = World.world.getMountById(object.getStats().getEffect(995));

                    if(mount == null && object.getTemplate().getType() == Constant.ITEM_TYPE_CERTIF_MONTURE)
                        objects.add(object);
                }
                for(GameObject object : objects)
                    player.removeItem(object.getGuid(), object.getQuantity(), true, true);
                break;

            case ExchangeAction.IN_TRUNK:
                ((Trunk) exchangeAction.getValue()).setPlayer(null);
                player.send("EV");
                break;

            case ExchangeAction.TRADING_WITH_COLLECTOR:
                Collector collector = World.world.getCollector((Integer) exchangeAction.getValue());
                if (collector == null) return;
                for (Player loc : World.world.getGuild(collector.getGuildId()).getPlayers()) {
                    if (loc != null && loc.isOnline()) {
                        SocketManager.GAME_SEND_gITM_PACKET(loc, org.starloco.locos.entity.Collector.parseToGuild(loc.getGuild().getId()));
                        String str = "";
                        str += "G" + collector.getFullName();
                        str += "|.|" + World.world.getMap(collector.getMap()).getX() + "|" + World.world.getMap(collector.getMap()).getY() + "|";
                        str += player.getName() + "|" + (collector.getXp());
                        if (!collector.getLogObjects().equals("")) str += collector.getLogObjects();
                        SocketManager.GAME_SEND_gT_PACKET(loc, str);
                    }
                }
                player.getGuildMember().giveXpToGuild(collector.getXp());
                player.getCurMap().RemoveNpc(collector.getId());
                SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(player.getCurMap(), collector.getId());
                collector.reloadTimer();
                collector.delCollector(collector.getId());
                player.send("EV");
                DatabaseManager.get(CollectorData.class).delete(collector);
                break;

            default:
                player.setLivreArtisant(false);
                player.send("EV");
                break;
        }


        player.setExchangeAction(null);
        DatabaseManager.get(PlayerData.class).update(player);
    }

    /** Fin Exchange Packet **/

    /**
     * Emote Packet *
     */
    private void parseEnvironementPacket(String packet) {
        switch (packet.charAt(1)) {
            case 'D'://Change direction
                setDirection(packet);
                break;
            case 'U'://Emote
                useEmote(packet);
                break;
        }
    }

    private void setDirection(String packet) {
        try {
            if (this.player.getFight() != null || this.player.isDead() == 1)
                return;
            int dir = Integer.parseInt(packet.substring(2));
            if (dir > 7 || dir < 0)
                return;
            this.player.set_orientation(dir);
            SocketManager.GAME_SEND_eD_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), dir);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void useEmote(String packet) {
        int emote = Integer.parseInt(packet.substring(2));
        if (emote == -1)
            return;
        if (this.player == null)
            return;
        if (this.player.getFight() != null)
            return;//Pas d'?mote en combat
        if (!this.player.getEmotes().contains(emote))
            return;
        if (emote != 1 && emote != 19 && emote != 20 && this.player.isSitted())
            this.player.setSitted(false);

        switch (emote) {
            case 20://Tabouret h?h? ( rime )
            case 19://s'allonger
            case 1:// s'asseoir
                final Party party = this.player.getParty();
                if(party != null && this.player.getFight() == null && party.getMaster() != null && this.player.getId() == party.getMaster().getId()) {
                    TimerWaiter.addNext(() -> party.getPlayers().stream().filter((follower1) -> party.isWithTheMaster(follower1, false, false)).forEach(follower -> {
                        follower.getGameClient().useEmote("eU1");
                    }), 0, TimeUnit.MILLISECONDS);
                }
                emote = 20;
                this.player.setSitted(!this.player.isSitted());
                break;
        }

        if (this.player.emoteActive() == 1 || this.player.emoteActive() == 19 || this.player.emoteActive() == 20)
            this.player.setEmoteActive(0);
        else
            this.player.setEmoteActive(emote);

        MountPark MP = this.player.getCurMap().getMountPark();
        SocketManager.GAME_SEND_eUK_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), this.player.emoteActive());
        if((emote == 2 || emote == 4 || emote == 3 || emote == 6 || emote == 8 || emote == 10) && MP != null)
        {
            final ArrayList<Mount> mounts = new ArrayList<>();
            for(Integer id : MP.getListOfRaising())  {
                Mount mount = World.world.getMountById(id);
                if(mount != null)
                    if(mount.getOwner() == this.player.getId())
                        mounts.add(mount);
            }
            final Player player = this.player;
            if(mounts.isEmpty()) return;
            final Mount mount = mounts.get(Formulas.getRandomValue(0, mounts.size() - 1));
            if(mounts.size() > 0) {
                int cells = 0;
                switch (emote) {
                    case 2:
                    case 4:
                        cells = 1;
                        break;

                    case 3:
                    case 8:
                        cells = Formulas.getRandomValue(2, 3);
                        break;

                    case 6:
                    case 10:
                        cells = Formulas.getRandomValue(4, 7);
                        break;
                }

                mount.moveMounts(player, cells, !(emote == 2 || emote == 3 || emote == 10));
            }
        }
    }

    /** Fin Emote Packet **/

    /**
     * Friend Packet *
     */
    private void parseFrienDDacket(String packet) {
        switch (packet.charAt(1)) {
            case 'A'://Ajthiser
                addFriend(packet);
                break;
            case 'D'://Effacer un ami
                removeFriend(packet);
                break;
            case 'L'://Liste
                SocketManager.GAME_SEND_FRIENDLIST_PACKET(this.player);
                break;
            case 'O':
                switch (packet.charAt(2)) {
                    case '-':
                        this.player.SetSeeFriendOnline(false);
                        SocketManager.GAME_SEND_BN(this.player);
                        break;
                    case '+':
                        this.player.SetSeeFriendOnline(true);
                        SocketManager.GAME_SEND_BN(this.player);
                        break;
                }
                break;
            case 'J': //Wife
                joinWife(packet);
                break;
        }
    }

    private void addFriend(String packet) {
        if (this.player == null)
            return;
        int guid = -1;
        switch (packet.charAt(2)) {
            case '%'://Nom de this.player
                packet = packet.substring(3);
                Player P = World.world.getPlayerByName(packet);
                if (P == null || !P.isOnline())//Si P est nul, ou si P est nonNul et P offline
                {
                    SocketManager.GAME_SEND_FA_PACKET(this.player, "Ef");
                    return;
                }
                guid = P.getAccID();
                break;
            case '*'://Pseudo
                packet = packet.substring(3);
                Account C = World.world.getAccountByPseudo(packet);
                if (C == null || !C.isOnline()) {
                    SocketManager.GAME_SEND_FA_PACKET(this.player, "Ef");
                    return;
                }
                guid = C.getId();
                break;
            default:
                packet = packet.substring(2);
                Player Pr = World.world.getPlayerByName(packet);
                if (Pr == null || !Pr.isOnline())//Si P est nul, ou si P est nonNul et P offline
                {
                    SocketManager.GAME_SEND_FA_PACKET(this.player, "Ef");
                    return;
                }
                guid = Pr.getAccount().getId();
                break;
        }
        if (guid == -1) {
            SocketManager.GAME_SEND_FA_PACKET(this.player, "Ef");
            return;
        }
        account.addFriend(guid);
    }

    private void removeFriend(String packet) {
        if (this.player == null)
            return;
        int guid = -1;
        switch (packet.charAt(2)) {
            case '%'://Nom de this.player
                packet = packet.substring(3);
                Player P = World.world.getPlayerByName(packet);
                if (P == null)//Si P est nul, ou si P est nonNul et P offline
                {
                    SocketManager.GAME_SEND_FD_PACKET(this.player, "Ef");
                    return;
                }
                guid = P.getAccID();
                break;
            case '*'://Pseudo
                packet = packet.substring(3);
                Account C = World.world.getAccountByPseudo(packet);
                if (C == null) {
                    SocketManager.GAME_SEND_FD_PACKET(this.player, "Ef");
                    return;
                }
                guid = C.getId();
                break;
            default:
                packet = packet.substring(2);
                Player Pr = World.world.getPlayerByName(packet);
                if (Pr == null || !Pr.isOnline())//Si P est nul, ou si P est nonNul et P offline
                {
                    SocketManager.GAME_SEND_FD_PACKET(this.player, "Ef");
                    return;
                }
                guid = Pr.getAccount().getId();
                break;
        }
        if (guid == -1 || !account.isFriendWith(guid)) {
            SocketManager.GAME_SEND_FD_PACKET(this.player, "Ef");
            return;
        }
        account.removeFriend(guid);
    }

    private void joinWife(String packet) {
        Player Wife = World.world.getPlayer(this.player.getWife());
        if (Wife == null)
            return;
        if (!Wife.isOnline()) {
            if (Wife.getSexe() == 0)
                SocketManager.GAME_SEND_Im_PACKET(this.player, "140");
            else
                SocketManager.GAME_SEND_Im_PACKET(this.player, "139");

            SocketManager.GAME_SEND_FRIENDLIST_PACKET(this.player);
            return;
        }
        switch (packet.charAt(2)) {
            case 'S'://Teleportation
                // TP Mariage : mettre une condition de donjon ...
                if (Wife.getCurMap().data.noTp || Wife.getCurMap().haveMobFix()) {
                    SocketManager.GAME_SEND_MESSAGE(this.player, this.player.getLang().trans("game.gameclient.joinwife.no"));
                    return;
                }
                if (this.player.getFight() != null)
                    return;
                else
                    this.player.meetWife(Wife);
                break;
            case 'C'://Suivre le deplacement
                if (packet.charAt(3) == '+')//Si lancement de la traque
                {
                    if (this.player.follow != null)
                        this.player.follow.follower.remove(this.player.getId());
                    SocketManager.GAME_SEND_FLAG_PACKET(this.player, Wife);
                    this.player.follow = Wife;
                    Wife.follower.put(this.player.getId(), this.player);
                } else
                //On arrete de suivre
                {
                    SocketManager.GAME_SEND_DELETE_FLAG_PACKET(this.player);
                    this.player.follow = null;
                    Wife.follower.remove(this.player.getId());
                }
                break;
        }
    }

    /** Fin Friend Packet **/

    /**
     * Fight Packet *
     */
    private void parseFightPacket(String packet) {
        try {
            switch (packet.charAt(1)) {
                case 'D'://D?tails d'un combat (liste des combats)
                    int key = -1;
                    try {
                        key = Integer.parseInt(packet.substring(2).replace(0x0
                                + "", ""));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (key == -1)
                        return;
                    SocketManager.GAME_SEND_FIGHT_DETAILS(this, this.player.getCurMap().getFight(key));
                    break;

                case 'H'://Aide
                    if (this.player.getFight() == null)
                        return;
                    this.player.getFight().toggleHelp(this.player.getId());
                    break;
                case 'L'://Lister les combats
                    SocketManager.GAME_SEND_FIGHT_LIST_PACKET(this, this.player.getCurMap());
                    break;
                case 'N'://Bloquer le combat
                    if (this.player.getFight() == null)
                        return;
                    this.player.getFight().toggleLockTeam(this.player.getId());
                    break;
                case 'P'://Seulement le groupe
                    if (this.player.getFight() == null || this.player.getParty() == null)
                        return;
                    this.player.getFight().toggleOnlyGroup(this.player.getId());
                    break;
                case 'S'://Bloquer les specs
                    if (this.player.getFight() != null)
                        this.player.getFight().toggleLockSpec(this.player);
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Fin Fight Packet **/

    /**
     * Game Packet *
     */
    private void parseGamePacket(String packet) {
        char c = packet.charAt(1);
        switch (c) {
            case 'A':
                if (this.player != null)
                    parseAction(packet);
                break;
            case 'C':
                if (this.player != null)
                    this.player.sendGameCreate();
                break;
            case 'd':
                showMonsterTarget(packet);
                break;
            case 'f':
                setFlag(packet);
                break;
            case 'F':
                if(this.player.getEnergy() > 0 && this.player.isDead() == 0)
                    return;
                this.player.setGhost();
                break;
            case 'I':
                getExtraInformations();
                break;
            case 'K':
                actionAck(packet);
                break;
            case 'P'://PvP Toogle
                this.player.toggleWings(packet.charAt(2));
                break;
            case 'p':
                setPlayerPosition(packet);
                break;
            case 'Q':
                leaveFight(packet);
                break;
            case 'R':
                readyFight(packet);
                break;
            case 't':
                if (this.player.getFight() != null)
                    this.player.getFight().playerPass(this.player);
                break;
            default:
                if(c == 1030) {
                    getExtraInformations();
                }
                break;
        }
    }

    private synchronized void parseAction(String packet) {
        if (this.player.getDoAction()) {
            SocketManager.GAME_SEND_GA_PACKET(this, "", "0", "", "");
            return;
        }
        int actionID;
        try {
            actionID = Integer.parseInt(packet.substring(2, 5));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return;
        }
        int nextGameActionID = 0;

        if (actions.size() > 0) {
            //On prend le plus haut GameActionID + 1
            nextGameActionID = (Integer) (actions.keySet().toArray()[actions.size() - 1]) + 1;
        }
        GameAction GA = new GameAction(nextGameActionID, actionID, packet);

        switch (actionID) {
            case 1://Deplacement
                final Party party = this.player.getParty();

                GameMap oldMap = this.player.getCurMap();
                GameCase oldCase = this.player.getCurCell();
                this.gameParseDeplacementPacket(GA);
                GameMap newMap = this.player.getCurMap();
                GameCase newCase = this.player.getCurCell();

                if(party != null && this.player.getFight() == null && party.getMaster() != null && party.getMaster().getName().equals(this.player.getName())) {
                    StringBuilder gm = new StringBuilder("GM");

                    TimerWaiter.addNext(() -> {
                        party.getPlayers().stream()
                            .filter((follower1) -> party.isWithTheMaster(follower1, false, oldMap != newMap))
                            .forEach(follower -> {
                                    GameClient client = follower.getGameClient();
                                    if (client != null && newMap.getId() == follower.getCurMap().getId()) {
                                        follower.getCurCell().removePlayer(follower);
                                        follower.setCurCell(newMap.getCase(newCase.getId()));
                                        follower.getCurCell().addPlayer(follower);
                                        gm.append("|-").append(follower.getId()).append("|+").append(follower.parseToGM());
                                    } else if (oldMap.getId() == follower.getCurMap().getId() && oldMap.getId() != newMap.getId() && oldCase.getId() == follower.getCurCell().getId()) {
                                        follower.teleport(newMap.getId(), newCase.getId());
                                    }
                            });
                        oldMap.send(gm.toString());
                    }, 0, TimeUnit.MILLISECONDS);
                }

                break;

            case 34://Get quest on sign.
                ExchangeAction<?> action = player.getExchangeAction();
                if(action == null || action.getType() != ExchangeAction.READING_DOCUMENT) {
                    break;
                }

                int qID = Integer.parseInt(packet.substring(5));
                DocumentActionData doc = (DocumentActionData) action.getValue();
                doc.onQuestHRef(player, qID);
            case 300://Sort
                gameTryCastSpell(packet);
                break;

            case 303://Attaque CaC
                gameTryCac(packet);
                break;

            case 500://Action Sur Map
                gameAction(GA);
                this.player.setGameAction(GA);
                break;

            case 507://Panneau int?rieur de la maison
                houseAction(packet);
                break;

            case 512:
                if (this.player.getAlignment() == Constant.ALIGNEMENT_NEUTRE)
                    return;
                this.player.openPrismeMenu();
                break;

            case 618://Mariage oui
                this.player.setisOK(Integer.parseInt(packet.substring(5, 6)));
                SocketManager.GAME_SEND_cMK_PACKET_TO_MAP(this.player.getCurMap(), "", this.player.getId(), this.player.getName(), "game.gameclient.maried.yes");
                Player boy = (Player) this.player.getCurMap().getCase(282).getPlayers().toArray()[0], girl = (Player) this.player.getCurMap().getCase(297).getPlayers().toArray()[0];

                if (girl.getisOK() > 0 && boy.getisOK() > 0)
                    World.world.wedding(girl, boy, 1);
                else
                    World.world.priestRequest(boy, girl, this.player == boy ? girl : boy);
                break;
            case 619://Mariage non
                this.player.setisOK(0);
                SocketManager.GAME_SEND_cMK_PACKET_TO_MAP(this.player.getCurMap(), "", this.player.getId(), this.player.getName(), "game.gameclient.maried.no");
                boy = (Player) this.player.getCurMap().getCase(282).getPlayers().toArray()[0];
                girl = (Player) this.player.getCurMap().getCase(297).getPlayers().toArray()[0];

                World.world.wedding(girl, boy, 0);
                break;

            case 900://Demande Defie
                if (Main.fightAsBlocked)
                    return;
                gameAskDuel(packet);
                break;

            case 901://Accepter Defie
                if (Main.fightAsBlocked)
                    return;
                gameAcceptDuel(packet);
                break;

            case 902://Refus/Anuler Defie
                if (Main.fightAsBlocked)
                    return;
                gameCancelDuel(packet);
                break;

            case 903://Rejoindre combat
                gameJoinFight(packet);
                break;

            case 906://Agresser
                if (Main.fightAsBlocked)
                    return;
                gameAggro(packet);
                break;

            case 909://Collector
                if (Main.fightAsBlocked)
                    return;
                /*long calcul = System.currentTimeMillis() - Config.startTime;
                if(calcul < 600000) {
                    this.player.sendMessage("Vous devez attendre encore " + ((600000 - calcul) / 60000) + " minute(s).");
                    return;
                }*/
                gameCollector(packet);
                break;

            case 912:// ataque Prisme
                if (Main.fightAsBlocked)
                    return;
                long calcul = System.currentTimeMillis() - Config.startTime;
                if(calcul < 600000) {
                    this.player.sendMessage(player.getLang().trans("game.gameclient.prisme.attack.wait", (600000 - calcul) / 60000));
                    return;
                }

                gamePrism(packet);
                break;
        }
    }

    private void gameParseDeplacementPacket(GameAction GA) {
        String path = GA.packet.substring(5);

        if (this.player.getFight() == null) {
            if (this.player.getBlockMovement()) {
                SocketManager.GAME_SEND_GA_PACKET(this, "", "0", "", "");
                removeAction(GA);
                return;
            }
            if (this.player.isDead() == 1) {
                SocketManager.GAME_SEND_BN(this.player);
                removeAction(GA);
                return;
            }
            if (this.player.getDoAction()) {
                SocketManager.GAME_SEND_GA_PACKET(this, "", "0", "", "");
                removeAction(GA);
                return;
            }
            if (this.player.getMount() != null && !this.player.isGhost()) {
                if (!this.player.getMorphMode() && (this.player.getPodUsed() > this.player.getMaxPod() || this.player.getMount().getActualPods() > this.player.getMount().getMaxPods())) {
                    SocketManager.GAME_SEND_Im_PACKET(this.player, "112");
                    SocketManager.GAME_SEND_GA_PACKET(this, "", "0", "", "");
                    removeAction(GA);
                    return;
                }
            }
            if (this.player.getPodUsed() > this.player.getMaxPod() && !this.player.isGhost()
                    && !this.player.getMorphMode()) {
                SocketManager.GAME_SEND_Im_PACKET(this.player, "112");
                SocketManager.GAME_SEND_GA_PACKET(this, "", "0", "", "");
                removeAction(GA);
                return;
            }
            //Si d?placement inutile
            GameCase targetCell = this.player.getCurMap().getCase(World.world.getCryptManager().cellCode_To_ID(path.substring(path.length() - 2)));

            if (targetCell == null || !targetCell.isWalkable(false)) {
                SocketManager.GAME_SEND_GA_PACKET(this, "", "0", "", "");
                removeAction(GA);
                return;
            }
            if(this.player.getCurMap().getId() == 6824 && this.player.start != null && targetCell.getId() == 325 && !this.player.start.leave) {
                this.player.start.leave = true;
                SocketManager.GAME_SEND_GA_PACKET(this, "", "0", "", "");
                removeAction(GA);
                return;
            }

            AtomicReference<String> pathRef = new AtomicReference<>(path);
            int result = PathFinding.isValidPath(this.player.getCurMap(), this.player.getCurCell().getId(), pathRef, null, this.player, targetCell.getId());

            if (result <= -9999) {
                result += 10000;
                GA.tp = true;
            }
            if (result == 0) {
                SocketManager.GAME_SEND_GA_PACKET(this, "", "0", "", "");
                removeAction(GA);

                // Maybe the player is right next to an Object and want to use it
                boolean allowSkill0 = Optional.ofNullable(this.player.getCurMap().data.interactiveObjects.get(targetCell.getId()))
                        .flatMap(World.world::getObjectBySprite)
                        .map(io -> io.allowSkill(0))
                        .orElse(false);

                if(allowSkill0) {
                    DataScriptVM.getInstance().handlers.onSkillUse(player, targetCell.getId(), 0);
                }
                return;
            }
            if (result != -1000 && result < 0)
                result = -result;

            //On prend en compte le nouveau path
            path = pathRef.get();
            //Si le path est invalide
            if (result == -1000)
                path = World.world.getCryptManager().getHashedValueByInt(this.player.get_orientation()) + World.world.getCryptManager().cellID_To_Code(this.player.getCurCell().getId());

            //On sauvegarde le path dans la variable
            GA.args = path;
            if (this.player.walkFast) {
                this.player.getCurCell().removePlayer(this.player);
                SocketManager.GAME_SEND_BN(this);
                //On prend la case ciblee
                GameCase nextCell = this.player.getCurMap().getCase(World.world.getCryptManager().cellCode_To_ID(path.substring(path.length() - 2)));
                targetCell = this.player.getCurMap().getCase(World.world.getCryptManager().cellCode_To_ID(GA.packet.substring(GA.packet.length() - 2)));

                //On definie la case et on ajoute le personnage sur la case
                this.player.setCurCell(nextCell);
                this.player.set_orientation(World.world.getCryptManager().getIntByHashedValue(path.charAt(path.length() - 3)));
                this.player.getCurCell().addPlayer(this.player);
                if (!this.player.isGhost())
                    this.player.setAway(false);

                SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(this.player.getCurMap(), this.player.getId());
                SocketManager.GAME_SEND_ADD_PLAYER_TO_MAP(this.player.getCurMap(), this.player);
                this.player.getCurMap().onPlayerArriveOnCell(this.player, this.player.getCurCell().getId());

                SocketManager.GAME_SEND_GA_PACKET(this, "", "0", "", "");
                this.player.refreshCraftSecure(true);

                removeAction(GA);
                return;
            } else {
                SocketManager.GAME_SEND_GA_PACKET_TO_MAP(this.player.getCurMap(), "" + GA.id, 1, this.player.getId(), "a" + World.world.getCryptManager().cellID_To_Code(this.player.getCurCell().getId()) + path);
            }

            this.addAction(GA);
            this.player.setSitted(false);
            this.player.setAway(true);
        } else {
            final Fighter fighter = this.player.getFight().getFighterByPerso(this.player);
            if (fighter != null) {
                GA.args = path;
                this.player.getFight().cast(this.player.getFight().getFighterByPerso(this.player), () -> this.player.getFight().onFighterMovement(fighter, GA), null);
            }
        }
    }

    private void gameTryCastSpell(String packet) {
        try {
            String[] split = packet.split(";");

            if(packet.contains("undefined") || split.length != 2)
                return;

            final int id = Integer.parseInt(split[0].substring(5)), cellId = Integer.parseInt(split[1]);
            final Fight fight = this.player.getFight();

            if (fight != null) {
                Spell.SortStats SS = this.player.getSortStatBySortIfHas(id);

                if (SS != null)
                    if(this.player.getFight().getCurAction().isEmpty())
                        this.player.getFight().cast(this.player.getFight().getFighterByPerso(this.player), () -> this.player.getFight().tryCastSpell(this.player.getFight().getFighterByPerso(this.player), SS, cellId), SS);
            }
        } catch (NumberFormatException e) {
            System.err.println(packet + "\n" + e);
        }
    }

    private void gameTryCac(String packet) {
        try {
            if(packet.contains("undefined")) return;
            final int cell = Integer.parseInt(packet.substring(5));
            if (this.player.getFight() != null && this.player.getFight().getCurAction().isEmpty())
                this.player.getFight().cast(this.player.getFight().getFighterByPerso(this.player), () -> this.player.getFight().tryCaC(this.player, cell), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void gameAction(GameAction GA) {
        String packet = GA.packet.substring(5);
        int cellID = -1;
        int actionID = -1;

        try {
            cellID = Integer.parseInt(packet.split(";")[0]);
            actionID = Integer.parseInt(packet.split(";")[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (walk) {
            actions.put(-1, GA);
            return;
        }

        //Si packet invalide, ou cellule introuvable
        if (cellID == -1 || actionID == -1 || this.player == null || this.player.getCurMap() == null || this.player.getCurMap().getCase(cellID) == null)
            return;

        GA.args = cellID + ";" + actionID;
        this.player.getGameClient().addAction(GA);
        if (this.player.isDead() == 0)
            this.player.startActionOnCell(GA);
    }

    private void houseAction(String packet) {
        int actionID = Integer.parseInt(packet.substring(5));
        House h = this.player.getInHouse();
        if (h == null)
            return;
        switch (actionID) {
            case 81://V?rouiller maison
                h.lock(this.player);
                break;
            case 97://Acheter maison
                h.buyIt(this.player);
                break;
            case 98://Vendre
            case 108://Modifier prix de vente
                h.sellIt(this.player);
                break;
        }
    }

    private void gameAskDuel(String packet) {

        try {
            if (this.player.cantDefie())
                return;
            int guid = Integer.parseInt(packet.substring(5));
            Player target = World.world.getPlayer(guid);
            if (target == null)
                return;
            if(this.player.getStalk() != null && player.getStalk().onPlayerTryToFight(player, target))
                return;
            if (player.getCurMap().getSubArea() != null && player.getCurMap().getSubArea().getArea().getSuperArea() == 3) {
                if (((player.getAlignment() != 0 && (!player.getCurMap().data.noAgro)) || target.getDeshonor() > 0)) {
                    if (!target.isOnline() || target.getFight() != null || target.getCurMap().getId()
                            != this.player.getCurMap().getId() || target.getAlignment() == this.player.getAlignment() || this.player.
                            getCurMap().getPlaces().size() < 2 || !target.canAggro() || target.isDead() == 1
                            || this.player.getFight() != null || player.isDead() == 1 || player.isGhost() || target.isGhost())
                        return;

                    this.clearAllPanels(this.player);
                    this.clearAllPanels(target);
                    SocketManager.GAME_SEND_GA_PACKET_TO_MAP(this.player.getCurMap(), "", 906, this.player.getId(), target.getId() + "");
                    this.player.getCurMap().newFight(this.player, target, Constant.FIGHT_TYPE_AGRESSION);
                    return;
                }
            }
            if (this.player.getCurMap().getPlaces().size() < 2) {
                SocketManager.GAME_SEND_DUEL_Y_AWAY(this, this.player.getId());
                return;
            }
            if (this.player.isAway() || this.player.getFight() != null || this.player.isDead() == 1) {
                SocketManager.GAME_SEND_DUEL_Y_AWAY(this, this.player.getId());
                return;
            }
            if (this.player.isMissingSubscription()) {
                SocketManager.GAME_SEND_EXCHANGE_REQUEST_ERROR(this.player.getGameClient(), 'S');
                return;
            }
            if (target.isMissingSubscription()) {
                SocketManager.GAME_SEND_EXCHANGE_REQUEST_ERROR(this.player.getGameClient(), 'S');
                return;
            }
            if (target.isAway() || target.getFight() != null || target.getCurMap().getId() != this.player.getCurMap().getId() || target.isDead() == 1 || target.getExchangeAction() != null || this.player.getExchangeAction() != null) {
                SocketManager.GAME_SEND_DUEL_E_AWAY(this, this.player.getId());
                return;
            }
            this.player.setDuelId(guid);
            this.player.setAway(true);
            World.world.getPlayer(guid).setDuelId(this.player.getId());
            World.world.getPlayer(guid).setAway(true);
            SocketManager.GAME_SEND_MAP_NEW_DUEL_TO_MAP(this.player.getCurMap(), this.player.getId(), guid);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void gameAcceptDuel(String packet) {
        if (this.player.cantDefie())
            return;
        int guid = -1;
        try {
            guid = Integer.parseInt(packet.substring(5));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return;
        }
        if (this.player.getDuelId() != guid || this.player.getDuelId() == -1 || this.player.isDead() == 1)
            return;
        int duel = this.player.getDuelId();
        Player player = World.world.getPlayer(duel);

        SocketManager.GAME_SEND_MAP_START_DUEL_TO_MAP(this.player.getCurMap(), duel, this.player.getId());
        this.clearPanelsForPlayer(player);
        Fight fight = this.player.getCurMap().newFight(World.world.getPlayer(duel), this.player, Constant.FIGHT_TYPE_CHALLENGE);

        this.player.setFight(fight);
        this.player.setAway(false);
        player.setFight(fight);
        player.setAway(false);
    }

    private void gameCancelDuel(String packet) {
        try {
            if (this.player.getDuelId() == -1)
                return;
            SocketManager.GAME_SEND_CANCEL_DUEL_TO_MAP(this.player.getCurMap(), this.player.getDuelId(), this.player.getId());
            Player player = World.world.getPlayer(this.player.getDuelId());
            player.setAway(false);
            player.setDuelId(-1);
            this.player.setAway(false);
            this.player.setDuelId(-1);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void gameJoinFight(String packet) {
        if (this.player.getFight() != null)
            return;
        if (this.player.isDead() == 1)
            return;

        String[] infos = packet.substring(5).split(";");
        if (infos.length == 1) {
            try {
                Fight F = this.player.getCurMap().getFight(Integer.parseInt(infos[0]));
                if (F != null)
                    F.joinAsSpectator(this.player);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                int guid = Integer.parseInt(infos[1]);
                if (this.player.isAway()) {
                    SocketManager.GAME_SEND_GA903_ERROR_PACKET(this, 'o', guid);
                    return;
                }
                Player player = World.world.getPlayer(guid);
                Fight fight = null;

                if (player == null) {
                    Prism prism = World.world.getPrisme(guid);
                    if(prism != null)
                        fight = prism.getFight();
                } else {
                    fight = player.getFight();
                }
                if (fight == null)
                    return;
                if (fight.getState() > 2) {
                    SocketManager.GAME_SEND_Im_PACKET(this.player, "191");
                    return;
                }
                if (this.player.isMissingSubscription()) {
                    SocketManager.GAME_SEND_EXCHANGE_REQUEST_ERROR(this.player.getGameClient(), 'S');
                    return;
                }

                this.clearAllPanels(null);
                if(fight.getPrism() != null)
                    fight.joinPrismFight(this.player, (fight.getTeam0().containsKey(guid) ? 0 : 1));
                else {
                    fight.joinFight(this.player, guid);

                    final Party party = this.player == null ? null : this.player.getParty();
                    final Fight f = fight;
                    if(party != null && party.getMaster() != null && party.getMaster().getName().equals(this.player.getName())) {
                        party.getPlayers().stream().filter((follower) -> party.isWithTheMaster(follower, false, false)).forEach(follower -> {
                            TimerWaiter.addNext(() -> {
                                f.joinFight(follower, this.player.getId());
                            }, follower.getParty().getOptionByPlayer(follower).getSecond(), TimeUnit.SECONDS);
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void gameAggro(String packet) {
        try {
            if (this.player == null || this.player.getFight() != null || this.player.isGhost() || this.player.isDead() == 1 || this.player.cantAgro())
                return;
            if (this.player.isMissingSubscription()) {
                SocketManager.GAME_SEND_EXCHANGE_REQUEST_ERROR(this.player.getGameClient(), 'S');
                return;
            }
            Player target = World.world.getPlayer(Integer.parseInt(packet.substring(5)));

            if (target == null || !target.isOnline() || target.getFight() != null || target.getCurMap().getId()
                    != this.player.getCurMap().getId() || target.getAlignment() == this.player.getAlignment() || this.player.
                    getCurMap().getPlaces().size() < 2 || !target.canAggro() || target.isDead() == 1)
                return;

            Area area = this.player.getCurMap().getArea();

            if(area != null && area.getId() == 42)
                return;
            if (target.isMissingSubscription()) {
                SocketManager.GAME_SEND_EXCHANGE_REQUEST_ERROR(target.getGameClient(), 'S');
                return;
            }
            if (target.getAlignment() == 0) {
                this.player.setDeshonor(this.player.getDeshonor() + 1);
                SocketManager.GAME_SEND_Im_PACKET(this.player, "084;1");
            }

            this.clearAllPanels(target);
            if(!this.player.is_showWings())
                this.player.toggleWings('+');
            SocketManager.GAME_SEND_GA_PACKET_TO_MAP(this.player.getCurMap(), "", 906, this.player.getId(), target.getId() + "");
            this.player.getCurMap().newFight(this.player, target, Constant.FIGHT_TYPE_AGRESSION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void gameCollector(String packet) {
        try {
            if (this.player == null)
                return;
            if (this.player.getFight() != null)
                return;
            if (this.player.getExchangeAction() != null || this.player.isDead() == 1 || this.player.isAway())
                return;

            int id = Integer.parseInt(packet.substring(5));
            Collector target = World.world.getCollector(id);

            if (target == null || target.getInFight() > 0)
                return;
            if (this.player.getCurMap().getId() != target.getMap())
                return;
            if (target.getExchange()) {
                SocketManager.GAME_SEND_Im_PACKET(this.player, "1180");
                return;
            }

            this.clearAllPanels(null);
            SocketManager.GAME_SEND_GA_PACKET_TO_MAP(this.player.getCurMap(), "", 909, this.player.getId(), id + "");
            this.player.getCurMap().startFightVersusPercepteur(this.player, target);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void gamePrism(String packet) {
        try {
            if (this.player.isGhost() || this.player.getFight() != null || this.player.getExchangeAction() != null || this.player.getAlignment() == 0 || this.player.isDead() == 1)
                return;
            int id = Integer.parseInt(packet.substring(5));
            Prism prism = World.world.getPrisme(id);
            if (prism.getFight() != null || prism.getMap() != this.player.getCurMap().getId())
                return;
            if(prism.getState() == Prism.NEW) {
                player.send("Im1154");
            }
            if(!prism.getSubArea().ownNearestSubArea(player)) {
                player.send("Im1157");
                return;
            }
            this.clearAllPanels(null);
            SocketManager.SEND_GA_ACTION_TO_Map(this.player.getCurMap(), "", 909, this.player.getId() + "", id + "");
            this.player.getCurMap().startFightVersusPrisme(this.player, prism);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO: Change the duel asking method by ExchangeAction (better way)
    public void clearAllPanels(Player target) {
        this.clearPanelsForPlayer(this.player);
        this.clearPanelsForPlayer(target);
    }

    private void clearPanelsForPlayer(Player player) {
        if(player != null) {
            if(player.getExchangeAction() != null) {
                player.setExchangeAction(null);
                player.send("EV");
            }
            if(player.getDuelId() != -1) {
                Player target = World.world.getPlayer(player.getDuelId());
                if(target != null) {
                    target.setAway(false);
                    target.setDuelId(-1);
                    SocketManager.GAME_SEND_CANCEL_DUEL_TO_MAP(target.getCurMap(), target.getDuelId(), target.getId());
                }
                player.setAway(false);
                player.setDuelId(-1);
                SocketManager.GAME_SEND_CANCEL_DUEL_TO_MAP(player.getCurMap(), player.getDuelId(), player.getId());
            }
        }
    }

    private void showMonsterTarget(String packet) {
        int chalID = 0;
        chalID = Integer.parseInt(packet.split("i")[1]);
        if (chalID != 0 && this.player.getFight() != null) {
            Fight fight = this.player.getFight();
            if (fight.getAllChallenges().containsKey(chalID))
                fight.getAllChallenges().get(chalID).showCibleToPerso(this.player);
        }
    }

    private void setFlag(String packet) {
        if (this.player == null)
            return;
        if (this.player.getFight() == null)
            return;
        int cellID = -1;
        try {
            cellID = Integer.parseInt(packet.substring(2));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cellID == -1)
            return;
        this.player.getFight().showCaseToTeam(this.player.getId(), cellID);
    }

    private void getExtraInformations() {
        try {
            if (this.player != null && this.player.getLastFight() != null) {
                if (player.applyEndFightAction()) {
                    return;
                }
                player.getCurMap().applyEndFightAction(player);
                player.setLastFightForEndFightAction(null);
            }
            sendExtraInformations();

        } catch (Exception e) {
            Main.logger.error("getExtraInformations", e);
        }
    }

    private void sendExtraInformations() {
        try {
            if(this.player == null) return;
            if (this.player.getFight() != null && !this.player.getFight().isFinish()) {
                //Only Collector
                SocketManager.GAME_SEND_MAP_GMS_PACKETS(this.player.getFight().getMap(), this.player);
                SocketManager.GAME_SEND_GDK_PACKET(this);
                if (this.player.getFight().onPlayerReconnection(this.player))
                    return;
            }

            //Objets sur la Map
            SocketManager.GAME_SEND_MAP_GMS_PACKETS(this.player.getCurMap(), this.player);
            SocketManager.GAME_SEND_GDK_PACKET(this);
            SocketManager.GAME_SEND_MAP_NPCS_GMS_PACKETS(player.getGameClient(), player.getCurMap());
            SocketManager.GAME_SEND_MAP_MOBS_GMS_PACKETS(player.getGameClient(), player.getCurMap());
            SocketManager.GAME_SEND_MAP_OBJECTS_GDS_PACKETS(player.getGameClient(), player.getCurMap());

            // Prism
            SocketManager.SEND_GM_PRISME_TO_MAP(this, this.player.getCurMap());
            // Merchant
            SocketManager.GAME_SEND_MERCHANT_LIST(this.player);
            // Houses
            World.world.getHouseManager().load(this.player, this.player.getCurMap().getId());
            // Collector
            SocketManager.GAME_SEND_MAP_PERCO_GMS_PACKETS(this, this.player.getCurMap());
            // Mount park
            SocketManager.GAME_SEND_Rp_PACKET(this.player, this.player.getCurMap().getMountPark());
            // Mount park objects
            SocketManager.GAME_SEND_GDO_OBJECT_TO_MAP(this, this.player.getCurMap());
            SocketManager.GAME_SEND_GM_MOUNT(this, this.player.getCurMap(), true);

            //Les drapeau de combats
            SocketManager.GAME_SEND_MAP_FIGHT_COUNT(this, this.player.getCurMap());
            Fight.FightStateAddFlag(this.player.getCurMap(), this.player);

            //items au sol
            this.player.getCurMap().sendFloorItems(this.player);
            // Cell overrides
            this.player.getCurMap().sendOverrides(this.player);
            // Anim states
            this.player.getCurMap().sendAnimStates(this.player);

            AuctionManager.getInstance().onPlayerLoadMap(player);
            World.world.showPrismes(this.player);
            this.player.refreshCraftSecure(false);
            this.player.afterFight = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void actionAck(String packet) {
        int id = -1;
        String[] infos = packet.substring(3).split("\\|");
        try {
            id = Integer.parseInt(infos[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (id == -1)
            return;
        GameAction GA = actions.get(id);

        if (GA == null)
            return;
        boolean isOk = packet.charAt(2) == 'K';
        switch (GA.actionId) {
            case 1://Deplacement
                if (isOk) {
                    if (this.player.getFight() == null) {
                        assert this.player.getCurCell() != null;
                        final Party party = this.player.getParty();

                        if(party != null && this.player.getFight() == null && party.getMaster() != null && party.getMaster().getName().equals(this.player.getName())) {
                            TimerWaiter.addNext(() -> party.getPlayers().stream()
                                    .filter((follower1) -> party.isWithTheMaster(follower1, false, false))
                                    .forEach(follower -> follower.getGameClient().actionAck(packet)), 0, TimeUnit.MILLISECONDS);
                        }

                        this.player.getCurCell().removePlayer(this.player);
                        SocketManager.GAME_SEND_BN(this);
                        String path = GA.args;
                        //On prend la case cibl?e

                        GameCase nextCell = this.player.getCurMap().getCase(World.world.getCryptManager().cellCode_To_ID(path.substring(path.length() - 2)));
                        int targetCellID = World.world.getCryptManager().cellCode_To_ID(GA.packet.substring(GA.packet.length() - 2));

                        //On d?finie la case et on ajoute le personnage sur la case
                        this.player.setCurCell(nextCell);
                        this.player.set_orientation(CryptManager.getIntByHashedValue(path.charAt(path.length() - 3)));
                        this.player.getCurCell().addPlayer(this.player);
                        if (!this.player.isGhost())
                            this.player.setAway(false);
                        this.player.getCurMap().onPlayerArriveOnCell(this.player, this.player.getCurCell().getId());


                        if (GA.tp) {
                            GA.tp = false;
                            this.player.teleport((short) 9864, 265);
                            return;
                        }

                        // TODO: Maybe check that the player is still where we think he is
                        // Maybe the player is right next to an Object and want to use it
                        boolean allowSkill0 = Optional.ofNullable(player.getCurMap().data.interactiveObjects.get(targetCellID))
                                .flatMap(World.world::getObjectBySprite)
                                .map(io -> io.allowSkill(0))
                                .orElse(false);

                        if(allowSkill0) {
                            DataScriptVM.getInstance().handlers.onSkillUse(player, targetCellID, 0);
                        }
                    } else {
                        this.player.getFight().onGK(this.player);
                        return;
                    }
                } else {
                    //Si le joueur s'arrete sur une case
                    int newCellID = -1;
                    try {
                        newCellID = Integer.parseInt(infos[1]);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    if (newCellID == -1)
                        return;

                    String path = GA.args;
                    this.player.getCurCell().removePlayer(this.player);
                    this.player.setCurCell(this.player.getCurMap().getCase(newCellID));
                    this.player.set_orientation(World.world.getCryptManager().getIntByHashedValue(path.charAt(path.length() - 3)));
                    this.player.getCurCell().addPlayer(this.player);
                    SocketManager.GAME_SEND_BN(this);
                    if (GA.tp) {
                        GA.tp = false;
                        this.player.teleport((short) 9864, 265);
                        return;
                    }
                }
                break;

            case 500://Action Sur Map
                this.player.finishActionOnCell(GA);
                this.player.setGameAction(null);
                break;

        }
        removeAction(GA);
    }

    private void setPlayerPosition(String packet) {
        if (this.player.getFight() == null)
            return;
        try {
            int cell = Integer.parseInt(packet.substring(2));
            this.player.getFight().exchangePlace(this.player, cell);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void leaveFight(String packet) {
        int id = 0;

        if (!packet.substring(2).isEmpty()) {
            try {
                id = Integer.parseInt(packet.substring(2));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Fight fight = this.player.getFight();

        if (fight == null || id < 0)
            return;

        if (id > 0) {
            final Player target = World.world.getPlayer(id);

            if (target == null || target.getFight() == null)
                return;
            if(fight.getTeamId(target.getId()) != fight.getTeamId(this.player.getId()))
                return;

            if ((fight.getInit0() != null && target == fight.getInit0().getPlayer()) || (fight.getInit1() != null && target == fight.getInit1().getPlayer()) || target == this.player)
                return;

            fight.leftFight(this.player, target);
        } else {
            fight.leftFight(this.player, null);
        }
    }

    private void readyFight(final String packet) {
        if (this.player.getFight() == null)
            return;
        if (this.player.getFight().getState() != Constant.FIGHT_STATE_PLACE)
            return;

        this.player.setReady(packet.substring(2).equalsIgnoreCase("1"));
        this.player.getFight().verifIfAllReady();
        SocketManager.GAME_SEND_FIGHT_PLAYER_READY_TO_FIGHT(this.player.getFight(), 3, this.player.getId(), packet.substring(2).equalsIgnoreCase("1"));

        final Party party = this.player.getParty();

        if(party != null && party.getMaster() != null && party.getMaster().getName().equals(this.player.getName())) {
            TimerWaiter.addNext(() -> party.getPlayers().stream()
                    .filter(follower -> party.isWithTheMaster(follower, true, false))
                    .forEach(follower -> follower.getGameClient().readyFight(packet)), 1, TimeUnit.SECONDS);
        }
    }

    /** Fin Game Packet **/

    /**
     * Guild Packet *
     */
    private void parseGuildPacket(String packet) {
        switch (packet.charAt(1)) {
            case 'B'://Stats
                boostCaracteristique(packet);
                break;
            case 'b'://Sorts
                boostSpellGuild(packet);
                break;
            case 'C'://Creation
                createGuild(packet);
                break;
            case 'f'://T?l?portation enclo de guilde
                teleportToGuildFarm(packet.substring(2));
                break;
            case 'F'://Retirer Collector
                removeTaxCollector(packet.substring(2));
                break;
            case 'h'://T?l?portation maison de guilde
                teleportToGuildHouse(packet.substring(2));
                break;
            case 'H'://Poser un Collector
                placeTaxCollector();
                break;
            case 'I'://Infos
                getInfos(packet.charAt(2));
                break;
            case 'J'://Join
                invitationGuild(packet.substring(2));
                break;
            case 'K'://Kick
                banToGuild(packet.substring(2));
                break;
            case 'P'://Promote
                changeMemberProfil(packet.substring(2));
                break;
            case 'T'://attaque sur Collector
                joinOrLeaveTaxCollector(packet.substring(2));
                break;
            case 'V'://Ferme le panneau de cr?ation de guilde
                leavePanelGuildCreate();
                break;
        }
    }

    private void boostCaracteristique(String packet) {
        if (this.player.getGuild() == null)
            return;
        Guild G = this.player.getGuild();
        if (!this.player.getGuildMember().canDo(Constant.G_BOOST))
            return;
        switch (packet.charAt(2)) {
            case 'p'://Prospec
                if (G.getCapital() < 1)
                    return;
                if (G.getStats(176) >= 500)
                    return;
                G.setCapital(G.getCapital() - 1);
                G.upgradeStats(176, 1);
                break;
            case 'x'://Sagesse
                if (G.getCapital() < 1)
                    return;
                if (G.getStats(124) >= 400)
                    return;
                G.setCapital(G.getCapital() - 1);
                G.upgradeStats(124, 1);
                break;
            case 'o'://Pod
                if (G.getCapital() < 1)
                    return;
                if (G.getStats(158) >= 5000)
                    return;
                G.setCapital(G.getCapital() - 1);
                G.upgradeStats(158, 20);
                break;
            case 'k'://Nb Collector
                if (G.getCapital() < 10)
                    return;
                if (G.getNbCollectors() >= 50)
                    return;
                G.setCapital(G.getCapital() - 10);
                G.setNbCollectors(G.getNbCollectors() + 1);
                break;
        }
        DatabaseManager.get(GuildData.class).update(G);
        SocketManager.GAME_SEND_gIB_PACKET(this.player, this.player.getGuild().parseCollectorToGuild());
    }

    private void boostSpellGuild(String packet) {
        if (this.player.getGuild() == null)
            return;
        Guild G2 = this.player.getGuild();
        if (!this.player.getGuildMember().canDo(Constant.G_BOOST))
            return;
        int spellID = Integer.parseInt(packet.substring(2));
        if (G2.getSpells().containsKey(spellID)) {
            if (G2.getCapital() < 5)
                return;
            G2.setCapital(G2.getCapital() - 5);
            G2.boostSpell(spellID);
            DatabaseManager.get(GuildData.class).update(G2);
            SocketManager.GAME_SEND_gIB_PACKET(this.player, this.player.getGuild().parseCollectorToGuild());
        } else {
            GameServer.a();
        }
    }

    private void createGuild(String packet) {
        if (this.player == null)
            return;
        if (this.player.getGuild() != null || this.player.getGuildMember() != null) {
            SocketManager.GAME_SEND_gC_PACKET(this.player, "Ea");
            return;
        }
        if (this.player.getFight() != null || this.player.isAway())
            return;
        try {
            String[] infos = packet.substring(2).split("\\|");
            //base 10 => 36
            String bgID = Integer.toString(Integer.parseInt(infos[0]), 36);
            String bgCol = Integer.toString(Integer.parseInt(infos[1]), 36);
            String embID = Integer.toString(Integer.parseInt(infos[2]), 36);
            String embCol = Integer.toString(Integer.parseInt(infos[3]), 36);
            String name = infos[4];
            if (World.world.guildNameIsUsed(name)) {
                SocketManager.GAME_SEND_gC_PACKET(this.player, "Ean");
                return;
            }

            //Validation du nom de la guilde
            String tempName = name.toLowerCase();
            boolean isValid = true;
            //V?rifie d'abord si il contient des termes d?finit
            if (tempName.length() > 20 || tempName.contains("mj")
                    || tempName.contains("modo") || tempName.contains("fuck")
                    || tempName.contains("admin")) {
                isValid = false;
            }
            //Si le nom passe le test, on v?rifie que les caract?re entr? sont correct.
            if (isValid) {
                int tiretCount = 0;
                for (char curLetter : tempName.toCharArray()) {
                    if (!((curLetter >= 'a' && curLetter <= 'z') || curLetter >= 'A' && curLetter <= 'Z' || curLetter == '-' || curLetter == ' ')) {
                        if(curLetter == '\'') continue;
                        isValid = false;
                        break;
                    }
                    if (curLetter == '-') {
                        if (tiretCount >= 3) {
                            isValid = false;
                            break;
                        } else {
                            tiretCount++;
                        }
                    }
                    if (curLetter == ' ') {
                        if (tiretCount >= 3) {
                            isValid = false;
                            break;
                        } else {
                            tiretCount++;
                        }
                    }
                }
            }
            //Si le nom est invalide
            if (!isValid) {
                SocketManager.GAME_SEND_gC_PACKET(this.player, "Ean");
                return;
            }
            //FIN de la validation
            String emblem = bgID + "," + bgCol + "," + embID + "," + embCol;//9,6o5nc,2c,0;
            if (World.world.guildEmblemIsUsed(emblem)) {
                SocketManager.GAME_SEND_gC_PACKET(this.player, "Eae");
                return;
            }
            if (this.player.getCurMap().getId() == 2196)//Temple de cr?ation de guilde
            {
                if (!this.player.hasItemTemplate(1575, 1, false))//Guildalogemme
                {
                    SocketManager.GAME_SEND_Im_PACKET(this.player, "14");
                    return;
                }
                this.player.removeItemByTemplateId(1575, 1, false);
            }
            Guild G = new Guild(name, emblem);
            GuildMember gm = G.addNewMember(this.player);
            gm.setAllRights(1, (byte) 0, 1, this.player);//1 => Meneur (Tous droits)
            this.player.setGuildMember(gm);//On ajthise le meneur
            World.world.addGuild(G);
            DatabaseManager.get(GuildMemberData.class).update(this.player);
            //Packets
            SocketManager.GAME_SEND_gS_PACKET(this.player, gm);
            SocketManager.GAME_SEND_gC_PACKET(this.player, "K");
            SocketManager.GAME_SEND_gV_PACKET(this.player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void teleportToGuildFarm(String packet) {
        if (this.player.getGuild() == null) {
            SocketManager.GAME_SEND_Im_PACKET(this.player, "1135");
            return;
        }
        if (this.player.getFight() != null || this.player.isAway())
            return;
        int MapID = Integer.parseInt(packet);
        MountPark MP = World.world.getMap(MapID).getMountPark();
        if (MP.getGuild().getId() != this.player.getGuild().getId()) {
            SocketManager.GAME_SEND_Im_PACKET(this.player, "1135");
            return;
        }
        int CellID = World.world.getEncloCellIdByMapId(MapID);
        if (this.player.hasItemTemplate(9035, 1, false)) {
            SocketManager.GAME_SEND_Im_PACKET(this.player, "022;1~9035");
            this.player.removeItemByTemplateId(9035, 1, false);
            this.player.teleport(MapID, CellID);
        } else {
            SocketManager.GAME_SEND_Im_PACKET(this.player, "1159");
        }
    }

    private void removeTaxCollector(String packet) {
        if (this.player.getGuild() == null || this.player.getFight() != null
                || this.player.isAway())
            return;
        if (!this.player.getGuildMember().canDo(Constant.G_POSPERCO))
            return;//On peut le retirer si on a le droit de le poser
        int idCollector = Integer.parseInt(packet);
        Collector collector = World.world.getCollector(idCollector);
        if (collector == null || collector.getInFight() > 0)
            return;
        collector.reloadTimer();
        SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(this.player.getCurMap(), idCollector);
        DatabaseManager.get(CollectorData.class).delete(collector);
        collector.delCollector(collector.getId());
        for (Player z : this.player.getGuild().getPlayers()) {
            if (z.isOnline()) {
                SocketManager.GAME_SEND_gITM_PACKET(z, org.starloco.locos.entity.Collector.parseToGuild(z.getGuild().getId()));
                String str = "";
                str += "R" + collector.getFullName() + "|";
                str += collector.getMap() + "|";
                str += World.world.getMap(collector.getMap()).getX() + "|"
                        + World.world.getMap(collector.getMap()).getY() + "|"
                        + this.player.getName();
                SocketManager.GAME_SEND_gT_PACKET(z, str);
            }
        }
    }

    private void teleportToGuildHouse(String packet) {
        if (this.player.getGuild() == null) {
            SocketManager.GAME_SEND_Im_PACKET(this.player, "1135");
            return;
        }

        if (this.player.getFight() != null || this.player.isAway())
            return;
        int HouseID = Integer.parseInt(packet);
        House h = World.world.getHouses().get(HouseID);
        if (h == null)
            return;
        if (this.player.getGuild().getId() != h.getGuildId()) {
            SocketManager.GAME_SEND_Im_PACKET(this.player, "1135");
            return;
        }
        if (!h.canDo(Constant.H_GTELE)) {
            SocketManager.GAME_SEND_Im_PACKET(this.player, "1136");
            return;
        }
        if (this.player.hasItemTemplate(8883, 1, false)) {
            SocketManager.GAME_SEND_Im_PACKET(this.player, "022;1~8883");
            this.player.removeItemByTemplateId(8883, 1, false);
            this.player.teleport((short) h.getHouseMapId(), h.getHouseCellId());
        } else {
            SocketManager.GAME_SEND_Im_PACKET(this.player, "1137");
        }
    }

    private void placeTaxCollector() {
        final Guild guild = this.player.getGuild();
        final GameMap map = this.player.getCurMap();

        if (guild == null || this.player.getFight() != null || this.player.isAway() || !this.player.getGuildMember().canDo(Constant.G_POSPERCO) || !guild.haveTenMembers())
            return;
        if (this.player.isMissingSubscription()) {
            SocketManager.GAME_SEND_EXCHANGE_REQUEST_ERROR(this.player.getGameClient(), 'S');
            return;
        }

        short price = (short) (1000 + 10 * guild.getLvl());//Calcul du prix du Collector

        if (this.player.getKamas() < price) {//Kamas insuffisants
            SocketManager.GAME_SEND_Im_PACKET(this.player, "182");
            return;
        }
        if (Collector.getCollectorByGuildId(map.getId()) > 0) {//La Map poss?de un Collector
            SocketManager.GAME_SEND_Im_PACKET(this.player, "1168;1");
            return;
        }
        if (map.getPlaces().size() < 2 || SoulStone.isInArenaMap(map.getId()) || map.data.noCollectors) {//La map ne poss?de pas de "places"
            SocketManager.GAME_SEND_Im_PACKET(this.player, "113");
            return;
        }

        if (Collector.countCollectorGuild(guild.getId()) >= guild.getNbCollectors())
            return;

        if (World.world.getDelayCollectors().get(map.getId()) != null) {
            long time = World.world.getDelayCollectors().get(map.getId());

            if ((System.currentTimeMillis() - time) < (((10L * guild.getLvl()) * 60) * 1000)) {
                this.player.send("Im1167;" + ((((((10L * guild.getLvl()) * 60) * 1000) - (System.currentTimeMillis() - time)) / 1000) / 60));
                return;
            }
            World.world.getDelayCollectors().remove(map.getId());
        }

        if(map.getSubArea() != null) {
            final byte[] quit = {0};
            World.world.getCollectors().values().stream().filter(collector -> collector != null && collector.getGuildId() == guild.getId()).forEach(collector -> {
                GameMap curMap = World.world.getMap(collector.getMap());
                if (curMap.getSubArea() != null && curMap.getSubArea().getId() == map.getSubArea().getId()) {
                    this.player.send("Im1168;1");
                    quit[0] = 1;
                }
            });
            if(quit[0] == 1) return;
        }

        World.world.getDelayCollectors().put(map.getId(), System.currentTimeMillis());
        this.player.setKamas(this.player.getKamas() - price);

        if (this.player.getKamas() <= 0)
            this.player.setKamas(0);


        short n1 = (short) (Formulas.getRandomValue(1, 129)), n2 = (short) (Formulas.getRandomValue(1, 227));
        Collector collector = new Collector(-1, map.getId(), this.player.getCurCell().getId(), (byte) 3, guild.getId(), n1, n2, this.player, System.currentTimeMillis(), "", 0, 0);
        DatabaseManager.get(CollectorData.class).insert(collector);
        World.world.addCollector(collector);
        SocketManager.GAME_SEND_ADD_PERCO_TO_MAP(map);
        SocketManager.GAME_SEND_STATS_PACKET(this.player);

        for (Player player : guild.getPlayers()) {
            if (player != null && player.isOnline()) {
                SocketManager.GAME_SEND_gITM_PACKET(player, org.starloco.locos.entity.Collector.parseToGuild(player.getGuild().getId()));
                String str = "";
                str += "S" + collector.getFullName() + "|";
                str += collector.getMap() + "|";
                str += World.world.getMap(collector.getMap()).getX() + "|" + World.world.getMap(collector.getMap()).getY() + "|" + this.player.getName();
                SocketManager.GAME_SEND_gT_PACKET(player, str);
            }
        }
    }

    private void getInfos(char c) {
        switch (c) {
            case 'B'://Collector
                SocketManager.GAME_SEND_gIB_PACKET(this.player, this.player.getGuild().parseCollectorToGuild());
                break;
            case 'F'://Enclos
                SocketManager.GAME_SEND_gIF_PACKET(this.player, World.world.parseMPtoGuild(this.player.getGuild().getId()));
                break;
            case 'G'://General
                SocketManager.GAME_SEND_gIG_PACKET(this.player, this.player.getGuild());
                break;
            case 'H'://House
                SocketManager.GAME_SEND_gIH_PACKET(this.player, World.world.getHouseManager().parseHouseToGuild(this.player));
                break;
            case 'M'://Members
                SocketManager.GAME_SEND_gIM_PACKET(this.player, this.player.getGuild(), '+');
                break;
            case 'T'://Collector
                SocketManager.GAME_SEND_gITM_PACKET(this.player, Collector.parseToGuild(this.player.getGuild().getId()));
                Collector.parseAttaque(this.player, this.player.getGuild().getId());
                Collector.parseDefense(this.player, this.player.getGuild().getId());
                break;
        }
    }

    private void invitationGuild(String packet) {
        switch (packet.charAt(0)) {
            case 'R'://Nom this.player
                Player P = World.world.getPlayerByName(packet.substring(1));
                if (P == null || this.player.getGuild() == null) {
                    SocketManager.GAME_SEND_gJ_PACKET(this.player, "Eu");
                    return;
                }
                if (!P.isOnline()) {
                    SocketManager.GAME_SEND_gJ_PACKET(this.player, "Eu");
                    return;
                }
                if (P.isAway()) {
                    SocketManager.GAME_SEND_gJ_PACKET(this.player, "Eo");
                    return;
                }
                if (P.getGuild() != null) {
                    SocketManager.GAME_SEND_gJ_PACKET(this.player, "Ea");
                    return;
                }
                if (!this.player.getGuildMember().canDo(Constant.G_INVITE)) {
                    SocketManager.GAME_SEND_gJ_PACKET(this.player, "Ed");
                    return;
                }
                if (this.player.getGuild().getPlayers().size() >= (40 + this.player.getGuild().getLvl()))//Limite membres max
                {
                    SocketManager.GAME_SEND_Im_PACKET(this.player, "155;"
                            + (40 + this.player.getGuild().getLvl()));
                    return;
                }
                this.player.setInvitation(P.getId());
                P.setInvitation(this.player.getId());

                SocketManager.GAME_SEND_gJ_PACKET(this.player, "R"
                        + packet.substring(1));
                SocketManager.GAME_SEND_gJ_PACKET(P, "r" + this.player.getId() + "|"
                        + this.player.getName() + "|" + this.player.getGuild().getName());
                break;
            case 'E'://ou Refus
                if (packet.substring(1).equalsIgnoreCase(this.player.getInvitation()
                        + "")) {
                    Player p = World.world.getPlayer(this.player.getInvitation());
                    if (p == null)
                        return;//Pas cens? arriver
                    SocketManager.GAME_SEND_gJ_PACKET(p, "Ec");
                }
                break;
            case 'K'://Accepte
                if (packet.substring(1).equalsIgnoreCase(this.player.getInvitation()
                        + "")) {
                    Player p = World.world.getPlayer(this.player.getInvitation());
                    if (p == null)
                        return;//Pas cens? arriver
                    Guild G = p.getGuild();
                    GuildMember GM = G.addNewMember(this.player);
                    DatabaseManager.get(GuildMemberData.class).update(this.player);
                    this.player.setGuildMember(GM);
                    this.player.setInvitation(-1);
                    p.setInvitation(-1);
                    //Packet
                    SocketManager.GAME_SEND_gJ_PACKET(p, "Ka" + this.player.getName());
                    SocketManager.GAME_SEND_gS_PACKET(this.player, GM);
                    SocketManager.GAME_SEND_gJ_PACKET(this.player, "Kj");
                }
                break;
        }
    }

    private void banToGuild(String name) {
        if (this.player.getGuild() == null)
            return;
        Player P = World.world.getPlayerByName(name);
        int guid = -1, guildId = -1;
        Guild toRemGuild;
        GuildMember toRemMember;
        if (P == null) {
            int infos[] = DatabaseManager.get(GuildMemberData.class).isPersoInGuild(name);
            guid = infos[0];
            guildId = infos[1];
            if (guildId < 0 || guid < 0)
                return;
            toRemGuild = World.world.getGuild(guildId);
            toRemMember = toRemGuild.getMember(guid);
        } else {
            toRemGuild = P.getGuild();
            if (toRemGuild == null)//La guilde du this.playernnage n'est pas charger ?
            {
                toRemGuild = World.world.getGuild(this.player.getGuild().getId());//On prend la guilde du this.player qui l'?jecte
            }
            toRemMember = toRemGuild.getMember(P.getId());
            if (toRemMember == null)
                return;//Si le membre n'est pas dans la guilde.
            if (toRemMember.getGuild().getId() != this.player.getGuild().getId())
                return;//Si guilde diff?rente
        }
        //si pas la meme guilde
        if (toRemGuild.getId() != this.player.getGuild().getId()) {
            SocketManager.GAME_SEND_gK_PACKET(this.player, "Ea");
            return;
        }
        //S'il n'a pas le droit de kick, et que ce n'est pas lui m?me la cible
        if (!this.player.getGuildMember().canDo(Constant.G_BAN)
                && this.player.getGuildMember().getPlayerId() != toRemMember.getPlayerId()) {
            SocketManager.GAME_SEND_gK_PACKET(this.player, "Ed");
            return;
        }
        //Si diff?rent : Kick
        if (this.player.getGuildMember().getPlayerId() != toRemMember.getPlayerId()) {
            if (toRemMember.getRank() == 1) //S'il veut kicker le meneur
                return;

            toRemGuild.removeMember(toRemMember.getPlayer());
            if (P != null)
                P.setGuildMember(null);
            if (toRemGuild.getId() == 1)
                toRemMember.getPlayer().modifAlignement(0);
            SocketManager.GAME_SEND_gK_PACKET(this.player, "K" + this.player.getName()
                    + "|" + name);
            if (P != null)
                SocketManager.GAME_SEND_gK_PACKET(P, "K" + this.player.getName());
        } else
        //si quitter
        {
            Guild G = this.player.getGuild();
            if (this.player.getGuildMember().getRank() == 1
                    && G.getPlayers().size() > 1) //Si le meneur veut quitter la guilde mais qu'il reste d'autre joueurs
            {
                SocketManager.GAME_SEND_MESSAGE(this.player, this.player.getLang().trans("game.gameclient.bantoguild.rank.mener"));
                return;
            }
            G.removeMember(this.player);
            this.player.setGuildMember(null);
            if (G.getId() == 1)
                this.player.modifAlignement(0);
            //S'il n'y a plus this.playernne
            if (G.getPlayers().isEmpty())
                World.world.removeGuild(G.getId());
            SocketManager.GAME_SEND_gK_PACKET(this.player, "K" + name + "|" + name);
        }
    }

    private void changeMemberProfil(String packet) {
        if (this.player.getGuild() == null)
            return; //Si le this.playernnage envoyeur n'a m?me pas de guilde

        String[] infos = packet.split("\\|");

        int guid = Integer.parseInt(infos[0]);
        int rank = Integer.parseInt(infos[1]);
        byte xpGive = Byte.parseByte(infos[2]);
        int right = Integer.parseInt(infos[3]);

        Player p = World.world.getPlayer(guid); //Cherche le this.playernnage a qui l'on change les droits dans la m?moire
        GuildMember toChange;
        GuildMember changer = this.player.getGuildMember();

        //R?cup?ration du this.playernnage ? changer, et verification de quelques conditions de base
        if (p == null) //Arrive lorsque le this.playernnage n'est pas charg? dans la m?moire
        {
            int guildId = DatabaseManager.get(GuildMemberData.class).isPersoInGuild(guid); //R?cup?re l'id de la guilde du this.playernnage qui n'est pas dans la m?moire

            if (guildId < 0)
                return; //Si le this.playernnage ? qui les droits doivent ?tre modifi? n'existe pas ou n'a pas de guilde

            if (guildId != this.player.getGuild().getId()) //Si ils ne sont pas dans la m?me guilde
            {
                SocketManager.GAME_SEND_gK_PACKET(this.player, "Ed");
                return;
            }
            toChange = World.world.getGuild(guildId).getMember(guid);
        } else {
            if (p.getGuild() == null)
                return; //Si la this.playernne ? qui changer les droits n'a pas de guilde
            if (this.player.getGuild().getId() != p.getGuild().getId()) //Si ils ne sont pas de la meme guilde
            {
                SocketManager.GAME_SEND_gK_PACKET(this.player, "Ea");
                return;
            }

            toChange = p.getGuildMember();
        }

        //V?rifie ce que le this.playernnage changeur ? le droit de faire

        if (changer.getRank() == 1) //Si c'est le meneur
        {
            if (changer.getPlayerId() == toChange.getPlayerId()) //Si il se modifie lui m?me, reset tthis sauf l'XP
            {
                rank = -1;
                right = -1;
            } else
            //Si il modifie un autre membre
            {
                if (rank == 1) //Si il met un autre membre "Meneur"
                {
                    changer.setAllRights(2, (byte) -1, 29694, this.player); //Met le meneur "Bras droit" avec tthis les droits

                    //D?fini les droits ? mettre au nouveau meneur
                    rank = 1;
                    xpGive = -1;
                    right = 1;
                }
            }
        } else
        //Sinon, c'est un membre normal
        {
            if (toChange.getRank() == 1) //S'il veut changer le meneur, reset tthis sauf l'XP
            {
                rank = -1;
                right = -1;
            } else
            //Sinon il veut changer un membre normal
            {
                if (!changer.canDo(Constant.G_RANK) || rank == 1) //S'il ne peut changer les rang ou qu'il veut mettre meneur
                    rank = -1; //"Reset" le rang

                if (!changer.canDo(Constant.G_RIGHT) || right == 1) //S'il ne peut changer les droits ou qu'il veut mettre les droits de meneur
                    right = -1; //"Reset" les droits

                if (!changer.canDo(Constant.G_HISXP)
                        && !changer.canDo(Constant.G_ALLXP)
                        && changer.getPlayerId() == toChange.getPlayerId()) //S'il ne peut changer l'XP de this.playernne et qu'il est la cible
                    xpGive = -1; //"Reset" l'XP
            }

            if (!changer.canDo(Constant.G_ALLXP) && !changer.equals(toChange)) //S'il n'a pas le droit de changer l'XP des autres et qu'il n'est pas la cible
                xpGive = -1; //"Reset" L'XP
        }
        toChange.setAllRights(rank, xpGive, right, this.player);
        SocketManager.GAME_SEND_gS_PACKET(this.player, this.player.getGuildMember());
        if (p != null && p.getId() != this.player.getId())
            SocketManager.GAME_SEND_gS_PACKET(p, p.getGuildMember());
    }

    private void joinOrLeaveTaxCollector(String packet) {
        int id = -1;
        String CollectorID = Integer.toString(Integer.parseInt(packet.substring(1)), 36);
        try {
            id = Integer.parseInt(CollectorID);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Collector collector = World.world.getCollector(id);
        boolean fail = this.player.isDead() == 1 || collector == null || collector.getInFight() <= 0;

        if(collector != null) {
            switch (packet.charAt(0)) {
                case 'J'://Rejoindre
                   /* if(fail = collector.addDefenseFight(this.player)) {
                        World.world.getGuild(collector.getGuildId()).getPlayers().stream().filter(player -> player != null && player.isOnline()).forEach(player -> {
                            Collector.parseDefense(player, collector.getGuildId());
                        });
                    }
                    break;*/
                    if (player.getFight() == null && !player.isAway() && !player.isInPrison()) {
                        if (collector.getDefenseFight().size() >= World.world.getMap(collector.getMap()).getMaxTeam())
                            return;//Plus de place
                        collector.addDefenseFight(player);
                    }
                    break;


                case 'V'://Leave
                    /*if(fail = collector.delDefenseFight(this.player)) {
                        World.world.getGuild(collector.getGuildId()).getPlayers().stream().filter(player -> player != null && player.isOnline()).forEach(player -> {
                            player.send("gITP-" + collector.getId() + "|" + Integer.toString(player.getId(), 36));
                        });
                    }
                    break;*/
                    collector.delDefenseFight(player);
                    break;
            }
        }
        /*if (!fail) {
            SocketManager.GAME_SEND_BN(this.player);
        }*/
        for (Player z : World.world.getGuild(collector.getGuildId()).getPlayers()) {
            if (z == null)
                continue;
            if (z.isOnline()) {
                SocketManager.GAME_SEND_gITM_PACKET(z, Collector.parseToGuild(collector.getGuildId()));
                Collector.parseAttaque(z, collector.getGuildId());
                Collector.parseDefense(z, collector.getGuildId());
            }
        }
    }

    private void leavePanelGuildCreate() {
        SocketManager.GAME_SEND_gV_PACKET(this.player);
    }

    /** Fin Guild Packet **/

    /**
     * Housse Packet *
     */
    private void parseHousePacket(String packet) {
        switch (packet.charAt(1)) {
            case 'B'://Acheter la maison
                packet = packet.substring(2);
                World.world.getHouseManager().buy(this.player);
                break;
            case 'G'://Maison de guilde
                packet = packet.substring(2);
                if (packet.isEmpty())
                    packet = null;
                World.world.getHouseManager().parseHG(this.player, packet);
                break;
            case 'Q'://Quitter/Expulser de la maison
                packet = packet.substring(2);
                World.world.getHouseManager().leave(this.player, packet);
                break;
            case 'S'://Modification du prix de vente
                packet = packet.substring(2);
                World.world.getHouseManager().sell(this.player, packet);
                break;
            case 'V'://Fermer fenetre d'achat
                World.world.getHouseManager().closeBuy(this.player);
                break;
        }
    }

    /** Fin Housse Packet **/

    /**
     * Enemy Packet *
     */
    private void parseEnemyPacket(String packet) {
        switch (packet.charAt(1)) {
            case 'A'://Ajthiser
                addEnemy(packet);
                break;
            case 'D'://Delete
                removeEnemy(packet);
                break;
            case 'L'://Liste
                SocketManager.GAME_SEND_ENEMY_LIST(this.player);
                break;
        }
    }

    private void addEnemy(String packet) {
        if (this.player == null)
            return;
        int guid = -1;
        switch (packet.charAt(2)) {
            case '%'://Nom de this.player
                packet = packet.substring(3);
                Player P = World.world.getPlayerByName(packet);
                if (P == null) {
                    SocketManager.GAME_SEND_FD_PACKET(this.player, "Ef");
                    return;
                }
                guid = P.getAccID();

                break;
            case '*'://Pseudo
                packet = packet.substring(3);
                Account C = World.world.getAccountByPseudo(packet);
                if (C == null) {
                    SocketManager.GAME_SEND_FD_PACKET(this.player, "Ef");
                    return;
                }
                guid = C.getId();
                break;
            default:
                packet = packet.substring(2);
                Player Pr = World.world.getPlayerByName(packet);
                if (Pr == null || !Pr.isOnline()) {
                    SocketManager.GAME_SEND_FD_PACKET(this.player, "Ef");
                    return;
                }
                guid = Pr.getAccount().getId();
                break;
        }
        if (guid == -1) {
            SocketManager.GAME_SEND_FD_PACKET(this.player, "Ef");
            return;
        }
        account.addEnemy(packet, guid);
    }

    private void removeEnemy(String packet) {
        if (this.player == null)
            return;
        int guid = -1;
        switch (packet.charAt(2)) {
            case '%'://Nom de this.player
                packet = packet.substring(3);
                Player P = World.world.getPlayerByName(packet);
                if (P == null) {
                    SocketManager.GAME_SEND_FD_PACKET(this.player, "Ef");
                    return;
                }
                guid = P.getAccID();

                break;
            case '*'://Pseudo
                packet = packet.substring(3);
                Account C = World.world.getAccountByPseudo(packet);
                if (C == null) {
                    SocketManager.GAME_SEND_FD_PACKET(this.player, "Ef");
                    return;
                }
                guid = C.getId();
                break;
            default:
                packet = packet.substring(2);
                Player Pr = World.world.getPlayerByName(packet);
                if (Pr == null || !Pr.isOnline()) {
                    SocketManager.GAME_SEND_FD_PACKET(this.player, "Ef");
                    return;
                }
                guid = Pr.getAccount().getId();
                break;
        }
        if (guid == -1 || !account.isEnemyWith(guid)) {
            SocketManager.GAME_SEND_FD_PACKET(this.player, "Ef");
            return;
        }
        account.removeEnemy(guid);
    }

    /** Enemy Packet **/

    /**
     * JobOption Packet *
     */
    private void parseJobOption(String packet) {
        switch (packet.charAt(1)) {
            case 'O':
                String[] infos = packet.substring(2).split("\\|");
                int pos = Integer.parseInt(infos[0]);
                int option = Integer.parseInt(infos[1]);
                int slots = Integer.parseInt(infos[2]);
                JobStat SM = this.player.getMetiers().get(pos);
                if (SM == null)
                    return;
                SM.setOptBinValue(option);
                SM.setSlotsPublic(slots);
                SocketManager.GAME_SEND_JO_PACKET(this.player, SM);
                break;
        }
    }

    /** Fin JobOption Packet **/

    /**
     * House Code Packet *
     */
    private void parseHouseKodePacket(String packet) {
        switch (packet.charAt(1)) {
            case 'V'://Fermer fenetre du code
                World.world.getHouseManager().closeCode(this.player);
                break;
            case 'K'://Envoi du code
                sendKey(packet);
                break;
        }
    }

    private void sendKey(String packet) {
        switch (packet.charAt(2)) {
            case '0'://Envoi du code || Boost
                packet = packet.substring(4);
                if (this.player.get_savestat() > 0) {
                    try {
                        int code = 0;
                        code = Integer.parseInt(packet);
                        if (code < 0)
                            return;
                        if (this.player.getCapital() < code)
                            code = this.player.getCapital();
                        this.player.boostStatFixedCount(this.player.get_savestat(), code);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        this.player.set_savestat(0);
                        SocketManager.GAME_SEND_KODE(this.player, "V");
                    }
                } else if (this.player.getExchangeAction() != null && this.player.getExchangeAction().getType() == ExchangeAction.IN_TRUNK) {
                    Trunk.open(this.player, packet, false);
                } else {
                    if (this.player.getInHouse() != null) {
                        this.player.getInHouse().open(this.player, packet, false);
                    }
                }
                break;
            case '1'://Changement du code
                if (this.player.getExchangeAction() != null) {
                    switch(this.player.getExchangeAction().getType()) {
                        case ExchangeAction.LOCK_TRUNK:
                            Trunk.lock(this.player, packet.substring(4));
                            break;
                        case ExchangeAction.LOCK_HOUSE:
                            World.world.getHouseManager().lockIt(this.player, packet.substring(4));
                            break;
                    }
                }
                break;
        }
    }

    /** Fin Housse Code Packet **/

    /**
     * Object Packet *
     */
    private void parseObjectPacket(String packet) {
        if (this.player.getExchangeAction() != null && packet.charAt(1) != 'M')
            return;
        switch (packet.charAt(1)) {
            case 'd':
                destroyObject(packet);
                break;
            case 'D':
                dropObject(packet);
                break;
            case 'M':
                movementObject(packet);
                break;
            case 'U':
                useObject(packet);
                break;
            case 'x':
                dissociateObvi(packet);
                break;
            case 'f':
                feedObvi(packet);
                break;
            case 's':
                setSkinObvi(packet);
                break;
            case 'r':
                setItemShortcut(packet.substring(2));
        }
    }

    private void destroyObject(String packet) {

        String[] infos = packet.substring(2).split("\\|");
        try {
            int guid = Integer.parseInt(infos[0]);
            int qua = 1;
            try {
                qua = Integer.parseInt(infos[1]);
            } catch (Exception ignored) {}

            GameObject obj = this.player.getItems().get(guid);
            if (obj == null || !this.player.hasItemGuid(guid) || qua <= 0
                    || this.player.getFight() != null || this.player.isAway()) {
                //SocketManager.GAME_SEND_DELETE_OBJECT_FAILED_PACKET(this);
                return;
            }
            if (obj.getPosition() != Constant.ITEM_POS_NO_EQUIPED)
                return;
            if (qua > obj.getQuantity())
                qua = obj.getQuantity();
            int newQua = obj.getQuantity() - qua;
            if (newQua <= 0) {
                this.player.removeItem(guid);
                World.world.removeGameObject(guid);
                DatabaseManager.get(ObjectData.class).delete(obj);
                SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(this.player, guid);
            } else {
                obj.setQuantity(newQua);
                SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player, obj);
            }
            SocketManager.GAME_SEND_STATS_PACKET(this.player);
            SocketManager.GAME_SEND_Ow_PACKET(this.player);
        } catch (Exception e) {
            e.printStackTrace();
            SocketManager.GAME_SEND_DELETE_OBJECT_FAILED_PACKET(this);
        }
    }

    private void dropObject(String packet) {
        int guid = -1;
        int qua = -1;
        try {
            guid = Integer.parseInt(packet.substring(2).split("\\|")[0]);
            qua = Integer.parseInt(packet.split("\\|")[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (guid == -1 || qua <= 0 || !this.player.hasItemGuid(guid)
                || this.player.getFight() != null || this.player.isAway())
            return;
        GameObject obj = this.player.getItems().get(guid);

        if(obj.isAttach()) return;

        int cellPosition = Constant.getNearestCellIdUnused(this.player);

        if (cellPosition < 0) {
            SocketManager.GAME_SEND_Im_PACKET(this.player, "1145");
            return;
        }
        if (obj.getPosition() != Constant.ITEM_POS_NO_EQUIPED) {
            obj.setPosition(Constant.ITEM_POS_NO_EQUIPED);
            SocketManager.GAME_SEND_OBJET_MOVE_PACKET(this.player, obj);
            if (obj.getPosition() == Constant.ITEM_POS_ARME
                    || obj.getPosition() == Constant.ITEM_POS_COIFFE
                    || obj.getPosition() == Constant.ITEM_POS_FAMILIER
                    || obj.getPosition() == Constant.ITEM_POS_CAPE
                    || obj.getPosition() == Constant.ITEM_POS_BOUCLIER
                    || obj.getPosition() == Constant.ITEM_POS_NO_EQUIPED)
                SocketManager.GAME_SEND_ON_EQUIP_ITEM(this.player.getCurMap(), this.player);
        }
        if (qua >= obj.getQuantity()) {
            this.player.removeItem(guid);
            this.player.getCurMap().getCase(cellPosition).tryDropItem(obj);
            obj.setPosition(Constant.ITEM_POS_NO_EQUIPED);
            SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(this.player, guid);
        } else {
            obj.setQuantity(obj.getQuantity() - qua);
            GameObject obj2 = obj.getClone(qua, true);
            obj2.setPosition(Constant.ITEM_POS_NO_EQUIPED);
            this.player.getCurMap().getCase(cellPosition).tryDropItem(obj2);
            SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player, obj);
        }
        if (Logging.USE_LOG)
            Logging.getInstance().write("Object", "Dropping : " + this.player.getName() + " a jet? [" + obj.getTemplate().getId() + "@" + obj.getGuid() + ";" + qua + "]");
        SocketManager.GAME_SEND_Ow_PACKET(this.player);
        SocketManager.GAME_SEND_GDO_PACKET_TO_MAP(this.player.getCurMap(), '+', this.player.getCurMap().getCase(cellPosition).getId(), obj.getTemplate().getId(), 0);
        SocketManager.GAME_SEND_STATS_PACKET(this.player);
    }

    public synchronized void movementObject(String packet) {
        String[] infos = packet.substring(2).split("" + (char) 0x0A)[0].split("\\|");
        try {
            int quantity = 1, id = Integer.parseInt(infos[0]), position = Integer.parseInt(infos[1]);
            try {
                quantity = Integer.parseInt(infos[2]);
            } catch (Exception ignored) {}

            GameObject object = this.player.getItems().get(id);
            if (object == null || player.getExchangeAction() != null)
                return;
            if (this.player.getFight() != null)
                if (this.player.getFight().getState() > Constant.FIGHT_STATE_ACTIVE)
                    return;

            /* Pet subscribe **/
            if (position == Constant.ITEM_POS_FAMILIER && !this.player.isSubscribe()) {
                SocketManager.GAME_SEND_EXCHANGE_REQUEST_ERROR(this, 'S');
                return;
            }
            /* End pet subscribe **/

            /* Feed mount **/
            if ((position == Constant.ITEM_POS_DRAGODINDE) && (this.player.getMount() != null)) {
                if (object.getTemplate().getType() == 41) {
                    if (object.getQuantity() > 0) {
                        if (quantity > object.getQuantity())
                            quantity = object.getQuantity();
                        if (object.getQuantity() - quantity > 0) {
                            int newQua = object.getQuantity() - quantity;
                            object.setQuantity(newQua);
                            SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player, object);
                        } else {
                            this.player.deleteItem(id);
                            World.world.removeGameObject(id);
                            SocketManager.SEND_OR_DELETE_ITEM(this, id);
                        }
                    }
                    this.player.getMount().aumEnergy(5000 * quantity);
                    SocketManager.GAME_SEND_Re_PACKET(this.player, "+", this.player.getMount());
                    SocketManager.GAME_SEND_Im_PACKET(this.player, "0105");
                    return;
                }
                SocketManager.GAME_SEND_Im_PACKET(this.player, "190");
                return;
            }
            /* End feed mount **/

            // Pour equiper un item apres avoir desequiper l item a la meme position
            boolean equipBack = false;

            /* Feed pet **/
            if (position == Constant.ITEM_POS_FAMILIER && object.getTemplate().getType() != Constant.ITEM_TYPE_FAMILIER && this.player.getObjetByPos(position) != null) {
                GameObject pets = this.player.getObjetByPos(position);
                Pet p = World.world.getPets(pets.getTemplate().getId());
                if (p == null)
                    return;
                if (p.getEpo() == object.getTemplate().getId()) {
                    PetEntry pet = World.world.getPetsEntry(pets.getGuid());
                    if (pet != null && p.getEpo() == object.getTemplate().getId())
                        pet.giveEpo(this.player);
                    return;
                }
                if (object.getTemplate().getId() != 2239 && !p.canEat(object.getTemplate().getId(), object.getTemplate().getType(), -1)) {
                    SocketManager.GAME_SEND_Im_PACKET(this.player, "153");
                    return;
                }

                int min = 0, max = 0;
                try {
                    min = Integer.parseInt(p.getGap().split(",")[0]);
                    max = Integer.parseInt(p.getGap().split(",")[1]);
                } catch (Exception e) {
                    // ok
                }

                PetEntry MyPets = World.world.getPetsEntry(pets.getGuid());
                if (MyPets == null)
                    return;
                if (p.getType() == 2 || p.getType() == 3
                        || object.getTemplate().getId() == 2239) {
                    if (object.getQuantity() - 1 > 0) {//Si il en reste
                        object.setQuantity(object.getQuantity() - 1);
                        SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player, object);
                    } else {
                        World.world.removeGameObject(object.getGuid());
                        this.player.removeItem(object.getGuid());
                        SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(this.player, object.getGuid());
                    }

                    if (object.getTemplate().getId() == 2239)
                        MyPets.restoreLife(this.player);
                    else
                        MyPets.eat(this.player, min, max, p.statsIdByEat(object.getTemplate().getId(), object.getTemplate().getType(), -1), object);

                    SocketManager.GAME_SEND_UPDATE_OBJECT_DISPLAY_PACKET(this.player, pets);
                    SocketManager.GAME_SEND_Ow_PACKET(this.player);
                    this.player.refreshStats();
                    SocketManager.GAME_SEND_ON_EQUIP_ITEM(this.player.getCurMap(), this.player);
                    SocketManager.GAME_SEND_STATS_PACKET(this.player);
                    if (this.player.getParty() != null)
                        SocketManager.GAME_SEND_PM_MOD_PACKET_TO_GROUP(this.player.getParty(), this.player);
                }
                return;
            /* End feed pet **/
            } else {
                ObjectTemplate template = object.getTemplate();
                int set = template.getPanoId();

                if (set >= 81 && set <= 92 && position != Constant.ITEM_POS_NO_EQUIPED) {
                    String[] stats = template.getStrTemplate().split(",");

                    for (String stat : stats) {
                        String[] split = stat.split("#");
                        int effect = Integer.parseInt(split[0], 16),spell = Integer.parseInt(split[1], 16);
                        int value = Integer.parseInt(split[3], 16);
                        if(effect == 289)
                            value = 1;
                        SocketManager.SEND_SB_SPELL_BOOST(this.player, effect + ";" + spell + ";" + value);
                        this.player.addObjectClassSpell(spell, effect, value);
                    }
                    this.player.addObjectClass(template.getId());
                }
                if (set >= 81 && set <= 92 && position == Constant.ITEM_POS_NO_EQUIPED) {
                    String[] stats = template.getStrTemplate().split(",");

                    for (String stat : stats) {
                        String[] split = stat.split("#");
                        int effect = Integer.parseInt(split[0], 16),spell = Integer.parseInt(split[1], 16);
                        SocketManager.SEND_SB_SPELL_BOOST(this.player, effect + ";" + spell + ";0");
                        this.player.removeObjectClassSpell(Integer.parseInt(split[1], 16));
                    }
                    this.player.removeObjectClass(template.getId());
                }
                if (!Constant.isValidPlaceForItem(object.getTemplate(), position) && position != Constant.ITEM_POS_NO_EQUIPED && object.getTemplate().getType() != 113)
                    return;


                if (!World.world.getConditionManager().validConditions(this.player, object.getTemplate().getConditions())) {
                    SocketManager.GAME_SEND_Im_PACKET(this.player, "119|44;"+object.getTemplate().getId()); // si le this.player ne v?rifie pas les conditions diverses
                    return;
                }
                GameObject shield = null, weapon = null;
                if ((position == Constant.ITEM_POS_BOUCLIER && (weapon = this.player.getObjetByPos(Constant.ITEM_POS_ARME)) != null)
                        || (position == Constant.ITEM_POS_ARME && (shield = this.player.getObjetByPos(Constant.ITEM_POS_BOUCLIER)) != null)) {
                    if (weapon != null) {
                        if (weapon.getTemplate().isTwoHanded()) {
                            this.player.unequipedObjet(weapon);
                            SocketManager.GAME_SEND_Im_PACKET(this.player, "119|44"); // si le this.player ne v?rifie pas les conditions diverses
                            return;
                        }
                    } else {
                        if (object.getTemplate().isTwoHanded()) {
                            if(shield != null)
                                player.unequipedObjet(shield);
                            SocketManager.GAME_SEND_Im_PACKET(this.player, "119|44"); // si le this.player ne v?rifie pas les conditions diverses
                            return;
                        }
                    }

                }

                if (object.getTemplate().getLevel() > this.player.getLevel()) {// si le this.player n'a pas le level
                    SocketManager.GAME_SEND_OAEL_PACKET(this);
                    return;
                }

                //On ne peut ?quiper 2 items de panoplies identiques, ou 2 Dofus identiques
                if (position != Constant.ITEM_POS_NO_EQUIPED && (object.getTemplate().getPanoId() != -1 || object.getTemplate().getType() == Constant.ITEM_TYPE_DOFUS) && this.player.hasEquiped(object.getTemplate().getId()))
                    return;

                // FIN DES VERIFS

                GameObject exObj = this.player.getObjetByPos2(position);//Objet a l'ancienne position
                int objGUID = object.getTemplate().getId();
                // CODE OBVI
                if (object.getTemplate().getType() == 113) {
                    if (exObj == null) {// si on place l'obvi sur un emplacement vide
                        SocketManager.send(this.player, "Im1161");
                        return;
                    }
                    if (exObj.getObvijevanPos() != 0) {// si il y a d?j? un obvi
                        SocketManager.GAME_SEND_BN(this.player);
                        return;
                    }
                    exObj.setObvijevanPos(object.getObvijevanPos()); // L'objet qui ?tait en place a maintenant un obvi
                    DatabaseManager.get(ObvijevanData.class).insert(new Pair<>(exObj.getGuid(), object.getTemplate().getId()));
                    this.player.removeItem(object.getGuid(), 1, false, false); // on enl?ve l'existance de l'obvi en lui-m?me
                    SocketManager.send(this.player, "OR" + object.getGuid()); // on le pr?cise au client.
                    DatabaseManager.get(ObjectData.class).delete(object);

                    exObj.refreshStatsObjet(object.parseStatsStringSansUserObvi() + ",3ca#" + Integer.toHexString(objGUID) + "#0#0#0d0+" + objGUID);

                    SocketManager.send(this.player, exObj.obvijevanOCO_Packet(position));
                    SocketManager.GAME_SEND_ON_EQUIP_ITEM(this.player.getCurMap(), this.player); // Si l'obvi ?tait cape ou coiffe : packet au client
                    // S'il y avait plusieurs objets
                    if (object.getQuantity() > 1) {
                        if (quantity > object.getQuantity())
                            quantity = object.getQuantity();

                        if (object.getQuantity() - quantity > 0)//Si il en reste
                        {
                            int newItemQua = object.getQuantity() - quantity;
                            GameObject newItem = object.getClone(newItemQua, true);
                            this.player.addItem(newItem, false, false);
                            World.world.addGameObject(newItem);
                            object.setQuantity(quantity);
                            SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player, object);
                        }
                    } else {
                        World.world.removeGameObject(object.getGuid());
                    }
                    DatabaseManager.get(PlayerData.class).update(this.player);
                    return; // on s'arr?te l? pour l'obvi
                } // FIN DU CODE OBVI
                if (exObj != null)//S'il y avait d?ja un objet sur cette place on d?s?quipe
                {
                    equipBack = exObj.getGuid() != object.getGuid();
                    GameObject obj2;
                    ObjectTemplate exObjTpl = exObj.getTemplate();
                    int idSetExObj = exObj.getTemplate().getPanoId();
                    if ((obj2 = this.player.getSimilarItem(exObj)) != null)//On le poss?de deja
                    {
                        obj2.setQuantity(obj2.getQuantity()
                                + exObj.getQuantity());
                        SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player, obj2);
                        World.world.removeGameObject(exObj.getGuid());
                        this.player.removeItem(exObj.getGuid());
                        SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(this.player, exObj.getGuid());
                    } else
                    //On ne le poss?de pas
                    {
                        exObj.setPosition(Constant.ITEM_POS_NO_EQUIPED);
                        if ((idSetExObj >= 81 && idSetExObj <= 92)
                                || (idSetExObj >= 201 && idSetExObj <= 212)) {
                            String[] stats = exObjTpl.getStrTemplate().split(",");
                            for (String stat : stats) {
                                String[] val = stat.split("#");
                                String modifi = Integer.parseInt(val[0], 16)
                                        + ";" + Integer.parseInt(val[1], 16)
                                        + ";0";
                                SocketManager.SEND_SB_SPELL_BOOST(this.player, modifi);
                                this.player.removeObjectClassSpell(Integer.parseInt(val[1], 16));
                            }
                            this.player.removeObjectClass(exObjTpl.getId());
                        }
                        SocketManager.GAME_SEND_OBJET_MOVE_PACKET(this.player, exObj);
                    }
                    if (this.player.getObjetByPos(Constant.ITEM_POS_ARME) == null)
                        SocketManager.GAME_SEND_OT_PACKET(this, -1);

                    //Si objet de panoplie
                    if (exObj.getTemplate().getPanoId() > 0)
                        SocketManager.GAME_SEND_OS_PACKET(this.player, exObj.getTemplate().getPanoId());
                } else {
                    GameObject obj2;
                    //On a un objet similaire
                    if ((obj2 = this.player.getSimilarItem(object)) != null) {
                        if (quantity > object.getQuantity())
                            quantity = object.getQuantity();

                        obj2.setQuantity(obj2.getQuantity() + quantity);
                        SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player, obj2);

                        if (object.getQuantity() - quantity > 0)//Si il en reste
                        {
                            object.setQuantity(object.getQuantity() - quantity);
                            SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player, object);
                        } else
                        //Sinon on supprime
                        {
                            World.world.removeGameObject(object.getGuid());
                            this.player.removeItem(object.getGuid());
                            SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(this.player, object.getGuid());
                        }
                    } else
                    //Pas d'objets similaires
                    {
                        if (object.getPosition() > 16) {
                            int oldPos = object.getPosition();
                            object.setPosition(position);
                            SocketManager.GAME_SEND_OBJET_MOVE_PACKET(this.player, object);

                            if (object.getQuantity() > 1) {
                                if (quantity > object.getQuantity())
                                    quantity = object.getQuantity();

                                if (object.getQuantity() - quantity > 0) {//Si il en reste
                                    GameObject newItem = object.getClone(object.getQuantity()
                                            - quantity, true);
                                    newItem.setPosition(oldPos);

                                    object.setQuantity(quantity);
                                    SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player, object);

                                    if (this.player.addItem(newItem, false, false))
                                        World.world.addGameObject(newItem);
                                }
                            }
                        } else {
                            object.setPosition(position);
                            SocketManager.GAME_SEND_OBJET_MOVE_PACKET(this.player, object);

                            if (object.getQuantity() > 1) {
                                if (quantity > object.getQuantity())
                                    quantity = object.getQuantity();

                                if (object.getQuantity() - quantity > 0) {//Si il en reste
                                    int newItemQua = object.getQuantity() - quantity;
                                    GameObject newItem = object.getClone(newItemQua, true);
                                    if (this.player.addItem(newItem, true, false))
                                        World.world.addGameObject(newItem);
                                    object.setQuantity(quantity);
                                    SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player, object);
                                }
                            }
                        }
                    }
                }
                if (position == Constant.ITEM_POS_ARME) {
                    switch (object.getTemplate().getId())
                    //Incarnation
                    {
                        case 9544: // Tourmenteur t?nebres
                            this.player.setFullMorph(1, false, false);
                            break;
                        case 9545: // Tourmenteur feu
                            this.player.setFullMorph(5, false, false);
                            break;
                        case 9546: // Tourmenteur feuille
                            this.player.setFullMorph(4, false, false);
                            break;
                        case 9547: // Tourmenteur gthiste
                            this.player.setFullMorph(3, false, false);
                            break;
                        case 9548: // Tourmenteur terre
                            this.player.setFullMorph(2, false, false);
                            break;
                        case 10125: // Bandit Archer
                            this.player.setFullMorph(7, false, false);
                            break;
                        case 10126: // Bandit Fine Lame
                            this.player.setFullMorph(6, false, false);
                            break;
                        case 10127: // Bandit Baroudeur
                            this.player.setFullMorph(8, false, false);
                            break;
                        case 10133: // Bandit Ensorcelleur
                            this.player.setFullMorph(9, false, false);
                            break;
                    }
                } else {// Tourmenteur ; on d?morphe
                    if (Constant.isIncarnationWeapon(object.getTemplate().getId()))
                        this.player.unsetFullMorph();
                }

                if (object.getTemplate().getId() == 2157) {
                    if (position == Constant.ITEM_POS_COIFFE) {
                        this.player.setGfxId((this.player.getSexe() == 1) ? 8009 : 8006);
                        SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(this.player.getCurMap(), this.player.getId());
                        SocketManager.GAME_SEND_ADD_PLAYER_TO_MAP(this.player.getCurMap(), this.player);
                        SocketManager.GAME_SEND_MESSAGE(this.player, this.player.getLang().trans("game.gameclient.movementobject.mercenaire"));
                    } else if (position == Constant.ITEM_POS_NO_EQUIPED) {
                        this.player.setGfxId(this.player.getClasse() * 10 + this.player.getSexe());
                        SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(this.player.getCurMap(), this.player.getId());
                        SocketManager.GAME_SEND_ADD_PLAYER_TO_MAP(this.player.getCurMap(), this.player);
                        SocketManager.GAME_SEND_MESSAGE(this.player, this.player.getLang().trans("game.gameclient.movementobject.mercenaire.disable"));
                    }
                }
                if (object.getTemplate().getId() != 2157 && this.player.isMorphMercenaire() && position == Constant.ITEM_POS_COIFFE) {
                    this.player.setGfxId(this.player.getClasse() * 10 + this.player.getSexe());
                    SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(this.player.getCurMap(), this.player.getId());
                    SocketManager.GAME_SEND_ADD_PLAYER_TO_MAP(this.player.getCurMap(), this.player);
                    SocketManager.GAME_SEND_MESSAGE(this.player, "Vous n'?tes plus mercenaire.");
                }
                // Si équipe un objet de métier, on lui donne le métier sinon on l'enlève
                /*if (position == Constant.ITEM_POS_ARME && this.player.getObjetByPos(Constant.ITEM_POS_ARME) != null) {
                    World.world.getJobs().stream().filter(e -> e.isValidTool(this.player.getObjetByPos(Constant.ITEM_POS_ARME).getTemplate().getId()))
                            .forEach(e -> {
                                this.player.learnJob(e);
                                JobStat SM = this.player.getMetierByID(e.getId());
                                if(SM != null) {
                                    SM.addXp(this.player, 581688);
                                    ArrayList<JobStat> SMs = new ArrayList<>();
                                    SMs.add(SM);
                                    SocketManager.GAME_SEND_JX_PACKET(this.player, SMs);
                                }
                                SocketManager.GAME_SEND_OT_PACKET(this, e.getId());
                            });
                }
                if(position == -1 && object.getTemplate().getPACost() > 0) {
                    World.world.getJobs().forEach(e -> {
                        JobStat job = this.player.getMetierByID(e.getId());
                        if (job != null) {
                            this.player.unlearnJob(job.getId());
                            this.player.send("JR" + e.getId());
                            SocketManager.GAME_SEND_OT_PACKET(this, -1);
                        }
                    });
                }*/

                this.player.refreshStats();
                SocketManager.GAME_SEND_STATS_PACKET(this.player);

                if (this.player.getParty() != null)
                    SocketManager.GAME_SEND_PM_MOD_PACKET_TO_GROUP(this.player.getParty(), this.player);

                if (position == Constant.ITEM_POS_ARME || position == Constant.ITEM_POS_COIFFE || position == Constant.ITEM_POS_FAMILIER || position == Constant.ITEM_POS_CAPE || position == Constant.ITEM_POS_BOUCLIER || position == Constant.ITEM_POS_NO_EQUIPED)
                    SocketManager.GAME_SEND_ON_EQUIP_ITEM(this.player.getCurMap(), this.player);

                //Si familier
                if (position == Constant.ITEM_POS_FAMILIER && this.player.isOnMount())
                    this.player.toogleOnMount();
                //Verif pour les thisils de m�tier
                if (position == Constant.ITEM_POS_NO_EQUIPED && this.player.getObjetByPos(Constant.ITEM_POS_ARME) == null)
                    SocketManager.GAME_SEND_OT_PACKET(this, -1);
                if (position == Constant.ITEM_POS_ARME && this.player.getObjetByPos(Constant.ITEM_POS_ARME) != null)
                    this.player.getMetiers().entrySet().stream().filter(e -> e.getValue().getTemplate().isValidTool(this.player.getObjetByPos(Constant.ITEM_POS_ARME).getTemplate().getId())).forEach(e -> SocketManager.GAME_SEND_OT_PACKET(this, e.getValue().getTemplate().getId()));


                //Si objet de panoplie
                if (object.getTemplate().getPanoId() > 0)
                    SocketManager.GAME_SEND_OS_PACKET(this.player, object.getTemplate().getPanoId());
                if (this.player.getFight() != null)
                    SocketManager.GAME_SEND_ON_EQUIP_ITEM_FIGHT(this.player, this.player.getFight().getFighterByPerso(this.player), this.player.getFight());
            }

            // Start craft secure show/hide
            if (position == Constant.ITEM_POS_ARME || (position == Constant.ITEM_POS_NO_EQUIPED && object.getTemplate().getPACost() > 0)) {
                this.player.refreshCraftSecure(true);
            }
            // End craft secure show/hide
            if(this.player.getFight() != null) {
                Fighter target = this.player.getFight().getFighterByPerso(this.player);
                this.player.getFight().getFighters(7).stream().filter(fighter -> fighter != null && fighter.getPlayer() != null).forEach(fighter -> fighter.getPlayer().send(this.player.getCurMap().getFighterGMPacket(this.player)));
                target.setPdv(this.player.getCurPdv());
                SocketManager.GAME_SEND_STATS_PACKET(this.player);
            }

            this.player.verifEquiped();
            DatabaseManager.get(PlayerData.class).update(this.player);
            if(equipBack)
                this.movementObject(packet);
        } catch (Exception e) {
            e.printStackTrace();
            SocketManager.GAME_SEND_DELETE_OBJECT_FAILED_PACKET(this);
        }
    }


    private void setItemShortcut(String packet) {
        // Official client seems to store Position -> Template+Stats rather than Position->guid
        // Maybe it's time for ItemHash (String) representing a TemplateID+Stats pair as a Comparable
        char action = packet.charAt(0);
        packet = packet.substring(1);
        String[] parts = packet.split(";");
        int position = Integer.parseInt(parts[0]);
        switch(action) {
            case 'A':
                int itemGUID = Integer.parseInt(parts[1]);
                if(player.addItemShortcutSend(position, itemGUID)) return;
            case 'M':
                int otherPos = Integer.parseInt(parts[1]);
                if(player.moveItemShortcutSend(position, otherPos)) return;
            case 'R':
                if(player.removeItemShortcutSend(position)) return;
        }
        send("BN"); // Error
        return;
    }

    private void useObject(String packet) {
        int guid = -1;
        int targetGuid = -1;
        short cellID = -1;
        int quantity = 1;
        Player target = null;
        try {
            String[] infos = packet.substring(2).split("\\|");
            guid = Integer.parseInt(infos[0]);
            quantity = infos.length > 3 ? Integer.parseInt(infos[3]) : 1;

            try {
                targetGuid = Integer.parseInt(infos[1]);
            } catch (Exception e) {
                // ok
            }
            try {
                cellID = Short.parseShort(infos[2]);
            } catch (Exception e) {
                // ok
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        //Si le joueur n'a pas l'objet
        if (World.world.getPlayer(targetGuid) != null)
            target = World.world.getPlayer(targetGuid);
        if (!this.player.hasItemGuid(guid) || this.player.isAway())
            return;
        if (target != null && target.isAway())
            return;
        GameObject obj = this.player.getItems().get(guid);
        if (obj == null)
            return;
        ObjectTemplate T = obj.getTemplate();
        if (T.getLevel() > this.player.getLevel() || (!obj.getTemplate().getConditions().equalsIgnoreCase("") && !World.world.getConditionManager().validConditions(this.player, obj.getTemplate().getConditions()))) {
            SocketManager.GAME_SEND_Im_PACKET(this.player, "119|43");
            return;
        }
        T.applyAction(this.player, target, guid, cellID, quantity);
        if (T.getType() == Constant.ITEM_TYPE_PAIN || T.getType() == Constant.ITEM_TYPE_VIANDE_COMESTIBLE) {
            if (target != null)
                SocketManager.GAME_SEND_eUK_PACKET_TO_MAP(target.getCurMap(), target.getId(), 17);
            else
                SocketManager.GAME_SEND_eUK_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), 17);
        } else if (T.getType() == Constant.ITEM_TYPE_BIERE) {
            if (target != null)
                SocketManager.GAME_SEND_eUK_PACKET_TO_MAP(target.getCurMap(), target.getId(), 18);
            else
                SocketManager.GAME_SEND_eUK_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), 18);
        }
    }

    private void dissociateObvi(String packet) {
        int guid = -1;
        int pos = -1;
        try {
            guid = Integer.parseInt(packet.substring(2).split("\\|")[0]);
            pos = Integer.parseInt(packet.split("\\|")[1]);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if ((guid == -1) || (!this.player.hasItemGuid(guid)))
            return;
        GameObject obj = this.player.getItems().get(guid);
        int idOBVI = DatabaseManager.get(ObvijevanData.class).load(obj.getGuid()).getFirst();

        if (idOBVI == -1) {
            DatabaseManager.get(ObvijevanData.class).delete(new Pair<>(obj.getGuid(), obj.getGuid()));
            switch (obj.getTemplate().getType()) {
                case 1:
                    idOBVI = 9255;
                    break;
                case 9:
                    idOBVI = 9256;
                    break;
                case 16:
                    idOBVI = 9234;
                    break;
                case 17:
                    idOBVI = 9233;
                    break;
                default:
                    SocketManager.GAME_SEND_MESSAGE(this.player, "Erreur d'obvijevan numero: 4. Merci de nous le signaler si le probleme est grave.", "000000");
                    return;
            }
        }

        ObjectTemplate t = World.world.getObjTemplate(idOBVI);
        GameObject obV = t.createNewItem(1, true);
        String obviStats = obj.getObvijevanStatsOnly();
        if (obviStats.equals("")) {
            SocketManager.GAME_SEND_MESSAGE(this.player, "Erreur d'obvijevan numero: 3. Merci de nous le signaler si le probleme est grave.", "000000");
            return;
        }
        obV.clearStats();
        obV.refreshStatsObjet(obviStats);
        if (this.player.addItem(obV, true, false))
            World.world.addGameObject(obV);
        obj.removeAllObvijevanStats();
        SocketManager.send(this.player, obj.obvijevanOCO_Packet(pos));
        SocketManager.GAME_SEND_ON_EQUIP_ITEM(this.player.getCurMap(), this.player);
        DatabaseManager.get(PlayerData.class).update(this.player);
    }

    private void feedObvi(String packet) {
        int guid = -1;
        int pos = -1;
        int victime = -1;
        try {
            guid = Integer.parseInt(packet.substring(2).split("\\|")[0]);
            pos = Integer.parseInt(packet.split("\\|")[1]);
            victime = Integer.parseInt(packet.split("\\|")[2]);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if ((guid == -1) || (!this.player.hasItemGuid(guid)))
            return;
        GameObject obj = this.player.getItems().get(guid);
        GameObject objVictime = World.world.getGameObject(victime);
        obj.obvijevanNourir(objVictime);

        int qua = objVictime.getQuantity();
        if (qua <= 1) {
            this.player.removeItem(objVictime.getGuid());
            SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(this.player, objVictime.getGuid());
        } else {
            objVictime.setQuantity(qua - 1);
            SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player, objVictime);
        }
        SocketManager.send(this.player, obj.obvijevanOCO_Packet(pos));
        DatabaseManager.get(PlayerData.class).update(this.player);
    }

    private void setSkinObvi(String packet) {
        int guid = -1;
        int pos = -1;
        int val = -1;
        try {
            guid = Integer.parseInt(packet.substring(2).split("\\|")[0]);
            pos = Integer.parseInt(packet.split("\\|")[1]);
            val = Integer.parseInt(packet.split("\\|")[2]);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if ((guid == -1) || (!this.player.hasItemGuid(guid)))
            return;
        GameObject obj = this.player.getItems().get(guid);
        if ((val >= 21) || (val <= 0))
            return;

        obj.obvijevanChangeStat(972, val);
        SocketManager.send(this.player, obj.obvijevanOCO_Packet(pos));
        if (pos != -1)
            SocketManager.GAME_SEND_ON_EQUIP_ITEM(this.player.getCurMap(), this.player);
    }

    /** Fin Object Packet **/

    /**
     * Group Packet *
     */
    private void parseGroupPacket(String packet) {
        switch (packet.charAt(1)) {
            case 'A'://Accepter invitation
                acceptInvitation();
                break;
            case 'F'://Suivre membre du groupe PF+GUID
                followMember(packet);
                break;
            case 'G'://Suivez le tous PG+GUID
                followAllMember(packet);
                break;
            case 'I'://inviation
                inviteParty(packet);
                break;
            case 'R'://Refuse
                refuseInvitation();
                break;
            case 'V'://Quitter
                leaveParty(packet);
                break;
            case 'W'://Localisation du groupe
                whereIsParty();
                break;
        }
    }

    private void acceptInvitation() {
        if (this.player == null || this.player.getInvitation() == 0)
            return;

        Player target = World.world.getPlayer(this.player.getInvitation());

        if (target == null)
            return;

        Party party = target.getParty();

        if (party == null) {
            party = new Party(target, this.player);
            SocketManager.GAME_SEND_GROUP_CREATE(this, party);
            SocketManager.GAME_SEND_PL_PACKET(this, party);
            SocketManager.GAME_SEND_GROUP_CREATE(target.getGameClient(), party);
            SocketManager.GAME_SEND_PL_PACKET(target.getGameClient(), party);
            target.setParty(party);
            SocketManager.GAME_SEND_ALL_PM_ADD_PACKET(target.getGameClient(), party);
        } else {
            SocketManager.GAME_SEND_GROUP_CREATE(this, party);
            SocketManager.GAME_SEND_PL_PACKET(this, party);
            SocketManager.GAME_SEND_PM_ADD_PACKET_TO_GROUP(party, this.player);
            party.addPlayer(this.player);
        }

        this.player.setParty(party);
        SocketManager.GAME_SEND_ALL_PM_ADD_PACKET(this, party);
        SocketManager.GAME_SEND_PR_PACKET(target);
    }

    private void followMember(String packet) {
        Party g = this.player.getParty();
        if (g == null)
            return;
        int pGuid = -1;
        try {
            pGuid = Integer.parseInt(packet.substring(3));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return;
        }
        if (pGuid == -1)
            return;
        Player P = World.world.getPlayer(pGuid);
        if (P == null || !P.isOnline())
            return;
        if (packet.charAt(2) == '+')//Suivre
        {
            if (this.player.follow != null)
                this.player.follow.follower.remove(this.player.getId());
            SocketManager.GAME_SEND_FLAG_PACKET(this.player, P);
            SocketManager.GAME_SEND_PF(this.player, "+" + P.getId());
            this.player.follow = P;
            P.follower.put(this.player.getId(), this.player);
            P.send("Im052;" + this.player.getName());
        } else if (packet.charAt(2) == '-')//Ne plus suivre
        {
            SocketManager.GAME_SEND_DELETE_FLAG_PACKET(this.player);
            SocketManager.GAME_SEND_PF(this.player, "-");
            this.player.follow = null;
            P.follower.remove(this.player.getId());
            P.send("Im053;" + this.player.getName());
        }
    }

    private void followAllMember(String packet) {
        Party g2 = this.player.getParty();
        if (g2 == null)
            return;
        int pGuid2 = -1;
        try {
            pGuid2 = Integer.parseInt(packet.substring(3));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return;
        }

        if (pGuid2 == -1)
            return;
        Player P2 = World.world.getPlayer(pGuid2);
        if (P2 == null || !P2.isOnline())
            return;
        if (packet.charAt(2) == '+')//Suivre
        {
            for (Player T : g2.getPlayers()) {
                if (T.getId() == P2.getId())
                    continue;
                if (T.follow != null)
                    T.follow.follower.remove(this.player.getId());
                SocketManager.GAME_SEND_FLAG_PACKET(T, P2);
                SocketManager.GAME_SEND_PF(T, "+" + P2.getId());
                T.follow = P2;
                P2.follower.put(T.getId(), T);
                P2.send("Im0178");
            }
        } else if (packet.charAt(2) == '-')//Ne plus suivre
        {
            for (Player T : g2.getPlayers()) {
                if (T.getId() == P2.getId())
                    continue;
                SocketManager.GAME_SEND_DELETE_FLAG_PACKET(T);
                SocketManager.GAME_SEND_PF(T, "-");
                T.follow = null;
                P2.follower.remove(T.getId());
                P2.send("Im053;" + T.getName());
            }
        }
    }

    private void inviteParty(String packet) {
        if (this.player == null)
            return;

        String name = packet.substring(2);
        Player target = World.world.getPlayerByName(name);

        if (target == null || !target.isOnline()) {
            SocketManager.GAME_SEND_GROUP_INVITATION_ERROR(this, "n" + name);
            return;
        }
        if (target.getParty() != null) {
            SocketManager.GAME_SEND_GROUP_INVITATION_ERROR(this, "a" + name);
            return;
        }
        if (target.getGroup() != null && this.player.getGroup() == null) {
            if (!target.getGroup().isPlayer()) {
                SocketManager.GAME_SEND_MESSAGE(this.player, this.player.getLang().trans("game.gameclient.inviteparty.noauthorize"));
                return;
            }
        }
        if (this.player.getParty() != null && this.player.getParty().getPlayers().size() == 8) {
            SocketManager.GAME_SEND_GROUP_INVITATION_ERROR(this, "f");
            return;
        }

        target.setInvitation(this.player.getId());
        this.player.setInvitation(target.getId());
        SocketManager.GAME_SEND_GROUP_INVITATION(this, this.player.getName(), name);
        SocketManager.GAME_SEND_GROUP_INVITATION(target.getGameClient(), this.player.getName(), name);
    }

    private void refuseInvitation() {
        if (this.player == null || this.player.getInvitation() == 0)
            return;

        Player player = World.world.getPlayer(this.player.getInvitation());

        if (player != null) {
            player.setInvitation(0);
            SocketManager.GAME_SEND_PR_PACKET(player);
        }

        this.player.setInvitation(0);
    }

    private void leaveParty(String packet) {
        Party party = this.player.getParty();

        if (party != null) {
            if (packet.length() == 2) { // player leave group
                party.leave(this.player);
                SocketManager.GAME_SEND_PV_PACKET(this, "");
                SocketManager.GAME_SEND_IH_PACKET(this.player, "");
            } else if (party.isChief(this.player.getId())) { // kick player from group
                int id;
                try {
                    id = Integer.parseInt(packet.substring(2));
                } catch (NumberFormatException e) {
                    return;
                }

                Player target = World.world.getPlayer(id);
                party.leave(target);
                SocketManager.GAME_SEND_PV_PACKET(target.getGameClient(), String.valueOf(this.player.getId()));
                SocketManager.GAME_SEND_IH_PACKET(target, "");
            }
        }
    }

    private void whereIsParty() {
        if (this.player == null)
            return;
        Party g = this.player.getParty();
        if (g == null)
            return;
        String str = "";
        boolean isFirst = true;
        for (Player GroupP : this.player.getParty().getPlayers()) {
            if (!isFirst)
                str += "|";
            str += GroupP.getCurMap().getX() + ";" + GroupP.getCurMap().getY()
                    + ";" + GroupP.getCurMap().getId() + ";2;" + GroupP.getId()
                    + ";" + GroupP.getName();
            isFirst = false;
        }
        SocketManager.GAME_SEND_IH_PACKET(this.player, str);
    }

    /** Fin Group Packet **/

    /**
     * MountPark Packet *
     */
    private void parseMountPacket(String packet) {
        switch (packet.charAt(1)) {
            case 'b'://Achat d'un enclos
                buyMountPark(packet);
                break;
            case 'd'://Demande Description en Item.
                dataMount(packet);
                break;
            case 'p'://Demande Decription en Enclo.
                dataMount(packet);
                break;
            case 'f'://Lib?re la monture
                killMount(packet);
                break;
            case 'n'://Change le nom
                renameMount(packet.substring(2));
                break;
            case 'r'://Monter sur la dinde
                rideMount();
                break;
            case 's'://Vendre l'enclo
                sellMountPark(packet);
                break;
            case 'v'://Fermeture panneau d'achat
                SocketManager.GAME_SEND_R_PACKET(this.player, "v");
                break;
            case 'x'://Change l'xp donner a la dinde
                setXpMount(packet);
                break;
            case 'c'://Castrer la dinde
                castrateMount();
                break;
            case 'o':// retirer objet de l'etable
                removeObjectInMountPark(packet);
                break;
        }
    }

    private void buyMountPark(String packet) {
        SocketManager.GAME_SEND_R_PACKET(this.player, "v");//Fermeture du panneau
        MountPark MP = this.player.getCurMap().getMountPark();
        Player Seller = World.world.getPlayer(MP.getOwner());
        if (MP.getOwner() == -1) {
            SocketManager.GAME_SEND_Im_PACKET(this.player, "196");
            return;
        }
        if (MP.getPrice() == 0) {
            SocketManager.GAME_SEND_Im_PACKET(this.player, "197");
            return;
        }
        if (this.player.getGuild() == null) {
            SocketManager.GAME_SEND_Im_PACKET(this.player, "1135");
            return;
        }
        if (this.player.getGuildMember().getRank() != 1) {
            SocketManager.GAME_SEND_Im_PACKET(this.player, "198");
            return;
        }
        if((System.currentTimeMillis() - this.player.getGuild().getDate()) <= 3600000L /**2419200000L**/) {//FIXME Ankalike: 2 semaines, en commentaire = 2 mois officiel
            SocketManager.GAME_SEND_MESSAGE(this.player, this.player.getLang().trans("game.gameclient.buy.mountpark.wait"));
            //this.player.send("Im1103");
            return;
        }
        byte enclosMax = (byte) Math.floor(this.player.getGuild().getLvl() / 10);
        byte TotalEncloGuild = (byte) World.world.totalMPGuild(this.player.getGuild().getId());
        if (TotalEncloGuild >= enclosMax) {
            SocketManager.GAME_SEND_Im_PACKET(this.player, "1103");
            return;
        }
        if (this.player.getKamas() < MP.getPrice()) {
            SocketManager.GAME_SEND_Im_PACKET(this.player, "182");
            return;
        }
        long NewKamas = this.player.getKamas() - MP.getPrice();
        this.player.setKamas(NewKamas);
        if (Seller != null) {
            long NewSellerBankKamas = Seller.getBankKamas() + MP.getPrice();
            Seller.setBankKamas(NewSellerBankKamas);
            if (Seller.isOnline()) {
                SocketManager.GAME_SEND_MESSAGE(this.player, this.player.getLang().trans("Vous venez de vendre votre enclos ! ", MP.getPrice()));
            }
        }
        MP.setPrice(0);//On vide le prix
        MP.setOwner(this.player.getId());
        MP.setGuild(this.player.getGuild());
        DatabaseManager.get(MountParkData.class).update(MP);
        DatabaseManager.get(PlayerData.class).update(this.player);
        //On rafraichit l'enclo
        for (Player z : this.player.getCurMap().getPlayers()) {
            SocketManager.GAME_SEND_Rp_PACKET(z, MP);
        }
    }

    private void dataMount(String packet) {
        try {
            int id = Integer.parseInt(packet.substring(2).split("\\|")[0]);

            if (id != 0) {
                Mount mount = World.world.getMountById(id);
                if (mount != null)
                    SocketManager.GAME_SEND_MOUNT_DESCRIPTION_PACKET(this.player, mount);
            }
        } catch (Exception ignored) {}
    }

    private void killMount(String packet) {
        if (this.player.getMount().getObjects().size() != 0) {
            SocketManager.GAME_SEND_Im_PACKET(this.player, "1106");
            return;
        }

        if (this.player.getMount() != null && this.player.isOnMount())
            this.player.toogleOnMount();
        SocketManager.GAME_SEND_Re_PACKET(this.player, "-", this.player.getMount());
        DatabaseManager.get(MountData.class).delete(this.player.getMount());
        World.world.removeMount(this.player.getMount().getId());
        this.player.setMount(null);
    }

    private void renameMount(String name) {
        if (this.player.getMount() == null)
            return;
        this.player.getMount().setName(name);
        DatabaseManager.get(MountData.class).update(this.player.getMount());
        SocketManager.GAME_SEND_Rn_PACKET(this.player, name);
    }

    private void rideMount() {
        if (!this.player.isSubscribe()) {
            SocketManager.GAME_SEND_EXCHANGE_REQUEST_ERROR(this, 'S');
            return;
        }

        this.player.toogleOnMount();
    }

    private void sellMountPark(String packet) {
        SocketManager.GAME_SEND_R_PACKET(this.player, "v");//Fermeture du panneau
        int price = Integer.parseInt(packet.substring(2));
        MountPark MP1 = this.player.getCurMap().getMountPark();
        if (!MP1.getEtable().isEmpty() || !MP1.getListOfRaising().isEmpty()) {
            SocketManager.GAME_SEND_MESSAGE(this.player, this.player.getLang().trans("game.gameclient.sellmountpark.mountparkfull"));
            return;
        }
        if (MP1.getOwner() == -1) {
            SocketManager.GAME_SEND_Im_PACKET(this.player, "194");
            return;
        }
        if (MP1.getOwner() != this.player.getId()) {
            SocketManager.GAME_SEND_Im_PACKET(this.player, "195");
            return;
        }
        MP1.setPrice(price);
        DatabaseManager.get(MountParkData.class).update(MP1);
        DatabaseManager.get(PlayerData.class).update(this.player);
        //On rafraichit l'enclo
        for (Player z : this.player.getCurMap().getPlayers()) {
            SocketManager.GAME_SEND_Rp_PACKET(z, MP1);
        }
    }

    private void setXpMount(String packet) {
        try {
            int xp = Integer.parseInt(packet.substring(2));
            if (xp < 0)
                xp = 0;
            if (xp > 90)
                xp = 90;
            this.player.setMountGiveXp(xp);
            SocketManager.GAME_SEND_Rx_PACKET(this.player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void castrateMount() {
        if (this.player.getMount() == null) {
            SocketManager.GAME_SEND_Re_PACKET(this.player, "Er", null);
            return;
        }
        this.player.getMount().setCastrated();
        SocketManager.GAME_SEND_Re_PACKET(this.player, "+", this.player.getMount());
    }

    private void removeObjectInMountPark(String packet) {
        int cell = Integer.parseInt(packet.substring(2));
        GameMap map = this.player.getCurMap();
        if (map.getMountPark() == null)
            return;
        MountPark MP = map.getMountPark();

        if (this.player.getGuild() == null) {
            SocketManager.GAME_SEND_BN(this);
            return;
        }
        if (!this.player.getGuildMember().canDo(8192)) {
            SocketManager.GAME_SEND_Im_PACKET(this.player, "193");
            return;
        }

        int item = MP.getCellAndObject().get(cell);
        ObjectTemplate t = World.world.getObjTemplate(item);
        GameObject obj = t.createNewItem(1, false); // creation de l'item au stats incorrecte

        int statNew = 0;// on vas chercher la valeur de la resistance de l'item
        for (Map.Entry<Integer, Map<Integer, Integer>> entry : MP.getObjDurab().entrySet()) {
            if (entry.getKey().equals(cell)) {
                for (Map.Entry<Integer, Integer> entry2 : entry.getValue().entrySet())
                    statNew = entry2.getValue();
            }
        }
        obj.getTxtStat().remove(812); //on retire les stats "32c"
        obj.addTxtStat(812, Integer.toHexString(statNew));// on ajthis les bonnes stats

        if (this.player.addItem(obj, true, false))//Si le joueur n'avait pas d'item similaire
            World.world.addGameObject(obj);
        if (MP.delObject(cell))
            SocketManager.SEND_GDO_PUT_OBJECT_MOUNT(map, cell + ";0;0"); // on retire l'objet de la map
    }

    /** Fin MountPark Packet **/

    /**
     * Quest Packet *
     */
    private void parseQuestData(String packet) {
        switch (packet.charAt(1)) {
            case 'L': // Quests list
                player.send(player.encodeQuestList());
                break;

            case 'S': // Quest steps
                int id = Integer.parseInt(packet.substring(2));
                player.sendQuestStatus(id);
                break;
        }
    }
    /** Fin Quest Packet **/

    /**
     * Spell Packet *
     */
    private void parseSpellPacket(String packet) {
        switch (packet.charAt(1)) {
            case 'B':
                boostSpell(packet);
                break;
            case 'F'://Oublie de sort
                forgetSpell(packet);
                break;
            case 'M':
                moveSpell(packet);
                break;
            case 'R':
                removeSpell(packet);
                break;
        }
    }

    private void boostSpell(String packet) {
        try {
            int id = Integer.parseInt(packet.substring(2));

            if (this.player.boostSpell(id)) {
                SocketManager.GAME_SEND_SPELL_UPGRADE_SUCCESS(this, id, this.player.getSortStatBySortIfHas(id).getLevel());
                SocketManager.GAME_SEND_STATS_PACKET(this.player);
            } else {
                SocketManager.GAME_SEND_SPELL_UPGRADE_FAILED(this);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            SocketManager.GAME_SEND_SPELL_UPGRADE_FAILED(this);
        }
    }

    private void forgetSpell(String packet) {
        if (this.player.getExchangeAction() == null || this.player.getExchangeAction().getType() != ExchangeAction.FORGETTING_SPELL)
            return;
        int id = Integer.parseInt(packet.substring(2));
        if(id == -1)
            this.player.setExchangeAction(null);
        if (this.player.forgetSpell(id)) {
            SocketManager.GAME_SEND_SPELL_UPGRADE_SUCCESS(this, id, this.player.getSortStatBySortIfHas(id).getLevel());
            SocketManager.GAME_SEND_STATS_PACKET(this.player);
            this.player.setExchangeAction(null);
        }
    }

    private void removeSpell(String packet) {
        packet = packet.substring(2);

        // Can happen when client gets into a weird state
        if(packet.equals("undefined")) return;

        int position = Integer.parseInt(packet);
        this.player.removeSpellShortcutAtPosition(position);
        this.player.send("SR"+position);
    }

    private void moveSpell(String packet) {
        String[] parts = packet.substring(2).split("\\|");

        int spellID = Integer.parseInt(parts[0]);
        int position = -1;
        if(parts.length > 1) {
            position = Integer.parseInt(parts[1]); // May return -1
        }

        Spell.SortStats spellStats = this.player.getSortStatBySortIfHas(spellID);
        if (spellStats != null) {
            this.player.setSpellShortcuts(spellID, position);

            // After 1.41, we need to send back the SM packet
            this.player.send("SM"+spellID+"|"+position);
            // Before 1.41, we only send BN
            SocketManager.GAME_SEND_BN(this);
        }
    }

    /** Fin Spell Packet **/

    /**
     * Waypoint Packet *
     */
    private void parseWaypointPacket(String packet) {
        switch (packet.charAt(1)) {
            case 'U'://Use
                waypointUse(packet);
                break;
            case 'u'://use zaapi
                zaapiUse(packet);
                break;
            case 'p':
                prismUse(packet);
                break;
            case 'V'://Quitter
                waypointLeave();
                break;
            case 'v'://quitter zaapi
                zaapiLeave();
                break;
            case 'w':
                prismLeave();
                break;
        }
    }

    private void waypointUse(String packet) {
        try {
            final int id = Integer.parseInt(packet.substring(2));
            final Party party = this.player.getParty();

            if(party != null && this.player.getFight() == null && party.getMaster() != null && party.getMaster().getName().equals(this.player.getName())) {
                party.getPlayers().stream().filter((follower1) -> party.isWithTheMaster(follower1, false, false) && follower1.getExchangeAction() == null).forEach(follower -> {
                    follower.setExchangeAction(new ExchangeAction<>(ExchangeAction.IN_ZAAPING, null));
                    follower.useZaap(id);
                });
            }

            this.player.useZaap(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void zaapiUse(final String packet) {
        if (this.player.getDeshonor() >= 2) {
            SocketManager.GAME_SEND_Im_PACKET(this.player, "183");
            return;
        }
        final Party party = this.player.getParty();

        if(party != null && this.player.getFight() == null && party.getMaster() != null && party.getMaster().getName().equals(this.player.getName())) {
            party.getPlayers().stream().filter((follower1) -> party.isWithTheMaster(follower1, false, false) && follower1.getExchangeAction() == null).forEach(follower -> {
                follower.setExchangeAction(new ExchangeAction<>(ExchangeAction.IN_ZAPPI, null));
                follower.getGameClient().zaapiUse(packet);
            });
        }

        this.player.Zaapi_use(packet);
    }

    private void prismUse(String packet) {
        if (this.player.getDeshonor() >= 2) {
            SocketManager.GAME_SEND_Im_PACKET(this.player, "183");
            return;
        }
        this.player.usePrisme(packet);
    }

    private void waypointLeave() {
        this.player.stopZaaping();
    }

    private void zaapiLeave() {
        this.player.Zaapi_close();
    }

    private void prismLeave() {
        this.player.Prisme_close();
    }

    /** Fin Waypoint Packet **/

    /**
     * Other *
     */
    private void parseTutorialsPacket(String packet) {
        if(this.player.getExchangeAction() == null || this.player.getExchangeAction().getType() != ExchangeAction.IN_SCENARIO)
            return;
        String[] param = packet.split("\\|");
        ScenarioActionData sad = (ScenarioActionData) this.player.getExchangeAction().getValue();

        if(packet.charAt(1) != 'V') {
            return;
        }
        boolean succeed = packet.charAt(2) == '1';

        // Move player to expected cell (FIXME can probably be used to teleport)
        this.player.set_orientation(Byte.parseByte(param[2]));
        this.player.getCurCell().removePlayer(this.player);
        GameCase cell = this.player.getCurMap().getCase(Short.parseShort(param[1]));
        cell.addPlayer(player);
        this.player.setCurCell(cell);

        sad.onCompletion(player, succeed);
    }

    /**
     * Fin Other *
     */

    public void kick() {
        if(this.session.isConnected())
            this.session.close(true);
    }

    public void disconnect() {
        if (this.account != null && this.player != null)
            this.account.disconnect(this.player);
    }

    public void addAction(GameAction GA) {
        actions.put(GA.id, GA);
        if (GA.actionId == 1)
            walk = true;

        if (Config.debug) {
            World.world.logger.error("Game > Create action id : " + GA.id);
            World.world.logger.error("Game > Packet : " + GA.packet);
        }
    }

    public synchronized void removeAction(GameAction GA) {
        if (GA.actionId == 1)
            walk = false;
        if (Config.debug)
            World.world.logger.debug("Game >  Delete action id : " + GA.id);
        actions.remove(GA.id);

        if (actions.get(-1) != null && GA.actionId == 1)//Si la queue est pas vide
        {
            //et l'actionID remove = Deplacement
            //int cellID = -1;
            String packet = actions.get(-1).packet.substring(5);
            int cell = Integer.parseInt(packet.split(";")[0]);
            ArrayList<Integer> list = null;
            try {
                list = PathFinding.getAllCaseIdAllDirrection(cell, this.player.getCurMap());
                //cellID = Pathfinding.getNearestCellAroundGA(this.player.getCurMap(), cell, this.player.getCurCell().getId(), null);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //cellID == this.player.getCurCell().getId()
            if ((list != null && list.contains(this.player.getCurCell().getId())) || distPecheur())// et on verrifie si le joueur = cellI
                this.player.getGameClient().gameAction(actions.get(-1));// On renvois comme demande
                //Risqu? mais bon pas le choix si on veut pas ?tre emmerder avec les bl?s. Parser le bon type ?
                //this.player.getGameClient().gameAction(actions.getWaitingAccount(-1));// On renvois comme demande
            actions.remove(-1);
        }
    }

    private boolean distPecheur() {
        try {
            String packet = actions.get(-1).packet.substring(5);
            JobStat SM = this.player.getMetierBySkill(Integer.parseInt(packet.split(";")[1]));
            if (SM == null)
                return false;
            if (SM.getTemplate() == null)
                return false;
            if (SM.getTemplate().getId() != 36)
                return false;
            int dis = PathFinding.getDistanceBetween(this.player.getCurMap(), Integer.parseInt(packet.split(";")[0]), this.player.getCurCell().getId());
            int dist = JobConstant.getDistCanne(this.player.getObjetByPos(Constant.ITEM_POS_ARME).getTemplate().getId());
            if (dis <= dist)
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public void changeName(String packet) {
        if(!this.player.hasItemTemplate(10860, 1, false)) {
            this.player.send("AlEr");
            this.player.sendMessage(this.player.getLang().trans("game.gameclient.changename.none.potion"));
            return;
        }

        final String name = packet;
        boolean isValid = true;

        if (name.length() > 20 || name.length() < 3 || name.contains("modo") || name.contains("admin") || name.contains("putain") || name.contains("administrateur") || name.contains("puta"))
            isValid = false;
        if (isValid) {
            int tiretCount = 0;
            char exLetterA = ' ';
            char exLetterB = ' ';
            for (char curLetter : name.toCharArray()) {
                if (!(((curLetter >= 'a' && curLetter <= 'z') || (curLetter >= 'A' && curLetter <= 'Z')) || curLetter == '-')) {
                    isValid = false;
                    break;
                }
                if (curLetter == exLetterA && curLetter == exLetterB) {
                    isValid = false;
                    break;
                }
                if (curLetter >= 'a' && curLetter <= 'z') {
                    exLetterA = exLetterB;
                    exLetterB = curLetter;
                }
                if (curLetter == '-') {
                    if (tiretCount >= 1) {
                        isValid = false;
                        break;
                    } else {
                        tiretCount++;
                    }
                }
            }
        }

        if(DatabaseManager.get(PlayerData.class).exist(name) || !isValid) {
            this.player.send("AlEs");
            return;
        }

        this.player.setName(name);
        this.player.send("AlEr");
        this.player.removeItemByTemplateId(10860, 1, false);
        SocketManager.GAME_SEND_ALTER_GM_PACKET(this.player.getCurMap(), this.player);
    }

    public void send(String packet) {
        try {
            if (Config.encryption && this.preparedKeys != null)
                packet = World.world.getCryptManager().cryptMessage(packet, this.preparedKeys);
            this.getSession().write(packet);
        } catch(Exception e) {
            Logging.getInstance().write("Error", "Send fail : " + packet);
            e.printStackTrace();
        }
    }
    
    public void send(AbstractDofusMessage abstractDofusMessage) {
        if(getSession() != null && !getSession().isClosing() && getSession().isConnected()) {
            abstractDofusMessage.serialize();
            LoggerFactory.getLogger(GameClient.class).debug("Send : " + abstractDofusMessage.toString());
            send(abstractDofusMessage.getOutput().toString());
        }
    }
}
