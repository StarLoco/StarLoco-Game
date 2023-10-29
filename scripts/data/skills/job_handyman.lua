local jobID = HandymanJob
local toolIDs = {7650}

-- Craft for Do It Yourself
local sk171Crafts = {
    -- Ash Drinking Trough
    {item=7590, ingredients={ {459, 1}, {311, 100} } },
    -- Oak Drinking Trough
    {item=7591, ingredients={ {1182, 1}, {311, 100}, {7653, 1} } },
    -- Maple Drinking Trough
    {item=7592, ingredients={ {1183, 1}, {311, 100}, {7657, 1}, {1182, 1} } },
    -- Chestnut Drinking Trough
    {item=7593, ingredients={ {311, 100}, {6868, 1} } },
    -- Hornbeam Drinking Trough
    {item=7594, ingredients={ {311, 100}, {1183, 1}, {1182, 1}, {7658, 1}, {793, 1}, {283, 1} } },
    -- Walnut Drinking Trough
    {item=7595, ingredients={ {311, 100}, {7659, 1} } },
    -- Cherry Drinking Trough
    {item=7596, ingredients={ {311, 100}, {1183, 1}, {1182, 1}, {283, 1}, {7660, 1} } },
    -- Yew Drinking Trough
    {item=7597, ingredients={ {311, 100}, {1183, 1}, {7654, 1}, {283, 1}, {1182, 1} } },
    -- Ebony Drinking Trough
    {item=7598, ingredients={ {311, 100}, {1183, 1}, {7655, 1}, {793, 1}, {283, 1}, {1182, 1} } },
    -- Elm Drinking Trough
    {item=7599, ingredients={ {1405, 1}, {7656, 1}, {793, 1}, {283, 1}, {1183, 1}, {1182, 1}, {311, 100} } },
    -- Bombu Drinking Trough
    {item=7600, ingredients={ {1182, 1}, {311, 100}, {7661, 1} } },
    -- Oliviolet Drinking Trough
    {item=7601, ingredients={ {1182, 1}, {311, 100}, {7662, 1} } },
    -- Bamboo Drinking Trough
    {item=7602, ingredients={ {1182, 1}, {7663, 1}, {1183, 1}, {311, 100} } },
    -- Dark Bamboo Drinking Trough
    {item=7603, ingredients={ {7664, 1}, {311, 100}, {1405, 1}, {793, 1}, {1182, 1}, {1183, 1}, {283, 1} } },
    -- Holy Bamboo Drinking Trough
    {item=7604, ingredients={ {793, 1}, {1405, 1}, {1182, 1}, {1406, 1}, {311, 100}, {1183, 1}, {283, 1}, {7665, 1} } },
    -- Ash Manger
    {item=7607, ingredients={ {580, 1}, {459, 1} } },
    -- Oak Manger
    {item=7608, ingredients={ {289, 100}, {7653, 1}, {580, 1} } },
    -- Yew Manger
    {item=7609, ingredients={ {401, 100}, {580, 1}, {7654, 1}, {533, 100}, {289, 100} } },
    -- Ebony Manger
    {item=7610, ingredients={ {7655, 1}, {400, 100}, {289, 100}, {401, 100}, {533, 100}, {580, 1} } },
    -- Elm Manger
    {item=7611, ingredients={ {532, 100}, {423, 100}, {7656, 1}, {580, 1}, {289, 100}, {401, 100}, {533, 100}, {400, 100} } },
    -- Maple Manger
    {item=7612, ingredients={ {580, 1}, {7657, 1}, {289, 100}, {400, 100} } },
    -- Hornbeam Manger
    {item=7613, ingredients={ {533, 100}, {400, 100}, {289, 100}, {401, 100}, {7658, 1}, {580, 1} } },
    -- Chestnut Manger
    {item=7614, ingredients={ {580, 1}, {6868, 1} } },
    -- Walnut Manger
    {item=7615, ingredients={ {580, 1}, {7659, 1} } },
    -- Cherry Manger
    {item=7616, ingredients={ {400, 100}, {7660, 1}, {580, 1}, {533, 100}, {289, 100} } },
    -- Bombu Manger
    {item=7617, ingredients={ {289, 100}, {580, 1}, {7661, 1} } },
    -- Oliviolet Manger
    {item=7618, ingredients={ {289, 100}, {7662, 1}, {580, 1} } },
    -- Bamboo Manger
    {item=7619, ingredients={ {289, 100}, {400, 100}, {580, 1}, {7663, 1} } },
    -- Dark Bamboo Manger
    {item=7620, ingredients={ {533, 100}, {401, 100}, {580, 1}, {7664, 1}, {423, 100}, {289, 100}, {400, 100} } },
    -- Holy Bamboo Manger
    {item=7621, ingredients={ {580, 1}, {400, 100}, {289, 100}, {533, 100}, {401, 100}, {423, 100}, {532, 100}, {7665, 1} } },
    -- Patter made of Tofu Feather
    {item=7622, ingredients={ {301, 1}, {746, 1}, {459, 1} } },
    -- Patter made of Blue Piwi Feather
    {item=7623, ingredients={ {459, 1}, {6897, 1} } },
    -- Patter made of Purple Piwi Feather
    {item=7624, ingredients={ {6898, 1}, {459, 1} } },
    -- Chestnut Slapper
    {item=7626, ingredients={ {6868, 1}, {1018, 1} } },
    -- Oak Slapper
    {item=7627, ingredients={ {435, 1}, {7653, 1}, {1018, 1} } },
    -- Yew Slapper
    {item=7629, ingredients={ {1018, 1}, {435, 1}, {7654, 1}, {7262, 1}, {7263, 1} } },
    -- Dragobutt in White Gobbly Leather
    {item=7635, ingredients={ {883, 1}, {1338, 1} } },
    -- Dragobutt in Black Gobbly Leather
    {item=7636, ingredients={ {1338, 1}, {884, 1} } },
    -- Dragobutt in Gobball Leather
    {item=7637, ingredients={ {459, 1}, {304, 1}, {1338, 1} } },
    -- Ancestral Wood Drinking Trough
    {item=7673, ingredients={ {7666, 1}, {1405, 1}, {311, 100}, {1182, 1}, {1406, 1}, {793, 1}, {283, 1}, {1183, 1} } },
    -- Bewitched Wood Drinking Trough
    {item=7674, ingredients={ {1405, 1}, {1406, 1}, {1182, 1}, {1183, 1}, {793, 1}, {283, 1}, {311, 100}, {7667, 1} } },
    -- Trunknid Wood Drinking Trough
    {item=7675, ingredients={ {311, 100}, {1182, 1}, {7668, 1} } },
    -- Golden Bamboo Drinking Trough
    {item=7676, ingredients={ {311, 100}, {1405, 1}, {1182, 1}, {7669, 1}, {1406, 1}, {793, 1}, {283, 1}, {1183, 1} } },
    -- Magic Bamboo Drinking Trough
    {item=7677, ingredients={ {1182, 1}, {311, 100}, {7670, 1}, {793, 1}, {283, 1}, {1183, 1} } },
    -- Bambooto Drinking Trough
    {item=7678, ingredients={ {311, 100}, {7671, 1}, {1182, 1}, {1183, 1} } },
    -- Holy Bambooto Wood Drinking Trough
    {item=7679, ingredients={ {311, 100}, {1405, 1}, {793, 1}, {283, 1}, {1183, 1}, {7672, 1}, {1182, 1} } },
    -- Kokoko Wood Drinking Trough
    {item=7682, ingredients={ {311, 100}, {1001, 1} } },
    -- Ancestral Wood Manger
    {item=7683, ingredients={ {401, 100}, {533, 100}, {532, 100}, {289, 100}, {400, 100}, {423, 100}, {580, 1}, {7666, 1} } },
    -- Bewitched Wood Manger
    {item=7684, ingredients={ {7667, 1}, {400, 100}, {423, 100}, {401, 100}, {533, 100}, {289, 100}, {580, 1} } },
    -- Golden Bamboo Manger
    {item=7685, ingredients={ {400, 100}, {533, 100}, {532, 100}, {7669, 1}, {580, 1}, {423, 100}, {289, 100}, {401, 100} } },
    -- Magic Bamboo Manger
    {item=7686, ingredients={ {533, 100}, {289, 100}, {400, 100}, {401, 100}, {580, 1}, {7670, 1} } },
    -- Bambooto Wood Manger
    {item=7687, ingredients={ {7671, 1}, {400, 100}, {289, 100}, {580, 1} } },
    -- Holy Bambooto Wood Manger
    {item=7688, ingredients={ {401, 100}, {580, 1}, {7672, 1}, {533, 100}, {423, 100}, {289, 100}, {400, 100} } },
    -- Kokoko Wood Manger
    {item=7689, ingredients={ {580, 1}, {1001, 1} } },
    -- Trunknid Wood Manger
    {item=7690, ingredients={ {289, 100}, {580, 1}, {7668, 1} } },
    -- Dragobutt in Gobball War Chief Leather
    {item=7691, ingredients={ {1338, 1}, {887, 1}, {6868, 1}, {459, 1} } },
    -- Dragobutt in Royal Gobball Leather
    {item=7692, ingredients={ {7662, 1}, {7661, 1}, {7653, 1}, {886, 1}, {7659, 1}, {6868, 1}, {459, 1}, {1338, 1} } },
    -- Dragobutt in Wild Boar Leather
    {item=7693, ingredients={ {459, 1}, {6868, 1}, {486, 1}, {1338, 1} } },
    -- Dragobutt in Dragon Pig Leather
    {item=7694, ingredients={ {1338, 1}, {487, 1}, {459, 1}, {6868, 1}, {7659, 1}, {7653, 1}, {7661, 1}, {7662, 1} } },
    -- Dragobutt in Piglet Leather
    {item=7695, ingredients={ {459, 1}, {1338, 1}, {901, 1}, {7659, 1}, {6868, 1} } },
    -- Dragobutt in Purple Bwork Leather
    {item=7696, ingredients={ {7659, 1}, {459, 1}, {1338, 1}, {2271, 1}, {7653, 1}, {6868, 1} } },
    -- Dragobutt in Lousy Pig Leather
    {item=7697, ingredients={ {6868, 1}, {1338, 1}, {459, 1}, {2275, 1} } },
    -- Dragobutt in Black Leather
    {item=7698, ingredients={ {7659, 1}, {6868, 1}, {459, 1}, {1338, 1}, {2287, 1}, {7653, 1}, {7661, 1} } },
    -- Dragobutt in Wild Plain Boar Leather
    {item=7699, ingredients={ {459, 1}, {1338, 1}, {2511, 1}, {7653, 1}, {7659, 1}, {6868, 1} } },
    -- Dragobutt in Minotoror Leather
    {item=7700, ingredients={ {7662, 1}, {7661, 1}, {6868, 1}, {7659, 1}, {7653, 1}, {459, 1}, {1338, 1}, {2999, 1} } },
    -- Patter made of Green Piwi Feather
    {item=7733, ingredients={ {6899, 1}, {459, 1} } },
    -- Patter made of Red Piwi Feather
    {item=7734, ingredients={ {6900, 1}, {459, 1} } },
    -- Patter made of Yellow Piwi Feather
    {item=7735, ingredients={ {459, 1}, {6902, 1} } },
    -- Patter made of Pink Piwi Feather
    {item=7736, ingredients={ {6903, 1}, {459, 1} } },
    -- Patter made of Evil Tofu Feather
    {item=7737, ingredients={ {2675, 1}, {746, 1}, {459, 1} } },
    -- Patter made of Crow Feather
    {item=7738, ingredients={ {6868, 1}, {1889, 1}, {748, 1}, {459, 1} } },
    -- Patter made of Ice Kwak Feather
    {item=7739, ingredients={ {749, 1}, {6868, 1}, {459, 1}, {414, 1}, {748, 1} } },
    -- Patter made of Fire Kwak Feather
    {item=7740, ingredients={ {6868, 1}, {415, 1}, {748, 1}, {749, 1}, {459, 1} } },
    -- Patter made of Wind Bwak Feather
    {item=7741, ingredients={ {749, 1}, {459, 1}, {6868, 1}, {416, 1}, {748, 1} } },
    -- Patter made of Earth Kwak Feather
    {item=7742, ingredients={ {748, 1}, {1141, 1}, {6868, 1}, {459, 1}, {749, 1} } },
    -- Patter made of Last Cheeken Feather
    {item=7743, ingredients={ {7659, 1}, {1685, 1}, {6868, 1}, {459, 1}, {749, 1}, {750, 1} } },
    -- Patter made of Kwak Pointed Feather
    {item=7744, ingredients={ {459, 1}, {749, 1}, {750, 1}, {6457, 1}, {1670, 1}, {7659, 1}, {6868, 1} } },
    -- Patter made of Lord Crow Feather
    {item=7745, ingredients={ {6868, 1}, {7036, 1}, {7035, 1}, {6458, 1}, {1892, 1}, {7661, 1}, {7659, 1}, {459, 1} } },
    -- Patter made of Royal Tofu Feather
    {item=7746, ingredients={ {6868, 1}, {459, 1}, {6458, 1}, {7035, 1}, {7036, 1}, {2247, 1}, {7661, 1}, {7659, 1} } },
    -- Ebony Slapper
    {item=7755, ingredients={ {7264, 1}, {435, 1}, {7262, 1}, {7655, 1}, {7263, 1}, {1018, 1} } },
    -- Elm Slapper
    {item=7756, ingredients={ {7263, 1}, {7262, 1}, {435, 1}, {7656, 1}, {7264, 1}, {1612, 1}, {1018, 1} } },
    -- Maple Slapper
    {item=7757, ingredients={ {435, 1}, {7657, 1}, {1018, 1}, {7262, 1} } },
    -- Hornbeam Slapper
    {item=7758, ingredients={ {7658, 1}, {7263, 1}, {1018, 1}, {435, 1}, {7262, 1}, {7264, 1} } },
    -- Ash Wood Slapper
    {item=7759, ingredients={ {459, 1}, {1018, 1} } },
    -- Walnut Slapper
    {item=7760, ingredients={ {1018, 1}, {7659, 1} } },
    -- Cherry Slapper
    {item=7761, ingredients={ {7263, 1}, {1018, 1}, {7660, 1}, {7262, 1}, {435, 1} } },
    -- Bamboo Slapper
    {item=7762, ingredients={ {7663, 1}, {435, 1}, {7262, 1}, {1018, 1} } },
    -- Bombu Slapper
    {item=7763, ingredients={ {1018, 1}, {7661, 1}, {435, 1} } },
    -- Oliviolet Slapper
    {item=7764, ingredients={ {7662, 1}, {435, 1}, {1018, 1} } },
    -- Dark Bamboo Slapper
    {item=7765, ingredients={ {1018, 1}, {7664, 1}, {435, 1}, {7263, 1}, {7262, 1}, {7264, 1}, {1612, 1} } },
    -- Holy Bamboo Slapper
    {item=7766, ingredients={ {435, 1}, {1612, 1}, {6735, 1}, {7665, 1}, {7262, 1}, {7264, 1}, {7263, 1}, {1018, 1} } },
    -- Ancestral Wood Slapper
    {item=7767, ingredients={ {6735, 1}, {1612, 1}, {7263, 1}, {7262, 1}, {435, 1}, {7264, 1}, {7666, 1}, {1018, 1} } },
    -- Bewitched Wood Slapper
    {item=7768, ingredients={ {7667, 1}, {435, 1}, {1018, 1}, {7264, 1}, {7263, 1}, {7262, 1}, {1612, 1}, {6735, 1} } },
    -- Trunknid Wood Slapper
    {item=7769, ingredients={ {435, 1}, {7668, 1}, {1018, 1} } },
    -- Golden Bamboo Slapper
    {item=7770, ingredients={ {6735, 1}, {435, 1}, {7262, 1}, {7264, 1}, {1612, 1}, {7669, 1}, {1018, 1}, {7263, 1} } },
    -- Kokoko Wood Slapper
    {item=7771, ingredients={ {1001, 1}, {1018, 1} } },
    -- Magic Bamboo Slapper
    {item=7772, ingredients={ {7264, 1}, {7263, 1}, {7670, 1}, {1018, 1}, {435, 1}, {7262, 1} } },
    -- Bambooto Wood Slapper
    {item=7773, ingredients={ {7262, 1}, {7671, 1}, {435, 1}, {1018, 1} } },
    -- Holy Bambooto Wood Slapper
    {item=7774, ingredients={ {7672, 1}, {435, 1}, {7263, 1}, {7262, 1}, {7264, 1}, {1612, 1}, {1018, 1} } },
    -- Ash Lightning Thrower
    {item=7775, ingredients={ {459, 1}, {447, 1} } },
    -- Oak Lightning Thrower
    {item=7776, ingredients={ {447, 1}, {746, 1}, {7653, 1} } },
    -- Yew Lightning Thrower
    {item=7777, ingredients={ {749, 1}, {746, 1}, {748, 1}, {7654, 1}, {447, 1} } },
    -- Ebony Lightning Thrower
    {item=7778, ingredients={ {447, 1}, {746, 1}, {7655, 1}, {748, 1}, {749, 1}, {750, 1} } },
    -- Elm Lightning Thrower
    {item=7779, ingredients={ {447, 1}, {7656, 1}, {6457, 1}, {746, 1}, {748, 1}, {749, 1}, {750, 1} } },
    -- Maple Lightning Thrower
    {item=7780, ingredients={ {447, 1}, {748, 1}, {746, 1}, {7657, 1} } },
    -- Hornbeam Lightning Thrower
    {item=7781, ingredients={ {748, 1}, {746, 1}, {749, 1}, {7658, 1}, {447, 1}, {750, 1} } },
    -- Chestnut Lightning Thrower
    {item=7782, ingredients={ {6868, 1}, {447, 1} } },
    -- Walnut Lightning Thrower
    {item=7783, ingredients={ {447, 1}, {7659, 1} } },
    -- Cherry Lightning Thrower
    {item=7784, ingredients={ {447, 1}, {748, 1}, {746, 1}, {7660, 1}, {749, 1} } },
    -- Bombu Lightning Thrower
    {item=7785, ingredients={ {746, 1}, {447, 1}, {7661, 1} } },
    -- Oliviolet Lightning Thrower
    {item=7786, ingredients={ {746, 1}, {447, 1}, {7662, 1} } },
    -- Bamboo Lightning Thrower
    {item=7787, ingredients={ {447, 1}, {7663, 1}, {748, 1}, {746, 1} } },
    -- Dark Bamboo Lightning Thrower
    {item=7788, ingredients={ {748, 1}, {749, 1}, {750, 1}, {6457, 1}, {447, 1}, {7664, 1}, {746, 1} } },
    -- Holy Bamboo Lightning Thrower
    {item=7789, ingredients={ {6457, 1}, {7036, 1}, {749, 1}, {7665, 1}, {447, 1}, {7035, 1}, {750, 1}, {6458, 1} } },
    -- Ancestral Wood Lightning Thrower
    {item=7790, ingredients={ {447, 1}, {6458, 1}, {7036, 1}, {7035, 1}, {6457, 1}, {7666, 1}, {749, 1}, {750, 1} } },
    -- Bewitched Wood Lightning Thrower
    {item=7791, ingredients={ {6457, 1}, {749, 1}, {750, 1}, {7667, 1}, {447, 1}, {6458, 1}, {7035, 1}, {7036, 1} } },
    -- Trunknid Wood Lightning Thrower
    {item=7792, ingredients={ {447, 1}, {746, 1}, {7668, 1} } },
    -- Golden Bamboo Lightning Thrower
    {item=7793, ingredients={ {7036, 1}, {6457, 1}, {749, 1}, {750, 1}, {7669, 1}, {447, 1}, {6458, 1}, {7035, 1} } },
    -- Magic Bamboo Lightning Thrower
    {item=7794, ingredients={ {447, 1}, {7670, 1}, {746, 1}, {748, 1}, {749, 1}, {750, 1} } },
    -- Bambooto Wood Lightning Thrower
    {item=7795, ingredients={ {748, 1}, {447, 1}, {7671, 1}, {746, 1} } },
    -- Holy Bambooto Wood Lightning Thrower
    {item=7796, ingredients={ {750, 1}, {6457, 1}, {7036, 1}, {7035, 1}, {6458, 1}, {447, 1}, {7672, 1}, {749, 1} } },
    -- Kokoko Wood Lightning Thrower
    {item=7797, ingredients={ {1001, 1}, {447, 1} } },
    -- Prism of Conquest
    {item=8990, ingredients={ {2529, 1}, {429, 1}, {548, 1}, {450, 1}, {2539, 1} } },
}

-- Craft for Craft a key
local sk182Crafts = {
    -- Gobball Dungeon Key
    {item=1568, ingredients={ {384, 10}, {2453, 1}, {2451, 1} } },
    -- Blacksmith Dungeon Key
    {item=1569, ingredients={ {746, 1}, {2274, 1}, {748, 1}, {747, 1} } },
    -- Skeletons Dungeon Key
    {item=1570, ingredients={ {310, 10}, {433, 1}, {430, 1} } },
    -- Bworker Dungeon Key
    {item=6884, ingredients={ {3001, 30}, {3002, 30}, {3208, 30}, {429, 30}, {409, 10}, {8145, 10}, {8135, 1}, {8388, 1} } },
    -- Pandikaze Key
    {item=7309, ingredients={ {7258, 2}, {7297, 2}, {7260, 2}, {7303, 2} } },
    -- Bulb Cavern Key
    {item=7310, ingredients={ {7264, 1}, {7263, 1}, {7262, 1} } },
    -- Kitsou Dungeon Key
    {item=7311, ingredients={ {7279, 7}, {7280, 7}, {7281, 7}, {7282, 7} } },
    -- Firefoux Dungeon Key
    {item=7312, ingredients={ {7299, 1}, {7300, 1}, {7294, 1}, {7293, 1}, {7298, 1}, {7292, 1} } },
    -- Dragon Pig Maze Key
    {item=7511, ingredients={ {2482, 5}, {2486, 5}, {2484, 5}, {7509, 1}, {7510, 1} } },
    -- Treechnid Dungeon Key
    {item=7557, ingredients={ {437, 10}, {463, 1}, {434, 1}, {435, 10} } },
    -- Koolich Dungeon Key
    {item=7908, ingredients={ {8084, 1}, {8002, 1}, {8083, 1} } },
    -- Tofu Dungeon Key
    {item=7918, ingredients={ {366, 10}, {301, 1}, {367, 1} } },
    -- Labyrinth of the Minotoror Key
    {item=7924, ingredients={ {8314, 5}, {8309, 5}, {8312, 5}, {8315, 5} } },
    -- Lord Crow Dungeon Key
    {item=7926, ingredients={ {2060, 35}, {1889, 30}, {2056, 5} } },
    -- Crackler Dungeon Key
    {item=7927, ingredients={ {2252, 15}, {2304, 10}, {431, 1} } },
    -- Bwork Dungeon Key
    {item=8135, ingredients={ {3001, 10}, {429, 20}, {3208, 20}, {3002, 10} } },
    -- Scaraleaf Dungeon Key
    {item=8139, ingredients={ {1464, 10}, {1467, 10}, {1465, 10}, {1466, 10} } },
    -- Secret Key to the Royal Tofu House
    {item=8142, ingredients={ {366, 25}, {367, 25}, {301, 25}, {8158, 2}, {7918, 1} } },
    -- Field Key
    {item=8143, ingredients={ {306, 10}, {2659, 10} } },
    -- Canidae Dungeon Key
    {item=8156, ingredients={ {2579, 6}, {2320, 5}, {2321, 5}, {2578, 5}, {2551, 5}, {2528, 5}, {2491, 5}, {440, 8} } },
    -- Key to the Minotot Room
    {item=8307, ingredients={ {8312, 10}, {8314, 10}, {8315, 10}, {8311, 10}, {8309, 10}, {8308, 5}, {7924, 1} } },
    -- Key to the Dragon Pig Den
    {item=8320, ingredients={ {8392, 1}, {8390, 1}, {8391, 1}, {2515, 10} } },
    -- Dreggon Sanctuary Key
    {item=8342, ingredients={ {8369, 1}, {847, 1}, {8371, 1}, {8372, 1}, {8370, 1} } },
    -- Dreggon Dungeon Key
    {item=8343, ingredients={ {8375, 1}, {8373, 1}, {8376, 1}, {8374, 1} } },
    -- Soft Oak Dungeon Key
    {item=8436, ingredients={ {1612, 1}, {1611, 1}, {7557, 1}, {8496, 1}, {437, 30}, {464, 3}, {463, 3}, {1610, 1} } },
    -- Sand Dungeon Key
    {item=8437, ingredients={ {8680, 10}, {1782, 5} } },
    -- Brakmarian Rat Dungeon Key
    {item=8438, ingredients={ {2322, 1}, {8481, 1}, {8483, 1}, {8571, 1} } },
    -- Bontarian Rat Dungeon Key
    {item=8439, ingredients={ {8483, 1}, {8570, 1}, {2322, 1}, {8481, 1} } },
    -- Grotto Hesque Key
    {item=8917, ingredients={ {8744, 10}, {8730, 1} } },
    -- Key to the Fungus Dungeon
    {item=9247, ingredients={ {1674, 1}, {6919, 1}, {6920, 1}, {8143, 30}, {426, 19}, {290, 10}, {6922, 1}, {6921, 1} } },
    -- Key to the Blop Dungeon
    {item=9248, ingredients={ {1772, 5}, {1778, 5}, {1776, 5}, {2556, 10}, {1774, 5} } },
    -- Key to the Rainbow Blop Lair
    {item=9254, ingredients={ {9250, 1}, {9252, 1}, {9249, 1}, {9251, 1} } },
    -- Ilyzaelle's Lookout Key
    {item=11474, ingredients={ {2481, 10}, {424, 9}, {1889, 8}, {479, 3}, {1694, 2}, {8681, 1}, {8439, 1}, {2512, 1} } },
    -- Qu'Tan's Sanctuary Key
    {item=11475, ingredients={ {449, 3}, {2277, 2}, {8438, 1}, {2492, 1}, {2502, 10}, {2273, 9}, {2150, 8}, {2056, 3} } },
}


local requirements = {jobID = jobID, toolIDs = toolIDs}
registerCraftSkill(171, sk171Crafts, requirements, ingredientsForCraftJob(jobID), jobID)
