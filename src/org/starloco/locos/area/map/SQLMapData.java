package org.starloco.locos.area.map;

import org.starloco.locos.client.Player;
import org.starloco.locos.entity.monster.MobGroupDef;
import org.starloco.locos.entity.monster.MonsterGrade;
import org.starloco.locos.entity.monster.MonsterGroup;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.other.Action;
import org.starloco.locos.util.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SQLMapData extends MapData {
    // TODO: Remove GameCase/CellCache completely
    private List<GameCase> cases = new ArrayList<>();
    private final HashMap<Integer,List<Action>> moveEndActions = new HashMap<>();
    private final List<Integer> npcs = new ArrayList<>();
    private final List<MobGroupDef> staticMobGroups = new LinkedList<>();

    protected SQLMapData(int id, String date, String key, String data, int width, int height, int x, int y,
                         int subAreaID, boolean noSellers, boolean noCollectors, boolean noPrisms,
                         boolean noTp, boolean noDefy, boolean noAgro, boolean noCanal, int mobGroupsMaxCount,
                         int mobGroupsMaxSize, List<MonsterGrade> monsters, String places) {
        super(id,
                date,
                key,
                data,
                width,
                height,
                x,
                y,
                subAreaID,
                noSellers,
                noCollectors,
                noPrisms,
                noTp,
                noDefy,
                noAgro,
                noCanal,
                mobGroupsMaxCount,
                mobGroupsMaxSize,
                monsters,
                places);
    }

    public static SQLMapData build(int id, String date, int w, int h, String key, String data, String places, String monsters, String mapPos, int maxGroupCnt, byte fixSize, byte minSize, byte maxSize, String forbidden) {
        String[] split = forbidden.split(";");
        boolean noSellers = split.length>0 && split[0].equals("1");
        boolean noCollectors = split.length>1 && split[1].equals("1");
        boolean noPrisms = split.length>2 && split[2].equals("1");
        boolean noTp = split.length>3 && split[3].equals("1");
        boolean noDefy = split.length>4 && split[4].equals("1");
        boolean noAgro = split.length>5 && split[5].equals("1");
        boolean noCanal = split.length>6 && split[6].equals("1");

        int x,y,subAreaID;
        String[] mapInfos = mapPos.split(",");
        x = Integer.parseInt(mapInfos[0]);
        y = Integer.parseInt(mapInfos[1]);
        subAreaID = Integer.parseInt(mapInfos[2]);

        String unique = "";
        if(monsters.contains("@")) {
            split = monsters.split("@");
            unique = split[0];
            monsters = split[1];
        }

        List<MonsterGrade> mobPossibles = new ArrayList<>();
        for (String mob : monsters.split("\\|")) {
            if (mob.equals("")) continue;
            int id1, lvl;
            try {
                id1 = Integer.parseInt(mob.split(",")[0]);
                lvl = Integer.parseInt(mob.split(",")[1]);
            } catch (NumberFormatException e) {
                System.err.println("Error map id on monsters : " + id);
                e.printStackTrace();
                continue;
            }
            if (id1 == 0 || lvl == 0)
                continue;
            if (World.world.getMonstre(id1) == null)
                continue;
            if (World.world.getMonstre(id1).getGradeByLevel(lvl) == null)
                continue;
            if (Config.modeHalloween) {
                switch (id1) {
                    case 98://Tofu
                        if (World.world.getMonstre(794) != null)
                            if (World.world.getMonstre(794).getGradeByLevel(lvl) != null)
                                id1 = 794;
                        break;
                    case 101://Bouftou
                        if (World.world.getMonstre(793) != null)
                            if (World.world.getMonstre(793).getGradeByLevel(lvl) != null)
                                id1 = 793;
                        break;
                }
            }

            boolean pass = false;
            for(MonsterGrade grade : mobPossibles) {
                if(unique.contains(String.valueOf(grade.getTemplate().getId())) && id1 == grade.getTemplate().getId()) {
                    pass = true;
                    break;
                }
            }
            if(!pass) {
                mobPossibles.add(World.world.getMonstre(id1).getGradeByLevel(lvl));
            }
        }

        return new SQLMapData(id, date, key, data, w, h, x, y, subAreaID, noSellers, noCollectors, noPrisms, noTp, noDefy, noAgro, noCanal, maxGroupCnt, maxSize, mobPossibles, places);
    }

    @Override
    public List<Integer> getNPCs() {
        return npcs;
    }

    @Override
    public void onMoveEnd(Player player) {
        final GameCase cell = player.getCurCell();
        if (cell == null) return;

        this.moveEndActions.getOrDefault(cell.getId(), Collections.emptyList()).forEach(action -> action.apply(player, null, -1, -1));
    }


    public void addStaticGroup(int cellID, String groupData) {
        this.staticMobGroups.add(new MobGroupDef(cellID, MonsterGroup.parseMobGroup(groupData)));
    }

    public String getForbidden() {
        return Stream.of(
                noSellers,
                noCollectors,
                noPrisms,
                noTp,
                noDefy,
                noAgro,
                noCanal
        ).map(b -> b?"1":"0").collect(Collectors.joining(";"));
    }
}
