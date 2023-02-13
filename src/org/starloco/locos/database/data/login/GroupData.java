package org.starloco.locos.database.data.login;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.command.administration.Group;
import org.starloco.locos.database.data.FunctionDAO;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GroupData extends FunctionDAO<Group> {

    public GroupData(HikariDataSource dataSource) {
        super(dataSource, "administration_groups");
    }


    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + ";");

            while (result.next()) {
                new Group(result.getInt("id"), result.getString("name"), result.getBoolean("isPlayer"), result.getString("commands"));
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
    }

    @Override
    public Group load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(Group entity) {
        throw new NotImplementedException();
    }

    @Override
    public void delete(Group entity) {
        throw new NotImplementedException();
    }

    @Override
    public void update(Group entity) {
        throw new NotImplementedException();
    }

    @Override
    public Class<?> getReferencedClass() {
        return GroupData.class;
    }
}
