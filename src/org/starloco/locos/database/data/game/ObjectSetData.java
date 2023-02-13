package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.object.ObjectSet;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ObjectSetData extends FunctionDAO<ObjectSet> {
    public ObjectSetData(HikariDataSource dataSource) {
        super(dataSource, "itemsets");
    }

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + ";");
            while (result.next()) {
                World.world.addItemSet(new ObjectSet(result.getInt("id"), result.getString("items"), result.getString("bonus")));
            }
            close(result);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
    }

    @Override
    public ObjectSet load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(ObjectSet entity) {
        throw new NotImplementedException();
    }

    @Override
    public void delete(ObjectSet entity) {
        throw new NotImplementedException();
    }

    @Override
    public void update(ObjectSet entity) {
        throw new NotImplementedException();
    }

    @Override
    public Class<?> getReferencedClass() {
        return ObjectSetData.class;
    }
}
