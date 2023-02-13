package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.quest.Quest;

import java.sql.ResultSet;
import java.sql.SQLException;

public class QuestData extends FunctionDAO<Quest> {
    public QuestData(HikariDataSource dataSource) {
        super(dataSource, "quest_data");
    }

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + ";");
            Quest.getQuestList().clear();

            while (result.next()) {
                Quest quest = new Quest(result.getInt("id"), result.getString("etapes"), result.getString("objectif"), result.getInt("npc"), result.getString("action"), result.getString("args"), (result.getInt("deleteFinish") == 1), result.getString("condition"));
                if (quest.getNpcTemplate() != null) {
                    quest.getNpcTemplate().setQuest(quest);
                    quest.getNpcTemplate().setExtraClip(4);
                }
                Quest.addQuest(quest);
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
    }

    @Override
    public Quest load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(Quest entity) {
        throw new NotImplementedException();
    }

    @Override
    public void delete(Quest entity) {
        throw new NotImplementedException();
    }

    @Override
    public void update(Quest entity) {
        throw new NotImplementedException();
    }

    @Override
    public Class<?> getReferencedClass() {
        return QuestData.class;
    }
}
