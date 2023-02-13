package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.game.world.World.Couple;
import org.starloco.locos.kernel.Main;
import org.starloco.locos.other.Dopeul;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DungeonData extends FunctionDAO<Object> {
    public DungeonData(HikariDataSource dataSource) {
        super(dataSource, "donjons");
    }

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + ";");
            while (result.next()) {
                Dopeul.getDonjons().put(result.getInt("map"), new Couple<>(result.getInt("npc"), result.getInt("key")));
            }
        } catch (SQLException e) {
            super.sendError(e);
            Main.stop("unknown");
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
        return DungeonData.class;
    }
}
