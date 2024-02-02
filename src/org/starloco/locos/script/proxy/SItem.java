package org.starloco.locos.script.proxy;

import org.classdump.luna.impl.DefaultUserdata;
import org.classdump.luna.impl.ImmutableTable;
import org.classdump.luna.lib.ArgumentIterator;
import org.starloco.locos.player.Player;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.item.FullItem;
import org.starloco.locos.script.types.MetaTables;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SItem  extends DefaultUserdata<FullItem> {
    private static final ImmutableTable META_TABLE= MetaTables.MetaTable(MetaTables.ReflectIndexTable(SItem.class));

    public SItem(FullItem userValue) {
        super(META_TABLE, userValue);
    }

    @SuppressWarnings("unused")
    private static int guid(FullItem item) {
        return item.getGuid();
    }

    @SuppressWarnings("unused")
    private static int id(FullItem item) {
        return item.template().getId();
    }

    @SuppressWarnings("unused")
    private static int type(FullItem item) {
        return item.template().getTypeID();
    }

    @SuppressWarnings("unused")
    private static long dateStatTS(FullItem item, ArgumentIterator args) {
        int statID = args.nextInt();

        String val = item.getTxtStat().get(statID);
        if(val == null) return -1;
        if (val.contains("#")) {
            val = val.split("#")[3];
        }
        return Long.parseLong(val);
    }

    @SuppressWarnings("unused")
    private static boolean hasTxtStat(FullItem item, ArgumentIterator args) {
        int stat = args.nextInt();
        String val = args.nextString().toString();

        String stats = item.getTxtStat().get(stat);
        if(stats == null || stats.isEmpty()) {
            return false;
        }
        return Arrays.asList(stats.split(",")).contains(val);
    }

    @SuppressWarnings("unused")
    private static boolean consumeTxtStat(FullItem item, ArgumentIterator args) {
        Player player = args.nextUserdata("SPlayer", SPlayer.class).getUserValue();
        int stat = args.nextInt();
        String val = args.nextString().toString();

        String stats = item.getTxtStat().get(stat);
        if(stats == null || stats.isEmpty())  return false;


        // TODO: change how Text stats are stored. They are a bit hacky currently,
        String newStats = Arrays.stream(stats.split(","))
                .filter(p -> !p.equals(val)) // Filter out val from stat
                .collect(Collectors.joining(","));

        if(newStats.equals(stats)) {
            // New stats are the same as before, we failed to remove the key
            return false;
        }
        item.getTxtStat().put(stat, newStats);

        SocketManager.GAME_SEND_UPDATE_ITEM(player, item);
        return true;
    }

}
