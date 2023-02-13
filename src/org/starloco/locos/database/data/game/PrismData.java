package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.entity.Prism;
import org.starloco.locos.game.world.World;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PrismData extends FunctionDAO<Prism> {
    public PrismData(HikariDataSource dataSource) {
        super(dataSource, "prismes");
    }

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + ";");

            while (result.next()) {
                World.world.addPrisme(new Prism(result.getInt("id"), result.getByte("alignement"), result.getInt("level"),
                        result.getShort("carte"), result.getInt("celda"), result.getInt("honor"), result.getInt("area")));
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
    }

    @Override
    public Prism load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(Prism entity) {
        PreparedStatement p = null;
        boolean ok = true;
        try {
            p = getPreparedStatement("REPLACE INTO " + getTableName() + " VALUES(?,?,?,?,?,?,?);");
            p.setInt(1, entity.getId());
            p.setInt(2, entity.getAlignment());
            p.setInt(3, entity.getLevel());
            p.setInt(4, entity.getMap());
            p.setInt(5, entity.getCell());
            p.setInt(6, entity.getConquestArea());
            p.setInt(7, entity.getHonor());
            execute(p);
        } catch (SQLException e) {
            ok = false;
            super.sendError(e);
        } finally {
            close(p);
        }
        return ok;
    }

    @Override
    public void delete(Prism entity) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("DELETE FROM " + getTableName() + " WHERE id = ?;");
            p.setInt(1, entity.getId());
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    @Override
    public void update(Prism entity) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `level` = ?, `honor` = ?, `area`= ? WHERE `id` = ?;");
            p.setInt(1, entity.getLevel());
            p.setInt(2, entity.getHonor());
            p.setInt(3, entity.getConquestArea());
            p.setInt(4, entity.getId());
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    @Override
    public Class<?> getReferencedClass() {
        return PrismData.class;
    }
}
