package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.quest.QuestObjective;

import java.sql.ResultSet;
import java.sql.SQLException;

public class QuestObjectiveData extends FunctionDAO<QuestObjective> {

    public QuestObjectiveData(HikariDataSource dataSource) {
        super(dataSource, "quest_objective");
    }

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + ";");
            QuestObjective.objectives.clear();
            while (result.next()) {
                QuestObjective objective = new QuestObjective(result.getInt("id"), result.getInt("type"), result.getInt("quest_step"),
                        result.getInt("validationType"), result.getInt("npc"), result.getString("conditions"), result.getString("item"), result.getString("monster"));
                QuestObjective.objectives.put(objective.getId(), objective);
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
    }

    @Override
    public QuestObjective load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(QuestObjective entity) {
        throw new NotImplementedException();
    }

    @Override
    public void delete(QuestObjective entity) {
        throw new NotImplementedException();
    }

    @Override
    public void update(QuestObjective entity) {
        throw new NotImplementedException();
    }

    @Override
    public Class<?> getReferencedClass() {
        return QuestObjectiveData.class;
    }
}
