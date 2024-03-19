package org.starloco.locos.database.legacydata.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.legacydata.FunctionDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.job.Job;

import java.sql.SQLException;

public class JobData extends FunctionDAO<Job> {
    public JobData(HikariDataSource dataSource) {
        super(dataSource, "jobs_data");
    }

    @Override
    public void loadFully() {
        try {
            getData("SELECT * FROM " + getTableName() + ";", result -> {
                while (result.next()) {

                    String skills = "";
                    if (result.getString("skills") != null)
                        skills = result.getString("skills");
                    World.world.addJob(new Job(result.getInt("id"), result.getString("tools"), result.getString("crafts"), skills));
                }
            });
        } catch (SQLException e) {
            super.sendError(e);
        }
    }

    @Override
    public Job load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(Job entity) {
        throw new NotImplementedException();
    }

    @Override
    public void delete(Job entity) {
        throw new NotImplementedException();
    }

    @Override
    public void update(Job entity) {
        throw new NotImplementedException();
    }

    @Override
    public Class<?> getReferencedClass() {
        return JobData.class;
    }
}
