package org.starloco.locos.client;

import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.area.map.entity.House;
import org.starloco.locos.area.map.entity.InteractiveObject;
import org.starloco.locos.area.map.entity.MountPark;
import org.starloco.locos.client.other.*;
import org.starloco.locos.command.administration.Group;
import org.starloco.locos.database.data.game.SaleOffer;
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
import org.starloco.locos.lang.LangEnum;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.object.ItemHash;
import org.starloco.locos.object.ObjectTemplate;
import org.starloco.locos.other.Action;
import org.starloco.locos.quest.Quest;
import org.starloco.locos.quest.QuestPlayer;
import org.starloco.locos.script.proxy.SPlayer;
import org.starloco.locos.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MorphedPlayer implements Player {

    private final BasePlayer base;
    private final MorphMode morphMode;

    public MorphedPlayer(BasePlayer base, MorphMode morphMode) {
        this.base = base;
        this.morphMode = morphMode;
    }

    public int getGfxId() { return morphMode.getGfxId(); }

    public List<Spell.SortStats> getSpells() { return new ArrayList<>(morphMode.getSpells().values()); }

    @Override
    public Map<Integer, Integer> getSpellPositions() {
        return morphMode.getSpellPositions();
    }

    public void parseObjects(String stuff) { base.parseObjects(stuff); }
    public void setId(int id) { base.setId(id); }
    public void setName(String name) { base.setName(name); }
    public void setColors(int i, int i1, int i2) { base.setColors(i, i1, i2); }
    public void setGroupe(Group groupe, boolean reload) { base.setGroupe(groupe, reload); }
    public void setInvisible(boolean b) { base.setInvisible(b); }
    public void setSexe(int sexe) { base.setSexe(sexe); }
    public void setClasse(int classe) { base.setClasse(classe); }
    public void setLevel(int level) { base.setLevel(level); }
    public void setEnergy(int energy) { base.setEnergy(energy); }
    public void setExp(long exp) { base.setExp(exp); }
    public void setPdv(int pdv) { base.setPdv(pdv); }
    public void setMaxPdv(int maxPdv) { base.setMaxPdv(maxPdv); }
    public void setDoAction(boolean b) { base.setDoAction(b); }
    public void setRoleplayBuff(int id) { base.setRoleplayBuff(id); }
    public void setBenediction(int id) { base.setBenediction(id); }
    public void setMalediction(int id) { base.setMalediction(id); }
    public void setMascotte(int id) { base.setMascotte(id); }
    public void setCandy(int id) { base.setCandy(id); }
    public void calculTurnCandy() { base.calculTurnCandy(); }
    public void setSpec(boolean s) { base.setSpec(s); }
    public void setAllTitle(String title) { base.setAllTitle(title); }
    public void teleportOldMap() { base.teleportOldMap(); }
    public void setCurrentPositionToOldPosition() { base.setCurrentPositionToOldPosition(); }
    public void setOldPosition() { base.setOldPosition(); }
    public void setOnline(boolean isOnline) { base.setOnline(isOnline); }
    public void setParty(Party party) { base.setParty(party); }
    public void parseSpells(String str, boolean send) { base.parseSpells(str, send); }
    public void setSavePos(int mapID, int cellID) { base.setSavePos(mapID, cellID); }
    public void setKamas(long l) { base.setKamas(l); }
    public void setAccount(Account c) { base.setAccount(c); }
    public void setSpellPoints(int spellPoints) { base.setSpellPoints(spellPoints); }
    public void setChangeName(boolean changeName) { base.setChangeName(changeName); }
    public void setReady(boolean ready) { base.setReady(ready); }
    public void setDuelId(int _duelid) { base.setDuelId(_duelid); }
    public void setFight(Fight fight) { base.setFight(fight); }
    public void setShowSeller(boolean is) { base.setShowSeller(is); }
    public void setCurCell(GameCase cell) { base.setCurCell(cell); }
    public void set_size(int _size) { base.set_size(_size); }
    public void setGfxId(int _gfxid) { base.setGfxId(_gfxid); }
    public void setCurMap(GameMap curMap) { base.setCurMap(curMap); }
    public void setAway(boolean away) { base.setAway(away); }
    public void learnSpell(int spell, int level, int pos) { base.learnSpell(spell, level, pos); }
    public void boostSpellIncarnation() { base.boostSpellIncarnation(); }
    public void transform(int id, boolean isLoad, boolean join) { base.transform(id, isLoad, join); }
    public void transform(MorphMode morphMode, boolean isLoad, boolean join) { base.transform(morphMode, isLoad, join); }
    public void unsetMorph() { base.unsetMorph(); }
    public void unsetFullMorph() { base.unsetFullMorph(); }
    public void setSpellShortcuts(int spellId, int position) { base.setSpellShortcuts(spellId, position); }
    public void remove() { base.remove(); }
    public void OnJoinGame() { base.OnJoinGame(); }
    public void checkVote() { base.checkVote(); }
    public void SetSeeFriendOnline(boolean bool) { base.SetSeeFriendOnline(bool); }
    public void sendGameCreate() { base.sendGameCreate(); }
    public void setEmoteActive(int emoteActive) { base.setEmoteActive(emoteActive); }
    public void set_orientation(int _orientation) { base.set_orientation(_orientation); }
    public void refreshLife(boolean refresh) { base.refreshLife(refresh); }
    public void setAlignment(byte alignment) { base.setAlignment(alignment); }
    public void useSmiley(String str) { base.useSmiley(str); }
    public void boostStat(int stat, boolean capital) { base.boostStat(stat, capital); }
    public void boostStatFixedCount(int stat, int countVal) { base.boostStatFixedCount(stat, countVal); }
    public void addItem(int templateId, int quantity, boolean useMax, boolean display) { base.addItem(templateId, quantity, useMax, display); }
    public void addItem(ObjectTemplate template, int quantity, boolean useMax, boolean display) { base.addItem(template, quantity, useMax, display); }
    public void addItem(GameObject item, boolean display) { base.addItem(item, display); }
    public void sellItem(int guid, int qua) { base.sellItem(guid, qua); }
    public void removeItem(int guid) { base.removeItem(guid); }
    public void removeItem(int guid, int nombre, boolean send, boolean deleteFromWorld) { base.removeItem(guid, nombre, send, deleteFromWorld); }
    public void deleteItem(int guid) { base.deleteItem(guid); }
    public void refreshStats() { base.refreshStats(); }
    public void unlearnJob(int m) { base.unlearnJob(m); }
    public void unequipedObjet(GameObject o) { base.unequipedObjet(o); }
    public void checkWeaponAndShieldConditions() { base.checkWeaponAndShieldConditions(); }
    public void setInvitation(int target) { base.setInvitation(target); }
    public void startActionOnCell(GameAction GA) { base.startActionOnCell(GA); }
    public void finishActionOnCell(GameAction GA) { base.finishActionOnCell(GA); }
    public void teleportD(int newMapID, int newCellID) { base.teleportD(newMapID, newCellID); }
    public void teleportLaby(short newMapID, int newCellID) { base.teleportLaby(newMapID, newCellID); }
    public void teleport(Pair<Integer, Integer> posIDs) { base.teleport(posIDs); }
    public void teleport(int newMapID, int newCellID) { base.teleport(newMapID, newCellID); }
    public void teleport(GameMap map, int cell) { base.teleport(map, cell); }
    public void disconnectInFight() { base.disconnectInFight(); }
    public void openBank() { base.openBank(); }
    public void refreshMapAfterFight() { base.refreshMapAfterFight(); }
    public void setBankKamas(long i) { base.setBankKamas(i); }
    public void addCapital(int pts) { base.addCapital(pts); }
    public void addSpellPoint(int spellPoints) { base.addSpellPoint(spellPoints); }
    public void addInBank(int guid, int qua, boolean outside) { base.addInBank(guid, qua, outside); }
    public void removeFromBank(int guid, int qua) { base.removeFromBank(guid, qua); }
    public void openMountPark(MountPark target) { base.openMountPark(target); }
    public void fullPDV() { base.fullPDV(); }
    public void warpToSavePos() { base.warpToSavePos(); }
    public void doJobAction(int actionID, InteractiveObject object, GameAction GA, GameCase cell) { base.doJobAction(actionID, object, GA, cell); }
    public void finishJobAction(int actionID, InteractiveObject object, GameAction GA, GameCase cell) { base.finishJobAction(actionID, object, GA, cell); }
    public void setCanAggro(boolean canAggro) { base.setCanAggro(canAggro); }
    public void setOnMount(boolean onMount) { base.setOnMount(onMount); }
    public void toogleOnMount() { base.toogleOnMount(); }
    public void setMount(Mount DD) { base.setMount(DD); }
    public void setMountGiveXp(int parseInt) { base.setMountGiveXp(parseInt); }
    public void resetVars() { base.resetVars(); }
    public void addChanel(String chan) { base.addChanel(chan); }
    public void removeChanel(String chan) { base.removeChanel(chan); }
    public void modifAlignement(int i) { base.modifAlignement(i); }
    public void setDeshonor(int deshonor) { base.setDeshonor(deshonor); }
    public void setShowWings(boolean showWings) { base.setShowWings(showWings); }
    public void set_honor(int honor) { base.set_honor(honor); }
    public void setALvl(int a) { base.setALvl(a); }
    public void toggleWings(char type) { base.toggleWings(type); }
    public void addHonor(int winH) { base.addHonor(winH); }
    public void remHonor(int losePH) { base.remHonor(losePH); }
    public void setGuildMember(GuildMember _guild) { base.setGuildMember(_guild); }
    public void openZaapMenu() { base.openZaapMenu(); }
    public void verifAndAddZaap(int mapId) { base.verifAndAddZaap(mapId); }
    public void openPrismeMenu() { base.openPrismeMenu(); }
    public void useZaap(int id) { base.useZaap(id); }
    public void usePrisme(String packet) { base.usePrisme(packet); }
    public void stopZaaping() { base.stopZaaping(); }
    public void Zaapi_close() { base.Zaapi_close(); }
    public void Prisme_close() { base.Prisme_close(); }
    public void Zaapi_use(String packet) { base.Zaapi_use(packet); }
    public void set_isClone(boolean isClone) { base.set_isClone(isClone); }
    public void setCurrentTitle(int i) { base.setCurrentTitle(i); }//FIN CLONAGE
    public void VerifAndChangeItemPlace() { base.VerifAndChangeItemPlace(); }
    public void setStalk(Stalk traq) { base.setStalk(traq); }
    public void setWife(int id) { base.setWife(id); }
    public void meetWife(BasePlayer p) { base.meetWife(p); }
    public void Divorce() { base.Divorce(); }
    public void changeOrientation(int toOrientation) { base.changeOrientation(toOrientation); }
    public void setDead(byte dead) { base.setDead(dead); }
    public void increaseTotalKills() { base.increaseTotalKills(); }
    public void die(byte type, long id) { base.die(type, id); }
    public void revive() { base.revive(); }
    public void setGhost(boolean ghost) { base.setGhost(ghost); }
    public void setFuneral() { base.setFuneral(); }
    public void setGhost() { base.setGhost(); }
    public void setAlive() { base.setAlive(); }
    public void setNeededEndFight(int hasEndFight, MonsterGroup group) { base.setNeededEndFight(hasEndFight, group); }
    public void setNeededEndFightAction(Action endFightAction) { base.setNeededEndFightAction(endFightAction); }
    public void addInStore(int ObjID, int price, int qua) { base.addInStore(ObjID, price, qua); }
    public void removeFromStore(int guid, int qua) { base.removeFromStore(guid, qua); }
    public void removeStoreItem(int guid) { base.removeStoreItem(guid); }
    public void addStoreItem(int guid, int price) { base.addStoreItem(guid, price); }
    public void setSpeed(int _Speed) { base.setSpeed(_Speed); }
    public void setMetierPublic(boolean b) { base.setMetierPublic(b); }
    public void setLivreArtisant(boolean b) { base.setLivreArtisant(b); }
    public void leaveEnnemyFaction() { base.leaveEnnemyFaction(); }
    public void leaveEnnemyFactionAndPay(BasePlayer perso) { base.leaveEnnemyFactionAndPay(perso); }
    public void leaveFaction() { base.leaveFaction(); }
    public void teleportWithoutBlocked(int newMapID, int newCellID) { base.teleportWithoutBlocked(newMapID, newCellID); }
    public void teleportFaction(int factionEnnemy) { base.teleportFaction(factionEnnemy); }
    public void addObjectClassSpell(int spell, int effect, int value) { base.addObjectClassSpell(spell, effect, value); }
    public void removeObjectClassSpell(int spell) { base.removeObjectClassSpell(spell); }
    public void addObjectClass(int item) { base.addObjectClass(item); }
    public void removeObjectClass(int item) { base.removeObjectClass(item); }
    public void refreshObjectsClass() { base.refreshObjectsClass(); }
    public void DialogTimer() { base.DialogTimer(); }
    public void setTimeTaverne(long timeTaverne) { base.setTimeTaverne(timeTaverne); }
    public void setGameAction(GameAction Action) { base.setGameAction(Action); }
    public void setBlockMovement(boolean b) { base.setBlockMovement(b); }
    public void send(String packet) { base.send(packet); }
    public void sendMessage(String msg) { base.sendMessage(msg); }
    public void sendTypeMessage(String name, String msg) { base.sendTypeMessage(name, msg); }
    public void sendServerMessage(String msg) { base.sendServerMessage(msg); }
    public void addQuestPerso(QuestPlayer qPerso) { base.addQuestPerso(qPerso); }
    public void delQuestPerso(int key) { base.delQuestPerso(key); }
    public void setInHouse(House h) { base.setInHouse(h); }
    public void setExchangeAction(ExchangeAction<?> exchangeAction) { base.setExchangeAction(exchangeAction); }
    public void refreshCraftSecure(boolean unequip) { base.refreshCraftSecure(unequip); }
    public void setFullMorphbouf(int team) { base.setFullMorphbouf(team); }
    public void unsetFullMorphbouf() { base.unsetFullMorphbouf(); }
    public void sendItemShortcuts() { base.sendItemShortcuts(); }

    public ArrayList<Integer> getIsCraftingType() { return base.getIsCraftingType(); }
    public int getId() { return base.getId(); }
    public String getName() { return base.getName(); }
    public Group getGroupe() { return base.getGroupe(); }
    public boolean isInvisible() { return base.isInvisible(); }
    public int getSexe() { return base.getSexe(); }
    public int getClasse() { return base.getClasse(); }
    public int getColor1() { return base.getColor1(); }
    public int getColor2() { return base.getColor2(); }
    public int getColor3() { return base.getColor3(); }
    public int getLevel() { return base.getLevel(); }
    public int getEnergy() { return base.getEnergy(); }
    public long getExp() { return base.getExp(); }
    public int getCurPdv() { return base.getCurPdv(); }
    public int getMaxPdv() { return base.getMaxPdv(); }
    public Stats getStats() { return base.getStats(); }
    public Stats getStatsParcho() { return base.getStatsParcho(); }
    public String parseStatsParcho() { return base.parseStatsParcho(); }
    public boolean getDoAction() { return base.getDoAction(); }
    public boolean isSpec() { return base.isSpec(); }
    public String getAllTitle() { return base.getAllTitle(); }
    public boolean isOnline() { return base.isOnline(); }
    public Party getParty() { return base.getParty(); }
    public String encodeSpellsToDB() { return base.encodeSpellsToDB(); }
    public Pair<Integer, Integer> getSavePosition() { return base.getSavePosition(); }
    public long getKamas() { return base.getKamas(); }
    public Map<Integer, SpellEffect> get_buff() { return base.get_buff(); }
    public Account getAccount() { return base.getAccount(); }
    public Guild getGuild() { return base.getGuild(); }
    public boolean isChangeName() { return base.isChangeName(); }
    public boolean isReady() { return base.isReady(); }
    public int getDuelId() { return base.getDuelId(); }
    public Fight getFight() { return base.getFight(); }
    public boolean is_showFriendConnection() { return base.is_showFriendConnection(); }
    public boolean is_showWings() { return base.is_showWings(); }
    public boolean isShowSeller() { return base.isShowSeller(); }
    public String get_canaux() { return base.get_canaux(); }
    public GameCase getCurCell() { return base.getCurCell(); }
    public int get_size() { return base.get_size(); }
    public boolean isMorphMercenaire() { return base.isMorphMercenaire(); }
    public GameMap getCurMap() { return base.getCurMap(); }
    public boolean isAway() { return base.isAway(); }
    public boolean isSitted() { return base.isSitted(); }
    public int getCapital() { return base.getCapital(); }
    public MorphMode getMorphMode() { return base.getMorphMode(); }
    public boolean isMorph() { return base.isMorph(); }
    public String encodeSpellListForSL() { return base.encodeSpellListForSL(); }
    public String parseALK() { return base.parseALK(); }
    public String parseToOa() { return base.parseToOa(); }
    public String parseToGM() { return base.parseToGM(); }
    public String parseToMerchant() { return base.parseToMerchant(); }
    public String getGMStuffString() { return base.getGMStuffString(); }
    public String getAsPacket() { return base.getAsPacket(); }
    public int getGrade() { return base.getGrade(); }
    public int emoteActive() { return base.emoteActive(); }
    public Stats getStuffStats() { return base.getStuffStats(); }
    public Stats getBuffsStats() { return base.getBuffsStats(); }
    public int get_orientation() { return base.get_orientation(); }
    public int getInitiative() { return base.getInitiative(); }
    public Stats getDonsStats() { return base.getDonsStats(); }
    public int getPodUsed() { return base.getPodUsed(); }
    public int getMaxPod() { return base.getMaxPod(); }
    public byte getAlignment() { return base.getAlignment(); }
    public int get_pdvper() { return base.get_pdvper(); }
    public boolean isMuted() { return base.isMuted(); }
    public String parseObjetsToDB() { return base.parseObjetsToDB(); }
    public Map<Integer, GameObject> getItems() { return base.getItems(); }
    public String encodeItemASK() { return base.encodeItemASK(); }
    public int getInvitation() { return base.getInvitation(); }
    public String parseToPM() { return base.parseToPM(); }
    public int getBankCost() { return base.getBankCost(); }
    public long getBankKamas() { return base.getBankKamas(); }
    public String parseBankPacket() { return base.parseBankPacket(); }
    public ArrayList<Job> getJobs() { return base.getJobs(); }
    public Map<Integer, JobStat> getMetiers() { return base.getMetiers(); }
    public String parseJobData() { return base.parseJobData(); }
    public int totalJobBasic() { return base.totalJobBasic(); }
    public int totalJobFM() { return base.totalJobFM(); }
    public boolean canAggro() { return base.canAggro(); }
    public boolean isOnMount() { return base.isOnMount(); }
    public int getMountXpGive() { return base.getMountXpGive(); }
    public Mount getMount() { return base.getMount(); }
    public int getDeshonor() { return base.getDeshonor(); }
    public int get_honor() { return base.get_honor(); }
    public int getALvl() { return base.getALvl(); }
    public GuildMember getGuildMember() { return base.getGuildMember(); }
    public int getAccID() { return base.getAccID(); }
    public String parseZaapList() { return base.parseZaapList(); }
    public String parsePrismesList() { return base.parsePrismesList(); }
    public boolean verifOtomaiZaap() { return base.verifOtomaiZaap(); }
    public String parseZaaps() { return base.parseZaaps(); }
    public String parsePrism() { return base.parsePrism(); }
    public boolean get_isClone() { return base.get_isClone(); }
    public byte getCurrentTitle() { return base.getCurrentTitle(); }
    public Stalk getStalk() { return base.getStalk(); }
    public String get_wife_friendlist() { return base.get_wife_friendlist(); }
    public String parse_towife() { return base.parse_towife(); }
    public int getWife() { return base.getWife(); }
    public int getisOK() { return base.getisOK(); }
    public List<GameObject> getEquippedObjects() { return base.getEquippedObjects(); }
    public byte isDead() { return base.isDead(); }
    public short getDeadLevel() { return base.getDeadLevel(); }
    public byte getDeathCount() { return base.getDeathCount(); }
    public long getTotalKills() { return base.getTotalKills(); }
    public String getDeathInformation() { return base.getDeathInformation(); }
    public boolean isGhost() { return base.isGhost(); }
    public Map<Integer, Integer> getStoreItems() { return base.getStoreItems(); }
    public int needEndFight() { return base.needEndFight(); }
    public MonsterGroup hasMobGroup() { return base.hasMobGroup(); }
    public boolean castEndFightAction() { return base.castEndFightAction(); }
    public String parseStoreItemsList() { return base.parseStoreItemsList(); }
    public int parseStoreItemsListPods() { return base.parseStoreItemsListPods(); }
    public String parseStoreItemstoBD() { return base.parseStoreItemstoBD(); }
    public int getSpeed() { return base.getSpeed(); }
    public boolean getMetierPublic() { return base.getMetierPublic(); }
    public boolean getLivreArtisant() { return base.getLivreArtisant(); }
    public String parsecolortomount() { return base.parsecolortomount(); }
    public Map<Integer, World.Couple<Integer, Integer>> getObjectsClassSpell() { return base.getObjectsClassSpell(); }
    public int storeAllBuy() { return base.storeAllBuy(); }
    public long getTimeTaverne() { return base.getTimeTaverne(); }
    public GameAction getGameAction() { return base.getGameAction(); }
    public int getAlignMap() { return base.getAlignMap(); }
    public List<Integer> getEmotes() { return base.getEmotes(); }
    public String parseEmoteToDB() { return base.parseEmoteToDB(); }
    public boolean getBlockMovement() { return base.getBlockMovement(); }
    public GameClient getGameClient() { return base.getGameClient(); }
    public boolean isSubscribe() { return base.isSubscribe(); }
    public boolean isMissingSubscription() { return base.isMissingSubscription(); }
    public boolean cantDefie() { return base.cantDefie(); }
    public boolean cantAgro() { return base.cantAgro(); }
    public boolean cantCanal() { return base.cantCanal(); }
    public boolean cantTP() { return base.cantTP(); }
    public boolean isInPrison() { return base.isInPrison(); }
    public Map<Integer, QuestPlayer> getQuestPerso() { return base.getQuestPerso(); }
    public String encodeQL() { return base.encodeQL(); }
    public House getInHouse() { return base.getInHouse(); }
    public ExchangeAction<?> getExchangeAction() { return base.getExchangeAction(); }
    public LangEnum getLang() { return base.getLang(); }
    public SPlayer scripted() { return base.scripted(); }

    public int getSpellPoints(boolean save) { return base.getSpellPoints(save); }
    public boolean setSitted(boolean sitted) { return base.setSitted(sitted); }
    public boolean unlearnSpell(int spell) { return base.unlearnSpell(spell); }
    public boolean boostSpell(int spellID) { return base.boostSpell(spellID); }
    public boolean forgetSpell(int spellID) { return base.forgetSpell(spellID); }
    public Spell.SortStats getSortStatBySortIfHas(int spellID) { return base.getSortStatBySortIfHas(spellID); }
    public String xpString(String c) { return base.xpString(c); }
    public Stats getTotalStats(boolean lessBuff) { return base.getTotalStats(lessBuff); }
    public String getItemsIDSplitByChar(String splitter) { return base.getItemsIDSplitByChar(splitter); }
    public String getStoreItemsIDSplitByChar(String splitter) { return base.getStoreItemsIDSplitByChar(splitter); }
    public boolean hasItemGuid(int guid) { return base.hasItemGuid(guid); }
    public GameObject getObjetByPos(int pos) { return base.getObjetByPos(pos); }
    public GameObject getObjetByPos2(int pos) { return base.getObjetByPos2(pos); }
    public boolean addXp(long winxp) { return base.addXp(winxp); }
    public boolean addXpIncarnations(long winxp) { return base.addXpIncarnations(winxp); }
    public boolean addKamas(long l) { return base.addKamas(l); }
    public boolean modKamasDisplay(long quantity) { return base.modKamasDisplay(quantity); }
    public GameObject getSimilarItem(GameObject exGameObject) { return base.getSimilarItem(exGameObject); }
    public int learnJob(Job m) { return base.learnJob(m); }
    public boolean hasEquiped(int id) { return base.hasEquiped(id); }
    public int getNumbEquipedItemOfPanoplie(int panID) { return base.getNumbEquipedItemOfPanoplie(panID); }
    public String getStringVar(String str) { return base.getStringVar(str); }
    public JobStat getMetierBySkill(int skID) { return base.getMetierBySkill(skID); }
    public String parseToFriendList(int guid) { return base.parseToFriendList(guid); }
    public String parseToEnemyList(int guid) { return base.parseToEnemyList(guid); }
    public JobStat getMetierByID(int job) { return base.getMetierByID(job); }
    public boolean hasItemType(int type) { return base.hasItemType(type); }
    public GameObject getItemTemplate(int i) { return base.getItemTemplate(i); }
    public int getNbItemTemplate(int i) { return base.getNbItemTemplate(i); }
    public boolean isDispo(BasePlayer sender) { return base.isDispo(sender); }
    public int setisOK(int ok) { return base.setisOK(ok); }
    public boolean hasSpell(int spellID) { return base.hasSpell(spellID); }
    public boolean addStaticEmote(int emote) { return base.addStaticEmote(emote); }
    public QuestPlayer getQuestPersoByQuest(Quest quest) { return base.getQuestPersoByQuest(quest); }
    public QuestPlayer getQuestPersoByQuestId(int id) { return base.getQuestPersoByQuestId(id); }
    public boolean removeItemShortcutSend(int position) { return base.removeItemShortcutSend(position); }
    public Optional<Integer> removeItemShortcutByHash(ItemHash hash) { return base.removeItemShortcutByHash(hash); }

    public boolean unlearnSpell(BasePlayer perso, int spellID, int level, int ancLevel, boolean save, boolean send){ return base.unlearnSpell(perso, spellID, level, ancLevel, save, send); }
    public boolean learnSpell(int spellID, int level, boolean save, boolean send, boolean learn){ return base.learnSpell(spellID, level, save, send, learn); }
    public boolean ensureSpellLevel(int spell, int level, boolean modPoints, boolean silent){ return base.ensureSpellLevel(spell, level, modPoints, silent); }
    public EnsureSpellLevelResult ensureSpellLevelSilent(int spell, int newLevel, boolean modPoints){ return base.ensureSpellLevelSilent(spell, newLevel, modPoints); }
    public boolean addItem(GameObject newItem, boolean stack, boolean display){ return base.addItem(newItem, stack, display); }
    public boolean addObjetSimiler(GameObject objet, boolean hasSimiler, int oldID){ return base.addObjetSimiler(objet, hasSimiler, oldID); }
    public boolean removeItemByTemplateId(int templateId, int count, boolean display){ return base.removeItemByTemplateId(templateId, count, display); }
    public boolean hasItemTemplate(int i, int q, boolean equipped){ return base.hasItemTemplate(i, q, equipped); }
    public GameObject getItemTemplate(int i, int q){ return base.getItemTemplate(i, q); }
    public boolean levelUp(boolean send, boolean addXp){ return base.levelUp(send, addXp); }
    public boolean levelUpIncarnations(boolean send, boolean addXp){ return base.levelUpIncarnations(send, addXp); }
    public int getValueOfClassObject(int spell, int effect){ return base.getValueOfClassObject(spell, effect); }
    public boolean consumeCurrency(SaleOffer.Currency cur, long qua){ return base.consumeCurrency(cur, qua); }
    public boolean moveItemShortcutSend(int oldPos, int newPos){ return base.moveItemShortcutSend(oldPos, newPos); }
    public boolean addItemShortcutSend(int position, int itemID){ return base.addItemShortcutSend(position, itemID); }

}
