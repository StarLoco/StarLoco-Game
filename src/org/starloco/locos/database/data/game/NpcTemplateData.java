package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.entity.npc.NpcTemplate;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Main;
import org.starloco.locos.object.ObjectTemplate;
import org.starloco.locos.quest.Quest;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NpcTemplateData extends FunctionDAO<NpcTemplate> {
    public NpcTemplateData(HikariDataSource dataSource) {
        super(dataSource, "npc_template");
    }

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + ";");
            while (result.next()) {
                int id = result.getInt("id");
                int bonusValue = result.getInt("bonusValue");
                int gfxID = result.getInt("gfxID");
                int scaleX = result.getInt("scaleX");
                int scaleY = result.getInt("scaleY");
                int sex = result.getInt("sex");
                int color1 = result.getInt("color1");
                int color2 = result.getInt("color2");
                int color3 = result.getInt("color3");
                String access = result.getString("accessories");
                int extraClip = result.getInt("extraClip");
                int customArtWork = result.getInt("customArtWork");
                String initQId = result.getString("initQuestion");
                String ventes = result.getString("ventes");
                String quests = result.getString("quests");
                String exchanges = result.getString("exchanges");
                NpcTemplate template = World.world.getNPCTemplate(result.getInt("id"));

                if (template == null) {
                    World.world.addNpcTemplate(new NpcTemplate(id, bonusValue, gfxID, scaleX, scaleY, sex, color1, color2, color3, access, extraClip, customArtWork, initQId, ventes, quests, exchanges, result.getString("path"), result.getByte("informations")));
                } else {
                    template.setInfos(id, bonusValue, gfxID, scaleX, scaleY, sex, color1, color2, color3, access, extraClip, customArtWork, initQId, ventes, quests, exchanges, result.getString("path"), result.getByte("informations"));
                }
            }
        } catch (SQLException e) {
            super.sendError(e);
            Main.stop("Can't load npc templates");
        } finally {
            close(result);
        }
    }

    @Override
    public NpcTemplate load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(NpcTemplate entity) {
        throw new NotImplementedException();
    }

    @Override
    public void delete(NpcTemplate entity) {
        throw new NotImplementedException();
    }

    @Override
    public void update(NpcTemplate entity) {
        String i = "";
        boolean first = true;
        for (ObjectTemplate obj : entity.getAllItem()) {
            if (first) i += obj.getId();
            else i += "," + obj.getId();
            first = false;
        }

        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `ventes` = ? WHERE `id` = ?;");
            p.setString(1, i);
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
        return NpcTemplateData.class;
    }

    public void loadQuest() {
        ResultSet result = null;
        try {
            result = getData("SELECT id, quests FROM " + getTableName() + ";");
            while (result.next()) {
                int id = result.getInt("id");
                String quests = result.getString("quests");
                if (quests.equalsIgnoreCase(""))
                    continue;
                NpcTemplate nt = World.world.getNPCTemplate(id);
                Quest quest = Quest.getQuestById(Integer.parseInt(quests));
                if (nt == null || quest == null)
                    continue;
                nt.setQuest(quest);
            }
        } catch (Exception e) {
            super.sendError(e);
            Main.stop("unknown");
        } finally {
            close(result);
        }
    }
}