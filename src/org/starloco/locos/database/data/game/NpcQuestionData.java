package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.entity.npc.NpcQuestion;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NpcQuestionData extends FunctionDAO<NpcQuestion> {
    public NpcQuestionData(HikariDataSource dataSource) {
        super(dataSource, "npc_questions");
    }

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + ";");
            while (result.next()) {
                World.world.addNPCQuestion(new NpcQuestion(result.getInt("ID"), result.getString("responses"), result.getString("params"), result.getString("cond"), result.getString("ifFalse")));
            }
        } catch (SQLException e) {
            super.sendError(e);
            Main.stop("Can't load npc questions");
        } finally {
            close(result);
        }
    }

    @Override
    public NpcQuestion load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(NpcQuestion entity) {
        throw new NotImplementedException();
    }

    @Override
    public void delete(NpcQuestion entity) {
        throw new NotImplementedException();
    }

    @Override
    public void update(NpcQuestion entity) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `responses` = ? WHERE `ID` = ?;");
            p.setString(1, entity.getAnwsers());
            p.setInt(2, entity.getId());
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    @Override
    public Class<?> getReferencedClass() {
        return NpcQuestionData.class;
    }

    public void updateLot() {
        NpcQuestion question = World.world.getNPCQuestion(1646);
        if(question != null) {
            question.setArgs(String.valueOf(Integer.parseInt(question.getArgs()) + 50));
            PreparedStatement p = null;
            try {
                p = getPreparedStatement("UPDATE `npc_questions` SET params='" + question.getArgs() + "' WHERE `id`=1646");
                execute(p);
            } catch (SQLException e) {
                super.sendError(e);
            } finally {
                close(p);
            }
        }
    }
}
