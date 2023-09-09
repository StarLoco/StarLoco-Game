package org.starloco.locos.area.map;

public final class OrthogonalProj {
    public static int getX(int w, int c) { return (int)Math.floor(c % (w-.5)); }

    public static int getY(int w, int c) { return (int) (c / (w-.5));}

    public static int getOrthX(int w, int cellId) {
        int x = getX(w, cellId);
        return Math.abs(getOrthY(w, cellId) - x) + x + getY(w, cellId)%2;
    }

    public static int getOrthY(int w, int cellId) {
        return Math.round(-getY(w, cellId)/ 2f) + getX(w, cellId);
    }

    public static short getCellId(int w, int x, int y) {
        return (short) Math.round(x + y * (w-.5));
    }

    public static int getCellsDistance(int w, int c1, int c2) {
        return (Math.abs( getOrthX(w, c1) - getOrthX(w, c2))
                        + Math.abs( getOrthY(w, c1) - getOrthY(w, c2)));
    }

    public static int getOrthCellID(int x, int y) {
        // TODO: return orthogonalProjection.get(getProjectionMapKey(x,y));
        return 0;
    }
}
