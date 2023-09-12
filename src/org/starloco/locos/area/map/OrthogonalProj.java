package org.starloco.locos.area.map;

public final class OrthogonalProj {
    public static int getOrthX(int w, int cellId) {
        return (cellId + getOrthY(w, cellId)*(w-1)) / w;
    }
    public static int getOrthXFromY(int w, int cellId, int y) {
        return (cellId + y*(w-1)) / w;
    }

    public static int getOrthY(int w, int cellId) {
        int t = ((w<<1) - 1);
        int lineNb = cellId / t;
        int lineOff = (cellId%t) % w;

        return lineOff - lineNb;
    }

    public static short getCellId(int w, int x, int y) {
        return (short) Math.round(x + y * (w-.5));
    }

    public static int getCellsDistance(int w, int c1, int c2) {
        int y1 = getOrthY(w, c1);
        int y2 = getOrthY(w, c2);

        return (Math.abs( getOrthXFromY(w, c1, y1) - getOrthXFromY(w, c2, y2))
                        + Math.abs( y1 - y2));
    }

    public static int getOrthCellID(int w, int x, int y) {
        int t = (w<<1)-1;
        int diff = x-y;
        int sum = x+y;
        return (diff>>1) * t + (diff%2 * w) + (sum>>1);
    }

    // Check is a cellId is on the edge of the grid. Those cells are usually inactive
    public static boolean isEdgeCell(int w, int h, int cellId) {
        int y = getOrthY(w, cellId);
        int x = getOrthXFromY(w, cellId, y);
        return isEdgeCellOrth(w, h, x, y);
    }


    // Check is a cellId is on the edge of the grid using orthogonal coordinates
    public static boolean isEdgeCellOrth(int w, int h, int x, int y) {
        int diff = x-y;
        int sum = x+y;

        return diff == 0        // Top
                || sum == (w-1)<<1  // Right
                || diff == (h-1) << 1 // Bottom
                || sum == 0           // Left
                ;
    }
}
