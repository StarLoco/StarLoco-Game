package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.hdv.BigStore;

import java.sql.SQLException;

public class HdvData extends FunctionDAO<Object> {
    public HdvData(HikariDataSource dataSource) {
        super(dataSource, "hdvs");
    }

    @Override
    public void loadFully() {
        try {
            getData("SELECT * FROM " + getTableName() + " ORDER BY id ASC;", result -> {
                while (result.next()) {
                    World.world.addHdv(new BigStore(result.getInt("map"), result.getFloat("sellTaxe"), result.getShort("sellTime"), result.getShort("accountItem"), result.getShort("lvlMax"), result.getString("categories")));
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
        return HdvData.class;
    }
}
