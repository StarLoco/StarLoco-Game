package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.entity.npc.NpcAnswer;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Main;
import org.starloco.locos.other.Action;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NpcAnswerData extends FunctionDAO<NpcAnswer> {
    public NpcAnswerData(HikariDataSource dataSource) {
        super(dataSource, "npc_reponses_actions");
    }

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + ";");
            while (result.next()) {
                int id = result.getInt("ID");
                NpcAnswer answer = World.world.getNpcAnswer(id);

                if (answer == null) {
                    answer = new NpcAnswer(id);
                    World.world.addNpcAnswer(answer);
                }
                answer.addAction(new Action(result.getInt("type"), result.getString("args"), "", null));
            }
        } catch (SQLException e) {
            super.sendError(e);
            Main.stop("unknown");
        } finally {
            close(result);
        }
    }

    @Override
    public NpcAnswer load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(NpcAnswer entity) {
        throw new NotImplementedException();
    }

    @Override
    public void delete(NpcAnswer entity) {
        throw new NotImplementedException();
    }

    @Override
    public void update(NpcAnswer entity) {
        throw new NotImplementedException();
    }

    @Override
    public Class<?> getReferencedClass() {
        return NpcAnswerData.class;
    }
}
