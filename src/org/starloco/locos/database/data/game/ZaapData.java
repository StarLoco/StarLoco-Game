package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.kernel.Constant;

import java.sql.SQLException;

public class ZaapData extends FunctionDAO<Object> {
    public ZaapData(HikariDataSource dataSource) {
        super(dataSource, "zaaps");
    }

    @Override
    public void loadFully() {
        try {
            getData("SELECT mapID, cellID FROM " + getTableName() + ";", result -> {
                while (result.next()) {
                    Constant.ZAAPS.put(result.getInt("mapID"), result.getInt("cellID"));
                }
            });
        } catch (SQLException e) {
            super.sendError(e);
        }
    }

    @Override
    public Object load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(Object entity) {
        throw new NotImplementedException();
    }

    @Override
    public void delete(Object entity) {
        throw new NotImplementedException();
    }

    @Override
    public void update(Object entity) {
        throw new NotImplementedException();
    }

    @Override
    public Class<?> getReferencedClass() {
        return ZaapData.class;
    }
}
