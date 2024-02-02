package org.starloco.locos.database.data.game;

import com.mysql.jdbc.Statement;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.hdv.BigStore;
import org.starloco.locos.hdv.BigStoreListing;
import org.starloco.locos.item.FullItem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BigStoreListingData extends FunctionDAO<BigStoreListing> {
    public BigStoreListingData(HikariDataSource dataSource) {
        super(dataSource, "hdvs_items");
    }

    @Override
    public void loadFully() {
        try {
            getData("SELECT * FROM " + getTableName() + ";", result -> {
                while (result.next()) {
                    BigStore bigStore = World.world.getHdv(result.getInt("map"));
                    if (bigStore != null) {
                        FullItem object = World.world.getGameObject(result.getInt("itemID"));
                        if (object == null) {
                            this.delete(new BigStoreListing(result.getInt("id"), 0, (byte) 0, 0, null));
                            continue;
                        }
                        BigStoreListing entry = new BigStoreListing(result.getInt("id"), result.getInt("price"), result.getByte("count"), result.getInt("ownerGuid"), object);
                        bigStore.addEntry(entry);
                    }
                }
            });
        } catch (SQLException e) {
            super.sendError(e);
        }
    }

    @Override
    public BigStoreListing load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(BigStoreListing entity) {
        PreparedStatement p = null;
        boolean ok = true;
        try {
            p = this.getConnection().prepareStatement("INSERT INTO " + getTableName() + " (`map`,`ownerGuid`,`price`,`count`,`itemID`) VALUES(?,?,?,?,?);", Statement.RETURN_GENERATED_KEYS);
            p.setInt(1, entity.getHdvId());
            p.setInt(2, entity.getOwner());
            p.setInt(3, entity.getPrice());
            p.setInt(4, entity.getLotSize().value);
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
            DatabaseManager.get(ObjectTemplateData.class).update(entity.getGameObject().template());
        } catch (SQLException e) {
            ok = false;
            super.sendError(e);
        } finally {
            close(p);
        }
        return ok;
    }

    @Override
    public void delete(BigStoreListing entity) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("DELETE FROM " + getTableName() + " WHERE `id` = ?;");
            p.setInt(1, entity.getId());
            execute(p);

            FullItem object = entity.getGameObject();
            if (object != null) {
                ((ObjectTemplateData) DatabaseManager.get(ObjectTemplateData.class)).update(object.template());
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    @Override
    public void update(BigStoreListing entity) {
        throw new NotImplementedException();
    }

    @Override
    public Class<?> getReferencedClass() {
        return BigStoreListingData.class;
    }
}
