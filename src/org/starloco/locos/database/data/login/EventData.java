package org.starloco.locos.database.data.login;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.starloco.locos.database.data.FunctionDAO;
import org.starloco.locos.event.type.Event;
import org.starloco.locos.event.type.EventFindMe;
import org.starloco.locos.event.type.EventSmiley;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Locos on 02/10/2016.
 */
public class EventData extends FunctionDAO<Event> {

    public EventData(HikariDataSource dataSource) {
        super(dataSource, "world_event_type");
    }

    public Event[] load() {
        ResultSet result = null;
        Event[] events = new Event[this.getNumberOfEvent()];

        try {
            result = getData("SELECT * FROM " + getTableName() + ";");

            if (result != null) {
                byte i = 0;

                while (result.next()) {
                    Event event = this.getEventById(result.getByte("id"), result);

                    if (event != null) {
                        events[i] = event;
                        i++;
                    }
                }
            }
        } catch(SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
        return events;
    }

    private byte getNumberOfEvent() {
        ResultSet result = null;
        byte numbers = 0;

        try {
            result = getData("SELECT COUNT(id) AS numbers FROM " + getTableName() + ";");

            if (result != null) {
                result.next();
                numbers = result.getByte("numbers");
            }
        } catch(SQLException e) {
            super.sendError(e);
        } finally {
            close(result);
        }
        return numbers;
    }

    private Event getEventById(byte id, ResultSet result) throws SQLException {
        byte maxPlayers = result.getByte("maxPlayers");
        String name = result.getString("name");
        String description = result.getString("description");
        switch(id) {
            case 1: // Smiley
                //TODO: Remettre l'event smiley
                //return new EventSmiley(id, result.getByte("maxPlayers"), result.getString("name"), result.getString("description"));
            case 2: // Trouve-moi
                return new EventFindMe(id, maxPlayers, name, description);
            default:
                return null;
        }
    }

    @Override
    public void loadFully() {
        throw new NotImplementedException();
    }

    @Override
    public Event load(int id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean insert(Event entity) {
        throw new NotImplementedException();
    }

    @Override
    public void delete(Event entity) {
        throw new NotImplementedException();
    }

    @Override
    public void update(Event entity) {
        throw new NotImplementedException();
    }

    @Override
    public Class<?> getReferencedClass() {
        return EventData.class;
    }
}
