package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.area.map.MapData;
import org.starloco.locos.area.map.SQLMapData;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.kernel.Main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GameMapData extends FunctionDAO<GameMap> {
    public GameMapData(HikariDataSource dataSource) {
        super(dataSource, "maps");
    }

    private SQLMapData mapDataFromResultSet(ResultSet result) throws SQLException {
        return SQLMapData.build(
                result.getInt("id"),
                result.getString("date"),
                result.getInt("width"),
                result.getInt("heigth"),
                result.getString("key"),
                result.getString("mapData"),
                result.getString("places"),
                result.getString("monsters"),
                result.getString("mappos"),
                result.getInt("numgroup"),
                result.getByte("fixSize"),
                result.getByte("minSize"),
                result.getByte("maxSize"),
                result.getString("forbidden"));
    }

    @Override
    public void loadFully() {
        try {
            getData("SELECT * FROM " + getTableName() + " LIMIT " + Constant.DEBUG_MAP_LIMIT + ";", result -> {
                while (result.next()) {
                    try {
                        MapData md = mapDataFromResultSet(result);
                        World.world.addMapData(md);

                        DatabaseManager.get(EndFightActionData.class).load(md.id);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Main.stop("SQLMapTemplate");
                    }
                }
            });

            getData("SELECT * FROM mobgroups_fix", result -> {
                while (result.next()) {
                    int mapId = result.getInt("mapid");
                    try {
                        MapData md = World.world.getMapData(mapId).orElse(null);
                        if (md instanceof SQLMapData) {
                            int cellId = result.getInt("cellid");
                            ((SQLMapData) md).addStaticGroup(cellId, result.getString("groupData"));
                            World.world.addGroupFix(mapId + ";" + cellId, result.getString("groupData"), result.getInt("Timer"));
                        }
                    } catch (SQLException e) {
                        throw new SQLException("Map #" + mapId, e);
                    }
                }
            });
        } catch (SQLException e) {
            super.sendError(e);
        }
    }

    @Override
    public GameMap load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(GameMap entity) {
        throw new NotImplementedException();
    }

    @Override
    public void delete(GameMap entity) {
        throw new NotImplementedException();
    }

    @Override
    public void update(GameMap entity) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `places` = ?, `numgroup` = ?, `forbidden` = ? WHERE id = ?;");
            p.setString(1, entity.getPlaces());
            p.setInt(2, entity.getMaxGroupNumb());
            p.setString(3, entity.getForbidden());
            p.setInt(4, entity.getId());
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    @Override
    public Class<?> getReferencedClass() {
        return GameMapData.class;
    }

    public boolean updateMonster(GameMap map, String monsters) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `monsters` = ? WHERE id = ?");
            p.setString(1, monsters);
            p.setInt(2, map.getId());
            execute(p);
            return true;
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
        return false;
    }

    public void reload() {
        try {
            getData("SELECT * FROM " + getTableName() + " LIMIT " + Constant.DEBUG_MAP_LIMIT, result -> {
                while (result.next()) {
                    try {
                        World.world.addMapData(mapDataFromResultSet(result));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Main.stop("SQLMapTemplate");
                    }
                }
            });
        } catch (SQLException e) {
            super.sendError(e);
        }
    }
}
