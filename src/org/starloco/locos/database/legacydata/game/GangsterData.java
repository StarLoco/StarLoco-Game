package org.starloco.locos.database.legacydata.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.legacydata.FunctionDAO;
import org.starloco.locos.entity.monster.boss.Bandit;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GangsterData extends FunctionDAO<Bandit> {
    public GangsterData(HikariDataSource dataSource) {
        super(dataSource, "bandits");
    }

    @Override
    public void loadFully() {
        try {
            getData("SELECT * FROM " + getTableName() + ";", result -> {
                while (result.next()) {
                    new Bandit(result.getString("mobs"), result.getString("maps"), result.getLong("time"));
                }
            });
        } catch (SQLException e) {
            super.sendError(e);
        }
    }

    @Override
    public Bandit load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(Bandit entity) {
        throw new NotImplementedException();
    }

    @Override
    public void delete(Bandit entity) {
        throw new NotImplementedException();
    }

    @Override
    public void update(Bandit obj) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `time` = ? WHERE `mobs` = '" + obj.parseMobs() + "';");
            p.setLong(1, obj.getTime());
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    @Override
    public Class<?> getReferencedClass() {
        return GangsterData.class;
    }
}
