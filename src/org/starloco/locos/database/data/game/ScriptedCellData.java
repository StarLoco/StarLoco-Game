package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ScriptedCellData extends FunctionDAO<Object> {
    public ScriptedCellData(HikariDataSource dataSource) {
        super(dataSource, "scripted_cells");
    }

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + ";");

            while (result.next()) {
                short mapId = result.getShort("MapID");
                int cellId = result.getInt("CellID");

                GameMap map = World.world.getMap(mapId);
                if (map == null) continue;
                GameCase cell = map.getCase(cellId);
                if (cell == null) continue;

                switch (result.getInt("EventID")) {
                    case 1: // Stop sur la case (triggers)
                        cell.addOnCellStopAction(result.getInt("ActionID"), result.getString("ActionsArgs"), result.getString("Conditions"), null);
                        break;
                }
            }
        } catch (SQLException e) {
            super.sendError(e);
            Main.stop("unknown");
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
        return ScriptedCellData.class;
    }

    public boolean update(int mapID1, int cellID1, int action, int event,
                          String args, String cond) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("REPLACE INTO " + getTableName() + " VALUES (?,?,?,?,?,?)");
            p.setInt(1, mapID1);
            p.setInt(2, cellID1);
            p.setInt(3, action);
            p.setInt(4, event);
            p.setString(5, args);
            p.setString(6, cond);
            execute(p);
            return true;
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
        return false;
    }

    public boolean delete(int mapID, int cellID) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("DELETE FROM " + getTableName() + " WHERE `MapID` = ? AND `CellID` = ?");
            p.setInt(1, mapID);
            p.setInt(2, cellID);
            execute(p);
            return true;
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
        return false;
    }
}
