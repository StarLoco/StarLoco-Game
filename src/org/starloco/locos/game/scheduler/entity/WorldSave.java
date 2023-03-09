package org.starloco.locos.game.scheduler.entity;

import org.starloco.locos.client.Player;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.game.*;
import org.starloco.locos.database.data.login.*;
import org.starloco.locos.entity.monster.Monster;
import org.starloco.locos.game.GameServer;
import org.starloco.locos.game.scheduler.Updatable;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.object.GameObject;

import java.util.ArrayList;
import java.util.Objects;

public class WorldSave extends Updatable {

    public final static Updatable updatable = new WorldSave(60 * 60 * 1000);
    private static Thread thread;

    private WorldSave(int wait) {
        super(wait);
    }

    @Override
    public void update() {
        if(this.verify())
            if (!Config.isSaving) {
                thread = new Thread(() -> WorldSave.cast(1));
                thread.setName(WorldSave.class.getName());
                thread.setDaemon(true);
                thread.start();
            }
    }

    public static void cast(int trys) {
        if(trys != 0)
            GameServer.setState(2);

        try {
            World.world.logger.debug("Starting the save of the world..");
            SocketManager.GAME_SEND_Im_PACKET_TO_ALL("1164;");
            Config.isSaving = true;

            /* Save of data */
            World.world.logger.info("-> of accounts.");
            World.world.getAccounts().stream().filter(Objects::nonNull).forEach(account -> ((AccountData) DatabaseManager.get(AccountData.class)).update(account));

            World.world.logger.info("-> of players.");
            World.world.logger.info("-> of members of guilds.");
            World.world.getPlayers().stream().filter(Objects::nonNull).filter(Player::isOnline).forEach(player -> {
                ((PlayerData) DatabaseManager.get(PlayerData.class)).update(player);
                if (player.getGuildMember() != null)
                    ((GuildMemberData) DatabaseManager.get(GuildMemberData.class)).update(player);
            });

            World.world.logger.info("-> of prisms.");
            World.world.getPrisms().values().forEach(prism -> ((PrismData) DatabaseManager.get(PrismData.class)).update(prism));

            World.world.logger.info("-> of guilds.");
            World.world.getGuilds().values().forEach(guild -> ((GuildData) DatabaseManager.get(GuildData.class)).update(guild));

            World.world.logger.info("-> of collectors.");
            World.world.getCollectors().values().stream().filter(collector -> collector.getInFight() <= 0).forEach(collector -> ((CollectorData) DatabaseManager.get(CollectorData.class)).update(collector));

            World.world.logger.info("-> of houses.");
            World.world.getHouses().values().stream().filter(house -> house.getOwnerId() > 0).forEach(house -> ((HouseData) DatabaseManager.get(HouseData.class)).update(house));

            World.world.logger.info("-> of trunks.");
            World.world.getTrunks().values().forEach(trunk -> ((TrunkData) DatabaseManager.get(TrunkData.class)).update(trunk));

            World.world.logger.info("-> of parks.");
            World.world.getMountparks().values().stream().filter(mp -> mp.getOwner() > 0 || mp.getOwner() == -1).forEach(mp -> ((MountParkData) DatabaseManager.get(MountParkData.class)).update(mp));

            World.world.logger.info("-> of mounts.");
            World.world.getMounts().values().forEach(mount -> ((MountData) DatabaseManager.get(MountData.class)).update(mount));

            World.world.logger.info("-> of areas.");
            World.world.getAreas().values().forEach(area -> ((AreaData) DatabaseManager.get(AreaData.class)).update(area));
            World.world.getSubAreas().values().forEach(subArea -> ((SubAreaData) DatabaseManager.get(SubAreaData.class)).update(subArea));

            World.world.logger.info("-> of objects.");
            try {
                new ArrayList<>(World.world.getGameObjects()).stream().filter(Objects::nonNull).filter(o -> o.getGuid() > 0)
                        .forEach(object -> {
                            try {
                                ((ObjectData) DatabaseManager.get(ObjectData.class)).update(object);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
            } catch(Exception e) {
                e.printStackTrace();
            }

            /* end save of data */
            World.world.logger.debug("The save has been doing successfully !");
            SocketManager.GAME_SEND_Im_PACKET_TO_ALL("1165;");
        } catch (Exception exception) {
            exception.printStackTrace();
            World.world.logger.error("Error when trying save of the world : " + exception.getMessage());
            if (trys < 10) {
                World.world.logger.error("Fail of the save, num of try : " + (trys + 1) + ".");
                WorldSave.cast(trys + 1);
                return;
            }
            Config.isSaving = false;
        } finally {
            Config.isSaving = false;
        }

        if(trys != 0) GameServer.setState(1);

        if(thread != null) {
            World.world.getMaps().stream().filter(map -> map != null && map.getMobGroups() != null)
                    .forEach(map -> {
                        map.getMobGroups().values().stream().filter(Objects::nonNull).forEach(Monster.MobGroup::addStarBonus);
                        map.getFixMobGroups().values().stream().filter(Objects::nonNull).forEach(Monster.MobGroup::addStarBonus);
                    });
            Thread copy = thread;
            thread = null;
            copy.interrupt();
        }
    }

    @Override
    public GameObject get() {
        return null;
    }
}