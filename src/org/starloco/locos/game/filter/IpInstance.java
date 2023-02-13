package org.starloco.locos.game.filter;

class IpInstance {

    /**
     * Declaring config *
     */
    private int connections;
    private long lastConnection;
    private boolean banned;

    public IpInstance() {
        this.connections = 0;
    }

    public void addConnection() {
        this.connections++;
    }

    public void resetConnections() {
        this.connections = 0;
    }

    public void updateLastConnection() {
        this.lastConnection = System.currentTimeMillis();
    }

    public void ban() {
        this.banned = true;
    }

    public boolean isBanned() {
        return this.banned;
    }

    public long getLastConnection() {
        return lastConnection;
    }

    public int getConnections() {
        return connections;
    }
}
