package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.client.Player;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.guild.Guild;
import org.starloco.locos.guild.GuildMember;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GuildMemberData extends FunctionDAO<Player> {
    public GuildMemberData(HikariDataSource dataSource) {
        super(dataSource, "guild_members");
    }

    @Override
    public void loadFully() {
        try {
            getData("SELECT * FROM guild_members", result -> {
                while (result.next()) {
                    try {
                        Guild g = World.world.getGuild(result.getInt("guild"));
                        if (g != null)
                            g.addMember(result.getInt("guid"), result.getInt("rank"), result.getByte("pxp"), result.getLong("xpdone"), result.getInt("rights"), result.getString("lastConnection").replaceAll("-", "~"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (SQLException e) {
            super.sendError(e);
        }
    }

    @Override
    public Player load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(Player entity) {
        throw new NotImplementedException();
    }

    @Override
    public void delete(Player entity) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("DELETE FROM " + getTableName() + " WHERE `guid` = ?");
            p.setInt(1, entity.getId());
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    @Override
    public void update(Player player) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("REPLACE INTO " + getTableName() + " VALUES(?,?,?,?,?,?,?,?,?,?,?)");
            GuildMember gm = player.getGuildMember();
            if(gm == null) return;
            p.setInt(1, gm.getPlayerId());
            p.setInt(2, gm.getGuild().getId());
            p.setString(3, player.getName());
            p.setInt(4, gm.getLvl());
            int gfx = gm.getGfx();
            if (gfx > 121 || gfx < 10)
                gfx = player.getClasse() * 10 + player.getSexe();
            p.setInt(5, gfx);
            p.setInt(6, gm.getRank());
            p.setLong(7, gm.getXpGave());
            p.setInt(8, gm.getXpGive());
            p.setInt(9, gm.getRights());
            p.setInt(10, gm.getAlign());
            p.setString(11, gm.getLastCo());
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    @Override
    public Class<?> getReferencedClass() {
        return GuildMemberData.class;
    }

    public void deleteAll(int id) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("DELETE FROM " + getTableName() + " WHERE `guild` = ?;");
            p.setInt(1, id);
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    public int isPersoInGuild(int id) {
        try {
            return getData("SELECT guild FROM " + getTableName() + " WHERE guid = " + id + ";", result ->
                    result.first() ? result.getInt("guild") : -1);
        } catch (SQLException e) {
            super.sendError(e);
        }
        return -1;
    }

    public int[] isPersoInGuild(String name) {
        try {
            return getData("SELECT guild,guid FROM " + getTableName() + " WHERE name = '" + name + "';", result ->
                    result.first() ? new int[]{result.getInt("guid"), result.getInt("guild")} : new int[]{-1, -1});
        } catch (SQLException e) {
            super.sendError(e);
        }
        return new int[]{-1, -1};
    }
}
