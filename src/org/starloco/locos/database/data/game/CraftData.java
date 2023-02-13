package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.game.world.World;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CraftData extends FunctionDAO<Object> {
    public CraftData(HikariDataSource dataSource) {
        super(dataSource, "crafts");
    }

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + ";");
            while (result.next()) {
                ArrayList<World.Couple<Integer, Integer>> m = new ArrayList<>();

                boolean cont = true;
                for (String str : result.getString("craft").split(";")) {
                    if(str.isEmpty()) continue;
                    try {
                        int tID = Integer.parseInt(str.split("\\*")[0]);
                        int qua = Integer.parseInt(str.split("\\*")[1]);
                        m.add(new World.Couple<>(tID, qua));
                    } catch (Exception e) {
                        e.printStackTrace();
                        cont = false;
                    }
                }
                if (!cont) // S'il y a eu une erreur de parsing, on ignore cette recette
                    continue;

                World.world.addCraft(result.getInt("id"), m);
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
    }

    @Override
    public Object load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(Object entity) {
        throw new NotImplementedException();
    }

    @Override
    public void delete(Object entity) {
        throw new NotImplementedException();
    }

    @Override
    public void update(Object entity) {
        throw new NotImplementedException();
    }

    @Override
    public Class<?> getReferencedClass() {
        return CraftData.class;
    }
}
