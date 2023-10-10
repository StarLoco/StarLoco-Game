package org.starloco.locos.database.data.game;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.entity.map.MountPark;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.game.world.World;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MountParkData extends FunctionDAO<MountPark> {
    public MountParkData(HikariDataSource dataSource) {
        super(dataSource, "mountpark_data");
    }

    @Override
    public void loadFully() {
        try {
            getData("SELECT * from mountpark_data", result -> {
                while (result.next()) {
                    int map = result.getInt("mapid");
                    MountPark park = World.world.getMountParks().get(map);
                    if (park == null) continue;
                    int owner = result.getInt("owner");
                    int guild = result.getInt("guild");
                    guild = World.world.getGuild(guild) != null ? guild : -1;
                    int price = result.getInt("price");
                    String data = result.getString("data");
                    String enclos = result.getString("enclos");
                    String objetPlacer = result.getString("ObjetPlacer");
                    String durabilite = result.getString("durabilite");

                    enclos = enclos.equals(" ") ? "" : enclos;
                    objetPlacer = objetPlacer.equals(" ") ? "" : objetPlacer;
                    durabilite = durabilite.equals(" ") ? "" : durabilite;

                    park.setData(owner, guild, price, data, objetPlacer, durabilite, enclos);
                }
            });
        } catch (SQLException e) {
            super.sendError(e);
        }
    }

    @Override
    public MountPark load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(MountPark entity) {
        PreparedStatement p = null;
        boolean ok = true;
        try {
            p = getPreparedStatement("INSERT INTO " + getTableName() + " (`mapid`, `owner`, `guild`, `price`, `data`, `enclos`, `ObjetPlacer`, `durabilite`) VALUES (?, ?, ?, ?, '', '', '', '');");
            p.setInt(1, entity.getMap());
            p.setInt(2, 0);
            p.setInt(3, -1);
            p.setInt(4, entity.getPriceBase());
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
    public void delete(MountPark entity) {
        throw new NotImplementedException();
    }

    @Override
    public void update(MountPark entity) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE " + getTableName() + " SET  `owner`=?, `guild`=?, `price` =?, `data` =?, `enclos` =?, `ObjetPlacer`=?, `durabilite`=? WHERE `mapid`=?;");
            p.setInt(1, entity.getOwner());
            p.setInt(2, (entity.getGuild() != null) ? entity.getGuild().getId() : -1);
            p.setInt(3, entity.getPrice());
            p.setString(4, entity.parseEtableToString());
            p.setString(5, entity.parseRaisingToString());
            p.setString(6, entity.getStringObject());
            p.setString(7, entity.getStringObjDurab());
            p.setInt(8, entity.getMap());
            execute(p);
        } catch (SQLException e) {
            super.sendError(e);
        } finally {
            close(p);
        }
    }

    @Override
    public Class<?> getReferencedClass() {
        return MountParkData.class;
    }

    public void exist(MountPark mountPark) {
        try {
            getData("SELECT * FROM " + getTableName() + " WHERE `mapid` = '" + mountPark.getMap() + "';", result -> {
                if (!result.next()) {
                    this.insert(mountPark);
                }
            });
        } catch (SQLException e) {
            super.sendError(e);
        }
    }
}
