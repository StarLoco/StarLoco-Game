package org.starloco.locos.area.map;

import org.starloco.locos.util.Pair;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public interface CellsDataProvider {
    long cellData(int cellID);
    int overrideMask(int cellID);

    default boolean active(int cellID) {
        return (cellData(cellID) & 0x1L) != 0;
    }

    default boolean lineOfSight(int cellID) {
        return (cellData(cellID)  & 0x400000000000000L) != 0;
    }

    default int movement(int cellID) {
        return (int) ((cellData(cellID) >> 2) & 0x7);
    }

    default int groundLevel(int cellID) {
        return (int) ((cellData(cellID) >> 5) & 0xF);
    }

    default int groundSlope(int cellID) {
        return (int) ((cellData(cellID) >> 9) & 0xF);
    }

    default int ground(int cellID) {
        return (int) ((cellData(cellID) >> 13) & 0xF);
    }

    static long b64ToLong(byte[] data, int offset) {
        long active = (data[offset] & 0x20) >> 5;
        long lineOfSight = data[offset] & 0x1;
        long movement = (data[offset+2] & 0x38) >> 3;
        long groundLevel = data[offset+1] & 0xF;
        long groundSlope = (data[offset+4] & 0x3C) >> 2;
        long groundNum = ((data[offset] & 0x18) << 6) | ((data[offset+2] & 0x7) << 6) | (data[offset+3] & 0x3F);
        long groundFlip = ((data[offset+4] & 0x2) >> 1);
        long groundRot = ((data[offset+1] & 0x30) >> 4);
        long obj1Num = ((data[offset] & 0x4) << 11) | ((data[offset+4] & 0x1) << 12) | ((data[offset+5] & 0x3F) << 6) | (data[offset+6] & 0x3F);
        long obj1Flip = ((data[offset+7] & 0x8) >> 3);
        long obj1Rot = ((data[offset+7] & 0x30) >> 4);
        long obj2Num = ((data[offset] & 0x2) << 12) | ((data[offset+7] & 0x1) << 12) | ((data[offset+8] & 0x3F) << 6) | (data[offset+9] & 0x3F);
        long obj2Flip = (data[offset+7] & 0x4) >> 2;
        long obj2Int = (data[offset+7] & 0x2) >> 1;

        // Obj2Interactive-Obj2Flip-Obj2Num-Obj1Rot-Obj1Flip-Obj1Num-GndRot-GndFlip-GndNum-GndSlope-GndLevel-Movement-LoS-Active
        // Each line make the room it needs first, then OR its value in.
        // Code could be optimized, but consistency and ease of read win.
        long result = 0;
        result = (result << 1) | obj2Int;
        result = (result << 1) | obj2Flip;
        result = (result << 14) | obj2Num;
        result = (result << 2) | obj1Rot;
        result = (result << 1) | obj1Flip;
        result = (result << 14) | obj1Num;
        result = (result << 2) | groundRot;
        result = (result << 1) | groundFlip;
        result = (result << 11) | groundNum;
        result = (result << 4) | groundSlope;
        result = (result << 4) | groundLevel;
        result = (result << 3) | movement;
        result = (result << 1) | lineOfSight;
        result = (result << 1) | active;

        return result;
    }

    class RawCellsDataProvider implements CellsDataProvider {
        private final long[] data;

        public RawCellsDataProvider(byte[] data) {
            if(data.length%10 != 0) throw new InvalidParameterException("raw mapdata length is not a multiple of 10");
            this.data = new long[data.length/10];

            for(int i=0;i<data.length;i+=10) {
                this.data[i/10] = b64ToLong(data, i);
            }
        }

        @Override
        public long cellData(int cellID) {
            return data[cellID];
        }

        @Override
        public int overrideMask(int cellID) { return 0; }
    }

    class CellsDataOverride implements CellsDataProvider {
        private final RawCellsDataProvider base;
        private final List<Pair<Integer,Long>> overrides = new ArrayList<>();

        public CellsDataOverride(RawCellsDataProvider base) {
            this.base = base;
        }


        @Override
        public long cellData(int cellID) {
            long override = overrides.stream().filter(p -> p.first == cellID).findFirst().map(Pair::getSecond).orElse(0L);
            if(override==0) return base.cellData(cellID);
            return override;
        }

        @Override
        public int overrideMask(int cellID) {
            long diff = cellData(cellID) | base.cellData(cellID);
            if(diff == 0) return 0;

            // TODO: compute difference bitflags

            return 0;
        }
    }
}
