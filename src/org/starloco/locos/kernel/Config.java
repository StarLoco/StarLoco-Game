package org.starloco.locos.kernel;

import org.starloco.locos.exchange.ExchangeClient;
import org.starloco.locos.game.GameServer;

import java.io.*;
import java.util.Properties;

public class Config {

    public static final long startTime = System.currentTimeMillis();
    public static boolean isRunning = false, isSaving = false,
            encryption, debug, logs, subscription, autoReboot, resetLimit, maxPets,
            allZaap, allEmotes, allowMulePvp, modeChristmas, modeHalloween, modeHeroic, modeEvent;

    public static GameServer gameServer;
    public static ExchangeClient exchangeClient;

    //database
    public static String databaseLoginHost, databaseLoginUser, databaseLoginPass, databaseGameHost, databaseGameUser, databaseGamePass;
    public static int databaseLoginPort, databaseGamePort;
    public static String databaseLoginName, databaseGameName;

    //network
    public static String gameIp, exchangeIp, version, gameServerKey, startMessage;
    public static int gameServerId, gamePort, exchangePort, startMap, startCell, startKamas, startLevel, limitByIp, timeBetweenEvent;
    public static short rateXp, rateDrop, rateHonor, rateJob, rateKamas, rateFm;

    public static void verify(String name) {
        if(new File(name).exists()) load(name);
        else create(name);
    }

    private static void load(String name) {
        Properties properties = new Properties();
        FileInputStream fileInputStream;

        try { fileInputStream = new FileInputStream(name);
        } catch(Exception e) {
            Main.logger.error(" > Config : file not found !");
            verify(name);
            return;
        }
        try { properties.load(fileInputStream);
        } catch(Exception e) {
            Main.logger.error(" > Config : can't load file !");
            verify(name);
            return;
        }
        try { fileInputStream.close();
        } catch (IOException e) {
            Main.logger.error(" > Config : can't close file !");
            verify(name);
            return;
        }

        int i = 5;

        try {
            Config.exchangeIp = properties.getProperty(Params.EXCHANGE_IP.toString()); i++;
            Config.exchangePort = Integer.parseInt(properties.getProperty(Params.EXCHANGE_PORT.toString())); i++;
            Config.encryption = Boolean.parseBoolean(properties.getProperty(Params.ENCRYPTION.toString())); i++;
            Config.debug = Boolean.parseBoolean(properties.getProperty(Params.DEBUG.toString())); i++;
            Config.logs = Boolean.parseBoolean(properties.getProperty(Params.LOGS.toString())); i = 12;

            Config.databaseLoginHost = properties.getProperty(Params.LOGIN_DB_HOST.toString()); i++;
            Config.databaseLoginPort = Integer.parseInt(properties.getProperty(Params.LOGIN_DB_PORT.toString())); i++;
            Config.databaseLoginUser = properties.getProperty(Params.LOGIN_DB_USER.toString()); i++;
            Config.databaseLoginPass = properties.getProperty(Params.LOGIN_DB_PASS.toString()); i++;
            Config.databaseLoginName = properties.getProperty(Params.LOGIN_DB_NAME.toString()); i = 18;

            Config.databaseGameHost = properties.getProperty(Params.GAME_DB_HOST.toString()); i++;
            Config.databaseGamePort = Integer.parseInt(properties.getProperty(Params.GAME_DB_PORT.toString())); i++;
            Config.databaseGameUser = properties.getProperty(Params.GAME_DB_USER.toString()); i++;
            Config.databaseGamePass = properties.getProperty(Params.GAME_DB_PASS.toString()); i++;
            Config.databaseGameName = properties.getProperty(Params.GAME_DB_NAME.toString()); i = 25;

            Config.gameIp = properties.getProperty(Params.GAME_IP.toString()); i++;
            Config.gamePort = Integer.parseInt(properties.getProperty(Params.GAME_PORT.toString())); i++;
            Config.gameServerId = Integer.parseInt(properties.getProperty(Params.GAME_SERVER_ID.toString())); i++;
            Config.gameServerKey = properties.getProperty(Params.GAME_SERVER_KEY.toString()); i++;
            Config.version = properties.getProperty(Params.VERSION.toString()); i++;
            Config.rateXp = Short.parseShort(properties.getProperty(Params.RATE_XP.toString())); i++;
            Config.rateDrop = Short.parseShort(properties.getProperty(Params.RATE_DROP.toString())); i++;
            Config.rateHonor = Short.parseShort(properties.getProperty(Params.RATE_HONOR.toString())); i++;
            Config.rateKamas = Short.parseShort(properties.getProperty(Params.RATE_KAMAS.toString())); i++;
            Config.rateJob = Short.parseShort(properties.getProperty(Params.RATE_JOB.toString())); i++;
            Config.rateFm = Short.parseShort(properties.getProperty(Params.RATE_FM.toString())); i++;
            Config.startMessage = properties.getProperty(Params.START_MESSAGE.toString()); i++;
            Config.startLevel = Integer.parseInt(properties.getProperty(Params.START_LEVEL.toString())); i++;
            Config.startKamas = Integer.parseInt(properties.getProperty(Params.START_KAMAS.toString())); i++;
            Config.startMap = Integer.parseInt(properties.getProperty(Params.START_MAP.toString())); i++;
            Config.startCell = Integer.parseInt(properties.getProperty(Params.START_CELL.toString())); i++;
            Config.limitByIp = Integer.parseInt(properties.getProperty(Params.LIMIT_BY_IP.toString())); i++;
            Config.subscription = Boolean.parseBoolean(properties.getProperty(Params.SUBSCRIPTION.toString())); i++;
            Config.autoReboot = Boolean.parseBoolean(properties.getProperty(Params.AUTO_REBOOT.toString())); i++;
            Config.resetLimit = Boolean.parseBoolean(properties.getProperty(Params.RESET_LIMIT.toString())); i++;
            Config.maxPets = Boolean.parseBoolean(properties.getProperty(Params.MAX_PETS.toString())); i++;
            Config.allZaap = Boolean.parseBoolean(properties.getProperty(Params.ALL_ZAAP.toString())); i++;
            Config.allEmotes = Boolean.parseBoolean(properties.getProperty(Params.ALL_EMOTES.toString())); i++;
            Config.allowMulePvp = Boolean.parseBoolean(properties.getProperty(Params.ALLOW_MULE_PVP.toString())); i++;
            Constant.TIME_BY_TURN = Integer.parseInt(properties.getProperty(Params.TIME_BY_TURN.toString())) * 1000; i++;
            Config.modeChristmas = Boolean.parseBoolean(properties.getProperty(Params.MODE_CHRISTMAS.toString())); i++;
            Config.modeHalloween = Boolean.parseBoolean(properties.getProperty(Params.MODE_HALLOWEEN.toString())); i++;
            Config.modeHeroic = Boolean.parseBoolean(properties.getProperty(Params.MODE_HEROIC.toString())); i++;
            Config.modeEvent = Boolean.parseBoolean(properties.getProperty(Params.MODE_EVENT.toString())); i++;
            Config.timeBetweenEvent = Integer.parseInt(properties.getProperty(Params.TIME_BETWEEN_EVENT.toString()));
        } catch(Exception e) {
            Main.logger.error(" > Config : not found or invalid parameters! (line " + i + ")");
            verify(name);
            return;
        }

        Main.logger.info(" > Config : config loaded with success !");
    }

    private static void create(String name) {
        Main.logger.info(" > Config : the configuration file don't exist !");
        BufferedWriter config;

        try {
            config = new BufferedWriter(new FileWriter(name, true));
        } catch (IOException e) {
            Main.logger.error(" > Config : can't create file " + name + " !");
            verify(name);
            return;
        }

        Main.logger.info(" > Config : the configuration file was created with successful !");
        write(name, config);
    }

    private static void write(String name, BufferedWriter config) {
        if(config == null) {
            Main.logger.error(" > Config : config is null !");
            verify(name);
            return;
        }

        StringBuilder sb = new StringBuilder();

        sb
                .append("# StarLoco - Game. By Locos.")
                .append("#Auto-Generated Config File\n")
                .append("\n")
                .append("#System\n")
                .append(Params.EXCHANGE_IP).append(" 127.0.0.1\n")
                .append(Params.EXCHANGE_PORT).append(" 666\n")
                .append(Params.ENCRYPTION).append(" false\n")
                .append(Params.DEBUG).append(" true\n")
                .append(Params.LOGS).append(" true\n")
                .append("\n")
                .append("#Login database\n")
                .append(Params.LOGIN_DB_HOST).append(" 127.0.0.1\n")
                .append(Params.LOGIN_DB_PORT).append(" 3306\n")
                .append(Params.LOGIN_DB_USER).append(" root\n")
                .append(Params.LOGIN_DB_PASS).append(" \n")
                .append(Params.LOGIN_DB_NAME).append(" starloco_login\n")
                .append("#Game database\n")
                .append(Params.GAME_DB_HOST).append(" 127.0.0.1\n")
                .append(Params.GAME_DB_PORT).append(" 3306\n")
                .append(Params.GAME_DB_USER).append(" root\n")
                .append(Params.GAME_DB_PASS).append(" \n")
                .append(Params.GAME_DB_NAME).append(" starloco_game\n")
                .append("\n")
                .append("#Game server\n")
                .append(Params.GAME_IP).append(" 127.0.0.1\n")
                .append(Params.GAME_PORT).append(" 5555\n")
                .append(Params.GAME_SERVER_ID).append(" 601\n")
                .append(Params.GAME_SERVER_KEY).append(" eratz\n")
                .append(Params.VERSION).append(" 1.37.10\n")
                .append(Params.RATE_XP).append(" 1\n")
                .append(Params.RATE_DROP).append(" 1\n")
                .append(Params.RATE_HONOR).append(" 1\n")
                .append(Params.RATE_KAMAS).append(" 1\n")
                .append(Params.RATE_JOB).append(" 1\n")
                .append(Params.RATE_FM).append(" 1\n")
                .append(Params.START_MESSAGE).append(" \n")
                .append(Params.START_LEVEL).append(" 1\n")
                .append(Params.START_KAMAS).append(" 0\n")
                .append(Params.START_MAP).append(" -1\n")
                .append(Params.START_CELL).append(" -1\n")
                .append(Params.LIMIT_BY_IP).append(" 8\n")
                .append(Params.SUBSCRIPTION).append(" false\n")
                .append(Params.AUTO_REBOOT).append(" false\n")
                .append(Params.RESET_LIMIT).append(" true\n")
                .append(Params.MAX_PETS).append(" false\n")
                .append(Params.ALL_ZAAP).append(" false\n")
                .append(Params.ALL_EMOTES).append(" false\n")
                .append(Params.ALLOW_MULE_PVP).append(" false\n")
                .append(Params.TIME_BY_TURN).append(" 30\n")
                .append(Params.MODE_CHRISTMAS).append(" false\n")
                .append(Params.MODE_HALLOWEEN).append(" false\n")
                .append(Params.MODE_HEROIC).append(" false\n")
                .append(Params.MODE_EVENT).append(" false\n")
                .append(Params.TIME_BETWEEN_EVENT).append(" 60\n");

        try {
            config.write(sb.toString());
            config.flush();
            config.close();
        } catch (IOException e) {
            Main.logger.error(" > Config : error on writing !");
            verify(name);
            return;
        }

        waiting(name);
    }

    private static void waiting(String name) {
        for(int i = 0; i < 10; i++) {
            Main.logger.info(" > Config : please, set the config properties, reloading into " + (10 - i) + "s.");
            try { Thread.sleep(1000);
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
        verify(name);
    }

    public static boolean isVersionGreaterThan(String version) {
        return Config.version.compareTo(version) >= 0;
    }

    private enum Params {
        EXCHANGE_IP("system.server.exchange.ip"),
        EXCHANGE_PORT("system.server.exchange.port"),
        GAME_IP("system.server.game.ip"),
        GAME_PORT("system.server.game.port"),
        GAME_SERVER_ID("system.server.game.id"),
        GAME_SERVER_KEY("system.server.game.key"),
        VERSION("system.server.game.version"),
        RATE_XP("system.server.game.rate.xp"),
        RATE_DROP("system.server.game.rate.drop"),
        RATE_HONOR("system.server.game.rate.honor"),
        RATE_JOB("system.server.game.rate.job"),
        RATE_KAMAS("system.server.game.rate.kamas"),
        RATE_FM("system.server.game.rate.fm"),
        START_MESSAGE("system.server.game.start.message"),
        START_KAMAS("system.server.game.start.kamas"),
        START_LEVEL("system.server.game.start.level"),
        START_MAP("system.server.game.start.map"),
        START_CELL("system.server.game.start.cell"),
        LIMIT_BY_IP("system.server.game.limitByIp"),
        ENCRYPTION("system.server.encyption"),
        DEBUG("system.server.debug"),
        LOGS("system.server.logs"),
        SUBSCRIPTION("system.server.game.subscription"),
        AUTO_REBOOT("system.server.game.autoReboot"),
        RESET_LIMIT("system.server.game.resetLimit"),
        MAX_PETS("system.server.game.maxPets"),
        ALL_ZAAP("system.server.game.allZaap"),
        ALL_EMOTES("system.server.game.allEmotes"),
        ALLOW_MULE_PVP("system.server.game.allowMulePvp"),
        TIME_BY_TURN("system.server.game.timeByTurn"),
        MODE_CHRISTMAS("system.server.game.mode.christmas"),
        MODE_HALLOWEEN("system.server.game.mode.halloween"),
        MODE_HEROIC("system.server.game.mode.heroic"),
        MODE_EVENT("system.server.game.mode.event"),
        TIME_BETWEEN_EVENT("system.server.game.timeBetweenEvent"),
        GAME_DB_HOST("database.game.host"),
        GAME_DB_PORT("database.game.port"),
        GAME_DB_USER("database.game.user"),
        GAME_DB_PASS("database.game.pass"),
        GAME_DB_NAME("database.game.name"),
        LOGIN_DB_HOST("database.login.host"),
        LOGIN_DB_PORT("database.login.port"),
        LOGIN_DB_USER("database.login.user"),
        LOGIN_DB_PASS("database.login.pass"),
        LOGIN_DB_NAME("database.login.name");

        private final String params;

        Params(String params){
            this.params = params;
        }

        public String toString(){
            return params;
        }
    }
}