package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.auction.Auction;
import org.starloco.locos.auction.AuctionManager;
import org.starloco.locos.client.Player;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.object.GameObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Locos on 03/02/2018.
 */
public class AuctionData extends FunctionDAO<Auction> {

    public AuctionData(HikariDataSource dataSource) {
        super(dataSource, "auctions");
    }

    @Override
    public void loadFully() {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM " + getTableName() + ";");
            while (result.next()) {
                final Player player = World.world.getPlayer(result.getInt("owner"));
                final GameObject object = World.world.getGameObject(result.getInt("object"));

                if(object == null || player == null) {
                    final Auction auction = new Auction(result.getInt("price"), player, new GameObject(result.getInt("object"), -1, 1, -1, "", 0), result.getByte("retry"));
                    this.delete(auction);
                } else {
                    final Auction auction = new Auction(result.getInt("price"), player, object, result.getByte("retry"));
                    AuctionManager.getInstance().getAuctions().add(auction);
                }
            }
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
    }

    @Override
    public Auction load(int id) {
        return null;
    }

    @Override
    public boolean insert(Auction entity) {
        PreparedStatement p = null;
        boolean ok = true;
        try {
            p = getPreparedStatement("INSERT INTO " + getTableName() + "(`price`, `owner`, `object`, `retry`) VALUES ('" +
                    entity.getPrice() + "', '" +
                    entity.getOwner().getId() + "', '" +
                    entity.getObject().getGuid() + "', '" +
                    entity.getRetry() + "');");
            execute(p);
        } catch (SQLException e) {
            ok = false;
            super.sendError(e);
        } finally {
            close(p);
        }
        return ok;
    }

    @Override
    public void delete(Auction entity) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("DELETE FROM " + getTableName() + " WHERE `object` = ?;");
            p.setInt(1, entity.getObject().getGuid());
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    @Override
    public void update(Auction entity) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET `retry` = ? WHERE `owner` = ? AND `object` = ?;");
            p.setByte(1, entity.getRetry());
            p.setInt(2, entity.getOwner().getId());
            p.setInt(3, entity.getObject().getGuid());
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    @Override
    public Class<?> getReferencedClass() {
        return AuctionData.class;
    }
}
