package org.starloco.locos.fight.ia.util;

import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.common.PathFinding;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractEasyIA;
import org.starloco.locos.fight.spells.Spell;
import org.starloco.locos.fight.spells.Spell.SortStats;
import org.starloco.locos.fight.spells.SpellEffect;
import org.starloco.locos.game.action.GameAction;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Constant;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Locos on 04/10/2015.
 */
public class Function {

    private final static Function instance = new Function();

    public static Function getInstance() {
        return instance;
    }

    public int IfPossibleRasboulvulner(Fight fight, Fighter fighter, Fighter target)// 0 = Rien, 5 = EC, 666 = NULL, 10 = SpellNull ou ActionEnCour ou Can'tCastSpell, 0 = AttaqueOK
    {
        if (fight == null || fighter == null)
            return 0;
        SortStats SS = null;
        for(Map.Entry<Integer, SortStats> entry : fighter.getMob().getSpells().entrySet()) {
            SortStats a = entry.getValue();
            if(a.getSpellID() == 1039)
                SS = a;
        }
        if (target == null)
            return 666;
        if(fighter.getPa() < 14)
            return 0;
        int attack = fight.tryCastSpell(fighter, SS, target.getCell().getId());

        if (attack != 0) {
            SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 1039, target.getId() + "", target.getId() + ",+" + 1);
            return attack;
        }
        return 0;
    }

    public boolean invoctantaIfPossible(Fight fight, Fighter fighter)
    {
        if (fight == null || fighter == null)
            return false;
        if (fighter.nbInvocation() >= 4)
            return false;
        Fighter nearest = getNearestEnnemy(fight, fighter, false);
        if (nearest == null)
            return false;
        int nearestCell = fighter.getCell().getId();
        int limit = 50;
        int _loc0_ = 0;
        SortStats spell = null;
        if (fighter.haveState(36))
        {

            spell = World.world.getSort(1110).getStatsByLevel(5);
            fighter.setState(36, 0);
        }
        if (fighter.haveState(37))
        {
            if(this.hasMobInFight(fight, 1091))
                return false;
            spell = World.world.getSort(1109).getStatsByLevel(5);
            fighter.setState(37, 0);
        }
        if (fighter.haveState(38))
        {
            if(this.hasMobInFight(fight, 1092))
                return false;
            spell = World.world.getSort(1108).getStatsByLevel(5);
            fighter.setState(38, 0);
        }
        if (fighter.haveState(35))
        {
            if(this.hasMobInFight(fight, 424))
                return false;
            spell = World.world.getSort(1107).getStatsByLevel(5);
            fighter.setState(35, 0);
        }
        while (_loc0_++ < limit)
        {
            nearestCell = PathFinding.getNearestCellAround(fight.getMap(), nearestCell, nearest.getCell().getId(), null);
        }
        if (nearestCell == -1) {
            nearestCell = PathFinding.getAvailableCellArround(fight, nearest.getCell().getId(), null);
            if(nearestCell == 0)
                return false;
        }
        if (spell == null)
            return false;
        int invoc = fight.tryCastSpell(fighter, spell, nearestCell);
        if (invoc != 0)
            return false;
        return true;
    }

    public boolean hasMobInFight(Fight fight, int id) {
        for(Fighter fighter : fight.getFighters(7))
            if(fighter.getMob() != null && !fighter.isDead() && fighter.getMob().getTemplate() != null && fighter.getMob().getTemplate().getId() == id)
                return true;
        return false;
    }
    public int tpIfPossibleRasboul(Fight fight, Fighter fighter, Fighter target)// 0 = Rien, 5 = EC, 666 = NULL, 10 = SpellNull ou ActionEnCour ou Can'tCastSpell, 0 = AttaqueOK
    {
        if (fight == null || fighter == null)
            return 0;
        SortStats SS = null;
        for(Map.Entry<Integer, SortStats> entry : fighter.getMob().getSpells().entrySet())
        {
            SortStats a = entry.getValue();
            if(a.getSpellID() == 1041)
                SS = a;
        }
        if (target == null)
            return 666;
        int attack = fight.tryCastSpell(fighter, SS, target.getCell().getId());
        if (attack != 0)
            return attack;
        return 0;
    }
    public SortStats findSpell(Fighter fighter, int id) {
        for(SortStats spell : fighter.getMob().getSpells().values()) {
            if(spell != null && spell.getSpellID() == id)
                return spell;
        }
        return null;
    }

    public boolean moveNearIfPossible(Fight fight, Fighter F, Fighter T)
    {
        if (fight == null)
            return false;
        if (F == null)
            return false;
        if (T == null)
            return false;
        if (F.getCurPm(fight) <= 0)
            return false;
        GameMap map = fight.getMap();
        if (map == null)
            return false;
        GameCase cell = F.getCell();
        if (cell == null)
            return false;
        GameCase cell2 = T.getCell();
        if (cell2 == null)
            return false;
        if (PathFinding.isNextTo(map, cell.getId(), cell2.getId()))
            return false;

        int cellID = PathFinding.getNearestCellAround(map, cell2.getId(), cell.getId(), null);
        //On demande le chemin plus court
        //Mais le chemin le plus court ne prend pas en compte les bords de map.
        if (cellID == -1)
        {
            Map<Integer, Fighter> ennemys = getLowHpEnnemyList(fight, F);
            for (Map.Entry<Integer, Fighter> target : ennemys.entrySet())
            {
                int cellID2 = PathFinding.getNearestCellAround(map, target.getValue().getCell().getId(), cell.getId(), null);
                if (cellID2 != -1)
                {
                    cellID = cellID2;
                    break;
                }
            }
        }
        ArrayList<GameCase> path = new AStarPathFinding(fight, cell.getId(), cell2.getId()).getShortestPath();
        if (path == null || path.isEmpty())
            return false;
        ArrayList<GameCase> finalPath = new ArrayList<GameCase>();
        for (int a = 0; a < F.getCurPm(fight); a++)
        {
            if (path.size() == a)
                break;
            finalPath.add(path.get(a));
        }
        String pathstr = "";
        try
        {
            int curCaseID = cell.getId();
            int curDir = 0;
            for (GameCase c : finalPath)
            {
                char d = PathFinding.getDirBetweenTwoCase(curCaseID, c.getId(), map, true);
                if (d == 0)
                    return false;//Ne devrait pas arriver :O
                if (curDir != d)
                {
                    if (finalPath.indexOf(c) != 0)
                        pathstr += World.world.getCryptManager().cellID_To_Code(curCaseID);
                    pathstr += d;
                }
                curCaseID = c.getId();
            }
            if (curCaseID != cell.getId())
                pathstr += World.world.getCryptManager().cellID_To_Code(curCaseID);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //Cr�ation d'une GameAction
        GameAction GA = new GameAction(0, 1, "");
        GA.args = pathstr;
        boolean result = fight.onFighterDeplace(F, GA);

        return result;
    }

    public int getMaxCellForTP(Fight fight, Fighter F, Fighter T, int dist) {
        if (fight == null || F == null || T == null || dist < 1)
            return -1;

        GameMap map = fight.getMap();
        GameCase cell = F.getCell(), cell2 = T.getCell(), temp;

        if (map == null || cell == null || cell2 == null || PathFinding.isNextTo(map, cell.getId(), cell2.getId()))
            return -1;

        ArrayList<GameCase> path = new AStarPathFinding(fight, cell.getId(), cell2.getId()).getShortestPath();

        if (path == null || path.isEmpty())
            return -1;

        int cellId = -1;

        for (int a = 0; a < dist; a++) {
            if (path.size() == a) break;
            temp = path.get(a);
            if(temp.getFirstFighter() != null || T.getId() == temp.getId()) continue;
                cellId = temp.getId();
        }

        return cellId;
    }

    public int attackBondIfPossible(Fight fight, Fighter fighter, Fighter target)// 0 = Rien, 5 = EC, 666 = NULL, 10 = SpellNull ou ActionEnCour ou Can'tCastSpell, 0 = AttaqueOK
    {
        if (fight == null || fighter == null)
            return 0;
        int cell = 0;
        SortStats SS2 = null;

        if(target == null)
            return 0;
        for (Map.Entry<Integer, SortStats> S : fighter.getMob().getSpells().entrySet())
        {
            int cellID = PathFinding.getCaseBetweenEnemy(target.getCell().getId(), fight.getMap(), fight);
            boolean effet4 = false;
            boolean effet6 = false;

            for(SpellEffect f : S.getValue().getEffects())
            {
                if(f.getEffectID() == 4)
                    effet4 = true;
                if(f.getEffectID() == 6)
                {
                    effet6 = true;
                    effet4 = true;
                }
            }
            if(effet4 == false)
                continue;
            if(effet6 == false)
            {
                cell = cellID;
                SS2 = S.getValue();
            }else
            {
                cell = target.getCell().getId();
                SS2 = S.getValue();
            }
        }
        if (cell >= 15 && cell <= 463 && SS2 != null)
        {
            int attack = fight.tryCastSpell(fighter, SS2, cell);
            if (attack != 0)
                return SS2.getSpell().getDuration();
        }
        else
        {
            if (target == null || SS2 == null)
                return 0;
            int attack = fight.tryCastSpell(fighter, SS2, cell);
            if (attack != 0)
                return SS2.getSpell().getDuration();
        }
        return 0;
    }

   

    public int moveFarIfPossible(Fight fight, Fighter F)
    {
        if (fight == null || F == null)
            return 0;
        if (fight.getMap() == null)
            return 0;
        int nbrcase = 0;
        //On cr�er une liste de distance entre ennemi et de cellid, nous permet de savoir si un ennemi est coll� a nous
        int dist[] = {1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000}, cell[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        for (int i = 0; i < 10; i++)//on repete 10 fois pour les 10 joueurs ennemis potentielle
        {
            for (Fighter f : fight.getFighters(3))
            {

                if (f.isDead())
                    continue;
                if (f == F || f.getTeam() == F.getTeam())
                    continue;
                int cellf = f.getCell().getId();
                if (cellf == cell[0] || cellf == cell[1] || cellf == cell[2]
                        || cellf == cell[3] || cellf == cell[4]
                        || cellf == cell[5] || cellf == cell[6]
                        || cellf == cell[7] || cellf == cell[8]
                        || cellf == cell[9])
                    continue;
                int d = 0;
                d = PathFinding.getDistanceBetween(fight.getMap(), F.getCell().getId(), f.getCell().getId());
                if (d < dist[i])
                {
                    dist[i] = d;
                    cell[i] = cellf;
                }
                if (dist[i] == 1000)
                {
                    dist[i] = 0;
                    cell[i] = F.getCell().getId();
                }
            }
        }
        //if(dist[0] == 0)return false;//Si ennemi "coll�"

        int dist2[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int PM = F.getCurPm(fight), caseDepart = F.getCell().getId(), destCase = F.getCell().getId();
        ArrayList<Integer> caseUse = new ArrayList<Integer>();
        caseUse.add(caseDepart); // On ne revient pas a sa position de d�part
        for (int i = 0; i <= PM; i++)//Pour chaque PM on analyse la meilleur case a prendre. C'est a dire la plus �liogn�e de tous.
        {
            if (destCase > 0)
                caseDepart = destCase;
            int curCase = caseDepart;

            /** En +15 **/
            curCase += 15;
            int infl = 0, inflF = 0;
            for (int a = 0; a < 10 && dist[a] != 0; a++)
            {
                dist2[a] = PathFinding.getDistanceBetween(fight.getMap(), curCase, cell[a]);//pour chaque ennemi on calcul la nouvelle distance depuis cette nouvelle case (curCase)
                if (dist2[a] > dist[a])//Si la cellule (curCase) demander et plus distante que la pr�cedente de l'ennemi alors on dirrige le mouvement vers elle
                    infl++;
            }

            if (infl > inflF
                    && curCase >= 15
                    && curCase <= 463
                    && testCotes(destCase, curCase)
                    && fight.getMap().getCase(curCase).isWalkable(false, true, -1)
                    && fight.getMap().getCase(curCase).getFighters().isEmpty()
                    && !caseUse.contains(curCase))//Si l'influence (infl) est la plus forte en comparaison avec inflF on garde la case si celle-ci est valide
            {
                inflF = infl;
                destCase = curCase;
            }
            /** En +15 **/

            /** En +14 **/
            curCase = caseDepart + 14;
            infl = 0;

            for (int a = 0; a < 10 && dist[a] != 0; a++)
            {
                dist2[a] = PathFinding.getDistanceBetween(fight.getMap(), curCase, cell[a]);
                if (dist2[a] > dist[a])
                    infl++;
            }

            if (infl > inflF
                    && curCase >= 15
                    && curCase <= 463
                    && testCotes(destCase, curCase)
                    && fight.getMap().getCase(curCase).isWalkable(false, true, -1)
                    && fight.getMap().getCase(curCase).getFighters().isEmpty()
                    && !caseUse.contains(curCase))
            {
                inflF = infl;
                destCase = curCase;
            }
            /** En +14 **/

            /** En -15 **/
            curCase = caseDepart - 15;
            infl = 0;
            for (int a = 0; a < 10 && dist[a] != 0; a++)
            {
                dist2[a] = PathFinding.getDistanceBetween(fight.getMap(), curCase, cell[a]);
                if (dist2[a] > dist[a])
                    infl++;
            }

            if (infl > inflF
                    && curCase >= 15
                    && curCase <= 463
                    && testCotes(destCase, curCase)
                    && fight.getMap().getCase(curCase).isWalkable(false, true, -1)
                    && fight.getMap().getCase(curCase).getFighters().isEmpty()
                    && !caseUse.contains(curCase))
            {
                inflF = infl;
                destCase = curCase;
            }
            /** En -15 **/

            /** En -14 **/
            curCase = caseDepart - 14;
            infl = 0;
            for (int a = 0; a < 10 && dist[a] != 0; a++)
            {
                dist2[a] = PathFinding.getDistanceBetween(fight.getMap(), curCase, cell[a]);
                if (dist2[a] > dist[a])
                    infl++;
            }

            if (infl > inflF
                    && curCase >= 15
                    && curCase <= 463
                    && testCotes(destCase, curCase)
                    && fight.getMap().getCase(curCase).isWalkable(false, true, -1)
                    && fight.getMap().getCase(curCase).getFighters().isEmpty()
                    && !caseUse.contains(curCase))
            {
                inflF = infl;
                destCase = curCase;
            }
            /** En -14 **/
            caseUse.add(destCase);
        }
        if (destCase < 15
                || destCase > 463
                || destCase == F.getCell().getId()
                || !fight.getMap().getCase(destCase).isWalkable(false, true, -1))
            return 0;

        if (F.getPm() <= 0)
            return 0;
        ArrayList<GameCase> path = new AStarPathFinding(fight, F.getCell().getId(), destCase).getShortestPath();
        if (path == null)
            return 0;
        ArrayList<GameCase> finalPath = new ArrayList<GameCase>();
        for (int a = 0; a < F.getCurPm(fight); a++)
        {
            if (path.size() == a)
                break;
            finalPath.add(path.get(a));
        }
        String pathstr = "";
        try
        {
            int curCaseID = F.getCell().getId();
            int curDir = 0;
            for (GameCase c : finalPath)
            {
                char d = PathFinding.getDirBetweenTwoCase(curCaseID, c.getId(), fight.getMap(), true);
                if (d == 0)
                    return 0;//Ne devrait pas arriver :O
                if (curDir != d)
                {
                    if (finalPath.indexOf(c) != 0)
                        pathstr += World.world.getCryptManager().cellID_To_Code(curCaseID);
                    pathstr += d;
                }
                curCaseID = c.getId();

                nbrcase = nbrcase + 1;
            }
            if (curCaseID != F.getCell().getId())
                pathstr += World.world.getCryptManager().cellID_To_Code(curCaseID);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //Cr�ation d'une GameAction
        GameAction GA = new GameAction(0, 1, "");
        GA.args = pathstr;

        if(!fight.onFighterDeplace(F, GA))
            return 0;

        return nbrcase * 500;
    }

    public boolean testCotes(int cellWeAre, int cellWego)//Nous permet d'interdire le d�placement du bord vers des cellules hors map
    {
        if (cellWeAre == 15 || cellWeAre == 44 || cellWeAre == 73
                || cellWeAre == 102 || cellWeAre == 131 || cellWeAre == 160
                || cellWeAre == 189 || cellWeAre == 218 || cellWeAre == 247
                || cellWeAre == 276 || cellWeAre == 305 || cellWeAre == 334
                || cellWeAre == 363 || cellWeAre == 392 || cellWeAre == 421
                || cellWeAre == 450)
        {
            if (cellWego == cellWeAre + 14 || cellWego == cellWeAre - 15)
                return false;
        }
        if (cellWeAre == 28 || cellWeAre == 57 || cellWeAre == 86
                || cellWeAre == 115 || cellWeAre == 144 || cellWeAre == 173
                || cellWeAre == 202 || cellWeAre == 231 || cellWeAre == 260
                || cellWeAre == 289 || cellWeAre == 318 || cellWeAre == 347
                || cellWeAre == 376 || cellWeAre == 405 || cellWeAre == 434
                || cellWeAre == 463)
        {
            if (cellWego == cellWeAre + 15 || cellWego == cellWeAre - 14)
                return false;
        }

        if (cellWeAre >= 451 && cellWeAre <= 462)
        {
            if (cellWego == cellWeAre + 15 || cellWego == cellWeAre + 14)
                return false;
        }
        if (cellWeAre >= 16 && cellWeAre <= 27)
        {
            if (cellWego == cellWeAre - 15 || cellWego == cellWeAre - 14)
                return false;
        }
        return true;
    }

    public boolean invocIfPossible(Fight fight, Fighter fighter)
    {
        if (fight == null || fighter == null)
            return false;
        if (fighter.nbInvocation() >= fighter.getTotalStats().getEffect(Constant.STATS_CREATURE))
            return false;
        Fighter nearest = getNearestEnnemy(fight, fighter, false);
        if (nearest == null)
            return false;
        int nearestCell = fighter.getCell().getId();
        int limit = 30;
        int _loc0_ = 0;
        SortStats spell = null;
        while ((spell = getInvocSpell(fight, fighter, nearestCell)) == null
                && _loc0_++ < limit)
        {
            nearestCell = PathFinding.getCaseBetweenEnemy(fighter.getCell().getId(), fight.getMap(), fight);
        }
        if (nearestCell == -1)
            return false;
        if (spell == null)
            return false;
        int invoc = fight.tryCastSpell(fighter, spell, nearestCell);
        if (invoc != 0)
            return false;
        return true;
    }

    public boolean invocIfPossibleloin(Fight fight, Fighter fighter, List<SortStats> Spelllist)
    {
        if (fight == null || fighter == null)
            return false;
        if (fighter.nbInvocation() >= fighter.getTotalStats().getEffect(Constant.STATS_CREATURE))
            return false;
        Fighter nearest = getNearestEnnemy(fight, fighter, false);
        if (nearest == null)
            return false;
        int nearestCell = fighter.getCell().getId();
        int limit = 10;
        int _loc0_ = 0;
        SortStats spell = null;
        while ((spell = getInvocSpellDopeul(fight, fighter, nearestCell, Spelllist)) == null
                && _loc0_++ < limit)
        {
            nearestCell = PathFinding.getNearestCellAround(fight.getMap(),
                    nearestCell, nearest.getCell().getId(), null);
        }
        if (nearestCell == -1 || spell == null)
            return false;
        return fight.tryCastSpell(fighter, spell, nearestCell) == 0;
    }

    public SortStats getInvocSpell(Fight fight, Fighter fighter, int nearestCell)
    {
        if (fight == null || fighter == null)
            return null;
        if (fighter.getMob() == null)
            return null;
        if (fight.getMap() == null)
            return null;
        if (fight.getMap().getCase(nearestCell) == null)
            return null;
        for (Map.Entry<Integer, SortStats> SS : fighter.getMob().getSpells().entrySet()) {
            if (!fight.canCastSpell1(fighter, SS.getValue(), fight.getMap().getCase(nearestCell), -1))
                continue;
            for (SpellEffect SE : SS.getValue().getEffects())
                if (SE.getEffectID() == 181)
                    return SS.getValue();
        }
        return null;
    }

    public SortStats getInvocSpellDopeul(Fight fight, Fighter fighter, int nearestCell, List<SortStats> Spelllist)
    {
        if (fight == null || fighter == null)
            return null;
        if (fighter.getMob() == null)
            return null;
        if (fight.getMap() == null)
            return null;
        if (fight.getMap().getCase(nearestCell) == null)
            return null;
        for (SortStats SS : Spelllist)
        {
            if (!fight.canCastSpell1(fighter, SS, fight.getMap().getCase(nearestCell), -1))
                continue;
            for (SpellEffect SE : SS.getEffects())
            {
                if (SE.getEffectID() == 181)
                    return SS;
            }
        }
        return null;
    }

    public int HealIfPossible(Fight fight, Fighter f, boolean autoSoin, int PDVPERmin)//boolean pour choisir entre auto-soin ou soin alli�
    {
        if (fight == null || f == null)
            return 10;
        if (f.isDead())
            return 10;
        if (autoSoin && (f.getPdv() * 100) / f.getPdvMax() > 95)
            return 10;
        Fighter target = null;
        SortStats SS = null;
        if (autoSoin)
        {
            int PDVPER = (f.getPdv() * 100) / f.getPdvMax();
            if (PDVPER < PDVPERmin && PDVPER < 95)
            {
                target = f;
                SS = getHealSpell(fight, f, target);
            }
        }
        else
        //s�lection joueur ayant le moins de pv
        {
            Fighter curF = null;
            //int PDVPERmin = 100;
            SortStats curSS = null;
            for (Fighter F : fight.getFighters(3))
            {
                if (f.isDead())
                    continue;
                if (F == f)
                    continue;
                if (F.isDead())
                    continue;
                if (F.getTeam() == f.getTeam())
                {
                    int PDVPER = (F.getPdv() * 100) / F.getPdvMax();
                    if (PDVPER < PDVPERmin && PDVPER < 95)
                    {
                        int infl = 0;
                        if (f.isCollector())
                        {
                            for (Map.Entry<Integer, SortStats> ss : World.world.getGuild(f.getCollector().getGuildId()).getSpells().entrySet())
                            {
                                if (ss.getValue() == null)
                                    continue;
                                if (infl < calculInfluenceHeal(ss.getValue())
                                        && calculInfluenceHeal(ss.getValue()) != 0
                                        && fight.canCastSpell1(f, ss.getValue(), F.getCell(), -1))//Si le sort est plus interessant
                                {
                                    infl = calculInfluenceHeal(ss.getValue());
                                    curSS = ss.getValue();
                                }
                            }
                        }
                        else
                        {
                            for (Map.Entry<Integer, SortStats> ss : f.getMob().getSpells().entrySet())
                            {
                                if (infl < calculInfluenceHeal(ss.getValue())
                                        && calculInfluenceHeal(ss.getValue()) != 0
                                        && fight.canCastSpell1(f, ss.getValue(), F.getCell(), -1))//Si le sort est plus interessant
                                {
                                    infl = calculInfluenceHeal(ss.getValue());
                                    curSS = ss.getValue();
                                }
                            }
                        }
                        if (curSS != SS && curSS != null)
                        {
                            curF = F;
                            SS = curSS;
                            PDVPERmin = PDVPER;
                        }
                    }
                }
            }
            target = curF;
        }
        if (target == null)
            return 10;
        if (target.isFullPdv())
            return 10;
        if (SS == null)
            return 10;
        int heal = fight.tryCastSpell(f, SS, target.getCell().getId());
        if (heal != 0)
            return SS.getSpell().getDuration();

        return 0;
    }

    public boolean HealIfPossible(Fight fight, Fighter f, boolean autoSoin)//boolean pour choisir entre auto-soin ou soin alli�
    {
        if (fight == null || f == null)
            return false;
        if (f.isDead())
            return false;
        if (autoSoin && (f.getPdv() * 100) / f.getPdvMax() > 95)
            return false;
        Fighter target = null;
        SortStats SS = null;
        if (autoSoin)
        {
            target = f;
            SS = getHealSpell(fight, f, target);
        }
        else
        //s�lection joueur ayant le moins de pv
        {
            Fighter curF = null;
            int PDVPERmin = 100;
            SortStats curSS = null;
            for (Fighter F : fight.getFighters(3))
            {
                if (f.isDead())
                    continue;
                if (F == f)
                    continue;
                if (F.isDead())
                    continue;
                if (F.getTeam() == f.getTeam())
                {
                    int PDVPER = (F.getPdv() * 100) / F.getPdvMax();
                    if (PDVPER < PDVPERmin && PDVPER < 95)
                    {
                        int infl = 0;
                        if (f.isCollector())
                        {
                            for (Map.Entry<Integer, SortStats> ss : World.world.getGuild(f.getCollector().getGuildId()).getSpells().entrySet())
                            {
                                if (ss.getValue() == null)
                                    continue;
                                if (infl < calculInfluenceHeal(ss.getValue())
                                        && calculInfluenceHeal(ss.getValue()) != 0
                                        && fight.canCastSpell1(f, ss.getValue(), F.getCell(), -1))//Si le sort est plus interessant
                                {
                                    infl = calculInfluenceHeal(ss.getValue());
                                    curSS = ss.getValue();
                                }
                            }
                        }
                        else
                        {
                            for (Map.Entry<Integer, SortStats> ss : f.getMob().getSpells().entrySet())
                            {
                                if (infl < calculInfluenceHeal(ss.getValue())
                                        && calculInfluenceHeal(ss.getValue()) != 0
                                        && fight.canCastSpell1(f, ss.getValue(), F.getCell(), -1))//Si le sort est plus interessant
                                {
                                    infl = calculInfluenceHeal(ss.getValue());
                                    curSS = ss.getValue();
                                }
                            }
                        }
                        if (curSS != SS && curSS != null)
                        {
                            curF = F;
                            SS = curSS;
                            PDVPERmin = PDVPER;
                        }
                    }
                }
            }
            target = curF;
        }
        if (target == null)
            return false;
        if (target.isFullPdv())
            return false;
        if (SS == null)
            return false;
        int heal = fight.tryCastSpell(f, SS, target.getCell().getId());
        if (heal != 0)
            return false;

        return true;
    }

    public boolean buffIfPossible(Fight fight, Fighter fighter, Fighter target)
    {
        if (fight == null || fighter == null)
            return false;
        if (target == null)
            return false;
        SortStats SS = getBuffSpell(fight, fighter, target);
        if (SS == null)
            return false;
        int buff = fight.tryCastSpell(fighter, SS, target.getCell().getId());
        if (buff != 0)
            return false;
        return true;
    }

    public SortStats getBuffSpell(Fight fight, Fighter F, Fighter T)
    {
        if (fight == null || F == null)
            return null;
        int infl = -1500000;
        SortStats ss = null;
        if (F.isCollector())
        {
            for (Map.Entry<Integer, SortStats> SS : World.world.getGuild(F.getCollector().getGuildId()).getSpells().entrySet())
            {
                if (SS.getValue() == null)
                    continue;
                if (infl < calculInfluence(SS.getValue(), F, T)
                        && calculInfluence(SS.getValue(), F, T) > 0
                        && fight.canCastSpell1(F, SS.getValue(), T.getCell(), -1))//Si le sort est plus interessant
                {
                    infl = calculInfluence(SS.getValue(), F, T);
                    ss = SS.getValue();
                }
            }
        }
        else
        {
            for (Map.Entry<Integer, SortStats> SS : F.getMob().getSpells().entrySet())
            {
                int inf = calculInfluence(SS.getValue(), F, T);
                if (infl < inf
                        && SS.getValue().getSpell().getType() == 1
                        && fight.canCastSpell1(F, SS.getValue(), T.getCell(), -1))//Si le sort est plus interessant
                {
                    infl = calculInfluence(SS.getValue(), F, T);
                    ss = SS.getValue();
                }
            }
        }
        return ss;
    }

    public boolean buffIfPossible(Fight fight, Fighter fighter, Fighter target, List<SortStats> Spelllist)
    {
        if (fight == null || fighter == null)
            return false;
        if (target == null)
            return false;
        SortStats SS = getBuffSpellDopeul(fight, fighter, target, Spelllist);
        if (SS == null)
            return false;
        int buff = fight.tryCastSpell(fighter, SS, target.getCell().getId());
        if (buff != 0)
            return true;
        return false;
    }

    public SortStats getBuffSpellDopeul(Fight fight, Fighter F, Fighter T, List<SortStats> Spelllist)
    {
        if (fight == null || F == null)
            return null;
        int infl = -1500000;
        SortStats ss = null;
        for (SortStats SS : Spelllist)
        {
            int inf = calculInfluence(SS, F, T);

            if (infl < inf && SS.getSpell().getType() == 1 && fight.canCastSpell1(F, SS, T.getCell(), -1))//Si le sort est plus interessant
            {
                infl = calculInfluence(SS, F, T);
                ss = SS;
            }
        }
        return ss;
    }

    public SortStats getHealSpell(Fight fight, Fighter F, Fighter T)
    {
        if (fight == null || F == null)
            return null;
        int infl = 0;
        SortStats ss = null;
        if (F.isCollector())
        {
            for (Map.Entry<Integer, SortStats> SS : World.world.getGuild(F.getCollector().getGuildId()).getSpells().entrySet())
            {
                if (SS.getValue() == null)
                    continue;
                if (infl < calculInfluenceHeal(SS.getValue())
                        && calculInfluenceHeal(SS.getValue()) != 0
                        && fight.canCastSpell1(F, SS.getValue(), T.getCell(), -1))//Si le sort est plus interessant
                {
                    infl = calculInfluenceHeal(SS.getValue());
                    ss = SS.getValue();
                }
            }
        }
        else
        {
            for (Map.Entry<Integer, SortStats> SS : F.getMob().getSpells().entrySet())
            {
                if (SS.getValue() == null)
                    continue;
                if (infl < calculInfluenceHeal(SS.getValue())
                        && calculInfluenceHeal(SS.getValue()) != 0
                        && fight.canCastSpell1(F, SS.getValue(), T.getCell(), -1))//Si le sort est plus interessant
                {
                    infl = calculInfluenceHeal(SS.getValue());
                    ss = SS.getValue();
                }
            }
        }
        return ss;
    }

    public int moveautourIfPossible(Fight fight, Fighter F, Fighter T)
    {
        if (fight == null)
            return 0;
        if (F == null)
            return 0;
        if (T == null)
            return 0;
        if (F.getCurPm(fight) <= 0)
            return 0;
        GameMap map = fight.getMap();
        if (map == null)
            return 0;
        GameCase cell = F.getCell();
        if (cell == null)
            return 0;
        GameCase cell2 = T.getCell();
        if (cell2 == null)
            return 0;
        if (PathFinding.isNextTo(map, cell.getId(), cell2.getId()))
            return 0;
        int nbrcase = 0;

        int cellID = PathFinding.getNearestCellAroundGA(map, cell2.getId(), cell.getId(), null);
        //On demande le chemin plus court
        //Mais le chemin le plus court ne prend pas en compte les bords de map.
        if (cellID == -1)
        {
            Map<Integer, Fighter> ennemys = getLowHpEnnemyList(fight, F);
            for (Map.Entry<Integer, Fighter> target : ennemys.entrySet())
            {
                int cellID2 = PathFinding.getNearestCellAroundGA(map, target.getValue().getCell().getId(), cell.getId(), null);
                if (cellID2 != -1)
                {
                    cellID = cellID2;
                    break;
                }
            }
        }
        ArrayList<GameCase> path = new AStarPathFinding(fight, cell.getId(), cellID).getShortestPath();
        if (path == null || path.isEmpty())
            return 0;

        ArrayList<GameCase> finalPath = new ArrayList<GameCase>();
        for (int a = 0; a < F.getCurPm(fight); a++)
        {
            if (path.size() == a)
                break;
            finalPath.add(path.get(a));
        }
        String pathstr = "";
        try
        {
            int curCaseID = cell.getId();
            int curDir = 0;
            for (GameCase c : finalPath)
            {
                char d = PathFinding.getDirBetweenTwoCase(curCaseID, c.getId(), map, true);
                if (d == 0)
                    return 0;//Ne devrait pas arriver :O
                if (curDir != d)
                {
                    if (finalPath.indexOf(c) != 0)
                        pathstr += World.world.getCryptManager().cellID_To_Code(curCaseID);
                    pathstr += d;
                }
                curCaseID = c.getId();

                nbrcase = nbrcase + 1;
            }
            if (curCaseID != cell.getId())
                pathstr += World.world.getCryptManager().cellID_To_Code(curCaseID);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //Cr�ation d'une GameAction
        GameAction GA = new GameAction(0, 1, "");
        GA.args = pathstr;
        if(!fight.onFighterDeplace(F, GA))
            return 0;

        return nbrcase * 500;
    }

    public int moveenfaceIfPossible(Fight fight, Fighter F, Fighter T, int dist)
    {
        if (fight == null)
            return 0;
        if (F == null)
            return 0;
        if (T == null)
            return 0;
        if (F.getCurPm(fight) <= 0)
            return 0;
        GameMap map = fight.getMap();
        if (map == null)
            return 0;
        GameCase cell = F.getCell();
        if (cell == null)
            return 0;
        GameCase cell2 = T.getCell();
        if (cell2 == null)
            return 0;
        if (PathFinding.isNextTo(map, cell.getId(), cell2.getId()))
            return 0;
        int nbrcase = 0;

        int cellID = PathFinding.getNearestligneGA(fight, cell2.getId(), cell.getId(), null, dist);
        //On demande le chemin plus court
        //Mais le chemin le plus court ne prend pas en compte les bords de map.
        if (cellID == -1)
        {
            Map<Integer, Fighter> ennemys = getLowHpEnnemyList(fight, F);
            for (Map.Entry<Integer, Fighter> target : ennemys.entrySet())
            {
                int cellID2 = PathFinding.getNearestligneGA(fight, target.getValue().getCell().getId(), cell.getId(), null, dist);
                if (cellID2 != -1)
                {
                    cellID = cellID2;
                    break;
                }
            }
        }
        ArrayList<GameCase> path = new AStarPathFinding(fight, cell.getId(), cellID).getShortestPath();//0pour en ligne
        if (path == null || path.isEmpty())
            return 0;

        ArrayList<GameCase> finalPath = new ArrayList<GameCase>();
        boolean ligneok = false;
        for (int a = 0; a < F.getCurPm(fight); a++)
        {
            if (path.size() == a)
                break;
            if(ligneok == true)
                break;
            if(PathFinding.casesAreInSameLine(fight.getMap(), path.get(a).getId(), T.getCell().getId(), 'z', 70))
                ligneok = true;
            finalPath.add(path.get(a));
        }
        String pathstr = "";
        try
        {
            int curCaseID = cell.getId();
            int curDir = 0;
            for (GameCase c : finalPath)
            {
                char d = PathFinding.getDirBetweenTwoCase(curCaseID, c.getId(), map, true);
                if (d == 0)
                    return 0;//Ne devrait pas arriver :O
                if (curDir != d)
                {
                    if (finalPath.indexOf(c) != 0)
                        pathstr += World.world.getCryptManager().cellID_To_Code(curCaseID);
                    pathstr += d;
                }
                curCaseID = c.getId();

                nbrcase = nbrcase + 1;
            }
            if (curCaseID != cell.getId())
                pathstr += World.world.getCryptManager().cellID_To_Code(curCaseID);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //Cr�ation d'une GameAction
        GameAction GA = new GameAction(0, 1, "");
        GA.args = pathstr;
        if(!fight.onFighterDeplace(F, GA))
            return 0;

        return nbrcase * 500;
    }

    public Fighter getNearestFriendNoInvok(Fight fight, Fighter fighter)
    {
        if (fight == null || fighter == null)
            return null;
        int dist = 1000;
        Fighter curF = null;
        for (Fighter f : fight.getFighters(3))
        {
            if (f.isDead() || (f.isInvocation() && !fighter.isInvocation()))
                continue;
            if (f == fighter)
                continue;
            if (f.getTeam2() == fighter.getTeam2() && !f.isInvocation())//Si c'est un ami et si c'est une invocation
            {
                int d = PathFinding.getDistanceBetween(fight.getMap(), fighter.getCell().getId(), f.getCell().getId());
                if (d < dist)
                {
                    dist = d;
                    curF = f;
                }
            }
        }
        return curF;
    }

    public Fighter getNearestFriend(Fight fight, Fighter fighter)
    {
        if (fight == null || fighter == null)
            return null;
        int dist = 1000;
        Fighter curF = null;
        for (Fighter f : fight.getFighters(3))
        {
            if (f.isDead() || (f.isInvocation() && !fighter.isInvocation()) || f.isHide())
                continue;
            if (f == fighter)
                continue;
            if (f.getTeam() == fighter.getTeam())//Si c'est un ami
            {
                int d = PathFinding.getDistanceBetween(fight.getMap(), fighter.getCell().getId(), f.getCell().getId());
                if (d < dist)
                {
                    dist = d;
                    curF = f;
                }
            }
        }
        return curF;
    }

    public Fighter getNearestEnnemy(Fight fight, Fighter fighter, boolean invocation)
    {
        if (fight == null || fighter == null)
            return null;
        int dist = 1000;
        Fighter curF = null;
        for (Fighter f : fight.getFighters(3))
        {
            if (f.isDead() || (!invocation && f.isInvocation()) || f.isHide())
                continue;
            if (f.getTeam2() != fighter.getTeam2())//Si c'est un ennemis
            {
                int d = PathFinding.getDistanceBetween(fight.getMap(), fighter.getCell().getId(), f.getCell().getId());
                if (d < dist)
                {
                    dist = d;
                    curF = f;
                }
            }
        }
        return curF;
    }

    public Map<Integer, Fighter> getLowHpEnnemyList(Fight fight, Fighter fighter)
    {
        if (fight == null || fighter == null)
            return null;
        Map<Integer, Fighter> list = new HashMap<Integer, Fighter>();
        Map<Integer, Fighter> ennemy = new HashMap<Integer, Fighter>();
        for (Fighter f : fight.getFighters(3))
        {
            if (f.isDead() || f.isHide())
                continue;
            if (f == fighter)
                continue;
            if (f.getTeam2() != fighter.getTeam2())
            {
                ennemy.put(f.getId(), f);
            }
        }
        int i = 0, i2 = ennemy.size();
        int curHP = 10000;
        Fighter curEnnemy = null;

        while (i < i2)
        {
            curHP = 200000;
            curEnnemy = null;
            for (Map.Entry<Integer, Fighter> t : ennemy.entrySet())
            {
                if (t.getValue().getPdv() < curHP)
                {
                    curHP = t.getValue().getPdv();
                    curEnnemy = t.getValue();
                }
            }
            list.put(curEnnemy.getId(), curEnnemy);
            ennemy.remove(curEnnemy.getId());
            i++;
        }
        return list;
    }

    public int attackIfPossible(Fight fight, Fighter fighter,List<SortStats> Spell)// 0 = Rien, 5 = EC, 666 = NULL, 10 = SpellNull ou ActionEnCour ou Can'tCastSpell, 0 = AttaqueOK
    {
        if (fight == null || fighter == null)
            return 0;
        Map<Integer, Fighter> ennemyList = getLowHpEnnemyList(fight, fighter);
        SortStats SS = null;
        Fighter target = null;
        for (Map.Entry<Integer, Fighter> t : ennemyList.entrySet())
        {
            SS = getBestSpellForTargetDopeul(fight, fighter, t.getValue(), fighter.getCell().getId(), Spell);

            if (SS != null)
            {
                target = t.getValue();
                break;
            }
        }
        int curTarget = 0, cell = 0;
        SortStats SS2 = null;

        for (SortStats S : Spell)
        {
            int targetVal = getBestTargetZone(fight, fighter, S, fighter.getCell().getId(), false);
            if (targetVal == -1 || targetVal == 0)
                continue;
            int nbTarget = targetVal / 1000;
            int cellID = targetVal - nbTarget * 1000;
            if (nbTarget > curTarget)
            {
                curTarget = nbTarget;
                cell = cellID;
                SS2 = S;
            }
        }
        if (curTarget > 0 && cell >= 15 && cell <= 463 && SS2 != null)
        {
            int attack = fight.tryCastSpell(fighter, SS2, cell);
            if (attack != 0)
                return SS2.getSpell().getDuration();
        }
        else
        {
            if (target == null || SS == null)
                return 0;
            int attack = fight.tryCastSpell(fighter, SS, target.getCell().getId());
            if (attack != 0)
                return SS.getSpell().getDuration();
        }
        return 0;
    }

    public int getInfluence(Fight fight, SortStats SS)
    {
        if (fight == null)
            return 0;
        int inf = 0;
        for (SpellEffect SE : SS.getEffects())
        {
            switch (SE.getEffectID())
            {
                case 950: // Etat
                    inf += 2500;
                    break;
                case 101: // Retrait PA
                    inf += 1000 * Formulas.getMaxJet(SE.getJet());
                    break;
                case 168: // Retrait PA Non esquivable
                    inf += 1500 * Formulas.getMaxJet(SE.getJet());
                    break;

                case 91: case 92: case 93: case 94: case 95: // Vol de vie
                case 96: case 97: case 98: case 99: case 100: // Dégâts
                    inf += 500 * Formulas.getMiddleJet(SE.getJet());
                    break;

                default:
                    inf += Formulas.getMiddleJet(SE.getJet());
                    break;
            }
        }
        return inf;
    }

    public SortStats getBestSpellForTargetDopeul(Fight fight, Fighter F, Fighter T, int launch, List<SortStats> listspell)
    {
        if (fight == null || F == null)
            return null;
        int inflMax = 0;
        SortStats ss = null;


        for (SortStats SS : listspell)
        {
            if (SS.getSpell().getType() != 0)
                continue;
            int curInfl = 0, Infl1 = 0, Infl2 = 0;
            int PA = F.getMob().getPa();
            int usedPA[] = {0, 0};
            if (!fight.canCastSpell1(F, SS, T.getCell(), launch))
                continue;
            curInfl = getInfluence(fight, SS);
            if(curInfl == 0)continue;
            if (curInfl > inflMax)
            {
                ss = SS;
                usedPA[0] = ss.getPACost();
                Infl1 = curInfl;
                inflMax = Infl1;
            }

            for (SortStats SS2 : listspell)
            {
                if (SS2.getSpell().getType() != 0)
                    continue;
                if ((PA - usedPA[0]) < SS2.getPACost())
                    continue;
                if (!fight.canCastSpell1(F, SS2, T.getCell(), launch))
                    continue;
                curInfl = getInfluence(fight, SS2);
                if(curInfl == 0)continue;
                if ((Infl1 + curInfl) > inflMax)
                {
                    ss = SS;
                    usedPA[1] = SS2.getPACost();
                    Infl2 = curInfl;
                    inflMax = Infl1 + Infl2;
                }
                for (SortStats SS3 : listspell)
                {
                    if (SS3.getSpell().getType() != 0)
                        continue;
                    if ((PA - usedPA[0] - usedPA[1]) < SS3.getPACost())
                        continue;
                    if (!fight.canCastSpell1(F, SS3, T.getCell(), launch))
                        continue;

                    curInfl = getInfluence(fight, SS3);
                    if(curInfl == 0)continue;
                    if ((curInfl + Infl1 + Infl2) > inflMax)
                    {
                        ss = SS;
                        inflMax = curInfl + Infl1 + Infl2;
                    }
                }
            }
        }
        return ss;
    }

    public int getBestTargetZone(Fight fight, Fighter fighter, SortStats spell, int launchCell, boolean line)
    {
        if (fight == null || fighter == null)
            return 0;
        if (spell.getPorteeType().isEmpty()
                || (spell.getPorteeType().charAt(0) == 'P' && spell.getPorteeType().charAt(1) == 'a')
                || spell.isLineLaunch() && !line)
        {
            return 0;
        }
        ArrayList<GameCase> possibleLaunch = new ArrayList<GameCase>();
        int CellF = -1;
        if (spell.getMaxPO() != 0)
        {
            char arg1 = 'C';
            char[] table = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v'};
            char arg2 = 'a';
            if (spell.getMaxPO() > 20)
            {
                arg2 = 'u';
            }
            else
            {
                arg2 = table[spell.getMaxPO()];
            }
            String args = Character.toString(arg1) + Character.toString(arg2);
            possibleLaunch = PathFinding.getCellListFromAreaString(fight.getMap(), launchCell, launchCell, args, 0, false);
        }
        else
        {
            possibleLaunch.add(fight.getMap().getCase(launchCell));
        }

        if (possibleLaunch == null)
        {
            return -1;
        }
        int nbTarget = 0;
        for (GameCase cell : possibleLaunch)
        {
            try
            {
                if (!fight.canCastSpell1(fighter, spell, cell, launchCell))
                    continue;
                int curTarget = 0;
                ArrayList<GameCase> cells = PathFinding.getCellListFromAreaString(fight.getMap(), cell.getId(), launchCell, spell.getPorteeType(), 0, false);
                for (GameCase c : cells)
                {
                    if (c == null)
                        continue;
                    if (c.getFirstFighter() == null)
                        continue;
                    if (c.getFirstFighter().getTeam2() != fighter.getTeam2())
                        curTarget++;
                }
                if (curTarget > nbTarget)
                {
                    if(cell.getFirstFighter() != null && cell.getFirstFighter().getTeam() == fighter.getTeam())
                        continue;
                    nbTarget = curTarget;
                    CellF = cell.getId();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if (nbTarget > 0 && CellF != -1)
            return CellF + nbTarget * 1000;
        else
            return 0;
    }

    public int calculInfluenceHeal(SortStats ss)
    {
        int inf = 0;
        for (SpellEffect SE : ss.getEffects())
        {
            if (SE.getEffectID() != 108)
                return 0;
            inf += 100 * Formulas.getMiddleJet(SE.getJet());
        }

        return inf;
    }

    public int calculInfluence(SortStats ss, Fighter C, Fighter T)
    {
        int infTot = 0;
        for (SpellEffect SE : ss.getEffects())
        {
            int inf = 0;
            switch (SE.getEffectID())
            {
                case 5:
                    inf = 500 * Formulas.getMiddleJet(SE.getJet());
                    break;
                case 89:
                    inf = 200 * Formulas.getMiddleJet(SE.getJet());
                    break;
                case 91:
                    inf = 150 * Formulas.getMiddleJet(SE.getJet());
                    break;
                case 92:
                    inf = 150 * Formulas.getMiddleJet(SE.getJet());
                    break;
                case 93:
                    inf = 150 * Formulas.getMiddleJet(SE.getJet());
                    break;
                case 94:
                    inf = 150 * Formulas.getMiddleJet(SE.getJet());
                    break;
                case 95:
                    inf = 150 * Formulas.getMiddleJet(SE.getJet());
                    break;
                case 96:
                    inf = 100 * Formulas.getMiddleJet(SE.getJet());
                    break;
                case 97:
                    inf = 100 * Formulas.getMiddleJet(SE.getJet());
                    break;
                case 98:
                    inf = 100 * Formulas.getMiddleJet(SE.getJet());
                    break;
                case 99:
                    inf = 100 * Formulas.getMiddleJet(SE.getJet());
                    break;
                case 100:
                    inf = 100 * Formulas.getMiddleJet(SE.getJet());
                    break;
                case 101:
                    inf = 1000 * Formulas.getMiddleJet(SE.getJet());
                    break;
                case 127:
                    inf = 1000 * Formulas.getMiddleJet(SE.getJet());
                    break;
                case 84:
                    inf = 1500 * Formulas.getMiddleJet(SE.getJet());
                    break;
                case 77:
                    inf = 1500 * Formulas.getMiddleJet(SE.getJet());
                    break;
                case 111:
                    inf = -1000 * Formulas.getMiddleJet(SE.getJet());
                    break;
                case 128:
                    inf = -1000 * Formulas.getMiddleJet(SE.getJet());
                    break;
                case 121:
                    inf = -100 * Formulas.getMiddleJet(SE.getJet());
                    break;
                case 131:
                    inf = 300 * Formulas.getMiddleJet(SE.getJet());
                    break;
                case 132:
                    inf = 2000;
                    break;
                case 138:
                    inf = -50 * Formulas.getMiddleJet(SE.getJet());
                    break;
                case 150:
                    inf = -2000;
                    break;
                case 168:
                    inf = 1000 * Formulas.getMiddleJet(SE.getJet());
                    break;
                case 169:
                    inf = 1000 * Formulas.getMiddleJet(SE.getJet());
                    break;
                case 210:
                    inf = -300 * Formulas.getMiddleJet(SE.getJet());
                    break;
                case 211:
                    inf = -300 * Formulas.getMiddleJet(SE.getJet());
                    break;
                case 212:
                    inf = -300 * Formulas.getMiddleJet(SE.getJet());
                    break;
                case 213:
                    inf = -300 * Formulas.getMiddleJet(SE.getJet());
                    break;
                case 214:
                    inf = -300 * Formulas.getMiddleJet(SE.getJet());
                    break;
                case 215:
                    inf = 300 * Formulas.getMiddleJet(SE.getJet());
                    break;
                case 216:
                    inf = 300 * Formulas.getMiddleJet(SE.getJet());
                    break;
                case 217:
                    inf = 300 * Formulas.getMiddleJet(SE.getJet());
                    break;
                case 218:
                    inf = 300 * Formulas.getMiddleJet(SE.getJet());
                    break;
                case 219:
                    inf = 300 * Formulas.getMiddleJet(SE.getJet());
                    break;
                case 265:
                    inf = -250 * Formulas.getMiddleJet(SE.getJet());
                case 765://Sacrifice
                    inf = -1000;
                    break;
                case 786://Arbre de vie
                    inf = -1000;
                    break;
                case 106: // Renvoie de sort
                    inf = -900;
                    break;
            }

            if (C.getTeam() == T.getTeam())
                infTot -= inf;
            else
                infTot += inf;
        }
        return infTot;
    }




    /**
     * NEW IA FUNCTION RESTORED
     */

    public boolean moveToAttack(Fight fight, Fighter caster, Fighter target, SortStats spell) {
        return target != null && moveToAttack(fight, caster, target.getCell(), spell, true);
    }
    /**
     * Move if needed to cast his spell
     * If can launch his spell, he don't move
     */
    public boolean moveToAttack(Fight fight, Fighter caster, GameCase cellTarget, SortStats spell, boolean doneMove) {
        if (fight == null || caster == null || caster.getCurPm(fight) <= 0)
            return false;

        GameMap map = fight.getMap();
        GameCase cell = caster.getCell();

        if (map == null || cell == null || cellTarget == null)
            return false;
        if (fight.canCastSpell1(caster, spell, cell, cellTarget.getId()) || (spell != null && !fight.canLaunchSpell(caster, spell, cellTarget)) || PathFinding.isNextTo(map, cell.getId(), cellTarget.getId()))
            return false;
        int dist = PathFinding.getDistanceBetweenTwoCase(fight.getMap(), cell, cellTarget);
        if(spell != null && dist < spell.getMinPO() && spell.getMinPO() > 1) {
            byte count = 0;
            GameCase temp = cell;
            do {
                List<GameCase> cells = this.getCellsAvailableAround(fight, temp, true, (byte) 0);
                if(!cells.isEmpty()) {
                    for(GameCase c : cells) {
                        int tmpDist = PathFinding.getDistanceBetweenTwoCase(fight.getMap(), c, cellTarget);
                        if(tmpDist > dist && fight.canCastSpell1(caster, spell, c, cellTarget.getId())) {
                            temp = c;
                            dist = tmpDist;
                            break;
                        }
                    }
                }
                count++;
            } while(dist < spell.getMinPO() && count < 30);
            if(temp.getId() != cell.getId() && fight.canCastSpell1(caster, spell, temp, cellTarget.getId()))
                return moveToCell(fight, caster, cell, temp, doneMove);
        }

        final ArrayList<GameCase> path, finalPath = new ArrayList<>();

        if(spell != null && spell.isLineLaunch() && PathFinding.casesAreInSameLine(fight.getMap(), cell.getId(), cellTarget.getId(), 'z', spell.getMaxPO())) {
            int id = this.getCellToBeInTheSameLine(fight, spell, cell.getId(), cellTarget.getId());
            if(id != -1)
                cellTarget = fight.getMap().getCase(id);
        }


        path = new AStarPathFinding(fight, cell.getId(), cellTarget.getId()).getShortestPath();

        if (path == null || path.isEmpty())
            return false;

        GameCase stop = null, next;


        int countPm = 0;
        GameCase tmp = null;
        for(GameCase c : path) {
            countPm ++;
            if(countPm <= caster.getCurPm(fight)) {
                if(fight.canCastSpell1(caster, spell, c, cellTarget.getId())) {
                    stop = c;
                    break;
                }
            } else {
                stop = tmp;
                break;
            }
            tmp = c;
        }

        if(!doneMove && stop != null) return true;
        else if (!doneMove) return false;

        for (int a = 0; a < caster.getCurPm(fight); a++) {
            if (path.size() == a) break;
            next = path.get(a);
            finalPath.add(next);
            if(stop != null && next.getId() == stop.getId())
                break;
        }

        StringBuilder str = new StringBuilder();
        try {
            int curCell = cell.getId();

            for (GameCase c : finalPath) {
                char dir = PathFinding.getDirBetweenTwoCase(curCell, c.getId(), map, true);
                if (dir == 0) return false; //Ne devrait pas arriver :O

                if (finalPath.indexOf(c) != 0)
                    str.append(World.world.getCryptManager().cellID_To_Code(curCell));
                str.append(dir);
                curCell = c.getId();
            }
            if (curCell != cell.getId())
                str.append(World.world.getCryptManager().cellID_To_Code(curCell));
        } catch (Exception e) {
            e.printStackTrace();
        }

        GameAction GA = new GameAction(0, 1, "");
        GA.args = str.toString();
        return fight.onFighterDeplace(caster, GA);
    }

    private boolean moveToCell(Fight fight, Fighter fighter, GameCase cell, GameCase cellTarget, boolean doneMove) {
        ArrayList<GameCase> path = new AStarPathFinding(fight, cell.getId(), cellTarget.getId()).getShortestPath();

        if (path == null || path.isEmpty())
            return false;

        StringBuilder str = new StringBuilder();
        try {
            int curCell = cell.getId();

            for (GameCase c : path) {
                char dir = PathFinding.getDirBetweenTwoCase(curCell, c.getId(), fight.getMap(), true);
                if (dir == 0) return false; //Ne devrait pas arriver :O

                if (path.indexOf(c) != 0)
                    str.append(World.world.getCryptManager().cellID_To_Code(curCell));
                str.append(dir);
                curCell = c.getId();
            }
            if (curCell != cell.getId())
                str.append(World.world.getCryptManager().cellID_To_Code(curCell));
        } catch (Exception e) {
            e.printStackTrace();
        }

        AtomicReference<String> pathRef = new AtomicReference<>(str.toString());
        int nStep = PathFinding.isValidPath(fight.getMap(), fighter.getCell().getId(), pathRef, fight, null, -1);
        str = new StringBuilder(pathRef.get());

        if (nStep == 0 || nStep > fight.getCurFighterPm() || nStep == -1000)
            return false;
        else if(!doneMove)
            return true;

        GameAction GA = new GameAction(0, 1, "");
        GA.args = str.toString();
        return fight.onFighterDeplace(fighter, GA);
    }

    public int getCellToBeInTheSameLine(Fight fight, Spell.SortStats spell, int cellStart, int cellEnd) {
        final Map<Short, List<Integer>> map = new HashMap<>();

        for (char c : new char[]{'b', 'd', 'f', 'h'}) {
            final List<Integer> cells = new ArrayList<>();
            int id = PathFinding.getCaseIDFromDirrection(cellStart, c, fight.getMap());
            for (int i = 0; i < spell.getMaxPO(); i++) {
                GameCase cell = fight.getMap().getCase(id);

                if (cell.getFirstFighter() == null && cell.isWalkable(true, true, -1) && id != cellEnd) {
                    if (PathFinding.casesAreInSameLine(fight.getMap(), id, cellEnd, 'z', spell.getMaxPO())) {
                        if (spell.hasLDV() && !Formulas.checkLos(fight.getMap(), (short) cell.getId(), (short) cellEnd))
                            break;

                        cells.add(id);
                        map.put((short) cells.size(), cells);
                        break;
                    } else {
                        cells.add(id);
                        id = PathFinding.getCaseIDFromDirrection(cell.getId(), c, fight.getMap());
                    }
                }
            }
        }
        int size = 99, chooseCell = -1;
        for (Map.Entry<Short, List<Integer>> entry : map.entrySet()) {
            if (entry.getKey() < size) {
                size = entry.getKey();
                chooseCell = entry.getValue().get(entry.getValue().size() - 1);
            }
        }
        return chooseCell;
    }

    public SortStats getBestBuffSpell(Fight fight, Fighter caster, Fighter target) {
        if (fight == null || caster == null)
            return null;
        int influence = -1500000;
        SortStats ss = null;
        Collection<SortStats> spells = (caster.isCollector() ? World.world.getGuild(caster.getCollector().getGuildId()).getSpells().values() : caster.getMob().getSpells().values());

        for (SortStats tmp : spells) {
            int i = calculInfluence(tmp, caster, target);
            if (influence < i && tmp.getSpell().getType() == 1) {
                influence = i;
                ss = tmp;
            }
        }
        return ss;
    }

    public SortStats getBestHealSpell(Fight fight, Fighter caster, Fighter target) {
        if (fight == null || caster == null)
            return null;
        int influence = 0;
        SortStats ss = null;

        Collection<SortStats> spells = (caster.isCollector() ? World.world.getGuild(caster.getCollector().getGuildId()).getSpells().values() : caster.getMob().getSpells().values());
        for (SortStats tmp : spells) {
            if (influence < calculInfluenceHeal(tmp) && calculInfluenceHeal(tmp) != 0) {
                influence = calculInfluenceHeal(tmp);
                ss = tmp;
            }
        }
        return ss;
    }

    public SortStats getSpellByPo(Fighter caster, int po) {
        SortStats spell = null;
        int maxPo = 0;
        for (SortStats tmp : caster.getMob().getSpells().values()) {
            if (tmp != null && tmp.getMaxPO() > maxPo && tmp.getMaxPO() <= po) {
                spell = tmp;
                maxPo = tmp.getMaxPO();
            }
        }
        return spell;
    }

    public Fighter getEnnemyWithDistance(Fight fight, Fighter fighter, int min, int max, List<Fighter> fighters) {
        if (fight == null || fighter == null)
            return null;
        Fighter target = null;
        byte i = 0;
        int near = 150000;

        while((i == 0 || i == 1) && target == null) { // If we don't found fighter, try to find an invocation
            for (Fighter f : fight.getFighters(3)) {
                if(i == 0 && ((f.isInvocation() && !fighter.isInvocation()) ||  f.isStatic()))
                    continue;
                // If we want another fighter (limited by the spell)
                if (f.isDead() || (fighters != null && fighters.contains(f)) || f.isHide())
                    continue;
                if (f.getTeam2() != fighter.getTeam2()) { // If it's an ennemy
                    int distance = PathFinding.getDistanceBetween(fight.getMap(), fighter.getCell().getId(), f.getCell().getId());
                    if (distance <= max && distance >= min && distance < near) {
                        max = distance;
                        target = f;
                    }
                }
            }
            i++;
        }
        return target;
    }

    public int tryCastSpell(Fight fight, Fighter fighter, Fighter target, int spellId) {
        if(target == null)
            return 10;
        return tryCastSpell(fight, fighter, target.getCell(), spellId);
    }

    public int tryCastSpell(Fight fight, Fighter fighter, GameCase target, int spellId) {
        if (fight == null || fighter == null || target == null)
            return 10;
        Spell.SortStats spell = Function.getInstance().findSpell(fighter, spellId);
        return fight.tryCastSpell(fighter, spell, target.getId());
    }

    /**
     * ===================================================================================
     *
     * ===================================================================================
     *
     */

    //region Abstract Eazy IA
    public boolean tryCastSpell(AbstractEasyIA ia, Fighter target, SortStats spell) {
        return ia != null && target != null && ia.getFight().tryCastSpell(ia.getFighter(), spell, target.getCell().getId()) == 0;
    }
    //endregion

    //region Trap/Glyph finder

    List<GameCase> getCellsAround(Fight fight, GameCase launch) {
        List<GameCase> cells = new ArrayList<>(), available = new ArrayList<>();
        char[] dirs = {'b', 'd', 'f', 'h'};
        if(fight == null) return cells;

        for (char dir : dirs) {
            GameCase cell = fight.getMap().getCase(PathFinding.GetCaseIDFromDirrection(launch.getId(), dir, fight.getMap(), true));
            if (cell != null && cell.isWalkable(true, true, -1))
                available.add(cell);
        }
        return available;
    }

    /**
     * Get cell around the target once time or more for trap (size <= 3)
     * @param fight Fight
     * @param once true if you just want the cell around the target, false if you want more than this
     * @param iteration
     * @return list of cells available
     */
    List<GameCase> getCellsAvailableAround(Fight fight, GameCase launch, boolean once, byte iteration) {
        List<GameCase> cells = new ArrayList<>(), available = new ArrayList<>();
        char[] dirs = {'b', 'd', 'f', 'h'};
        if(fight == null || launch == null) return cells;

        final GameMap map = fight.getMap();
        if(map == null) return available;

        for (char dir : dirs) {
            GameCase cell = map.getCase(PathFinding.GetCaseIDFromDirrection(launch.getId(), dir, map, true));
            if (once || (cell != null && cellAvailable(fight, cell))) available.add(cell);
            if(!once) cells.add(cell);
        }
        if(!once) {
            for (GameCase c : cells) {
                if(c == null) continue;
                for (char dir : dirs) {
                    GameCase cell = map.getCase(PathFinding.GetCaseIDFromDirrection(c.getId(), dir, map, true));
                    if (cell != null && cellAvailable(fight, cell))
                        available.add(cell);
                }
            }
        }
        return available;
    }

    public List<GameCase> getCellsAvailableAround(Fighter target, boolean once, byte iteration) {
        return this.getCellsAvailableAround(target.getFight(), target.getCell(), once, iteration);
    }

    private boolean cellAvailable(Fight fight, GameCase cell) {
        return cell.getFirstFighter() == null && cell.isWalkable(true, true, cell.getId()) && fight.getAllTraps().stream().filter((t) -> t.getCell().getId() == cell.getId()).count() == 0;
    }
    //endregion
}
