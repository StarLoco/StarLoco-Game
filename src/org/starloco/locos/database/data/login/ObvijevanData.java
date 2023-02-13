package org.starloco.locos.database.data.login;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.util.Pair;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.data.FunctionDAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ObvijevanData extends FunctionDAO<Pair<Integer, Integer>> {

    public ObvijevanData(HikariDataSource dataSource) {
        super(dataSource, "world_obvijevans");
    }

    @Override
    public void loadFully() {
        throw new NotImplementedException();
    }

    @Override
    public Pair<Integer, Integer> load(int id) {
        ResultSet result = null;
        int template = -1;
        try {
            result = getData("SELECT * FROM " + getTableName() + " WHERE `id` = '" + id + "';");

            if (result.next()) {
                template = result.getInt("template");
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
        return new Pair<>(template, template);
    }

    /**
     * @param entity <objectId, obvijevanTemplateId>
     */
    @Override
    public boolean insert(Pair<Integer, Integer> entity) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("INSERT INTO " + getTableName() + "(`id`, `template`) VALUES(?, ?);");
            p.setInt(1, entity.getKey());
            p.setInt(2, entity.getValue());
            execute(p);
        } catch (Exception e) {
            super.sendError(e);
        } finally {
            close(p);
        }
        return true;
    }

    @Override
    public void delete(Pair<Integer, Integer> entity) {
        PreparedStatement ps = null;
        try {
            ps = getPreparedStatement("DELETE FROM " + getTableName() + " WHERE id = '" + entity.getKey() + "';");
            execute(ps);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(ps);
        }
    }

    @Override
    public void update(Pair<Integer, Integer> entity) {
        throw new NotImplementedException();
    }

    @Override
    public Class<?> getReferencedClass() {
        return ObvijevanData.class;
    }
}
