package org.starloco.locos.database.legacydata.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.legacydata.FunctionDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Main;
import org.starloco.locos.object.ObjectAction;
import org.starloco.locos.object.ObjectTemplate;

import java.sql.SQLException;

public class ObjectActionData extends FunctionDAO<ObjectAction> {
    public ObjectActionData(HikariDataSource dataSource) {
        super(dataSource, "objectsactions");
    }

    @Override
    public void loadFully() {
        try {
            World.world.getObjectsTemplates().values().forEach(template -> template.getOnUseActions().clear());
            getData("SELECT * FROM " + getTableName() + ";", result -> {
                while (result.next()) {
                    int id = result.getInt("template");
                    ObjectTemplate template = World.world.getObjTemplate(id);

                    if (template != null)
                        template.addAction(new ObjectAction(result.getString("type"), result.getString("args"), ""));
                }
            });
        } catch (SQLException e) {
            super.sendError(e);
            Main.stop("Can't load objects actions");
        }
    }

    @Override
    public ObjectAction load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(ObjectAction entity) {
        throw new NotImplementedException();
    }

    @Override
    public void delete(ObjectAction entity) {
        throw new NotImplementedException();
    }

    @Override
    public void update(ObjectAction entity) {
        throw new NotImplementedException();
    }

    @Override
    public Class<?> getReferencedClass() {
        return ObjectActionData.class;
    }
}
