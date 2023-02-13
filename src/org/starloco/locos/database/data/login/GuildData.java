package org.starloco.locos.database.data.login;

import com.mysql.jdbc.Statement;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.guild.Guild;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GuildData extends FunctionDAO<Guild> {

    public GuildData(HikariDataSource dataSource) {
        super(dataSource, "world_guilds");
    }


    @Override
    public void loadFully() {
        throw new NotImplementedException();
    }

    @Override
    public Guild load(int id) {
        ResultSet result = null;
        Guild guild = null;

        try {
            result = getData("SELECT * FROM " + getTableName() + " WHERE `id` = " + id + ";");

            while (result.next()) {
                guild = new Guild(result.getInt("id"), result.getString("name"), result.getString("emblem"), result.getInt("lvl"), result.getLong("xp"), result.getInt("capital"), result.getInt("maxCollectors"), result.getString("spells"), result.getString("stats"), result.getLong("date"));
                World.world.addGuild(guild);
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
        return guild;
    }

    @Override
    public boolean insert(Guild entity) {
        PreparedStatement statement = null;
        boolean ok = true;
        try {
            statement = this.getConnection().prepareStatement("INSERT INTO " + getTableName() + " (`name`, `emblem`, `lvl`, `xp`, `capital`, `maxCollectors`, `spells`, `stats`, `date`) VALUES (?,?,1,0,0,0,?,?,?);", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, entity.getName());
            statement.setString(2, entity.getEmblem());
            statement.setString(3, "462;0|461;0|460;0|459;0|458;0|457;0|456;0|455;0|454;0|453;0|452;0|451;0|");
            statement.setString(4, "176;100|158;1000|124;100|");
            statement.setLong(5, entity.getDate());
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                ok = false;
            } else {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        entity.setId(generatedKeys.getInt(1));
                    } else {
                        ok = false;
                    }
                }
            }
        } catch (SQLException e) {
            super.sendError(e);
            ok = false;
        } finally {
            close(statement);
        }
        return ok;
    }

    @Override
    public void delete(Guild entity) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("DELETE FROM " + getTableName() + " WHERE `id` = ?;");
            p.setInt(1, entity.getId());
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    @Override
    public void update(Guild entity) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `lvl` = ?, `xp` = ?, `capital` = ?, `maxCollectors` = ?, `spells` = ?, `stats` = ? WHERE id = ?;");
            p.setInt(1, entity.getLvl());
            p.setLong(2, entity.getXp());
            p.setInt(3, entity.getCapital());
            p.setInt(4, entity.getNbCollectors());
            p.setString(5, entity.compileSpell());
            p.setString(6, entity.compileStats());
            p.setInt(7, entity.getId());
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    @Override
    public Class<?> getReferencedClass() {
        return GuildData.class;
    }
}
