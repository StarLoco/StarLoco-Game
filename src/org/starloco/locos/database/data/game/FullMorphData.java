package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.starloco.locos.client.other.MorphMode;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.game.world.World;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

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
                int[] args = {};
                String temp = result.getString("args");
                if (StringUtils.isNotBlank(temp)) {
                    args = Arrays.stream(temp.split(",")).mapToInt(Integer::parseInt).toArray();
                }

                MorphMode mode = new MorphMode(result.getInt("id"), result.getString("name"), result.getInt("gfxId"), result.getString("spells"), args);
                World.world.getMorphModes().put(mode.getId(), mode);
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
