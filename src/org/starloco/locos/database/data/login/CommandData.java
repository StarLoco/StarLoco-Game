package org.starloco.locos.database.data.login;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.command.administration.Command;
import org.starloco.locos.database.data.FunctionDAO;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CommandData extends FunctionDAO<Command> {

    public CommandData(HikariDataSource dataSource) {
        super(dataSource, "administration_commands");
    }

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + ";");
            while (result.next()) {
                new Command(result.getInt("id"), result.getString("command"), result.getString("args"), result.getString("description"));
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
    }

    @Override
    public Command load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(Command entity) {
        throw new NotImplementedException();
    }

    @Override
    public void delete(Command entity) {
        throw new NotImplementedException();
    }

    @Override
    public void update(Command entity) {
        throw new NotImplementedException();
    }

    @Override
    public Class<?> getReferencedClass() {
        return CommandData.class;
    }
}
