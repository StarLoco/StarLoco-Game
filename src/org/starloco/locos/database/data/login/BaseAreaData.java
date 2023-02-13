package org.starloco.locos.database.data.login;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.area.Area;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.game.world.World;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BaseAreaData extends FunctionDAO<Area>  {

	public BaseAreaData(HikariDataSource dataSource)
	{
		super(dataSource, "world_base_areas");
	}

	@Override
	public void loadFully() {
		ResultSet result = null;
		try {
			result = getData("SELECT * FROM " + getTableName() + ";");
			while (result.next()) {
				Area area = new Area(result.getInt("id"), result.getInt("superarea"));
				World.world.addArea(area);
			}
		} catch (SQLException e) {
			super.sendError(e);
		} finally	{
			close(result);
		}
	}

	@Override
	public Area load(int id) {
		throw new NotImplementedException();
	}

	@Override
	public boolean insert(Area entity) {
		throw new NotImplementedException();
	}

	@Override
	public void delete(Area entity) {
		throw new NotImplementedException();
	}

	@Override
	public void update(Area entity) {
		throw new NotImplementedException();
	}

	@Override
	public Class<?> getReferencedClass() {
		return BaseAreaData.class;
	}
}
