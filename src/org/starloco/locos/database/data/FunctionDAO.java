package org.starloco.locos.database.data;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.LoggerFactory;
import org.starloco.locos.database.DatabaseManager;

import java.sql.*;

/**
 * Created by Locos on 14/07/2017.
 */
public abstract class FunctionDAO<T> implements DAO<T> {

    private final Object locker = new Object();
    private String tableName;
    protected HikariDataSource dataSource;
    protected Logger logger = (Logger) LoggerFactory.getLogger(this.getReferencedClass());

    public FunctionDAO(HikariDataSource dataSource) {
        this.dataSource = dataSource;
        logger.setLevel(Level.OFF);
    }

    public FunctionDAO(HikariDataSource dataSource, String tableName) {
        this.dataSource = dataSource;
        this.tableName = tableName;
        logger.setLevel(Level.OFF);
    }

    public String getTableName() {
        return "`" + tableName + "`";
    }

    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    protected void execute(String query) {
        synchronized (locker) {
            Statement statement = null;
            Connection connection = null;
            try {
                connection = this.getConnection();
                statement = connection.createStatement();
                statement.execute(query);
                logger.debug("SQL request executed successfully {}", query);
            } catch (SQLException e) {
                logger.error("Can't execute SQL Request :" + query, e);
            } finally {
                close(statement);
                close(connection);
            }
        }
    }

    protected void execute(PreparedStatement statement) {
        synchronized (locker) {
            Connection connection = null;
            try {
                connection = statement.getConnection();
                statement.execute();
                logger.debug("SQL request executed successfully {}", statement.toString());
            } catch (SQLException e) {
                logger.error("Can't execute SQL Request :" + statement.toString(), e);
            } finally {
                close(statement);
                close(connection);
            }
        }
    }

    protected PreparedStatement getPreparedStatement(String query) throws SQLException {
        try {
            return this.getConnection().prepareStatement(query);
        } catch (SQLException e) {
            logger.error("Can't execute prepared statement on datasource connection", e);
            return null;
        }
    }

    protected ResultSet getData(String query) {
        synchronized (locker) {
            ResultSet result = null;
            try {
                if (!query.endsWith(";")) query += ";";
                Connection connection = this.getConnection();
                Statement statement = connection.createStatement();
                DatabaseManager.closeStatementsUnused();
                result = statement.executeQuery(query);
                DatabaseManager.addToClose(statement);
                logger.debug("SQL request executed successfully {}", query);
            } catch (SQLException e) {
                logger.error("Can't execute SQL Request :" + query, e);
            }
            return result;
        }
    }

    protected void close(PreparedStatement statement) {
        if (statement != null) {
            try {
                if (!statement.isClosed()) {
                    statement.clearParameters();
                    statement.close();
                    this.close(statement.getConnection());
                }
            } catch (Exception e) {
                logger.error("Can't close statement", e);
            }
        }
    }

    protected void close(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    logger.trace("{} released", connection);
                }
            } catch (Exception e) {
                logger.error("Can't close connection", e);
            }
        }
    }

    protected void close(Statement statement) {
        if (statement == null)
            return;
        try {
            if(!statement.isClosed()) {
                statement.close();
            }
        } catch (Exception e) {
            logger.error("Can't close statement", e);
        }
    }

    protected void close(ResultSet resultSet) {
        if (resultSet == null)
            return;
        try {
            if(!resultSet.isClosed())
                resultSet.close();
        } catch (Exception e) {
            logger.error("Can't close resultSet", e);
        }
    }

    protected void sendError(Exception e) {
        logger.error("Error in " + this.getReferencedClass().getName() + " : " + e.getMessage(), e);
    }
}
