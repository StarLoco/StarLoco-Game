package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.entity.monster.Monster;
import org.starloco.locos.game.world.World;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class DropData extends FunctionDAO<World.Drop> {
    public DropData(HikariDataSource dataSource) {
        super(dataSource, "drops");
    }

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            World.world.getMonstres().forEach(monster -> monster.getDrops().clear());
            result = getData("SELECT * FROM " + getTableName() + ";");
            while (result.next()) {
                Monster MT = World.world.getMonstre(result.getInt("monsterId"));
                String action = result.getString("action");
                String condition = "";
                ArrayList<Double> percents = getPercents(result);

                if (!action.equals("-1") && !action.equals("1") && action.contains(":")) {
                    condition = action.split(":")[1];
                    action = action.split(":")[0];
                }

                if (World.world.getObjTemplate(result.getInt("objectId")) != null && MT != null) {
                    MT.addDrop(new World.Drop(result.getInt("objectId"), percents, result.getInt("ceil"), Integer.parseInt(action), result.getInt("level"), condition));
                } else {
                    if(MT == null && result.getInt("monsterId") == 0) {
                        World.Drop drop = new World.Drop(result.getInt("objectId"), percents, result.getInt("ceil"), Integer.parseInt(action), result.getInt("level"), condition);
                        World.world.getMonstres().stream().filter(Objects::nonNull).forEach(monster -> monster.addDrop(drop));
                    }
                }
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
    }

    private ArrayList<Double> getPercents(ResultSet result) throws SQLException {
        ArrayList<Double> percents = new ArrayList<>();
        percents.add(result.getDouble("percentGrade1"));
        percents.add(result.getDouble("percentGrade2"));
        percents.add(result.getDouble("percentGrade3"));
        percents.add(result.getDouble("percentGrade4"));
        percents.add(result.getDouble("percentGrade5"));
        return percents;
    }

    @Override
    public World.Drop load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(World.Drop entity) {
        throw new NotImplementedException();
    }

    @Override
    public void delete(World.Drop entity) {
        throw new NotImplementedException();
    }

    @Override
    public void update(World.Drop entity) {
        throw new NotImplementedException();
    }

    @Override
    public Class<?> getReferencedClass() {
        return DropData.class;
    }
}
