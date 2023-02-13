package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.entity.pet.Pet;
import org.starloco.locos.game.world.World;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PetTemplateData extends FunctionDAO<Pet> {
    public PetTemplateData(HikariDataSource dataSource) {
        super(dataSource, "pets");
    }

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + ";");
            while (result.next()) {
                World.world.addPets(new Pet(result.getInt("TemplateID"), result.getInt("Type"), result.getString("Gap"),
                        result.getString("StatsUp"), result.getInt("Max"), result.getInt("Gain"), result.getInt("DeadTemplate"), result.getInt("Epo"), result.getString("jet")));
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
    }

    @Override
    public Pet load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(Pet entity) {
        throw new NotImplementedException();
    }

    @Override
    public void delete(Pet entity) {
        throw new NotImplementedException();
    }

    @Override
    public void update(Pet entity) {
        throw new NotImplementedException();
    }

    @Override
    public Class<?> getReferencedClass() {
        return PetTemplateData.class;
    }
}
