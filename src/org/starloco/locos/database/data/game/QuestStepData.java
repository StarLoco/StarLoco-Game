package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.quest.QuestObjective;

import java.sql.ResultSet;
import java.sql.SQLException;

public class QuestStepData extends FunctionDAO<QuestObjective> {

    public QuestStepData(HikariDataSource dataSource) {
        super(dataSource, "quest_etapes");
    }


    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + ";");
            QuestObjective.getObjectives().clear();
            while (result.next()) {
                QuestObjective step = new QuestObjective(result.getInt("id"), result.getInt("type"), result.getInt("objectif"),
                        result.getString("item"), result.getInt("npc"), result.getString("monster"), result.getString("conditions"), result.getInt("validationType"));
                QuestObjective.addObjective(step);
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
        return QuestStepData.class;
    }
}
