package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.job.maging.Rune;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RuneData extends FunctionDAO<Rune> {
    public RuneData(HikariDataSource dataSource) {
        super(dataSource, "runes");
    }

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + ";");
            while (result.next()) {
                new Rune(result.getShort("id"), result.getFloat("weight"), result.getByte("bonus"));
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
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
