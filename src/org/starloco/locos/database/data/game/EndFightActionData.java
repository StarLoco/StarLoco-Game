package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.area.map.MapData;
import org.starloco.locos.area.map.SQLMapData;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Main;
import org.starloco.locos.other.Action;

import java.sql.SQLException;

public class EndFightActionData extends FunctionDAO<Object> {
    public EndFightActionData(HikariDataSource dataSource) {
        super(dataSource, "endfight_action");
    }

    @Override
    public void loadFully() {
        throw new NotImplementedException();
    }

    @Override
    public Object load(int id) {
        MapData md = World.world.getMapData(id).orElse(null);
        if (!(md instanceof SQLMapData))
            return null;
        SQLMapData smd = (SQLMapData)md;
        smd.clearEndFightActions();
        try {
            getData("SELECT * FROM " + getTableName() + " WHERE map = " + id + ";", result -> {
                while (result.next()) {
                    smd.addEndFightAction(result.getInt("fighttype"), new Action(result.getInt("action"), result.getString("args"), result.getString("cond")));
                }
            });
        } catch (SQLException e) {
            super.sendError(e);
            Main.stop("Can't load endfight actions");
        }
        return null;
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
        return EndFightActionData.class;
    }
}
