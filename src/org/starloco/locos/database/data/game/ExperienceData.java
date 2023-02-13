package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Main;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ExperienceData extends FunctionDAO<World.ExpLevel> {
    public ExperienceData(HikariDataSource dataSource) {
        super(dataSource, "experience");
    }

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + ";");
            while (result.next()) {
                World.ExpLevel exp = new World.ExpLevel(result.getInt("lvl"), result.getLong("perso"), result.getInt("metier"),
                        result.getInt("dinde"), result.getInt("pvp"), result.getLong("tourmenteurs"), result.getLong("bandits"));
                World.world.addExpLevel(result.getInt("lvl"), exp);
            }
        } catch (SQLException e) {
            super.sendError(e);
            Main.stop("Can't load experiences");
        } finally {
            close(result);
        }
    }

    @Override
    public World.ExpLevel load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(World.ExpLevel entity) {
        throw new NotImplementedException();
    }

    @Override
    public void delete(World.ExpLevel entity) {
        throw new NotImplementedException();
    }

    @Override
    public void update(World.ExpLevel entity) {
        throw new NotImplementedException();
    }

    @Override
    public Class<?> getReferencedClass() {
        return ExperienceData.class;
    }
}
