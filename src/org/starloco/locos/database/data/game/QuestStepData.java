package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.quest.QuestStep;

import java.sql.SQLException;

public class QuestStepData extends FunctionDAO<QuestStep> {
    public QuestStepData(HikariDataSource dataSource) {
        super(dataSource, "quest_step");
    }

    @Override
    public void loadFully() {
        try {
            getData("SELECT * FROM " + getTableName() + ";", result -> {
                QuestStep.steps.clear();

                while (result.next()) {
                    QuestStep step = new QuestStep(result.getInt("id"), result.getInt("xp"), result.getInt("kamas"), result.getString("item"), result.getString("action"));
                    QuestStep.steps.put(step.getId(), step);
                }
            });
        } catch (SQLException e) {
            super.sendError(e);
        }
    }

    @Override
    public QuestStep load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(QuestStep entity) {
        throw new NotImplementedException();
    }

    @Override
    public void delete(QuestStep entity) {
        throw new NotImplementedException();
    }

    @Override
    public void update(QuestStep entity) {
        throw new NotImplementedException();
    }

    @Override
    public Class<?> getReferencedClass() {
        return QuestStepData.class;
    }
}
