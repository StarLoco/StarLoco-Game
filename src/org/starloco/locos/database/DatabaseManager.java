package org.starloco.locos.database;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.LoggerFactory;
import org.starloco.locos.database.legacydata.DAO;
import org.starloco.locos.database.legacydata.game.*;
import org.starloco.locos.database.legacydata.login.*;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Main;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final DatabaseManager instance = new DatabaseManager();

    private final Logger logger = (Logger) LoggerFactory.getLogger(DatabaseManager.class);

    private final HikariDataSource login, game;

    private final List<DAO<?>> daos = new ArrayList<>();

    /**
     * Construct the two connection database
     */
    public DatabaseManager() {
        ((Logger) LoggerFactory.getLogger("com.zaxxer.hikari.HikariDataSource")).setLevel(Level.ERROR);
        ((Logger) LoggerFactory.getLogger("com.zaxxer.hikari.HikariConfig")).setLevel(Level.ERROR);
        ((Logger) LoggerFactory.getLogger("com.zaxxer.hikari.pool.HikariPool")).setLevel(Level.ERROR);
        ((Logger) LoggerFactory.getLogger("DEBUG com.zaxxer.hikari.pool.PoolBase")).setLevel(Level.ERROR);

        logger.trace("Reading database config");
        this.login = this.createHikariDataSource(Config.databaseLoginHost, String.valueOf(Config.databaseLoginPort), Config.databaseLoginName, Config.databaseLoginUser, Config.databaseLoginPass);
        logger.debug("Connection to the login database, ok");

        this.game = this.createHikariDataSource(Config.databaseGameHost, String.valueOf(Config.databaseGamePort), Config.databaseGameName, Config.databaseGameUser, Config.databaseGamePass);
        logger.debug("Connection to the game database, ok");

        this.initialize();
        logger.debug("All data have been initialized");
        logger.setLevel(Level.ERROR);
    }

    public boolean isConnected() {
        try {
            return login != null && login.getConnection() != null && !login.getConnection().isClosed() &&
                    game != null && game.getConnection() != null && !game.getConnection().isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     *  Initialiazation of all entities table
     */
    private void initialize() {

        //region login data
        this.daos.add(new AccountData(this.login));
        this.daos.add(new EventData(this.login));
        this.daos.add(new PlayerData(this.login));
        this.daos.add(new AdData(this.login));
        this.daos.add(new ServerData(this.login));
        this.daos.add(new BanIpData(this.login));
        this.daos.add(new BaseAreaData(this.login));
        this.daos.add(new BaseSubAreaData(this.login));
        this.daos.add(new GuildData(this.login));
        this.daos.add(new BaseHouseData(this.login));
        this.daos.add(new BaseTrunkData(this.login));
        this.daos.add(new MountData(this.login));
        this.daos.add(new BaseMountParkData(this.login));
        this.daos.add(new ObjectData(this.login));
        this.daos.add(new ObvijevanData(this.login));
        this.daos.add(new PetData(this.login));
        //endregion

        //region game data
        this.daos.add(new AreaData(this.game));
        this.daos.add(new AuctionData(this.game));
        this.daos.add(new GangsterData(this.game));
        this.daos.add(new BankData(this.game));
        this.daos.add(new TrunkData(this.game));
        this.daos.add(new GuildMemberData(this.game));
        this.daos.add(new BigStoreListingData(this.game));
        this.daos.add(new HouseData(this.game));
        this.daos.add(new MountParkData(this.game));
        this.daos.add(new CollectorData(this.game));
        this.daos.add(new PrismData(this.game));
        this.daos.add(new SubAreaData(this.game));
        this.daos.add(new AreaData(this.game));
        this.daos.add(new ChallengeData(this.game));
        this.daos.add(new TrunkData(this.game));
        this.daos.add(new CraftData(this.game));
        this.daos.add(new DropData(this.game));
        this.daos.add(new ExtraMonsterData(this.game));
        this.daos.add(new FullMorphData(this.game));
        this.daos.add(new GiftData(this.game));
        this.daos.add(new HdvData(this.game));
        this.daos.add(new HouseData(this.game));
        this.daos.add(new ObjectTemplateData(this.game));
        this.daos.add(new ObjectSetData(this.game));
        this.daos.add(new JobData(this.game));
        this.daos.add(new MonsterData(this.game));
        this.daos.add(new MountParkData(this.game));
        this.daos.add(new NpcData(this.game));
        this.daos.add(new ObjectActionData(this.game));
        this.daos.add(new PetTemplateData(this.game));
        this.daos.add(new RuneData(this.game));
        this.daos.add(new SubAreaData(this.game));
        this.daos.add(new SpellData(this.game));
        this.daos.add(new ZaapiData(this.game));
        this.daos.add(new HeroicMobsGroupsData(this.game));
        this.daos.add(new QuestProgressData(this.game));
        //endregion
    }

    /**
     * Create and try to connect to them
     * @param host ip address of database
     * @param port port of database
     * @param database name
     * @param user of the database
     * @param pass of the database
     * @return connection or null
     */
    private HikariDataSource createHikariDataSource(String host, String port, String database, String user, String pass) {
        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        config.addDataSourceProperty("serverName", host);
        config.addDataSourceProperty("port", port);
        config.addDataSourceProperty("databaseName", database);
        config.addDataSourceProperty("user", user);
        config.addDataSourceProperty("password", pass);
        config.setAutoCommit(true); // AutoCommit, c'est cool
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(1);
        HikariDataSource source = new HikariDataSource(config);

        if (!this.tryConnection(source)) {
            logger.error("Please check your username and password and database connection");
            Main.stop("statics try connection failed");
            return null;
        }

        return source;
    }

    /**
     * @param dataSource Hikari config
     * @return true if it's ok, otherwise false
     */
    private boolean tryConnection(HikariDataSource dataSource) {
        try {
            Connection connection = dataSource.getConnection();
            connection.close();
            return true;
        } catch (Exception e) {
            logger.error("error when trying to connect to data source", e);
            return false;
        }
    }

    /**
     * @param c the entity class of the dao
     * @return dao class of the entity
     */
    public static <D extends DAO<T>,T> D get(Class<D> c) {
        for(DAO<?> dao : instance.daos) {
            if(dao.getReferencedClass().equals(c))
                return (D) dao;
        }
        return null;
    }

    public static DatabaseManager getInstance() {
        return instance;
    }
}
