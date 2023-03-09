package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.entity.monster.Monster;
import org.starloco.locos.fight.spells.Spell;
import org.starloco.locos.fight.spells.Spell.SortStats;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Main;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SpellData extends FunctionDAO<Spell> {
    public SpellData(HikariDataSource dataSource) {
        super(dataSource, "sorts");
    }

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + ";");
            boolean modif = false;

            while (result.next()) {
                int id = result.getInt("id");
                Spell spell = World.world.getSort(id);

                SortStats l1 = parseSortStats(id, 1, result.getString("lvl1")), l2 = parseSortStats(id, 2, result.getString("lvl2")),
                        l3 = parseSortStats(id, 3, result.getString("lvl3")), l4 = parseSortStats(id, 4, result.getString("lvl4")), l5 = null, l6 = null;
                if (!result.getString("lvl5").equalsIgnoreCase("-1"))
                    l5 = parseSortStats(id, 5, result.getString("lvl5"));
                if (!result.getString("lvl6").equalsIgnoreCase("-1"))
                    l6 = parseSortStats(id, 6, result.getString("lvl6"));


                if (spell != null) {
                    spell.setInfo(result.getInt("sprite"), result.getString("spriteInfos"), result.getString("effectTarget"), result.getInt("type"), result.getShort("duration"));
                    modif = true;
                } else {
                    spell = new Spell(id, result.getString("nom"), result.getInt("sprite"), result.getString("spriteInfos"), result.getString("effectTarget"), result.getInt("type"), result.getShort("duration"), result.getString("invalid_state"), result.getString("needed_state"));
                    World.world.addSort(spell);
                }
                spell.getSpellsStats().clear();
                spell.addSpellStats(1, l1);
                spell.addSpellStats(2, l2);
                spell.addSpellStats(3, l3);
                spell.addSpellStats(4, l4);
                spell.addSpellStats(5, l5);
                spell.addSpellStats(6, l6);

            }
            if (modif)
                for (Monster monster : World.world.getMonstres())
                    monster.getGrades().values().forEach(Monster.MobGrade::refresh);
        } catch (SQLException e) {
            super.sendError(e);
            Main.stop("Can't load spells");
        } finally {
            close(result);
        }
    }

    @Override
    public Spell load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(Spell entity) {
        throw new NotImplementedException();
    }

    @Override
    public void delete(Spell entity) {
        throw new NotImplementedException();
    }

    @Override
    public void update(Spell entity) {
        throw new NotImplementedException();
    }

    @Override
    public Class<?> getReferencedClass() {
        return SpellData.class;
    }

    private SortStats parseSortStats(int id, int lvl, String str) {
        try {
            String[] stat = str.split(",");
            String effets = stat[0], CCeffets = stat[1];
            int PACOST = 6;

            try {
                PACOST = Integer.parseInt(stat[2].trim());
            } catch (NumberFormatException ignored) {}

            int POm = Integer.parseInt(stat[3].trim());
            int POM = Integer.parseInt(stat[4].trim());
            int TCC = Integer.parseInt(stat[5].trim());
            int TEC = Integer.parseInt(stat[6].trim());

            boolean line = stat[7].trim().equalsIgnoreCase("true");
            boolean LDV = stat[8].trim().equalsIgnoreCase("true");
            boolean emptyCell = stat[9].trim().equalsIgnoreCase("true");
            boolean MODPO = stat[10].trim().equalsIgnoreCase("true");

            int MaxByTurn = Integer.parseInt(stat[12].trim());
            int MaxByTarget = Integer.parseInt(stat[13].trim());
            int CoolDown = Integer.parseInt(stat[14].trim());

            String type = stat[15].trim();

            int level = Integer.parseInt(stat[stat.length - 2].trim());
            boolean endTurn = stat[19].trim().equalsIgnoreCase("true");

            return new SortStats(id, lvl, PACOST, POm, POM, TCC, TEC, line, LDV, emptyCell, MODPO, MaxByTurn, MaxByTarget, CoolDown, level, endTurn, effets, CCeffets, type);
        } catch (Exception e) {
            super.sendError(e);
            return null;
        }
    }
}
