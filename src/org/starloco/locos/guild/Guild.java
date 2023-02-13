package org.starloco.locos.guild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.starloco.locos.area.map.entity.House;
import org.starloco.locos.client.Player;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.game.GuildMemberData;
import org.starloco.locos.database.data.game.HouseData;
import org.starloco.locos.database.data.login.GuildData;
import org.starloco.locos.entity.Collector;
import org.starloco.locos.fight.spells.Spell.SortStats;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Constant;

public class Guild {

    private int id;
    private long xp;
    private final long date;
    private String name = "", emblem = "";
    private int lvl, capital = 0, nbCollectors = 0;
    private final Map<Integer, GuildMember> members = new TreeMap<>();
    private final Map<Integer, SortStats> spells = new HashMap<>(); // <Id, Level>
    private final Map<Integer, Integer> stats = new HashMap<>(); // <Effect, Quantity>

    public Guild(String name, String emblem) {
        this.name = name;
        this.emblem = emblem;
        this.lvl = 1;
        this.xp = 0;
        this.date = System.currentTimeMillis();
        this.decompileSpell("462;0|461;0|460;0|459;0|458;0|457;0|456;0|455;0|454;0|453;0|452;0|451;0|");
        this.decompileStats("176;100|158;1000|124;0|");
        ((GuildData) DatabaseManager.get(GuildData.class)).insert(this);
    }

    public Guild(int id, String name, String emblem, int lvl, long xp, int capital, int nbCollectors, String sorts, String stats, long date) {
        this.id = id;
        this.name = name;
        this.emblem = emblem;
        this.xp = xp;
        this.lvl = lvl;
        this.capital = capital;
        this.nbCollectors = nbCollectors;
        this.date = date;
        this.decompileSpell(sorts);
        this.decompileStats(stats);
    }

    public void addMember(int id, int r, byte pXp, long x, int ri, String lastCo) {
        Player player = World.world.getPlayer(id);
        if (player == null) return;
        GuildMember guildMember = new GuildMember(player, this, r, x, pXp, ri, lastCo);
        this.members.put(id, guildMember);
        player.setGuildMember(guildMember);
    }

    public GuildMember addNewMember(Player player) {
        GuildMember guildMember = new GuildMember(player, this, 0, 0, (byte) 0, 0, player.getAccount().getLastConnectionDate());
        this.members.put(player.getId(), guildMember);
        player.setGuildMember(guildMember);
        return guildMember;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNbCollectors() {
        return this.nbCollectors;
    }

    public void setNbCollectors(int nbr) {
        this.nbCollectors = nbr;
    }

    public int getCapital() {
        return this.capital;
    }

    public void setCapital(int nbr) {
        this.capital = nbr;
    }

    public Map<Integer, SortStats> getSpells() {
        return this.spells;
    }

    public Map<Integer, Integer> getStats() {
        return stats;
    }

    public long getDate() {
        return date;
    }

    public void boostSpell(int id) {
        SortStats SS = this.spells.get(id);
        if (SS != null && SS.getLevel() == 5)
            return;
        this.spells.put(id, ((SS == null) ? World.world.getSort(id).getStatsByLevel(1) : World.world.getSort(id).getStatsByLevel(SS.getLevel() + 1)));
    }

    public void unBoostSpell(int id) {
        SortStats SS = this.spells.get(id);
        if (SS != null) {
            this.capital += 5 * SS.getLevel();
            this.spells.put(id, null);
        }
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmblem() {
        return this.emblem;
    }

    public long getXp() {
        return this.xp;
    }

    public int getLvl() {
        return this.lvl;
    }

    public boolean haveTenMembers() {
        return this.id == 1 || this.id == 2 || (this.members.size() >= 10);
    }

    public List<Player> getPlayers() {
        //return this.members.stream().filter(guildMember -> guildMember.getPlayer() != null).map(GuildMember::getPlayer).collect(Collectors.toList());
    	ArrayList<Player> a = new ArrayList<>();
		for (GuildMember GM : this.members.values())
			if (GM.getPlayer() != null)
				a.add(GM.getPlayer());
		return a;
    }

    public GuildMember getMember(int id) {
        for(GuildMember guildMember : this.members.values())
            if(guildMember.getPlayerId() == id)
                return guildMember;
        return null;
    }

    public void removeMember(Player player) {
        House house = World.world.getHouseManager().getHouseByPerso(player);
        if (house != null)
            if (World.world.getHouseManager().houseOnGuild(this.id) > 0)
                ((HouseData) DatabaseManager.get(HouseData.class)).updateGuild(house, 0, 0);
        this.members.remove(player.getId());
        ((GuildMemberData) DatabaseManager.get(GuildMemberData.class)).delete(player);
    }

    public void addXp(long xp) {
        this.xp += xp;
        while (this.xp >= World.world.getGuildXpMax(this.lvl) && this.lvl < 200) this.levelUp();
    }

    private void levelUp() {
        this.lvl++;
        this.capital += 5;
    }

    private void decompileSpell(String spells) {
        for (String split : spells.split("\\|"))
            this.spells.put(Integer.parseInt(split.split(";")[0]), World.world.getSort(Integer.parseInt(split.split(";")[0])).getStatsByLevel(Integer.parseInt(split.split(";")[1])));
    }

    public String compileSpell() {
        if (this.spells.isEmpty())
            return "";

        StringBuilder toReturn = new StringBuilder();
        boolean isFirst = true;

        for (Entry<Integer, SortStats> curSpell : this.spells.entrySet()) {
            if (!isFirst)
                toReturn.append("|");
            toReturn.append(curSpell.getKey()).append(";").append(((curSpell.getValue() == null) ? 0 : curSpell.getValue().getLevel()));
            isFirst = false;
        }

        return toReturn.toString();
    }

    private void decompileStats(String statsStr) {
        for (String split : statsStr.split("\\|"))
            this.stats.put(Integer.parseInt(split.split(";")[0]), Integer.parseInt(split.split(";")[1]));
    }

    public String compileStats() {
        if (this.stats.isEmpty())
            return "";

        StringBuilder toReturn = new StringBuilder();
        boolean isFirst = true;

        for (Entry<Integer, Integer> curStats : this.stats.entrySet()) {
            if (!isFirst)
                toReturn.append("|");

            toReturn.append(curStats.getKey()).append(";").append(curStats.getValue());

            isFirst = false;
        }

        return toReturn.toString();
    }

    public void upgradeStats(int id, int add) {
        this.stats.put(id, (this.stats.get(id) + add));
    }

    public int resetStats(int id) {
        int quantity = this.stats.get(id);
        this.stats.put(id, 0);
        return quantity;
    }

    public int getStats(int id) {
        return stats.get(id);
    }

    //region Parse packet
    public String parseCollectorToGuild() {
        return String.valueOf(getNbCollectors()) + "|" + Collector.countCollectorGuild(getId()) + "|" + 100 * getLvl() + "|" + getLvl() + "|" + getStats(158) + "|" + getStats(176) + "|" + getStats(124) + "|" + getNbCollectors() + "|" + getCapital() + "|" + (1000 + (10 * getLvl())) + "|" + compileSpell();
    }

    public String parseQuestionTaxCollector() {
        return "1" + ';' + getName() + ',' + getStats(Constant.STATS_ADD_PODS) + ',' + getStats(Constant.STATS_ADD_PROS) + ',' + getStats(Constant.STATS_ADD_SAGE) + ',' + getNbCollectors();
    }

    public String parseMembersToGM() {
        StringBuilder str = new StringBuilder();
        for (GuildMember GM : this.members.values()) {
            String online = "0";
            if (GM.getPlayer() != null)
                if (GM.getPlayer().isOnline())
                    online = "1";
            if (str.length() != 0)
                str.append("|");
            str.append(GM.getPlayerId()).append(";");
            str.append(GM.getName()).append(";");
            str.append(GM.getLvl()).append(";");
            str.append(GM.getGfx()).append(";");
            str.append(GM.getRank()).append(";");
            str.append(GM.getXpGave()).append(";");
            str.append(GM.getXpGive()).append(";");
            str.append(GM.getRights()).append(";");
            str.append(online).append(";");
            str.append(GM.getAlign()).append(";");
            str.append(GM.getHoursFromLastCo());
        }
        return str.toString();
    }
    //endregion
}
