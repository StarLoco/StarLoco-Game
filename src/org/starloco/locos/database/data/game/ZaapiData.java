package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.kernel.Constant;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ZaapiData extends FunctionDAO<Object> {
    public ZaapiData(HikariDataSource dataSource) {
        super(dataSource, "zaapi");
    }

    @Override
    public void loadFully() {
        ResultSet result = null;
        String bontarien = "", brakmarien = "", neutre = "";
        try {
            result = getData("SELECT mapid, align FROM " + getTableName() + ";");
            while (result.next()) {
                if (result.getInt("align") == Constant.ALIGNEMENT_BONTARIEN) {
                    bontarien += result.getString("mapid");
                    if (!result.isLast()) bontarien += ",";
                } else if (result.getInt("align") == Constant.ALIGNEMENT_BRAKMARIEN) {
                    brakmarien += result.getString("mapid");
                    if (!result.isLast()) brakmarien += ",";
                } else {
                    neutre += result.getString("mapid");
                    if (!result.isLast()) neutre += ",";
                }
            }
            Constant.ZAAPI.put(Constant.ALIGNEMENT_BONTARIEN, bontarien);
            Constant.ZAAPI.put(Constant.ALIGNEMENT_BRAKMARIEN, brakmarien);
            Constant.ZAAPI.put(Constant.ALIGNEMENT_NEUTRE, neutre);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
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
