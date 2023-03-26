package org.starloco.locos.client;

import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.area.map.entity.House;
import org.starloco.locos.area.map.entity.InteractiveObject;
import org.starloco.locos.area.map.entity.MountPark;
import org.starloco.locos.client.other.*;
import org.starloco.locos.command.administration.Group;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.game.SaleOffer;
import org.starloco.locos.database.data.login.PlayerData;
import org.starloco.locos.entity.monster.MonsterGroup;
import org.starloco.locos.entity.mount.Mount;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.spells.Spell;
import org.starloco.locos.fight.spells.SpellEffect;
import org.starloco.locos.game.GameClient;
import org.starloco.locos.game.action.ExchangeAction;
import org.starloco.locos.game.action.GameAction;
import org.starloco.locos.game.world.World;
import org.starloco.locos.guild.Guild;
import org.starloco.locos.guild.GuildMember;
import org.starloco.locos.job.Job;
import org.starloco.locos.job.JobStat;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.lang.LangEnum;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.object.ItemHash;
import org.starloco.locos.object.ObjectTemplate;
import org.starloco.locos.other.Action;
import org.starloco.locos.quest.Quest;
import org.starloco.locos.quest.QuestPlayer;
import org.starloco.locos.script.proxy.SPlayer;
import org.starloco.locos.util.Pair;
import org.starloco.locos.util.TimerWaiter;

import java.util.*;
import java.util.concurrent.TimeUnit;

public interface Player {
    static BasePlayer create(String name, int sexe, int classe, int color1, int color2, int color3, Account compte) {
        StringBuilder z = new StringBuilder();
        if (Config.allZaap) {
            for (Map.Entry<Integer, Integer> i : Constant.ZAAPS.entrySet()) {
                if (z.length() != 0)
                    z.append(",");
                z.append(i.getKey());
            }
        }
        if (classe > 12 || classe < 1)
            return null;
        if (sexe < 0 || sexe > 1)
            return null;
        BasePlayer player = new BasePlayer(-1, name, -1, sexe, classe, color1, color2, color3, Config.startKamas,
                ((Config.startLevel - 1)), ((Config.startLevel - 1) * 5), 10000, Config.startLevel,
                World.world.getPersoXpMin(Config.startLevel), 100, Integer.parseInt(classe
                + "" + sexe), (byte) 0, compte.getId(), new HashMap<>(), (byte) 1, (byte) 0, (byte) 0, "*#%!pi$:?",
                (Config.startMap > 0 ? (short) Config.startMap : Constant.getStartMap(classe)),
                (Config.startCell > 0 ? (short) Config.startCell : Constant.getStartCell(classe)),
                //(short)6824,
                //224,
                "", "", 100, Constant.getStartSorts(classe), Constant.getStartSortsPlaces(classe), (Config.startMap > 0 ? (short) Config.startMap : Constant.getStartMap(classe))
                + ","
                + (Config.startCell > 0 ? (short) Config.startCell : Constant.getStartCell(classe)), "", 0, -1, 0, 0, 0, z.toString(), (byte) 0, 0, -1, "", (Config.allEmotes ? "0;1;2;3;4;5;6;7;8;9;10;11;12;13;14;15;16;17;18;19;20;21" : "0"), 0, true, "118,0;119,0;123,0;124,0;125,0;126,0", 0, false, "0,0,0,0", (byte) 0, 0);

        player.getEmotes().add(0);
        player.getEmotes().add(1);
        for (int a = 1; a <= player.getLevel(); a++)
            Constant.onLevelUpSpells(player, a);

        if (!((PlayerData) DatabaseManager.get(PlayerData.class)).insert(player))
            return null;
        if (Config.startMap != 0)
            player.setSavePos(7411, 311);

        SocketManager.GAME_SEND_WELCOME(player);
        World.world.sendMessageToAll("client.player.onjoingame.welcome", player.getName());

        World.world.addPlayer(player);

        TimerWaiter.addNext(() -> {
            for (ObjectTemplate template : World.world.getItemSet(5).getItemTemplates()) {
                if (template != null) {
                    GameObject object = template.createNewItem(1, false);
                    if (object != null) {
                        if (player.addItem(object, true, false))
                            World.world.addGameObject(object);
                        player.send("Im021;1~" + template.getId());
                    }
                }
            }
        }, 5, TimeUnit.SECONDS);
        return player;
    }

    static String getCompiledEmote(List<Integer> i) {
        int i2 = 0;
        for (Integer b : i) i2 += (2 << (b - 2));
        return i2 + "|0";
    }

    //CLONAGE
    static BasePlayer ClonePerso(BasePlayer P, int id, int pdv) {
        HashMap<Integer, Integer> stats = new HashMap<>();
        stats.put(Constant.STATS_ADD_VITA, pdv);
        stats.put(Constant.STATS_ADD_FORC, P.getStats().getEffect(Constant.STATS_ADD_FORC));
        stats.put(Constant.STATS_ADD_SAGE, P.getStats().getEffect(Constant.STATS_ADD_SAGE));
        stats.put(Constant.STATS_ADD_INTE, P.getStats().getEffect(Constant.STATS_ADD_INTE));
        stats.put(Constant.STATS_ADD_CHAN, P.getStats().getEffect(Constant.STATS_ADD_CHAN));
        stats.put(Constant.STATS_ADD_AGIL, P.getStats().getEffect(Constant.STATS_ADD_AGIL));
        stats.put(Constant.STATS_ADD_PA, P.getStats().getEffect(Constant.STATS_ADD_PA));
        stats.put(Constant.STATS_ADD_PM, P.getStats().getEffect(Constant.STATS_ADD_PM));
        stats.put(Constant.STATS_ADD_RP_NEU, P.getStats().getEffect(Constant.STATS_ADD_RP_NEU));
        stats.put(Constant.STATS_ADD_RP_TER, P.getStats().getEffect(Constant.STATS_ADD_RP_TER));
        stats.put(Constant.STATS_ADD_RP_FEU, P.getStats().getEffect(Constant.STATS_ADD_RP_FEU));
        stats.put(Constant.STATS_ADD_RP_EAU, P.getStats().getEffect(Constant.STATS_ADD_RP_EAU));
        stats.put(Constant.STATS_ADD_RP_AIR, P.getStats().getEffect(Constant.STATS_ADD_RP_AIR));
        stats.put(Constant.STATS_ADD_AFLEE, P.getStats().getEffect(Constant.STATS_ADD_AFLEE));
        stats.put(Constant.STATS_ADD_MFLEE, P.getStats().getEffect(Constant.STATS_ADD_MFLEE));

        byte showWings = 0;
        int alvl = 0;
        if (P.getAlignment() != 0 && P.is_showWings()) {
            showWings = 1;
            alvl = P.getGrade();
        }
        int mountID = -1;
        if (P.getMount() != null) {
            mountID = P.getMount().getId();
        }

        BasePlayer Clone = new BasePlayer(id, P.getName(), (P.getGroupe() != null) ? P.getGroupe().getId() : -1, P.getSexe(), P.getClasse(), P.getColor1(), P.getColor2(), P.getColor3(), P.getLevel(), 100, P.getGfxId(), stats, "", 100, showWings, mountID, alvl, P.getAlignment());
        Clone.getItems().putAll(P.getItems());
        Clone.set_isClone(true);
        if (P.isOnMount()) {
            Clone.toogleOnMount();
        }
        return Clone;
    }

    ArrayList<Integer> getIsCraftingType();

    void parseObjects(String stuff);

    int getId();

    void setId(int id);

    String getName();

    void setName(String name);

    void setColors(int i, int i1, int i2);

    Group getGroupe();

    void setGroupe(Group groupe, boolean reload);

    boolean isInvisible();

    void setInvisible(boolean b);

    int getSexe();

    void setSexe(int sexe);

    int getClasse();

    void setClasse(int classe);

    int getColor1();

    int getColor2();

    int getColor3();

    int getLevel();

    void setLevel(int level);

    int getEnergy();

    void setEnergy(int energy);

    long getExp();

    void setExp(long exp);

    int getCurPdv();

    void setPdv(int pdv);

    int getMaxPdv();

    void setMaxPdv(int maxPdv);

    Stats getStats();

    Stats getStatsParcho();

    String parseStatsParcho();

    boolean getDoAction();

    void setDoAction(boolean b);

    void setRoleplayBuff(int id);

    void setBenediction(int id);

    void setMalediction(int id);

    void setMascotte(int id);

    void setCandy(int id);

    void calculTurnCandy();

    List<Spell.SortStats> getSpells();

    Map<Integer, Integer> getSpellPositions();

    boolean isSpec();

    void setSpec(boolean s);

    String getAllTitle();

    void setAllTitle(String title);

    void teleportOldMap();

    void setCurrentPositionToOldPosition();

    void setOldPosition();

    void setOnline(boolean isOnline);

    boolean isOnline();

    Party getParty();

    void setParty(Party party);

    String encodeSpellsToDB();

    void parseSpells(String str, boolean send);

    Pair<Integer, Integer> getSavePosition();

    void setSavePos(int mapID, int cellID);

    long getKamas();

    void setKamas(long l);

    Map<Integer, SpellEffect> get_buff();

    Account getAccount();

    void setAccount(Account c);

    int getSpellPoints(boolean save);

    void setSpellPoints(int spellPoints);

    Guild getGuild();

    void setChangeName(boolean changeName);

    boolean isChangeName();

    boolean isReady();

    void setReady(boolean ready);

    int getDuelId();

    void setDuelId(int _duelid);

    Fight getFight();

    void setFight(Fight fight);

    boolean is_showFriendConnection();

    boolean is_showWings();

    boolean isShowSeller();

    void setShowSeller(boolean is);

    String get_canaux();

    GameCase getCurCell();

    void setCurCell(GameCase cell);

    int get_size();

    void set_size(int _size);

    int getGfxId();

    void setGfxId(int _gfxid);

    boolean isMorphMercenaire();

    GameMap getCurMap();

    void setCurMap(GameMap curMap);

    boolean isAway();

    void setAway(boolean away);

    boolean isSitted();

    boolean setSitted(boolean sitted);

    int getCapital();

    // returns Couple<ptsDelta,worked> Worked can only be false when spell/level doesn't exist, or modPoints is true.
    EnsureSpellLevelResult ensureSpellLevelSilent(int spell, int newLevel, boolean modPoints);

    boolean ensureSpellLevel(int spell, int level, boolean modPoints, boolean silent);

    void learnSpell(int spell, int level, int pos);

    boolean learnSpell(int spellID, int level, boolean save, boolean send, boolean learn);

    boolean unlearnSpell(int spell);

    boolean unlearnSpell(BasePlayer perso, int spellID, int level,
                         int ancLevel, boolean save, boolean send);

    boolean boostSpell(int spellID);

    void boostSpellIncarnation();

    boolean forgetSpell(int spellID);

    MorphMode getMorphMode();

    void transform(int id, boolean isLoad, boolean join);

    void transform(MorphMode morphMode, boolean isLoad, boolean join);

    boolean isMorph();

    void unsetMorph();

    void unsetFullMorph();

    String encodeSpellListForSL();

    void setSpellShortcuts(int spellId, int position);

    Spell.SortStats getSortStatBySortIfHas(int spellID);

    String parseALK();

    void remove();

    void OnJoinGame();

    void checkVote();

    void SetSeeFriendOnline(boolean bool);

    void sendGameCreate();

    String parseToOa();

    String parseToGM();

    String parseToMerchant();

    String getGMStuffString();

    String getAsPacket();

    int getGrade();

    String xpString(String c);

    int emoteActive();

    void setEmoteActive(int emoteActive);

    Stats getStuffStats();

    Stats getBuffsStats();

    int get_orientation();

    void set_orientation(int _orientation);

    int getInitiative();

    Stats getTotalStats(boolean lessBuff);

    Stats getDonsStats();

    int getPodUsed();

    int getMaxPod();

    void refreshLife(boolean refresh);

    byte getAlignment();

    void setAlignment(byte alignment);

    int get_pdvper();

    void useSmiley(String str);

    void boostStat(int stat, boolean capital);

    void boostStatFixedCount(int stat, int countVal);

    boolean isMuted();

    String parseObjetsToDB();

    void addItem(int templateId, int quantity, boolean useMax, boolean display);

    void addItem(ObjectTemplate template, int quantity, boolean useMax, boolean display);

    boolean addItem(GameObject newItem, boolean stack, boolean display);

    void addItem(GameObject item, boolean display);

    boolean addObjetSimiler(GameObject objet, boolean hasSimiler, int oldID);

    Map<Integer, GameObject> getItems();

    String encodeItemASK();

    String getItemsIDSplitByChar(String splitter);

    String getStoreItemsIDSplitByChar(String splitter);

    boolean hasItemGuid(int guid);

    void sellItem(int guid, int qua);

    void removeItem(int guid);

    void removeItem(int guid, int nombre, boolean send, boolean deleteFromWorld);

    void deleteItem(int guid);

    GameObject getObjetByPos(int pos);

    //TODO: Delete s'te fonction.
    GameObject getObjetByPos2(int pos);

    void refreshStats();

    boolean levelUp(boolean send, boolean addXp);

    boolean addXp(long winxp);

    boolean levelUpIncarnations(boolean send, boolean addXp);

    boolean addXpIncarnations(long winxp);

    boolean addKamas(long l);

    boolean modKamasDisplay(long quantity);

    GameObject getSimilarItem(GameObject exGameObject);

    int learnJob(Job m);

    void unlearnJob(int m);

    void unequipedObjet(GameObject o);

    void checkWeaponAndShieldConditions();

    boolean hasEquiped(int id);

    int getInvitation();

    void setInvitation(int target);

    String parseToPM();

    int getNumbEquipedItemOfPanoplie(int panID);

    void startActionOnCell(GameAction GA);

    void finishActionOnCell(GameAction GA);

    void teleportD(int newMapID, int newCellID);

    void teleportLaby(short newMapID, int newCellID);

    void teleport(Pair<Integer, Integer> posIDs);

    void teleport(int newMapID, int newCellID);

    void teleport(GameMap map, int cell);

    void disconnectInFight();

    int getBankCost();

    void openBank();

    String getStringVar(String str);

    void refreshMapAfterFight();

    long getBankKamas();

    void setBankKamas(long i);

    String parseBankPacket();

    void addCapital(int pts);

    void addSpellPoint(int spellPoints);

    void addInBank(int guid, int qua, boolean outside);

    void removeFromBank(int guid, int qua);

    void openMountPark(MountPark target);

    void fullPDV();

    void warpToSavePos();

    boolean removeItemByTemplateId(int templateId, int count, boolean display);

    ArrayList<Job> getJobs();

    Map<Integer, JobStat> getMetiers();

    void doJobAction(int actionID, InteractiveObject object, GameAction GA, GameCase cell);

    void finishJobAction(int actionID, InteractiveObject object, GameAction GA, GameCase cell);

    String parseJobData();

    int totalJobBasic();

    int totalJobFM();

    boolean canAggro();

    void setCanAggro(boolean canAggro);

    JobStat getMetierBySkill(int skID);

    String parseToFriendList(int guid);

    String parseToEnemyList(int guid);

    JobStat getMetierByID(int job);

    boolean isOnMount();

    void setOnMount(boolean onMount);

    void toogleOnMount();

    int getMountXpGive();

    Mount getMount();

    void setMount(Mount DD);

    void setMountGiveXp(int parseInt);

    void resetVars();

    void addChanel(String chan);

    void removeChanel(String chan);

    void modifAlignement(int i);

    int getDeshonor();

    void setDeshonor(int deshonor);

    void setShowWings(boolean showWings);

    int get_honor();

    void set_honor(int honor);

    int getALvl();

    void setALvl(int a);

    void toggleWings(char type);

    void addHonor(int winH);

    void remHonor(int losePH);

    GuildMember getGuildMember();

    void setGuildMember(GuildMember _guild);

    int getAccID();

    String parseZaapList()//Pour le packet WC
    ;

    String parsePrismesList();

    void openZaapMenu();

    void verifAndAddZaap(int mapId);

    boolean verifOtomaiZaap();

    void openPrismeMenu();

    void useZaap(int id);

    void usePrisme(String packet);

    String parseZaaps();

    String parsePrism();

    void stopZaaping();

    void Zaapi_close();

    void Prisme_close();

    void Zaapi_use(String packet);

    boolean hasItemTemplate(int i, int q, boolean equipped);

    boolean hasItemType(int type);

    GameObject getItemTemplate(int i, int q);

    GameObject getItemTemplate(int i);

    int getNbItemTemplate(int i);

    boolean isDispo(BasePlayer sender);

    boolean get_isClone();

    void set_isClone(boolean isClone);

    byte getCurrentTitle();

    void setCurrentTitle(int i);

    //FIN CLONAGE
    void VerifAndChangeItemPlace();

    Stalk getStalk();

    void setStalk(Stalk traq);

    void setWife(int id);

    String get_wife_friendlist();

    String parse_towife();

    void meetWife(BasePlayer p)// Se teleporter selon les sacro-saintes autorisations du mariage.
    ;

    void Divorce();

    int getWife();

    int setisOK(int ok);

    int getisOK();

    List<GameObject> getEquippedObjects();

    void changeOrientation(int toOrientation);

    byte isDead();

    void setDead(byte dead);

    short getDeadLevel();

    byte getDeathCount();

    void increaseTotalKills();

    long getTotalKills();

    String getDeathInformation();

    void die(byte type, long id);

    void revive();

    boolean isGhost();

    void setGhost(boolean ghost);

    void setFuneral();

    void setGhost();

    void setAlive();

    Map<Integer, Integer> getStoreItems();

    int needEndFight();

    MonsterGroup hasMobGroup();

    void setNeededEndFight(int hasEndFight, MonsterGroup group);

    void setNeededEndFightAction(Action endFightAction);

    boolean castEndFightAction();

    String parseStoreItemsList();

    int parseStoreItemsListPods();

    String parseStoreItemstoBD();

    void addInStore(int ObjID, int price, int qua);

    void removeFromStore(int guid, int qua);

    void removeStoreItem(int guid);

    void addStoreItem(int guid, int price);

    int getSpeed();

    void setSpeed(int _Speed);

    boolean getMetierPublic();

    void setMetierPublic(boolean b);

    boolean getLivreArtisant();

    void setLivreArtisant(boolean b);

    boolean hasSpell(int spellID);

    void leaveEnnemyFaction();

    void leaveEnnemyFactionAndPay(BasePlayer perso);

    void leaveFaction();

    void teleportWithoutBlocked(int newMapID, int newCellID)//Aucune condition genre <<en_prison>> etc
    ;

    void teleportFaction(int factionEnnemy);

    String parsecolortomount();

    //region Objects class
    Map<Integer, World.Couple<Integer, Integer>> getObjectsClassSpell();

    void addObjectClassSpell(int spell, int effect, int value);

    void removeObjectClassSpell(int spell);

    void addObjectClass(int item);

    void removeObjectClass(int item);

    void refreshObjectsClass();

    int getValueOfClassObject(int spell, int effect);

    int storeAllBuy();

    void DialogTimer();

    long getTimeTaverne();

    void setTimeTaverne(long timeTaverne);

    GameAction getGameAction();

    void setGameAction(GameAction Action);

    int getAlignMap();

    List<Integer> getEmotes();

    boolean addStaticEmote(int emote);

    String parseEmoteToDB();

    boolean getBlockMovement();

    void setBlockMovement(boolean b);

    GameClient getGameClient();

    void send(String packet);

    void sendMessage(String msg);

    void sendTypeMessage(String name, String msg);

    void sendServerMessage(String msg);

    boolean isSubscribe();

    boolean isMissingSubscription();

    boolean cantDefie();

    boolean cantAgro();

    boolean cantCanal();

    boolean cantTP();

    boolean isInPrison();

    void addQuestPerso(QuestPlayer qPerso);

    void delQuestPerso(int key);

    Map<Integer, QuestPlayer> getQuestPerso();

    QuestPlayer getQuestPersoByQuest(Quest quest);

    QuestPlayer getQuestPersoByQuestId(int id);

    String encodeQL();

    House getInHouse();

    void setInHouse(House h);

    ExchangeAction<?> getExchangeAction();

    void setExchangeAction(ExchangeAction<?> exchangeAction);

    void refreshCraftSecure(boolean unequip);

    void setFullMorphbouf(int team);

    void unsetFullMorphbouf();

    LangEnum getLang();

    SPlayer scripted();

    boolean consumeCurrency(SaleOffer.Currency cur, long qua);

    boolean moveItemShortcutSend(int oldPos, int newPos);

    boolean addItemShortcutSend(int position, int itemID);

    boolean removeItemShortcutSend(int position);

    Optional<Integer> removeItemShortcutByHash(ItemHash hash);

    void sendItemShortcuts();
}
