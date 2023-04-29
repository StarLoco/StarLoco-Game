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

public class QuestProgressData extends FunctionDAO<QuestProgress> {

    public QuestProgressData(HikariDataSource dataSource) {
        super(dataSource, "quest_progress");
    }

    @Override
    public void loadFully() {
        throw new NotImplementedException();
    }

    @Override
    public QuestProgress load(int accountId) {
        Account account = World.world.getAccount(accountId);
        try {
            getData("SELECT * FROM " + getTableName() + " WHERE `account_id` = " + accountId + ";", result -> {
                while (result.next()) {
                    int pId = result.getInt("player_id");
                    int qId = result.getInt("quest_id");
                    int sId = result.getInt("current_step");
                    Set<Integer> completedObjectives = Arrays.stream(result.getString("completed_objectives").split("\\|")).filter(s->s.length()!=0).map(Integer::parseInt).collect(Collectors.toSet());
                    boolean finished = result.getBoolean("finished");

                    // new PlayerQuestProgress(result.getInt("id"), result.getInt("quest"), result.getInt("finish") == 1, result.getInt("player"), result.getString("stepsValidation"))
                    QuestProgress qp = new QuestProgress(accountId, pId, qId, sId, completedObjectives, finished);
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
        try(PreparedStatement p = getPreparedStatement("DELETE FROM " + getTableName() + " WHERE `account_id` = ? AND `player_id` = ? AND `quest_id` = ?;")){
            p.setInt(1, entity.accountId);
            p.setInt(2, entity.playerId);
            p.setInt(3, entity.questId);
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        }
    }

    private void replace(QuestProgress entity) {
        if(entity == null) return;

        try(PreparedStatement p = getPreparedStatement("REPLACE INTO " + getTableName() + " (`account_id`, `player_id`, `quest_id`, `current_step`, `completed_objectives`, `finished`) VALUES (?, ?, ?, ?, ?, ?);")) {
            p.setInt(1, entity.accountId);
            p.setInt(2, entity.playerId);
            p.setInt(3, entity.questId);
            p.setInt(4, entity.getCurrentStep());
            p.setString(5, entity.getCompletedObjectives().stream().map(Object::toString).collect(Collectors.joining("|")));
            p.setBoolean(6, entity.isFinished());

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
        return QuestProgressData.class;
    }
}
