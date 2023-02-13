package org.starloco.locos.database.data.login;

import com.mysql.jdbc.Statement;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.client.Player;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.quest.QuestPlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QuestPlayerData extends FunctionDAO<QuestPlayer> {

    public QuestPlayerData(HikariDataSource dataSource) {
        super(dataSource, "world_players_quests");
    }

    @Override
    public void loadFully() {
        throw new NotImplementedException();
    }

    /**
     * @param id of the player !!
     * @return QuestPlayer
     */
    @Override
    public QuestPlayer load(int id) {
        Player player = World.world.getPlayer(id);
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + " WHERE `player` = " + id + ";");

            while (result.next()) {
                player.addQuestPerso(new QuestPlayer(result.getInt("id"), result.getInt("quest"), result.getInt("finish") == 1, result.getInt("player"), result.getString("stepsValidation")));
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
        return null;
    }

    //TODO: Change the insert to set the id of the entity
    @Override
    public boolean insert(QuestPlayer entity) {
        PreparedStatement statement = null;
        boolean ok = true;
        try {
            statement = this.getConnection().prepareStatement("INSERT INTO " + getTableName() + "(`quest`, `finish`, `player`, `stepsValidation`) VALUES (?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, entity.getQuest().getId());
            statement.setInt(2, entity.isFinish() ? 1 : 0);
            statement.setInt(3, entity.getPlayer().getId());
            statement.setString(4, entity.getQuestObjectivesToString());
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                ok = false;
            } else {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        entity.setId(generatedKeys.getInt(1));
                    } else {
                        ok = false;
                    }
                }
            }
        } catch (SQLException e) {
            super.sendError(e);
            ok = false;
        } finally {
            close(statement);
        }
        return ok;
    }

    @Override
    public void delete(QuestPlayer entity) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("DELETE FROM " + getTableName() + " WHERE `id` = ?;");
            p.setInt(1, entity.getId());
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    @Override
    public void update(QuestPlayer entity) {
        if(entity == null) return;
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `finish` = ?, `stepsValidation` = ? WHERE `id` = ?;");
            if(p != null) {
                p.setInt(1, entity.isFinish() ? 1 : 0);
                p.setString(2, entity.getQuestObjectivesToString());
                p.setInt(3, entity.getId());
                execute(p);
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            if(p != null) close(p);
        }
    }

    @Override
    public Class<?> getReferencedClass() {
        return QuestPlayerData.class;
    }
}
