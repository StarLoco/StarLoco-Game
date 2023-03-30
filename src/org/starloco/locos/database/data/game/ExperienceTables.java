package org.starloco.locos.database.data.game;

import java.util.Arrays;
import java.util.List;

public class ExperienceTables {
    public static class ExperienceTable {
        private final long[] maxLevelExps;

        public ExperienceTable(long[] maxLevelExps) {
            this.maxLevelExps = maxLevelExps;
        }

        public long minXpAt(int lvl) {
            return maxLevelExps[lvl-1];
        }

        public long maxXpAt(int lvl) {
            if(lvl >= maxLevelExps.length) return maxLevelExps[maxLevelExps.length-1];
            return maxLevelExps[lvl];
        }

        public int levelForXp(long xp) {
            int idx = Arrays.binarySearch(maxLevelExps, xp);

            // If you have exactly the max value for the level, you're actually next level
            if(idx >= 0) return idx+1;

            // No exact match, get insertion point
            idx = -idx -1;
            // In this case, the insertion point is the level we want :)
            return idx;
        }

        public int maxLevel() {
            return maxLevelExps.length;
        }
    }
    public final ExperienceTable players;
    public final ExperienceTable guilds;
    public final ExperienceTable jobs;
    public final ExperienceTable mounts;
    public final ExperienceTable pvp;
    public final ExperienceTable livitinems;
    public final ExperienceTable tormentators;
    public final ExperienceTable bandits;

    public ExperienceTables(long[] players, long[] guilds, long[] jobs, long[] mounts, long[] pvp, long[] livitinems, long[] tormentators, long[] bandits) {
        this.players = new ExperienceTable(players);
        this.guilds = new ExperienceTable(guilds);
        this.jobs = new ExperienceTable(jobs);
        this.mounts = new ExperienceTable(mounts);
        this.pvp = new ExperienceTable(pvp);
        this.livitinems = new ExperienceTable(livitinems);
        this.tormentators = new ExperienceTable(tormentators);
        this.bandits = new ExperienceTable(bandits);
    }
}
