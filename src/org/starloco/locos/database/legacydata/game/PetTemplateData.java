package org.starloco.locos.database.legacydata.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.legacydata.FunctionDAO;
import org.starloco.locos.entity.pet.Pet;
import org.starloco.locos.game.world.World;

import java.sql.SQLException;

public class PetTemplateData extends FunctionDAO<Pet> {
    public PetTemplateData(HikariDataSource dataSource) {
        super(dataSource, "pets");
    }

    @Override
    public void loadFully() {
        try {
            getData("SELECT * FROM " + getTableName() + ";", result -> {
                while (result.next()) {
                    World.world.addPets(new Pet(result.getInt("TemplateID"), result.getInt("Type"), result.getString("Gap"),
                            result.getString("StatsUp"), result.getInt("Max"), result.getInt("Gain"), result.getInt("DeadTemplate"), result.getInt("Epo"), result.getString("jet")));
                }
            });
        } catch (SQLException e) {
            super.sendError(e);
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
