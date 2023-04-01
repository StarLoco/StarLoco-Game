package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.area.map.MapData;
import org.starloco.locos.area.map.SQLMapData;
import org.starloco.locos.util.Pair;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.entity.npc.Npc;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Config;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NpcData extends FunctionDAO<Pair<Npc, Integer>> {
    public NpcData(HikariDataSource dataSource) {
        super(dataSource, "npcs");
    }

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + ";");
            while (result.next()) {
                int mapID = result.getInt("mapid");
                MapData md = World.world.getMapData(mapID).orElseThrow(() -> new IllegalArgumentException(String.format("unknown map #%d", mapID)));

                int id = result.getInt("npcid");
                if (!Config.modeChristmas && id == 795) // PNJ Noel
                    continue;
                if(Config.modeHeroic && (id == 1121 || id == 1127)) // PNJ Traque Heroic
                    continue;

                if(md instanceof SQLMapData) {
                    // We don't want to override script map data
                    md.addNpc(id, result.getInt("cellid"), result.getInt("orientation"));
                }
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
    }

    @Override
    public Pair<Npc, Integer> load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(Pair<Npc, Integer> entity) {
        PreparedStatement p = null;
        boolean ok = true;
        try {
            p = getPreparedStatement("INSERT INTO " + getTableName() + " VALUES (?,?,?,?,?);");
            p.setInt(1, entity.getSecond());
            p.setInt(2, entity.getFirst().getTemplate().getId());
            p.setInt(3, entity.getFirst().getCellId());
            p.setInt(4, entity.getFirst().getOrientation());
            p.setBoolean(5, false);
            execute(p);
        } catch (SQLException e) {
            ok = false;
            super.sendError(e);
        } finally {
            close(p);
        }
        return ok;
    }

    @Override
    public void delete(Pair<Npc, Integer> entity) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("DELETE FROM " + getTableName() + " WHERE mapid = ? AND cellid = ?;");
            p.setInt(1, entity.getSecond());
            p.setInt(2, entity.getFirst().getCellId());
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    @Override
    public void update(Pair<Npc, Integer> entity) {
        throw new NotImplementedException();
    }

    @Override
    public Class<?> getReferencedClass() {
        return NpcData.class;
    }
}
