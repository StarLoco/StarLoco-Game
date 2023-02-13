package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.area.Area;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.game.world.World;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AreaData extends FunctionDAO<Area> {
	public AreaData(HikariDataSource dataSource)
	{
		super(dataSource, "area_data");
	}


	@Override
	public void loadFully() {
		ResultSet result = null;
		try	{
			result = getData("SELECT * FROM " + getTableName() + ";");
			while (result.next()) {
				int id = result.getInt("id");
				Area area = World.world.getArea(id);

				if(area != null) {
					area.setAlignement(result.getInt("alignement"));
					area.setPrismId(result.getInt("Prisme"));
				}
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
	public void update(Area area) {
		PreparedStatement p = null;
		try {
			p = getPreparedStatement("UPDATE " + getTableName() + " SET `alignement` = ?, `Prisme` = ? WHERE id = ?");
			p.setInt(1, area.getAlignement());
			p.setInt(2, area.getPrismId());
			p.setInt(3, area.getId());
			execute(p);
		} catch (SQLException e) {
			super.sendError(e);
		} finally	{
			close(p);
		}
	}

	@Override
	public Class<?> getReferencedClass() {
		return AreaData.class;
	}
}
