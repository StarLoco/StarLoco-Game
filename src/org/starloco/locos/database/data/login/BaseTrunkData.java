package org.starloco.locos.database.data.login;

import com.mysql.jdbc.Statement;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.area.map.entity.Trunk;
import org.starloco.locos.client.Account;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.database.data.game.BankData;
import org.starloco.locos.database.data.game.TrunkData;
import org.starloco.locos.game.world.World;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BaseTrunkData extends FunctionDAO<Trunk> {

    public BaseTrunkData(HikariDataSource dataSource)
	{
		super(dataSource, "world_base_trunks");
	}

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + ";");
            while (result.next()) {
                int id = result.getInt("id");

                if(World.world.getTrunk(id) == null) {
                    Trunk trunk = new Trunk(id, result.getInt("id_house"), result.getShort("mapid"), result.getInt("cellid"));
                    World.world.addTrunk(trunk);
                    ((TrunkData) DatabaseManager.get(TrunkData.class)).exist(trunk);
                }
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
    }

    @Override
    public Trunk load(int id) {
        throw new NotImplementedException();
    }

    //TODO: Change insert to set the id of the entity
    @Override
    public boolean insert(Trunk entity) {
        PreparedStatement statement = null;
        boolean ok = true;
        try {
            for(Trunk trunk : World.world.getTrunks().values()) {
                if(trunk.getHouseId() == entity.getHouseId() && trunk.getMapId() == entity.getMapId() && trunk.getCellId() == entity.getCellId()) {
                    entity = trunk;
                    ok = false;
                }
            }


            if(ok) {
                boolean found = false;
                ResultSet result = null;
                try {
                    result = getData("SELECT * FROM " + getTableName() + ";");
                    while (result.next()) {
                        if(result.getInt("id_house") == entity.getHouseId() && result.getShort("mapid") == entity.getMapId() && result.getShort("cellid") == entity.getCellId()) {
                            entity.setId(result.getInt("id"));
                            ((TrunkData) DatabaseManager.get(TrunkData.class)).exist(entity);
                            found = true;
                            break;
                        }
                    }
                } catch (SQLException e) {
                    super.sendError(e);
                } finally {
                    close(result);
                }

                if(!found) {
                    statement = this.getConnection().prepareStatement("INSERT INTO " + getTableName() + " (`id_house`, `mapid`, `cellid`) VALUES (?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
                    statement.setInt(1, entity.getHouseId());
                    statement.setInt(2, entity.getMapId());
                    statement.setInt(3, entity.getCellId());
                    int affectedRows = statement.executeUpdate();

                    if (affectedRows == 0) {
                        ok = false;
                    } else {
                        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                entity.setId(generatedKeys.getInt(1));
                                ((TrunkData) DatabaseManager.get(TrunkData.class)).insert(entity);
                            } else {
                                ok = false;
                            }
                        }
                    }
                }
            } else {
                ok = true;
                ((TrunkData) DatabaseManager.get(TrunkData.class)).insert(entity);
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
    public void delete(Trunk entity) {
        throw new NotImplementedException();
    }

    @Override
    public void update(Trunk entity) {
        throw new NotImplementedException();
    }

    @Override
    public Class<?> getReferencedClass() {
        return BaseTrunkData.class;
    }
}
