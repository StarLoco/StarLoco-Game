package org.starloco.locos.database.data.login;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.game.scheduler.entity.WorldPub;
import org.starloco.locos.kernel.Config;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PubData extends FunctionDAO<Object> {

    public PubData(HikariDataSource dataSource)
	{
		super(dataSource, "world_pubs");
	}

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + " WHERE `server` = " + Config.gameServerId);
            while (result.next())
                WorldPub.pubs.add(result.getString("data"));
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
        return PubData.class;
    }
}
