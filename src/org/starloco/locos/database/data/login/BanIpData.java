package org.starloco.locos.database.data.login;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.data.FunctionDAO;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BanIpData extends FunctionDAO<String> {
    public BanIpData(HikariDataSource dataSource) {
        super(dataSource, "administration_ban_ip");
    }

    @Override
    public void loadFully() {
        throw new NotImplementedException();
    }

    @Override
    public String load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(String entity) {
        PreparedStatement p = null;
        boolean ok = true;
        try {
            p = getPreparedStatement("INSERT INTO " + getTableName() + " VALUES (?)");
            p.setString(1, entity);
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
    public void delete(String ip) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("DELETE FROM " + getTableName() + " WHERE `ip` = ?");
            p.setString(1, ip);
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    @Override
    public void update(String entity) {
        throw new NotImplementedException();
    }

    @Override
    public Class<?> getReferencedClass() {
        return BanIpData.class;
    }
}
