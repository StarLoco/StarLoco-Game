package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.area.map.entity.Tutorial;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Main;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TutorialData extends FunctionDAO<Tutorial> {
    public TutorialData(HikariDataSource dataSource) {
        super(dataSource, "tutoriel");
    }

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + ";");
            while (result.next()) {
                int id = result.getInt("id");
                String start = result.getString("start");
                String reward = result.getString("reward1") + "$" + result.getString("reward2") + "$" + result.getString("reward3") + "$" + result.getString("reward4");
                String end = result.getString("end");
                World.world.addTutorial(new Tutorial(id, reward, start, end));
            }
        } catch (SQLException e) {
            super.sendError(e);
            Main.stop("Can't load tutorials");
        } finally {
            close(result);
        }
    }

    @Override
    public Tutorial load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(Tutorial entity) {
        throw new NotImplementedException();
    }

    @Override
    public void delete(Tutorial entity) {
        throw new NotImplementedException();
    }

    @Override
    public void update(Tutorial entity) {
        throw new NotImplementedException();
    }

    @Override
    public Class<?> getReferencedClass() {
        return TutorialData.class;
    }
}
