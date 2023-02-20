package org.starloco.locos.common;

import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.client.Player;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.util.AStarPathFinding;
import org.starloco.locos.fight.spells.Spell;
import org.starloco.locos.fight.traps.Glyph;
import org.starloco.locos.fight.traps.Trap;
import org.starloco.locos.game.GameServer;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Constant;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class PathFinding {


    public static int isValidPath(GameMap map, int cellID,
                                  AtomicReference<String> pathRef, Fight fight, Player perso,
                                  int targetCell) {
        AtomicReference<Integer> nSteps = new AtomicReference<>();
        nSteps.set(0);
        synchronized (nSteps.get()) {
            nSteps.set(0);
            int newPos = cellID;
            int Steps = 0;
            String path = pathRef.get();
            String newPath = "";
            for (int i = 0; i < path.length(); i += 3) {
                String SmallPath = path.substring(i, i + 3);
                char dir = SmallPath.charAt(0);
                int dirCaseID = World.world.getCryptManager().cellCode_To_ID(SmallPath.substring(1));
                nSteps.set(0);
                //Si en combat et Si Pas d�but du path, on v�rifie tacle
                if (fight != null && i != 0 && getEnemyFighterArround(newPos, map, fight, true) != null) {
                    pathRef.set(newPath);
                    return Steps;
                }
                //Si en combat, et pas au d�but du path
                if (fight != null && i != 0) {
                    for (Trap trap : fight.getAllTraps()) {
                        if (getDistanceBetween(map, trap.getCell().getId(), newPos) <= trap.getSize()) {
                            pathRef.set(newPath);
                            return Steps;
                        }
                    }
                }

                String[] aPathInfos = ValidSinglePath(nSteps, newPos, SmallPath, map, fight, perso, targetCell).split(":");
                if (aPathInfos[0].equalsIgnoreCase("stop")) {
                    newPos = Integer.parseInt(aPathInfos[1]);
                    Steps += nSteps.get();
                    newPath += dir + World.world.getCryptManager().cellID_To_Code(newPos);
                    pathRef.set(newPath);
                    return -Steps;
                } else if (aPathInfos[0].equalsIgnoreCase("ok")) {
                    newPos = dirCaseID;
                    Steps += nSteps.get();
                } else if (aPathInfos[0].equalsIgnoreCase("stoptp")) {
                    newPos = Integer.parseInt(aPathInfos[1]);
                    Steps += nSteps.get();
                    newPath += dir + World.world.getCryptManager().cellID_To_Code(newPos);
                    pathRef.set(newPath);
                    return -Steps - 10000;
                } else {
                    pathRef.set(newPath);
                    return -1000;
                }
                newPath += dir + World.world.getCryptManager().cellID_To_Code(newPos);
            }
            pathRef.set(newPath);
            return Steps;
        }
    }

    public static ArrayList<Fighter> getEnemyFighterArround(int cellID,
                                                            GameMap map, Fight fight, boolean returnNull) {
        char[] dirs = {'b', 'd', 'f', 'h'};
        ArrayList<Fighter> enemy = new ArrayList<>();
        if(map == null) return enemy;

        for (char dir : dirs) {
            GameCase cell = map.getCase((short) GetCaseIDFromDirrection(cellID, dir, map, false));
            if(cell != null) {
                Fighter f = cell.getFirstFighter();
                if (f != null) {
                    if (f.getFight() != fight || f.isHide())
                        continue;
                    if (f.getTeam() != fight.getFighterByGameOrder().getTeam())
                        enemy.add(f);
                }
            }
        }
        if (returnNull && (enemy.size() == 0 || enemy.size() == 4))
            return null;

        return enemy;
    }

    public static boolean isNextTo(GameMap map, int cell1, int cell2) {
        boolean result = false;
        if (cell1 + 14 == cell2)
            result = true;
        else if (cell1 + 15 == cell2)
            result = true;
        else
            result = cell1 - 14 == cell2 || cell1 - 15 == cell2;
        return result;
    }

    public static String ValidSinglePath(AtomicReference<Integer> nSteps, int CurrentPos, String Path, GameMap map,
                                         Fight fight, Player perso, int targetCell) {
        nSteps.set(0);
        char dir = Path.charAt(0);
        int dirCaseID = World.world.getCryptManager().cellCode_To_ID(Path.substring(1)), check = ("353;339;325;311;297;283;269;255;241;227;213;228;368;354;340;326;312;298;284;270;256;242;243;257;271;285;299;313;327;341;355;369;383".contains(String.valueOf(targetCell)) ? 1 : 0);

        if (fight != null && fight.isOccuped(dirCaseID))
            return "no:";

        if(perso != null) {
            if (perso.getCases)
                if (!perso.thisCases.contains(CurrentPos))
                    perso.thisCases.add(CurrentPos);
        }
        // int oldPos = CurrentPos;
        int lastPos = CurrentPos, oldPos = CurrentPos;

        for (nSteps.set(1); nSteps.get() <= 64; nSteps.set(nSteps.get() + 1)) {
            if (GetCaseIDFromDirrection(lastPos, dir, map, fight != null) == dirCaseID) {
                if (fight != null && fight.isOccuped(dirCaseID))
                    return "stop:" + lastPos;
                GameCase cell = map.getCase(dirCaseID);
                if(map.getId() == 2019) {
                    if (cell.getId() == 297 && ((cell.getPlayers() != null && cell.getPlayers().size() > 0) || perso.getSexe() == 0))
                        return "stop:" + oldPos;
                    if (cell.getId() == 282 && ((cell.getPlayers() != null && cell.getPlayers().size() > 0) || perso.getSexe() == 1))
                        return "stop:" + oldPos;
                }
                if (cell.isWalkable(true, fight != null, targetCell)) {
                    return "ok:";
                } else {
                    nSteps.set(nSteps.get() - 1);
                    return ("stop:" + lastPos);
                }
            } else {
                lastPos = GetCaseIDFromDirrection(lastPos, dir, map, fight != null);
            }

            if (fight == null) {
                if (perso.getCurMap().getId() == 9588) {
                    String cell = "353;339;325;311;297;283;269;255;241;227;213;228;368;354;340;326;312;298;284;270;256;242;243;257;271;285;299;313;327;341;355;369;383";
                    if (cell.contains(String.valueOf(lastPos)))
                        check++;
                    if (check > 1)
                        return "stoptp:" + lastPos;
                }
                try {
                    if (perso.getCases)
                        if (!perso.thisCases.contains(lastPos))
                            perso.thisCases.add(lastPos);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (lastPos < 0)
                    continue;
                GameCase _case = map.getCase(lastPos);
                if (_case == null)
                    continue;
                if(map.getId() == 2019) {
                    if(_case.getId() == 297 && ((_case.getPlayers() != null && _case.getPlayers().size() > 0) || perso.getSexe() == 0))
                        return "stop:" + oldPos;
                    if(_case.getId() == 282 && ((_case.getPlayers() != null && _case.getPlayers().size() > 0) || perso.getSexe() == 1))
                        return "stop:" + oldPos;
                }
                if (_case.getOnCellStopAction())
                    return "stop:" + lastPos;
                if (map.isAggroByMob(perso, lastPos))
                    return "stop:" + lastPos;
                if(!map.getCase(lastPos).isWalkable(true, false, targetCell))
                    return "stop:" + oldPos;
                oldPos = lastPos;
            } else {
                if (fight.isOccuped(lastPos))
                    return "no:";
                if (getEnemyFighterArround(lastPos, map, fight, true) != null)//Si ennemie proche
                    return "stop:" + lastPos;
                for (Trap p : fight.getAllTraps()) {
                    if (getDistanceBetween(map, p.getCell().getId(), lastPos) <= p.getSize()) {//on arrete le d�placement sur la 1ere case du piege
                        return "stop:" + lastPos;
                    }
                }
            }
        }
        return "no:";
    }

    public static ArrayList<Integer> getAllCaseIdAllDirrection(int caseId, GameMap map) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        char[] dir = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        int _c = -1;
        for (char d : dir) {
            _c = GetCaseIDFromDirrection(caseId, d, map, false);
            if (_c > 0)
                list.add(_c);
        }
        return list;
    }


    public final static Map<String, List<Short>> outForbiddenCells = new HashMap<>();

    public static int GetCaseIDFromDirrection(int cellId, char dir,
                                              GameMap map, boolean fight) {
        if (map == null)
            return -1;
        int cell = -1;
        switch (dir) {
            case 'a':
                cell = fight ? -1 : cellId + 1;
                break;
            case 'b':
                cell =  cellId + map.getW();
                break;
            case 'c':
                cell =  fight ? -1 : cellId + (map.getW() * 2 - 1);
                break;
            case 'd':
                cell =  cellId + (map.getW() - 1);
                break;
            case 'e':
                cell =  fight ? -1 : cellId - 1;
                break;
            case 'f':
                cell =  cellId - map.getW();
                break;
            case 'g':
                cell =  fight ? -1 : cellId - (map.getW() * 2 - 1);
                break;
            case 'h':
                cell =  cellId - map.getW() + 1;
                break;
        }
        List<Short> cells = outForbiddenCells.get(map.getW() + "_" + map.getH());
        if(cells != null && cells.contains((short) cell))
            return -1;
        return cell;
    }

    public static int getDistanceBetween(GameMap map, int id1, int id2) {
        if (id1 == id2)
            return 0;
        if (map == null)
            return 0;

        int diffX = Math.abs(getCellXCoord(map, id1) - getCellXCoord(map, id2));
        int diffY = Math.abs(getCellYCoord(map, id1) - getCellYCoord(map, id2));
        return (diffX + diffY);
    }

    public static Fighter getEnemyAround(int cellId, GameMap map, Fight fight) {
        char[] dirs = {'b', 'd', 'f', 'h'};
        for (char dir : dirs) {
            GameCase cell = map.getCase(GetCaseIDFromDirrection(cellId, dir, map, false));
            if (cell == null)
                continue;
            Fighter f = cell.getFirstFighter();

            if (f != null)
                if (f.getFight() == fight)
                    if (f.getTeam() != fight.getFighterByGameOrder().getTeam())
                        return f;
        }
        return null;
    }

    public static List<Fighter> getEnemiesAround(int cellId, GameMap map, Fight fight) {
        final List<Fighter> array = new ArrayList<>();
        for (char dir : new char[] {'b', 'd', 'f', 'h'}) {
            GameCase cell = map.getCase(GetCaseIDFromDirrection(cellId, dir, map, false));
            if (cell != null) {
                Fighter f = cell.getFirstFighter();
                if (f != null)
                    if (f.getFight() == fight)
                        if (f.getTeam() != fight.getFighterByGameOrder().getTeam())
                            array.add(f);
            }
        }
        return array;
    }

    public static int newCaseAfterPush(Fight fight, GameCase CCase, GameCase TCase, int value) {
        GameMap map = fight.getMap();
        if (CCase.getId() == TCase.getId())
            return 0;

        char c = getDirBetweenTwoCase(CCase.getId(), TCase.getId(), map, true);
        int id = TCase.getId();

        if (value < 0) {
            c = getOpositeDirection(c);
            value = -value;
        }
        boolean b = false;
        for (int a = 0; a < value; a++) {
            int nextCase = GetCaseIDFromDirrection(id, c, map, true);

            if (map.getCase(nextCase) != null && map.getCase(nextCase).isWalkable(false) && map.getCase(nextCase).getFirstFighter() == null)
                id = nextCase;
            else
                return -(value - a);

            for (Trap trap : fight.getAllTraps()) {
                GameCase trapCell = trap.getCell(), nextCell = map.getCase(nextCase);
                if (getDistanceBetween(map, trapCell.getId(), nextCell.getId()) <= trap.getSize()) {
                    if(!casesAreInSameLine(map, trapCell.getId(), nextCell.getId(), 'z', 15))
                        id = GetCaseIDFromDirrection(nextCase, c, map, true);
                    b = true;
                }
            }

            if (b) break;
        }

        if (id == TCase.getId()) id = 0;
        return id;
    }


    public static int newCaseAfterPush(Fight fight, GameCase currentCell, GameCase targetCell, int value, boolean piege) {
        GameMap map = fight.getMap();

        if (currentCell.getId() == targetCell.getId())
            return 0;
        char dir = getDirBetweenTwoCase(currentCell.getId(), targetCell.getId(), map, true);
        int id = targetCell.getId(), nextCase = 0;

        if (value < 0) {
            dir = getOpositeDirection(dir);
            value = -value;
        }

        boolean b = false;
        for (int a = 0; a < value; a++) {
            nextCase = GetCaseIDFromDirrection(id, dir, map, true);

            if (map.getCase(nextCase) != null && map.getCase(nextCase).isWalkable(false) && map.getCase(nextCase).getFighters().isEmpty())
                id = nextCase;
            else
                return -(value - a);

            for (Trap trap : fight.getAllTraps()) {
                GameCase trapCell = trap.getCell(), nextCell = map.getCase(nextCase);
                if (getDistanceBetween(map, trapCell.getId(), nextCell.getId()) <= trap.getSize()) {
                    if(!casesAreInSameLine(map, trapCell.getId(), nextCell.getId(), 'z', 15))
                        id = GetCaseIDFromDirrection(nextCase, dir, map, true);
                    b = true;
                    break;
                }
            }

            if (b) break;
        }

        if (id == targetCell.getId())
            return 0;
        return id;
    }

    public static int getDistanceBetweenTwoCase(GameMap map, GameCase c1, GameCase c2) {
        int dist = 0;
        if (c1 == null || c2 == null) {
            return dist;
        }
        if (c1.getId() == c2.getId())
            return dist;
        int id = c1.getId();
        char c = getDirBetweenTwoCase(c1.getId(), c2.getId(), map, true);

        while (map != null && c2 != map.getCase(id)) {
            id = GetCaseIDFromDirrection(id, c, map, true);
            if (map.getCase(id) == null) {
                return dist;
            }
            dist++;
        }
        return dist;
    }

    public static char getOpositeDirection(char c) {
        switch (c) {
            case 'a':
                return 'e';
            case 'b':
                return 'f';
            case 'c':
                return 'g';
            case 'd':
                return 'h';
            case 'e':
                return 'a';
            case 'f':
                return 'b';
            case 'g':
                return 'c';
            case 'h':
                return 'd';
        }
        return 0x00;
    }

    public static int getNearenemycontremur(GameMap map, int startCell,
                                            int endCell, ArrayList<GameCase> forbidens)
    {
        //On prend la cellule autour de la cible, la plus proche
        int dist = 1000;
        int cellID = startCell;
        if (forbidens == null)
            forbidens = new ArrayList<GameCase>();
        char[] dirs = {'b', 'd', 'f', 'h'};
        GameCase hd = null, bg = null, hg = null, bd = null;
        for (char d : dirs)
        {
            if(d == 'b')//En Haut à Droite.
            {
                int c = PathFinding.GetCaseIDFromDirrection(startCell, d, map, true);
                if (map.getCase(c) == null)
                    continue;
                hd = map.getCase(c);
            }
            else if(d == 'f')//En Bas à Gauche.
            {
                int c = PathFinding.GetCaseIDFromDirrection(startCell, d, map, true);
                if (map.getCase(c) == null)
                    continue;
                bg = map.getCase(c);
            }
            else if(d == 'd')//En Haut à Gauche.
            {
                int c = PathFinding.GetCaseIDFromDirrection(startCell, d, map, true);
                if (map.getCase(c) == null)
                    continue;
                hg = map.getCase(c);
            }
            else if(d == 'h')//En Bas à Droite.
            {
                int c = PathFinding.GetCaseIDFromDirrection(startCell, d, map, true);
                if (map.getCase(c) == null)
                    continue;
                bd = map.getCase(c);
            }
        }

        GameCase[] tab = {hd,bg,hg,bd};
        for(GameCase c : tab)
        {
            if(c == null)
                continue;
            if(c == hd)
            {
                if(!c.isWalkable(false) && bg != null || c.getFirstFighter() != null && bg != null)
                {
                    // On cherche la distance entre
                    int dis = PathFinding.getDistanceBetween(map, endCell, bg.getId());
                    // Si la distance est strictement inférieur à 1000 et que la case
                    // est marchable et que personne ne
                    // se trouve dessus et que la case n'est pas interdite
                    if (dis < dist && !forbidens.contains(bg))
                    {
                        // On crée la distance
                        dist = dis;
                        // On modifie la cellule
                        cellID = bg.getId();
                    }
                }
            }
            else if(c == bg)
            {
                if(!c.isWalkable(false) && hd != null || c.getFirstFighter() != null && hd != null)
                {
                    // On cherche la distance entre
                    int dis = PathFinding.getDistanceBetween(map, endCell, hd.getId());
                    // Si la distance est strictement inférieur à 1000 et que la case
                    // est marchable et que personne ne
                    // se trouve dessus et que la case n'est pas interdite
                    if (dis < dist && !forbidens.contains(hd))
                    {
                        // On crée la distance
                        dist = dis;
                        // On modifie la cellule
                        cellID = hd.getId();
                    }
                }
            }
            else if(c == bd)
            {
                if(!c.isWalkable(false) && hg != null || c.getFirstFighter() != null && hg != null)
                {
                    // On cherche la distance entre
                    int dis = PathFinding.getDistanceBetween(map, endCell, hg.getId());
                    // Si la distance est strictement inférieur à 1000 et que la case
                    // est marchable et que personne ne
                    // se trouve dessus et que la case n'est pas interdite
                    if (dis < dist && !forbidens.contains(hg))
                    {
                        // On crée la distance
                        dist = dis;
                        // On modifie la cellule
                        cellID = hg.getId();
                    }
                }
            }
            else if(c == hg)
            {
                if(!c.isWalkable(false) && bd != null || c.getFirstFighter() != null && bd != null)
                {
                    // On cherche la distance entre
                    int dis = PathFinding.getDistanceBetween(map, endCell, bd.getId());
                    // Si la distance est strictement inférieur à 1000 et que la case
                    // est marchable et que personne ne
                    // se trouve dessus et que la case n'est pas interdite
                    if (dis < dist && !forbidens.contains(bd))
                    {
                        // On crée la distance
                        dist = dis;
                        // On modifie la cellule
                        cellID = bd.getId();
                    }
                }
            }
        }

        //On renvoie -1 si pas trouvé
        return cellID == startCell ? -1 : cellID;
    }
    
    public static int getCaseBetweenEnemy(int cellId, GameMap map, Fight fight)
    {
        if(map == null) return 0;
        char[] dirs = {'f', 'd', 'b', 'h'};
        for (char dir : dirs)
        {
            int id = GetCaseIDFromDirrection(cellId, dir, map, false);
            GameCase cell = map.getCase(id);
            if (cell == null)
                continue;
            Fighter f = cell.getFirstFighter();

            if (f == null && cell.isWalkable(false))
                return cell.getId();
        }
        return 0;
    }

    public static int getAvailableCellArround(Fight fight, int cellId, List<Integer> cellsUnavailable) {
        if(fight == null || fight.getMap() == null) return 0;
        char[] dirs = {'f', 'd', 'b', 'h'};

        for (char dir : Formulas.shuffleCharArray(dirs)) {
            int id = GetCaseIDFromDirrection(cellId, dir, fight.getMap(), false);
            GameCase cell = fight.getMap().getCase(id);

            if (cell != null) {
                Fighter fighter = cell.getFirstFighter();
                if (fighter == null && cell.isWalkable(false)) {
                    if(cellsUnavailable != null && cellsUnavailable.contains(cell.getId()))
                        continue;
                    return cell.getId();
                }
            }
        }
        return 0;
    }

    public static int getNearestligneGA(Fight fight, int startCell,
                                        int endCell, ArrayList<GameCase> forbidens, int distmin)
    {

        GameMap map = fight.getMap();
        ArrayList<Glyph> glyphs = new ArrayList<Glyph>();//Copie du tableau
        glyphs.addAll(fight.getAllGlyphs());
        int dist = 1000;
        //On prend la cellule autour de la cible, la plus proche
        int cellID = startCell;
        if (forbidens == null)
            forbidens = new ArrayList<>();
        char[] dirs = {'b', 'd', 'f', 'h'};
        for (char d : dirs)
        {

            int c = PathFinding.GetCaseIDFromDirrection(startCell, d, map, true);
            if (map.getCase(c) == null)
                continue;
            int dis = PathFinding.getDistanceBetween(map, endCell, c);
            int dis2 = PathFinding.getDistanceBetween(map, startCell, c);
            // Si la distance est strictement inférieur à 1000 et que la case
            // est marchable et que personne ne
            // se trouve dessus et que la case n'est pas interdite
            if (dis < dist && dis2 <= distmin && map.getCase(c).isWalkable(true, true, -1)
                    && map.getCase(c).getFirstFighter() == null
                    && !forbidens.contains(map.getCase(c)))
            {
                boolean ok1 = true;
                for(Glyph g : glyphs)
                {
                    if(PathFinding.getDistanceBetween(map,c , g.getCell().getId()) <= g.getSize() && g.getSpell() != 476)
                        ok1 = false;
                }

                if(!ok1)
                    continue;
                // On crée la distance
                dist = dis;
                // On modifie la cellule
                cellID = c;
            }else if (dis < dist && map.getCase(c).isWalkable(true, true, -1)
                    && map.getCase(c).getFirstFighter() == null
                    && !forbidens.contains(map.getCase(c)))
            {
                boolean ok1 = true;
                for(Glyph g : glyphs)
                {
                    if(PathFinding.getDistanceBetween(map,c , g.getCell().getId()) <= g.getSize() && g.getSpell() != 476)
                        ok1 = false;
                }

                if(!ok1)
                    continue;
                // On crée la distance
                dist = dis;
                // On modifie la cellule
                cellID = c;
            }
            boolean ok = false;
            while(!ok)
            {
                int h = PathFinding.GetCaseIDFromDirrection(c, d, map, true);
                if (map.getCase(h) == null)
                    ok = true;
                dis = PathFinding.getDistanceBetween(map, endCell, c);
                dis2 = PathFinding.getDistanceBetween(map, startCell, c);
                // Si la distance est strictement inférieur à 1000 et que la case
                // est marchable et que personne ne
                // se trouve dessus et que la case n'est pas interdite
                if (dis < dist && dis2 <= distmin && map.getCase(c).isWalkable(true, true, -1)
                        && map.getCase(c).getFirstFighter() == null
                        && !forbidens.contains(map.getCase(c)))
                {
                    boolean ok1 = true;
                    for(Glyph g : glyphs)
                    {
                        if(PathFinding.getDistanceBetween(map,c , g.getCell().getId()) <= g.getSize() && g.getSpell() != 476)
                            ok1 = false;
                    }

                    if(!ok1)
                        continue;
                    // On crée la distance
                    dist = dis;
                    // On modifie la cellule
                    cellID = c;
                }else if (dis < dist && map.getCase(c).isWalkable(true, true, -1)
                        && map.getCase(c).getFirstFighter() == null
                        && !forbidens.contains(map.getCase(c)))
                {
                    boolean ok1 = true;
                    for(Glyph g : glyphs)
                    {
                        if(PathFinding.getDistanceBetween(map,c , g.getCell().getId()) <= g.getSize() && g.getSpell() != 476)
                            ok1 = false;
                    }

                    if(!ok1)
                        continue;
                    // On crée la distance
                    dist = dis;
                    // On modifie la cellule
                    cellID = c;
                }
                c = h;
            }


        }

        return cellID == startCell ? -1 : cellID;
    }

    public static int getNearenemycontremur2(GameMap map, int startCell, int endCell, ArrayList<GameCase> forbidens, Fighter F) {
        //On prend la cellule autour de la cible, la plus proche
        int dist = 1000;
        int cellID = startCell;
        if (forbidens == null)
            forbidens = new ArrayList<>();
        char[] dirs = {'b', 'd', 'f', 'h'};
        char perso = ' ';
        for (char d : dirs) {
            int c = PathFinding.GetCaseIDFromDirrection(startCell, d, map, true);
            if (map.getCase(c) == null)
                continue;
            if(map.getCase(c) == F.getCell())
                perso = d;
        }
        
        for (char d : dirs) {
            if(getOpositeDirection(perso) == d) {
                int c = PathFinding.GetCaseIDFromDirrection(startCell, d, map, true);
                if (map.getCase(c) == null) continue;
                if(!map.getCase(c).isWalkable(false) || map.getCase(c).getFirstFighter() != null) {
                    int dis = PathFinding.getDistanceBetween(map, endCell, map.getCase(c).getId());
                    if (dis < dist && !forbidens.contains(map.getCase(c)) && F.getCell() != map.getCase(c)) {
                        // On crée la distance
                        dist = dis;
                        // On modifie la cellule
                        cellID = map.getCase(c).getId();
                    }
                }
            }
        }

        //On renvoie -1 si pas trouvé
        return cellID == startCell ? -1 : cellID;
    }

    public static int getNearestCellDiagGA(GameMap map, int startCell,
                                           int endCell, ArrayList<GameCase> forbidens)
    {
        //On prend la cellule autour de la cible, la plus proche
        int dist = 1000;
        int cellID = startCell;
        if (forbidens == null)
            forbidens = new ArrayList<GameCase>();
        char[] dirs = {'b', 'd', 'f', 'h'};
        GameCase hd = null;
        GameCase bg = null;
        GameCase hg = null;
        GameCase bd = null;
        for (char d : dirs)
        {
            if(d == 'b')//En Haut à Droite.
            {
                int c = PathFinding.GetCaseIDFromDirrection(startCell, d, map, true);
                if (map.getCase(c) == null)
                    continue;
                hd = map.getCase(c);
            }
            else if(d == 'f')//En Bas à Gauche.
            {
                int c = PathFinding.GetCaseIDFromDirrection(startCell, d, map, true);
                if (map.getCase(c) == null)
                    continue;
                bg = map.getCase(c);
            }
            else if(d == 'd')//En Haut à Gauche.
            {
                int c = PathFinding.GetCaseIDFromDirrection(startCell, d, map, true);
                if (map.getCase(c) == null)
                    continue;
                hg = map.getCase(c);
            }
            else if(d == 'h')//En Bas à Droite.
            {
                int c = PathFinding.GetCaseIDFromDirrection(startCell, d, map, true);
                if (map.getCase(c) == null)
                    continue;
                bd = map.getCase(c);
            }
        }

        GameCase[] tab = {hd,bg,hg,bd};
        for(GameCase c : tab)
        {
            if(c == null)
                continue;
            if(c == hd)//En Haut à Droite.
            {
                if(hd.getFirstFighter() == null && hd.blockLoS() == true)
                {
                    int p = PathFinding.GetCaseIDFromDirrection(c.getId(), 'b', map, true);
                    if (map.getCase(p) == null)
                        continue;
                    // On cherche la distance entre
                    int dis = PathFinding.getDistanceBetween(map, endCell, p);
                    // Si la distance est strictement inférieur à 1000 et que la case
                    // est marchable et que personne ne
                    // se trouve dessus et que la case n'est pas interdite
                    if (dis < dist && map.getCase(p).isWalkable(true, true, -1)
                            && map.getCase(p).getFirstFighter() == null
                            && !forbidens.contains(map.getCase(p)))
                    {
                        // On crée la distance
                        dist = dis;
                        // On modifie la cellule
                        cellID = p;
                    }
                }
                int p = PathFinding.GetCaseIDFromDirrection(c.getId(), 'h', map, true);
                if (map.getCase(p) == null)
                    continue;
                // On cherche la distance entre
                int dis = PathFinding.getDistanceBetween(map, endCell, p);
                // Si la distance est strictement inférieur à 1000 et que la case
                // est marchable et que personne ne
                // se trouve dessus et que la case n'est pas interdite
                if (dis < dist && map.getCase(p).isWalkable(true, true, -1)
                        && map.getCase(p).getFirstFighter() == null
                        && !forbidens.contains(map.getCase(p)))
                {
                    // On crée la distance
                    dist = dis;
                    // On modifie la cellule
                    cellID = p;
                }

                int m = PathFinding.GetCaseIDFromDirrection(c.getId(), 'd', map, true);
                if (map.getCase(m) == null)
                    continue;
                // On cherche la distance entre
                dis = PathFinding.getDistanceBetween(map, endCell, m);
                // Si la distance est strictement inférieur à 1000 et que la case
                // est marchable et que personne ne
                // se trouve dessus et que la case n'est pas interdite
                if (dis < dist && map.getCase(m).isWalkable(true, true, -1)
                        && map.getCase(m).getFirstFighter() == null
                        && !forbidens.contains(map.getCase(m)))
                {
                    // On crée la distance
                    dist = dis;
                    // On modifie la cellule
                    cellID = m;
                }
            }
            else if(c == bg)//En Bas à Gauche.
            {
                if(bg.getFirstFighter() == null && bg.blockLoS() == true)
                {
                    int p = PathFinding.GetCaseIDFromDirrection(c.getId(), 'f', map, true);
                    if (map.getCase(p) == null)
                        continue;
                    // On cherche la distance entre
                    int dis = PathFinding.getDistanceBetween(map, endCell, p);
                    // Si la distance est strictement inférieur à 1000 et que la case
                    // est marchable et que personne ne
                    // se trouve dessus et que la case n'est pas interdite
                    if (dis < dist && map.getCase(p).isWalkable(true, true, -1)
                            && map.getCase(p).getFirstFighter() == null
                            && !forbidens.contains(map.getCase(p)))
                    {
                        // On crée la distance
                        dist = dis;
                        // On modifie la cellule
                        cellID = p;
                    }
                }
                int p = PathFinding.GetCaseIDFromDirrection(c.getId(), 'h', map, true);
                if (map.getCase(p) == null)
                    continue;
                // On cherche la distance entre
                int dis = PathFinding.getDistanceBetween(map, endCell, p);
                // Si la distance est strictement inférieur à 1000 et que la case
                // est marchable et que personne ne
                // se trouve dessus et que la case n'est pas interdite
                if (dis < dist && map.getCase(p).isWalkable(true, true, -1)
                        && map.getCase(p).getFirstFighter() == null
                        && !forbidens.contains(map.getCase(p)))
                {
                    // On crée la distance
                    dist = dis;
                    // On modifie la cellule
                    cellID = p;
                }

                int m = PathFinding.GetCaseIDFromDirrection(c.getId(), 'd', map, true);
                if (map.getCase(m) == null)
                    continue;
                // On cherche la distance entre
                dis = PathFinding.getDistanceBetween(map, endCell, m);
                // Si la distance est strictement inférieur à 1000 et que la case
                // est marchable et que personne ne
                // se trouve dessus et que la case n'est pas interdite
                if (dis < dist && map.getCase(m).isWalkable(true, true, -1)
                        && map.getCase(m).getFirstFighter() == null
                        && !forbidens.contains(map.getCase(m)))
                {
                    // On crée la distance
                    dist = dis;
                    // On modifie la cellule
                    cellID = m;
                }
            }
            else if(c == hg)//En Haut à Gauche.
            {
                if(hg.getFirstFighter() == null && hg.blockLoS() == true)
                {
                    int p = PathFinding.GetCaseIDFromDirrection(c.getId(), 'd', map, true);
                    if (map.getCase(p) == null)
                        continue;
                    // On cherche la distance entre
                    int dis = PathFinding.getDistanceBetween(map, endCell, p);
                    // Si la distance est strictement inférieur à 1000 et que la case
                    // est marchable et que personne ne
                    // se trouve dessus et que la case n'est pas interdite
                    if (dis < dist && map.getCase(p).isWalkable(true, true, -1)
                            && map.getCase(p).getFirstFighter() == null
                            && !forbidens.contains(map.getCase(p)))
                    {
                        // On crée la distance
                        dist = dis;
                        // On modifie la cellule
                        cellID = p;
                    }
                }
            }
            else if(c == bd)//En Haut à Gauche.
            {
                if(bd.getFirstFighter() == null && bd.blockLoS() == true)
                {
                    int p = PathFinding.GetCaseIDFromDirrection(c.getId(), 'h', map, true);
                    if (map.getCase(p) == null)
                        continue;
                    // On cherche la distance entre
                    int dis = PathFinding.getDistanceBetween(map, endCell, p);
                    // Si la distance est strictement inférieur à 1000 et que la case
                    // est marchable et que personne ne
                    // se trouve dessus et que la case n'est pas interdite
                    if (dis < dist && map.getCase(p).isWalkable(true, true, -1)
                            && map.getCase(p).getFirstFighter() == null
                            && !forbidens.contains(map.getCase(p)))
                    {
                        // On crée la distance
                        dist = dis;
                        // On modifie la cellule
                        cellID = p;
                    }
                }
            }
        }
        return cellID == startCell ? -1 : cellID;
    }


    public static boolean casesAreInSameLine(GameMap map, GameCase c1, GameCase c2, int max) {
        char dir = getDirBetweenTwoCase(c1.getId(), c2.getId(), map, true);
        if(dir != 0) {
            GameCase c = c1;
            for (int a = 0; a < max; a++) {
                if (GetCaseIDFromDirrection(c.getId(), dir, map, true) == c2.getId())
                    return true;
                if (GetCaseIDFromDirrection(c.getId(), dir, map, true) == -1)
                    break;
                if(!c.isWalkable(true) || c.getFirstFighter() != null)
                    break;
                c = map.getCase(GetCaseIDFromDirrection(c.getId(), dir, map, true));
            }
        }
        return false;
    }

    public static boolean casesAreInSameLine(GameMap map, int c1, int c2, char dir, int max) {
        if (c1 == c2)
            return true;

        if (dir != 'z')//Si la direction est d�finie
        {
            for (int a = 0; a < max; a++) {
                if (GetCaseIDFromDirrection(c1, dir, map, true) == c2)
                    return true;
                if (GetCaseIDFromDirrection(c1, dir, map, true) == -1)
                    break;
                c1 = GetCaseIDFromDirrection(c1, dir, map, true);
            }
        } else
        //Si on doit chercher dans toutes les directions
        {
            char[] dirs = {'b', 'd', 'f', 'h'};
            for (char d : dirs) {
                int c = c1;
                for (int a = 0; a < max; a++) {
                    if (GetCaseIDFromDirrection(c, d, map, true) == c2)
                        return true;
                    c = GetCaseIDFromDirrection(c, d, map, true);
                }
            }
        }
        return false;
    }

    public static ArrayList<Fighter> getCiblesByZoneByWeapon(Fight fight,
                                                             int type, GameCase cell, int castCellID) {
        ArrayList<Fighter> cibles = new ArrayList<>();
        char c = getDirBetweenTwoCase(castCellID, cell.getId(), fight.getMap(), true);
        if (c == 0) {
            //On cible quand meme le fighter sur la case
            if (cell.getFirstFighter() != null)
                cibles.add(cell.getFirstFighter());
            return cibles;
        }

        switch (type) {
            //Cases devant celle ou l'on vise
            case Constant.ITEM_TYPE_MARTEAU:
                Fighter f = getFighter2CellBefore(castCellID, c, fight.getMap());
                if (f != null)
                    cibles.add(f);
                Fighter g = get1StFighterOnCellFromDirection(fight.getMap(), castCellID, (char) (c - 1));
                if (g != null)
                    cibles.add(g);//Ajoute case a gauche
                Fighter h = get1StFighterOnCellFromDirection(fight.getMap(), castCellID, (char) (c + 1));
                if (h != null)
                    cibles.add(h);//Ajoute case a droite
                Fighter i = cell.getFirstFighter();
                if (i != null)
                    cibles.add(i);
                break;
            case Constant.ITEM_TYPE_BATON:
                int dist = PathFinding.getDistanceBetween(fight.getMap(), cell.getId(), castCellID);
                int newCell = PathFinding.getCaseIDFromDirrection(castCellID, c, fight.getMap());

                Fighter j = get1StFighterOnCellFromDirection(fight.getMap(), (dist > 1 ? newCell : castCellID), (char) (c - 1));
                if (j != null)
                    cibles.add(j);//Ajoute case a gauche
                Fighter k = get1StFighterOnCellFromDirection(fight.getMap(), (dist > 1 ? newCell : castCellID), (char) (c + 1));
                if (k != null)
                    cibles.add(k);//Ajoute case a droite
                Fighter l = cell.getFirstFighter();
                if (l != null)
                    cibles.add(l);//Ajoute case cible
                break;
            case Constant.ITEM_TYPE_PIOCHE:
            case Constant.ITEM_TYPE_EPEE:
            case Constant.ITEM_TYPE_FAUX:
            case Constant.ITEM_TYPE_DAGUES:
            case Constant.ITEM_TYPE_BAGUETTE:
            case Constant.ITEM_TYPE_PELLE:
            case Constant.ITEM_TYPE_ARC:
            case Constant.ITEM_TYPE_HACHE:
            case Constant.ITEM_TYPE_OUTIL:
                Fighter m = cell.getFirstFighter();
                if (m != null)
                    cibles.add(m);
                break;
        }
        return cibles;
    }

    private static Fighter get1StFighterOnCellFromDirection(GameMap map, int id,
                                                            char c) {
        if (c == (char) ('a' - 1))
            c = 'h';
        if (c == (char) ('h' + 1)) c = 'a';
        GameCase cell = map.getCase(GetCaseIDFromDirrection(id, c, map, false));
        return cell != null ? cell.getFirstFighter() : null;
    }

    private static Fighter getFighter2CellBefore(int CellID, char c, GameMap map) {
        int new2CellID = GetCaseIDFromDirrection(GetCaseIDFromDirrection(CellID, c, map, false), c, map, false);
        GameCase cell = map != null ? map.getCase(new2CellID) : null;
        return cell != null ? cell.getFirstFighter() : null;
    }

    public static char getDirBetweenTwoCase(int cell1ID, int cell2ID, GameMap map,
                                            boolean Combat) {
        ArrayList<Character> dirs = new ArrayList<Character>();
        dirs.add('b');
        dirs.add('d');
        dirs.add('f');
        dirs.add('h');
        if (!Combat) {
            dirs.add('a');
            dirs.add('b');
            dirs.add('c');
            dirs.add('d');
        }
        for (char c : dirs) {
            int cell = cell1ID;
            for (int i = 0; i <= 64; i++) {
                if (GetCaseIDFromDirrection(cell, c, map, Combat) == cell2ID)
                    return c;
                cell = GetCaseIDFromDirrection(cell, c, map, Combat);
            }
        }
        return 0;
    }

    public static boolean canWalkToThisCell(GameMap map, int cell1, int cell2, boolean fight) {
        ArrayList<GameCase> path = new AStarPathFinding(map, cell1, cell2).getShortestPath();
        if(path == null || path.size() == 0) return path != null;
        char dir = getDirBetweenTwoCase(cell2, path.get(path.size() - 1).getId(), map, true);
        GameCase id = map.getCase(GetCaseIDFromDirrection(cell2, dir, map, true));

        return path.contains(id);
    }

    public static ArrayList<GameCase> getCellListFromAreaString(GameMap map,
                                                            int cellID, int castCellID, String zoneStr, int PONum, boolean isCC) {
        ArrayList<GameCase> cases = new ArrayList<GameCase>();
        int c = PONum;
        if (map == null || map.getCase(cellID) == null)
            return cases;
        cases.add(map.getCase(cellID));

        if(zoneStr.length() < (c + 2)) return cases;
        int taille = World.world.getCryptManager().getIntByHashedValue(zoneStr.charAt(c + 1));
        switch (zoneStr.charAt(c)) {
            case 'C':// Cercle
                for (int a = 0; a < taille; a++) {
                    char[] dirs = {'b', 'd', 'f', 'h'};
                    ArrayList<GameCase> cases2 = new ArrayList<GameCase>();// on �vite les
                    // modifications
                    // concurrentes
                    cases2.addAll(cases);
                    for (GameCase aCell : cases2) {
                        if(aCell == null) continue;
                        for (char d : dirs) {
                            GameCase cell = map.getCase(PathFinding.GetCaseIDFromDirrection(aCell.getId(), d, map, true));
                            if (cell == null)
                                continue;
                            if (!cases.contains(cell))
                                cases.add(cell);
                        }
                    }
                }
                break;

            case 'X':// Croix
                char[] dirs = {'b', 'd', 'f', 'h'};
                for (char d : dirs) {
                    int cID = cellID;
                    for (int a = 0; a < taille; a++) {
                        cases.add(map.getCase(GetCaseIDFromDirrection(cID, d, map, true)));
                        cID = GetCaseIDFromDirrection(cID, d, map, true);
                    }
                }
                break;

            case 'L':// Ligne
                char dir = PathFinding.getDirBetweenTwoCase(castCellID, cellID, map, true);
                for (int a = 0; a < taille; a++) {
                    cases.add(map.getCase(GetCaseIDFromDirrection(cellID, dir, map, true)));
                    cellID = GetCaseIDFromDirrection(cellID, dir, map, true);
                }
                break;

            case 'P':// Player?

                break;

            default:
                GameServer.a();
                break;
        }
        return cases;
    }

    public static int getCellXCoord(GameMap map, int cellID) {
        if (map == null)
            return 0;
        int w = map.getW();
        return ((cellID - (w - 1) * getCellYCoord(map, cellID)) / w);
    }

    public static int getCellYCoord(GameMap map, int cellID) {
        int w = map.getW();
        int loc5 = cellID / ((w * 2) - 1);
        int loc6 = cellID - loc5 * ((w * 2) - 1);
        int loc7 = loc6 % w;
        return (loc5 - loc7);
    }

    public static int getNearestCellAround(GameMap map, int startCell, int endCell, ArrayList<GameCase> forbidden) {
        if (map == null)
            return -1;
        if (forbidden == null) forbidden = new ArrayList<>();
        int dist = 1000, cellId = startCell;
        List<Short> cells = outForbiddenCells.get(map.getW() + "_" + map.getH());

        for (char d : new char[] {'b', 'd', 'f', 'h'}) {
            int newCellId = PathFinding.GetCaseIDFromDirrection(startCell, d, map, true);
            GameCase cell = map.getCase(newCellId);

            if (cell != null) {
                int distance = PathFinding.getDistanceBetween(map, endCell, newCellId);

                if (distance < dist && cell.isWalkable(true, true, -1) && cell.getFirstFighter() == null
                        && !forbidden.contains(cell) && !cells.contains((short) newCellId)) {
                    dist = distance;
                    cellId = newCellId;
                }
            }
        }
        return cellId == startCell ? -1 : cellId;
    }

    public static int getNearestCellAroundGA(GameMap map, int startCell,
                                             int endCell, ArrayList<GameCase> forbidens) {
        //On prend la cellule autour de la cible, la plus proche
        int dist = 1000;
        int cellID = startCell;
        if (forbidens == null)
            forbidens = new ArrayList<GameCase>();
        char[] dirs = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        for (char d : dirs) {
            int c = PathFinding.GetCaseIDFromDirrection(startCell, d, map, true);
            int dis = PathFinding.getDistanceBetween(map, endCell, c);
            if (map.getCase(c) == null)
                continue;
            if (dis < dist && map.getCase(c).isWalkable(true)
                    && map.getCase(c).getFirstFighter() == null
                    && !forbidens.contains(map.getCase(c))) {
                dist = dis;
                cellID = c;
            }
        }

        //On renvoie -1 si pas trouv�
        return cellID == startCell ? -1 : cellID;
    }

    public static ArrayList<GameCase> getShortestPathBetween(GameMap map, int start,
                                                         int dest, int distMax) {
        ArrayList<GameCase> curPath = new ArrayList<GameCase>();
        ArrayList<GameCase> curPath2 = new ArrayList<GameCase>();
        ArrayList<GameCase> closeCells = new ArrayList<GameCase>();
        int limit = 1000;
        //int oldCaseID = begin;
        GameCase curCase = map.getCase(start);
        int stepNum = 0;
        boolean stop = false;

        while (!stop && stepNum++ <= limit) {
            int nearestCell = getNearestCellAround(map, curCase.getId(), dest, closeCells);
            if (nearestCell == -1) {
                closeCells.add(curCase);
                if (curPath.size() > 0) {
                    curPath.remove(curPath.size() - 1);
                    if (curPath.size() > 0)
                        curCase = curPath.get(curPath.size() - 1);
                    else
                        curCase = map.getCase(start);
                } else {
                    curCase = map.getCase(start);
                }
            } else if (distMax == 0 && nearestCell == dest) {
                curPath.add(map.getCase(dest));
                break;
            } else if (distMax > PathFinding.getDistanceBetween(map, nearestCell, dest)) {
                curPath.add(map.getCase(dest));
                break;
            } else
            //on continue
            {
                curCase = map.getCase(nearestCell);
                closeCells.add(curCase);
                curPath.add(curCase);
            }
        }

        curCase = map.getCase(start);
        closeCells.clear();
        if (!curPath.isEmpty()) {
            closeCells.add(curPath.get(0));
        }

        while (!stop && stepNum++ <= limit) {
            int nearestCell = getNearestCellAround(map, curCase.getId(), dest, closeCells);
            if (nearestCell == -1) {
                closeCells.add(curCase);
                if (curPath2.size() > 0) {
                    curPath2.remove(curPath2.size() - 1);
                    if (curPath2.size() > 0)
                        curCase = curPath2.get(curPath2.size() - 1);
                    else
                        curCase = map.getCase(start);
                } else
                //Si retour a zero
                {
                    curCase = map.getCase(start);
                }
            } else if (distMax == 0 && nearestCell == dest) {
                curPath2.add(map.getCase(dest));
                break;
            } else if (distMax > PathFinding.getDistanceBetween(map, nearestCell, dest)) {
                curPath2.add(map.getCase(dest));
                break;
            } else
            //on continue
            {
                curCase = map.getCase(nearestCell);
                closeCells.add(curCase);
                curPath2.add(curCase);
            }
        }

        if ((curPath2.size() < curPath.size() && curPath2.size() > 0)
                || curPath.isEmpty())
            curPath = curPath2;
        return curPath;
    }

    public static String getShortestStringPathBetween(GameMap map, int start,
                                                      int dest, int distMax) {
        if (start == dest)
            return null;
        ArrayList<GameCase> path = getShortestPathBetween(map, start, dest, distMax);
        if (path == null)
            return null;
        String pathstr = "";
        int curCaseID = start;
        char curDir = '\000';
        for (GameCase c : path) {
            char d = getDirBetweenTwoCase(curCaseID, c.getId(), map, true);
            if (d == 0)
                return null;
            if (curDir != d) {
                if (path.indexOf(c) != 0)
                    pathstr = pathstr + World.world.getCryptManager().cellID_To_Code(curCaseID);
                pathstr = pathstr + d;
                curDir = d;
            }
            curCaseID = c.getId();
        }
        if (curCaseID != start) {
            pathstr = pathstr + World.world.getCryptManager().cellID_To_Code(curCaseID);
        }
        path.clear();
        if (pathstr.isEmpty())
            return null;
        return "a" + World.world.getCryptManager().cellID_To_Code(start) + pathstr;
    }

    public static boolean checkLoS(GameMap map, int cell1, int cell2,
                                   Fighter fighter, boolean isPeur) {
        if (fighter != null && fighter.getPlayer() != null) // on ne rev�rifie pas (en plus du client) pour les joueurs
            return true;
        ArrayList<Integer> CellsToConsider;
        CellsToConsider = getLoSBotheringIDCases(map, cell1, cell2, true);
        if (CellsToConsider == null) {
            return true;
        }
        for (Integer cellID : CellsToConsider) {
            if (map.getCase(cellID) != null)
                if (!map.getCase(cellID).blockLoS()
                        || (!map.getCase(cellID).isWalkable(false) && isPeur)) {
                    return false;
                }
        }
        return true;
    }

    private static ArrayList<Integer> getLoSBotheringIDCases(GameMap map,
                                                             int cellID1, int cellID2, boolean Combat) {
        ArrayList<Integer> toReturn = new ArrayList<Integer>();
        int consideredCell1 = cellID1;
        int consideredCell2 = cellID2;
        char dir = 'b';
        int diffX = 0;
        int diffY = 0;
        int compteur = 0;
        ArrayList<Character> dirs = new ArrayList<Character>();
        dirs.add('b');
        dirs.add('d');
        dirs.add('f');
        dirs.add('h');

        while (getDistanceBetween(map, consideredCell1, consideredCell2) > 2
                && compteur < 300) {
            diffX = getCellXCoord(map, consideredCell1)
                    - getCellXCoord(map, consideredCell2);
            diffY = getCellYCoord(map, consideredCell1)
                    - getCellYCoord(map, consideredCell2);
            if (Math.abs(diffX) > Math.abs(diffY)) { // si il ya une plus grande diff�rence pour la premi�re coordonn�e
                if (diffX > 0)
                    dir = 'f';
                else
                    dir = 'b';
                consideredCell1 = GetCaseIDFromDirrection(consideredCell1, dir, map, Combat); // on avance le chemin d'obstacles possibles
                consideredCell2 = GetCaseIDFromDirrection(consideredCell2, getOpositeDirection(dir), map, Combat); // des deux c�t�s
                toReturn.add(consideredCell1); // la liste des cases potentiellement obstacles
                toReturn.add(consideredCell2); // la liste des cases potentiellement obstacles
            } else if (Math.abs(diffX) < Math.abs(diffY)) { // si il y a une plus grand diff�rence pour la seconde
                if (diffY > 0) // d�termine dans quel sens
                    dir = 'h';
                else
                    dir = 'd';
                consideredCell1 = GetCaseIDFromDirrection(consideredCell1, dir, map, Combat); // on avance le chemin d'obstacles possibles
                consideredCell2 = GetCaseIDFromDirrection(consideredCell2, getOpositeDirection(dir), map, Combat); // des deux c�t�s
                toReturn.add(consideredCell1); // la liste des cases potentiellement obstacles
                toReturn.add(consideredCell2); // la liste des cases potentiellement obstacles
            } else {
                if (compteur == 0) // si on est en diagonale parfaite
                    return getLoSBotheringCasesInDiagonal(map, cellID1, cellID2, diffX, diffY);
                if (dir == 'f' || dir == 'b') // on change la direction dans le cas o� on se retrouve en diagonale
                    if (diffY > 0)
                        dir = 'h';
                    else
                        dir = 'd';
                else if (dir == 'h' || dir == 'd')
                    if (diffX > 0)
                        dir = 'f';
                    else
                        dir = 'b';
                consideredCell1 = GetCaseIDFromDirrection(consideredCell1, dir, map, Combat); // on avance le chemin d'obstacles possibles
                consideredCell2 = GetCaseIDFromDirrection(consideredCell2, getOpositeDirection(dir), map, Combat); // des deux c�t�s
                toReturn.add(consideredCell1); // la liste des cases potentiellement obstacles
                toReturn.add(consideredCell2); // la liste des cases potentiellement obstacles
            }
            compteur++;
        }
        if (getDistanceBetween(map, consideredCell1, consideredCell2) == 2) {
            dir = 0;
            diffX = getCellXCoord(map, consideredCell1)
                    - getCellXCoord(map, consideredCell2);
            diffY = getCellYCoord(map, consideredCell1)
                    - getCellYCoord(map, consideredCell2);
            if (diffX == 0)
                if (diffY > 0)
                    dir = 'h';
                else
                    dir = 'd';
            if (diffY == 0)
                if (diffX > 0)
                    dir = 'f';
                else
                    dir = 'b';
            if (dir != 0)
                toReturn.add(GetCaseIDFromDirrection(consideredCell1, dir, map, Combat));
        }
        return toReturn;
    }

    private static ArrayList<Integer> getLoSBotheringCasesInDiagonal(GameMap map,
                                                                     int cellID1, int cellID2, int diffX, int diffY) {
        ArrayList<Integer> toReturn = new ArrayList<Integer>();
        char dir = 'a';
        if (diffX > 0 && diffY > 0)
            dir = 'g';
        if (diffX > 0 && diffY < 0)
            dir = 'e';
        if (diffX < 0 && diffY > 0)
            dir = 'a';
        if (diffX < 0 && diffY < 0)
            dir = 'c';
        int consideredCell = cellID1, compteur = 0;
        while (consideredCell != -1 && compteur < 100) {
            consideredCell = GetCaseIDFromDirrection(consideredCell, dir, map, false);
            if (consideredCell == cellID2)
                return toReturn;
            toReturn.add(consideredCell);
            compteur++;
        }
        return toReturn;
    }

    public static ArrayList<Fighter> getFightersAround(int cellID, GameMap map) {
        char[] dirs = {'b', 'd', 'f', 'h'};
        ArrayList<Fighter> fighters = new ArrayList<>();

        for (char dir : dirs) {
            GameCase gameCase = map.getCase(GetCaseIDFromDirrection(cellID, dir, map, false));
            if(gameCase == null) continue;
            Fighter f = gameCase.getFirstFighter();
            if (f != null)
                fighters.add(f);
        }
        return fighters;
    }

    public static char getDirEntreDosCeldas(GameMap map, int id1, int id2) {
        if (id1 == id2)
            return 0;
        if (map == null)
            return 0;
        int difX = (getCellXCoord(map, id1) - getCellXCoord(map, id2));
        int difY = (getCellYCoord(map, id1) - getCellYCoord(map, id2));
        int difXabs = Math.abs(difX);
        int difYabs = Math.abs(difY);
        if (difXabs > difYabs) {
            if (difX > 0)
                return 'f';
            else
                return 'b';
        } else {
            if (difY > 0)
                return 'h';
            else
                return 'd';
        }
    }

    public static int getCellArroundByDir(int cellId, char dir, GameMap map) {
        if (map == null)
            return -1;

        switch (dir) {
            case 'b':
                return cellId + map.getW();//En Haut � Droite.
            case 'd':
                return cellId + (map.getW() - 1);//En Haut � Gauche.
            case 'f':
                return cellId - map.getW();//En Bas � Gauche.
            case 'h':
                return cellId - map.getW() + 1;//En Bas � Droite.
        }
        return -1;
    }

    public static GameCase checkIfCanPushEntity(Fight fight, int startCell,
                                            int endCell, char direction) {
        GameMap map = fight.getMap();
        GameCase cell = map.getCase(getCellArroundByDir(startCell, direction, map));
        GameCase oldCell = cell;
        GameCase actualCell = cell;

        while (actualCell.getId() != endCell) {
            actualCell = map.getCase(getCellArroundByDir(actualCell.getId(), direction, map));
            if (!actualCell.getFighters().isEmpty()
                    || !actualCell.isWalkable(false))
                return oldCell;

            for (Trap trap : fight.getAllTraps()) {

                if (PathFinding.getDistanceBetween(fight.getMap(), trap.getCell().getId(), actualCell.getId()) <= trap.getSize())
                    return actualCell;
            }

            oldCell = actualCell;
        }

        return null;
    }

    public static boolean haveFighterOnThisCell(int cell, Fight fight, boolean astar) {
        for (Fighter f : fight.getFighters(astar ? 3 : 7)) {
            if (f.getCell() != null && f.getCell().getId() == cell && !f.isDead())
                return true;
        }
        return false;
    }

    public static int getCaseIDFromDirrection(int CaseID, char Direccion,
                                              GameMap map) {
        switch (Direccion) {// mag.get_w() = te da el ancho del mapa
            case 'b':
                return CaseID + map.getW(); // diagonal derecha abajo
            case 'd':
                return CaseID + (map.getW() - 1); // diagonal izquierda abajo
            case 'f':
                return CaseID - map.getW(); // diagonal izquierda arriba
            case 'h':
                return CaseID - map.getW() + 1;// diagonal derecha arriba
        }
        return -1;
    }

    public static boolean cellArroundCaseIDisOccuped(Fight fight, int cell) {
        char[] dirs = {'b', 'd', 'f', 'h'};
        ArrayList<Integer> Cases = new ArrayList<Integer>();

        for (char dir : dirs) {
            int caseID = PathFinding.GetCaseIDFromDirrection(cell, dir, fight.getMap(), true);
            Cases.add(caseID);
        }
        int ha = 0;
        for (int o = 0; o < Cases.size(); o++) {
            GameCase c = fight.getMap().getCase(Cases.get(o));
            if (c != null && c.getFirstFighter() != null)
                ha++;
        }
        return ha != 4;

    }

    public static List<GameCase> getCellsByDir(Fight fight, int startCell, char dir, int limit) {
        List<GameCase> cells = new ArrayList<>();
        for(int i = 0; i < limit; i++) {
            int id = GetCaseIDFromDirrection(startCell, dir, fight.getMap(), true);
            if(!haveFighterOnThisCell(id, fight, false)) {
                cells.add(fight.getMap().getCase(id));
            }
        }
        return cells;
    }

    public static Fighter getEnnemyInLine(GameMap map, int startCell, Fighter fighter, int min, int max) {
        Fighter target = null;
        char[] dirs = {'b', 'd', 'f', 'h'};
        int endCell = fighter.getCell().getId();

        for (char d : dirs) {
            int id = PathFinding.GetCaseIDFromDirrection(startCell, d, map, true);
            GameCase cell = map.getCase(id);
            if (cell == null)
                continue;
            int distance = PathFinding.getDistanceBetween(map, endCell, cell.getId());
            // Si la distance est strictement inférieur à 1000 et que la case
            // est marchable et que personne ne
            // se trouve dessus et que la case n'est pas interdite
            if (distance < max && distance > min && cell.getFirstFighter() != null)
                if(cell.getFirstFighter().getTeam2() != fighter.getTeam2())
                    target = cell.getFirstFighter();

            boolean ok = false;
            while(!ok) {
                int h = PathFinding.GetCaseIDFromDirrection(cell.getId(), d, map, true);
                if (map.getCase(h) != null) {
                    distance = PathFinding.getDistanceBetween(map, endCell, h);
                    // Si la distance est strictement inférieur à 1000 et que la case est marchable et que personne ne se trouve dessus et que la case n'est pas interdite
                    if (distance < max && cell.getFirstFighter() != null)
                        if(cell.getFirstFighter().getTeam2() != fighter.getTeam2())
                            target = cell.getFirstFighter();
                } else {
                    ok = true;
                }
                cell = map.getCase(h);
            }
        }
        return target;
    }
}
