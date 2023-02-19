package org.starloco.locos.database;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.LoggerFactory;
import org.starloco.locos.database.data.BaseData;
import org.starloco.locos.database.data.DAO;
import org.starloco.locos.database.data.game.*;
import org.starloco.locos.database.data.login.*;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Main;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class DatabaseManager {

    private static final DatabaseManager instance = new DatabaseManager();
    private static final List<Statement> statements = Collections.synchronizedList(new ArrayList<>());

    private final Logger logger = (Logger) LoggerFactory.getLogger(DatabaseManager.class);

    private final HikariDataSource login, game;

    private final List<DAO<?>> daos = new ArrayList<>();

    /**
     * Construct the two connection database
     */
    public DatabaseManager() {
        logger.trace("Reading database config");
        this.login = this.createHikariDataSource(Config.databaseLoginHost, String.valueOf(Config.databaseLoginPort), Config.databaseLoginName, Config.databaseLoginUser, Config.databaseLoginPass);
        logger.debug("Connection to the login database, ok");
        this.game = this.createHikariDataSource(Config.databaseGameHost, String.valueOf(Config.databaseGamePort), Config.databaseGameName, Config.databaseGameUser, Config.databaseGamePass);
        logger.debug("Connection to the game database, ok");
        this.initialize();
        logger.debug("All data have been initialized");
        logger.setLevel(Level.OFF);
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
        this.daos.add(new BaseData(null));

        //region login data
        this.daos.add(new AccountData(this.login));
        this.daos.add(new CommandData(this.login));
        this.daos.add(new EventData(this.login));
        this.daos.add(new PlayerData(this.login));
        this.daos.add(new ServerData(this.login));
        this.daos.add(new BanIpData(this.login));
        this.daos.add(new BaseAreaData(this.login));
        this.daos.add(new BaseSubAreaData(this.login));
        this.daos.add(new GuildData(this.login));
        this.daos.add(new GroupData(this.login));
        this.daos.add(new BaseHouseData(this.login));
        this.daos.add(new BaseTrunkData(this.login));
        this.daos.add(new MountData(this.login));
        this.daos.add(new BaseMountParkData(this.login));
        this.daos.add(new ObjectData(this.login));
        this.daos.add(new ObvijevanData(this.login));
        //this.daos.add(new PubData(this.login));
        this.daos.add(new PetData(this.login));
        this.daos.add(new QuestPlayerData(this.login));
        this.daos.add(new ShopObjectData(this.login));
        //endregion

        //region game data
        this.daos.add(new AreaData(this.game));
        this.daos.add(new AuctionData(this.game));
        this.daos.add(new GangsterData(this.game));
        this.daos.add(new BankData(this.game));
        this.daos.add(new TrunkData(this.game));
        this.daos.add(new GuildMemberData(this.game));
        this.daos.add(new HdvObjectData(this.game));
        this.daos.add(new HouseData(this.game));
        this.daos.add(new MountParkData(this.game));
        this.daos.add(new CollectorData(this.game));
        this.daos.add(new PrismData(this.game));
        this.daos.add(new SubAreaData(this.game));
        this.daos.add(new AnimationData(this.game));
        this.daos.add(new AreaData(this.game));
        this.daos.add(new ChallengeData(this.game));
        this.daos.add(new TrunkData(this.game));
        this.daos.add(new CraftData(this.game));
        this.daos.add(new DungeonData(this.game));
        this.daos.add(new DropData(this.game));
        this.daos.add(new EndFightActionData(this.game));
        this.daos.add(new ExperienceData(this.game));
        this.daos.add(new ExtraMonsterData(this.game));
        this.daos.add(new FullMorphData(this.game));
        this.daos.add(new GiftData(this.game));
        this.daos.add(new HdvData(this.game));
        this.daos.add(new HouseData(this.game));
        this.daos.add(new InteractiveDoorData(this.game));
        this.daos.add(new InteractiveObjectData(this.game));
        this.daos.add(new ObjectTemplateData(this.game));
        this.daos.add(new ObjectSetData(this.game));
        this.daos.add(new JobData(this.game));
        this.daos.add(new MapData(this.game));
        this.daos.add(new MonsterData(this.game));
        this.daos.add(new MountParkData(this.game));
        this.daos.add(new NpcQuestionData(this.game));
        this.daos.add(new NpcAnswerData(this.game));
        this.daos.add(new NpcTemplateData(this.game));
        this.daos.add(new NpcData(this.game));
        this.daos.add(new ObjectActionData(this.game));
        this.daos.add(new PetTemplateData(this.game));
        this.daos.add(new QuestData(this.game));
        this.daos.add(new QuestStepData(this.game));
        this.daos.add(new QuestObjectiveData(this.game));
        this.daos.add(new RuneData(this.game));
        this.daos.add(new ScriptedCellData(this.game));
        this.daos.add(new SubAreaData(this.game));
        this.daos.add(new SpellData(this.game));
        this.daos.add(new TutorialData(this.game));
        this.daos.add(new ZaapData(this.game));
        this.daos.add(new ZaapiData(this.game));
        this.daos.add(new HeroicMobsGroupsData(this.game));
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
        logger.error("Connection ok");
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
    public static DAO<?> get(Class<?> c) {
        for(DAO<?> dao : instance.daos) {
            if(dao.getReferencedClass().equals(c))
                return dao;
        }
        return get(BaseData.class); // escape warning, this could not happen
    }

    public static DatabaseManager getInstance() {
        return instance;
    }

    public static void addToClose(Statement statement) {
        DatabaseManager.statements.add(statement);
    }

    public static void closeStatementsUnused() throws SQLException {
        Iterator<Statement> statements = DatabaseManager.statements.iterator();

        while(statements.hasNext()) {
            Statement statement = statements.next();
            try {
                if(!statement.isClosed()) {
                    if (statement.getResultSet() != null && statement.getResultSet().isClosed()) {
                        statement.close();
                        statement.getConnection().close();
                    } else if (statement.getResultSet() == null) {
                        statement.close();
                        statement.getConnection().close();
                    }
                    statements.remove();
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
