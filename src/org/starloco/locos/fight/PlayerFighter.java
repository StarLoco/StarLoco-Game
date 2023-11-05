package org.starloco.locos.fight;

import org.starloco.locos.client.Player;
import org.starloco.locos.client.other.Stats;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.data.game.ExperienceTables;
import org.starloco.locos.entity.mount.Mount;
import org.starloco.locos.fight.spells.Spell;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Constant;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class PlayerFighter extends Fighter {
    protected final Player player;

    protected PlayerFighter(Fight f, Player player) {
        super(player.getId(), f);
        this.player = player;
    }

    @Override
    public String getPacketsName() {
        return player.getName();
    }

    @Override
    public int getType() {
        return 1;
    }

    @Override
    public int getLvl() {
        return player.getLevel();
    }

    @Override
    public int baseMaxPdv() {
        return player.getMaxPdv();
    }

    @Override
    protected Stats getBaseStats() {
        return player.getTotalStats(true);
    }

    @Override
    public int getDefaultGfx() {
        return player.getGfxId();
    }

    public void sendStats() {
        SocketManager.GAME_SEND_STATS_PACKET(this.player);
    }

    @Override
    public void initFightBuffs() {
        this.fightBuffs.addAll(this.player.get_buff().values());
    }

    @Override
    public void send(String pck) {
        if(!player.isOnline()) return;
        player.send(pck);
    }

    @Override
    public Optional<Spell.SortStats> spellRankForID(int id) {
        return player.getSpells().stream().filter(s -> s.getSpellID() == id).findFirst();
    }

    @Override
    public int criticalStrikeModifier(int porcCC, int spellID) {
        int agi = getTotalStats().getEffect(Constant.STATS_ADD_AGIL);
        if (agi < 0)
            agi = 0;
        porcCC -= getTotalStats().getEffect(Constant.STATS_ADD_CC);
        if (player.getObjectsClassSpell().containsKey(spellID)) {
            int modi = player.getValueOfClassObject(spellID, 287);
            porcCC -= modi;
        }
        porcCC = (int) ((porcCC * 2.9901) / Math.log(agi + 12));
        return Math.max(2, porcCC);
    }

    @Override
    public String xpString(String separator) {
        ExperienceTables.ExperienceTable xpTable = World.world.getExperiences().players;

        return xpTable.minXpAt(this.player.getLevel()) + separator
            + this.player.getExp() + separator + xpTable.maxXpAt(this.player.getLevel());
    }

    protected Stream<String> getGMPacketParts() {
        List<String> factionParts = Arrays.asList(
            String.valueOf(player.getAlignment()),
            "0",
            player.is_showWings() ? String.valueOf(player.getGrade()) : "0",
            String.valueOf(player.getLevel() + player.getId()) // WTF ?
        );

        if (player.is_showWings() && player.getDeshonor() > 0) {
            factionParts.add(String.valueOf(player.getDeshonor() > 0 ? 1 : 0));
        }

        int[] colors = player.getColors();

        return Stream.of(
            String.valueOf(player.getClasse()),
            player.getGfxId() + "^" + player.get_size(),
            String.valueOf(player.getSexe()),
            String.valueOf(player.getLevel()),
            String.join(",", factionParts),
            (colors[0] == -1 ? "-1" : Integer.toHexString(colors[0])),
            (colors[1] == -1 ? "-1" : Integer.toHexString(colors[1])),
            (colors[2]  == -1 ? "-1" : Integer.toHexString(colors[2])),
            player.getGMStuffString(),
            String.valueOf(getPdv()),
            String.valueOf(getTotalStats().getEffect(Constant.STATS_ADD_PA)),
            String.valueOf(getTotalStats().getEffect(Constant.STATS_ADD_PM)),
            String.valueOf(getTotalStats().getEffect(Constant.STATS_ADD_RP_NEU)),
            String.valueOf(getTotalStats().getEffect(Constant.STATS_ADD_RP_TER)),
            String.valueOf(getTotalStats().getEffect(Constant.STATS_ADD_RP_FEU)),
            String.valueOf(getTotalStats().getEffect(Constant.STATS_ADD_RP_EAU)),
            String.valueOf(getTotalStats().getEffect(Constant.STATS_ADD_RP_AIR)),
            String.valueOf(getTotalStats().getEffect(Constant.STATS_ADD_ADODGE)),
            String.valueOf(getTotalStats().getEffect(Constant.STATS_ADD_MDODGE))
        );
    }

    @Override
    Optional<Mount> getMount() {
        return Optional.ofNullable(player.getMount());
    }

    public int[] getColors() {
        return new int[]{player.getColor1(), player.getColor2(), player.getColor3()};
    }

    @Override
    protected String getMountColors() {
        return player.encodeColorsForMount();
    }

    @Override
    public boolean aiControlled() { return false; }

    @Override
    boolean canLoot() { return true; }

    public Object scripted() {
        return player.scripted();
    }

    @Override
    public Player getPlayer() {
        return player;
    }
}
