package org.starloco.locos.kernel;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.fusesource.jansi.AnsiConsole;
import org.slf4j.LoggerFactory;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.area.map.entity.InteractiveObject;
import org.starloco.locos.auction.AuctionManager;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.game.HeroicMobsGroupsData;
import org.starloco.locos.database.data.login.ServerData;
import org.starloco.locos.entity.monster.Monster;
import org.starloco.locos.entity.mount.Mount;
import org.starloco.locos.event.EventManager;
import org.starloco.locos.exchange.ExchangeClient;
import org.starloco.locos.game.GameServer;
import org.starloco.locos.game.scheduler.Updatable;
import org.starloco.locos.game.scheduler.entity.WorldPlayerOption;
import org.starloco.locos.game.scheduler.entity.WorldPub;
import org.starloco.locos.game.scheduler.entity.WorldSave;
import org.starloco.locos.game.world.World;
import org.starloco.locos.util.TimerWaiter;

import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {

    public static Logger logger = (Logger) LoggerFactory.getLogger(Main.class);
    public static final List<Runnable> runnables = new LinkedList<>();

    public static short angels = 0, demons  = 0;
    public static boolean mapAsBlocked = false, fightAsBlocked = false, tradeAsBlocked = false;

    public static void main(String[] args) throws SQLException {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (Config.isRunning) {
                GameServer.setState(0);
                Config.isRunning = false;
                Main.fightAsBlocked = tradeAsBlocked = true;

                if(Config.gameServer != null)
                    Config.gameServer.kickAll(true);

                Logging.getInstance().stop();
                DatabaseManager.get(ServerData.class).loadFully();
            }
            Main.logger.info("The server is now closed.");
        }));

        try {
            System.setOut(new PrintStream(System.out, true, "IBM850"));
            if (!new File("Logs/Error").exists()) new File("Logs/Error").mkdir();
            System.setErr(new PrintStream(Files.newOutputStream(Paths.get("Logs/Error/" + new SimpleDateFormat("dd-MM-yyyy - HH-mm-ss", Locale.FRANCE).format(new Date()) + ".log"))));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Main.start();
    }

    public static void start() {
        Main.logger.info("You use " + System.getProperty("java.vendor") + " with the version " + System.getProperty("java.version"));
        Main.logger.debug("Starting of the server : " + new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss", Locale.FRANCE).format(new Date()));
        Main.logger.debug("Current timestamp ms : " + System.currentTimeMillis());
        Main.logger.debug("Current timestamp ns : " + System.nanoTime());

        Config.verify("game.config.properties");
        Logging.getInstance().initialize();

        if(DatabaseManager.getInstance().isConnected()) {
            Config.isRunning = true;
	        World.world.createWorld();

            new GameServer().initialize();
            new ExchangeClient().initialize();

            Main.logger.info("The server is ready ! Waiting for connection..\n");

	        if (!Config.debug) {
	            ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
	            root.setLevel(Level.OFF);
	        }

	        final List<Updatable> updatables = Arrays.asList(
	                WorldSave.updatable, GameMap.updatable, InteractiveObject.updatable, Mount.updatable,
                    WorldPlayerOption.updatable, WorldPub.updatable,
                    AuctionManager.getInstance(), /*Tavernier.getInstance(),*/ EventManager.getInstance());

            while (Config.isRunning) {
                try {
                    for (Updatable updatable : updatables) {
                        if (!Config.isRunning)
                            break;

                        if (updatable != null)
                            try {
                                updatable.update();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                    }

                    if (!Main.runnables.isEmpty()) {
                        Iterator<Runnable> iterator = Main.runnables.iterator();
                        while (iterator.hasNext()) {
                            try {
                                Runnable runnable = iterator.next();
                                if (runnable != null) runnable.run();
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                iterator.remove();
                            }
                        }
                    }

                    try {
                        if(Config.isRunning) Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            Main.logger.error("An error occurred when the server have try a connection on the Mysql server. Please check your identification.");
        }
    }

    public static void stop(String reason) {
        Logging.getInstance().write("Error", reason);

        GameServer.setState(0);
        ((HeroicMobsGroupsData) DatabaseManager.get(HeroicMobsGroupsData.class)).deleteAll();
        ((HeroicMobsGroupsData) DatabaseManager.get(HeroicMobsGroupsData.class)).deleteAllFix();

        for (GameMap map : World.world.getMaps()) {
            for (Monster.MobGroup group : map.getMobGroups().values()) {
                if (!group.isFix())
                    ((HeroicMobsGroupsData) DatabaseManager.get(HeroicMobsGroupsData.class)).insert(map.getId(), group);
                else
                    ((HeroicMobsGroupsData) DatabaseManager.get(HeroicMobsGroupsData.class)).insertFix(map.getId(), group);
            }
        }

        WorldSave.cast(0);
        GameServer.setState(0);
        checkStop();
    }

    private static void checkStop() {
        if(!Config.isSaving)
            System.exit(0);
        else
            TimerWaiter.addNext(Main::checkStop, 5000);
    }

    public static void clear() { //~30ms
        AnsiConsole.out.print("\033[H\033[2J");
    }
}
