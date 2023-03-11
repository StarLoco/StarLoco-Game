package org.starloco.locos.script.proxy;

import org.classdump.luna.Metatables;
import org.classdump.luna.impl.DefaultUserdata;
import org.classdump.luna.impl.ImmutableTable;
import org.classdump.luna.lib.AbstractLibFunction;
import org.classdump.luna.lib.ArgumentIterator;
import org.classdump.luna.runtime.ExecutionContext;
import org.starloco.locos.client.Player;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.object.GameObject;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SItem  extends DefaultUserdata<GameObject> {
    private static final ImmutableTable INDEX_TABLE = new ImmutableTable.Builder()
            // Calls
            .add("hasTxtStat", new HasTxtStatCall())
            .add("consumeTxtStat", new ConsumeTxtStatCall())
            .build();
    private static final ImmutableTable META_TABLE = new ImmutableTable.Builder()
            .add(Metatables.MT_INDEX, INDEX_TABLE)
            .build();

    public SItem(GameObject userValue) {
        super(META_TABLE, userValue);
    }

    private static class HasTxtStatCall extends AbstractLibFunction {
        @Override
        protected String name() { return "item:HasTxtStatCall"; }

        @Override
        public void invoke(ExecutionContext context, ArgumentIterator args) {
            GameObject item = args.nextUserdata("SItem", SItem.class).getUserValue();
            int stat = args.nextInt();
            String val = args.nextString().toString();

            String stats = item.getTxtStat().get(stat);
            if(stats == null || stats.isEmpty()) {
                context.getReturnBuffer().setTo(false);
                return;
            }
            // TODO: change how Text stats are stored. They are a bit hacky currently,
            context.getReturnBuffer().setTo(Arrays.asList(stats.split(",")).contains(val));
        }
    }
    private static class ConsumeTxtStatCall extends AbstractLibFunction {
        @Override
        protected String name() { return "item:ConsumeStatCall"; }

        @Override
        public void invoke(ExecutionContext context, ArgumentIterator args) {
            // , SItem lItem, Long lStat, ByteString lValue
            GameObject item = args.nextUserdata("SItem", SItem.class).getUserValue();
            Player player = args.nextUserdata("SPlayer", SPlayer.class).getUserValue();
            int stat = args.nextInt();
            String val = args.nextString().toString();

            String stats = item.getTxtStat().get(stat);
            if(stats == null || stats.isEmpty()) {
                context.getReturnBuffer().setTo(false);
                return;
            }

            // TODO: change how Text stats are stored. They are a bit hacky currently,
            String newStats = Arrays.stream(stats.split(","))
                    .filter(p -> !p.equals(val)) // Filter out val from stat
                    .collect(Collectors.joining(","));

            if(newStats.equals(stats)) {
                // New stats are the same as before, we failed to remove the key
                context.getReturnBuffer().setTo(false);
                return;
            }
            item.getTxtStat().put(stat, newStats);

            context.getReturnBuffer().setTo(true);
            SocketManager.GAME_SEND_UPDATE_ITEM(player, item);
        }
     }
}
