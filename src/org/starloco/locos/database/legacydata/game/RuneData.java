package org.starloco.locos.database.legacydata.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.legacydata.FunctionDAO;
import org.starloco.locos.job.maging.Rune;

import java.sql.SQLException;

public class RuneData extends FunctionDAO<Rune> {
    public RuneData(HikariDataSource dataSource) {
        super(dataSource, "runes");
    }

    @Override
    public void loadFully() {
        try {
            getData("SELECT * FROM " + getTableName() + ";", result -> {
                while (result.next()) {
                    new Rune(result.getShort("id"), result.getFloat("weight"), result.getByte("bonus"));
                }
            });
        } catch (SQLException e) {
            super.sendError(e);
        }
    }

    @Override
    public Rune load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(Rune entity) {
        throw new NotImplementedException();
    }

    @Override
    public void delete(Rune entity) {
        throw new NotImplementedException();
    }

    @Override
    public void update(Rune entity) {
        throw new NotImplementedException();
    }

    @Override
    public Class<?> getReferencedClass() {
        return RuneData.class;
    }
}
