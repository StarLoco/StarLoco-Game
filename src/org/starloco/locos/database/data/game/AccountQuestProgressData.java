package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.client.Account;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.quest.QuestProgress;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class AccountQuestProgressData extends FunctionDAO<QuestProgress> {

    public AccountQuestProgressData(HikariDataSource dataSource) {
        super(dataSource, "account_quest_progress");
    }

    @Override
    public void loadFully() {
        throw new NotImplementedException();
    }

    /**
     * @param id of the player !!
     * @return PlayerQuestProgress
     */
    @Override
    public QuestProgress load(int id) {
        Account account = World.world.getAccount(id);
        try {
            getData("SELECT * FROM " + getTableName() + " WHERE `account_id` = " + id + ";", result -> {
                while (result.next()) {
                    int qId = result.getInt("quest_id");
                    int sId = result.getInt("current_step");
                    Set<Integer> completedObjectives = Arrays.stream(result.getString("completed_objectives").split("\\|")).filter(s->s.length()!=0).map(Integer::parseInt).collect(Collectors.toSet());
                    boolean finished = result.getBoolean("finished");

                    QuestProgress qp = new QuestProgress(id, qId, sId, completedObjectives, finished);
                    account.addQuestProgression(qp);
                }
            });
        } catch (SQLException e) {
            super.sendError(e);
        }
        return null;
    }

    @Override
    public boolean insert(QuestProgress entity) {
        replace(entity);
        return true;
    }

    @Override
    public void delete(QuestProgress entity) {
        try(PreparedStatement p = getPreparedStatement("DELETE FROM " + getTableName() + " WHERE `account_id` = ? AND `quest_id` = ?;")){
            p.setInt(1, entity.entityId);
            p.setInt(2, entity.questId);
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        }
    }

    private void replace(QuestProgress entity) {
        if(entity == null) return;

        try(PreparedStatement p = getPreparedStatement("REPLACE INTO " + getTableName() + " (`account_id`, `quest_id`, `current_step`, `completed_objectives`, `finished`) VALUES (?, ?, ?, ?, ?);")) {
            p.setInt(1, entity.entityId);
            p.setInt(2, entity.questId);
            p.setInt(3, entity.getCurrentStep());
            p.setString(4, entity.getCompletedObjectives().stream().map(Object::toString).collect(Collectors.joining("|")));
            p.setBoolean(5, entity.isFinished());

            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        }
    }

    @Override
    public void update(QuestProgress entity) {
        replace(entity);
    }

    @Override
    public Class<?> getReferencedClass() {
        return AccountQuestProgressData.class;
    }
}
