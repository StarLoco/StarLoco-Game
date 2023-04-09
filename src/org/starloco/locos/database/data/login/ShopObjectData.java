package org.starloco.locos.database.data.login;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.area.Area;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.object.ObjectTemplate;
import org.starloco.locos.object.ShopObject;

import java.sql.SQLException;

public class ShopObjectData extends FunctionDAO<Area>  {

	public ShopObjectData(HikariDataSource dataSource)
	{
		super(dataSource, "website_shop_objects");
	}

	@Override
	public void loadFully() {
		try {
			ShopObject.objects.clear();
			getData("SELECT * FROM " + getTableName() + " WHERE `active` = 1 AND `server` = " + Config.gameServerId + ";", result -> {
				short id = 1;
				while (result.next()) {
					ObjectTemplate template = World.world.getObjTemplate(result.getInt("template"));
					if (template != null) {
						ShopObject object = new ShopObject(id, template, result.getBoolean("jp"), result.getShort("price"));
						ShopObject.objects.add(object);
						id++;
					}
				}
			});
		} catch (SQLException e) {
			super.sendError(e);
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
		return ShopObjectData.class;
	}
}
