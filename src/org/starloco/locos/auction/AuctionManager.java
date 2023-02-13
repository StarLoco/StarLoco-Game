package org.starloco.locos.auction;

import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.client.Account;
import org.starloco.locos.client.Player;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.game.AuctionData;
import org.starloco.locos.entity.exchange.PlayerExchange;
import org.starloco.locos.entity.npc.Npc;
import org.starloco.locos.game.scheduler.Updatable;
import org.starloco.locos.game.world.World;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.util.TimerWaiter;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by Locos on 31/01/2018.
 */
public class AuctionManager extends Updatable {

    //region static
    private static AuctionManager instance;

    public static AuctionManager getInstance() {
        return instance == null ? instance = new AuctionManager() : instance;
    }
    //endregion

    private final LinkedList<Auction> auctions = new LinkedList<>();
    private Auction current;
    private final GameMap map;
    private final Npc npc;
    private ScheduledFuture task;
    private byte counter;

    public AuctionManager() {
        super(10000);
        this.map = World.world.getMap((short) 10111);
        this.npc = this.map.getNpcByTemplateId(9605);
    }

    public List<Auction> getAuctions() {
        return auctions;
    }

    public void talk(String key, GameObject object, boolean tradeTalk, Object... params) {
        for(Player player : this.map.getPlayers()) {
            String msg = player.getLang().trans(key, params);
            if(object != null) msg = getTalkStringObject(object, msg);
            player.send("cMK|" + npc.getId() + "|Commissaire|" + msg + "|");
        }
        if(tradeTalk) {
            for (Player player : World.world.getOnlinePlayers()) {
                String msg = player.getLang().trans(key, params);
                if (object != null) msg = getTalkStringObject(object, msg);
                player.send("cMK:|" + npc.getId() + "|Commissaire|" + msg + "|");
            }
        }
    }

    public void talkNext() {
        for(Player player : this.map.getPlayers()) {
            String msg = player.getLang().trans("game.auction.auctionmanager.stop.none")+ (this.auctions.size() == 0 ? "" : player.getLang().trans("game.auction.auctionmanager.stop.next"));
            player.send("cMK|" + npc.getId() + "|Commissaire|" + msg + "|");
        }
        for(Player player : World.world.getOnlinePlayers()) {
            String msg = player.getLang().trans("game.auction.auctionmanager.stop.none")+ (this.auctions.size() == 0 ? "" : player.getLang().trans("game.auction.auctionmanager.stop.next"));
            player.send("cMK:|" + npc.getId() + "|Commissaire|" + msg + "|");
        }
    }

    public void talk(Player player, String msg) {
        player.send("cMK|" + npc.getId() + "|Commissaire|" + msg + "|");
    }

    private String getTalkStringObject(GameObject object, String msg) {
        return "°0" + msg + "|" + object.getTemplate().getId() + "!" + object.parseStatsString();
    }

    private boolean currentIsAvailable() {
        return auctionIsAvailable(current);
    }

    private boolean auctionIsAvailable(Auction auction) {
        boolean available = auction != null && auction.getOwner() != null && auction.getObject() != null;
        if(!available) System.err.println(auction == null ? "AuctionM : current is null" : "AuctionM : " + auction.getObject() + " " + auction.getOwner() + " ");
        return available;
    }

    @Override
    public void update() {
        if(this.verify()) {
            Date date = Calendar.getInstance().getTime();
            int hour = Integer.parseInt(new SimpleDateFormat("HH").format(date));

            if (hour >= 16 && hour < 23) {
                if (current == null) {
                    this.current = this.auctions.pollFirst();

                    if (current != null && currentIsAvailable()) {
                        this.task = TimerWaiter.addNext(() -> check(null, -1), 0, TimeUnit.MILLISECONDS);
                    } else {
                        this.current = null;
                    }
                }
            }
        }
    }

    private synchronized void check(final Player player, int kamas) {
        if(current != null && currentIsAvailable()) {
            if(!this.start(player, kamas))
                if(!this.newAuction(player, kamas))
                    if(!this.counter(player, kamas))
                        this.stop();
        }
    }

    private boolean start(Player player, int kamas) {
        if (kamas == -1 && currentIsAvailable() && player != null) { // Lancement de l'enchère
            this.talk("game.auction.auctionmanager.start", current.getObject(), true, current.getObject().getQuantity(), current.getPrice(), current.getOwner().getName());
            this.counter = 0;
            this.task = TimerWaiter.addNext(() -> check(player, 0), 8, TimeUnit.SECONDS);
            return true;
        }
        return false;
    }

    private boolean newAuction(Player player, int kamas) {
        if (kamas > 0 && kamas != current.getPrice()) {
            if(current.getCustomer() != null) {
                current.getCustomer().addKamas(current.getPrice());
                SocketManager.GAME_SEND_STATS_PACKET(current.getCustomer());
            }
            counter = 0;
            current.setPrice(kamas);
            current.setCustomer(player);

            int newPrice = (int) (this.current.getPrice() * 0.05) + this.current.getPrice();

            this.talk("game.auction.auctionmanager.newAuction", current.getObject(), true, current.getObject().getQuantity(), current.getPrice(), current.getCustomer().getName(), newPrice);
            this.task = TimerWaiter.addNext(() -> check(player, 0), 8, TimeUnit.SECONDS);
            return true;
        }
        return false;
    }

    private boolean counter(Player player, int kamas) {
        if (counter == 0 || counter == 1) {
            if(this.currentIsAvailable()) {
                if (current.getCustomer() != null)
                    this.talk("game.auction.auctionmanager.counter.1", current.getObject(), false, current.getObject().getQuantity(), current.getPrice(), current.getCustomer().getName(), (counter + 1));
                else
                    this.talk("game.auction.auctionmanager.counter.2", current.getObject(), false, current.getObject().getQuantity(), current.getPrice(), (counter + 1));
                this.counter++;
                this.task = TimerWaiter.addNext(() -> check(player, kamas), 8, TimeUnit.SECONDS);
                return true;
            }
        }
        counter = 0;
        return false;
    }

    private void stop() {
        if(this.currentIsAvailable()) {
            Player target = this.current.getCustomer();
            final GameObject object = this.current.getObject();

            if (target != null) {
                target.addInBank(object.getGuid(), object.getQuantity(), true);
                target.send("M121");

                final Account owner = this.current.getOwner().getAccount();
                owner.setBankKamas(owner.getBankKamas() + this.current.getPrice());
                if(owner.getCurrentPlayer() != null)
                    owner.getCurrentPlayer().send("Im065;" + this.current.getPrice() + "~" + object.getTemplate().getId());
                ((AuctionData) DatabaseManager.get(AuctionData.class)).delete(this.current);
                this.talk("game.auction.auctionmanager.stop.felicitation", null, false, target.getName());
            } else {
                if (this.current.getRetry() >= 3) {
                    target = this.current.getOwner();
                    if (target.isOnline())
                        target.send("Im067");
                    target.addInBank(object.getGuid(), object.getQuantity(), true);
                    ((AuctionData) DatabaseManager.get(AuctionData.class)).delete(this.current);
                } else {
                    this.current.incRetry();
                    this.auctions.addLast(this.current);
                    ((AuctionData) DatabaseManager.get(AuctionData.class)).update(this.current);
                }
                if(target != null)
                    this.talkNext();
            }
        }
        this.current = null;
    }

    @Override
    public Object get() {
        return null;
    }

    public void onPlayerLoadMap(Player player) {
        if(!this.isValid(player)) return;

        if(this.current != null && this.currentIsAvailable()) {
            Date date = Calendar.getInstance().getTime();
            int hour = Integer.parseInt(new SimpleDateFormat("HH").format(date));
            if (hour >= 16 && hour < 23) {
                player.sendTypeMessage("Auction", player.getLang().trans("game.auction.auctionmanager.encherie.infos"));
            }
            if(this.current.getCustomer() == null) {
                String msg = player.getLang().trans("game.auction.auctionmanager.start", current.getObject().getQuantity(), current.getPrice(), current.getOwner().getName());
                this.talk(player, getTalkStringObject(current.getObject(), msg));
            } else {
                String msg = player.getLang().trans("game.auction.auctionmanager.newAuction", current.getObject().getQuantity(), current.getPrice(), current.getCustomer().getName(), "");
                this.talk(player, getTalkStringObject(current.getObject(), msg));
            }
        }
    }

    public synchronized void onPlayerChat(Player player, String msg) {
        if(!this.isValid(player)) return;
        if(this.current != null && this.currentIsAvailable() && (msg.startsWith("moi") || msg.startsWith("me"))) {
            try {
                if(player.getId() == this.current.getOwner().getId()) return;
                msg = msg.replace("|", "");

                int percent = (int) (this.current.getPrice() * 0.05);
                int price = (percent <= 1 ? 1 : percent) + this.current.getPrice();

                if(msg.length() > 4 && msg.contains(" ")) {
                    String[] split = msg.split(" ");
                    if(split.length == 2) {
                        try {
                            if(Integer.parseInt(split[1]) > price)
                                price = Integer.parseInt(split[1]);
                        } catch(Exception ignored) {}
                    }
                }

                if(player.getKamas() < price) {
                    player.send("Im063");
                    return;
                }

                player.addKamas(-price);
                player.sendTypeMessage("Auction", player.getLang().trans("game.auction.auctionmanager.encherie",  price));
                SocketManager.GAME_SEND_STATS_PACKET(player);
                if(this.task != null) this.task.cancel(true);
                int p = price;
                this.task = TimerWaiter.addNext(() -> this.check(player, p), 100, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onPlayerCommand(Player player, String[] info) {
        if(!this.isValid(player)) return;
        if(player.getExchangeAction() != null) {
            PlayerExchange.NpcExchange exchange = ((PlayerExchange.NpcExchange) player.getExchangeAction().getValue());

            if(info.length > 1) {
                if (info[1].equalsIgnoreCase("show")) {
                    int count = 0;
                    for (Auction auction : this.auctions) {
                        if(!this.auctionIsAvailable(auction)) continue;
                        if (count == 100) break;
                        count++;

                        GameObject object = auction.getObject();
                        String str = object.getGuid() + "|" + object.getQuantity() + "|" + object.getTemplate().getId() + "|" +
                                auction.getObject().parseStatsString() + ",3db#0#0#0#" + auction.getOwner().getName() + ",c2#" + Integer.toHexString(auction.getPrice()) + "#0#0#0";

                        TimerWaiter.addNext(() -> SocketManager.GAME_SEND_EXCHANGE_OTHER_MOVE_OK(player.getGameClient(), 'O', "+", str), count * 100);
                    }
                    player.sendTypeMessage("Auction", player.getLang().trans("game.auction.auctionmanager.waitingqueue", auctions.size()));
                } else {
                    try {
                        if (exchange != null) {
                            int price = Integer.parseInt(info[1]);
                            if (price < 10 || price > 1_000_000) throw new Exception();

                            if(this.auctions.stream().filter(auction -> auction.getOwner() != null && auction.getOwner().getId() == player.getId()).count() >= 3) {
                                player.sendTypeMessage("Auction", player.getLang().trans("game.auction.auctionmanager.too.much.items"));
                            }

                            final Auction auction = new Auction(price, player, null, (byte) 0);
                            exchange.setAuction(auction);
                            if (exchange.isPlayerOk())
                                exchange.toogleOK(true);
                            player.sendTypeMessage("Auction", player.getLang().trans("game.auction.auctionmanager.confirm", price));
                        }
                    } catch (Exception e) {
                        player.sendTypeMessage("Auction", player.getLang().trans("game.auction.auctionmanager.price"));
                    }
                }
            }
        }
    }

    public void onPlayerOpenExchange(Player player, PlayerExchange.NpcExchange exchange) {
        if(!this.isValid(player)) return;

        player.sendTypeMessage("Auction", player.getLang().trans("game.auction.auctionmanager.infos.1"));
        player.sendTypeMessage("Auction", player.getLang().trans("game.auction.auctionmanager.infos.2"));
        player.sendTypeMessage("Auction", player.getLang().trans("game.auction.auctionmanager.infos.3"));
    }

    public boolean onPlayerChangeItemInNpcExchange(Player player, GameObject object) {
        if(!this.isValid(player)) return false;
        if(object != null && object.getTemplate() != null && object.getTemplate().getLevel() <= 50) return true;
        if(player.getExchangeAction() != null) {
           PlayerExchange.NpcExchange exchange = ((PlayerExchange.NpcExchange) player.getExchangeAction().getValue());
           if(exchange != null) {
               if (exchange.getItems1().size() >= 1) return true;
               else player.sendTypeMessage("Auction", player.getLang().trans("game.auction.auctionmanager.enterprice"));
           }
        }
        return false;
    }

    public boolean onPlayerAccept(Player player, PlayerExchange.NpcExchange exchange) {
        if(player != null)if(!this.isValid(player)) return false;
        if(exchange != null) {
            final Auction auction = exchange.getAuction();

            if (auction != null && exchange.getItems1().size() == 1) {
                if(player == null) {
                    exchange.toogleOK(true);
                    ((AuctionData) DatabaseManager.get(AuctionData.class)).insert(auction);
                    return true;
                } else {
                    this.auctions.addLast(auction);
                    player.sendTypeMessage("Auction", player.getLang().trans("game.auction.auctionmanager.deposit"));
                    return false;
                }
            }
        }
        return false;
    }

    private boolean isValid(Player player) {
        if(!this.map.getPlayers().contains(player)) return false;
        if(player.getFight() != null || player.isDead() == 1 || player.isGhost()
                || (player.getExchangeAction() != null && !(player.getExchangeAction().getValue() instanceof PlayerExchange.NpcExchange))) {
            player.sendTypeMessage("Auction", player.getLang().trans("game.auction.auctionmanager.isvalid"));
            return false;
        }
        return true;
    }
}
