package org.starloco.locos.database.legacydata.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.legacydata.FunctionDAO;
import org.starloco.locos.kernel.Constant;

import java.sql.SQLException;

public class ZaapiData extends FunctionDAO<Object> {
    public ZaapiData(HikariDataSource dataSource) {
        super(dataSource, "zaapi");
    }

    @Override
    public void loadFully() {
        try {
            getData("SELECT mapid, align FROM " + getTableName() + ";", result -> {
                StringBuilder angels = new StringBuilder(), demons = new StringBuilder(), neutral = new StringBuilder();
                while (result.next()) {
                    if (result.getInt("align") == Constant.ALIGNEMENT_BONTARIEN) {
                        angels.append(result.getString("mapid"));
                        if (!result.isLast()) angels.append(",");
                    } else if (result.getInt("align") == Constant.ALIGNEMENT_BRAKMARIEN) {
                        demons.append(result.getString("mapid"));
                        if (!result.isLast()) demons.append(",");
                    } else {
                        neutral.append(result.getString("mapid"));
                        if (!result.isLast()) neutral.append(",");
                    }
                }
                Constant.ZAAPI.put(Constant.ALIGNEMENT_BONTARIEN, angels.toString());
                Constant.ZAAPI.put(Constant.ALIGNEMENT_BRAKMARIEN, demons.toString());
                Constant.ZAAPI.put(Constant.ALIGNEMENT_NEUTRE, neutral.toString());
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
        return ZaapiData.class;
    }
}
