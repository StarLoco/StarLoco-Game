package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.object.GameObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MapData extends FunctionDAO<GameMap> {
    public MapData(HikariDataSource dataSource) {
        super(dataSource, "maps");
    }

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + " LIMIT " + Constant.DEBUG_MAP_LIMIT + ";");

            while (result.next()) {
                World.world.addMap(new GameMap(result.getShort("id"), result.getString("date"), result.getByte("width"), result.getByte("heigth"), result.getString("key"), result.getString("places"), result.getString("mapData"), result.getString("monsters"), result.getString("mappos"), result.getByte("numgroup"), result.getByte("fixSize"), result.getByte("minSize"), result.getByte("maxSize"), result.getString("forbidden"), result.getByte("sniffed")));
            }
            close(result);

            result = getData("SELECT * FROM mobgroups_fix");
            while (result.next()) {
                GameMap map = World.world.getMap(result.getShort("mapid"));
                if (map != null) {
                    GameCase cell = map.getCase(result.getInt("cellid"));
                    if(cell != null) {
                        map.addStaticGroup(result.getInt("cellid"), result.getString("groupData"), false);
                        World.world.addGroupFix(result.getInt("mapid") + ";" + result.getInt("cellid"), result.getString("groupData"), result.getInt("Timer"));
                    }
                }
            }
        } catch (SQLException e) {
            try {
                System.out.println(result.getShort("id"));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            super.sendError(e);
        } finally {
            close(result);
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
        return MapData.class;
    }

    public boolean updateGs(GameMap map) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `numgroup` = ?, `minSize` = ?, `fixSize` = ?, `maxSize` = ? WHERE id = ?;");
            p.setInt(1, map.getMaxGroupNumb());
            p.setInt(2, map.getMinSize());
            p.setInt(3, map.getFixSize());
            p.setInt(4, map.getMaxSize());
            p.setInt(5, map.getId());
            execute(p);
            return true;
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
        return false;
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
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + " LIMIT " + Constant.DEBUG_MAP_LIMIT);

            while (result.next()) {
                GameMap map = World.world.getMap(result.getShort("id"));
                if (map == null) {
                    World.world.addMap(new GameMap(result.getShort("id"), result.getString("date"), result.getByte("width"), result.getByte("heigth"), result.getString("key"), result.getString("places"), result.getString("mapData"), result.getString("monsters"), result.getString("mappos"), result.getByte("numgroup"), result.getByte("fixSize"), result.getByte("minSize"), result.getByte("maxSize"), result.getString("forbidden"), result.getByte("sniffed")));
                    continue;
                }
                map.setInfos(result.getString("date"), result.getString("monsters"), result.getString("mappos"), result.getByte("numgroup"), result.getByte("fixSize"), result.getByte("minSize"), result.getByte("maxSize"), result.getString("forbidden"));
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
    }
}
