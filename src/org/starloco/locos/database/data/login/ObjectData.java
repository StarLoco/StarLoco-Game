package org.starloco.locos.database.data.login;

import com.mysql.jdbc.Statement;
import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.object.ObjectTemplate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ObjectData extends FunctionDAO<GameObject> {

    public ObjectData(HikariDataSource dataSource) {
        super(dataSource, "world_objects");
    }

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + ";");

            while (result != null && result.next()) {
                int id = result.getInt("id");
                int template = result.getInt("template");
                int quantity = result.getInt("quantity");
                int position = result.getInt("position");
                String stats = result.getString("stats");
                int puit = result.getInt("puit");

                if(quantity == 0) continue;
                GameObject object = World.world.newObjet(id, template, quantity, position, stats, puit);
                if(object.getTemplate() == null)
                    this.delete(object);
                else
                    World.world.addGameObject(object);
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
    }

    @Override
    public GameObject load(int id) {
        ResultSet result = null;
        GameObject object = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + " WHERE `id` IN (" + id + ");");

            while (result != null && result.next()) {
                int template = result.getInt("template");
                int quantity = result.getInt("quantity");
                int position = result.getInt("position");
                String stats = result.getString("stats");
                int puit = result.getInt("puit");

                if(quantity > 0) {
                    object = World.world.newObjet(result.getInt("id"), template, quantity, position, stats, puit);
                    World.world.addGameObject(object);
                }
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
        return object;
    }

    @Override
    public boolean insert(GameObject entity) {
        PreparedStatement statement = null;
        boolean ok = true;
        try {
            statement = this.getConnection().prepareStatement("INSERT INTO " + getTableName() + " (`template`, `quantity`, `position`, `stats`, `puit`) VALUES (?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, entity.getTemplate().getId());
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
    public void delete(GameObject entity) {
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
    public void update(GameObject entity) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `template` = ?, `quantity` = ?, `position` = ?, `puit` = ?, `stats` = ? WHERE `id` = ?;");
            p.setInt(1, entity.getTemplate().getId());
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
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + " WHERE `id` IN (" + ids + ");");

            while (result != null && result.next()) {
                int quantity = result.getInt("quantity");

                if(quantity > 0) {
                    GameObject object = World.world.newObjet(result.getInt("id"), result.getInt("template"), quantity,
                            result.getInt("position"), result.getString("stats"), result.getInt("puit"));
                    World.world.addGameObject(object);
                }
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
    }
}
