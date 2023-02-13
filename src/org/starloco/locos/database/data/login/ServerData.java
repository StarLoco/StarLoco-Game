package org.starloco.locos.database.data.login;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Main;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ServerData extends FunctionDAO<Long> {
    public ServerData(HikariDataSource dataSource) {
        super(dataSource, "world_servers");
    }

    @Override
    public void loadFully() {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + DatabaseManager.get(PlayerData.class).getTableName() + " SET `logged` = 0 WHERE `server` = '" + Config.gameServerId + "';");
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    @Override
    public Long load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(Long entity) {
        throw new NotImplementedException();
    }

    @Override
    public void delete(Long entity) {
        throw new NotImplementedException();
    }

    @Override
    public void update(Long entity) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `uptime` = ? WHERE `id` = ?;");
            p.setLong(1, entity);
            p.setInt(2, Config.gameServerId);
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    @Override
    public Class<?> getReferencedClass() {
        return ServerData.class;
    }
}
