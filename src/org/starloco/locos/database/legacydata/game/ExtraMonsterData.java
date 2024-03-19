package org.starloco.locos.database.legacydata.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.legacydata.FunctionDAO;
import org.starloco.locos.game.world.World;

import java.sql.SQLException;

public class ExtraMonsterData extends FunctionDAO<Object> {
    public ExtraMonsterData(HikariDataSource dataSource) {
        super(dataSource, "extra_monster");
    }

    @Override
    public void loadFully() {
        try {
            getData("SELECT * from extra_monster", result -> {
                while (result.next()) {
                    World.world.addExtraMonster(result.getInt("idMob"), result.getString("superArea"), result.getString("subArea"), result.getInt("chances"));
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
        return ExtraMonsterData.class;
    }
}
