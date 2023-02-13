package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.area.SubArea;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.game.world.World;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SubAreaData extends FunctionDAO<SubArea> {

    public SubAreaData(HikariDataSource dataSource) {
        super(dataSource, "subarea_data");
    }

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + ";");
            while (result.next()) {
                int id = result.getInt("id");
                SubArea subArea = World.world.getSubArea(id);

                if(subArea != null) {
                    subArea.setAlignment(result.getByte("alignement"));
                    subArea.setPrism(World.world.getPrisme(result.getInt("Prisme")));
                    subArea.setConquerable(result.getInt("conquistable") == 0);
                }
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
    }

    @Override
    public SubArea load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(SubArea entity) {
        throw new NotImplementedException();
    }

    @Override
    public void delete(SubArea entity) {
        throw new NotImplementedException();
    }

    @Override
    public void update(SubArea subarea) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `alignement` = ?, `prisme` = ?, `conquistable` = ? WHERE `id` = ?");
            p.setInt(1, subarea.getAlignment());
            p.setInt(2, subarea.getPrism() == null ? 0 : subarea.getPrism().getId());
            p.setInt(3, subarea.getConquerable() ? 0 : 1);
            p.setInt(4, subarea.getId());
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    @Override
    public Class<?> getReferencedClass() {
        return SubAreaData.class;
    }
}
