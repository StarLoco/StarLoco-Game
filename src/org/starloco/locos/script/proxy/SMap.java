package org.starloco.locos.script.proxy;

import io.jsonwebtoken.lang.Maps;
import org.classdump.luna.Table;
import org.classdump.luna.impl.DefaultTable;
import org.classdump.luna.impl.DefaultUserdata;
import org.classdump.luna.impl.ImmutableTable;
import org.classdump.luna.lib.ArgumentIterator;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.area.map.ScriptMapData;
import org.starloco.locos.client.Player;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.entity.monster.MobGroupDef;
import org.starloco.locos.entity.monster.MonsterGrade;
import org.starloco.locos.entity.monster.MonsterGroup;
import org.starloco.locos.entity.npc.Npc;
import org.starloco.locos.script.ScriptVM;
import org.starloco.locos.script.types.MetaTables;

import java.util.*;

public class SMap extends DefaultUserdata<GameMap> {
    private static final ImmutableTable META_TABLE= MetaTables.MetaTable(MetaTables.ReflectIndexTable(SMap.class));

    public SMap(GameMap userValue) { super(META_TABLE, userValue); }

    @SuppressWarnings("unused")
    private static int id(GameMap m) {
        return m.getId();
    }

    @SuppressWarnings("unused")
    private static Table def(GameMap m) {
        return m.data.scripted();
    }

    @SuppressWarnings("unused")
    private static SArea area(GameMap m) {
        return m.getArea().scripted();
    }

    @SuppressWarnings("unused")
    private static SSubArea subArea(GameMap m) {
        return m.getSubArea().scripted();
    }

    @SuppressWarnings("unused")
    private static Table cellPlayers(GameMap m, ArgumentIterator args) {
        int cellID = args.nextInt();
        if(m.getCase(cellID) == null) return DefaultTable.factory().newTable();
        return ScriptVM.scriptedValsTable(m.getCase(cellID).getPlayers().stream());
    }

    @SuppressWarnings("unused")
    private static Table mobGroupById(GameMap m, ArgumentIterator args) {
        int id = args.nextInt();
        return Optional.ofNullable(m.getMobGroups().get(id)).map(MonsterGroup::getMobs)
            .map(Map::values)
            .map(Collection::stream)
            .map(s -> s.map(MonsterGrade::scripted))
            .map(ScriptVM::listOf)
            .orElse(null);
    }

    @SuppressWarnings("unused")
    private static Table mobGroups(GameMap m) {
        return ScriptVM.listOf(m.getMobGroups().values().stream()
            .map(MonsterGroup::getMobs)
            .map(Map::values)
            .map(Collection::stream)
            .map(s -> s.map(MonsterGrade::scripted))
            .map(ScriptVM::listOf)
        );
    }

    @SuppressWarnings("unused")
    private static int spawnGroupDef(GameMap m, ArgumentIterator args) {
        MobGroupDef def = MobGroupDef.Mapper.get().from(args.nextTable());

        return m.spawnMobGroup(def, true);
    }

    @SuppressWarnings("unused")
    private static void updateNpcExtraForPlayer(GameMap m, ArgumentIterator args) {
        int entId = args.nextInt();
        Player p = args.nextUserdata("SPlayer", SPlayer.class).getUserValue(); // Check this
        Npc npc = m.getNpcByTemplateId(entId);
        if(npc == null) return;

        SocketManager.GAME_SEND_GX_PACKET(p, npc);
    }

    @SuppressWarnings("unused")
    private static String getAnimationState(GameMap m, ArgumentIterator args) {
        int cellId = args.nextInt();
        return m.getAnimationState(cellId);
    }

    @SuppressWarnings("unused")
    private static void setAnimationState(GameMap m, ArgumentIterator args) {
        int cellId = args.nextInt();
        String animName = args.nextString().toString();
//        Runnable r = Optional.ofNullable(args.nextOptionalFunction(null))
//            .map(fn -> (Runnable)(() -> DataScriptVM.getInstance().call(fn)))
//            .orElse(null);

        m.setAnimationState(cellId, animName);
    }

    @SuppressWarnings("unused")
    private static void sendAction(GameMap m, ArgumentIterator args) {
        Player p = args.nextUserdata("SPlayer", SPlayer.class).getUserValue();
        int actionID = args.nextInt();
        int actionType = args.nextInt();
        String actionValue = args.nextString().toString();

        String actionIDStr = "";
        if(actionID != -1) actionIDStr = String.valueOf(actionID);

        SocketManager.GAME_SEND_GA_PACKET_TO_MAP(m, actionIDStr, actionType, p.getId(), actionValue);
    }

    @SuppressWarnings("unused")
    private static void setCellData(GameMap m, ArgumentIterator args) {
        int cellID = args.nextInt();
        String field = args.nextString().toString();
        int val = args.nextInt();

        if(m.cellsData.applyOverrides(cellID, Maps.of(field, val).build())) {
            SocketManager.GAME_SEND_GDC_PACKET_TO_MAP(m, cellID, true);
        }
    }

}
