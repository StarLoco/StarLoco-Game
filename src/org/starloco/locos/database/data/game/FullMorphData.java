package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.game.world.World;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FullMorphData extends FunctionDAO<Object> {
    public FullMorphData(HikariDataSource dataSource) {
        super(dataSource, "full_morphs");
    }

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + ";");

            while (result.next()) {
                String[] args = null;
                if (!result.getString("args").equals("0")) {
                    args = result.getString("args").split("@")[1].split(",");
                }

                World.world.addFullMorph(result.getInt("id"), result.getString("name"), result.getInt("gfxId"), result.getString("spells"), args);
            }
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
        return FullMorphData.class;
    }
}
