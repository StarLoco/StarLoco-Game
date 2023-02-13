package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Main;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.object.ObjectTemplate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ObjectTemplateData extends FunctionDAO<ObjectTemplate> {
    public ObjectTemplateData(HikariDataSource dataSource) {
        super(dataSource, "item_template");
    }

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + ";");
            while (result.next()) {
                ObjectTemplate template = World.world.getObjTemplate(result.getInt("id"));
                if (template != null) {
                    template.setInfos(result.getString("statsTemplate"), result.getString("name"), result.getInt("type"),
                            result.getInt("level"), result.getInt("pod"), result.getInt("prix"), result.getInt("panoplie"),
                            result.getString("conditions"), result.getString("armesInfos"), result.getInt("sold"), result.getInt("avgPrice"),
                            result.getInt("points"), result.getInt("newPrice"));
                } else {
                    World.world.addObjTemplate(new ObjectTemplate(result.getInt("id"), result.getString("statsTemplate"),
                            result.getString("name"), result.getInt("type"), result.getInt("level"), result.getInt("pod"),
                            result.getInt("prix"), result.getInt("panoplie"), result.getString("conditions"), result.getString("armesInfos"),
                            result.getInt("sold"), result.getInt("avgPrice"), result.getInt("points"), result.getInt("newPrice")));
                }
            }
        } catch (SQLException e) {
            super.sendError(e);
            Main.stop("Can't load objects templates");
        } finally {
            close(result);
        }
    }

    @Override
    public ObjectTemplate load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(ObjectTemplate entity) {
        throw new NotImplementedException();
    }

    @Override
    public void delete(ObjectTemplate entity) {
        throw new NotImplementedException();
    }

    @Override
    public void update(ObjectTemplate entity) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE `" + getTableName() + "` SET sold = ?,avgPrice = ? WHERE id = ?");
            p.setLong(1, entity.getSold());
            p.setInt(2, entity.getAvgPrice());
            p.setInt(3, entity.getId());
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    @Override
    public Class<?> getReferencedClass() {
        return ObjectTemplateData.class;
    }
}
