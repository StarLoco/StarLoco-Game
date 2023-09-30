package org.starloco.locos.area.map;

import org.starloco.locos.common.CryptManager;
import org.starloco.locos.game.world.World;
import org.starloco.locos.util.Pair;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface CellsDataProvider {
    int cellCount();

    long cellData(int cellID);
    int overrideMask(int cellID);

    static int activeRaw(long cellData) {
        return (int) (cellData & 0x1);
    }

    static int lineOfSightRaw(long cellData) {
        return (int) ((cellData >> 1) & 0x1L);
    }

    static int movementRaw(long cellData) {
        return (int) ((cellData >> 2) & 0x7);
    }

    static int groundRotRaw(long cellData) {
        return (int) ((cellData >> 25) & 0x3);
    }

    static int groundLevelRaw(long cellData) {
        return (int) ((cellData >> 5) & 0xF);
    }

    static int groundSlopeRaw(long cellData) {
        return (int) ((cellData >> 9) & 0xF);
    }

    static int groundFlipRaw(long cellData) {
        return (int) ((cellData >>24) & 0x1);
    }

    static int groundNumRaw(long cellData) {
        return (int) ((cellData >> 13) & 0xF);
    }

    static int object1NumRaw(long cellData) {
        return (int) ((cellData >> 27) & 0x3FFF);
    }

    static int object1RotRaw(long cellData) {
        return (int) ((cellData>>42) & 0x3);
    }

    static int object1FlipRaw(long cellData) {
        return (int) ((cellData >> 41) & 1);
    }

    static int object2NumRaw(long cellData) {
        return (int) ((cellData >> 44) & 0x3FFF);
    }

    static int object2FlipRaw(long cellData) {
        return (int) ((cellData >> 58) & 1);
    }

    static int object2InteractiveRaw(long cellData) {
        return (int) ((cellData >> 59) & 0x1);
    }


    default boolean active(int cellID) {
        return activeRaw(cellData(cellID)) != 0;
    }

    default boolean lineOfSight(int cellID) {
        return lineOfSightRaw(cellData(cellID)) != 0;
    }

    default int movement(int cellID) {
        return movementRaw(cellData(cellID));
    }

    default int object2(int cellID) {
        return object2NumRaw(cellData(cellID));
    }

    default boolean object2Interactive(int cellID) {
        return object2InteractiveRaw(cellData(cellID)) != 0;
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
        long obj2Interactive = (data[offset+7] & 0x2) >> 1;

        // Obj2Interactive-Obj2Flip-Obj2Num-Obj1Rot-Obj1Flip-Obj1Num-GndRot-GndFlip-GndNum-GndSlope-GndLevel-Movement-LoS-Active
        // Each line make the room it needs first, then OR its value in.
        // Code could be optimized, but consistency and ease of read win.
        long result = 0;
        result = (result << 1) | obj2Interactive;   // Offset=59
        result = (result << 1) | obj2Flip;          // Offset=58
        result = (result << 14) | obj2Num;          // Offset=44
        result = (result << 2) | obj1Rot;           // Offset=42
        result = (result << 1) | obj1Flip;          // Offset=41
        result = (result << 14) | obj1Num;          // Offset=27
        result = (result << 2) | groundRot;         // Offset=25
        result = (result << 1) | groundFlip;        // Offset=24
        result = (result << 11) | groundNum;        // Offset=13
        result = (result << 4) | groundSlope;       // Offset=9
        result = (result << 4) | groundLevel;       // Offset=5
        result = (result << 3) | movement;          // Offset=2
        result = (result << 1) | lineOfSight;       // Offset=1
        result = (result << 1) | active;            // Offset=0

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
        public int cellCount() { return data.length; }

        @Override
        public long cellData(int cellID) {
            return data[cellID];
        }

        @Override
        public int overrideMask(int cellID) { return 0; }
    }

    class CellsDataOverride implements CellsDataProvider {
        private final RawCellsDataProvider base;

        // K: cellID, V: [overrides, modMask]
        private final HashMap<Integer, long[]> overrides = new HashMap<>();

        public CellsDataOverride(RawCellsDataProvider base) {
            this.base = base;
        }

        @Override
        public int cellCount() { return base.cellCount(); }

        @Override
        public long cellData(int cellID) {
            long[] override = overrides.get(cellID);
            long data = base.cellData(cellID);
            // We have an override
            if(override != null && override[1] != 0) {
                data &= ~ override[1];
                data |= override[0];
            }
            return data;
        }

        @Override
        public int overrideMask(int cellID) {
            long[] override = overrides.get(cellID);
            if(override == null) return 0;

            long ovMask = override[1];

            int clientMask = 0;
            if((ovMask & 0x0000000000000001L) != 0) clientMask |= 0x2000;   // active
            if((ovMask & 0x0000000000000002L) != 0) clientMask |= 0x1000;   // lineOfSight
            if((ovMask & 0x000000000000001CL) != 0) clientMask |= 0x0800;   // movement
            if((ovMask & 0x00000000000001E0L) != 0) clientMask |= 0x0400;   // groundLevel
            if((ovMask & 0x0000000000001E00L) != 0) clientMask |= 0x0200;   // groundSlope
            if((ovMask & 0x0000000000FFE000L) != 0) clientMask |= 0x0100;   // groundNum
            if((ovMask & 0x0000000001000000L) != 0) clientMask |= 0x0080;   // groundFlip
            if((ovMask & 0x0000000006000000L) != 0) clientMask |= 0x0040;   // groundRot
            if((ovMask & 0x000001FFF8000000L) != 0) clientMask |= 0x0020;   // obj1Num
            if((ovMask & 0x0000020000000000L) != 0) clientMask |= 0x0010;   // obj1Flip
            if((ovMask & 0x00000C0000000000L) != 0) clientMask |= 0x0008;   // obj1Rot
            if((ovMask & 0x03FFF00000000000L) != 0) clientMask |= 0x0004;   // obj2Num
            if((ovMask & 0x0400000000000000L) != 0) clientMask |= 0x0002;   // obj2Flip
            if((ovMask & 0x0800000000000000L) != 0) clientMask |= 0x0001;   // obj2Interactive

            return clientMask;
        }

        public String encodeCellData(int cellID) {
            long data = Optional.ofNullable(overrides.get(cellID))
                .map(a -> a[0])
                .orElse(base.cellData(cellID));

            int groundNum = groundNumRaw(data);
            int l1Num = object1NumRaw(data);
            int l2Num = object2NumRaw(data);


            return Stream.of(
                    (activeRaw(data) << 5) | ((groundNum & 0x600) >> 6) | ((l1Num & 0x2000) >> 11) | ((l2Num & 0x2000) >> 12) | lineOfSightRaw(data),
                    (groundRotRaw(data) << 4) | (groundLevelRaw(data)),
                    (movementRaw(data) << 3) | ((groundNum >> 6) & 0x7),
                    groundNum & 0x3F,
                    (groundSlopeRaw(data) << 2) | (groundFlipRaw(data) << 1) | ((l1Num >> 12) & 0x1),
                    (l1Num >>6) & 0x3F,
                    l1Num & 0x3F,
                    (object1RotRaw(data) << 4) | (object1FlipRaw(data) << 3) | (object2FlipRaw(data) << 2) | (object2InteractiveRaw(data) << 1) | ((l2Num >> 12) & 0x1),
                    (l2Num >> 6) & 0x3F,
                    l2Num & 0x3F
                )
                .map(CryptManager::getHashedValueByInt)
                .map(Object::toString)
                .collect(Collectors.joining());
        }


        private boolean setActive(int cellId, int val) {
            long[] ov = overrides.computeIfAbsent(cellId, (k) -> new long[2]);

            if(activeRaw(ov[0]) == val) return false;

            apply(ov, (val & 0x1) , 0x00000000000000001);

            // TODO Optimize: Detect when changing value to base value

            overrides.put(cellId, ov);
            return true;
        }

        private boolean setInteractive(int cellId, int val) {
            long[] ov = overrides.computeIfAbsent(cellId, (k) -> new long[2]);

            if(object2InteractiveRaw(ov[0]) == val) return false;

            apply(ov, ((long)(val & 0x1)) << 59 , 0x00800000000000000L);

            // TODO Optimize: Detect when changing value to base value

            overrides.put(cellId, ov);
            return true;
        }

        private boolean setMovement(int cellId, int val) {
            long[] ov = overrides.computeIfAbsent(cellId, (k) -> new long[2]);

            if(movementRaw(ov[0]) == val) return false;

            apply(ov, (val & 0xF) << 2, 0x000000000000001CL);

            // TODO Optimize: Detect when changing value to base value

            overrides.put(cellId, ov);
            return true;
        }

        private static void apply(long[] ov, long val, long mask) {
            // clear bits
            ov[0] &= ~mask;
            // Set bits
            ov[0] |= val;
            // Set mask
            ov[1] |= mask;
        }

        // Return true if it changed anything
        public boolean applyOverrides(int cellId, Map<String, Integer> overrides) {
            if(overrides.isEmpty()) return false;

            Iterator<Map.Entry<String,Integer>> it = overrides.entrySet().iterator();
            boolean changed = false;
            while(it.hasNext()) {
                Map.Entry<String,Integer> e = it.next();
                switch(e.getKey().toLowerCase()) {
                    case "active":
                        changed = setActive(cellId, e.getValue());
                        break;
                    case "interactive":
                        changed = setInteractive(cellId, e.getValue());
                        break;
                    case "movement":
                        changed = setMovement(cellId, e.getValue());
                        break;
                    default:
                        World.world.logger.warn("ignoring unknown cell override '{}'", e.getKey());
                }
            }
            return changed;
        }

        // Return true if it changed anything
        public boolean removeOverrides(int cellId, Map<String, Integer> overrides) {
            if(overrides.isEmpty()) return false;

            Iterator<String> it = overrides.keySet().iterator();
            boolean changed = false;
            while(it.hasNext()) {
                String key = it.next();
                switch(key.toLowerCase()) {
                    case "active":
                        changed = setActive(cellId, 0);
                        break;
                    case "movement":
                        changed = setMovement(cellId, 0);
                        break;
                    default:
                        World.world.logger.warn("ignoring unknown cell override '{}'", key);
                }
            }
            return changed;
        }

        public Stream<Integer> getOverrides() {
            return overrides.keySet().stream();
        }

        public boolean isEmpty() {
            return overrides.isEmpty();
        }
    }
}
