package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.entity.monster.Monster;
import org.starloco.locos.game.world.World;
import org.starloco.locos.object.GameObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Locos on 15/08/2015.
 */
public class HeroicMobsGroupsData extends FunctionDAO<Object> {

    public HeroicMobsGroupsData(HikariDataSource dataSource) { super(dataSource); }

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM `heroic_mobs_groups`;");
            while (result.next()) {
                final GameMap map = World.world.getMap(result.getShort("map"));
                if(map != null) {
                    final Monster.MobGroup group = new Monster.MobGroup(result.getInt("id"), map, result.getInt("cell"), result.getString("group"), result.getString("objects"), result.getShort("stars"));
                    map.respawnGroup(group);
                }
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
        return HeroicMobsGroupsData.class;
    }

    public void insert(short map, Monster.MobGroup group) {
        PreparedStatement prepare = null;
        try {
            final StringBuilder objects = new StringBuilder(), groups = new StringBuilder();

            group.getObjects().stream().filter(Objects::nonNull)
                    .forEach(object -> objects.append(objects.toString().isEmpty() ? "" : ",").append(object.getGuid()));
            group.getMobs().values().stream().filter(Objects::nonNull)
                    .forEach(monster -> groups.append(groups.toString().isEmpty() ? "" : ";").append(monster.getTemplate().getId()).append(",").append(monster.getLevel()).append(",").append(monster.getLevel()));

            prepare = getPreparedStatement("INSERT INTO `heroic_mobs_groups` VALUES (?, ?, ?, ?, ?, ?);");
            prepare.setInt(1, group.getId());
            prepare.setInt(2, map);
            prepare.setInt(3, group.getCellId());
            prepare.setString(4, groups.toString());
            prepare.setString(5, objects.toString());
            prepare.setInt(6, group.getStarBonus());
            execute(prepare);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(prepare);
        }
    }

    public void update(short map, Monster.MobGroup group) {
        PreparedStatement prepare = null;
        try {
            final StringBuilder objects = new StringBuilder(), groups = new StringBuilder();
            group.getObjects().stream().filter(Objects::nonNull).forEach(object -> objects.append(objects.toString().isEmpty() ? "" : ",").append(object.getGuid()));
            group.getMobs().values().stream().filter(Objects::nonNull).forEach(monster -> groups.append(groups.toString().isEmpty() ? "" : ";").append(monster.getTemplate().getId()).append(",").append(monster.getLevel()).append(",").append(monster.getLevel()));

            prepare = getPreparedStatement("UPDATE `heroic_mobs_groups` SET `objects` = ?, `stars` = ? WHERE `id` = ? AND `map` = ? AND `group` = ?;");
            prepare.setString(1, objects.toString());
            prepare.setInt(2, group.getStarBonus());
            prepare.setLong(3, group.getId());
            prepare.setInt(4, map);
            prepare.setString(5, groups.toString());
            execute(prepare);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(prepare);
        }
    }

    public void delete(short map, Monster.MobGroup group) {
        PreparedStatement prepare = null;
        try {
            final StringBuilder groups = new StringBuilder();
            group.getMobs().values().stream().filter(Objects::nonNull).forEach(monster -> groups.append(groups.toString().isEmpty() ? "" : ";").append(monster.getTemplate().getId()).append(",").append(monster.getLevel()).append(",").append(monster.getLevel()));

            prepare = getPreparedStatement("DELETE FROM `heroic_mobs_groups` WHERE `id` = ? AND `map` = ? AND `group` = ?;");
            prepare.setLong(1, group.getId());
            prepare.setInt(2, map);
            prepare.setString(3, groups.toString());
            execute(prepare);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(prepare);
        }
    }

    public void deleteAll() {
        PreparedStatement prepare = null;
        try {
            prepare = getPreparedStatement("DELETE FROM `heroic_mobs_groups`;");
            execute(prepare);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(prepare);
        }
    }

    public void loadFix() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM `heroic_mobs_groups_fix`;");
            while (result.next()) {
                ArrayList<GameObject> objects = new ArrayList<>();
                for(String value : result.getString("objects").split(",")) {
                    final GameObject object = World.world.getGameObject(Integer.parseInt(value));
                    if(object != null)
                        objects.add(object);
                }
                GameMap map = World.world.getMap((short) result.getInt("map"));
                int cell = result.getInt("cell");
                for(Monster.MobGroup group : map.getMobGroups().values()) {
                    if(group != null && group.getCellId() == cell)
                        group.setStarBonus(result.getShort("stars"));
                }
                GameMap.fixMobGroupObjects.put(result.getInt("map") + "," + result.getInt("cell"), objects);
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
    }

    public void insertFix(short map, Monster.MobGroup group) {
        PreparedStatement prepare = null;
        try {
            final StringBuilder objects = new StringBuilder(), groups = new StringBuilder();
            group.getObjects().stream().filter(object -> object != null).forEach(object -> objects.append(objects.toString().isEmpty() ? "" : ",").append(object.getGuid()));
            group.getMobs().values().stream().filter(monster -> monster != null).forEach(monster -> groups.append(groups.toString().isEmpty() ? "" : ";").append(monster.getTemplate().getId()).append(",").append(monster.getLevel()).append(",").append(monster.getLevel()));

            prepare = getPreparedStatement("INSERT INTO `heroic_mobs_groups_fix` VALUES (?, ?, ?, ?)");
            prepare.setInt(1, map);
            prepare.setInt(2, group.getCellId());
            prepare.setString(3, groups.toString());
            prepare.setString(4, objects.toString());
            prepare.setInt(5, group.getStarBonus());
            execute(prepare);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(prepare);
        }
    }

    public void updateFix() {
        PreparedStatement prepare = null;
        try {
            for(Map.Entry<String, ArrayList<GameObject>> entry : GameMap.fixMobGroupObjects.entrySet()) {
                String[] split = entry.getKey().split(",");
                final StringBuilder objects = new StringBuilder();
                entry.getValue().stream().filter(object -> object != null).forEach(object -> objects.append(objects.toString().isEmpty() ? "" : ",").append(object.getGuid()));

                prepare = getPreparedStatement("UPDATE `heroic_mobs_groups_fix` SET `objects` = ? WHERE `map` = ? AND `cell` = ? AND `group` = ?;");
                prepare.setString(1, objects.toString());
                prepare.setLong(2, Integer.parseInt(split[0]));
                prepare.setInt(3, Integer.parseInt(split[1]));
                prepare.setString(4, World.world.getGroupFix(Integer.parseInt(split[0]), Integer.parseInt(split[1])).get("groupData"));
                execute(prepare);
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(prepare);
        }
    }

    public void deleteAllFix() {
        PreparedStatement prepare = null;
        try {
            prepare = getPreparedStatement("DELETE FROM `heroic_mobs_groups_fix`;");
            execute(prepare);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(prepare);
        }
    }
}
