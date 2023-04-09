package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.object.ObjectSet;

import java.sql.SQLException;

public class ObjectSetData extends FunctionDAO<ObjectSet> {
    public ObjectSetData(HikariDataSource dataSource) {
        super(dataSource, "itemsets");
    }

    @Override
    public void loadFully() {
        try {
            getData("SELECT * FROM " + getTableName() + ";", result -> {
                while (result.next()) {
                    World.world.addItemSet(new ObjectSet(result.getInt("id"), result.getString("items"), result.getString("bonus")));
                }
            });
        } catch (SQLException e) {
            super.sendError(e);
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
