package org.starloco.locos.event;

import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.client.Player;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.login.EventData;
import org.starloco.locos.event.type.Event;
import org.starloco.locos.game.scheduler.Updatable;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.util.TimerWaiter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created by Locos on 02/10/2016.
 */
public class EventManager extends Updatable {

    public final static int TOKEN = 50007, NPC = 16000;
    private final static EventManager singleton = new EventManager();

    public static EventManager getInstance() {
        return singleton;
    }

    public enum State {
        WAITING, INITIALIZE, PROCESSED, STARTED, FINISHED
    }

    /** EventManager **/

    private final Event[] events;
    private State state = State.WAITING;
    private Event current, lastest;
    private short count = 0;
    private final List<Player> participants = new ArrayList<>();

    private EventManager() {
        super(60000);
        this.events = ((EventData) DatabaseManager.get(EventData.class)).load();
    }

    public Event[] getEvents() {
        return events;
    }

    public State getState() {
        return state;
    }

    public Event getCurrentEvent() {
        return current;
    }

    public List<Player> getParticipants() {
        return participants;
    }

    public void startNewEvent(Event temp) {
        Event event = temp == null ? this.events[Formulas.random.nextInt(this.events.length)] : temp;

        if(event != null) {
            if(this.events.length > 1 && this.lastest != null && event.getEventId() == this.lastest.getEventId()) {
                this.startNewEvent(null);
                return;
            }

            event.prepare();
            this.lastTime = System.currentTimeMillis();
            this.current = event;

            if(this.current.getMaxPlayers() == -1) {
                World.world.sendMessageToAll("event.eventmanager.start.wait", event.getEventName());
                this.state = State.STARTED;
                TimerWaiter.addNext(() -> this.current.perform(), 0, TimeUnit.SECONDS);
            } else {
                this.state = State.PROCESSED;
                World.world.sendMessageToAll("event.eventmanager.start.now", event.getEventName());
            }
        } else {
            this.startNewEvent(null);
        }
    }

    private synchronized void startCurrentEvent() {
        if(this.state == State.STARTED)
            return;
        this.state = State.STARTED;

        if(!this.hasEnoughPlayers()) {
            this.count = 0;
            this.lastTime = System.currentTimeMillis();
            this.state = State.PROCESSED;
        } else if(this.moveAllPlayersToEventMap(true)) {
            this.lastTime = System.currentTimeMillis();
            TimerWaiter.addNext(() -> this.current.perform(), 0, TimeUnit.SECONDS);
        }
    }

    public void finishCurrentEvent() {
        this.participants.stream().filter(Objects::nonNull).forEach(player -> {
            player.teleportOldMap();
            player.setBlockMovement(false);
        });

        this.lastest = this.current;
        this.current = null;
        this.lastTime = System.currentTimeMillis();
        this.count = 0;
        this.state = State.WAITING;
    }

    public synchronized byte subscribe(final Player player) {
        if(this.current == null || this.state == State.WAITING) {
            return 0;
        } else {
            if(this.state == State.PROCESSED) {
                if (this.participants.size() >= this.current.getMaxPlayers()) {
                    player.sendMessage(player.getLang().trans("event.eventmanager.subscribe.full", this.current.getEventName()));
                } else if (this.participants.contains(player)) {
                    this.participants.remove(player);
                    player.sendMessage(player.getLang().trans("event.eventmanager.unsubscribe", this.current.getEventName()));
                } else if (this.hasSameIP(player)) {
                    player.sendMessage(player.getLang().trans("event.eventmanager.subscribe.already.reseau"));
                } else {
                    this.participants.add(player);
                    player.sendMessage(player.getLang().trans("event.eventmanager.subscribe.already", this.current.getEventName()));

                    if (this.participants.size() >= this.current.getMaxPlayers()) {
                        this.startCurrentEvent();
                    } else {
                        this.participants.forEach(target -> target.sendMessage(target.getLang().trans("event.eventmanager.wait.player", this.current.getMaxPlayers() - this.participants.size())));
                    }
                }
            } else {
                player.sendMessage(player.getLang().trans("event.eventmanager.start.already", this.current.getEventName()));
            }
        }
        return 1;
    }

    private boolean hasSameIP(Player player) {
        if(player != null && player.getAccount() != null) {
            final String ip = player.getAccount().getCurrentIp();

            if(ip.equals("127.0.0.1"))
                return false;
            for (Player target : this.participants) {
                if (target != null && target.getAccount() != null) {
                    return ip.equals(target.getAccount().getCurrentIp());
                }
            }
        }
        return false;
    }

    private boolean hasEnoughPlayers() {
        if(this.current == null)
            return false;
        short percent = (short) ((100 * this.participants.size()) / this.current.getMaxPlayers());
        return percent >= 30;
    }

    @Override
    public void update() {
        if(Config.modeEvent && this.verify()) {
            if (this.state == State.WAITING) {
                short result = (short) (Config.timeBetweenEvent - (++count));
                if (result == 0) {
                    this.count = 0;
                    this.lastTime = System.currentTimeMillis();
                    this.state = State.INITIALIZE;
                    TimerWaiter.addNext(() -> startNewEvent(null), 0, TimeUnit.SECONDS);
                } else if (result == 60 || result == 30 || result == 15 || result == 5) {
                    World.world.sendMessageToAll("event.eventmanager.start", Short.toString(result));
                }
            } else if (this.state == State.PROCESSED) {
                short result = (short) ((this.hasEnoughPlayers() ? 5 : 10) - (++count));
                this.moveAllPlayersToEventMap(false);

                if (result <= 0) {
                    this.startCurrentEvent();
                } else if(result == 1 && this.hasEnoughPlayers()) {
                    for(Player player : this.participants) {
                        player.sendMessage(player.getLang().trans("event.eventmanager.start", "1"));
                    }
                }
            }
        }
    }

    @Override
    public Object get() {
        return lastTime;
    }

    private boolean moveAllPlayersToEventMap(boolean teleport) {
        boolean ok = true;
        final StringBuilder afk = teleport ? new StringBuilder() : null;

        final Iterator<Player> iterator1 = this.participants.iterator();

        while(iterator1.hasNext()) {
            final Player player = iterator1.next();
            if(player.getFight() != null || !player.isOnline() || player.isGhost() || player.getDoAction()) {
                ok = false;
                iterator1.remove();
                player.sendMessage(player.getLang().trans("event.eventmanager.player.unavailable"));
                player.sendMessage(player.getLang().trans("event.eventmanager.player.eject"));

                if(teleport) {
                    afk.append(afk.length() == 0 ? ("<b>" + player.getName() + "</b>") : (", <b>" + player.getName() + "</b>"));
                }
            }
        }

        if(!ok || !teleport) {
            if(teleport) {
                this.participants.forEach(player -> player.sendMessage(player.getLang().trans("event.eventmanager.player.afk",afk.toString())));
                World.world.getOnlinePlayers().stream().filter(target -> !afk.toString().contains(target.getName()))
                        .forEach(target -> target.sendMessage(target.getLang().trans("event.eventmanager.subscribe.last.secondes",this.current.getEventName())));
            }
            return false;
        }

        final Iterator<Player> iterator2 = this.participants.iterator();

        while(iterator2.hasNext()) {
            final Player player = iterator2.next();

            if(player.getFight() == null && player.isOnline() && !player.isGhost() && !player.getDoAction()) {
                player.setOldPosition();
                player.setBlockMovement(true);
                GameCase cell = this.current.getEmptyCellForPlayer(player);
                if(cell == null)
                    cell = this.current.getMap().getCase(this.current.getMap().getRandomFreeCellId());
                player.teleport(this.current.getMap().getId(), cell.getId());
                SocketManager.GAME_SEND_eD_PACKET_TO_MAP(this.current.getMap(), player.getId(), 4);
            } else {
                ok = false;
                iterator2.remove();
                player.sendMessage(player.getLang().trans("event.eventmanager.player.unavailable"));
                player.sendMessage(player.getLang().trans("event.eventmanager.player.eject"));
            }
        }

        return ok;
    }

    public static boolean isInEvent(Player player) {
        if(Config.modeEvent && EventManager.getInstance().state == State.STARTED)
            for(Player target : EventManager.getInstance().participants)
                if(target.getId() == player.getId())
                    return true;
        return false;
    }
}
