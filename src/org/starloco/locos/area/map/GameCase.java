package org.starloco.locos.area.map;

import org.starloco.locos.area.map.entity.House;
import org.starloco.locos.area.map.entity.InteractiveObject;
import org.starloco.locos.area.map.entity.MountPark;
import org.starloco.locos.area.map.entity.Trunk;
import org.starloco.locos.client.Player;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.game.TrunkData;
import org.starloco.locos.database.data.login.BaseTrunkData;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.game.action.ExchangeAction;
import org.starloco.locos.game.action.GameAction;
import org.starloco.locos.game.world.World;
import org.starloco.locos.job.JobConstant;
import org.starloco.locos.job.maging.BreakingObject;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.other.Action;
import org.starloco.locos.util.TimerWaiter;

import java.util.ArrayList;
import java.util.List;

public class GameCase {

    private int id;
    private boolean walkable = true, loS = true;

    private List<Player> players;
    private ArrayList<Fighter> fighters;
    private ArrayList<Action> onCellStop;
    private InteractiveObject object;
    private GameObject droppedItem;

    public GameCase(GameMap map, int id, boolean walkable, boolean loS, int objId) {
        this.id = id;
        this.walkable = walkable;
        this.loS = loS;
        if (objId != -1)
            this.object = new InteractiveObject(objId, map, this);
    }

    public int getId() {
        return id;
    }

    public boolean isWalkable(boolean useObject) {
        if (this.object != null && useObject)
            return this.walkable && this.object.isWalkable();
        return this.walkable;
    }

    public boolean isWalkable(boolean useObject, boolean inFight, int targetCell) {
        if(this.object != null && useObject) {
            if((inFight || this.getId() != targetCell) && this.object.getTemplate() != null) {
                switch(this.object.getTemplate().getId()) {
                    case 7515:
                    case 7511:
                    case 7517:
                    case 7512:
                    case 7513:
                    case 7516:
                    case 7550:
                    case 7518:
                    case 7534:
                    case 7535:
                    case 7533:
                    case 7551:
                    case 7514:
                        return this.walkable;
                    case 6763:
                    case 6766:
                    case 6767:
                    case 6772:
                        return false;
                }
            }
            return this.walkable && this.object.isWalkable();
        }
        return this.walkable;
    }

    public boolean isLoS() {
        return this.loS;
    }

    public boolean blockLoS() {
        if (this.fighters == null)
            return this.loS;
        boolean hide = true;
        for (Fighter fighter : this.fighters)
            if (!fighter.isHide())
                hide = false;
        return this.loS && hide;
    }

    public void addPlayer(Player player) {
        if (this.players == null)
            this.players = new ArrayList<>();
        if(!this.players.contains(player))
            this.players.add(player);
    }

    public synchronized void removePlayer(Player player) {
        if (this.players != null) {
            if(this.players.contains(player))
                this.players.remove(player);
            if (this.players.isEmpty()) this.players = null;
        }
    }

    public List<Player> getPlayers() {
        if (this.players == null)
            return new ArrayList<>();
        return players;
    }

    public void addFighter(Fighter fighter) {
        if (this.fighters == null)
            this.fighters = new ArrayList<>();
        if(!this.fighters.contains(fighter))
            this.fighters.add(fighter);
    }

    public void removeFighter(Fighter fighter) {
        if (this.fighters != null) {
            if(this.fighters.contains(fighter))
                this.fighters.remove(fighter);
            if (this.fighters.isEmpty()) this.fighters = null;
        }
    }

    public ArrayList<Fighter> getFighters() {
        if (this.fighters == null)
            return new ArrayList<>();
        return fighters;
    }

    public Fighter getFirstFighter() {
        if(this.fighters != null && this.fighters.size() > 0)
            return fighters.get(0); // return this.fighters.get(0);o
        return null;
    }

    public void addOnCellStopAction(int id, String args, String cond, GameMap map) {
        if (this.onCellStop == null)
            this.onCellStop = new ArrayList<>();
        this.onCellStop.add(new Action(id, args, cond, map));
    }

    public void applyOnCellStopActions(Player player) {
        if (this.onCellStop != null)
            for (Action action : this.onCellStop)
                action.apply(player, null, -1, -1);
    }

    public boolean getOnCellStopAction() {
        return this.onCellStop != null;
    }

    public ArrayList<Action> getOnCellStop() {
        return onCellStop;
    }

    public void setOnCellStop(ArrayList<Action> onCellStop) {
        this.onCellStop = onCellStop;
    }
    public void clearOnCellAction() {
        this.onCellStop = null;
    }

    public InteractiveObject getObject() {
        return this.object;
    }

    public void addDroppedItem(GameObject obj) {
        this.droppedItem = obj;
    }

    public GameObject getDroppedItem(boolean delete) {
        GameObject obj = this.droppedItem;
        if (delete)
            this.droppedItem = null;
        return obj;
    }

    public void clearDroppedItem() {
        this.droppedItem = null;
    }

    public boolean canDoAction(int id) {
        if (this.object == null)
            return false;
        switch (id) {
            //Atelier des F�es
            case 151:
                return this.object.getId() == 7028;

            //Fontaine jouvence
            case 111:
            case 62:
                return this.object.getId() == 7004;
            //Moudre et egrenner - Paysan
            case 122:
            case 47:
                return this.object.getId() == 7007;
            //Faucher Bl�
            case 45:
                switch (this.object.getId()) {
                    case 7511://Bl�
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Faucher Orge
            case 53:
                switch (this.object.getId()) {
                    case 7515://Orge
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;

            //Faucher Avoine
            case 57:
                switch (this.object.getId()) {
                    case 7517://Avoine
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Faucher Houblon
            case 46:
                switch (this.object.getId()) {
                    case 7512://Houblon
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Faucher Lin
            case 50:
            case 68:
                switch (this.object.getId()) {
                    case 7513://Lin
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Faucher Riz
            case 159:
                switch (this.object.getId()) {
                    case 7550://Riz
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Faucher Seigle
            case 52:
                switch (this.object.getId()) {
                    case 7516://Seigle
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Faucher Malt
            case 58:
                switch (this.object.getId()) {
                    case 7518://Malt
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Faucher Chanvre - Cueillir Chanvre
            case 69:
            case 54:
                switch (this.object.getId()) {
                    case 7514://Chanvre
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Scier - Bucheron
            case 101:
                return this.object.getId() == 7003;
            //Couper Fr�ne
            case 6:
                switch (this.object.getId()) {
                    case 7500://Fr�ne
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Couper Ch�taignier
            case 39:
                switch (this.object.getId()) {
                    case 7501://Ch�taignier
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Couper Noyer
            case 40:
                switch (this.object.getId()) {
                    case 7502://Noyer
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Couper Ch�ne
            case 10:
                switch (this.object.getId()) {
                    case 7503://Ch�ne
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Couper Oliviolet
            case 141:
                switch (this.object.getId()) {
                    case 7542://Oliviolet
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Couper Bombu
            case 139:
                switch (this.object.getId()) {
                    case 7541://Bombu
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Couper Erable
            case 37:
                switch (this.object.getId()) {
                    case 7504://Erable
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Couper Bambou
            case 154:
                switch (this.object.getId()) {
                    case 7553://Bambou
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Couper If
            case 33:
                switch (this.object.getId()) {
                    case 7505://If
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Couper Merisier
            case 41:
                switch (this.object.getId()) {
                    case 7506://Merisier
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Couper Eb�ne
            case 34:
                switch (this.object.getId()) {
                    case 7507://Eb�ne
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Couper Kalyptus
            case 174:
                switch (this.object.getId()) {
                    case 7557://Kalyptus
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Couper Charme
            case 38:
                switch (this.object.getId()) {
                    case 7508://Charme
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Couper Orme
            case 35:
                switch (this.object.getId()) {
                    case 7509://Orme
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Couper Bambou Sombre
            case 155:
                switch (this.object.getId()) {
                    case 7554://Bambou Sombre
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Couper Bambou Sacr�
            case 158:
                switch (this.object.getId()) {
                    case 7552://Bambou Sacr�
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Puiser
            case 102:
                switch (this.object.getId()) {
                    case 7519://Puits
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Polir
            case 48:
                return this.object.getId() == 7005;
            //Tas de patate
            case 42:
                return this.object.getId() == 7510;
            //Moule/Fondre - Mineur
            case 32:
                return this.object.getId() == 7002;
            case 22:
                return this.object.getId() == 7006;
            //Miner Fer
            case 24:
                switch (this.object.getId()) {
                    case 7520://Miner
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Miner Cuivre
            case 25:
                switch (this.object.getId()) {
                    case 7522://Miner
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Miner Bronze
            case 26:
                switch (this.object.getId()) {
                    case 7523://Miner
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Miner Kobalte
            case 28:
                switch (this.object.getId()) {
                    case 7525://Miner
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Miner Manga
            case 56:
                switch (this.object.getId()) {
                    case 7524://Miner
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Miner Sili
            case 162:
                switch (this.object.getId()) {
                    case 7556://Miner
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Miner Etain
            case 55:
                switch (this.object.getId()) {
                    case 7521://Miner
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Miner Argent
            case 29:
                switch (this.object.getId()) {
                    case 7526://Miner
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Miner Bauxite
            case 31:
                switch (this.object.getId()) {
                    case 7528://Miner
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Miner Or
            case 30:
                switch (this.object.getId()) {
                    case 7527://Miner
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Miner Dolomite
            case 161:
                switch (this.object.getId()) {
                    case 7555://Miner
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Fabriquer potion - Alchimiste
            case 23:
                return this.object.getId() == 7019;
            //Cueillir Tr�fle
            case 71:
                switch (this.object.getId()) {
                    case 7533://Tr�fle
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Cueillir Menthe
            case 72:
                switch (this.object.getId()) {
                    case 7534://Menthe
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Cueillir Orchid�e
            case 73:
                switch (this.object.getId()) {
                    case 7535:// Orchid�e
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Cueillir Edelweiss
            case 74:
                switch (this.object.getId()) {
                    case 7536://Edelweiss
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Cueillir Graine de Pandouille
            case 160:
                switch (this.object.getId()) {
                    case 7551://Graine de Pandouille
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Vider - P�cheur
            case 133:
                return this.object.getId() == 7024;
            //P�cher Petits poissons de mer
            case 128:
                switch (this.object.getId()) {
                    case 7530://Petits poissons de mer
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //P�cher Petits poissons de rivi�re
            case 124:
                switch (this.object.getId()) {
                    case 7529://Petits poissons de rivi�re
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //P�cher Pichon
            case 136:
                switch (this.object.getId()) {
                    case 7544://Pichon
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //P�cher Ombre Etrange
            case 140:
                switch (this.object.getId()) {
                    case 7543://Ombre Etrange
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //P�cher Poissons de rivi�re
            case 125:
                switch (this.object.getId()) {
                    case 7532://Poissons de rivi�re
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //P�cher Poissons de mer
            case 129:
                switch (this.object.getId()) {
                    case 7531://Poissons de mer
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //P�cher Gros poissons de rivi�re
            case 126:
                switch (this.object.getId()) {
                    case 7537://Gros poissons de rivi�re
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //P�cher Gros poissons de mers
            case 130:
                switch (this.object.getId()) {
                    case 7538://Gros poissons de mers
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //P�cher Poissons g�ants de rivi�re
            case 127:
                switch (this.object.getId()) {
                    case 7539://Poissons g�ants de rivi�re
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //P�cher Poissons g�ants de mer
            case 131:
                switch (this.object.getId()) {
                    case 7540://Poissons g�ants de mer
                        return this.object.getState() == JobConstant.IOBJECT_STATE_FULL;
                }
                return false;
            //Boulanger
            case 109://Pain
            case 27://Bonbon
                return this.object.getId() == 7001;
            //Poissonier
            case 135://Faire un poisson (mangeable)
                return this.object.getId() == 7022;
            //Chasseur
            case 134:
                return this.object.getId() == 7023;
            //Boucher
            case 132:
                return this.object.getId() == 7025;
            case 157:
                return (this.object.getId() == 7030 || this.object.getId() == 7031);
            case 44://Sauvegarder le Zaap
            case 114://Utiliser le Zaap
                switch (this.object.getId()) {
                    //Zaaps
                    case 7000:
                    case 7026:
                    case 7029:
                    case 4287:
                        return true;
                }
                return false;

            case 175://Acc�der
            case 176://Acheter
            case 177://Vendre
            case 178://Modifier le prix de vente
                switch (this.object.getId()) {
                    //Enclos
                    case 6763:
                    case 6766:
                    case 6767:
                    case 6772:
                        return true;
                }
                return false;

            case 179://Levier
                return this.object.getId() == 7045;
            //Se rendre � incarnam
            case 183:
                switch (this.object.getId()) {
                    case 1845:
                    case 1853:
                    case 1854:
                    case 1855:
                    case 1856:
                    case 1857:
                    case 1858:
                    case 1859:
                    case 1860:
                    case 1861:
                    case 1862:
                    case 2319:
                        return true;
                }
                return false;

            //Enclume magique
            case 1:
            case 113:
            case 115:
            case 116:
            case 117:
            case 118:
            case 119:
            case 120:
                return this.object.getId() == 7020;

            //Enclume
            case 19:
            case 143:
            case 145:
            case 144:
            case 142:
            case 146:
            case 67:
            case 21:
            case 65:
            case 66:
            case 20:
            case 18:
                return this.object.getId() == 7012;

            //Costume Mage
            case 167:
            case 165:
            case 166:
                return this.object.getId() == 7036;

            //Coordo Mage
            case 164:
            case 163:
                return this.object.getId() == 7037;

            //Joai Mage
            case 168:
            case 169:
                return this.object.getId() == 7038;

            //Bricoleur
            case 171:
            case 182:
                return this.object.getId() == 7039;

            //Forgeur Bouclier
            case 156:
                return this.object.getId() == 7027;

            //Coordonier
            case 13:
            case 14:
                return this.object.getId() == 7011;

            //Tailleur (Dos)
            case 123:
            case 64:
                return this.object.getId() == 7015;

            //Sculteur
            case 17:
            case 16:
            case 147:
            case 148:
            case 149:
            case 15:
                return this.object.getId() == 7013;
            //TODO: R�par�
            //Tailleur (Haut)
            case 63:
                return (this.object.getId() == 7014 || this.object.getId() == 7016);
            //Atelier : Cr�er Amu // Anneau
            case 11:
            case 12:
                return (this.object.getId() >= 7008 && this.object.getId() <= 7010);
            //Maison
            case 81://V�rouiller
            case 84://Acheter
            case 97://Entrer
            case 98://Vendre
            case 108://Modifier le prix de vente
                return (this.object.getId() >= 6700 && this.object.getId() <= 6776);
            //Coffre
            case 104://Ouvrir
            case 105://Code
                return (this.object.getId() == 7350
                        || this.object.getId() == 7351 || this.object.getId() == 7353);
            case 170://Livre des artisants.
                return this.object.getId() == 7035;
            case 121:
            case 181:
                return this.object.getId() == 7021;
            case 110:
                return this.object.getId() == 7018;
            case 153:
                return this.object.getId() == 7352;

            default:
                return false;
        }
    }

    public void startAction(final Player player, GameAction GA) {
        if(player.getExchangeAction() != null) {
            player.send("BN");
            return;
        }
        int actionID = -1;
        short CcellID = -1;
        try {
            actionID = Integer.parseInt(GA.args.split(";")[1]);
            CcellID = Short.parseShort(GA.args.split(";")[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (actionID == -1) {
            SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("area.map.gamecase.startaction.idnull"));
            return;
        }
        if (player.getDoAction()) {
            SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("area.map.gamecase.startaction.current.action"));
            return;
        }
        if (JobConstant.isJobAction(actionID) && player.getFight() == null) {
            if (player.getPodUsed() > player.getMaxPod()) {
                SocketManager.GAME_SEND_Im_PACKET(player, "112");
                return;
            }
            if (player.getMount() != null) {
                if (player.getMount().getActualPods() > player.getMount().getMaxPods()) {
                    SocketManager.GAME_SEND_Im_PACKET(player, "112");
                    return;
                }
            }
            player.setDoAction(true);
            player.doJobAction(actionID, this.object, GA, this);
            return;
        }
        switch (actionID) {
            case 111:
            case 62://Fontaine de jouvence
                SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("area.map.gamecase.startaction.fountain"));
                player.fullPDV();
                break;

            case 42://Tas de patate
                if (!this.object.isInteractive())
                    return;//Si l'objet est utilis�
                if (this.object.getState() != JobConstant.IOBJECT_STATE_FULL)
                    return;//Si le puits est vide
                this.object.setState(JobConstant.IOBJECT_STATE_EMPTYING);
                this.object.setInteractive(false);
                SocketManager.GAME_SEND_GA_PACKET_TO_MAP(player.getCurMap(), ""
                        + GA.id, 501, player.getId() + "", this.id + ","
                        + this.object.getUseDuration() + ","
                        + this.object.getUnknowValue());
                SocketManager.GAME_SEND_GDF_PACKET_TO_MAP(player.getCurMap(), this);

                TimerWaiter.addNext(() -> {
                    this.getObject().setState(JobConstant.IOBJECT_STATE_EMPTY);
                    this.getObject().setInteractive(false);
                    this.getObject().disable();
                    SocketManager.GAME_SEND_GDF_PACKET_TO_MAP(player.getCurMap(), this);
                    int qua = Formulas.getRandomValue(1, 5);//On a entre 1 et 10 eaux
                    GameObject obj = World.world.getObjTemplate(537).createNewItem(qua, false);
                    if (player.addObjet(obj, true))
                        World.world.addGameObject(obj);
                    SocketManager.GAME_SEND_IQ_PACKET(player, player.getId(), qua);
                }, this.getObject().getUseDuration());
                break;

            case 44://Sauvegarder pos
                if (!player.verifOtomaiZaap())
                    return;
                short map = player.getCurMap().getId();
                String str = map + "," + World.world.getZaapCellIdByMapId(map);
                player.set_savePos(str);
                SocketManager.GAME_SEND_Im_PACKET(player, "06");
                break;

            case 102://Puiser
                if (!this.getObject().isInteractive())
                    return;//Si l'objet est utilis�
                if (this.getObject().getState() != JobConstant.IOBJECT_STATE_FULL)
                    return;//Si le puits est vide
                this.getObject().setState(JobConstant.IOBJECT_STATE_EMPTYING);
                this.getObject().setInteractive(false);
                SocketManager.GAME_SEND_GA_PACKET_TO_MAP(player.getCurMap(), ""
                        + GA.id, 501, player.getId() + "", this.id + ","
                        + this.getObject().getUseDuration() + ","
                        + this.getObject().getUnknowValue());
                SocketManager.GAME_SEND_GDF_PACKET_TO_MAP(player.getCurMap(), this);

                TimerWaiter.addNext(() -> {
                    this.getObject().setState(JobConstant.IOBJECT_STATE_EMPTY);
                    this.getObject().setInteractive(false);
                    this.getObject().disable();
                    SocketManager.GAME_SEND_GDF_PACKET_TO_MAP(player.getCurMap(), this);
                    int qua = Formulas.getRandomValue(1, 5);//On a entre 1 et 10 eaux
                    GameObject obj = World.world.getObjTemplate(6857).createNewItem(qua, false);
                    if (player.addObjet(obj, true))
                        World.world.addGameObject(obj);
                    player.send("Im021;" + qua + "~6857");
                    SocketManager.GAME_SEND_IQ_PACKET(player, player.getId(), qua);
                }, this.getObject().getUseDuration());
                break;

            case 114://Utiliser (zaap)
                player.openZaapMenu();
                player.getGameClient().removeAction(GA);
                break;

            case 157: //Zaapis
                String ZaapiList = "";
                String[] Zaapis;
                int count = 0;
                int price = 20;
                if(Constant.ZAAPI.isEmpty() || Constant.ZAAPI.get(Constant.ALIGNEMENT_BONTARIEN).isEmpty() || Constant.ZAAPI.get(Constant.ALIGNEMENT_BRAKMARIEN).isEmpty()) break;
                if (player.getCurMap().getSubArea().getArea().getId() == 7 && (player.getAlignment() == 1 || player.getAlignment() == 0 || player.getAlignment() == 3))//Ange, Neutre ou S�rianne
                {
                    Zaapis = Constant.ZAAPI.get(Constant.ALIGNEMENT_BONTARIEN).split(",");
                    if (player.getAlignment() == 1)
                        price = 10;
                } else if (player.getCurMap().getSubArea().getArea().getId() == 11
                        && (player.getAlignment() == 2 || player.getAlignment() == 0 || player.getAlignment() == 3))//D�mons, Neutre ou S�rianne
                {
                    Zaapis = Constant.ZAAPI.get(Constant.ALIGNEMENT_BRAKMARIEN).split(",");
                    if (player.getAlignment() == 2)
                        price = 10;
                } else {
                    Zaapis = Constant.ZAAPI.get(Constant.ALIGNEMENT_NEUTRE).split(",");
                }

                if (Zaapis.length > 0) {
                    for (String s : Zaapis) {
                        if (count == Zaapis.length)
                            ZaapiList += s + ";" + price;
                        else
                            ZaapiList += s + ";" + price + "|";
                        count++;
                    }
                    player.setExchangeAction(new ExchangeAction<>(ExchangeAction.IN_ZAPPI, 0));
                    SocketManager.GAME_SEND_ZAAPI_PACKET(player, ZaapiList);
                }
                break;
            case 175://Acceder a un enclos
                final MountPark park = player.getCurMap().getMountPark();
                if (park == null)
                    return;

                try {
                    park.getEtable().stream().filter(mount -> mount != null).forEach(mount -> mount.checkBaby(player,park));
                    park.getListOfRaising().stream().filter(integer -> World.world.getMountById(integer) != null).forEach(integer -> World.world.getMountById(integer).checkBaby(player,park));
                } catch(Exception e) {
                    e.printStackTrace();
                }

                if(park.getGuild() != null)
                    for(Player target : park.getGuild().getPlayers())
                        if(target != null && target.getExchangeAction() != null && target.getExchangeAction().getType() == ExchangeAction.IN_MOUNTPARK && target.getCurMap().getId() == player.getCurMap().getId()) {
                            player.send("Im120");
                            return;
                        }

                player.openMountPark(null);
                break;
            case 176://Achat enclo
                MountPark MP = player.getCurMap().getMountPark();
                if (MP.getOwner() == -1)//Public
                {
                    SocketManager.GAME_SEND_Im_PACKET(player, "196");
                    return;
                }
                if (MP.getPrice() == 0)//Non en vente
                {
                    SocketManager.GAME_SEND_Im_PACKET(player, "197");
                    return;
                }
                if (player.getGuild() == null)//Pas de guilde
                {
                    SocketManager.GAME_SEND_Im_PACKET(player, "1135");
                    return;
                }
                if (player.getGuildMember().getRank() != 1)//Non meneur
                {
                    SocketManager.GAME_SEND_Im_PACKET(player, "198");
                    return;
                }
                SocketManager.GAME_SEND_R_PACKET(player, "D" + MP.getPrice()
                        + "|" + MP.getPrice());
                break;
            case 177://Vendre enclo
            case 178://Modifier prix de vente
                MountPark MP1 = player.getCurMap().getMountPark();
                if (MP1.getOwner() == -1) {
                    SocketManager.GAME_SEND_Im_PACKET(player, "194");
                    return;
                }
                if (MP1.getOwner() != player.getId()) {
                    SocketManager.GAME_SEND_Im_PACKET(player, "195");
                    return;
                }
                SocketManager.GAME_SEND_R_PACKET(player, "D" + MP1.getPrice() + "|" + MP1.getPrice());
                break;
            case 183://Retourner sur Incarnam
                if (player.getLevel() > 30) {
                    SocketManager.GAME_SEND_Im_PACKET(player, "1127");
                    player.getGameClient().removeAction(GA);
                    return;
                }
                short mapID = Constant.getStartMap(player.getClasse());
                int cellID = Constant.getStartCell(player.getClasse());
                player.teleport(mapID, cellID);
                player.getGameClient().removeAction(GA);
                break;
            case 81://V�rouiller maison
                House house = World.world.getHouseManager().getHouseIdByCoord(player.getCurMap().getId(), CcellID);
                if (house == null)
                    return;
                player.setInHouse(house);
                house.lock(player);
                break;
            case 84://Rentrer dans une maison
                house = World.world.getHouseManager().getHouseIdByCoord(player.getCurMap().getId(), CcellID);
                if (house == null)
                    return;

                GameMap mapHouse = World.world.getMap((short) house.getHouseMapId());
                if (mapHouse == null) {
                    SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("area.map.gamecase.startaction.house.broken"));
                    return;
                }
                GameCase caseHouse = mapHouse.getCase(house.getHouseCellId());
                if (caseHouse == null || !caseHouse.isWalkable(true)) {
                    SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("area.map.gamecase.startaction.house.broken"));
                    return;
                }
                if (player.isOnMount()) {
                    SocketManager.GAME_SEND_Im_PACKET(player, "1118");
                    return;
                }
                house.enter(player);
                player.setInHouse(house);
                break;
            case 97://Acheter maison
                house = World.world.getHouseManager().getHouseIdByCoord(player.getCurMap().getId(), CcellID);
                if (house == null)
                    return;
                player.setInHouse(house);
                house.buyIt(player);
                break;

            case 104://Ouvrir coffre priv�
                if(player.getInHouse() == null) break;
                Trunk trunk = Trunk.getTrunkIdByCoord(player.getCurMap().getId(), CcellID);

                if (trunk == null) {
                    trunk = new Trunk(-1, player.getInHouse().getId(), player.getCurMap().getId(), CcellID);
                    trunk.setOwnerId(player.getInHouse().getOwnerId());
                    trunk.setKey("-");
                    trunk.setKamas(0);
                    ((BaseTrunkData) DatabaseManager.get(BaseTrunkData.class)).insert(trunk);
                    World.world.addTrunk(trunk);
                }
                if(player.getInHouse() != null && trunk.getOwnerId() != player.getAccID() && trunk.getHouseId() == player.getInHouse().getId() && player.getId() == player.getInHouse().getOwnerId()) {
                    trunk.setOwnerId(player.getId());
                    ((TrunkData) DatabaseManager.get(TrunkData.class)).update(player, player.getInHouse());
                }

                trunk.enter(player);
                break;
            case 105://V�rouiller coffre
                Trunk t = Trunk.getTrunkIdByCoord(player.getCurMap().getId(), CcellID);

                if (t == null)
                    return;
                t.Lock(player);
                break;
            case 153:
                trunk = Trunk.getTrunkIdByCoord(player.getCurMap().getId(), CcellID);

                if(trunk != null) {
                    if (trunk.getPlayer() != null) {
                        player.send("Im120");
                        return;
                    }
                    player.setExchangeAction(new ExchangeAction<>(ExchangeAction.IN_TRUNK, trunk));
                    Trunk.open(player, "-", true);
                }
                break;
            case 98://Vendre
            case 108://Modifier prix de vente
                House h4 = World.world.getHouseManager().getHouseIdByCoord(player.getCurMap().getId(), CcellID);
                if (h4 == null)
                    return;
                player.setInHouse(h4);
                h4.sellIt(player);
                break;
            case 170:// Livre des artisans
                player.setLivreArtisant(true);
                player.setExchangeAction(new ExchangeAction<>(ExchangeAction.CRAFTING_BOOK, new BreakingObject()));
                SocketManager.GAME_SEND_ECK_PACKET(player, 14, "2;11;13;14;15;16;17;18;19;20;24;25;26;27;28;31;33;36;41;43;44;45;46;47;48;49;50;56;58;62;63;64;65");
                break;
            case 181:
                SocketManager.SEND_GDF_PERSO(player, CcellID, 3, 1);
                SocketManager.GAME_SEND_ECK_PACKET(player, 3, "8;181");
                player.setExchangeAction(new ExchangeAction<>(ExchangeAction.BREAKING_OBJECTS, new BreakingObject()));
                break;
        }
        player.getGameClient().removeAction(GA);
    }

    public void finishAction(Player perso, GameAction GA) {
        int actionID = -1;
        try {
            actionID = Integer.parseInt(GA.args.split(";")[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (actionID == -1)
            return;

        if (JobConstant.isJobAction(actionID)) {
            perso.finishJobAction(actionID, this.object, GA, this);
            perso.setDoAction(false);
            return;
        }
        perso.setDoAction(false);
        switch (actionID) {
            case 44://Sauvegarder a un zaap
            case 81://V�rouiller maison
            case 84://ouvrir maison
            case 97://Acheter maison.
            case 98://Vendre
            case 104://Ouvrir coffre
            case 105://Code coffre
            case 108://Modifier prix de vente
            case 157://Zaapi
            case 121://Briser une ressource
            case 181://Concasseur
            case 110:
            case 153:
            case 183:
                break;
            case 42://Tas de patate
                if (this.object == null)
                    return;
                this.object.setState(JobConstant.IOBJECT_STATE_EMPTY);
                this.object.setInteractive(false);
                this.object.disable();
                SocketManager.GAME_SEND_GDF_PACKET_TO_MAP(perso.getCurMap(), this);
                int qua = Formulas.getRandomValue(1, 5);//On a entre 1 et 5 patates
                GameObject obj = World.world.getObjTemplate(537).createNewItem(qua, false);
                if (perso.addObjet(obj, true))
                    World.world.addGameObject(obj);
                SocketManager.GAME_SEND_IQ_PACKET(perso, perso.getId(), qua);
                break;
            case 102://Puiser
                if (this.object == null)
                    return;

                this.object.setState(JobConstant.IOBJECT_STATE_EMPTY);
                this.object.setInteractive(false);
                this.object.disable();
                SocketManager.GAME_SEND_GDF_PACKET_TO_MAP(perso.getCurMap(), this);
                qua = Formulas.getRandomValue(1, 10);//On a entre 1 et 10 eaux
                obj = World.world.getObjTemplate(311).createNewItem(qua, false);
                if (perso.addObjet(obj, true))
                    World.world.addGameObject(obj);
                SocketManager.GAME_SEND_IQ_PACKET(perso, perso.getId(), qua);
                break;
        }
    }
}