package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.client.Player;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.entity.Collector;
import org.starloco.locos.game.world.World;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CollectorData extends FunctionDAO<Collector> {
    public CollectorData(HikariDataSource dataSource) {
        super(dataSource, "percepteurs");
    }

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + ";");

            while (result.next()) {
                GameMap map = World.world.getMap(result.getShort("mapid"));
                if (map != null) {
                    int poseur_id = result.getInt("poseur_id");
                    Player player = World.world.getPlayer(poseur_id);
                    String date = result.getString("date");
                    long time = 0;
                    if (date != null && !date.equals("")) {
                        time = Long.parseLong(date);
                    }

                    World.world.addCollector(new Collector(result.getInt("guid"), result.getShort("mapid"), result.getInt("cellid"), result.getByte("orientation"), result.getInt("guild_id"), result.getShort("N1"), result.getShort("N2"), player, time, result.getString("objets"), result.getLong("kamas"), result.getLong("xp")));
                }
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
    }

    @Override
    public Collector load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(Collector entity) {
        PreparedStatement p = null;
        boolean ok = true;
        try {
            p = getPreparedStatement("INSERT INTO " + getTableName() + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
            entity.setId(this.getId());
            p.setInt(1, entity.getId());
            p.setInt(2, entity.getMap());
            p.setInt(3, entity.getCell());
            p.setInt(4, 3);
            p.setInt(5, entity.getGuildId());
            p.setInt(6, entity.getPoseur().getId());
            p.setString(7, Long.toString(entity.getDate()));
            p.setInt(8, entity.getN1());
            p.setInt(9, entity.getN2());
            p.setString(10, "");
            p.setLong(11, 0);
            p.setLong(12, 0);
            int affectedRows = p.executeUpdate();

            if (affectedRows == 0) {
                ok = false;
            } else {
                try (ResultSet generatedKeys = p.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        entity.setId(generatedKeys.getInt(1));
                    } else {
                        ok = false;
                    }
                }
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
        return ok;
    }

    @Override
    public void delete(Collector entity) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("DELETE FROM " + getTableName() + " WHERE guid = ?");
            p.setInt(1, entity.getId());
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    @Override
    public void update(Collector entity) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `objets` = ?,`kamas` = ?,`xp` = ? WHERE guid = ?");
            p.setString(1, entity.parseItemCollector());
            p.setLong(2, entity.getKamas());
            p.setLong(3, entity.getXp());
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
        return CollectorData.class;
    }

    private int getId() {
        ResultSet result = null;
        int i = -100;//Pour �viter les conflits avec touts autre NPC
        try {
            result = getData("SELECT `guid` FROM `percepteurs` ORDER BY `guid` ASC LIMIT 0 , 1");
            while (result.next()) {
                i = result.getInt("guid") - 1;
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
        if (i >= -9999)
            i = -10000;

        return i;
    }
}
