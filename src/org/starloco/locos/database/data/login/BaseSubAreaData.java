package org.starloco.locos.database.data.login;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.area.SubArea;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.game.world.World;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BaseSubAreaData extends FunctionDAO<SubArea> {

	public BaseSubAreaData(HikariDataSource dataSource)
	{
		super(dataSource, "world_base_sub_areas");
	}

	@Override
	public void loadFully() {
		ResultSet result = null;
		try {
			result = getData("SELECT * FROM " + getTableName() + ";");

			while (result.next()) {
				SubArea subArea = new SubArea(result.getInt("id"), result.getString("name"), result.getInt("area"), result.getString("nearest_sub_areas"));
				World.world.addSubArea(subArea);

				if (subArea.getArea() != null)
					subArea.getArea().addSubArea(subArea);
			}
		} catch (SQLException e) {
			super.sendError(e);
		} finally {
			close(result);
		}
	}

	@Override
	public SubArea load(int id) {
		throw new NotImplementedException();
	}

	@Override
	public boolean insert(SubArea entity) {
		throw new NotImplementedException();
	}

	@Override
	public void delete(SubArea entity) {
		throw new NotImplementedException();
	}

	@Override
	public void update(SubArea entity) {
		throw new NotImplementedException();
	}

	@Override
	public Class<?> getReferencedClass() {
		return BaseSubAreaData.class;
	}
}
