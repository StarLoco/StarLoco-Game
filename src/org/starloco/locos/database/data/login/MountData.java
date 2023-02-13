package org.starloco.locos.database.data.login;

import com.mysql.jdbc.Statement;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.client.Player;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.entity.mount.Mount;
import org.starloco.locos.game.world.World;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MountData extends FunctionDAO<Mount> {

    public MountData(HikariDataSource dataSource) {
        super(dataSource, "world_mounts");
    }

    @Override
    public void loadFully() {
        throw new NotImplementedException();
    }

    @Override
    public Mount load(int id) {
        ResultSet result = null;
        Mount mount = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + " WHERE `id` = " + id + ";");

            while (result.next()) {
                mount = new Mount(result.getInt("id"), result.getInt("color"), result.getInt("sex"), result.getInt("amour"), result.getInt("endurance"), result.getInt("level"), result.getLong("xp"),
                        result.getString("name"), result.getInt("fatigue"), result.getInt("energy"), result.getInt("reproductions"), result.getInt("maturity"), result.getInt("serenity"), result.getString("objects"),
                        result.getString("ancestors"), result.getString("capacitys"), result.getInt("size"), result.getInt("cell"), result.getShort("map"), result.getInt("owner"), result.getInt("orientation"),
                        result.getLong("fecundatedDate"), result.getInt("couple"), result.getInt("savage"));
                World.world.addMount(mount);
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
        return mount;
    }

    //TODO: Change the insert to set the id of the entity
    @Override
    public boolean insert(Mount entity) {
        PreparedStatement statement = null;
        boolean ok = true;
        try {
            statement = this.getConnection().prepareStatement("INSERT INTO " + getTableName() + "(`color`, `sex`, `name`, `xp`, `level`, `endurance`, `amour`, `maturity`, `serenity`, `reproductions`, `fatigue`, `energy`," +
                    "`objects`, `ancestors`, `capacitys`, `size`, `map`, `cell`, `owner`, `orientation`, `fecundatedDate`, `couple`, `savage`) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, entity.getColor());
            statement.setInt(2, entity.getSex());
            statement.setString(3, entity.getName());
            statement.setLong(4, entity.getExp());
            statement.setInt(5, entity.getLevel());
            statement.setInt(6, entity.getEndurance());
            statement.setInt(7, entity.getAmour());
            statement.setInt(8, entity.getMaturity());
            statement.setInt(9, entity.getState());
            statement.setInt(10, entity.getReproduction());
            statement.setInt(11, entity.getFatigue());
            statement.setInt(12, entity.getEnergy());
            statement.setString(13, entity.parseObjectsToString());
            statement.setString(14, entity.getAncestors());
            statement.setString(15, entity.parseCapacitysToString());
            statement.setInt(16, entity.getSize());
            statement.setInt(17, entity.getMapId());
            statement.setInt(18, entity.getCellId());
            statement.setInt(19, entity.getOwner());
            statement.setInt(20, entity.getOrientation());
            statement.setLong(21, entity.getFecundatedDate());
            statement.setInt(22, entity.getCouple());
            statement.setInt(23, entity.getSavage());
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
    public void delete(Mount entity) {
        if(entity  == null) return;
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("DELETE FROM " + getTableName() + " WHERE `id` = ?;");
            if(p != null) {
                p.setInt(1, entity.getId());
                execute(p);
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    public void delete(Player player) {
        this.delete(player.getMount());
        World.world.delDragoByID(player.getMount().getId());
        player.setMountGiveXp(0);
        player.setMount(null);
        ((PlayerData) DatabaseManager.get(PlayerData.class)).update(player);
    }

    @Override
    public void update(Mount entity) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `name` = ?, `xp` = ?, `level` = ?, `endurance` = ?, `amour` = ?, `maturity` = ?, `serenity` = ?, `reproductions` = ?," +
                    "`fatigue` = ?, `energy` = ?, `ancestors` = ?, `objects` = ?, `owner` = ?, `capacitys` = ?, `size` = ?, `cell` = ?, `map` = ?," +
                    " `orientation` = ?, `fecundatedDate` = ?, `couple` = ? WHERE `id` = ?;");
            if(p != null) {
                p.setString(1, entity.getName());
                p.setLong(2, entity.getExp());
                p.setInt(3, entity.getLevel());
                p.setInt(4, entity.getEndurance());
                p.setInt(5, entity.getAmour());
                p.setInt(6, entity.getMaturity());
                p.setInt(7, entity.getState());
                p.setInt(8, entity.getReproduction());
                p.setInt(9, entity.getFatigue());
                p.setInt(10, entity.getEnergy());
                p.setString(11, entity.getAncestors());
                p.setString(12, entity.parseObjectsToString());
                p.setInt(13, entity.getOwner());
                p.setString(14, entity.parseCapacitysToString());
                p.setInt(15, entity.getSize());
                p.setInt(16, entity.getCellId());
                p.setInt(17, entity.getMapId());
                p.setInt(18, entity.getOrientation());
                p.setLong(19, entity.getFecundatedDate());
                p.setInt(20, entity.getCouple());
                p.setInt(21, entity.getId());
                execute(p);
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    @Override
    public Class<?> getReferencedClass() {
        return MountData.class;
    }
}
