package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.starloco.locos.client.other.MorphMode;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.fight.spells.Spell;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FullMorphData extends FunctionDAO<Object> {
    public FullMorphData(HikariDataSource dataSource) {
        super(dataSource, "full_morphs");
    }

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + ";");

            while (result.next()) {
                int[] args = {};
                String temp = result.getString("args");
                if (StringUtils.isNotBlank(temp)) {
                    args = Arrays.stream(temp.split(",")).mapToInt(Integer::parseInt).toArray();
                }

                Map<Integer, Spell.SortStats> spells = new HashMap<>();
                Map<Integer, Integer> spellPositions = new HashMap<>();

                String[] spellParts = result.getString("spells").split(",");
                for (String e : spellParts) {
                    try {
                        String[] parts = e.split(";");
                        int id = Integer.parseInt(parts[0]);
                        int lvl = Integer.parseInt(parts[1]);

                        Spell.SortStats ss =  World.world.getSort(id).getStatsByLevel(lvl);
                        if(ss == null) throw new IllegalStateException(String.format("player has unknown spell: %d/%d", id, lvl));
                        spells.put(id, World.world.getSort(id).getStatsByLevel(lvl));

                        if(parts.length < 3 || parts[2].equalsIgnoreCase("")) continue;
                        int position = World.world.getCryptManager().getIntByHashedValue(parts[2].charAt(0)); // may return -1
                        if(position == 63) continue; // It was "_" which means no shortcut
                        if(position > 30) {
                            // Too high to be a valid base64 position
                            position =  Integer.parseInt(parts[2]);
                        }
                        spellPositions.put(id, position);
                    } catch (NumberFormatException e1) {
                        Main.logger.error("Cannot load player's spell", e1);
                    }
                }



                MorphMode mode = new MorphMode(result.getInt("id"), result.getString("name"), result.getInt("gfxId"), spells, spellPositions, args);
                World.world.getMorphModes().put(mode.getId(), mode);
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
    }



    @Override
    public Object load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(Object entity) {
        throw new NotImplementedException();
    }

    @Override
    public void delete(Object entity) {
        throw new NotImplementedException();
    }

    @Override
    public void update(Object entity) {
        throw new NotImplementedException();
    }

    @Override
    public Class<?> getReferencedClass() {
        return FullMorphData.class;
    }
}
