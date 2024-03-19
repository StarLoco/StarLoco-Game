package org.starloco.locos.database.legacydata.login;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.util.Pair;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.legacydata.FunctionDAO;

import java.sql.PreparedStatement;
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
        int template = -1;
        try {
            template = getData("SELECT * FROM " + getTableName() + " WHERE `id` = '" + id + "';", result -> result.next() ? result.getInt("template") : -1);
        } catch (SQLException e) {
            super.sendError(e);
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
            p.setInt(1, entity.getFirst());
            p.setInt(2, entity.getSecond());
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
            ps = getPreparedStatement("DELETE FROM " + getTableName() + " WHERE id = '" + entity.getFirst() + "';");
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
