package org.starloco.locos.area.map;

import org.starloco.locos.entity.map.InteractiveObject;
import org.starloco.locos.player.Player;
import org.starloco.locos.entity.map.InteractiveObjectTemplate;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.game.world.World;
import org.starloco.locos.item.FullItem;

import java.util.*;
import java.util.stream.Collectors;

//  Temporary proxy class until we fully clean up GameCase usages
public class GameCase {
    private final GameMap map;
    public final int cellId;

    public GameCase(GameMap map, int cellId) {
        this.map = map;
        this.cellId = cellId;
    }

    public int getId() { return cellId; }

    private <T extends Actor> List<T> getActorsOfType(Class<T> clz) {
        return map.actors.getOrDefault(cellId, Collections.emptySet()).stream()
            .filter(clz::isInstance)
            .map(clz::cast)
            .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    public List<Player> getPlayers() {
        return getActorsOfType(Player.class);
    }

    public List<Fighter> getFighters() {
        return getActorsOfType(Fighter.class);
    }

    public FullItem getDroppedItem(boolean delete) {
        if(delete) {
            return map.droppedItems.remove(cellId);
        }
        return map.droppedItems.get(cellId);
    }

    public void clearDroppedItem() {
        map.droppedItems.remove(cellId);
    }

    public InteractiveObject getObject() {
        return map.interactiveObjects.get(cellId);
    }

    public boolean isWalkable(boolean inFight) {
        if(!map.cellsData.active(cellId)) return false;
        switch (map.cellsData.movement(cellId)) {
            case 0:
                return false;
            case 1:
                // Only walkable out of fights
                return !inFight;
            default: // 2+
                return true;
        }
    }

    public boolean isWalkableFight() {
        return this.isWalkable(true);
    }
    public boolean isWalkable(boolean checkObject, boolean inFight, int targetCell) {
        // Official servers:
        // Cells without objects: depends on walkable field
        // Cells with objects:
        // -> Cannot stop on cell if object is ready to harvest
        // -> Can always go through walkable objects

        Optional<InteractiveObjectTemplate> ioDef = World.world.getObjectBySprite(this.map.cellsData.object2(cellId));

        // Cells with objects:
        if(checkObject && ioDef.isPresent()) {
            boolean isReady = this.map.getAnimationState(cellId) == null || this.map.getAnimationState(cellId).equals("ready");
            // -> Cannot stop on cell if object is ready to harvest
            if (this.cellId == targetCell && isReady) return false;
            // -> Cannot go through non-walkable objects
            if (!ioDef.get().isWalkable()) return false;
        }
        return this.isWalkable(inFight);
    }

    private <T extends Actor> void addActor(T actor) {
        map.actors.computeIfAbsent(cellId, i -> new HashSet<>()).add(actor);
    }

    public void addPlayer(Player player) {
        addActor(player);
    }

    public void addFighter(Fighter init0) { addActor(init0); }

    private <T extends Actor> void removeActor(T actor) {
        map.actors.getOrDefault(cellId, Collections.emptySet()).remove(actor);
    }

    public void removePlayer(Player p) {
        removeActor(p);
    }
    public void removeFighter(Fighter f) {
        removeActor(f);
    }

    public Fighter getFirstFighter() {
        if(getFighters().isEmpty()) return null;
        return getFighters().get(0);
    }

//    public boolean canDoAction(int id) {
//        InteractiveObject object = map.interactiveObjects.get(cellId);
//
//        if (object == null)
//            return false;
//        switch (id) {
//            //Atelier des F�es
//            case 151:
//                return object.getId() == 7028;
//
//            //Fontaine jouvence
//            case 111:
//            case 62:
//                return object.getId() == 7004;
//            //Moudre et egrenner - Paysan
//            case 122:
//            case 47:
//                return object.getId() == 7007;
//            //Faucher Bl�
//            case 45:
//                switch (object.getId()) {
//                    case 7511://Bl�
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Faucher Orge
//            case 53:
//                switch (object.getId()) {
//                    case 7515://Orge
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//
//            //Faucher Avoine
//            case 57:
//                switch (object.getId()) {
//                    case 7517://Avoine
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Faucher Houblon
//            case 46:
//                switch (object.getId()) {
//                    case 7512://Houblon
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Faucher Lin
//            case 50:
//            case 68:
//                switch (object.getId()) {
//                    case 7513://Lin
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Faucher Riz
//            case 159:
//                switch (object.getId()) {
//                    case 7550://Riz
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Faucher Seigle
//            case 52:
//                switch (object.getId()) {
//                    case 7516://Seigle
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Faucher Malt
//            case 58:
//                switch (object.getId()) {
//                    case 7518://Malt
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Faucher Chanvre - Cueillir Chanvre
//            case 69:
//            case 54:
//                switch (object.getId()) {
//                    case 7514://Chanvre
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Scier - Bucheron
//            case 101:
//                return object.getId() == 7003;
//            //Couper Fr�ne
//            case 6:
//                switch (object.getId()) {
//                    case 7500://Fr�ne
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Couper Ch�taignier
//            case 39:
//                switch (object.getId()) {
//                    case 7501://Ch�taignier
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Couper Noyer
//            case 40:
//                switch (object.getId()) {
//                    case 7502://Noyer
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Couper Ch�ne
//            case 10:
//                switch (object.getId()) {
//                    case 7503://Ch�ne
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Couper Oliviolet
//            case 141:
//                switch (object.getId()) {
//                    case 7542://Oliviolet
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Couper Bombu
//            case 139:
//                switch (object.getId()) {
//                    case 7541://Bombu
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Couper Erable
//            case 37:
//                switch (object.getId()) {
//                    case 7504://Erable
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Couper Bambou
//            case 154:
//                switch (object.getId()) {
//                    case 7553://Bambou
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Couper If
//            case 33:
//                switch (object.getId()) {
//                    case 7505://If
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Couper Merisier
//            case 41:
//                switch (object.getId()) {
//                    case 7506://Merisier
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Couper Eb�ne
//            case 34:
//                switch (object.getId()) {
//                    case 7507://Eb�ne
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Couper Kalyptus
//            case 174:
//                switch (object.getId()) {
//                    case 7557://Kalyptus
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Couper Charme
//            case 38:
//                switch (object.getId()) {
//                    case 7508://Charme
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Couper Orme
//            case 35:
//                switch (object.getId()) {
//                    case 7509://Orme
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Couper Bambou Sombre
//            case 155:
//                switch (object.getId()) {
//                    case 7554://Bambou Sombre
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Couper Bambou Sacr�
//            case 158:
//                switch (object.getId()) {
//                    case 7552://Bambou Sacr�
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Puiser
//            case 102:
//                switch (object.getId()) {
//                    case 7519://Puits
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Polir
//            case 48:
//                return object.getId() == 7005;
//            //Tas de patate
//            case 42:
//                return object.getId() == 7510;
//            //Moule/Fondre - Mineur
//            case 32:
//                return object.getId() == 7002;
//            case 22:
//                return object.getId() == 7006;
//            //Miner Fer
//            case 24:
//                switch (object.getId()) {
//                    case 7520://Miner
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Miner Cuivre
//            case 25:
//                switch (object.getId()) {
//                    case 7522://Miner
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Miner Bronze
//            case 26:
//                switch (object.getId()) {
//                    case 7523://Miner
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Miner Kobalte
//            case 28:
//                switch (object.getId()) {
//                    case 7525://Miner
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Miner Manga
//            case 56:
//                switch (object.getId()) {
//                    case 7524://Miner
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Miner Sili
//            case 162:
//                switch (object.getId()) {
//                    case 7556://Miner
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Miner Etain
//            case 55:
//                switch (object.getId()) {
//                    case 7521://Miner
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Miner Argent
//            case 29:
//                switch (object.getId()) {
//                    case 7526://Miner
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Miner Bauxite
//            case 31:
//                switch (object.getId()) {
//                    case 7528://Miner
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Miner Or
//            case 30:
//                switch (object.getId()) {
//                    case 7527://Miner
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Miner Dolomite
//            case 161:
//                switch (object.getId()) {
//                    case 7555://Miner
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Fabriquer potion - Alchimiste
//            case 23:
//                return object.getId() == 7019;
//            //Cueillir Tr�fle
//            case 71:
//                switch (object.getId()) {
//                    case 7533://Tr�fle
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Cueillir Menthe
//            case 72:
//                switch (object.getId()) {
//                    case 7534://Menthe
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Cueillir Orchid�e
//            case 73:
//                switch (object.getId()) {
//                    case 7535:// Orchid�e
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Cueillir Edelweiss
//            case 74:
//                switch (object.getId()) {
//                    case 7536://Edelweiss
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Cueillir Graine de Pandouille
//            case 160:
//                switch (object.getId()) {
//                    case 7551://Graine de Pandouille
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Vider - P�cheur
//            case 133:
//                return object.getId() == 7024;
//            //P�cher Petits poissons de mer
//            case 128:
//                switch (object.getId()) {
//                    case 7530://Petits poissons de mer
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //P�cher Petits poissons de rivi�re
//            case 124:
//                switch (object.getId()) {
//                    case 7529://Petits poissons de rivi�re
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //P�cher Pichon
//            case 136:
//                switch (object.getId()) {
//                    case 7544://Pichon
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //P�cher Ombre Etrange
//            case 140:
//                switch (object.getId()) {
//                    case 7543://Ombre Etrange
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //P�cher Poissons de rivi�re
//            case 125:
//                switch (object.getId()) {
//                    case 7532://Poissons de rivi�re
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //P�cher Poissons de mer
//            case 129:
//                switch (object.getId()) {
//                    case 7531://Poissons de mer
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //P�cher Gros poissons de rivi�re
//            case 126:
//                switch (object.getId()) {
//                    case 7537://Gros poissons de rivi�re
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //P�cher Gros poissons de mers
//            case 130:
//                switch (object.getId()) {
//                    case 7538://Gros poissons de mers
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //P�cher Poissons g�ants de rivi�re
//            case 127:
//                switch (object.getId()) {
//                    case 7539://Poissons g�ants de rivi�re
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //P�cher Poissons g�ants de mer
//            case 131:
//                switch (object.getId()) {
//                    case 7540://Poissons g�ants de mer
//                        return object.getState() == JobConstant.IOBJECT_STATE_FULL;
//                }
//                return false;
//            //Boulanger
//            case 109://Pain
//            case 27://Bonbon
//                return object.getId() == 7001;
//            //Poissonier
//            case 135://Faire un poisson (mangeable)
//                return object.getId() == 7022;
//            //Chasseur
//            case 134:
//                return object.getId() == 7023;
//            //Boucher
//            case 132:
//                return object.getId() == 7025;
//            case 157:
//                return (object.getId() == 7030 || object.getId() == 7031);
//            case 44://Sauvegarder le Zaap
//            case 114://Utiliser le Zaap
//                switch (object.getId()) {
//                    //Zaaps
//                    case 7000:
//                    case 7026:
//                    case 7029:
//                    case 4287:
//                        return true;
//                }
//                return false;
//
//            case 175://Acc�der
//            case 176://Acheter
//            case 177://Vendre
//            case 178://Modifier le prix de vente
//                switch (object.getId()) {
//                    //Enclos
//                    case 6763:
//                    case 6766:
//                    case 6767:
//                    case 6772:
//                        return true;
//                }
//                return false;
//
//            case 179://Levier
//                return object.getId() == 7045;
//            //Se rendre � incarnam
//            case 183:
//                switch (object.getId()) {
//                    case 1845:
//                    case 1853:
//                    case 1854:
//                    case 1855:
//                    case 1856:
//                    case 1857:
//                    case 1858:
//                    case 1859:
//                    case 1860:
//                    case 1861:
//                    case 1862:
//                    case 2319:
//                        return true;
//                }
//                return false;
//
//            //Enclume magique
//            case 1:
//            case 113:
//            case 115:
//            case 116:
//            case 117:
//            case 118:
//            case 119:
//            case 120:
//                return object.getId() == 7020;
//
//            //Enclume
//            case 19:
//            case 143:
//            case 145:
//            case 144:
//            case 142:
//            case 146:
//            case 67:
//            case 21:
//            case 65:
//            case 66:
//            case 20:
//            case 18:
//                return object.getId() == 7012;
//
//            //Costume Mage
//            case 167:
//            case 165:
//            case 166:
//                return object.getId() == 7036;
//
//            //Coordo Mage
//            case 164:
//            case 163:
//                return object.getId() == 7037;
//
//            //Joai Mage
//            case 168:
//            case 169:
//                return object.getId() == 7038;
//
//            //Bricoleur
//            case 171:
//            case 182:
//                return object.getId() == 7039;
//
//            //Forgeur Bouclier
//            case 156:
//                return object.getId() == 7027;
//
//            //Coordonier
//            case 13:
//            case 14:
//                return object.getId() == 7011;
//
//            //Tailleur (Dos)
//            case 123:
//            case 64:
//                return object.getId() == 7015;
//
//            //Sculteur
//            case 17:
//            case 16:
//            case 147:
//            case 148:
//            case 149:
//            case 15:
//                return object.getId() == 7013;
//            //TODO: R�par�
//            //Tailleur (Haut)
//            case 63:
//                return (object.getId() == 7014 || object.getId() == 7016);
//            //Atelier : Cr�er Amu // Anneau
//            case 11:
//            case 12:
//                return (object.getId() >= 7008 && object.getId() <= 7010);
//            //Maison
//            case 81://V�rouiller
//            case 84://Acheter
//            case 97://Entrer
//            case 98://Vendre
//            case 108://Modifier le prix de vente
//                return (object.getId() >= 6700 && object.getId() <= 6776);
//            //Coffre
//            case 104://Ouvrir
//            case 105://Code
//                return (object.getId() == 7350
//                        || object.getId() == 7351 || object.getId() == 7353);
//            case 170://Livre des artisants.
//                return object.getId() == 7035;
//            case 121:
//            case 181:
//                return object.getId() == 7021;
//            case 110:
//                return object.getId() == 7018;
//            case 153:
//                return object.getId() == 7352;
//
//            default:
//                return false;
//        }
//    }
//
//    public void startAction(final Player player, GameAction GA) {
//        if(player.getExchangeAction() != null) {
//            player.send("BN");
//            return;
//        }
//        int skillId = -1;
//        short CcellID = -1;
//        InteractiveObject object = map.interactiveObjects.get(cellId);
//
//        try {
//            String[] parts = GA.args.split(";");
//            CcellID = Short.parseShort(parts[0]);
//            skillId = Integer.parseInt(parts[1]);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (skillId == -1) {
//            SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("area.map.gamecase.startaction.idnull"));
//            return;
//        }
//        if (player.getDoAction()) {
//            SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("area.map.gamecase.startaction.current.action"));
//            return;
//        }
//        if (JobConstant.isJobAction(skillId) && player.getFight() == null) {
//            if (player.getPodUsed() > player.getMaxPod()) {
//                SocketManager.GAME_SEND_Im_PACKET(player, "112");
//                return;
//            }
//            if (player.getMount() != null) {
//                if (player.getMount().getActualPods() > player.getMount().getMaxPods()) {
//                    SocketManager.GAME_SEND_Im_PACKET(player, "112");
//                    return;
//                }
//            }
//            player.setDoAction(true);
//            player.doJobAction(skillId, GA.id, cellId, object);
//            return;
//        }
//        switch (skillId) {
//            case 111:
//            case 62://Fontaine de jouvence
//                SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("area.map.gamecase.startaction.fountain"));
//                player.fullPDV();
//                break;
//
//            case 42://Tas de patate
//                if (!object.isInteractive())
//                    return;//Si l'objet est utilis�
//                if (object.getState() != JobConstant.IOBJECT_STATE_FULL)
//                    return;//Si le puits est vide
//                object.setState(JobConstant.IOBJECT_STATE_EMPTYING);
//                object.setInteractive(false);
//                SocketManager.GAME_SEND_GA_PACKET_TO_MAP(player.getCurMap(), ""
//                        + GA.id, 501, player.getId() + "", this.cellId + ","
//                        + object.getUseDuration() + ","
//                        + object.getUnknowValue());
//                SocketManager.GAME_SEND_GDF_PACKET_TO_MAP(player.getCurMap(), this.cellId, object);
//
//                TimerWaiter.addNext(() -> {
//                    this.getObject().setState(JobConstant.IOBJECT_STATE_EMPTY);
//                    this.getObject().setInteractive(false);
//                    this.getObject().disable();
//                    SocketManager.GAME_SEND_GDF_PACKET_TO_MAP(player.getCurMap(), this.cellId, object);
//                    int qua = Formulas.getRandomValue(1, 5);
//                    GameObject obj = World.world.getObjTemplate(537).createNewItem(qua, false);
//                    if (player.addItem(obj, true, false))
//                        World.world.addGameObject(obj);
//                    SocketManager.GAME_SEND_IQ_PACKET(player, player.getId(), qua);
//                }, this.getObject().getUseDuration());
//                break;
//
//            case 44://Sauvegarder pos
//                if (!player.verifOtomaiZaap())
//                    return;
//                int map = player.getCurMap().getId();
//                player.setSavePos(map, Constant.ZAAPS.getOrDefault(map, -1));
//                SocketManager.GAME_SEND_Im_PACKET(player, "06");
//                break;
//
//            case 102://Puiser
//                if (!this.getObject().isInteractive())
//                    return;//Si l'objet est utilis�
//                if (this.getObject().getState() != JobConstant.IOBJECT_STATE_FULL)
//                    return;//Si le puits est vide
//                this.getObject().setState(JobConstant.IOBJECT_STATE_EMPTYING);
//                this.getObject().setInteractive(false);
//                SocketManager.GAME_SEND_GA_PACKET_TO_MAP(player.getCurMap(), ""
//                        + GA.id, 501, player.getId() + "", this.cellId + ","
//                        + this.getObject().getUseDuration() + ","
//                        + this.getObject().getUnknowValue());
//                SocketManager.GAME_SEND_GDF_PACKET_TO_MAP(player.getCurMap(), this.cellId, object);
//
//                TimerWaiter.addNext(() -> {
//                    this.getObject().setState(JobConstant.IOBJECT_STATE_EMPTY);
//                    this.getObject().setInteractive(false);
//                    this.getObject().disable();
//                    SocketManager.GAME_SEND_GDF_PACKET_TO_MAP(player.getCurMap(), this.cellId, object);
//                    int qua = Formulas.getRandomValue(1, 10);
//                    GameObject obj = World.world.getObjTemplate(311).createNewItem(qua, false);
//                    if (player.addItem(obj, true, false))
//                        World.world.addGameObject(obj);
//                    player.send("Im021;" + qua + "~311");
//                    SocketManager.GAME_SEND_IQ_PACKET(player, player.getId(), qua);
//                }, this.getObject().getUseDuration());
//                break;
//
//            case 114://Utiliser (zaap)
//                player.openZaapMenu();
//                player.getGameClient().removeAction(GA);
//                break;
//
//            case 157: //Zaapis
//                String ZaapiList = "";
//                String[] Zaapis;
//                int count = 0;
//                int price = 20;
//                if(Constant.ZAAPI.isEmpty() || Constant.ZAAPI.get(Constant.ALIGNEMENT_BONTARIEN).isEmpty() || Constant.ZAAPI.get(Constant.ALIGNEMENT_BRAKMARIEN).isEmpty()) break;
//                if (player.getCurMap().getSubArea().getArea().getId() == 7 && (player.getAlignment() == 1 || player.getAlignment() == 0 || player.getAlignment() == 3))//Ange, Neutre ou S�rianne
//                {
//                    Zaapis = Constant.ZAAPI.get(Constant.ALIGNEMENT_BONTARIEN).split(",");
//                    if (player.getAlignment() == 1)
//                        price = 10;
//                } else if (player.getCurMap().getSubArea().getArea().getId() == 11
//                        && (player.getAlignment() == 2 || player.getAlignment() == 0 || player.getAlignment() == 3))//D�mons, Neutre ou S�rianne
//                {
//                    Zaapis = Constant.ZAAPI.get(Constant.ALIGNEMENT_BRAKMARIEN).split(",");
//                    if (player.getAlignment() == 2)
//                        price = 10;
//                } else {
//                    Zaapis = Constant.ZAAPI.get(Constant.ALIGNEMENT_NEUTRE).split(",");
//                }
//
//                if (Zaapis.length > 0) {
//                    for (String s : Zaapis) {
//                        if (count == Zaapis.length)
//                            ZaapiList += s + ";" + price;
//                        else
//                            ZaapiList += s + ";" + price + "|";
//                        count++;
//                    }
//                    player.setExchangeAction(new ExchangeAction<>(ExchangeAction.IN_ZAPPI, 0));
//                    SocketManager.GAME_SEND_ZAAPI_PACKET(player, ZaapiList);
//                }
//                break;
//            case 175://Acceder a un enclos
//                final MountPark park = player.getCurMap().getMountPark();
//                if (park == null)
//                    return;
//
//                try {
//                    park.getEtable().stream().filter(mount -> mount != null).forEach(mount -> mount.checkBaby(player,park));
//                    park.getListOfRaising().stream().filter(integer -> World.world.getMountById(integer) != null).forEach(integer -> World.world.getMountById(integer).checkBaby(player,park));
//                } catch(Exception e) {
//                    e.printStackTrace();
//                }
//
//                if(park.getGuild() != null)
//                    for(Player target : park.getGuild().getPlayers())
//                        if(target != null && target.getExchangeAction() != null && target.getExchangeAction().getType() == ExchangeAction.IN_MOUNTPARK && target.getCurMap().getId() == player.getCurMap().getId()) {
//                            player.send("Im120");
//                            return;
//                        }
//
//                player.openMountPark(null);
//                break;
//            case 176://Achat enclo
//                MountPark MP = player.getCurMap().getMountPark();
//                if (MP.getOwner() == -1)//Public
//                {
//                    SocketManager.GAME_SEND_Im_PACKET(player, "196");
//                    return;
//                }
//                if (MP.getPrice() == 0)//Non en vente
//                {
//                    SocketManager.GAME_SEND_Im_PACKET(player, "197");
//                    return;
//                }
//                if (player.getGuild() == null)//Pas de guilde
//                {
//                    SocketManager.GAME_SEND_Im_PACKET(player, "1135");
//                    return;
//                }
//                if (player.getGuildMember().getRank() != 1)//Non meneur
//                {
//                    SocketManager.GAME_SEND_Im_PACKET(player, "198");
//                    return;
//                }
//                SocketManager.GAME_SEND_R_PACKET(player, "D" + MP.getPrice()
//                        + "|" + MP.getPrice());
//                break;
//            case 177://Vendre enclo
//            case 178://Modifier prix de vente
//                MountPark MP1 = player.getCurMap().getMountPark();
//                if (MP1.getOwner() == -1) {
//                    SocketManager.GAME_SEND_Im_PACKET(player, "194");
//                    return;
//                }
//                if (MP1.getOwner() != player.getId()) {
//                    SocketManager.GAME_SEND_Im_PACKET(player, "195");
//                    return;
//                }
//                SocketManager.GAME_SEND_R_PACKET(player, "D" + MP1.getPrice() + "|" + MP1.getPrice());
//                break;
//            case 183://Retourner sur Incarnam
//                if (player.getLevel() > 30) {
//                    SocketManager.GAME_SEND_Im_PACKET(player, "1127");
//                    player.getGameClient().removeAction(GA);
//                    return;
//                }
//                int mapID = Constant.getStartMap(player.getClasse());
//                int cellID = Constant.getStartCell(player.getClasse());
//                player.teleport(mapID, cellID);
//                player.getGameClient().removeAction(GA);
//                break;
//            case 81://V�rouiller maison
//                House house = World.world.getHouseManager().getHouseIdByCoord(player.getCurMap().getId(), CcellID);
//                if (house == null)
//                    return;
//                house.lock(player);
//                break;
//            case 84://Rentrer dans une maison
//                house = World.world.getHouseManager().getHouseIdByCoord(player.getCurMap().getId(), CcellID);
//                if (house == null)
//                    return;
//
//                GameMap mapHouse = World.world.getMap( house.getHouseMapId());
//                if (mapHouse == null) {
//                    SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("area.map.gamecase.startaction.house.broken"));
//                    return;
//                }
//                if (!mapHouse.getCase(house.getHouseCellId()).isWalkable(true, false)) {
//                    SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("area.map.gamecase.startaction.house.broken"));
//                    return;
//                }
//                if (player.isOnMount()) {
//                    SocketManager.GAME_SEND_Im_PACKET(player, "1118");
//                    return;
//                }
//                house.enter(player);
//                player.setInHouse(house);
//                break;
//            case 97://Acheter maison
//                house = World.world.getHouseManager().getHouseIdByCoord(player.getCurMap().getId(), CcellID);
//                if (house == null)
//                    return;
//                player.setInHouse(house);
//                house.buyIt(player);
//                break;
//
//            case 104://Ouvrir coffre priv�
//                if(player.getInHouse() == null) break;
//
//                int trunkCellID = CcellID;
//                Trunk trunk = Trunk.getTrunkIdByCoord(player.getCurMap().getId(), CcellID).orElseGet(() -> {
//                    Trunk t = new Trunk(-1, player.getInHouse().getId(), player.getCurMap().getId(), trunkCellID);
//                    t.setOwnerId(player.getInHouse().getOwnerId());
//                    t.setKey("-");
//                    t.setKamas(0);
//                    ((BaseTrunkData) DatabaseManager.get(BaseTrunkData.class)).insert(t);
//                    World.world.addTrunk(t);
//                    return t;
//                });
//
//                if(player.getInHouse() != null && trunk.getOwnerId() != player.getAccID() && trunk.getHouseId() == player.getInHouse().getId() && player.getId() == player.getInHouse().getOwnerId()) {
//                    trunk.setOwnerId(player.getId());
//                    ((TrunkData) DatabaseManager.get(TrunkData.class)).update(player, player.getInHouse());
//                }
//
//                trunk.enter(player);
//                break;
//            case 105://V�rouiller coffre
//                Trunk.getTrunkIdByCoord(player.getCurMap().getId(), CcellID).ifPresent(t -> {
//                    t.Lock(player);
//                });
//                break;
////            case 153: // Poubelle
////                player.openTrunk(CcellID);
////                break;
//            case 98://Vendre
//            case 108://Modifier prix de vente
//                House h4 = World.world.getHouseManager().getHouseIdByCoord(player.getCurMap().getId(), CcellID);
//                if (h4 == null)
//                    return;
//                player.setInHouse(h4);
//                h4.sellIt(player);
//                break;
//            case 170:// Livre des artisans
//                player.setLivreArtisant(true);
//                player.setExchangeAction(new ExchangeAction<>(ExchangeAction.USING_OBJECT, EmptyActionData.INSTANCE));
//                SocketManager.GAME_SEND_ECK_PACKET(player, 14, "2;11;13;14;15;16;17;18;19;20;24;25;26;27;28;31;33;36;41;43;44;45;46;47;48;49;50;56;58;62;63;64;65");
//                break;
//            case 181:
//                SocketManager.SEND_GDF_PERSO(player, CcellID, 3, 1);
//                SocketManager.GAME_SEND_ECK_PACKET(player, 3, "8;181");
//                player.setExchangeAction(new ExchangeAction<>(ExchangeAction.BREAKING_OBJECTS, new BreakingObject()));
//                break;
//        }
//        player.getGameClient().removeAction(GA);
//    }

//    public void finishAction(Player perso, GameAction GA) {
//        int actionID = -1;
//        try {
//            actionID = Integer.parseInt(GA.args.split(";")[1]);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (actionID == -1)
//            return;
//
//        InteractiveObject object = map.interactiveObjects.get(cellId);
//
//        if (JobConstant.isJobAction(actionID)) {
//            perso.finishJobAction(actionID, object, GA, cellId);
//            perso.setDoAction(false);
//            return;
//        }
//        perso.setDoAction(false);
//        switch (actionID) {
//            case 44://Sauvegarder a un zaap
//            case 81://V�rouiller maison
//            case 84://ouvrir maison
//            case 97://Acheter maison.
//            case 98://Vendre
//            case 104://Ouvrir coffre
//            case 105://Code coffre
//            case 108://Modifier prix de vente
//            case 157://Zaapi
//            case 121://Briser une ressource
//            case 181://Concasseur
//            case 110:
//            case 153:
//            case 183:
//                break;
//            case 42://Tas de patate
//                if (object == null)
//                    return;
//                object.setState(JobConstant.IOBJECT_STATE_EMPTY);
//                object.setInteractive(false);
//                object.disable();
//                SocketManager.GAME_SEND_GDF_PACKET_TO_MAP(perso.getCurMap(), cellId, object);
//                int qua = Formulas.getRandomValue(1, 5);
//                GameObject obj = World.world.getObjTemplate(537).createNewItem(qua, false);
//                if (perso.addItem(obj, true, false))
//                    World.world.addGameObject(obj);
//                SocketManager.GAME_SEND_IQ_PACKET(perso, perso.getId(), qua);
//                break;
//            case 102://Puiser
//                if (object == null)
//                    return;
//
//                object.setState(JobConstant.IOBJECT_STATE_EMPTY);
//                object.setInteractive(false);
//                object.disable();
//                SocketManager.GAME_SEND_GDF_PACKET_TO_MAP(perso.getCurMap(), cellId, object);
//                qua = Formulas.getRandomValue(1, 10);//On a entre 1 et 10 eaux
//                obj = World.world.getObjTemplate(311).createNewItem(qua, false);
//                if (perso.addItem(obj, true, false))
//                    World.world.addGameObject(obj);
//                SocketManager.GAME_SEND_IQ_PACKET(perso, perso.getId(), qua);
//                break;
//        }
//    }

    public void tryDropItem(FullItem obj) {
        if(map.droppedItems.putIfAbsent(cellId, obj) != null) {
            throw new IllegalStateException("attempted to drop item on a cell that already has a dropped item");
        }
    }

    public boolean blockLoS() {
        if (getFighters() == null)
            return map.data.lineOfSight(cellId);
        boolean hide = true;
        for (Fighter fighter : getFighters())
            if (!fighter.isHidden())
                hide = false;
        return map.data.lineOfSight(cellId) && hide;
    }
}