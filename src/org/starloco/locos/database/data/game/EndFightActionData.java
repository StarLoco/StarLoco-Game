package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Main;
import org.starloco.locos.other.Action;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EndFightActionData extends FunctionDAO<Object> {
    public EndFightActionData(HikariDataSource dataSource) {
        super(dataSource, "endfight_action");
    }

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            World.world.getMaps().forEach(map -> map.getEndFightAction().clear());
            result = getData("SELECT * FROM " + getTableName() + ";");
            while (result.next()) {
                GameMap map = World.world.getMap(result.getShort("map"));
                if (map == null)
                    continue;
                map.addEndFightAction(result.getInt("fighttype"), new Action(result.getInt("action"), result.getString("args"), result.getString("cond"), null));
            }
        } catch (SQLException e) {
            super.sendError(e);
            Main.stop("Can't load endfght actions");
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
        return EndFightActionData.class;
    }
}
