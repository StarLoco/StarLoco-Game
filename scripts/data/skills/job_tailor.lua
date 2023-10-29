local jobID = TailorJob
local toolIDs = {951}

-- Craft for Sew a Bag
local sk123Crafts = {
    -- Small Gobbly Wool Bag
    {item=1697, ingredients={ {881, 10}, {885, 10} } },
    -- Small Harvester Bag
    {item=1698, ingredients={ {881, 20}, {301, 5} } },
    -- Minor Adventurer Bag
    {item=1699, ingredients={ {388, 18}, {881, 18}, {885, 18} } },
    -- Harvester Bag
    {item=1702, ingredients={ {885, 25}, {646, 10}, {881, 25} } },
    -- Large Harvester Bag
    {item=1703, ingredients={ {301, 20}, {384, 20}, {882, 2}, {418, 2}, {409, 2} } },
    -- Sack of Novist
    {item=1704, ingredients={ {1690, 2}, {651, 1}, {1670, 4}, {415, 4}, {418, 2}, {649, 2} } },
    -- Adventurer Bag
    {item=1705, ingredients={ {416, 1}, {1141, 1}, {388, 1}, {418, 1} } },
    -- Great Adventurer Bag
    {item=1707, ingredients={ {2550, 4}, {1890, 27}, {2528, 21}, {7030, 12}, {2551, 11}, {1663, 8} } },
    -- Cawwot-Bag
    {item=6501, ingredients={ {372, 26}, {646, 12}, {361, 10}, {649, 10}, {418, 5}, {648, 35} } },
    -- Chief Crocodyl Schoolbag
    {item=6830, ingredients={ {1613, 30}, {1663, 30}, {2248, 12}, {1890, 12}, {6739, 1}, {6740, 1} } },
    -- Small Moskito Schoolbag
    {item=6916, ingredients={ {519, 20}, {307, 18}, {371, 15} } },
    -- Larvabag
    {item=7108, ingredients={ {2328, 1}, {363, 17}, {362, 15}, {364, 14} } },
    -- Farmer Bag
    {item=8131, ingredients={ {532, 20}, {373, 10}, {2662, 3} } },
}

-- Craft for Sew a Hat
local sk63Crafts = {
    -- Pointed Hat
    {item=629, ingredients={ {887, 1}, {1670, 1}, {882, 1}, {388, 6} } },
    -- Slob Headgear
    {item=696, ingredients={ {533, 1}, {420, 6}, {400, 1}, {1671, 1} } },
    -- Sortingat Hogwat
    {item=698, ingredients={ {384, 10}, {881, 10}, {1672, 1}, {420, 10}, {885, 10} } },
    -- The Troma
    {item=699, ingredients={ {761, 100}, {885, 6}, {420, 2}, {1673, 1} } },
    -- The Oiram
    {item=701, ingredients={ {880, 1}, {886, 1}, {882, 1}, {1674, 1} } },
    -- Kritter
    {item=702, ingredients={ {433, 1}, {1614, 1}, {1675, 1}, {310, 1}, {430, 1} } },
    -- Dora
    {item=703, ingredients={ {420, 10}, {473, 2}, {472, 2}, {880, 1}, {1676, 1} } },
    -- Eachure Hat
    {item=704, ingredients={ {485, 5}, {486, 1}, {1677, 1}, {887, 1}, {420, 10} } },
    -- Tromatizing Mask
    {item=705, ingredients={ {761, 10}, {420, 10}, {1678, 1}, {997, 10}, {388, 10} } },
    -- Crystaloball
    {item=706, ingredients={ {761, 10}, {420, 10}, {388, 10}, {1679, 1} } },
    -- Minot'Horn
    {item=707, ingredients={ {840, 1}, {388, 1}, {1680, 2}, {761, 1}, {420, 1} } },
    -- Pilbocks Hat
    {item=708, ingredients={ {388, 10}, {420, 10}, {301, 1}, {1681, 1} } },
    -- Lorko Kasko
    {item=709, ingredients={ {1610, 2}, {1682, 1}, {1612, 2}, {420, 2}, {434, 2} } },
    -- Korko Klako
    {item=710, ingredients={ {420, 10}, {1683, 1}, {882, 1} } },
    -- Jouik Krampe
    {item=711, ingredients={ {646, 10}, {1684, 1}, {420, 1}, {885, 10} } },
    -- Gulliver
    {item=712, ingredients={ {2486, 30}, {2551, 25}, {2317, 25}, {2647, 30}, {419, 30} } },
    -- Farter
    {item=940, ingredients={ {388, 5}, {752, 1} } },
    -- Komintot Headband
    {item=941, ingredients={ {761, 10}, {363, 3} } },
    -- Smoother
    {item=942, ingredients={ {409, 1}, {752, 1} } },
    -- Small Vitality Headband
    {item=943, ingredients={ {851, 10}, {881, 2} } },
    -- Vitality Headband
    {item=944, ingredients={ {851, 12}, {881, 5} } },
    -- Great Vitality Headband
    {item=949, ingredients={ {851, 14}, {881, 8} } },
    -- White Dreggheadgear
    {item=1904, ingredients={ {881, 10}, {308, 10}, {847, 5}, {1891, 5}, {2511, 1} } },
    -- Chafer Helmet
    {item=1908, ingredients={ {6458, 2}, {2479, 1}, {6475, 1}, {901, 20}, {2275, 20}, {6457, 3} } },
    -- Shako
    {item=2070, ingredients={ {310, 10}, {430, 5}, {2664, 4}, {2300, 1}, {1675, 1} } },
    -- Dark Makroute
    {item=2094, ingredients={ {2277, 4}, {653, 1}, {654, 1}, {643, 1}, {6478, 1}, {648, 1} } },
    -- The Bear
    {item=2095, ingredients={ {646, 5}, {1890, 5}, {2277, 4}, {643, 1}, {6479, 1} } },
    -- Brave's Dark Dora
    {item=2096, ingredients={ {2277, 5}, {2285, 1}, {6480, 1}, {1610, 20}, {420, 20} } },
    -- Champo
    {item=2097, ingredients={ {2035, 1}, {1674, 1}, {290, 50}, {377, 50}, {2661, 20} } },
    -- Daudgee
    {item=2104, ingredients={ {2492, 1}, {2286, 20}, {2465, 2}, {6457, 2}, {6458, 2} } },
    -- Fire Kwak Headdress
    {item=2409, ingredients={ {653, 1}, {415, 50}, {649, 6}, {650, 4}, {1894, 2} } },
    -- Treechelmet
    {item=2410, ingredients={ {437, 60}, {435, 40}, {434, 20}, {463, 15}, {2250, 3} } },
    -- Gobball Headgear
    {item=2411, ingredients={ {882, 40}, {2448, 1}, {384, 150}, {881, 100} } },
    -- Royal Gobball Headgear
    {item=2438, ingredients={ {2411, 1}, {880, 1}, {384, 400}, {881, 300}, {882, 200}, {2462, 1} } },
    -- Jelleadgear
    {item=2447, ingredients={ {311, 10}, {757, 10}, {368, 20}, {369, 20}, {995, 20}, {993, 20} } },
    -- Adventurer Hat
    {item=2474, ingredients={ {8248, 4}, {1019, 3} } },
    -- Vegadgear
    {item=2485, ingredients={ {421, 25}, {593, 15}, {395, 15}, {594, 2}, {6797, 1} } },
    -- Caracap
    {item=2531, ingredients={ {2618, 200}, {2610, 150}, {2611, 150}, {2613, 150}, {2609, 150}, {2630, 20} } },
    -- Ice Kwak Headdress
    {item=2535, ingredients={ {414, 50}, {649, 6}, {650, 4}, {1894, 2}, {653, 1} } },
    -- Crow Helmet
    {item=2546, ingredients={ {2058, 1}, {2056, 1}, {1692, 1}, {2060, 45}, {1889, 40} } },
    -- Toady
    {item=2641, ingredients={ {1674, 10}, {6922, 1}, {6921, 1}, {377, 110}, {290, 110} } },
    -- Golden Dreggheadgear
    {item=6472, ingredients={ {313, 20}, {842, 15}, {1891, 10}, {845, 7}, {6458, 3}, {2511, 1} } },
    -- Sapphire Dreggheadgear
    {item=6473, ingredients={ {466, 2}, {881, 30}, {1129, 25}, {844, 18}, {882, 10}, {465, 2} } },
    -- Black Dreggheadgear
    {item=6474, ingredients={ {843, 40}, {1891, 30}, {846, 30}, {882, 30}, {6458, 10}, {316, 3}, {466, 3} } },
    -- Dark Miner Hat
    {item=6477, ingredients={ {2559, 1}, {6476, 1}, {2245, 13}, {2274, 8}, {7035, 3}, {7036, 3}, {1694, 2} } },
    -- Dora Bora
    {item=6481, ingredients={ {2617, 80}, {2607, 10}, {1678, 5}, {6441, 4}, {2254, 100}, {2253, 100}, {2624, 100}, {2605, 80} } },
    -- Korko Kousto
    {item=6482, ingredients={ {2254, 50}, {2624, 30}, {2253, 20}, {2605, 10}, {2607, 1}, {711, 1} } },
    -- Bronze Bwork Helmet
    {item=6483, ingredients={ {7906, 10}, {7036, 7}, {8057, 3}, {8058, 3}, {6813, 1}, {3000, 1}, {7406, 1}, {2465, 100} } },
    -- Wabbit Ears
    {item=6500, ingredients={ {360, 16}, {418, 10}, {649, 36}, {646, 21}, {2288, 19}, {419, 18} } },
    -- Bearman's Headgear
    {item=6719, ingredients={ {6844, 1}, {6845, 1}, {6841, 1}, {6479, 1} } },
    -- Earth Kwak Headdress
    {item=6760, ingredients={ {649, 6}, {650, 4}, {1894, 2}, {653, 1}, {1141, 50} } },
    -- Wind Kwak Headdress
    {item=6761, ingredients={ {653, 1}, {416, 50}, {649, 6}, {650, 4}, {1894, 2} } },
    -- Ambusheadgear
    {item=6764, ingredients={ {2602, 35}, {2254, 35}, {6736, 12}, {1018, 5}, {2605, 2} } },
    -- Farle's Straw Hat
    {item=6778, ingredients={ {1890, 15}, {650, 5}, {653, 5}, {2550, 4}, {2026, 2}, {2029, 2}, {2036, 2} } },
    -- Florheadgear
    {item=6797, ingredients={ {309, 40}, {2662, 10}, {2665, 1} } },
    -- Chief Crocodyl Headgear
    {item=6834, ingredients={ {6738, 1}, {6739, 1}, {6740, 1}, {1663, 25}, {1613, 25}, {2664, 4} } },
    -- Arachelmet
    {item=6913, ingredients={ {365, 10}, {362, 1} } },
    -- Moskabuto
    {item=6917, ingredients={ {371, 35}, {519, 30}, {307, 15} } },
    -- Musheadgear
    {item=6921, ingredients={ {377, 4}, {290, 2} } },
    -- Prespwig
    {item=6926, ingredients={ {2571, 25}, {2573, 11}, {2574, 3}, {653, 1}, {2572, 1} } },
    -- Crackler Helmet
    {item=6930, ingredients={ {448, 16}, {447, 12}, {431, 6}, {2306, 2}, {2252, 20} } },
    -- Green Scara Helmet
    {item=6936, ingredients={ {398, 12}, {1458, 10}, {2293, 5}, {396, 1}, {1466, 40} } },
    -- Red Scara Helmet
    {item=6937, ingredients={ {1457, 10}, {2292, 5}, {396, 1}, {1465, 40}, {398, 12} } },
    -- White Scara Helmet
    {item=6938, ingredients={ {1467, 40}, {398, 12}, {1456, 10}, {2290, 5}, {396, 1} } },
    -- Blue Scara Helmet
    {item=6939, ingredients={ {1464, 40}, {398, 12}, {1455, 10}, {2291, 5}, {396, 1} } },
    -- Boowolf Headgear
    {item=6952, ingredients={ {440, 30}, {439, 30}, {2579, 20}, {2578, 10}, {2575, 2}, {8396, 1} } },
    -- Sulik
    {item=6988, ingredients={ {7307, 6}, {7308, 4}, {7382, 2}, {7306, 2}, {7305, 2}, {7304, 2}, {7393, 1}, {6441, 10} } },
    -- Nee Cap
    {item=6989, ingredients={ {7379, 1}, {7384, 1}, {415, 15}, {7276, 12}, {7343, 12}, {291, 8}, {2503, 4}, {7284, 4} } },
    -- Mothat
    {item=6990, ingredients={ {7307, 1}, {7384, 1}, {307, 500}, {371, 500}, {7277, 16}, {7284, 9}, {7259, 4} } },
    -- Guavhat
    {item=6991, ingredients={ {7268, 20}, {7267, 20}, {7266, 20}, {7273, 5}, {7258, 3} } },
    -- Kannipiwi
    {item=6992, ingredients={ {6898, 15}, {6902, 15}, {6903, 15}, {6897, 15}, {6899, 15}, {1088, 1} } },
    -- Kracker Cap
    {item=7056, ingredients={ {2247, 1}, {6441, 1}, {7343, 20}, {2278, 20}, {2513, 15}, {2277, 9}, {7292, 4}, {7385, 1} } },
    -- Octovius
    {item=7058, ingredients={ {1798, 400}, {7279, 30}, {7030, 20}, {7260, 19}, {7273, 15}, {7301, 12} } },
    -- Larvhat
    {item=7109, ingredients={ {362, 14}, {364, 11}, {2328, 1}, {519, 40}, {363, 18} } },
    -- Himune
    {item=7141, ingredients={ {291, 8}, {7381, 1}, {7343, 50}, {7275, 34}, {7278, 32}, {1889, 20}, {652, 12}, {654, 9} } },
    -- Paperb' Hat
    {item=7142, ingredients={ {7307, 1}, {7385, 1}, {388, 40}, {7276, 30}, {409, 16}, {7259, 5}, {651, 3}, {7304, 2} } },
    -- Solomonk
    {item=7143, ingredients={ {7284, 8}, {650, 6}, {7259, 2}, {7295, 2}, {7292, 1}, {7308, 1}, {7385, 1}, {7276, 10} } },
    -- Palishat
    {item=7144, ingredients={ {7297, 3}, {416, 3}, {7380, 1}, {7379, 1}, {1690, 16}, {649, 15}, {7292, 4} } },
    -- Gaddie's Hat
    {item=7145, ingredients={ {761, 20}, {7343, 15}, {7275, 15}, {7258, 9}, {7297, 3}, {7305, 1}, {7384, 1} } },
    -- Lullibye
    {item=7146, ingredients={ {7306, 1}, {646, 16}, {414, 10}, {7343, 8}, {2248, 8}, {7275, 5}, {7258, 3} } },
    -- Krutch
    {item=7150, ingredients={ {7278, 18}, {7258, 8}, {7297, 6}, {2550, 5}, {7293, 4}, {7379, 1}, {1141, 26} } },
    -- Koloss
    {item=7151, ingredients={ {7343, 16}, {7258, 12}, {291, 11}, {7277, 6}, {7276, 5}, {7294, 3}, {7305, 1}, {7379, 1} } },
    -- Wheritz Hat
    {item=7152, ingredients={ {7278, 8}, {2550, 3}, {7382, 1}, {7308, 1}, {2248, 15}, {7343, 15}, {415, 12}, {7275, 10} } },
    -- Pandawushu Headband
    {item=7177, ingredients={ {7278, 1}, {7282, 2} } },
    -- Akwadala Hat
    {item=7226, ingredients={ {7222, 50}, {7282, 4}, {7278, 3}, {7297, 1}, {7258, 1} } },
    -- Terrdala Hat
    {item=7227, ingredients={ {7297, 10}, {7258, 8}, {7281, 5}, {7277, 4}, {7026, 1}, {7224, 50} } },
    -- Feudala Hat
    {item=7228, ingredients={ {7260, 5}, {7276, 5}, {7275, 4}, {7259, 1}, {466, 1}, {7225, 50}, {7280, 12} } },
    -- Aerdala Hat
    {item=7229, ingredients={ {1891, 20}, {7282, 12}, {7258, 10}, {7279, 8}, {7026, 1}, {7223, 50} } },
    -- Kitsou Cap
    {item=7339, ingredients={ {7279, 1}, {7278, 1}, {7277, 1}, {7282, 1}, {7275, 1}, {7276, 1} } },
    -- Bimd'Oule Hat
    {item=7516, ingredients={ {8066, 2}, {8086, 1}, {8389, 1}, {8250, 21}, {8002, 18}, {8065, 5}, {7384, 2} } },
    -- Turkohat
    {item=7553, ingredients={ {2594, 2}, {2599, 50}, {2179, 25}, {2588, 15}, {2586, 11}, {2591, 3} } },
    -- Ougaat
    {item=7680, ingredients={ {9280, 4}, {8824, 1}, {2641, 1}, {9281, 1}, {8775, 16}, {2035, 8}, {9267, 7}, {9263, 6} } },
    -- Gobkool Headgear
    {item=7886, ingredients={ {304, 30}, {7905, 3}, {7907, 2}, {2411, 1}, {384, 30} } },
    -- Royal Tofu's Crown
    {item=7921, ingredients={ {2675, 10}, {1685, 1}, {7026, 1}, {2246, 1}, {1892, 1}, {301, 200}, {8249, 50}, {2247, 10} } },
    -- Koalak Headgear
    {item=8009, ingredients={ {8002, 7}, {8061, 6}, {8060, 5}, {8062, 4}, {8059, 3}, {8085, 1} } },
    -- Helmetofu
    {item=8114, ingredients={ {366, 18}, {367, 13}, {8557, 1}, {301, 25} } },
    -- Golden Scara Helmet
    {item=8116, ingredients={ {6939, 1}, {8160, 1}, {8133, 1}, {6937, 1}, {6936, 1}, {6938, 1} } },
    -- Farmer Sunhat
    {item=8125, ingredients={ {532, 35}, {300, 15}, {2662, 2} } },
    -- Black Scara Helmet
    {item=8133, ingredients={ {8141, 1}, {6939, 1}, {6936, 1}, {6937, 1}, {6938, 1}, {8140, 1} } },
    -- Legendary Crackler Helmet
    {item=8147, ingredients={ {6930, 1}, {448, 50}, {431, 16}, {467, 8}, {8102, 5}, {2305, 5}, {7028, 2} } },
    -- Kabuto
    {item=8163, ingredients={ {8140, 21}, {8002, 20}, {7925, 15}, {7370, 8}, {7384, 2}, {434, 100}, {6737, 40} } },
    -- Red Piwi Hat
    {item=8243, ingredients={ {287, 1}, {6900, 1} } },
    -- Blue Piwi Hat
    {item=8244, ingredients={ {287, 1}, {6897, 1} } },
    -- Purple Piwi Hat
    {item=8245, ingredients={ {287, 1}, {6898, 1} } },
    -- Green Piwi Hat
    {item=8246, ingredients={ {287, 1}, {6899, 1} } },
    -- Yellow Piwi Hat
    {item=8247, ingredients={ {6902, 1}, {287, 1} } },
    -- Pink Piwi Hat
    {item=8248, ingredients={ {287, 1}, {6903, 1} } },
    -- Kaliptus Headband
    {item=8260, ingredients={ {8160, 15}, {7380, 3}, {7381, 2}, {8157, 2}, {7904, 500}, {7903, 500}, {7288, 100}, {7287, 100} } },
    -- Moowolf Headgear
    {item=8267, ingredients={ {2580, 1}, {8389, 1}, {1696, 1}, {1691, 10}, {2581, 8}, {2577, 5}, {2268, 5}, {6952, 1} } },
    -- Minotot Headgear
    {item=8284, ingredients={ {8405, 5}, {8398, 3}, {7393, 3}, {707, 1}, {8406, 1}, {8409, 1}, {8251, 21}, {2247, 15} } },
    -- Dragon Pig Headgear
    {item=8285, ingredients={ {481, 1}, {8083, 31}, {2336, 25}, {8058, 12}, {8055, 4}, {8056, 3}, {6738, 1} } },
    -- Dreggon Helmet
    {item=8287, ingredients={ {6474, 1}, {6472, 1}, {8354, 10}, {1904, 1}, {8367, 1}, {8368, 1}, {8363, 1}, {8361, 1} } },
    -- Shika's Hat
    {item=8304, ingredients={ {1671, 4}, {8085, 3}, {2036, 1}, {2026, 1}, {7904, 50}, {8061, 12}, {8062, 12} } },
    -- Flying Dreggon Headgear
    {item=8330, ingredients={ {8351, 1}, {8252, 10}, {654, 10}, {1904, 1}, {6472, 1}, {8350, 1} } },
    -- Dragostess Hat
    {item=8331, ingredients={ {2557, 100}, {1904, 1}, {6472, 1}, {8357, 1}, {8157, 1}, {2659, 100}, {7287, 100} } },
    -- Billalo
    {item=8441, ingredients={ {8762, 19}, {8766, 13}, {2278, 12}, {8753, 4}, {8755, 3}, {8486, 2}, {8764, 2} } },
    -- Black Rat Mask
    {item=8442, ingredients={ {8484, 2}, {8481, 30}, {2495, 20}, {2584, 20}, {761, 20}, {792, 10}, {8485, 4} } },
    -- White Rat Hat
    {item=8451, ingredients={ {1890, 12}, {8486, 2}, {2097, 1}, {8484, 1}, {2322, 25}, {8481, 20}, {2550, 15} } },
    -- Lord of the Rats' Ceremonial Hat
    {item=8457, ingredients={ {1890, 200}, {2571, 50}, {8484, 6}, {8158, 5}, {8493, 2}, {8487, 1}, {8442, 1}, {8451, 1} } },
    -- Ancestral Treechelmet
    {item=8463, ingredients={ {918, 1}, {435, 120}, {1611, 20}, {8496, 3}, {8494, 3}, {1612, 2}, {2410, 1} } },
    -- Soft Oak Hat
    {item=8474, ingredients={ {6490, 2}, {6487, 1}, {2410, 1}, {8463, 1}, {1674, 10}, {1660, 10}, {6488, 10}, {6486, 10} } },
    -- Ruskie Hat
    {item=8530, ingredients={ {8755, 12}, {8754, 8}, {466, 5}, {8795, 3}, {8808, 1}, {8995, 1}, {8790, 48}, {8756, 28} } },
    -- Doro L. Blak
    {item=8569, ingredients={ {8731, 1}, {8787, 19}, {8732, 8}, {8745, 7}, {8771, 5}, {8753, 4}, {8144, 1}, {8997, 1} } },
    -- Hat Ariutokinabot
    {item=8619, ingredients={ {8744, 10}, {8749, 10}, {8763, 2}, {8745, 2}, {8994, 1} } },
    -- Ukando Hat
    {item=8628, ingredients={ {2551, 10}, {8750, 10}, {8744, 10}, {8746, 2}, {8994, 1} } },
    -- Hat Tsokey
    {item=8629, ingredients={ {8994, 1}, {375, 12}, {8752, 10}, {8744, 10}, {8748, 2} } },
    -- Hatter Lily
    {item=8630, ingredients={ {8994, 1}, {2528, 20}, {8751, 10}, {8744, 10}, {8747, 2} } },
    -- Hat Wisholdoo
    {item=8631, ingredients={ {372, 10}, {8744, 10}, {8749, 2}, {8745, 2}, {8994, 1} } },
    -- Cap Ricott
    {item=8632, ingredients={ {8994, 1}, {417, 12}, {8744, 10}, {8750, 10}, {8746, 2} } },
    -- Cap Rayer
    {item=8633, ingredients={ {8744, 10}, {8752, 10}, {8748, 2}, {8994, 1}, {429, 16} } },
    -- Cool Hood
    {item=8634, ingredients={ {439, 14}, {8751, 10}, {8744, 10}, {8747, 2}, {8994, 1} } },
    -- Helmet Hicc
    {item=8635, ingredients={ {1890, 20}, {8749, 10}, {8744, 10}, {8745, 2}, {8994, 1} } },
    -- Robbie Hoodie Cap
    {item=8636, ingredients={ {8744, 10}, {372, 10}, {8746, 2}, {8994, 1}, {8750, 10} } },
    -- Zinda Hood
    {item=8637, ingredients={ {8752, 10}, {8744, 10}, {2283, 4}, {8748, 2}, {8994, 1} } },
    -- Sthood
    {item=8638, ingredients={ {8744, 10}, {8751, 10}, {8747, 2}, {2466, 2}, {8994, 1} } },
    -- Tynril Hat
    {item=8699, ingredients={ {8770, 1}, {8997, 1}, {8916, 1}, {8807, 8}, {8791, 7}, {8753, 5}, {8777, 2}, {1557, 1} } },
    -- Jester Hat
    {item=8704, ingredients={ {2506, 2}, {8996, 1}, {8798, 1}, {8783, 34}, {2273, 28}, {2550, 22}, {8807, 8}, {8753, 7} } },
    -- Zoth Sergeant Mask
    {item=8820, ingredients={ {8774, 4}, {929, 2}, {8777, 1}, {8800, 110}, {8780, 42}, {8797, 36}, {8806, 14}, {8772, 5} } },
    -- Zoth Disciple Hat
    {item=8821, ingredients={ {8803, 14}, {8773, 3}, {8784, 3}, {929, 1}, {8764, 1}, {8800, 89}, {8782, 26}, {8785, 19} } },
    -- Light Treeckler Mask
    {item=8822, ingredients={ {8797, 69}, {8779, 17}, {8781, 14}, {8796, 13}, {8756, 12}, {8774, 3}, {8792, 2}, {8996, 1} } },
    -- Dark Treeckler Mask
    {item=8823, ingredients={ {8771, 3}, {8995, 1}, {8785, 43}, {8757, 16}, {8762, 16}, {8784, 9}, {8790, 8} } },
    -- Snailmet
    {item=8824, ingredients={ {8798, 1}, {8832, 59}, {8783, 22}, {8801, 19}, {8766, 18}, {8760, 11}, {8793, 9}, {8807, 4} } },
    -- Barbrossa's Hat
    {item=8829, ingredients={ {8754, 7}, {8758, 2}, {8755, 2}, {8995, 1}, {8757, 29}, {8763, 14} } },
    -- Thierry Voodoo Mask
    {item=8839, ingredients={ {8808, 2}, {8789, 1}, {2625, 1}, {8764, 1}, {8777, 1}, {1088, 1}, {8797, 31}, {8801, 29} } },
    -- Jav Voodoo Mask
    {item=8840, ingredients={ {8777, 1}, {8797, 31}, {8801, 29}, {8808, 2}, {1088, 1}, {8789, 1}, {2626, 1}, {8764, 1} } },
    -- Sarbak Voodoo Mask
    {item=8841, ingredients={ {2627, 1}, {8777, 1}, {8764, 1}, {1088, 1}, {8789, 1}, {8797, 31}, {8801, 29}, {8808, 2} } },
    -- Archer Voodoo Mask
    {item=8842, ingredients={ {8808, 2}, {1088, 1}, {8777, 1}, {8764, 1}, {8789, 1}, {2628, 1}, {8797, 31}, {8801, 29} } },
    -- Crocodyl Dandy's Hat
    {item=8843, ingredients={ {8790, 29}, {1613, 18}, {8755, 6}, {8786, 6}, {8998, 1}, {8798, 1}, {1664, 120}, {8766, 43} } },
    -- Mopy King Sovereign Hood
    {item=8844, ingredients={ {8801, 210}, {745, 25}, {8324, 15}, {8808, 11}, {8810, 11}, {8811, 11}, {8809, 4}, {8998, 1} } },
    -- Ouassingue Hood
    {item=8845, ingredients={ {8808, 1}, {8801, 101}, {8807, 14}, {8811, 7}, {8810, 4}, {8791, 3}, {8764, 1} } },
    -- Zoth Girl Hat
    {item=8846, ingredients={ {929, 5}, {8774, 3}, {8741, 3}, {8996, 1}, {8800, 75}, {8801, 23}, {8805, 13}, {8769, 6} } },
    -- Zoth Warrior Helmet
    {item=8847, ingredients={ {8770, 1}, {8800, 102}, {8790, 36}, {8802, 14}, {8807, 6}, {929, 5}, {8811, 4}, {8773, 4} } },
    -- Zoth Master Hat
    {item=8848, ingredients={ {8804, 16}, {8807, 6}, {8810, 5}, {929, 5}, {8792, 4}, {8808, 1}, {8916, 1}, {8800, 115} } },
    -- Improved Helmet
    {item=8918, ingredients={ {8766, 1}, {8763, 1}, {8801, 1}, {1019, 1} } },
    -- Air Pikoko Helmet
    {item=8989, ingredients={ {8998, 1}, {8916, 1}, {8788, 51}, {8783, 51}, {8767, 51}, {8792, 13}, {8776, 13}, {8769, 13} } },
    -- Mush Ombrero
    {item=9147, ingredients={ {9263, 24}, {9267, 20}, {9269, 18}, {7383, 18}, {9278, 15}, {7407, 15}, {8753, 8}, {8404, 1} } },
    -- Batouta Helmet
    {item=9181, ingredients={ {7381, 15}, {8776, 12}, {9401, 4}, {6483, 1}, {8800, 50}, {8832, 23}, {7405, 22}, {8787, 17} } },
    -- Mitch Shroom
    {item=9394, ingredients={ {8787, 43}, {8788, 41}, {8800, 32}, {9382, 20}, {8795, 10}, {8786, 10}, {7384, 6}, {7404, 4} } },
    -- Royal Bloprown
    {item=9395, ingredients={ {9148, 1}, {8766, 50}, {8832, 32}, {8795, 10}, {8764, 10}, {9175, 1}, {9166, 1}, {9157, 1} } },
    -- The Kim
    {item=9461, ingredients={ {7384, 19}, {8755, 15}, {8795, 15}, {8786, 10}, {1892, 8}, {7143, 1}, {8267, 1}, {8251, 20} } },
    -- Qu'Tan Horned Cap
    {item=11548, ingredients={ {2268, 16}, {7300, 12}, {11533, 6}, {11532, 5}, {8487, 3}, {8284, 1}, {8738, 36}, {11535, 18} } },
}

-- Craft for Sew a Cape
local sk64Crafts = {
    -- Bowisse's Cloak
    {item=744, ingredients={ {364, 50}, {881, 10}, {384, 10}, {1687, 1} } },
    -- Vampire Cloak
    {item=754, ingredients={ {1686, 1}, {384, 10}, {881, 1}, {752, 1} } },
    -- Dispenser of Justice Cloak
    {item=758, ingredients={ {761, 100}, {388, 25}, {2503, 4}, {882, 2} } },
    -- Chogreloting Cheeken Cloak
    {item=759, ingredients={ {1685, 1}, {650, 1}, {652, 1}, {291, 1} } },
    -- Swashbucloak
    {item=772, ingredients={ {1686, 1}, {384, 8}, {881, 8}, {388, 4} } },
    -- Ellinie Cloak
    {item=773, ingredients={ {420, 10}, {301, 10}, {1689, 1} } },
    -- Cape Ulais
    {item=774, ingredients={ {1687, 1}, {885, 1}, {881, 1} } },
    -- Cape Ability
    {item=775, ingredients={ {384, 15}, {416, 1}, {1687, 1} } },
    -- Sin Cape
    {item=776, ingredients={ {1688, 1}, {885, 1}, {414, 1} } },
    -- Salt 'n' Battery Cape
    {item=777, ingredients={ {642, 1}, {438, 1}, {414, 4}, {1688, 4}, {1691, 1} } },
    -- Maj'Hic Cloak
    {item=779, ingredients={ {301, 20}, {646, 5}, {1690, 2}, {1688, 1}, {881, 20} } },
    -- Itou Lascione Cape
    {item=781, ingredients={ {418, 1}, {648, 10}, {1141, 2}, {415, 2} } },
    -- Cape of Good Hope
    {item=790, ingredients={ {1690, 4}, {416, 2}, {1686, 1}, {650, 1} } },
    -- Small Redness Cloak
    {item=932, ingredients={ {309, 6}, {881, 4}, {384, 4} } },
    -- Redness Cloak
    {item=933, ingredients={ {881, 6}, {384, 6}, {309, 6} } },
    -- Great Redness Cloak
    {item=934, ingredients={ {384, 9}, {881, 9}, {309, 9} } },
    -- Small Bluish Cloak
    {item=935, ingredients={ {881, 10}, {362, 2}, {519, 1} } },
    -- Bluish Cloak
    {item=936, ingredients={ {881, 18}, {362, 3}, {519, 1} } },
    -- Great Bluish Cloak
    {item=937, ingredients={ {881, 26}, {362, 4}, {519, 1} } },
    -- Small Dark Cloak
    {item=945, ingredients={ {885, 5}, {1692, 1} } },
    -- Dark Cloak
    {item=946, ingredients={ {885, 10}, {365, 5}, {1692, 1} } },
    -- Great Dark Cloak
    {item=947, ingredients={ {365, 15}, {885, 15}, {1692, 1} } },
    -- Treecloak
    {item=948, ingredients={ {437, 10}, {836, 10}, {434, 10} } },
    -- Mad Boowolf Cloak
    {item=952, ingredients={ {439, 1}, {1692, 1}, {440, 1}, {438, 1}, {291, 10} } },
    -- Mad Tofu Cloak
    {item=953, ingredients={ {301, 100}, {885, 1}, {1692, 1}, {880, 1} } },
    -- Poak Cloak
    {item=954, ingredients={ {384, 10}, {761, 10}, {881, 10}, {1686, 1} } },
    -- Cape Hillary
    {item=955, ingredients={ {384, 20}, {881, 10}, {880, 1}, {418, 1}, {1686, 1} } },
    -- Cape Rice
    {item=956, ingredients={ {1687, 1}, {881, 20}, {384, 20}, {388, 5}, {648, 5} } },
    -- The Gobb
    {item=957, ingredients={ {384, 10}, {881, 10}, {414, 2}, {1688, 1} } },
    -- Carpet Cape
    {item=1500, ingredients={ {1690, 6}, {1141, 4}, {882, 3}, {409, 2}, {650, 1} } },
    -- Jules Yanos' Cloak
    {item=1693, ingredients={ {649, 4}, {880, 1}, {1691, 1}, {882, 1}, {1694, 1}, {651, 1} } },
    -- Elya Wood's Cloak
    {item=1695, ingredients={ {1690, 10}, {409, 10}, {880, 2}, {1691, 1}, {840, 1}, {1696, 1}, {418, 10} } },
    -- Ouginak Cloak
    {item=1910, ingredients={ {2248, 30}, {2321, 30}, {2286, 5}, {2285, 1}, {2503, 1}, {2464, 1} } },
    -- Crow Cloak
    {item=2061, ingredients={ {1889, 100}, {2060, 100}, {3209, 1}, {1692, 1}, {2059, 1} } },
    -- Mushd Cloak
    {item=2380, ingredients={ {417, 50}, {2584, 10}, {424, 5}, {2585, 1}, {426, 1} } },
    -- Flowing Cloak
    {item=2381, ingredients={ {1455, 10}, {2291, 10}, {414, 5}, {420, 5}, {416, 5}, {1688, 1} } },
    -- Maimpa Cloak
    {item=2382, ingredients={ {2317, 10}, {2496, 8}, {2248, 4}, {648, 4}, {2501, 1} } },
    -- Kwag'U Cape
    {item=2383, ingredients={ {1457, 10}, {2315, 10}, {2268, 1}, {1686, 1}, {2576, 1}, {2281, 1} } },
    -- Grazor
    {item=2385, ingredients={ {2035, 1}, {373, 20}, {364, 20}, {594, 20}, {1687, 10}, {2557, 10}, {2253, 2} } },
    -- Rags
    {item=2386, ingredients={ {424, 10}, {1692, 4}, {1688, 3}, {2273, 2}, {2656, 1} } },
    -- Hairy Cloak
    {item=2387, ingredients={ {648, 12}, {646, 12}, {1894, 8}, {1890, 8}, {2248, 4}, {291, 2} } },
    -- Fire Kwape
    {item=2412, ingredients={ {301, 5}, {2675, 2}, {1889, 1}, {648, 1}, {415, 50} } },
    -- Treecapa
    {item=2413, ingredients={ {434, 30}, {435, 30}, {437, 30}, {463, 5}, {792, 3} } },
    -- Gobbling Cape
    {item=2414, ingredients={ {885, 30}, {882, 10}, {2466, 1}, {384, 80}, {881, 30} } },
    -- Royal Gobbling Cape
    {item=2445, ingredients={ {882, 200}, {2414, 1}, {880, 1}, {384, 500}, {881, 300}, {885, 300} } },
    -- Jellicape
    {item=2446, ingredients={ {311, 50}, {368, 30}, {757, 20}, {369, 15}, {995, 10}, {2436, 1} } },
    -- Adventurer Cloak
    {item=2473, ingredients={ {936, 1}, {945, 1} } },
    -- Vegicape
    {item=2489, ingredients={ {395, 25}, {380, 10}, {373, 10}, {374, 2}, {6795, 1} } },
    -- Caracape
    {item=2532, ingredients={ {2618, 150}, {2610, 150}, {2611, 150}, {2613, 150}, {2619, 1}, {2609, 150} } },
    -- Ice Kwape
    {item=2534, ingredients={ {2675, 2}, {648, 1}, {1889, 1}, {414, 50}, {301, 5} } },
    -- Mastralis Croak
    {item=2547, ingredients={ {2285, 10}, {2059, 8}, {1889, 80}, {646, 80}, {2675, 80}, {1690, 50}, {1692, 20}, {1892, 10} } },
    -- Caracape Minotoris
    {item=2629, ingredients={ {650, 10}, {1894, 10}, {1141, 10}, {2667, 4}, {840, 4}, {2998, 1}, {2999, 1}, {416, 10} } },
    -- Crowcape
    {item=6449, ingredients={ {2060, 100}, {1889, 100}, {2056, 15}, {1692, 2}, {2061, 1} } },
    -- Bearman's Cloak
    {item=6720, ingredients={ {6841, 1}, {648, 1}, {388, 1}, {761, 1} } },
    -- Earth Kwape
    {item=6756, ingredients={ {2675, 2}, {648, 1}, {1889, 1}, {1141, 50}, {301, 5} } },
    -- Wind Kwape
    {item=6757, ingredients={ {648, 1}, {416, 50}, {301, 5}, {2675, 2}, {1889, 1} } },
    -- Ambusherot
    {item=6763, ingredients={ {2624, 5}, {2254, 30}, {2602, 20}, {2605, 8}, {1018, 5} } },
    -- Farle's Cloak
    {item=6775, ingredients={ {2550, 8}, {424, 8}, {426, 8}, {653, 4}, {882, 30}, {409, 18}, {2503, 9} } },
    -- Purple Cloak
    {item=6795, ingredients={ {2665, 1}, {309, 30}, {2659, 10} } },
    -- Mush Mush Cloak
    {item=6922, ingredients={ {300, 8}, {377, 5} } },
    -- Prespic Cloak
    {item=6927, ingredients={ {2571, 15}, {2573, 3}, {2572, 1}, {407, 1}, {653, 1} } },
    -- Crackler Cloak
    {item=6931, ingredients={ {431, 5}, {543, 1}, {2252, 20}, {448, 17}, {447, 16} } },
    -- Green Scaracape
    {item=6940, ingredients={ {2293, 4}, {396, 1}, {1466, 30}, {398, 10}, {1458, 6} } },
    -- Blue Scaracape
    {item=6941, ingredients={ {398, 10}, {1455, 6}, {2291, 4}, {396, 1}, {1464, 30} } },
    -- Red Scaracape
    {item=6942, ingredients={ {1457, 6}, {2292, 4}, {396, 1}, {1465, 30}, {398, 10} } },
    -- White Scaracape
    {item=6943, ingredients={ {1467, 30}, {398, 10}, {1456, 6}, {2290, 4}, {396, 1} } },
    -- Boowolf Cloak
    {item=6954, ingredients={ {1690, 5}, {8396, 1}, {438, 1}, {2575, 1}, {2579, 10}, {291, 5} } },
    -- Hallelujah Cloak
    {item=6993, ingredients={ {2455, 10}, {2239, 10}, {1687, 6}, {1686, 6}, {7297, 2} } },
    -- Hooded Cloak
    {item=6994, ingredients={ {1689, 10}, {102, 10}, {291, 8}, {1730, 2}, {642, 1}, {1672, 1} } },
    -- Graytess Cape
    {item=6995, ingredients={ {7293, 12}, {7292, 10}, {7259, 5}, {7305, 1}, {7304, 1}, {7258, 30}, {7276, 20} } },
    -- Cape Huccino
    {item=7137, ingredients={ {7277, 3}, {2464, 3}, {7258, 2}, {1692, 10}, {7275, 3} } },
    -- Nettlez
    {item=7138, ingredients={ {7295, 1}, {7308, 1}, {7384, 1}, {7277, 12}, {7276, 10}, {653, 6}, {7294, 3}, {7292, 2} } },
    -- Pandawushu Cloak
    {item=7174, ingredients={ {7278, 2}, {7282, 2} } },
    -- Akwadala Cloak
    {item=7230, ingredients={ {7222, 50}, {7258, 10}, {7266, 10}, {7268, 8}, {7267, 7} } },
    -- Terrdala Cloak
    {item=7231, ingredients={ {7308, 1}, {7224, 50}, {7267, 15}, {7297, 8}, {7258, 5}, {388, 5} } },
    -- Feudala Cloak
    {item=7232, ingredients={ {7276, 2}, {7278, 2}, {7259, 1}, {7225, 50}, {7258, 10}, {7277, 2}, {7275, 2} } },
    -- Aerdala Cloak
    {item=7233, ingredients={ {7259, 1}, {7223, 50}, {7297, 10}, {7258, 5}, {7278, 2}, {7277, 1} } },
    -- Kitsou Wrap
    {item=7340, ingredients={ {7282, 1}, {7279, 1}, {7277, 1}, {7278, 1}, {7275, 1}, {7281, 1} } },
    -- Cape Tenfuture
    {item=7515, ingredients={ {8398, 2}, {8381, 1}, {8251, 25}, {2247, 10}, {7381, 5}, {8403, 5}, {7393, 4}, {8383, 2} } },
    -- Turkocape
    {item=7552, ingredients={ {2599, 20}, {2586, 3}, {2588, 2}, {2598, 2}, {2591, 1}, {2594, 1} } },
    -- Gobkool Cape
    {item=7884, ingredients={ {7905, 2}, {2414, 1}, {7907, 1}, {304, 30}, {384, 30}, {8002, 5} } },
    -- Koalak Cloak
    {item=8007, ingredients={ {8062, 3}, {8059, 2}, {8050, 2}, {8060, 2}, {8061, 4}, {8002, 3} } },
    -- Tofu Cloak
    {item=8112, ingredients={ {301, 35}, {367, 30}, {366, 30}, {8557, 1} } },
    -- Golden Scaracape
    {item=8117, ingredients={ {6943, 1}, {6940, 1}, {6941, 1}, {8134, 1}, {8161, 1}, {6942, 1} } },
    -- Black Scaracape
    {item=8134, ingredients={ {8141, 1}, {6943, 1}, {6941, 1}, {8138, 1}, {6942, 1}, {6940, 1} } },
    -- Legendary Crackler Cloak
    {item=8181, ingredients={ {2305, 3}, {8102, 3}, {7028, 2}, {6931, 1}, {431, 30}, {467, 10}, {2304, 10} } },
    -- Red Piwi Cape
    {item=8231, ingredients={ {6900, 1}, {287, 1} } },
    -- Blue Piwi Cape
    {item=8232, ingredients={ {6897, 1}, {287, 1} } },
    -- Green Piwi Cape
    {item=8233, ingredients={ {287, 1}, {6899, 1} } },
    -- Purple Piwi Cape
    {item=8234, ingredients={ {6898, 1}, {287, 1} } },
    -- Pink Piwi Cape
    {item=8235, ingredients={ {287, 1}, {6903, 1} } },
    -- Yellow Piwi Cape
    {item=8236, ingredients={ {6902, 1}, {287, 1} } },
    -- Moowolf Cape
    {item=8265, ingredients={ {8085, 80}, {2577, 5}, {7379, 2}, {7385, 2}, {6954, 1}, {8144, 1}, {1696, 1}, {952, 1} } },
    -- Minotoror Cloak
    {item=8279, ingredients={ {8311, 40}, {840, 25}, {8321, 8}, {8250, 8}, {2998, 6}, {8065, 2}, {8066, 2} } },
    -- Minotot Cloak
    {item=8280, ingredients={ {8322, 15}, {8321, 15}, {2998, 15}, {8397, 10}, {8279, 1}, {8399, 1}, {8406, 1}, {8311, 100} } },
    -- Dragon Pig Cloak
    {item=8281, ingredients={ {2643, 1}, {8393, 1}, {2644, 1}, {2591, 25}, {8392, 18}, {8391, 18}, {8063, 4} } },
    -- Dreggon Cape
    {item=8286, ingredients={ {8355, 20}, {8360, 20}, {8352, 20}, {8356, 20}, {8354, 20}, {8353, 20}, {8367, 2}, {8364, 2} } },
    -- Shika's Cape
    {item=8302, ingredients={ {424, 8}, {426, 8}, {8059, 6}, {8062, 6}, {2550, 5}, {8002, 5}, {1890, 50} } },
    -- Little Red Waddling Cape
    {item=8366, ingredients={ {8798, 1}, {2383, 1}, {8994, 1}, {8783, 81}, {8781, 58}, {8791, 18}, {8754, 9}, {8753, 6} } },
    -- Black Rat Cape
    {item=8443, ingredients={ {8481, 40}, {1890, 35}, {2322, 30}, {2551, 15}, {2323, 10}, {8485, 3}, {8484, 1} } },
    -- White Rat Cape
    {item=8452, ingredients={ {1890, 35}, {2248, 30}, {8481, 20}, {1894, 10}, {2278, 10}, {8486, 3}, {2386, 1} } },
    -- Lord of the Rats' Ceremonial Cape
    {item=8458, ingredients={ {2299, 2}, {8452, 1}, {8443, 1}, {2550, 21}, {8486, 10}, {8485, 10}, {8493, 4}, {641, 2} } },
    -- Ancestral Treecape
    {item=8464, ingredients={ {8495, 1}, {2413, 1}, {948, 1}, {435, 150}, {7016, 45}, {8494, 3}, {8496, 2} } },
    -- Worn Soft Oak Cape
    {item=8472, ingredients={ {6486, 5}, {1660, 5}, {6488, 4}, {948, 1}, {8464, 1}, {2413, 1}, {6487, 1}, {463, 200} } },
    -- Cloak Orporal
    {item=8639, ingredients={ {8767, 10}, {8766, 10}, {8757, 4}, {8760, 4}, {8995, 1}, {2551, 65}, {8801, 20} } },
    -- Cape of Spades
    {item=8640, ingredients={ {8995, 1}, {1890, 65}, {8801, 20}, {8766, 10}, {8767, 10}, {8760, 4}, {8757, 4} } },
    -- F-Ha(r)t
    {item=8641, ingredients={ {372, 24}, {8801, 20}, {8766, 10}, {8767, 10}, {8757, 4}, {8760, 4}, {8995, 1} } },
    -- Cap Cape
    {item=8642, ingredients={ {8767, 10}, {8757, 4}, {8760, 4}, {8995, 1}, {2528, 71}, {8801, 20}, {8766, 10} } },
    -- Cape Sulitis
    {item=8643, ingredients={ {8757, 4}, {8760, 4}, {2512, 2}, {8995, 1}, {8801, 20}, {8766, 10}, {8767, 10} } },
    -- Cape o'Tbelly
    {item=8644, ingredients={ {8801, 20}, {1893, 16}, {8766, 10}, {8767, 10}, {8760, 4}, {8757, 4}, {8995, 1} } },
    -- Cape Ytal
    {item=8645, ingredients={ {8801, 20}, {1652, 15}, {8767, 10}, {8766, 10}, {8757, 4}, {8760, 4}, {8995, 1} } },
    -- Cape Anama
    {item=8646, ingredients={ {8801, 20}, {8767, 10}, {8766, 10}, {2495, 6}, {8760, 4}, {8757, 4}, {8995, 1} } },
    -- Cape Wuera
    {item=8647, ingredients={ {8760, 4}, {8757, 4}, {8995, 1}, {8801, 20}, {8766, 10}, {8767, 10}, {2499, 7} } },
    -- Cape Hulco
    {item=8648, ingredients={ {8801, 20}, {8767, 10}, {8766, 10}, {2583, 6}, {8757, 4}, {8760, 4}, {8995, 1} } },
    -- Cape Adossia
    {item=8649, ingredients={ {8766, 10}, {8760, 4}, {8757, 4}, {8995, 1}, {8801, 20}, {2491, 11}, {8767, 10} } },
    -- Cape Hernaum
    {item=8650, ingredients={ {8767, 10}, {8766, 10}, {2302, 5}, {8757, 4}, {8760, 4}, {8995, 1}, {8801, 20} } },
    -- Mopy King Sovereign Cape
    {item=8818, ingredients={ {8756, 42}, {8808, 11}, {8811, 6}, {8810, 5}, {8809, 3}, {8997, 1}, {8795, 1}, {8801, 160} } },
    -- Ouassingue Cape
    {item=8819, ingredients={ {8791, 8}, {8807, 7}, {8810, 6}, {8811, 4}, {952, 1}, {8801, 82}, {8760, 12} } },
    -- Powerful Dazzling Cloak
    {item=8866, ingredients={ {2281, 1}, {8800, 150}, {929, 50}, {8801, 50}, {2278, 10}, {2273, 10}, {8808, 3} } },
    -- Moopet Cape
    {item=8867, ingredients={ {2556, 15}, {8801, 8}, {8760, 6}, {8735, 3}, {8748, 2}, {8994, 1} } },
    -- Inky Veil
    {item=8876, ingredients={ {8801, 30}, {8832, 21}, {8807, 17}, {8800, 12}, {8813, 6}, {1829, 6}, {1859, 5}, {8812, 1} } },
    -- Improved Wooden Wings
    {item=8919, ingredients={ {8767, 1}, {8801, 1}, {8756, 1}, {1021, 1} } },
    -- Ragoat
    {item=9141, ingredients={ {8996, 1}, {8756, 80}, {8767, 52}, {8800, 40}, {8778, 31}, {9277, 12}, {9269, 10}, {9280, 2} } },
    -- Deadly Night Cape
    {item=9142, ingredients={ {9280, 2}, {8410, 2}, {6995, 1}, {8801, 60}, {8800, 26}, {8251, 21}, {9278, 12}, {8791, 9} } },
    -- Chee Cape
    {item=9180, ingredients={ {6811, 1}, {8384, 1}, {8383, 1}, {8387, 1}, {8766, 100}, {8786, 11}, {8795, 3}, {9401, 2} } },
    -- Phero-Cape
    {item=10846, ingredients={ {10909, 1}, {8360, 50}, {7294, 42}, {2286, 21}, {8359, 17}, {8323, 3} } },
    -- Ilyzawing Cape
    {item=11542, ingredients={ {2509, 63}, {10587, 30}, {8492, 25}, {11527, 15}, {11526, 10}, {11530, 10}, {8404, 2}, {8281, 1} } },
}


local requirements = {jobID = jobID, toolIDs = toolIDs}
registerCraftSkill(123, sk123Crafts, requirements, ingredientsForCraftJob(jobID), jobID)
registerCraftSkill(63, sk63Crafts, requirements, ingredientsForCraftJob(jobID), jobID)
registerCraftSkill(64, sk64Crafts, requirements, ingredientsForCraftJob(jobID), jobID)
