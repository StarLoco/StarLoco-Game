package org.starloco.locos.database.data;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * Created by Locos on 14/07/2017.
 */
public abstract class FunctionDAO<T> implements DAO<T> {
    public interface ResultSetFunction<R> {
        R apply(ResultSet rs) throws SQLException;
    }
    public interface ResultSetConsumer {
        void apply(ResultSet rs) throws SQLException;
    }

    private final Object locker = new Object();
    private String tableName;
    protected HikariDataSource dataSource;
    protected Logger logger = (Logger) LoggerFactory.getLogger(this.getReferencedClass());

    public FunctionDAO(HikariDataSource dataSource) {
        this.dataSource = dataSource;
        logger.setLevel(Level.ERROR);
    }

    public FunctionDAO(HikariDataSource dataSource, String tableName) {
        this.dataSource = dataSource;
        this.tableName = tableName;
        logger.setLevel(Level.ERROR);
    }

    public String getTableName() {
        return "`" + tableName + "`";
    }

    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    protected void execute(String query) {
        synchronized (locker) {
            try(Connection connection = this.getConnection()){
                try(Statement statement = connection.createStatement()) {
                    statement.execute(query);
                    logger.debug("SQL request executed successfully {}", query);
                }
            } catch (SQLException e) {
                logger.error("Can't execute SQL Request :" + query, e);
            }
        }
    }

    protected void execute(PreparedStatement statement) {
        synchronized (locker) {
            try {
                statement.execute();
                logger.debug("SQL request executed successfully {}", statement.toString());
            } catch (SQLException e) {
                logger.error("Can't execute SQL Request :" + statement.toString(), e);
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

    protected void getData(String query, ResultSetConsumer consumer) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            try (Statement stat = conn.createStatement()) {
                consumer.apply(stat.executeQuery(query));
            }
        }
    }

    protected <R> R getData(String query, ResultSetFunction<R> consumer) throws SQLException {
        try(Connection conn = dataSource.getConnection()) {
            try(Statement stat = conn.createStatement()) {
                return consumer.apply(stat.executeQuery(query));
            }
        }
    }

    protected void close(PreparedStatement statement) {
        if (statement != null) {
            try {
                if (!statement.isClosed()) {
                    statement.clearParameters();
                    statement.close();
                    Connection connection = statement.getConnection();
                    if (connection != null && !connection.isClosed()) {
                        connection.close();
                        logger.trace("{} released", connection);
                    }
                }
            } catch (Exception e) {
                logger.error("Can't close statement", e);
            }
        }
    }

    protected void sendError(Exception e) {
        logger.error("Error in " + this.getReferencedClass().getName() + " : " + e.getMessage(), e);
    }
}
