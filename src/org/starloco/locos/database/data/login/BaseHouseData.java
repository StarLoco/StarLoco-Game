package org.starloco.locos.database.data.login;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.area.map.entity.House;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.game.world.World;

import java.sql.SQLException;

public class BaseHouseData extends FunctionDAO<House> {
	public BaseHouseData(HikariDataSource dataSource)
	{
		super(dataSource, "world_base_houses");
	}

	@Override
	public void loadFully() {
		try {
			getData("SELECT * FROM " + getTableName() + ";", result -> {
				while (result.next()) {
					World.world.addHouse(new House(result.getInt("id"), result.getShort("map_id"), result.getInt("cell_id"), result.getInt("mapid"), result.getInt("caseid")));
				/* Set base price to all houses
				long saleBase = RS.getLong("saleBase");
				DatabaseManager.getDynamics().getHouseData().update(RS.getInt("id"), saleBase);*/
				}
			});
		} catch (SQLException e) {
			super.sendError(e);
		}
	}

	@Override
	public House load(int id) {
		throw new NotImplementedException();
	}

	@Override
	public boolean insert(House entity) {
		throw new NotImplementedException();
	}

	@Override
	public void delete(House entity) {
		throw new NotImplementedException();
	}

	@Override
	public void update(House entity) {
		throw new NotImplementedException();
	}

	@Override
	public Class<?> getReferencedClass() {
		return BaseHouseData.class;
	}
}
