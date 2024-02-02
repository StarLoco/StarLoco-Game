package org.starloco.locos.database.data.login;

import com.mysql.jdbc.Statement;
import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.item.FullItem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ObjectData extends FunctionDAO<FullItem> {

    public ObjectData(HikariDataSource dataSource) {
        super(dataSource, "world_objects");
    }

    @Override
    public void loadFully() {
        try {
            getData("SELECT * FROM " + getTableName() + ";", result -> {
                while (result != null && result.next()) {
                    int id = result.getInt("id");
                    int template = result.getInt("template");
                    int quantity = result.getInt("quantity");
                    int position = result.getInt("position");
                    String stats = result.getString("stats");
                    int puit = result.getInt("puit");

                    if (quantity == 0) continue;
                    FullItem object = World.world.newObjet(id, template, quantity, position, stats, puit);
                    if (object.template() == null)
                        this.delete(object);
                    else
                        World.world.addGameObject(object);
                }
            });
        } catch (SQLException e) {
            super.sendError(e);
        }
    }

    @Override
    public FullItem load(int id) {
        try {
            return getData("SELECT * FROM " + getTableName() + " WHERE `id` IN (" + id + ");", result -> {
                if(!result.next()) return null;
                int template = result.getInt("template");
                int quantity = result.getInt("quantity");
                int position = result.getInt("position");
                String stats = result.getString("stats");
                int puit = result.getInt("puit");

                if (quantity > 0) {
                    FullItem object = World.world.newObjet(result.getInt("id"), template, quantity, position, stats, puit);
                    World.world.addGameObject(object);
                    return object;
                }

                return null;
            });
        } catch (SQLException e) {
            super.sendError(e);
        }
        return null;
    }

    @Override
    public boolean insert(FullItem entity) {
        PreparedStatement statement = null;
        boolean ok = true;
        try {
            statement = this.getConnection().prepareStatement("INSERT INTO " + getTableName() + " (`template`, `quantity`, `position`, `stats`, `puit`) VALUES (?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, entity.template().getId());
            statement.setInt(2, entity.getQuantity());
            statement.setInt(3, entity.getPosition());
            statement.setString(4, entity.parseToSave());
            statement.setInt(5, entity.getPuit());
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                ok = false;
            } else {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        entity.setId(generatedKeys.getInt(1));
                    } else {
                        ok = false;
                    }
                }
            }
        } catch (SQLException e) {
            super.sendError(e);
            ok = false;
        } finally {
            close(statement);
        }
        return ok;
    }

    @Override
    public void delete(FullItem entity) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("DELETE FROM " + getTableName() + " WHERE id = ?;");
            p.setInt(1, entity.getGuid());
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    @Override
    public void update(FullItem entity) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `template` = ?, `quantity` = ?, `position` = ?, `puit` = ?, `stats` = ? WHERE `id` = ?;");
            p.setInt(1, entity.template().getId());
            p.setInt(2, entity.getQuantity());
            p.setInt(3, entity.getPosition());
            p.setInt(4, entity.getPuit());
            p.setString(5, entity.parseToSave());
            p.setInt(6, entity.getGuid());
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    @Override
    public Class<?> getReferencedClass() {
        return ObjectData.class;
    }

    public void loads(String ids) {
        try {
            getData("SELECT * FROM " + getTableName() + " WHERE `id` IN (" + ids + ");", result -> {
                while (result != null && result.next()) {
                    int quantity = result.getInt("quantity");

                    if (quantity > 0) {
                        FullItem object = World.world.newObjet(result.getInt("id"), result.getInt("template"), quantity,
                                result.getInt("position"), result.getString("stats"), result.getInt("puit"));
                        World.world.addGameObject(object);
                    }
                }
            });
        } catch (SQLException e) {
            super.sendError(e);
        }
    }
}
