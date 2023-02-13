package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.hdv.Hdv;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HdvData extends FunctionDAO<Object> {
    public HdvData(HikariDataSource dataSource) {
        super(dataSource, "hdvs");
    }

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + " ORDER BY id ASC;");
            while (result.next()) {
                World.world.addHdv(new Hdv(result.getInt("map"), result.getFloat("sellTaxe"), result.getShort("sellTime"), result.getShort("accountItem"), result.getShort("lvlMax"), result.getString("categories")));
            }
            close(result);
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
        return HdvData.class;
    }
}
