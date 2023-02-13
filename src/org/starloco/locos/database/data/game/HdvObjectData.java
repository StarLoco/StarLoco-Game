package org.starloco.locos.database.data.game;

import com.mysql.jdbc.Statement;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.hdv.Hdv;
import org.starloco.locos.hdv.HdvEntry;
import org.starloco.locos.object.GameObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HdvObjectData extends FunctionDAO<HdvEntry> {
    public HdvObjectData(HikariDataSource dataSource) {
        super(dataSource, "hdvs_items");
    }

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + ";");
            while (result.next()) {
                Hdv hdv = World.world.getHdv(result.getInt("map"));
                if (hdv != null) {
                    GameObject object = World.world.getGameObject(result.getInt("itemID"));
                    if (object == null) {
                        this.delete(new HdvEntry(result.getInt("id"), 0, (byte) 0, 0, null));
                        continue;
                    }
                    HdvEntry entry = new HdvEntry(result.getInt("id"), result.getInt("price"), result.getByte("count"), result.getInt("ownerGuid"), object);
                    hdv.addEntry(entry, true);
                    World.world.setNextObjectHdvId(result.getInt("id"));
                }
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
    }

    @Override
    public HdvEntry load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(HdvEntry entity) {
        PreparedStatement p = null;
        boolean ok = true;
        try {
            p = this.getConnection().prepareStatement("INSERT INTO " + getTableName() + " (`map`,`ownerGuid`,`price`,`count`,`itemID`) VALUES(?,?,?,?,?);", Statement.RETURN_GENERATED_KEYS);
            p.setInt(1, entity.getHdvId());
            p.setInt(2, entity.getOwner());
            p.setInt(3, entity.getPrice());
            p.setInt(4, entity.getAmount(false));
            p.setInt(5, entity.getGameObject().getGuid());

            int affectedRows = p.executeUpdate();

            if (affectedRows == 0) {
                ok = false;
            } else {
                try (ResultSet generatedKeys = p.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        entity.setId(generatedKeys.getInt(1));
                    } else {
                        ok = false;
                    }
                }
            }
            ((ObjectTemplateData) DatabaseManager.get(ObjectTemplateData.class)).update(entity.getGameObject().getTemplate());
        } catch (SQLException e) {
            ok = false;
            super.sendError(e);
        } finally {
            close(p);
        }
        return ok;
    }

    @Override
    public void delete(HdvEntry entity) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("DELETE FROM " + getTableName() + " WHERE `id` = ?;");
            p.setInt(1, entity.getId());
            execute(p);

            GameObject object = entity.getGameObject();
            if (object != null) {
                ((ObjectTemplateData) DatabaseManager.get(ObjectTemplateData.class)).update(object.getTemplate());
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    @Override
    public void update(HdvEntry entity) {
        throw new NotImplementedException();
    }

    @Override
    public Class<?> getReferencedClass() {
        return HdvObjectData.class;
    }
}
