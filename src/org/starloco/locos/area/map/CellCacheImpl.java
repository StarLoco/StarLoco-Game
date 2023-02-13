package org.starloco.locos.area.map;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by guillaume on 06/12/14
 * Modified by Diabu on 27/05/16
 */
public class CellCacheImpl implements CellCache {
    private final static HashMap<String,Int2IntArrayMap> orthogonalProjections = new HashMap<>();

    //private final List<Short> nonWalkableCells;
    private final List<Short> lineBlocker;
    private final short width,height;
    //private final Short2ShortAVLTreeMap groundSlope, groundLevel;
    private final Int2IntArrayMap orthogonalProjection;

    public CellCacheImpl(/*List<Short> nonWalkableCells,*/ List<Short> lineBlocker, short width, short height/*, Short2ShortAVLTreeMap groundSlope, Short2ShortAVLTreeMap groundLevel*/) {
        //this.nonWalkableCells = nonWalkableCells;
        this.lineBlocker = lineBlocker;
        this.width = width;
        this.height = height;
        //this.groundLevel = groundLevel;
        //this.groundSlope = groundSlope;

        String projKey = width+"_"+height;
        Int2IntArrayMap proj = orthogonalProjections.getOrDefault(projKey, null);
        if(proj == null) {
            proj = new Int2IntArrayMap();
            // For each cellID, get OrthX & OrthY to compute the Key and cache the result
            for(int c=0;c<width * height + (width-1) * (height-1);c++)
                proj.put(getProjectionMapKey(getOrthX(c),getOrthY(c)), c);

            orthogonalProjections.put(projKey, proj);
        }
        orthogonalProjection = proj;
    }

    public boolean isWalkable(short cellId){
        return false;//!nonWalkableCells.contains(cellId) && !isOutOfMap(cellId);
    }

    @Override
    public List<Short> getDirectWalkable(short cellId) {
        List<Short> s = new LinkedList<>();
        if(isWalkable((short)(cellId+1)))s.add((short)(cellId+1));
        if(isWalkable((short)(cellId-1)))s.add((short)(cellId-1));
        if(isWalkable((short)(cellId+width)))s.add((short)(cellId+width));
        if(isWalkable((short)(cellId-width)))s.add((short)(cellId-width));
        if(isWalkable((short)(cellId+width)))s.add((short)(cellId+width-1));
        if(isWalkable((short)(cellId-width)))s.add((short)(cellId-width+1));
        if(isWalkable((short)(cellId+width*2-1)))s.add((short)(cellId+width*2-1));
        if(isWalkable((short)(cellId-width*2+1)))s.add((short)(cellId-width*2+1));
        return s;
    }

    public boolean isOutOfMap(short cellId){
        short doubleWidthMinusOne = (short) (width*2-1);
        if(cellId < width) return true; //cases du haut
        if(cellId >= (height - 1) * doubleWidthMinusOne) return true; //cases du bas
        short mod = (short) (cellId % doubleWidthMinusOne);
        if (mod == 0) return true; //cases de gauche
        if (mod == (width - 1)) return true; //cases de droite
        return false;
    }

    @Override
    public float getHeight(short cell) {
        short res = 0;
        /*short t = groundSlope.getOrDefault(cell, (short) -1);
        if (t != -1 && t != 1)
            res += 0.5;
        t = groundLevel.getOrDefault(cell, (short) -1);
        if (t != -1)
            res += t - 7;*/
        return res;
    }

    @Override
    public boolean blockLineOfSight(short c) {
        return !lineBlocker.contains(c);
    }

    @Override
    public short getX(int c) { return (short)Math.floor(c % (width-.5)); }

    @Override
    public short getY(int c) { return (short) (c / (width-.5));}

    @Override
    public short getCellId(int x, int y) {
        return (short) Math.round(x + y * (width-.5));
    }

    @Override
    public int getOrthCellID(int x, int y) {
        return orthogonalProjection.get(getProjectionMapKey(x,y));
    }

    // width + height - 2 is maxX (=width)
    private int getProjectionMapKey(int x, int y) { return y*(width + height - 2) + x; }
}