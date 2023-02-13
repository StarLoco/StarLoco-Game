package org.starloco.locos.area.map;

import java.util.List;

/**
 * Created by Locos on 21/12/2017.
 */
public interface CellCache {

    /**
     * @param cellId id of the cell
     * @return if the cell is walkable or not
     */
    boolean isWalkable(short cellId);

    /**
     *
     * @param cellId id of the center
     * @return all cells directly available and walkable from the given cellId
     */
    List<Short> getDirectWalkable(short cellId);

    /**
     * Check whether a cell is on the bound or not
     * @param cellId
     * @return true if cell is on one bound of the map false otherwise
     */
    boolean isOutOfMap(short cellId);


    float getHeight(short cell);

    boolean blockLineOfSight(short c);


    short getX(int cellId);
    short getY(int cellId);
    short getCellId(int x, int y);

    default int getOrthX(int cellID) {
        int x = getX(cellID);
        return Math.abs(getOrthY(cellID) - x) + x + getY(cellID)%2;
    }

    default int getOrthY(int cellID) {
        return Math.round(-getY(cellID)/ 2f) + getX(cellID);
    }

    int getOrthCellID(int x, int y);

    default short getCellsDistance(short c1, short c2) {
        return (short)
                (Math.abs( getOrthX(c1) - getOrthX(c2))
                        + Math.abs( getOrthY(c1) - getOrthY(c2)));
    }
}