local jobID = ShoemakerJob
local toolIDs = {579}

-- Craft for Craft Boots
local sk13Crafts = {
    -- Bowisse's Boots
    {item=127, ingredients={ {887, 1}, {886, 1}, {486, 20}, {880, 1}, {487, 1} } },
    -- Small Concentration Boots
    {item=128, ingredients={ {486, 8}, {883, 7}, {881, 7}, {884, 7} } },
    -- Concentration Boots
    {item=129, ingredients={ {486, 8}, {884, 9}, {883, 9}, {881, 8} } },
    -- Iop's Crushers
    {item=130, ingredients={ {486, 14}, {444, 6}, {350, 4}, {304, 2} } },
    -- Small Gobball Boots
    {item=297, ingredients={ {881, 1}, {883, 1} } },
    -- Tofu Sandals
    {item=298, ingredients={ {366, 10}, {301, 10} } },
    -- Faillette Boots
    {item=299, ingredients={ {442, 10}, {304, 10}, {441, 10}, {486, 3} } },
    -- Celery-Tea's Boots
    {item=348, ingredients={ {384, 24}, {304, 18}, {350, 3}, {487, 2}, {887, 1}, {485, 1} } },
    -- Ragalde's Boots
    {item=733, ingredients={ {886, 1}, {486, 10}, {313, 5}, {466, 1}, {887, 1} } },
    -- Einekeineux
    {item=769, ingredients={ {884, 3}, {883, 1} } },
    -- Fecaflip
    {item=770, ingredients={ {883, 3}, {884, 1} } },
    -- Mulish Cat's Boots
    {item=784, ingredients={ {881, 3}, {883, 1} } },
    -- Kluh's Boots
    {item=837, ingredients={ {304, 7}, {883, 1} } },
    -- Small Strength Boots
    {item=861, ingredients={ {884, 3}, {304, 1} } },
    -- Intelligence Sandals
    {item=862, ingredients={ {304, 7}, {476, 1} } },
    -- Nimble Boots
    {item=863, ingredients={ {884, 5}, {384, 3} } },
    -- Chance Boots
    {item=864, ingredients={ {304, 5}, {884, 3} } },
    -- Great Concentration Boots
    {item=888, ingredients={ {884, 11}, {883, 11}, {881, 9}, {486, 8} } },
    -- Powerful Concentration Boots
    {item=889, ingredients={ {883, 13}, {884, 13}, {881, 10}, {486, 8} } },
    -- Gobball Boots
    {item=890, ingredients={ {881, 1}, {883, 2} } },
    -- Great Gobball Boots
    {item=891, ingredients={ {883, 2}, {881, 2} } },
    -- Powerful Gobball Boots
    {item=892, ingredients={ {883, 3}, {881, 2} } },
    -- Klime's Small Boots
    {item=902, ingredients={ {304, 5}, {486, 5}, {384, 4} } },
    -- Klime's Boots
    {item=903, ingredients={ {486, 7}, {304, 6}, {384, 6} } },
    -- Klime's Great Boots
    {item=904, ingredients={ {486, 8}, {304, 7}, {384, 7} } },
    -- Klime's Ultra-Powerful Boots
    {item=905, ingredients={ {384, 15}, {486, 15}, {886, 1}, {485, 1}, {887, 1} } },
    -- Small Chase Boots
    {item=906, ingredients={ {2507, 1}, {1891, 1}, {8002, 1}, {887, 2}, {901, 2} } },
    -- Chase Boots
    {item=907, ingredients={ {887, 4}, {1891, 1}, {8002, 1}, {2507, 1}, {901, 4} } },
    -- Great Chase Boots
    {item=908, ingredients={ {1891, 1}, {887, 6}, {901, 6}, {8002, 1}, {2507, 1} } },
    -- Powerful Chase Boots
    {item=909, ingredients={ {887, 8}, {901, 8}, {1891, 1}, {2507, 1}, {8002, 1} } },
    -- Hogmeiser's Boots
    {item=910, ingredients={ {886, 2}, {911, 1}, {487, 3}, {880, 3}, {887, 3}, {882, 3}, {901, 2} } },
    -- Hogmeiser's Golden Boots
    {item=912, ingredients={ {886, 1}, {313, 10}, {887, 3}, {910, 1}, {487, 1}, {466, 1}, {315, 1} } },
    -- Klime's Powerful Boots
    {item=924, ingredients={ {486, 9}, {304, 8}, {384, 7} } },
    -- Skill Boots
    {item=1621, ingredients={ {1455, 4}, {486, 4}, {304, 4}, {1663, 1} } },
    -- Power Boots
    {item=1622, ingredients={ {1663, 1}, {304, 4}, {486, 4}, {1456, 4} } },
    -- Anticipation Boots
    {item=1623, ingredients={ {304, 4}, {486, 4}, {1457, 4}, {1663, 1} } },
    -- Nexus' Boots
    {item=1624, ingredients={ {304, 6}, {883, 6}, {1455, 4} } },
    -- Apprenticeship Boots
    {item=1665, ingredients={ {880, 3}, {1613, 3}, {883, 1}, {884, 1}, {887, 1}, {486, 1} } },
    -- Apprentice Summoner Boots
    {item=1666, ingredients={ {2605, 10}, {1891, 10}, {2647, 10}, {1613, 2}, {2646, 2}, {887, 1} } },
    -- Winged Boots
    {item=2063, ingredients={ {2060, 50}, {1889, 50}, {2056, 5}, {2059, 1}, {2058, 1} } },
    -- Lymph a Tik Boots
    {item=2372, ingredients={ {1778, 5}, {2254, 5}, {1687, 2}, {594, 10}, {887, 10} } },
    -- Moon Boots
    {item=2373, ingredients={ {1018, 3}, {1687, 1}, {6441, 1}, {2357, 10}, {2275, 4}, {650, 4} } },
    -- Tarsy's Boots
    {item=2374, ingredients={ {1692, 2}, {442, 20}, {443, 20}, {2275, 10}, {409, 5} } },
    -- Orino Boots
    {item=2375, ingredients={ {901, 20}, {419, 20}, {2551, 10}, {2285, 1}, {2286, 100}, {2486, 30} } },
    -- Animal Boots
    {item=2384, ingredients={ {2666, 1}, {2292, 30}, {313, 30}, {2275, 20}, {1890, 20}, {2248, 20}, {2511, 20} } },
    -- Dark Animal Boots
    {item=2400, ingredients={ {2667, 1}, {2999, 1}, {840, 1}, {313, 30}, {1890, 20}, {2248, 20}, {1692, 10}, {2666, 2} } },
    -- Fire Kwakoboots
    {item=2421, ingredients={ {415, 20}, {388, 8}, {2275, 5}, {883, 4}, {884, 4} } },
    -- Gobboots
    {item=2422, ingredients={ {304, 10}, {884, 10}, {2467, 1}, {887, 1} } },
    -- Treeboots
    {item=2423, ingredients={ {435, 20}, {434, 10}, {2250, 1}, {2249, 25}, {437, 20} } },
    -- Eni Kere Miracle Boots
    {item=2435, ingredients={ {2511, 2}, {1688, 1}, {2271, 4}, {2275, 4}, {887, 2} } },
    -- Royal Gobboots
    {item=2442, ingredients={ {886, 1}, {304, 150}, {883, 70}, {884, 70}, {887, 50}, {2422, 1} } },
    -- Jelliboots
    {item=2470, ingredients={ {368, 5}, {757, 5}, {311, 10}, {994, 6}, {995, 6}, {993, 6} } },
    -- Adventurer Boots
    {item=2476, ingredients={ {297, 3}, {298, 3} } },
    -- Caraboots
    {item=2530, ingredients={ {2613, 20}, {2618, 100}, {1002, 60}, {2609, 20}, {2610, 20}, {2611, 20} } },
    -- Croboots
    {item=2545, ingredients={ {2060, 12}, {1889, 10}, {1692, 1}, {2063, 1}, {2056, 1} } },
    -- Satisfaction Boots
    {item=3207, ingredients={ {441, 10}, {444, 10}, {388, 5} } },
    -- Gobball Breeder Boots
    {item=6470, ingredients={ {2315, 30}, {2271, 10}, {2501, 10}, {2264, 10}, {3208, 4}, {2555, 1} } },
    -- Patent Lousy Pig Shoes
    {item=6471, ingredients={ {2301, 4}, {2496, 3}, {2497, 2}, {2486, 2}, {2482, 1} } },
    -- Wabbit Flip-Flops
    {item=6493, ingredients={ {305, 70}, {372, 35}, {2288, 25}, {360, 5}, {361, 5}, {419, 2} } },
    -- Bearman's Boots
    {item=6721, ingredients={ {304, 4}, {2275, 1}, {2511, 1}, {6844, 1} } },
    -- Ambusherboots
    {item=6744, ingredients={ {2605, 8}, {6737, 8}, {1018, 5}, {2624, 2}, {6735, 1} } },
    -- Ice Kwakaboots
    {item=6753, ingredients={ {884, 4}, {883, 4}, {414, 20}, {388, 8}, {2275, 5} } },
    -- Earth Kwakoboots
    {item=6754, ingredients={ {1141, 20}, {388, 8}, {2275, 5}, {884, 4}, {883, 4} } },
    -- Wind Kwakoboots
    {item=6755, ingredients={ {388, 8}, {2275, 5}, {884, 4}, {883, 4}, {416, 20} } },
    -- Farle's Hooves
    {item=6774, ingredients={ {470, 6}, {2032, 3}, {2021, 3}, {2511, 20}, {2275, 20}, {2250, 8}, {2264, 6} } },
    -- Wild Boots
    {item=6794, ingredients={ {2661, 8}, {306, 8}, {2659, 10} } },
    -- Chief Crocodyl Sharp Slippers
    {item=6825, ingredients={ {6740, 1}, {1663, 20}, {1664, 10}, {1613, 10}, {6739, 5}, {1614, 5} } },
    -- Boar Feet
    {item=6909, ingredients={ {386, 2}, {486, 2}, {388, 3} } },
    -- Crackler Boots
    {item=6933, ingredients={ {2252, 11}, {448, 5}, {447, 3}, {547, 1}, {450, 1} } },
    -- Boowolf Boots
    {item=6953, ingredients={ {440, 25}, {1690, 25}, {439, 20}, {2578, 10}, {2576, 1}, {438, 1} } },
    -- Flee-Flops
    {item=7104, ingredients={ {7273, 10}, {7017, 1}, {7013, 12}, {7282, 12}, {7279, 10}, {7016, 10} } },
    -- Larvaboots
    {item=7107, ingredients={ {7423, 1}, {519, 30}, {362, 15}, {363, 14}, {364, 14} } },
    -- Cruella Sandals
    {item=7214, ingredients={ {2275, 5}, {7013, 2}, {1891, 1}, {7273, 1}, {7282, 1} } },
    -- Siren Thong
    {item=7215, ingredients={ {7298, 4}, {7299, 3}, {7269, 1}, {7380, 1}, {7281, 15}, {7280, 10}, {7014, 9}, {7289, 5} } },
    -- Sandals Koholiks
    {item=7216, ingredients={ {7281, 9}, {7273, 7}, {7280, 6}, {7301, 5}, {7271, 1}, {7261, 1} } },
    -- Still Sandals
    {item=7217, ingredients={ {7300, 4}, {7283, 3}, {7299, 3}, {7383, 1}, {7301, 25}, {7014, 10}, {7289, 6}, {7298, 5} } },
    -- Sandals Adin
    {item=7218, ingredients={ {7260, 15}, {7030, 8}, {7270, 1}, {7017, 1}, {2275, 25}, {7273, 15} } },
    -- Sandals Ailuya
    {item=7219, ingredients={ {7273, 10}, {7282, 10}, {7281, 9}, {7016, 4}, {7280, 3}, {7272, 1} } },
    -- Akwadala Geta
    {item=7242, ingredients={ {7263, 3}, {7222, 50}, {7262, 5}, {7013, 5}, {7273, 5} } },
    -- Terrdala Geta
    {item=7243, ingredients={ {7273, 15}, {7016, 15}, {7281, 10}, {7017, 1}, {7303, 1}, {7224, 50} } },
    -- Feudala Geta
    {item=7244, ingredients={ {7280, 10}, {7303, 1}, {7225, 50}, {7013, 12}, {7016, 10}, {7273, 10} } },
    -- Aerdala Geta
    {item=7245, ingredients={ {7223, 50}, {7273, 5}, {7016, 3}, {7279, 2}, {7030, 1}, {315, 1} } },
    -- Sandal Titude
    {item=7513, ingredients={ {8346, 1}, {2528, 100}, {7264, 26}, {7925, 15}, {7016, 12}, {2514, 10}, {7026, 3}, {8383, 2} } },
    -- Harry Boots
    {item=7514, ingredients={ {8053, 10}, {7379, 2}, {8082, 2}, {8400, 1}, {8401, 1}, {8249, 100}, {8052, 28}, {8388, 20} } },
    -- Turkoboots
    {item=7554, ingredients={ {2179, 10}, {2586, 10}, {2591, 3}, {2599, 50}, {2588, 10}, {2598, 10} } },
    -- Gobkool Boots
    {item=7883, ingredients={ {304, 20}, {7905, 2}, {7907, 1}, {2422, 1}, {384, 20} } },
    -- Koalak Boots
    {item=8006, ingredients={ {8060, 5}, {8062, 5}, {8061, 5}, {8050, 2}, {8054, 1}, {8085, 1} } },
    -- Tofu Slippers
    {item=8111, ingredients={ {301, 12}, {367, 9}, {8557, 1}, {366, 22} } },
    -- Golden Scaraboots
    {item=8122, ingredients={ {1456, 10}, {1457, 10}, {1458, 10}, {8141, 10}, {1455, 10}, {8161, 1} } },
    -- Farmer Boots
    {item=8128, ingredients={ {401, 10}, {2661, 2}, {399, 2} } },
    -- Legendary Crackler Boots
    {item=8146, ingredients={ {431, 15}, {467, 8}, {466, 8}, {2306, 4}, {6457, 4}, {8102, 3}, {6933, 1} } },
    -- Red Piwi Sandals
    {item=8225, ingredients={ {287, 1}, {6900, 1} } },
    -- Blue Piwi Sandals
    {item=8226, ingredients={ {6897, 1}, {287, 1} } },
    -- Purple Piwi Sandals
    {item=8227, ingredients={ {6898, 1}, {287, 1} } },
    -- Green Piwi Sandals
    {item=8228, ingredients={ {6899, 1}, {287, 1} } },
    -- Yellow Piwi Sandals
    {item=8229, ingredients={ {6902, 1}, {287, 1} } },
    -- Pink Piwi Sandals
    {item=8230, ingredients={ {6903, 1}, {287, 1} } },
    -- Moowolf Boots
    {item=8264, ingredients={ {6953, 1}, {1890, 160}, {8388, 30}, {1691, 10}, {8385, 5}, {8389, 1}, {8401, 1}, {1696, 1} } },
    -- Minotoror Boots
    {item=8276, ingredients={ {880, 9}, {2998, 4}, {2999, 2}, {2666, 1}, {8312, 120}, {8314, 100}, {8084, 15} } },
    -- Minotot Sandals
    {item=8277, ingredients={ {8408, 1}, {8407, 1}, {8276, 1}, {2384, 1}, {8054, 30}, {8052, 25}, {8388, 10}, {2400, 1} } },
    -- Dragon Pig Mules
    {item=8278, ingredients={ {8052, 9}, {2645, 5}, {8393, 2}, {8390, 25}, {8392, 15}, {2464, 15}, {8391, 14} } },
    -- Dreggon Boots
    {item=8291, ingredients={ {8053, 5}, {8367, 4}, {8383, 1}, {8358, 1}, {8054, 15}, {8356, 12}, {8359, 5}, {8388, 5} } },
    -- Shika's Clogs
    {item=8301, ingredients={ {2035, 2}, {435, 100}, {8314, 30}, {7925, 10}, {8050, 3}, {8054, 3}, {1671, 2} } },
    -- Black Rat Boots
    {item=8446, ingredients={ {2482, 2}, {8491, 2}, {3002, 2}, {8482, 2}, {733, 1}, {7030, 35}, {2271, 5} } },
    -- White Rat Boots
    {item=8456, ingredients={ {8492, 3}, {756, 1}, {2287, 1}, {8482, 1}, {2063, 1}, {2275, 32}, {8483, 15} } },
    -- Lord of the Rats' Ceremonial Boots
    {item=8462, ingredients={ {8483, 50}, {2322, 50}, {2451, 40}, {2467, 20}, {8493, 3}, {8456, 1}, {8446, 1}, {417, 100} } },
    -- Ancestral Shin Guards
    {item=8467, ingredients={ {1612, 10}, {470, 10}, {1610, 10}, {918, 3}, {8494, 2}, {2423, 1}, {7925, 20} } },
    -- Soft Oak Flip-Flops
    {item=8471, ingredients={ {1611, 35}, {1610, 25}, {1612, 20}, {6488, 4}, {6486, 3}, {2423, 1}, {8467, 1}, {7925, 40} } },
    -- Boot-a-Hoop
    {item=8663, ingredients={ {8753, 2}, {8996, 1}, {2496, 60}, {8763, 20}, {8756, 20}, {8766, 10}, {8767, 10} } },
    -- Sleephairs
    {item=8664, ingredients={ {8753, 2}, {8996, 1}, {2301, 60}, {8763, 20}, {8756, 20}, {8767, 20}, {8766, 10} } },
    -- Boots Hox
    {item=8665, ingredients={ {2497, 40}, {8756, 20}, {8763, 20}, {8766, 10}, {8767, 10}, {8753, 2}, {8996, 1} } },
    -- Shal'Hal Boots
    {item=8666, ingredients={ {8753, 2}, {8996, 1}, {8763, 20}, {8756, 20}, {8766, 10}, {8309, 10}, {8767, 10} } },
    -- Boots Ulism
    {item=8667, ingredients={ {8767, 10}, {8766, 10}, {8753, 2}, {8996, 1}, {8763, 20}, {8756, 20}, {2482, 16} } },
    -- Boots Hanik
    {item=8668, ingredients={ {2288, 25}, {8756, 20}, {8763, 20}, {8766, 10}, {8767, 10}, {8753, 2}, {8996, 1} } },
    -- Nailed Thongs
    {item=8669, ingredients={ {8756, 20}, {8767, 10}, {8766, 10}, {8753, 2}, {8996, 1}, {2584, 36}, {8763, 20} } },
    -- Wawka Boots
    {item=8670, ingredients={ {8767, 10}, {8766, 10}, {8753, 2}, {8996, 1}, {2249, 37}, {8763, 20}, {8756, 20} } },
    -- Getas Bernacles
    {item=8713, ingredients={ {2551, 63}, {8763, 20}, {8756, 20}, {8767, 10}, {8766, 10}, {8753, 2}, {8996, 1} } },
    -- Crusuede Shoes
    {item=8726, ingredients={ {8753, 2}, {8996, 1}, {2528, 42}, {8756, 20}, {8763, 20}, {8766, 10}, {8767, 10} } },
    -- Relief Boots
    {item=8727, ingredients={ {8753, 2}, {8996, 1}, {7297, 23}, {8763, 20}, {8756, 20}, {8766, 10}, {8767, 10} } },
    -- Veggie Boots
    {item=8728, ingredients={ {7260, 41}, {8756, 20}, {8763, 20}, {8766, 10}, {8767, 10}, {8753, 2}, {8996, 1} } },
    -- Boots Kwish
    {item=8858, ingredients={ {8756, 29}, {8807, 7}, {8776, 6}, {8810, 5}, {8755, 4}, {8811, 4}, {8801, 90} } },
    -- Round Kimbo Sandals
    {item=8861, ingredients={ {8793, 8}, {8758, 3}, {7217, 1}, {8998, 1}, {8789, 1}, {8996, 1}, {8778, 98}, {8790, 69} } },
    -- Wooden Treeckler Boots
    {item=8869, ingredients={ {8077, 8}, {8770, 1}, {1558, 1}, {8797, 61}, {7013, 42}, {8832, 26}, {8796, 16}, {7014, 12} } },
    -- Ta Boots
    {item=9139, ingredients={ {8776, 10}, {8770, 2}, {8769, 30}, {8792, 25}, {9263, 25}, {8064, 20}, {9279, 15}, {9269, 12} } },
    -- Spore Boots
    {item=9140, ingredients={ {8387, 1}, {426, 60}, {8800, 28}, {9269, 17}, {8805, 16}, {9263, 15}, {9401, 2}, {8384, 1} } },
    -- Cherry Bloopts
    {item=9158, ingredients={ {1775, 50}, {2556, 50}, {1776, 10}, {9383, 1}, {6909, 1}, {9388, 1} } },
    -- Pippin Bloopts
    {item=9159, ingredients={ {9388, 1}, {2556, 50}, {1773, 50}, {1774, 10}, {9383, 1}, {6909, 1} } },
    -- Coco Bloopts
    {item=9160, ingredients={ {2556, 50}, {1770, 50}, {1772, 10}, {6909, 1}, {9388, 1}, {9383, 1} } },
    -- Indigo Bloopts
    {item=9161, ingredients={ {1778, 10}, {6909, 1}, {9388, 1}, {9383, 1}, {2556, 50}, {1777, 50} } },
    -- Royal Cherry Bloopts
    {item=9162, ingredients={ {1775, 100}, {2556, 100}, {9382, 10}, {9383, 10}, {9388, 5}, {9158, 1}, {9384, 1} } },
    -- Royal Pippin Bloopts
    {item=9163, ingredients={ {9382, 10}, {9388, 5}, {9387, 1}, {9159, 1}, {1773, 100}, {2556, 100}, {9383, 10} } },
    -- Royal Coco Bloopts
    {item=9164, ingredients={ {1770, 100}, {9383, 10}, {9382, 10}, {9388, 5}, {9160, 1}, {9385, 1}, {2556, 100} } },
    -- Royal Indigo Bloopts
    {item=9165, ingredients={ {9161, 1}, {2556, 100}, {1777, 100}, {9382, 10}, {9383, 10}, {9388, 5}, {9386, 1} } },
    -- Royal Rainbow Bloopts
    {item=9166, ingredients={ {9162, 1}, {9164, 1}, {9381, 10}, {9389, 5}, {9165, 1}, {9391, 1}, {9163, 1} } },
    -- Pierced Boots
    {item=9182, ingredients={ {8761, 10}, {8996, 2}, {9401, 1}, {6807, 1}, {2527, 1}, {8763, 40}, {8790, 31}, {8388, 15} } },
    -- Qu'Tanned Boots
    {item=11547, ingredients={ {11538, 16}, {11533, 8}, {2527, 1}, {7514, 1}, {2528, 67}, {8001, 36}, {11534, 23}, {2674, 20} } },
}

-- Craft for Craft a Belt
local sk14Crafts = {
    -- Power Belt
    {item=156, ingredients={ {304, 10}, {884, 10}, {486, 10}, {444, 8}, {313, 8} } },
    -- Tot's Belt
    {item=203, ingredients={ {313, 2}, {486, 8}, {304, 4}, {887, 4}, {446, 2} } },
    -- Small Vital Belt
    {item=252, ingredients={ {441, 1}, {883, 1} } },
    -- Vital Belt
    {item=253, ingredients={ {441, 2}, {883, 2} } },
    -- Great Vital Belt
    {item=254, ingredients={ {441, 3}, {883, 3} } },
    -- Imposing Vital Belt
    {item=255, ingredients={ {883, 4}, {441, 4} } },
    -- Small Strong Belt
    {item=256, ingredients={ {312, 1}, {883, 1} } },
    -- Strong Belt
    {item=257, ingredients={ {883, 2}, {312, 2} } },
    -- Great Strong Belt
    {item=258, ingredients={ {883, 3}, {312, 3} } },
    -- Imposing Strong Belt
    {item=259, ingredients={ {312, 4}, {883, 4} } },
    -- Small Nimble Belt
    {item=260, ingredients={ {884, 1}, {312, 1} } },
    -- Nimble Belt
    {item=261, ingredients={ {312, 2}, {884, 2} } },
    -- Great Nimble Belt
    {item=262, ingredients={ {884, 4}, {312, 2} } },
    -- Imposing Nimble Belt
    {item=263, ingredients={ {884, 4}, {312, 4} } },
    -- Stamina Belt
    {item=356, ingredients={ {486, 16}, {304, 10}, {444, 6}, {883, 6} } },
    -- Blo'up Belt
    {item=734, ingredients={ {444, 5}, {304, 5}, {486, 5} } },
    -- Chance Belt
    {item=860, ingredients={ {441, 2}, {884, 4} } },
    -- Adelus
    {item=1487, ingredients={ {901, 9}, {304, 2}, {887, 2}, {486, 2}, {350, 1} } },
    -- Luthuthu Belt
    {item=1667, ingredients={ {2275, 10}, {648, 10}, {887, 8}, {1663, 4}, {486, 12}, {1613, 10} } },
    -- Trembling Dodu Belt
    {item=1668, ingredients={ {887, 4}, {1663, 4}, {1613, 2}, {486, 12}, {2511, 10}, {901, 10} } },
    -- Event Belt
    {item=1669, ingredients={ {2271, 2}, {487, 2}, {901, 1}, {316, 1}, {465, 1}, {1667, 1}, {1613, 3} } },
    -- Celerity Belt
    {item=1700, ingredients={ {1889, 30}, {301, 30}, {2271, 1}, {2273, 1} } },
    -- Pocket Belt
    {item=1701, ingredients={ {443, 8}, {444, 8}, {486, 4}, {388, 4} } },
    -- Crocoburio's Power
    {item=1720, ingredients={ {1663, 20}, {1664, 10}, {310, 10}, {2664, 1}, {883, 20} } },
    -- The Cruncher
    {item=2233, ingredients={ {305, 30}, {2179, 20}, {1664, 4}, {887, 4} } },
    -- The Friend Lehunui
    {item=2367, ingredients={ {445, 8}, {2511, 5}, {761, 5}, {646, 5}, {2273, 1} } },
    -- Ramoune the Eternal
    {item=2368, ingredients={ {445, 8}, {310, 5}, {2511, 5}, {2273, 1}, {2336, 1} } },
    -- Powerful Dazzling Belt
    {item=2369, ingredients={ {2511, 20}, {2275, 20}, {883, 20}, {887, 20}, {486, 20}, {965, 50}, {884, 20}, {304, 20} } },
    -- The Vegete Hative
    {item=2370, ingredients={ {421, 40}, {306, 20}, {594, 10}, {2254, 10}, {1772, 5} } },
    -- Comfleecy Belt
    {item=2371, ingredients={ {313, 10}, {649, 10}, {2315, 5}, {650, 5}, {1890, 2} } },
    -- Peeka Belt
    {item=2403, ingredients={ {1894, 10}, {2512, 3}, {2511, 2}, {2056, 1}, {2513, 1} } },
    -- Fire Kwakelt
    {item=2427, ingredients={ {415, 15}, {2511, 8}, {2275, 5}, {1891, 3}, {2605, 1} } },
    -- Gobball Belt
    {item=2428, ingredients={ {384, 50}, {884, 5}, {883, 5}, {304, 5} } },
    -- Treechelt
    {item=2429, ingredients={ {435, 20}, {434, 10}, {437, 10}, {463, 1}, {2250, 1} } },
    -- Royal Gobball Belt
    {item=2444, ingredients={ {304, 100}, {884, 50}, {883, 50}, {887, 30}, {886, 1}, {2428, 1} } },
    -- Jellibelt
    {item=2471, ingredients={ {995, 5}, {2436, 1}, {369, 20}, {368, 15}, {757, 15}, {994, 10} } },
    -- Adventurer Belt
    {item=2477, ingredients={ {255, 2}, {259, 2} } },
    -- Ice Kwakelt
    {item=2537, ingredients={ {414, 15}, {2511, 8}, {2275, 5}, {1891, 3}, {2605, 1} } },
    -- Crowish Belt
    {item=2655, ingredients={ {887, 10}, {2511, 10}, {2275, 10}, {2285, 5}, {2622, 1}, {1892, 1}, {2525, 1}, {2059, 10} } },
    -- Chouquish Belt
    {item=2677, ingredients={ {2271, 2}, {2622, 1}, {2621, 1}, {1663, 30}, {1613, 10}, {2511, 10}, {998, 4} } },
    -- Kokokette Belt
    {item=2678, ingredients={ {2618, 1}, {883, 10}, {1002, 10}, {997, 5}, {2624, 3} } },
    -- Yellow Turtle Belt
    {item=2681, ingredients={ {2275, 10}, {446, 10}, {2511, 10}, {444, 10}, {465, 1}, {2611, 1} } },
    -- Blue Turtle Belt
    {item=2683, ingredients={ {446, 10}, {2275, 10}, {2511, 10}, {444, 10}, {465, 1}, {2613, 1} } },
    -- Green Turtle Belt
    {item=2685, ingredients={ {444, 10}, {2275, 10}, {446, 10}, {2511, 10}, {2609, 1}, {465, 1} } },
    -- Red Turtle Belt
    {item=2687, ingredients={ {2610, 1}, {446, 10}, {2275, 10}, {2511, 10}, {444, 10}, {465, 1} } },
    -- Sticky Belt
    {item=2688, ingredients={ {1018, 10}, {442, 10}, {2275, 2}, {2607, 1}, {2605, 1}, {2602, 10} } },
    -- Banisation Belt
    {item=2689, ingredients={ {2599, 10}, {2179, 4}, {2511, 4}, {2588, 1} } },
    -- Wild Banisation Belt
    {item=2710, ingredients={ {312, 10}, {2179, 4}, {2586, 1}, {2591, 1}, {2598, 1} } },
    -- Crab Belt
    {item=2803, ingredients={ {379, 10}, {2303, 1} } },
    -- Boowish Belt
    {item=2804, ingredients={ {438, 10}, {651, 2}, {292, 2}, {2805, 1}, {291, 10}, {2511, 10}, {2575, 10} } },
    -- Moowish Belt
    {item=2807, ingredients={ {2577, 10}, {2806, 1}, {2582, 1}, {1696, 1}, {2275, 30}, {438, 20}, {440, 10}, {1691, 10} } },
    -- Drasmuty Belt
    {item=2808, ingredients={ {313, 20}, {2643, 4}, {2644, 2}, {479, 2}, {477, 1}, {316, 1}, {487, 1} } },
    -- Chafeerce Belt
    {item=2809, ingredients={ {485, 1}, {2323, 1}, {310, 4}, {1675, 1}, {430, 1} } },
    -- Spicy Belt
    {item=2810, ingredients={ {2572, 2}, {2573, 2}, {2574, 1}, {1672, 1} } },
    -- Ouginiakal Belt
    {item=2811, ingredients={ {2321, 10}, {2286, 10}, {443, 10}, {2248, 2}, {2285, 1}, {2493, 1} } },
    -- The Xerbo
    {item=3206, ingredients={ {2480, 1}, {2503, 1}, {2282, 1}, {2278, 1}, {2513, 2}, {2464, 1} } },
    -- Aman Date Belt
    {item=6450, ingredients={ {2525, 1}, {2621, 1}, {2275, 30}, {2248, 30}, {1890, 30}, {2271, 5}, {2559, 2}, {1694, 2} } },
    -- GM Wabbit Y-fwonts
    {item=6498, ingredients={ {372, 150}, {360, 50}, {419, 50}, {654, 50}, {418, 20}, {406, 1} } },
    -- Bearman's Belt
    {item=6724, ingredients={ {6845, 1}, {884, 4}, {883, 4}, {304, 3} } },
    -- Deceitful Belt
    {item=6745, ingredients={ {2254, 15}, {2624, 12}, {1018, 3}, {6737, 3}, {2605, 1} } },
    -- Earth Kwakelt
    {item=6758, ingredients={ {2605, 1}, {1141, 15}, {2511, 8}, {2275, 5}, {1891, 3} } },
    -- Wind Kwakelt
    {item=6759, ingredients={ {2275, 5}, {1891, 3}, {2605, 1}, {416, 15}, {2511, 8} } },
    -- Farlebelt
    {item=6776, ingredients={ {2264, 5}, {6739, 3}, {2275, 20}, {486, 20}, {2511, 20}, {1891, 10}, {2271, 6} } },
    -- Flowery Belt
    {item=6796, ingredients={ {309, 15}, {373, 11}, {374, 11} } },
    -- Chief Crocodyl Belt
    {item=6831, ingredients={ {6739, 1}, {1663, 35}, {1613, 25}, {467, 2}, {6740, 1}, {315, 1} } },
    -- Boar Belt
    {item=6908, ingredients={ {387, 1}, {388, 1}, {386, 1} } },
    -- Arachnobelt
    {item=6912, ingredients={ {307, 2}, {365, 2} } },
    -- Robber Belt
    {item=6925, ingredients={ {252, 3}, {260, 3}, {256, 3} } },
    -- Prespic Belt
    {item=6929, ingredients={ {2574, 3}, {407, 1}, {2572, 1}, {2573, 1}, {2571, 4} } },
    -- Crackler Belt
    {item=6935, ingredients={ {2252, 24}, {448, 5}, {447, 3}, {450, 2}, {431, 1} } },
    -- Green Scarabelt
    {item=6948, ingredients={ {1466, 40}, {398, 15}, {1458, 10}, {2293, 3}, {396, 1} } },
    -- White Scarabelt
    {item=6949, ingredients={ {1467, 40}, {398, 15}, {1456, 10}, {2290, 3}, {396, 1} } },
    -- Red Scarabelt
    {item=6950, ingredients={ {2292, 3}, {396, 1}, {1465, 40}, {398, 15}, {1457, 10} } },
    -- Blue Scarabelt
    {item=6951, ingredients={ {2291, 3}, {396, 1}, {1464, 40}, {398, 15}, {1455, 10} } },
    -- Boowolf Thong
    {item=6955, ingredients={ {439, 15}, {2579, 10}, {440, 10}, {1690, 5}, {2576, 1} } },
    -- Ditchy Belt
    {item=7139, ingredients={ {7298, 3}, {7300, 3}, {7299, 2}, {7382, 1}, {7303, 15}, {7030, 12}, {7280, 12}, {7260, 10} } },
    -- Xenature
    {item=7140, ingredients={ {7302, 2}, {7259, 2}, {7379, 1}, {7280, 12}, {7281, 11}, {7301, 6}, {7299, 6}, {2550, 3} } },
    -- Akwadala Belt
    {item=7238, ingredients={ {7222, 50}, {2275, 10}, {7260, 4}, {7273, 2}, {1891, 2} } },
    -- Terrdala Belt
    {item=7239, ingredients={ {7281, 2}, {7277, 1}, {7224, 50}, {7260, 5}, {7273, 2} } },
    -- Feudala Belt
    {item=7240, ingredients={ {7258, 20}, {7016, 20}, {7260, 15}, {7280, 2}, {7259, 1}, {7225, 50}, {2275, 25} } },
    -- Aerdala Belt
    {item=7241, ingredients={ {7223, 50}, {2275, 20}, {7273, 20}, {1891, 13}, {7016, 10}, {7260, 10} } },
    -- Grendibelt
    {item=7559, ingredients={ {3206, 1}, {8916, 1}, {8767, 58}, {7032, 18}, {8807, 12}, {8776, 6}, {6457, 5}, {8761, 1} } },
    -- Gobkool Belt
    {item=7885, ingredients={ {304, 20}, {384, 20}, {7907, 2}, {7905, 2}, {2428, 1} } },
    -- Golden Lady's Belt
    {item=7902, ingredients={ {313, 100}, {2499, 20}, {2541, 10}, {2529, 10}, {2654, 2}, {2454, 1} } },
    -- Koalak Belt
    {item=8008, ingredients={ {8059, 1}, {8054, 1}, {8061, 1}, {8060, 1}, {8062, 1}, {8050, 1} } },
    -- Tofu Belt
    {item=8113, ingredients={ {301, 10}, {366, 9}, {367, 8}, {8557, 1} } },
    -- Golden Scarabelt
    {item=8119, ingredients={ {6951, 1}, {6949, 1}, {8132, 1}, {6950, 1}, {6948, 1}, {8161, 1} } },
    -- Farmer Beltudas
    {item=8124, ingredients={ {390, 2}, {427, 15}, {373, 10}, {306, 10} } },
    -- Black Scarabelt
    {item=8132, ingredients={ {6948, 1}, {8141, 1}, {6949, 1}, {6951, 1}, {6950, 1} } },
    -- Legendary Crackler Belt
    {item=8152, ingredients={ {2304, 10}, {431, 10}, {746, 10}, {466, 5}, {467, 5}, {8102, 2}, {6935, 1} } },
    -- Red Piwi Belt
    {item=8237, ingredients={ {6900, 1}, {287, 1} } },
    -- Blue Piwi Belt
    {item=8238, ingredients={ {6897, 1}, {287, 1} } },
    -- Purple Piwi Belt
    {item=8239, ingredients={ {287, 1}, {6898, 1} } },
    -- Green Piwi Belt
    {item=8240, ingredients={ {287, 1}, {6899, 1} } },
    -- Yellow Piwi Belt
    {item=8241, ingredients={ {287, 1}, {6902, 1} } },
    -- Pink Piwi Belt
    {item=8242, ingredients={ {6903, 1}, {287, 1} } },
    -- Moowolf Belt
    {item=8266, ingredients={ {8065, 14}, {8066, 11}, {8399, 3}, {8397, 3}, {2804, 1}, {6955, 1}, {8144, 1}, {8250, 80} } },
    -- Minotoror Belt
    {item=8282, ingredients={ {2998, 4}, {8054, 2}, {2999, 1}, {8314, 80}, {8312, 75}, {8356, 8}, {8391, 5} } },
    -- Minotot Belt
    {item=8283, ingredients={ {8053, 12}, {2998, 8}, {2999, 3}, {8400, 1}, {8406, 1}, {8282, 1}, {8407, 1}, {8250, 25} } },
    -- Dreggon Belt
    {item=8288, ingredients={ {8356, 11}, {8053, 10}, {8365, 1}, {8344, 1}, {8354, 20}, {8352, 20}, {8355, 20}, {8362, 15} } },
    -- Shika's Belt
    {item=8303, ingredients={ {2275, 30}, {8390, 10}, {8391, 10}, {8392, 10}, {7907, 5}, {8323, 2}, {2480, 1} } },
    -- Black Rat Belt
    {item=8447, ingredients={ {2275, 50}, {7030, 30}, {8481, 15}, {8484, 2}, {8491, 2}, {3205, 1}, {8485, 1} } },
    -- White Rat Belt
    {item=8455, ingredients={ {749, 1}, {8483, 25}, {8492, 3}, {7036, 3}, {8482, 2}, {7035, 2}, {3204, 1} } },
    -- Lord of the Rats' Ceremonial Belt
    {item=8461, ingredients={ {8483, 50}, {8484, 5}, {8482, 5}, {8493, 3}, {8455, 1}, {8447, 1}, {2562, 1}, {2322, 100} } },
    -- Ancestral Treechelt
    {item=8468, ingredients={ {1612, 10}, {918, 4}, {2429, 1}, {920, 1}, {1611, 30}, {2250, 25}, {1610, 10} } },
    -- Autumnal Soft Oak G-String
    {item=8473, ingredients={ {1611, 50}, {1610, 25}, {1612, 20}, {6486, 9}, {6488, 8}, {2429, 1}, {8468, 1}, {464, 130} } },
    -- Bloody Belt
    {item=8651, ingredients={ {2596, 10}, {8784, 2}, {8770, 1}, {8782, 30}, {8780, 28}, {8781, 24}, {8778, 20}, {8779, 18} } },
    -- Belt Sterous
    {item=8652, ingredients={ {2468, 2}, {8784, 2}, {8770, 1}, {8782, 30}, {8780, 28}, {8781, 24}, {8778, 20}, {8779, 18} } },
    -- Clinkin Belt
    {item=8653, ingredients={ {8778, 20}, {8779, 18}, {2297, 2}, {8784, 2}, {8770, 1}, {8782, 30}, {8780, 28}, {8781, 24} } },
    -- Sleepless Belt
    {item=8654, ingredients={ {2510, 2}, {8770, 1}, {8782, 30}, {8780, 28}, {8781, 24}, {8778, 20}, {8779, 18}, {8784, 2} } },
    -- Blub Belt
    {item=8655, ingredients={ {8781, 24}, {8778, 20}, {8779, 18}, {8784, 2}, {2631, 2}, {8770, 1}, {8782, 30}, {8780, 28} } },
    -- Belt Atio
    {item=8656, ingredients={ {8784, 2}, {2267, 2}, {8770, 1}, {8782, 30}, {8780, 28}, {8781, 24}, {8778, 20}, {8779, 18} } },
    -- Strap Pado
    {item=8657, ingredients={ {8784, 2}, {8770, 1}, {8782, 30}, {8780, 28}, {8781, 24}, {8778, 20}, {8779, 18}, {2673, 2} } },
    -- Diezzle Belt
    {item=8658, ingredients={ {8784, 2}, {8770, 1}, {2669, 1}, {8782, 30}, {8780, 28}, {8781, 24}, {8778, 20}, {8779, 18} } },
    -- Sticky Strap
    {item=8659, ingredients={ {8781, 24}, {8778, 20}, {8779, 18}, {8784, 2}, {2651, 1}, {8770, 1}, {8782, 30}, {8780, 28} } },
    -- Targ Belt
    {item=8660, ingredients={ {8784, 2}, {2652, 1}, {8770, 1}, {8782, 30}, {8780, 28}, {8781, 24}, {8778, 20}, {8779, 18} } },
    -- Girdle Belt
    {item=8661, ingredients={ {8779, 18}, {8784, 2}, {8770, 1}, {2650, 1}, {8782, 30}, {8780, 28}, {8781, 24}, {8778, 20} } },
    -- Belt Urgid
    {item=8662, ingredients={ {8782, 30}, {8780, 28}, {8781, 24}, {8778, 20}, {8779, 18}, {8784, 2}, {2649, 1}, {8770, 1} } },
    -- Bherbal Bherb Belt
    {item=8856, ingredients={ {8764, 4}, {8996, 2}, {6450, 1}, {8798, 1}, {8793, 12}, {8776, 11}, {8808, 9}, {8792, 8} } },
    -- Parkinn Pan Pikokowal Belt
    {item=8862, ingredients={ {8767, 60}, {8783, 50}, {8788, 40}, {8769, 16}, {8776, 14}, {8792, 12}, {8798, 1}, {8789, 1} } },
    -- Dark Treeckler Belt
    {item=8870, ingredients={ {8995, 1}, {8785, 48}, {7016, 25}, {8784, 9}, {8741, 4}, {8739, 3}, {8776, 3} } },
    -- Light Treeckler Belt
    {item=8871, ingredients={ {8796, 13}, {8792, 5}, {8771, 3}, {8775, 3}, {8916, 1}, {8797, 68}, {7013, 35}, {8780, 28} } },
    -- Krabelt
    {item=8873, ingredients={ {8733, 2}, {8732, 2}, {8738, 1}, {8744, 39}, {8734, 2} } },
    -- Stringsecticide
    {item=9143, ingredients={ {8757, 17}, {9269, 14}, {8786, 13}, {9277, 12}, {8795, 10}, {8771, 8}, {9280, 2}, {8736, 26} } },
    -- Fungas Belt
    {item=9144, ingredients={ {8405, 3}, {9280, 1}, {8870, 1}, {8873, 1}, {9278, 18}, {8380, 12}, {8791, 9}, {8381, 4} } },
    -- Slice of Undergrowth
    {item=9145, ingredients={ {9277, 8}, {8764, 8}, {9280, 1}, {8384, 1}, {8783, 40}, {8780, 35}, {9278, 12}, {8777, 12} } },
    -- Ougaabelt
    {item=9146, ingredients={ {8771, 10}, {8803, 8}, {8791, 4}, {9280, 2}, {8871, 1}, {9281, 1}, {9279, 13}, {9269, 12} } },
    -- Cherry Blop Belt
    {item=9167, ingredients={ {1775, 50}, {2556, 50}, {1776, 10}, {9388, 1}, {6929, 1}, {9381, 1} } },
    -- Pippin Blop Belt
    {item=9168, ingredients={ {1773, 50}, {1774, 10}, {9388, 1}, {9381, 1}, {6929, 1}, {2556, 50} } },
    -- Coco Blop Belt
    {item=9169, ingredients={ {6929, 1}, {2556, 50}, {1770, 50}, {1772, 10}, {9388, 1}, {9381, 1} } },
    -- Indigo Blop Belt
    {item=9170, ingredients={ {2556, 50}, {1777, 50}, {1778, 10}, {9388, 1}, {9381, 1}, {6929, 1} } },
    -- Royal Cherry Blop Belt
    {item=9171, ingredients={ {9383, 10}, {9388, 5}, {9167, 1}, {9384, 1}, {2556, 100}, {1775, 100}, {9381, 10} } },
    -- Royal Pippin Blop Belt
    {item=9172, ingredients={ {9388, 5}, {9168, 1}, {9387, 1}, {2556, 100}, {1773, 100}, {9381, 10}, {9383, 10} } },
    -- Royal Coco Blop Belt
    {item=9173, ingredients={ {2556, 100}, {9381, 10}, {9383, 10}, {9388, 5}, {9385, 1}, {9169, 1}, {1770, 100} } },
    -- Royal Indigo Blop Belt
    {item=9174, ingredients={ {9383, 10}, {9381, 10}, {9388, 5}, {9386, 1}, {9170, 1}, {2556, 100}, {1777, 100} } },
    -- Royal Rainbow Blop Belt
    {item=9175, ingredients={ {9172, 1}, {9391, 1}, {9173, 1}, {9171, 1}, {9174, 1}, {9382, 10}, {9389, 5} } },
    -- Biibl Belt
    {item=9183, ingredients={ {8736, 33}, {8774, 25}, {8772, 16}, {8804, 11}, {8806, 10}, {8405, 3}, {9401, 2}, {6812, 1} } },
    -- Pink Tutu
    {item=9366, ingredients={ {309, 50}, {6903, 30}, {998, 10}, {8805, 1}, {1686, 1} } },
    -- Feathered Belt
    {item=11545, ingredients={ {11527, 25}, {11529, 16}, {11531, 5}, {8266, 1}, {7386, 1}, {7297, 82}, {8389, 71}, {9269, 25} } },
}


registerCraftSkill(13, sk13Crafts, {jobID = jobID, toolIDs = toolIDs}, ingredientsForCraftJob(jobID), jobID)
