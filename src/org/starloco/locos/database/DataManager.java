package org.starloco.locos.database;

import org.starloco.locos.kernel.Config;
import redis.clients.jedis.JedisPooled;

public class DataManager {
    private final JedisPooled dbPool;

    public DataManager() {
        dbPool = new JedisPooled(Config.databaseV2GameHost, Config.databaseV2GamePort, Config.databaseV2GameUser, Config.databaseV2GamePass);
    }

}
