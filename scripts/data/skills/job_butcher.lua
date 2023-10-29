local jobID = ButcherJob
local toolIDs = {1945}

-- Craft for Prepare meat
local sk134Crafts = {
    -- Nuggets
    {item=1947, ingredients={ {1987, 1}, {1973, 1} } },
    -- Nuggets **
    {item=1948, ingredients={ {1973, 7}, {1988, 1} } },
    -- Salted Larva
    {item=1949, ingredients={ {1990, 1}, {1730, 1} } },
    -- Salted Larva **
    {item=1950, ingredients={ {1730, 2}, {1991, 1} } },
    -- Salted Larva ***
    {item=1951, ingredients={ {1730, 3}, {1992, 1} } },
    -- Insect Croquette
    {item=1952, ingredients={ {1730, 2}, {1993, 1}, {311, 1} } },
    -- Insect Croquette **
    {item=1953, ingredients={ {1730, 5}, {385, 3}, {1994, 1} } },
    -- Roast Gobball Leg
    {item=1954, ingredients={ {1973, 1}, {1995, 1}, {1731, 1} } },
    -- Roast Gobball Leg **
    {item=1955, ingredients={ {1973, 2}, {1731, 2}, {1997, 1} } },
    -- Roast Gobball Leg ***
    {item=1956, ingredients={ {1973, 3}, {1998, 1}, {1731, 3} } },
    -- Roast Gobball Leg ****
    {item=1957, ingredients={ {1973, 4}, {1731, 4}, {1999, 1} } },
    -- Dragomeat Steak
    {item=1958, ingredients={ {1731, 4}, {2014, 1}, {1975, 4}, {1730, 4} } },
    -- Dragomeat Steak **
    {item=1959, ingredients={ {1731, 5}, {1975, 5}, {1730, 5}, {2015, 1} } },
    -- Dragomeat Steak ***
    {item=1960, ingredients={ {2016, 1}, {1730, 5}, {1731, 5}, {1975, 5} } },
    -- Dragomeat Steak ****
    {item=1961, ingredients={ {1975, 6}, {1730, 6}, {1731, 6}, {2017, 1} } },
    -- Gwilled Wabbit
    {item=1962, ingredients={ {1977, 2}, {2000, 1}, {1973, 4}, {1978, 2} } },
    -- Gwilled Wabbit **
    {item=1963, ingredients={ {2001, 1}, {1973, 5}, {1978, 3}, {1977, 2} } },
    -- Gwilled Wabbit ***
    {item=1964, ingredients={ {1973, 6}, {1977, 3}, {1978, 3}, {2002, 1} } },
    -- Gwilled Wabbit ****
    {item=1965, ingredients={ {1973, 7}, {1977, 5}, {1978, 5}, {2003, 1} } },
    -- Cooked Ham
    {item=1966, ingredients={ {1978, 2}, {2004, 1}, {1974, 1} } },
    -- Cooked Ham **
    {item=1967, ingredients={ {2005, 1}, {1978, 3}, {1974, 3} } },
    -- Cooked Ham ***
    {item=1968, ingredients={ {1978, 4}, {2006, 1}, {1974, 5} } },
    -- Cooked Ham ****
    {item=1969, ingredients={ {1974, 8}, {1978, 5}, {2007, 1} } },
    -- Brawn Salad
    {item=1970, ingredients={ {1978, 2}, {2008, 1}, {394, 1}, {1730, 2}, {311, 2} } },
    -- Brawn Salad **
    {item=1971, ingredients={ {1978, 4}, {1730, 4}, {394, 3}, {311, 2}, {2009, 1} } },
    -- Brawn Salad ***
    {item=1972, ingredients={ {1978, 6}, {1730, 6}, {394, 4}, {311, 2}, {2010, 1} } },
    -- Nuggets ****
    {item=2013, ingredients={ {1973, 4}, {1989, 1} } },
    -- First part of the Dragon Pig Maze Key
    {item=7509, ingredients={ {386, 3}, {2481, 3} } },
    -- Bow Wow Ghost
    {item=7536, ingredients={ {1711, 1} } },
    -- Bow Meow Ghost
    {item=7537, ingredients={ {1728, 1} } },
    -- Wabbit Ghost
    {item=7538, ingredients={ {1748, 1} } },
    -- Fire Bwak Ghost
    {item=7539, ingredients={ {2074, 1} } },
    -- Water Bwak Ghost
    {item=7540, ingredients={ {2075, 1} } },
    -- Air Bwak Ghost
    {item=7541, ingredients={ {2076, 1} } },
    -- Earth Bwak Ghost
    {item=7542, ingredients={ {2077, 1} } },
    -- Quaquack Ghost
    {item=7543, ingredients={ {6604, 1} } },
    -- Nomoon Ghost
    {item=7544, ingredients={ {6716, 1} } },
    -- Peki Ghost
    {item=7545, ingredients={ {6978, 1} } },
    -- Little White Bow Meow Ghost
    {item=7546, ingredients={ {7414, 1} } },
    -- Little Black Bow Wow Ghost
    {item=7547, ingredients={ {7415, 1} } },
    -- Ghast Ghost
    {item=7548, ingredients={ {7518, 1} } },
    -- Pink Dragoone Ghost
    {item=7549, ingredients={ {7519, 1} } },
    -- Croum Ghost
    {item=7550, ingredients={ {7520, 1} } },
    -- Minimino Ghost
    {item=7551, ingredients={ {7522, 1} } },
    -- Baby Crowdzilla Ghost
    {item=7721, ingredients={ {7703, 1} } },
    -- Mischievous Squirrel Ghost
    {item=7722, ingredients={ {7704, 1} } },
    -- Leopardo Ghost
    {item=7723, ingredients={ {7705, 1} } },
    -- Bilby Ghost
    {item=7724, ingredients={ {7706, 1} } },
    -- Blue Piwin Ghost
    {item=7726, ingredients={ {7708, 1} } },
    -- Yellow Piwin Ghost
    {item=7727, ingredients={ {7709, 1} } },
    -- Pink Piwin Ghost
    {item=7728, ingredients={ {7710, 1} } },
    -- Red Piwin Ghost
    {item=7729, ingredients={ {7711, 1} } },
    -- Green Piwin Ghost
    {item=7730, ingredients={ {7712, 1} } },
    -- Purple Piwin Ghost
    {item=7731, ingredients={ {7713, 1} } },
    -- Mini Wa Ghost
    {item=7893, ingredients={ {7891, 1} } },
    -- Willy Peninzias Ghost
    {item=7894, ingredients={ {7892, 1} } },
    -- Bloody Koalak Ghost
    {item=8016, ingredients={ {7911, 1} } },
    -- Bworky Ghost
    {item=8017, ingredients={ {8000, 1} } },
    -- Atooin Ghost
    {item=8020, ingredients={ {7714, 1} } },
    -- Treechster Ghost
    {item=8171, ingredients={ {8153, 1} } },
    -- Gobtubby Ghost
    {item=8172, ingredients={ {8151, 1} } },
    -- El Scarador Ghost
    {item=8173, ingredients={ {8154, 1} } },
    -- Minifoux Ghost
    {item=8174, ingredients={ {8155, 1} } },
    -- Kaniger Steak
    {item=8504, ingredients={ {1974, 1}, {8501, 1}, {1978, 1}, {1730, 1}, {1973, 1}, {311, 1} } },
    -- Koalak Steak
    {item=8505, ingredients={ {311, 1}, {1973, 1}, {1974, 1}, {1731, 1}, {1730, 1}, {1977, 1}, {8502, 1}, {1978, 1} } },
    -- Crocodyl Steak
    {item=8506, ingredients={ {1978, 1}, {1974, 1}, {8503, 1}, {1730, 1}, {1973, 1}, {311, 1}, {1731, 1} } },
    -- Pingoku Ghost
    {item=8524, ingredients={ {8211, 1} } },
    -- Pandawa Cub Ghost
    {item=8565, ingredients={ {8561, 1} } },
    -- Ross Ghost
    {item=8679, ingredients={ {8677, 1} } },
    -- Fëanor Ghost
    {item=8706, ingredients={ {8693, 1} } },
    -- Tabby Bow Meow Ghost
    {item=8885, ingredients={ {7524, 1} } },
    -- The Godfather's Gobtubby Ghost
    {item=9595, ingredients={ {9594, 1} } },
    -- Young Wild Boar Ghost
    {item=9616, ingredients={ {7707, 1} } },
    -- Angora Bow Meow Ghost
    {item=9661, ingredients={ {9617, 1} } },
    -- Miniminotot Ghost
    {item=9665, ingredients={ {9620, 1} } },
    -- Black Dragoone Ghost
    {item=9666, ingredients={ {9624, 1} } },
    -- Crocodyl Ghost
    {item=9671, ingredients={ {9623, 1} } },
    -- Demon Rump
    {item=9778, ingredients={ {1974, 4}, {1977, 4}, {311, 4}, {1730, 4}, {1973, 4}, {9771, 1}, {1978, 4}, {1731, 4} } },
    --
    {item=9779, ingredients={  } },
    -- Demon Rump**
    {item=9780, ingredients={ {1730, 5}, {311, 5}, {1731, 5}, {1973, 5}, {1977, 5}, {1978, 5}, {9773, 1}, {1974, 6} } },
    -- Demon Rump***
    {item=9781, ingredients={ {1731, 6}, {1978, 6}, {1977, 6}, {1973, 6}, {1730, 6}, {311, 6}, {9774, 1}, {1974, 7} } },
    -- Demon Rump****
    {item=9782, ingredients={ {1973, 7}, {1731, 7}, {1730, 7}, {9775, 1}, {311, 7}, {1978, 7}, {1977, 7}, {1974, 7} } },
    -- Entrecote of Angel***
    {item=9783, ingredients={ {9776, 1}, {1974, 7}, {1730, 6}, {1731, 6}, {1977, 6}, {1978, 6}, {1973, 6}, {311, 6} } },
    -- Entrecote of Angel****
    {item=9784, ingredients={ {1731, 7}, {1973, 7}, {1978, 7}, {1974, 7}, {9777, 1}, {1977, 7}, {311, 7}, {1730, 7} } },
    -- Rushu's Shushu Ghost
    {item=9786, ingredients={ {9785, 1} } },
    -- Vampyrina Ghost
    {item=10108, ingredients={ {10106, 1} } },
    -- Borbat Ghost
    {item=10109, ingredients={ {10107, 1} } },
    -- Jellufo Ghost
    {item=10597, ingredients={ {10544, 1} } },
    -- Ogrine Seeker Ghost
    {item=10658, ingredients={ {10657, 1} } },
    -- Tarzantula Ghost
    {item=10867, ingredients={ {10863, 1} } },
    -- Smush Ghost
    {item=10868, ingredients={ {10864, 1} } },
    -- Tofrazzle Ghost
    {item=10869, ingredients={ {10865, 1} } },
    -- Sting Ghost
    {item=10870, ingredients={ {10866, 1} } },
    -- Snow Bow Meow Ghost
    {item=10986, ingredients={ {10985, 1} } },
    -- Brindled Minifoux Ghost
    {item=11016, ingredients={ {11011, 1} } },
    -- Komet Ghost
    {item=11466, ingredients={ {11465, 1} } },
    -- Armoured Dragoone Ghost
    {item=11558, ingredients={ {11554, 1} } },
    -- Entrecote of Angel
    {item=11573, ingredients={ {1973, 4}, {1974, 4}, {1977, 4}, {11571, 1}, {1978, 4}, {311, 4}, {1730, 4}, {1731, 4} } },
    -- Entrecote of Angel**
    {item=11574, ingredients={ {1974, 6}, {1973, 5}, {1731, 5}, {1730, 5}, {311, 5}, {1978, 5}, {1977, 5}, {11572, 1} } },
    -- Red Dragoone Ghost
    {item=11704, ingredients={ {11702, 1} } },
    -- Weegle Owl Ghost
    {item=11722, ingredients={ {11711, 1} } },
    -- Flibalak Ghost
    {item=11737, ingredients={ {11715, 1} } },
    -- Kwismas Reindeer Ghost
    {item=11739, ingredients={ {11719, 1} } },
    -- Solar Bear Ghost
    {item=11744, ingredients={ {11727, 1} } },
    -- Pandiscamp Ghost
    {item=11763, ingredients={ {11755, 1} } },
    -- Tibilax Ghost
    {item=11768, ingredients={ {11729, 1} } },
    -- Minipoth Ghost
    {item=11777, ingredients={ {11775, 1} } },
    -- El Palador Ghost
    {item=11783, ingredients={ {11781, 1} } },
    -- Borbat Ghost
    {item=11800, ingredients={ {11804, 1} } },
    -- Borbat Ghost
    {item=11805, ingredients={ {11809, 1} } },
    -- Tibilax Ghost
    {item=12749, ingredients={ {7891, 1} } },
    -- Patrick the Sandcastle Ghost
    {item=12765, ingredients={ {12761, 1} } },
    -- Solar Bear Ghost
    {item=12821, ingredients={ {12818, 1} } },
    -- Karne Ghost
    {item=12824, ingredients={ {12817, 1} } },
}

registerCraftSkill(134, sk134Crafts, {jobID = jobID, toolIDs = toolIDs}, ingredientsForCraftJob(jobID), jobID)
