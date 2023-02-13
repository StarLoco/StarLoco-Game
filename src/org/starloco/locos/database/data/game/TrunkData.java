package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.area.map.entity.House;
import org.starloco.locos.area.map.entity.Trunk;
import org.starloco.locos.client.Player;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.game.world.World;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TrunkData extends FunctionDAO<Trunk> {
    public TrunkData(HikariDataSource dataSource) {
        super(dataSource, "coffres");
    }


    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + "");

            while (result.next()) {
                int id = result.getInt("id");
                String objects = result.getString("object");
                objects = (objects == null || objects.equals(" ") ? "" : objects);
                Trunk trunk = World.world.getTrunk(id);

                if(trunk != null) {
                    trunk.setObjects(objects);
                    trunk.setKamas(result.getInt("kamas"));
                    trunk.setOwnerId(result.getInt("owner_id"));
                    trunk.setKey(result.getString("key"));
                }
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
    }

    @Override
    public Trunk load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(Trunk trunk) {
        PreparedStatement p = null;
        boolean ok = true;
        try {
            p = getPreparedStatement("INSERT INTO " + getTableName() + " (`id`, `object`, `kamas`, `key`, `owner_id`) " + "VALUES (?, ?, ?, ?, ?)");
            p.setInt(1, trunk.getId());
            p.setString(2, "");
            p.setInt(3, 0);
            p.setString(4, "-");
            p.setInt(5, trunk.getOwnerId());
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
    public void delete(Trunk entity) {
        throw new NotImplementedException();
    }

    @Override
    public void update(Trunk t) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `kamas`=?, `object`=? WHERE `id`=?");
            p.setLong(1, t.getKamas());
            p.setString(2, t.parseTrunkObjetsToDB());
            p.setInt(3, t.getId());
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    @Override
    public Class<?> getReferencedClass() {
        return TrunkData.class;
    }

    public void exist(Trunk trunk) {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + " WHERE `id` = '" + trunk.getId() + "';");
            if(!result.next()) {
                this.insert(trunk);
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
    }

    public void update(Player player, House house) {
        PreparedStatement p = null;

        for (Trunk trunk : Trunk.getTrunksByHouse(house)) {
            if(trunk.getOwnerId() != player.getAccID()) {
                trunk.setOwnerId(player.getAccID());
                trunk.setKey("-");

                try {
                    p = getPreparedStatement("UPDATE " + getTableName() + " SET `owner_id`=?, `key`='-' WHERE `id`=?");
                    p.setInt(1, player.getAccID());
                    p.setInt(2, trunk.getId());
                    execute(p);
                } catch (SQLException e) {
                    super.sendError(e);
                } finally {
                    close(p);
                }
            }
        }
    }


    public void updateCode(Player P, Trunk t, String packet) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `key`=? WHERE `id`=? AND owner_id=?");
            p.setString(1, packet);
            p.setInt(2, t.getId());
            p.setInt(3, P.getAccID());
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }
}
