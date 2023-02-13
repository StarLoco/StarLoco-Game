package org.starloco.locos.database.data.login;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.area.map.entity.MountPark;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.database.data.game.MountParkData;
import org.starloco.locos.game.world.World;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BaseMountParkData extends FunctionDAO<MountPark> {

	public BaseMountParkData(HikariDataSource dataSource)
	{
		super(dataSource, "world_base_mountparks");
	}

	@Override
	public void loadFully() {
		ResultSet result = null;
		try {
			result = getData("SELECT * FROM " + getTableName() + ";");
			while (result.next()) {
				GameMap map = World.world.getMap(result.getShort("mapid"));
				if (map == null)
					continue;
				MountPark MP = new MountPark(map, result.getInt("cellid"), result.getInt("size"), result.getInt("priceBase"), result.getInt("cellMount"), result.getInt("cellporte"), result.getString("cellEnclos"), result.getInt("sizeObj"));
				World.world.addMountPark(MP);
				((MountParkData) DatabaseManager.get(MountParkData.class)).exist(MP);
			}
		} catch (SQLException e) {
			super.sendError(e);
		} finally {
			close(result);
		}
	}

	@Override
	public MountPark load(int id) {
		throw new NotImplementedException();
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
			p.setInt(4, entity.getMap().getId());
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


	/*
	//FIXME: Usefull now ?
	public void reload(int i)
	{
		Result result = null;
		try
		{
			result = getData("SELECT * from mountpark_data");
			ResultSet RS = result.resultSet;
			while (RS.next())
			{
                GameMap map = World.world.getMaps(RS.getShort("mapid"));
				if (map == null)
					continue;
				if (RS.getShort("mapid") != i)
					continue;
				if (!World.world.getMountPark().containsKey(RS.getShort("mapid")))
				{
					MountPark MP = new MountPark(map, RS.getInt("cellid"), RS.getInt("size"), RS.getInt("priceBase"), RS.getInt("cellMount"), RS.getInt("cellporte"), RS.getString("cellEnclos"), RS.getInt("sizeObj"));
					World.world.addMountPark(MP);
				}
				else
				{
					World.world.getMountPark().get(RS.getShort("mapid")).setInfos(map, RS.getInt("cellid"), RS.getInt("size"),RS.getInt("cellMount"), RS.getInt("cellporte"), RS.getString("cellEnclos"), RS.getInt("sizeObj"));
				}
			}
		}
		catch (SQLException e)
		{
			super.sendError("Mountpark_dataData reload", e);
		}
		finally
		{
			close(result);
		}
	}*/
}
