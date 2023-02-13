package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.quest.QuestStep;

import java.sql.ResultSet;
import java.sql.SQLException;

public class QuestObjectiveData extends FunctionDAO<QuestStep> {
    public QuestObjectiveData(HikariDataSource dataSource) {
        super(dataSource, "quest_objectifs");
    }

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + ";");
            QuestStep.getSteps().clear();

            while (result.next()) {
                QuestStep objective = new QuestStep(result.getInt("id"), result.getInt("xp"), result.getInt("kamas"), result.getString("item"), result.getString("action"));
                QuestStep.addStep(objective);
            }
            close(result);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
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
        return QuestObjectiveData.class;
    }
}
