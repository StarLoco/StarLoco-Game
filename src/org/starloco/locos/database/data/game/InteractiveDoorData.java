package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.area.map.entity.InteractiveDoor;
import org.starloco.locos.database.data.FunctionDAO;

import java.sql.SQLException;

public class InteractiveDoorData extends FunctionDAO<Object> {

    public InteractiveDoorData(HikariDataSource dataSource) {
        super(dataSource, "interactive_doors");
    }

    @Override
    public void loadFully() {
        try {
            getData("SELECT * FROM " + getTableName() + ";", result -> {
                while (result.next())
                    new InteractiveDoor(result.getString("maps"), result.getString("doorsEnable"), result.getString("doorsDisable"),
                            result.getString("cellsEnable"), result.getString("cellsDisable"), result.getString("requiredCells"),
                            result.getString("button"), result.getShort("time"));
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
        return InteractiveDoorData.class;
    }
}
