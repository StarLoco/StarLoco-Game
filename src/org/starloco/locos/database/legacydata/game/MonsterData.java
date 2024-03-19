package org.starloco.locos.database.legacydata.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.legacydata.FunctionDAO;
import org.starloco.locos.entity.monster.Monster;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Main;

import java.sql.SQLException;

public class MonsterData extends FunctionDAO<Monster> {
    public MonsterData(HikariDataSource dataSource) {
        super(dataSource, "monsters");
    }

    @Override
    public void loadFully() {
        try {
            getData("SELECT * FROM " + getTableName() + ";", result -> {
                while (result.next()) {
                    int id = result.getInt("id");
                    int gfxID = result.getInt("gfxID");
                    int align = result.getInt("align");
                    String colors = result.getString("colors");
                    String grades = result.getString("grades");
                    String spells = result.getString("spells");
                    String stats = result.getString("stats");
                    String statsInfos = result.getString("statsInfos");
                    String pdvs = result.getString("pdvs");
                    String pts = result.getString("points");
                    String inits = result.getString("inits");
                    int mK = result.getInt("minKamas");
                    int MK = result.getInt("maxKamas");
                    String xp = result.getString("exps");
                    int IAType = result.getInt("AI_Type");
                    boolean capturable = result.getInt("capturable") == 1;
                    int aggroDistance = result.getInt("aggroDistance");
                    boolean isBoss = result.getInt("isBoss") == 1;
                    boolean isArchMonster = result.getInt("isBoss") == 1;

                    if (World.world.getMonstre(id) == null) {
                        World.world.addMobTemplate(id, new Monster(id, gfxID, align, colors, grades, spells, stats, statsInfos, pdvs, pts, inits, mK, MK, xp, IAType, capturable, aggroDistance, isBoss, isArchMonster));
                    } else {
                        World.world.getMonstre(id).setInfos(gfxID, align, colors, grades, spells, stats, statsInfos, pdvs, pts, inits, mK, MK, xp, IAType, capturable, aggroDistance, isBoss, isArchMonster);
                    }
                }
            });
        } catch (SQLException e) {
            super.sendError(e);
            Main.stop("Can't load monsters");
        }
    }

    @Override
    public Monster load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(Monster entity) {
        throw new NotImplementedException();
    }

    @Override
    public void delete(Monster entity) {
        throw new NotImplementedException();
    }

    @Override
    public void update(Monster entity) {
        throw new NotImplementedException();
    }

    @Override
    public Class<?> getReferencedClass() {
        return MonsterData.class;
    }
}
