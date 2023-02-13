package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.util.Pair;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.client.Account;
import org.starloco.locos.database.data.FunctionDAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GiftData extends FunctionDAO<Pair<Account, String>> {
    public GiftData(HikariDataSource dataSource) {
        super(dataSource, "gifts");
    }

    @Override
    public void loadFully() {
        throw new NotImplementedException();
    }

    @Override
    public Pair<Account, String> load(int id) {
        ResultSet result = null;
        String gift = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + " WHERE id = '" + id + "';");
            if (result.next()) {
                gift = result.getString("objects");
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            super.close(result);
        }
        return new Pair<>(null, gift);
    }

    @Override
    public boolean insert(Pair<Account, String> entity) {
        PreparedStatement p = null;
        boolean ok = true;
        try {
            p = getPreparedStatement("INSERT INTO " + getTableName() + "(`id`, `objects`) VALUES ('" + entity.getKey().getId() + "', '');");
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
    public void delete(Pair<Account, String> entity) {
        throw new NotImplementedException();
    }

    @Override
    public void update(Pair<Account, String> entity) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `objects` = ? WHERE `id` = ?;");
            p.setString(1, entity.getValue());
            p.setInt(2, entity.getKey().getId());
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    @Override
    public Class<?> getReferencedClass() {
        return GiftData.class;
    }
}
