package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.area.map.entity.Animation;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.game.world.World;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AnimationData extends FunctionDAO<Animation> {
    public AnimationData(HikariDataSource dataSource) {
        super(dataSource, "animations");
    }

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + ";");
            while (result.next()) {
                World.world.addAnimation(new Animation(result.getInt("guid"), result.getInt("id"), result.getString("nom"), result.getInt("area"), result.getInt("action"), result.getInt("size")));
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
    }

    @Override
    public Animation load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(Animation entity) {
        throw new NotImplementedException();
    }

    @Override
    public void delete(Animation entity) {
        throw new NotImplementedException();
    }

    @Override
    public void update(Animation entity) {
        throw new NotImplementedException();
    }

    @Override
    public Class<?> getReferencedClass() {
        return AnimationData.class;
    }
}
