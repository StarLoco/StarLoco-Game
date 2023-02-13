package org.starloco.locos.database.data.login;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.entity.pet.PetEntry;
import org.starloco.locos.game.world.World;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PetData extends FunctionDAO<PetEntry> {

    public PetData(HikariDataSource dataSource) {
        super(dataSource, "world_pets");
    }

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + ";");

            while (result.next()) {
                World.world.addPetsEntry(new PetEntry(result.getInt("id"), result.getInt("template"), result.getLong("lastEatDate"), result.getInt("quantityEat"), result.getInt("pdv"), result.getInt("corpulence"), (result.getInt("isEPO") == 1)));
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
    }

    @Override
    public PetEntry load(int id) {
        throw new NotImplementedException();
    }

    //TODO: Change the insert to set the id of the entity
    @Override
    public boolean insert(PetEntry entity) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("INSERT INTO " + getTableName() + "(`id`, `template`, `lastEatDate`, `quantityEat`, `pdv`, `corpulence`, `isEPO`) VALUES (?, ?, ?, ?, ?, ?, ?);");
            p.setInt(1, entity.getObjectId());
            p.setInt(2, entity.getTemplate());
            p.setLong(3, entity.getLastEatDate());
            p.setInt(4, 0);
            p.setInt(5, 10);
            p.setInt(6, 0);
            p.setInt(7, 0);
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
        return true;
    }

    @Override
    public void delete(PetEntry entity) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("DELETE FROM " + getTableName() + " WHERE `id` = ?");
            p.setInt(1, entity.getObjectId());
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    @Override
    public void update(PetEntry entity) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `lastEatDate` = ?, `quantityEat` = ?, `pdv` = ?, `corpulence` = ?, `isEPO` = ? WHERE `id` = ?;");
            p.setLong(1, entity.getLastEatDate());
            p.setInt(2, entity.getQuaEat());
            p.setInt(3, entity.getPdv());
            p.setInt(4, entity.getCorpulence());
            p.setInt(5, (entity.getIsEupeoh() ? 1 : 0));
            p.setInt(6, entity.getObjectId());
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    @Override
    public Class<?> getReferencedClass() {
        return PetData.class;
    }
}
