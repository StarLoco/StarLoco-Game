package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Main;
import org.starloco.locos.item.ItemTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ObjectTemplateData extends FunctionDAO<ItemTemplate> {
    public ObjectTemplateData(HikariDataSource dataSource) {
        super(dataSource, "item_template");
    }

    @Override
    public void loadFully() {
        try {
            getData("SELECT * FROM " + getTableName() + ";", result -> {
                while (result.next()) {
                    World.world.addObjTemplate(new ItemTemplate(result.getInt("id"), result.getString("statsTemplate"),
                        result.getString("name"), result.getInt("type"), result.getInt("level"), result.getInt("pod"),
                        result.getInt("prix"), result.getInt("panoplie"), result.getString("conditions"), result.getString("armesInfos"),
                        result.getInt("sold"), result.getInt("avgPrice"), result.getInt("points"), result.getInt("newPrice")));
                }
            });
        } catch (SQLException e) {
            super.sendError(e);
            Main.stop("Can't load objects templates");
        }
    }

    @Override
    public ItemTemplate load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(ItemTemplate entity) {
        throw new NotImplementedException();
    }

    @Override
    public void delete(ItemTemplate entity) {
        throw new NotImplementedException();
    }

    @Override
    public void update(ItemTemplate entity) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET sold = ?,avgPrice = ? WHERE id = ?");
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
