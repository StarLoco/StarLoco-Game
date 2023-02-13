package org.starloco.locos.database.data.login;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.client.Account;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.database.data.game.BankData;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Config;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountData extends FunctionDAO<Account> {

    public AccountData(HikariDataSource source) {
        super(source, "world_accounts");
    }
    
    @Override
    public void loadFully() {
        ResultSet result = null;

        try {
            result = super.getData("SELECT * FROM " + getTableName() + ";");

            while (result.next()) {
                String pseudo = result.getString("pseudo"), lastDate = result.getString("lastConnectionDate");

                if(pseudo == null || pseudo.isEmpty())
                    continue;
                Account a = new Account(result.getInt("guid"), result.getString("account").toLowerCase(), result.getString("pseudo"), result.getString("reponse"), (result.getInt("banned") == 1), result.getString("lastIP"), lastDate, result.getString("friends"), result.getString("enemy"), result.getInt("points"), result.getLong("subscribe"), result.getLong("muteTime"), result.getString("mutePseudo"), result.getString("lastVoteIP"), result.getString("heurevote"));
                World.world.addAccount(a);
                ((BankData) DatabaseManager.get(BankData.class)).load(a.getId());
            }
        } catch (Exception e) {
            super.sendError(e);
        } finally {
            close(result);
        }
    }

    @Override
    public Account load(int id) {
        ResultSet result = null;
        Account account = null;
        try {
            result = super.getData("SELECT * FROM " + getTableName() + " WHERE guid = " + id);

            while (result.next()) {
                account = World.world.getAccount(result.getInt("guid"));

                if (account == null || !account.isOnline()) {
                    Account acc = new Account(result.getInt("guid"), result.getString("account").toLowerCase(), result.getString("pseudo"), result.getString("reponse"), (result.getInt("banned") == 1), result.getString("lastIP"), result.getString("lastConnectionDate"), result.getString("friends"), result.getString("enemy"), result.getInt("points"), result.getLong("subscribe"), result.getLong("muteTime"), result.getString("mutePseudo"), result.getString("lastVoteIP"), result.getString("heurevote"));
                    World.world.addAccount(acc);
                    ((BankData) DatabaseManager.get(BankData.class)).load(acc.getId());
                    World.world.ReassignAccountToChar(acc);
                }
            }
        } catch (Exception e) {
            super.sendError(e);
        } finally {
            close(result);
        }
        return account;
    }

    @Override
    public boolean insert(Account entity) {
        throw new NotImplementedException("It's not allowed to insert account directly server-side");
    }

    @Override
    public void delete(Account entity) {
        throw new NotImplementedException("It's not allowed to delete account directly server-side");
    }

    //TODO: Account update all data
    @Override
    public void update(Account entity) {
        PreparedStatement statement = null;
        try {
            statement = getPreparedStatement("UPDATE " + getTableName() + " SET banned = '"
                    + (entity.isBanned() ? 1 : 0) + "', friends = '"
                    + entity.parseFriendListToDB() + "', enemy = '"
                    + entity.parseEnemyListToDB() + "', muteTime = '"
                    + entity.getMuteTime() + "', mutePseudo = '"
                    + entity.getMutePseudo() + "' WHERE guid = '" + entity.getId()
                    + "'");
            execute(statement);
        } catch (Exception e) {
            super.sendError(e);
        } finally {
            close(statement);
        }
    }

    @Override
    public Class<?> getReferencedClass() {
        return AccountData.class;
    }

    public long getSubscribe(int id) {
        long subscribe = 0;
        ResultSet result = null;
        try {
            result = super.getData("SELECT guid, subscribe FROM " + getTableName() + " WHERE guid = " + id);

            if(result != null) {
                while (result.next()) {
                    subscribe = result.getLong("subscribe");
                }
            }
        } catch (Exception e) {
            super.sendError(e);
        } finally {
            close(result);
        }
        return subscribe;
    }

    public void updateVoteAll() {
        ResultSet result = null;
        Account a = null;
        try {
            result = super.getData("SELECT guid, heurevote, lastVoteIP FROM " + getTableName() + ";");
            while (result.next()) {
                a = World.world.getAccount(result.getInt("guid"));
                if (a == null)
                    continue;
                a.updateVote(result.getString("heurevote"), result.getString("lastVoteIP"));
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
    }

    public void updateLastConnection(Account compte) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `lastIP` = ?, `lastConnectionDate` = ? WHERE `guid` = ?");
            p.setString(1, compte.getCurrentIp());
            p.setString(2, compte.getLastConnectionDate());
            p.setInt(3, compte.getId());
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    public void setLogged(int id, int logged) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `logged` = ? WHERE `guid` = ?;");
            if(p != null) {
                p.setInt(1, logged);
                p.setInt(2, id);
                execute(p);
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    public boolean updateBannedTime(Account acc, long time) {
        PreparedStatement statement = null;
        try {
            statement = getPreparedStatement("UPDATE " + getTableName() + " SET banned = '" + (acc.isBanned() ? 1 : 0) + "', bannedTime = '" + time + "' WHERE guid = '" + acc.getId() + "'");
            execute(statement);
            return true;
        } catch (Exception e) {
            super.sendError(e);
        } finally {
            close(statement);
        }
        return false;
    }

    public int loadPoints(String user) {
        return this.loadPointsWithoutUsersDb(user);
    }

    public void updatePoints(int id, int points) {
        this.updatePointsWithoutUsersDb(id, points);
    }

    public int loadPointsWithoutUsersDb(String user) {
        ResultSet result = null;
        int points = 0;
        try {
            result = super.getData("SELECT * FROM " + getTableName() + " WHERE `account` LIKE '" + user + "'");
            if (result.next()) {
                points = result.getInt("points");
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
        return points;
    }

    public void updatePointsWithoutUsersDb(int id, int points) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `points` = ? WHERE `guid` = ?");
            p.setInt(1, points);
            p.setInt(2, id);
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    public int loadPointsWithUsersDb(String account) {
        ResultSet result = null;
        int points = 0, user = -1;
        try {
            result = super.getData("SELECT account, users FROM " + getTableName() + " WHERE `account` LIKE '" + account + "'");
            
            if (result.next()) user = result.getInt("users");
            close(result);

            if(user == -1) {
                result = super.getData("SELECT id, points FROM `users` WHERE `id` = " + user + ";");
                if (result.next()) points = result.getInt("users");
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
        return points;
    }

    public void updatePointsWithUsersDb(int id, int points) {
        PreparedStatement p = null;
        int user = -1;
        try {
            ResultSet result = super.getData("SELECT guid, users FROM " + getTableName() + " WHERE `guid` LIKE '" + id + "'");
            
            if (result.next()) user = result.getInt("users");
            close(result);

            if(user != -1) {
                p = getPreparedStatement("UPDATE `users` SET `points` = ? WHERE `id` = ?;");
                p.setInt(1, points);
                p.setInt(2, id);
                execute(p);
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }
}