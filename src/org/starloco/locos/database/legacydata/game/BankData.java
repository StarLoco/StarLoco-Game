package org.starloco.locos.database.legacydata.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.client.Account;
import org.starloco.locos.database.legacydata.FunctionDAO;
import org.starloco.locos.game.world.World;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BankData extends FunctionDAO<Account> {
    public BankData(HikariDataSource dataSource) {
        super(dataSource, "banks");
    }

    @Override
    public void loadFully() {
        throw new NotImplementedException();
    }

    @Override
    public Account load(int id) {
        Account account = World.world.ensureAccountLoaded(id);
        try {
            getData("SELECT * FROM " + getTableName() + " WHERE id = '" + id + "';", result -> {
                if (result.next()) {
                    account.parseBank(result.getInt("kamas"), result.getString("items"));
                } else {
                    account.parseBank(-1, null);
                }
            });
        } catch (SQLException e) {
            super.sendError(e);
        }
        return account;
    }

    @Override
    public boolean insert(Account entity) {
        PreparedStatement p = null;
        boolean ok = true;
        try {
            p = getPreparedStatement("INSERT INTO " + getTableName() + "(`id`, `kamas`, `items`) VALUES (?, 0, '')");
            p.setInt(1, entity.getId());
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
    public void delete(Account entity) {
        throw new NotImplementedException();
    }

    public void update(Account acc) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `kamas` = ?, `items` = ? WHERE `id` = ?");
            p.setLong(1, acc.getBankKamas());
            p.setString(2, acc.parseBankObjectsToDB());
            p.setInt(3, acc.getId());
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    @Override
    public Class<?> getReferencedClass() {
        return BankData.class;
    }
}
