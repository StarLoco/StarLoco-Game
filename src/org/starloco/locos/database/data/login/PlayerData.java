package org.starloco.locos.database.data.login;

import com.mysql.jdbc.Statement;
import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.client.Account;
import org.starloco.locos.client.Player;
import org.starloco.locos.command.administration.Group;
import org.starloco.locos.database.DatabaseManager;

import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.database.data.game.GuildMemberData;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.kernel.Main;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;

public class PlayerData extends FunctionDAO<Player> {

    public PlayerData(HikariDataSource dataSource) {
        super(dataSource, "world_players");
    }

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + " WHERE server = " + Config.gameServerId + ";");
            while (result.next()) {
                Player player = new Player(result.getInt("id"), result.getString("name"), result.getInt("groupe"),
                        result.getInt("sexe"), result.getInt("class"), result.getInt("color1"), result.getInt("color2"),
                        result.getInt("color3"), result.getLong("kamas"), result.getInt("spellboost"),
                        result.getInt("capital"), result.getInt("energy"), result.getInt("level"), result.getLong("xp"),
                        result.getInt("size"), result.getInt("gfx"), result.getByte("alignement"), result.getInt("account"),
                        this.getStats(result), result.getByte("seeFriend"), result.getByte("seeAlign"), result.getByte("seeSeller"),
                        result.getString("canaux"), result.getShort("map"), result.getInt("cell"), result.getString("objets"),
                        result.getString("storeObjets"), result.getInt("pdvper"), result.getString("spells"), result.getString("savepos"),
                        result.getString("jobs"), result.getInt("mountxpgive"), result.getInt("mount"), result.getInt("honor"),
                        result.getInt("deshonor"), result.getInt("alvl"), result.getString("zaaps"), result.getByte("title"),
                        result.getInt("wife"), result.getString("morphMode"), result.getString("allTitle"), result.getString("emotes"),
                        result.getLong("prison"), false, result.getString("parcho"), result.getLong("timeDeblo"),
                        result.getBoolean("noall"), result.getString("deadInformation"), result.getByte("deathCount"),
                        result.getLong("totalKills"));

                player.VerifAndChangeItemPlace();
                World.world.addPlayer(player);
                if (player.isShowSeller())
                    World.world.addSeller(player);
            }
        } catch (SQLException e) {
            super.sendError(e);
            Main.stop("Can't load players");
        } finally {
            close(result);
        }
    }

    @Override
    public Player load(int id) {
        ResultSet result = null;
        Player player = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + " WHERE id = '" + id + "' AND server = " + Config.gameServerId + ";");
            while (result.next()) {
                Player oldPlayer = World.world.getPlayer((int) id);
                player = new Player(result.getInt("id"), result.getString("name"), result.getInt("groupe"), result.getInt("sexe"),
                        result.getInt("class"), result.getInt("color1"), result.getInt("color2"), result.getInt("color3"), result.getLong("kamas"),
                        result.getInt("spellboost"), result.getInt("capital"), result.getInt("energy"), result.getInt("level"), result.getLong("xp"),
                        result.getInt("size"), result.getInt("gfx"), result.getByte("alignement"), result.getInt("account"), this.getStats(result),
                        result.getByte("seeFriend"), result.getByte("seeAlign"), result.getByte("seeSeller"), result.getString("canaux"),
                        result.getShort("map"), result.getInt("cell"), result.getString("objets"), result.getString("storeObjets"),
                        result.getInt("pdvper"), result.getString("spells"), result.getString("savepos"), result.getString("jobs"),
                        result.getInt("mountxpgive"), result.getInt("mount"), result.getInt("honor"), result.getInt("deshonor"),
                        result.getInt("alvl"), result.getString("zaaps"), result.getByte("title"), result.getInt("wife"),
                        result.getString("morphMode"), result.getString("allTitle"), result.getString("emotes"), result.getLong("prison"),
                        false, result.getString("parcho"), result.getLong("timeDeblo"), result.getBoolean("noall"),
                        result.getString("deadInformation"), result.getByte("deathCount"), result.getLong("totalKills"));

                if(oldPlayer != null)
                    player.setNeededEndFight(oldPlayer.needEndFight(), oldPlayer.hasMobGroup());

                player.VerifAndChangeItemPlace();
                World.world.addPlayer(player);
            }
        } catch (SQLException e) {
            super.sendError(e);
            Main.stop("unknown");
        } finally {
            close(result);
        }
        return player;
    }

    @Override
    public boolean insert(Player entity) {
        PreparedStatement statement = null;
        boolean ok = true;
        try {
            Connection connection = this.getConnection();
            String sql = "INSERT INTO " + getTableName() + "(`name`, `sexe`, `class`, `color1`, `color2`, `color3`, `kamas`, `spellboost`, `capital`, `energy`, `level`, `xp`, `size`, `gfx`, `account`, `cell`, `map`, `spells`, `objets`, `storeObjets`, `morphMode`, `server`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'','','0',?)";
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, entity.getName());
            statement.setInt(2, entity.getSexe());
            statement.setInt(3, entity.getClasse());
            statement.setInt(4, entity.getColor1());
            statement.setInt(5, entity.getColor2());
            statement.setInt(6, entity.getColor3());
            statement.setLong(7, entity.getKamas());
            statement.setInt(8, entity.get_spellPts());
            statement.setInt(9, entity.getCapital());
            statement.setInt(10, entity.getEnergy());
            statement.setInt(11, entity.getLevel());
            statement.setLong(12, entity.getExp());
            statement.setInt(13, entity.get_size());
            statement.setInt(14, entity.getGfxId());
            statement.setInt(15, entity.getAccID());
            statement.setInt(16, entity.getCurCell().getId());
            statement.setInt(17, entity.getCurMap().getId());
            statement.setString(18, entity.parseSpellToDB());
            statement.setInt(19, Config.gameServerId);

            int affectedRows = statement.executeUpdate();
            if (affectedRows != 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        entity.setId(generatedKeys.getInt(1));
                    }
                    else {
                        ok = false;
                    }
                }
            } else {
                ok = false;
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
    public void delete(Player entity) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("DELETE FROM " + getTableName() + " WHERE id = ?");
            p.setInt(1, entity.getId());
            execute(p);

            ObjectData dao = (ObjectData) DatabaseManager.get(ObjectData.class);
            if (!entity.getItemsIDSplitByChar(",").equals(""))
                for(String id : entity.getItemsIDSplitByChar(",").split(","))
                    dao.delete(World.world.getGameObject(Integer.parseInt(id)));
            if (!entity.getStoreItemsIDSplitByChar(",").equals(""))
                for(String id : entity.getStoreItemsIDSplitByChar(",").split(","))
                    dao.delete(World.world.getGameObject(Integer.parseInt(id)));
            if (entity.getMount() != null)
                ((MountData) DatabaseManager.get(MountData.class)).update(entity.getMount());
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    @Override
    public void update(Player entity) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `kamas`= ?, `spellboost`= ?, `capital`= ?, `energy`= ?, `level`= ?, `xp`= ?, `size` = ?, `gfx`= ?, `alignement`= ?, `honor`= ?, `deshonor`= ?, `alvl`= ?, `vitalite`= ?, `force`= ?, `sagesse`= ?, `intelligence`= ?, `chance`= ?, `agilite`= ?, `seeFriend`= ?, `seeAlign`= ?, `seeSeller`= ?, `canaux`= ?, `map`= ?, `cell`= ?, `pdvper`= ?, `spells`= ?, `objets`= ?, `storeObjets`= ?, `savepos`= ?, `zaaps`= ?, `jobs`= ?, `mountxpgive`= ?, `mount`= ?, `title`= ?, `wife`= ?, `morphMode`= ?, `allTitle` = ?, `emotes` = ?, `prison` = ?, `parcho` = ?, `timeDeblo` = ?, `noall` = ?, `deadInformation` = ?, `deathCount` = ?, `totalKills` = ? WHERE `id` = ? LIMIT 1");
            p.setLong(1, entity.getKamas());
            p.setInt(2, entity.get_spellPts());
            p.setInt(3, entity.getCapital());
            p.setInt(4, entity.getEnergy());
            p.setInt(5, entity.getLevel());
            p.setLong(6, entity.getExp());
            p.setInt(7, entity.get_size());
            p.setInt(8, entity.getGfxId());
            p.setInt(9, entity.getAlignment());
            p.setInt(10, entity.get_honor());
            p.setInt(11, entity.getDeshonor());
            p.setInt(12, entity.getALvl());
            p.setInt(13, entity.stats.getEffect(Constant.STATS_ADD_VITA));
            p.setInt(14, entity.stats.getEffect(Constant.STATS_ADD_FORC));
            p.setInt(15, entity.stats.getEffect(Constant.STATS_ADD_SAGE));
            p.setInt(16, entity.stats.getEffect(Constant.STATS_ADD_INTE));
            p.setInt(17, entity.stats.getEffect(Constant.STATS_ADD_CHAN));
            p.setInt(18, entity.stats.getEffect(Constant.STATS_ADD_AGIL));
            p.setInt(19, (entity.is_showFriendConnection() ? 1 : 0));
            p.setInt(20, (entity.is_showWings() ? 1 : 0));
            p.setInt(21, (entity.isShowSeller() ? 1 : 0));
            p.setString(22, entity.get_canaux());
            if (entity.getCurMap() != null) p.setInt(23, entity.getCurMap().getId());
            else p.setInt(23, 7411);
            if (entity.getCurCell() != null) p.setInt(24, entity.getCurCell().getId());
            else p.setInt(24, 311);
            p.setInt(25, entity.get_pdvper());
            p.setString(26, entity.parseSpellToDB());
            p.setString(27, entity.parseObjetsToDB());
            p.setString(28, entity.parseStoreItemstoBD());
            p.setString(29, entity.getSavePosition());
            p.setString(30, entity.parseZaaps());
            p.setString(31, entity.parseJobData());
            p.setInt(32, entity.getMountXpGive());
            p.setInt(33, (entity.getMount() != null ? entity.getMount().getId() : -1));
            p.setByte(34, (entity.getCurrentTitle()));
            p.setInt(35, entity.getWife());
            p.setString(36, (entity.getMorphMode() ? 1 : 0) + ";" + entity.getMorphId());
            p.setString(37, entity.getAllTitle());
            p.setString(38, entity.parseEmoteToDB());
            p.setLong(39, (entity.isInEnnemyFaction ? entity.enteredOnEnnemyFaction : 0));
            p.setString(40, entity.parseStatsParcho());
            p.setLong(41, entity.getTimeTaverne());
            p.setBoolean(42, entity.noall);
            p.setString(43, entity.getDeathInformation());
            p.setByte(44, entity.getDeathCount());
            p.setLong(45, entity.getTotalKills());
            p.setInt(46, entity.getId());
            execute(p);
            if (entity.getGuildMember() != null)
                ((GuildMemberData) DatabaseManager.get(GuildMemberData.class)).update(entity);
            if (entity.getMount() != null)
                ((MountData) DatabaseManager.get(MountData.class)).update(entity.getMount());
        } catch (Exception e) {
            super.sendError(e);
        } finally {
            close(p);
        }

        try {
            if (entity.getQuestPerso() != null && !entity.getQuestPerso().isEmpty())
                entity.getQuestPerso().values().stream().filter(Objects::nonNull).forEach(QP -> ((QuestPlayerData) DatabaseManager.get(QuestPlayerData.class)).update(QP));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Class<?> getReferencedClass() {
        return PlayerData.class;
    }

    private HashMap<Integer, Integer> getStats(ResultSet result) throws SQLException {
        HashMap<Integer, Integer> stats = new HashMap<>();
        stats.put(Constant.STATS_ADD_VITA, result.getInt("vitalite"));
        stats.put(Constant.STATS_ADD_FORC, result.getInt("force"));
        stats.put(Constant.STATS_ADD_SAGE, result.getInt("sagesse"));
        stats.put(Constant.STATS_ADD_INTE, result.getInt("intelligence"));
        stats.put(Constant.STATS_ADD_CHAN, result.getInt("chance"));
        stats.put(Constant.STATS_ADD_AGIL, result.getInt("agilite"));
        return stats;
    }

    public Player loadClone(int id) {
        ResultSet result = null;
        Player player = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + " WHERE id = '" + id + "';");
            while (result.next()) {
                if (result.getInt("server") != Config.gameServerId)
                    continue;

                player = new Player(result.getInt("id"), result.getString("name"), result.getInt("groupe"), result.getInt("sexe"),
                        result.getInt("class"), result.getInt("color1"), result.getInt("color2"), result.getInt("color3"), result.getLong("kamas"),
                        result.getInt("spellboost"), result.getInt("capital"), result.getInt("energy"), result.getInt("level"), result.getLong("xp"),
                        result.getInt("size"), result.getInt("gfx"), result.getByte("alignement"), result.getInt("account"), this.getStats(result),
                        result.getByte("seeFriend"), result.getByte("seeAlign"), result.getByte("seeSeller"), result.getString("canaux"),
                        result.getShort("map"), result.getInt("cell"), result.getString("objets"), result.getString("storeObjets"),
                        result.getInt("pdvper"), result.getString("spells"), result.getString("savepos"), result.getString("jobs"),
                        result.getInt("mountxpgive"), result.getInt("mount"), result.getInt("honor"), result.getInt("deshonor"),
                        result.getInt("alvl"), result.getString("zaaps"), result.getByte("title"), result.getInt("wife"),
                        result.getString("morphMode"), result.getString("allTitle"), result.getString("emotes"), result.getLong("prison"),
                        false, result.getString("parcho"), result.getLong("timeDeblo"), result.getBoolean("noall"),
                        result.getString("deadInformation"), result.getByte("deathCount"), result.getLong("totalKills"));

                player.VerifAndChangeItemPlace();
            }
        } catch (SQLException e) {
            super.sendError(e);
            Main.stop("unknown");
        } finally {
            close(result);
        }
        return player;
    }
    
    public void loadByAccountId(int id) {
        try {
            Account account = World.world.getAccount(id);
            if (account != null)
                if (account.getPlayers() != null)
                    account.getPlayers().values().stream().filter(Objects::nonNull).forEach(World.world::verifyClone);
        } catch (Exception e) {
            super.sendError(e);
        }

        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + " WHERE account = '" + id + "'");
            
            while (result.next()) {
                if (result.getInt("server") != Config.gameServerId)
                    continue;

                Player p = World.world.getPlayer(result.getInt("id"));
                if (p != null) {
                    if (p.getFight() != null) {
                        continue;
                    }
                }

                HashMap<Integer, Integer> stats = new HashMap<Integer, Integer>();

                stats.put(Constant.STATS_ADD_VITA, result.getInt("vitalite"));
                stats.put(Constant.STATS_ADD_FORC, result.getInt("force"));
                stats.put(Constant.STATS_ADD_SAGE, result.getInt("sagesse"));
                stats.put(Constant.STATS_ADD_INTE, result.getInt("intelligence"));
                stats.put(Constant.STATS_ADD_CHAN, result.getInt("chance"));
                stats.put(Constant.STATS_ADD_AGIL, result.getInt("agilite"));
                Player player = new Player(result.getInt("id"), result.getString("name"), result.getInt("groupe"), result.getInt("sexe"), result.getInt("class"), result.getInt("color1"), result.getInt("color2"), result.getInt("color3"), result.getLong("kamas"), result.getInt("spellboost"), result.getInt("capital"), result.getInt("energy"), result.getInt("level"), result.getLong("xp"), result.getInt("size"), result.getInt("gfx"), result.getByte("alignement"), result.getInt("account"), stats, result.getByte("seeFriend"), result.getByte("seeAlign"), result.getByte("seeSeller"), result.getString("canaux"), result.getShort("map"), result.getInt("cell"), result.getString("objets"), result.getString("storeObjets"), result.getInt("pdvper"), result.getString("spells"), result.getString("savepos"), result.getString("jobs"), result.getInt("mountxpgive"), result.getInt("mount"), result.getInt("honor"), result.getInt("deshonor"), result.getInt("alvl"), result.getString("zaaps"), result.getByte("title"), result.getInt("wife"), result.getString("morphMode"), result.getString("allTitle"), result.getString("emotes"), result.getLong("prison"), false, result.getString("parcho"), result.getLong("timeDeblo"), result.getBoolean("noall"), result.getString("deadInformation"), result.getByte("deathCount"), result.getLong("totalKills"));

                if(p != null)
                        player.setNeededEndFight(p.needEndFight(), p.hasMobGroup());
                player.VerifAndChangeItemPlace();
                World.world.addPlayer(player);
                //TODO: Need to finihs database system
                /*int guild = DatabaseManager.getDynamics().getGuildMemberData().isPersoInGuild(result.getInt("id"));
                if (guild >= 0)
                    player.setGuildMember(World.world.getGuild(guild).getMember(result.getInt("id")));*/
            }
        } catch (SQLException e) {
            super.sendError(e);
            Main.stop("unknown");
        } finally {
            close(result);
        }
    }

    public String loadTitles(int guid) {
        ResultSet result = null;
        String title = "";
        try {
            result = getData("SELECT * FROM " + getTableName() + " WHERE id = '" + guid + "';");
            
            if (result.next()) {
                title = result.getString("allTitle");
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
        return title;
    }

    public void updateInfos(Player perso) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `name` = ?, `sexe`=?, `class`= ?, `color1` = ?, `color2` = ?, `color3` = ? WHERE `id`= ?;");
            p.setString(1, perso.getName());
            p.setInt(2, perso.getSexe());
            p.setInt(3, perso.getClasse());
            p.setInt(4, perso.getColor1());
            p.setInt(5, perso.getColor2());
            p.setInt(6, perso.getColor3());
            p.setInt(7, perso.getId());
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    public void updateGroupe(int group, String name) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `groupe` = ? WHERE `name` = ?;");

            p.setInt(1, group);
            p.setString(2, name);
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    public void updateGroupe(Player perso) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `groupe` = ? WHERE `id`= ?");
            int id = (perso.getGroupe() != null) ? perso.getGroupe().getId() : -1;
            p.setInt(1, id);
            p.setInt(2, perso.getId());
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    public void updateTimeTaverne(Player player) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `timeDeblo` = ? WHERE `id` = ?");
            p.setLong(1, player.getTimeTaverne());
            p.setInt(2, player.getId());
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    public void updateTitles(int guid, String title) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `allTitle` = ? WHERE `id` = ?");
            p.setString(1, title);
            p.setInt(2, guid);
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    public void updateLogged(int guid, int logged) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `logged` = ? WHERE `id` = ?");
            p.setInt(1, logged);
            p.setInt(2, guid);
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    public void updateAllLogged(int guid, int logged) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `logged` = ? WHERE `account` = ?");
            if(p != null) {
                p.setInt(1, logged);
                p.setInt(2, guid);
                execute(p);
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            if(p != null) close(p);
        }
    }

    public boolean exist(String name) {
        ResultSet result = null;
        boolean exist = false;
        try {
            result = getData("SELECT COUNT(*) AS exist FROM " + getTableName() + " WHERE name LIKE '" + name + "';");
            if (result.next()) {
                if (result.getInt("exist") > 0)
                    exist = true;
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
        return exist;
    }

    public String haveOtherPlayer(int account) {
        ResultSet result = null;
        String servers = "";
        try {
            result = getData("SELECT server FROM " + getTableName() + " WHERE account = '"
                    + account + "' AND NOT server = '" + Config.gameServerId + "'");
            while (result.next()) {
                servers += (servers.isEmpty() ? result.getInt("server") : ","
                        + result.getInt("server"));
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
        return servers;
    }

    public void reloadGroup(Player p) {
        ResultSet result = null;
        try {
            result = getData("SELECT groupe FROM " + getTableName() + " WHERE id = '" + p.getId() + "'");
            if (result.next()) {
                int group = result.getInt("groupe");
                Group g = Group.getGroupeById(group);
                p.setGroupe(g, false);
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
    }

    public byte canRevive(Player player) {
        ResultSet result = null;
        byte revive = 0;
        try {
            result = getData("SELECT id, revive FROM " + getTableName() + " WHERE `id` = '" + player.getId() + "';");
            while (result.next())
                revive = result.getByte("revive");
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
        return revive;
    }

    public void setRevive(Player player) {
        try {
            PreparedStatement p = getPreparedStatement("UPDATE " + getTableName() + " SET `revive` = 0 WHERE `id` = '" + player.getId() + "';");
            execute(p);
            close(p);
        } catch (SQLException e) {
            super.sendError(e);
        }
    }
}
