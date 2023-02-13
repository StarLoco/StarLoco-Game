package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.area.map.entity.House;
import org.starloco.locos.client.Player;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.game.world.World;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HouseData extends FunctionDAO<House> {
    public HouseData(HikariDataSource dataSource) {
        super(dataSource, "houses");
    }

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM houses");
            while (result.next()) {
                int id = result.getInt("id");
                int owner = result.getInt("owner_id");
                int sale = result.getInt("sale");
                int guild = result.getInt("guild_id");
                int access = result.getInt("access");
                String key = result.getString("key");
                int guildRights = result.getInt("guild_rights");
                House house = World.world.getHouse(id);
                if (house == null)
                    continue;
                if (owner != 0 && World.world.getAccount(owner) == null) {
                    (new Exception("La maison " + id
                            + " a un propriétaire inexistant.")).printStackTrace();
                }
                house.setOwnerId(owner);
                house.setSale(sale);
                house.setGuildId(guild);
                house.setAccess(access);
                house.setKey(key);
                house.setGuildRightsWithParse(guildRights);
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
    }

    @Override
    public House load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(House entity) {
        throw new NotImplementedException();
    }

    @Override
    public void delete(House entity) {
        throw new NotImplementedException();
    }

    @Override
    public void update(House h) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `owner_id` = ?,`sale` = ?,`guild_id` = ?,`access` = ?,`key` = ?,`guild_rights` = ? WHERE id = ?");
            p.setInt(1, h.getOwnerId());
            p.setInt(2, h.getSale());
            p.setInt(3, h.getGuildId());
            p.setInt(4, h.getAccess());
            p.setString(5, h.getKey());
            p.setInt(6, h.getGuildRights());
            p.setInt(7, h.getId());
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    @Override
    public Class<?> getReferencedClass() {
        return HouseData.class;
    }

    public boolean update(int id, long price) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `sale` = ? WHERE id = ?");
            p.setLong(1, price);
            p.setInt(2, id);
            execute(p);
            return true;
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
        return false;
    }

    public void buy(Player P, House h) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `sale`='0', `owner_id`=?, `guild_id`='0', `access`='0', `key`='-', `guild_rights`='0' WHERE `id`=?");
            p.setInt(1, P.getAccID());
            p.setInt(2, h.getId());
            execute(p);

            h.setSale(0);
            h.setOwnerId(P.getAccID());
            h.setGuildId(0);
            h.setAccess(0);
            h.setKey("-");
            h.setGuildRights(0);

            ((TrunkData) DatabaseManager.get(TrunkData.class)).update(P, h);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    public void sell(House h, int price) {
        h.setSale(price);
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `sale`=? WHERE `id` = ?;");
            p.setInt(1, price);
            p.setInt(2, h.getId());
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    public void updateCode(Player P, House h, String packet) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `key`=? WHERE `id`=? AND owner_id = ?;");
            p.setString(1, packet);
            p.setInt(2, h.getId());
            p.setInt(3, P.getAccID());
            execute(p);
            h.setKey(packet);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    public void updateGuild(House h, int GuildID, int GuildRights) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `guild_id`=?, `guild_rights`=? WHERE `id` = ?;");
            p.setInt(1, GuildID);
            p.setInt(2, GuildRights);
            p.setInt(3, h.getId());
            execute(p);
            h.setGuildId(GuildID);
            h.setGuildRights(GuildRights);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    public void removeGuild(int GuildID) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `guild_rights`='0', `guild_id`='0' WHERE `guild_id`=?;");
            p.setInt(1, GuildID);
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }
}
