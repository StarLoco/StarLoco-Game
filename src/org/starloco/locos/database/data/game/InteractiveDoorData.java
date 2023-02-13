package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.area.map.entity.InteractiveDoor;
import org.starloco.locos.area.map.entity.InteractiveObject.InteractiveObjectTemplate;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.object.ObjectTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractiveDoorData extends FunctionDAO<Object> {

    public InteractiveDoorData(HikariDataSource dataSource) {
        super(dataSource, "interactive_doors");
    }

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + ";");
            while (result.next())
                new InteractiveDoor(result.getString("maps"), result.getString("doorsEnable"), result.getString("doorsDisable"),
                        result.getString("cellsEnable"), result.getString("cellsDisable"), result.getString("requiredCells"),
                        result.getString("button"), result.getShort("time"));
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
        return InteractiveDoorData.class;
    }
}
