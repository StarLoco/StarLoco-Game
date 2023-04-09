package org.starloco.locos.database.data.login;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.area.map.entity.MountPark;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.database.data.game.MountParkData;
import org.starloco.locos.game.world.World;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BaseMountParkData extends FunctionDAO<MountPark> {

	public BaseMountParkData(HikariDataSource dataSource) {
		super(dataSource, "world_base_mountparks");
	}

	@Override
	public void loadFully() {
		try {
			getData("SELECT * FROM " + getTableName() + ";", result -> {
				while (result.next()) {
					MountPark MP = new MountPark(result.getShort("mapid"), result.getInt("cellid"), result.getInt("size"), result.getInt("priceBase"), result.getInt("cellMount"), result.getInt("cellporte"), result.getString("cellEnclos"), result.getInt("sizeObj"));
					World.world.addMountPark(MP);
					((MountParkData) DatabaseManager.get(MountParkData.class)).exist(MP);
				}
			});
		} catch (SQLException e) {
			super.sendError(e);
		}
	}

	@Override
	public MountPark load(int id) {
		try {
			getData("SELECT * FROM " + getTableName() + " WHERE `mapid` = " + id +" ;", result -> {
				if (result.next()) {
					MountPark MP = new MountPark(result.getInt("mapid"), result.getInt("cellid"), result.getInt("size"), result.getInt("priceBase"), result.getInt("cellMount"), result.getInt("cellporte"), result.getString("cellEnclos"), result.getInt("sizeObj"));
					World.world.addMountPark(MP);
					((MountParkData) DatabaseManager.get(MountParkData.class)).exist(MP);
				}
			});
		} catch (SQLException e) {
			super.sendError(e);
		}
		return null;
	}

	@Override
	public boolean insert(MountPark entity) {
		throw new NotImplementedException();
	}

	@Override
	public void delete(MountPark entity) {
		throw new NotImplementedException();
	}

	@Override
	public void update(MountPark entity) {
		PreparedStatement p = null;
		try {
			p = getPreparedStatement("UPDATE " + getTableName() + " SET `cellMount` =?, `cellPorte`=?, `cellEnclos`=? WHERE `mapid`=?");
			p.setInt(1, entity.getMountcell());
			p.setInt(2, entity.getDoor());
			p.setString(3, entity.parseStringCellObject());
			p.setInt(4, entity.getMap());
			execute(p);
		} catch (SQLException e) {
			super.sendError(e);
		} finally {
			close(p);
		}
	}

	@Override
	public Class<?> getReferencedClass() {
		return BaseMountParkData.class;
	}
}
