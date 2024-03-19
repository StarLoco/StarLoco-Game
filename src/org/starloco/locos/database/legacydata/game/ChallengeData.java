package org.starloco.locos.database.legacydata.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.legacydata.FunctionDAO;
import org.starloco.locos.game.world.World;

import java.sql.SQLException;

public class ChallengeData extends FunctionDAO<Object> {
    public ChallengeData(HikariDataSource dataSource) {
        super(dataSource, "challenge");
    }

    @Override
    public void loadFully() {
        try {
            getData("SELECT * FROM " + getTableName() + ";", result -> {
                while (result.next()) {
                    World.world.addChallenge(result.getInt("id") + "," + result.getInt("gainXP") + "," + result.getInt("gainDrop") + "," + result.getInt("gainParMob") + "," + result.getInt("conditions"));
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
        return ChallengeData.class;
    }
}
