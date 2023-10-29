local jobID = JewellerJob
local toolIDs = {491}

-- Craft for Create a Ring
local sk11Crafts = {
    -- Small Wisdom Ring
    {item=100, ingredients={ {442, 1}, {312, 1} } },
    -- Wisdom Ring
    {item=101, ingredients={ {442, 3}, {312, 1} } },
    -- Great Wisdom Ring
    {item=102, ingredients={ {442, 5}, {312, 1} } },
    -- Powerful Wisdom Ring
    {item=103, ingredients={ {442, 7}, {312, 1} } },
    -- Small Chance Ring
    {item=109, ingredients={ {441, 1}, {442, 1} } },
    -- Chance Ring
    {item=110, ingredients={ {442, 3}, {441, 1} } },
    -- Great Chance Ring
    {item=111, ingredients={ {442, 5}, {441, 1} } },
    -- Powerful Chance Ring
    {item=112, ingredients={ {442, 7}, {441, 1} } },
    -- Small Agility Ring
    {item=118, ingredients={ {312, 1}, {441, 1} } },
    -- Agility Ring
    {item=119, ingredients={ {441, 3}, {312, 1} } },
    -- Great Agility Ring
    {item=120, ingredients={ {441, 5}, {312, 1} } },
    -- Powerful Agility Ring
    {item=121, ingredients={ {441, 7}, {312, 1} } },
    -- Rocky Ring
    {item=278, ingredients={ {445, 6}, {441, 2}, {442, 2} } },
    -- Ano Rak Ring
    {item=346, ingredients={ {444, 5}, {442, 5}, {441, 5} } },
    -- Valuable Ring
    {item=355, ingredients={ {441, 4}, {443, 2}, {445, 2} } },
    -- Ecalisor
    {item=359, ingredients={ {446, 5}, {444, 5}, {441, 4}, {442, 4} } },
    -- Silimelle's Wedding Ring
    {item=732, ingredients={ {350, 5}, {444, 3}, {445, 2}, {315, 1} } },
    -- Royal Gantie
    {item=767, ingredients={ {441, 3}, {312, 2} } },
    -- Camate
    {item=768, ingredients={ {442, 3}, {312, 2} } },
    -- Fortifying Ring
    {item=785, ingredients={ {441, 4}, {476, 3}, {473, 3} } },
    -- Ano Rexik Ring
    {item=787, ingredients={ {441, 1}, {473, 1} } },
    -- Treering
    {item=836, ingredients={ {464, 1}, {437, 4}, {434, 4} } },
    -- Ring of Satisfaction
    {item=841, ingredients={ {443, 4}, {473, 4}, {476, 4}, {441, 4} } },
    -- Strong Ring
    {item=849, ingredients={ {301, 1}, {312, 1} } },
    -- Nimble Ring
    {item=850, ingredients={ {385, 1}, {312, 1} } },
    -- Vital Ring
    {item=851, ingredients={ {312, 1}, {365, 1} } },
    -- Small Magic Ring
    {item=852, ingredients={ {441, 4}, {312, 1} } },
    -- Mental Ring
    {item=1087, ingredients={ {441, 5}, {312, 5}, {476, 5} } },
    -- Sacrier's Wound
    {item=1493, ingredients={ {443, 3}, {445, 3}, {463, 1} } },
    -- Ringer
    {item=1494, ingredients={ {441, 10}, {312, 2}, {463, 1} } },
    -- Soff Ring
    {item=1495, ingredients={ {444, 6}, {445, 2}, {464, 2}, {461, 2}, {466, 1} } },
    -- Ecaflip's Luck
    {item=1496, ingredients={ {460, 4}, {312, 6}, {442, 6}, {476, 6} } },
    -- Xelor's Past
    {item=1497, ingredients={ {350, 6}, {443, 3}, {464, 2}, {441, 2}, {444, 2} } },
    -- Enutrof's Memento
    {item=1498, ingredients={ {441, 2}, {350, 6}, {445, 3}, {464, 2}, {444, 2} } },
    -- The Enutrofion
    {item=1499, ingredients={ {313, 4}, {444, 4}, {746, 3}, {750, 2}, {467, 1}, {442, 4} } },
    -- Soft Treering
    {item=1559, ingredients={ {464, 1}, {435, 10}, {434, 10}, {437, 10}, {1660, 2} } },
    -- Dark Treering
    {item=1602, ingredients={ {1660, 1}, {836, 1}, {1611, 3}, {1610, 2} } },
    -- Magus Fecalizer
    {item=1656, ingredients={ {443, 5}, {441, 6}, {444, 6}, {445, 6} } },
    -- Warrior Fecalizer
    {item=1657, ingredients={ {443, 6}, {444, 6}, {445, 6}, {441, 5} } },
    -- Fire Kwakring
    {item=2418, ingredients={ {444, 4}, {313, 3}, {2648, 3}, {442, 2}, {415, 1} } },
    -- Bouze Lite Yeah's Ring
    {item=2419, ingredients={ {2463, 1}, {2460, 1}, {2453, 1} } },
    -- Ringtree
    {item=2420, ingredients={ {464, 5}, {2563, 1}, {435, 40}, {2249, 20}, {463, 5} } },
    -- Royal Gobball Ring
    {item=2441, ingredients={ {383, 10}, {2465, 1}, {2419, 1}, {2463, 50}, {2453, 20} } },
    -- Gelano
    {item=2469, ingredients={ {370, 2}, {2242, 2}, {368, 100}, {757, 100}, {2437, 2}, {2241, 2} } },
    -- Adventurer Ring
    {item=2475, ingredients={ {121, 4}, {112, 4} } },
    -- Tribal Ring
    {item=3203, ingredients={ {746, 2}, {465, 1}, {747, 1}, {6457, 1}, {1129, 4}, {842, 4} } },
    -- Trav' Ring
    {item=6463, ingredients={ {479, 1}, {749, 1}, {747, 1}, {313, 5}, {465, 1} } },
    -- Satisfied Summoner's Ring
    {item=6464, ingredients={ {746, 1}, {841, 1}, {6458, 1}, {445, 4}, {750, 1} } },
    -- Loopine's Ring
    {item=6465, ingredients={ {6458, 5}, {6457, 5}, {750, 5}, {466, 2}, {316, 2}, {2251, 1}, {2252, 20} } },
    -- Ano Neemous Ring
    {item=6467, ingredients={ {750, 2}, {466, 1}, {6458, 3}, {6457, 2}, {746, 2}, {465, 2} } },
    -- Young Vald's Ring
    {item=6468, ingredients={ {749, 2}, {748, 2}, {466, 1}, {746, 1}, {448, 20}, {479, 2} } },
    -- Farle's Wedding Ring
    {item=6469, ingredients={ {2300, 4}, {2305, 4}, {467, 4}, {749, 3}, {6458, 3}, {316, 2}, {747, 5} } },
    -- Gogorified Miner Ring
    {item=6506, ingredients={ {316, 2}, {6458, 10}, {6457, 10}, {749, 10}, {465, 2}, {467, 2}, {466, 2} } },
    -- Bearman's Wedding Ring
    {item=6722, ingredients={ {303, 4}, {350, 4}, {471, 3}, {6842, 1} } },
    -- Ice Kwakring
    {item=6732, ingredients={ {2648, 3}, {442, 2}, {414, 1}, {444, 4}, {313, 3} } },
    -- Deceitful Wedding Ring
    {item=6743, ingredients={ {2624, 1}, {2633, 1}, {6736, 8}, {6737, 2}, {2607, 1} } },
    -- Earth Kwakring
    {item=6748, ingredients={ {444, 4}, {2648, 3}, {313, 3}, {442, 2}, {1141, 1} } },
    -- Wind Kwakring
    {item=6749, ingredients={ {444, 4}, {2648, 3}, {313, 3}, {442, 2}, {416, 1} } },
    -- Farle's Magic Bracelet
    {item=6767, ingredients={ {467, 2}, {749, 2}, {315, 2}, {424, 20}, {750, 12}, {6458, 6}, {746, 3} } },
    -- Country Ring
    {item=6791, ingredients={ {374, 8}, {2661, 5}, {373, 4} } },
    -- Chief Crocodyl Bracelet
    {item=6819, ingredients={ {315, 1}, {1663, 30}, {1613, 30}, {1664, 10}, {1614, 5}, {467, 2} } },
    -- Boar Ring
    {item=6910, ingredients={ {386, 10}, {388, 5}, {387, 1} } },
    -- Arachnoring
    {item=6911, ingredients={ {365, 3}, {519, 1} } },
    -- Mos Kitano
    {item=6915, ingredients={ {307, 1}, {2583, 1}, {519, 1}, {371, 1} } },
    -- Mush Mush Ring
    {item=6919, ingredients={ {377, 1}, {300, 2} } },
    -- Mush Mush Wedding Ring
    {item=6920, ingredients={ {300, 2}, {290, 1} } },
    -- Prespic Ring
    {item=6928, ingredients={ {407, 1}, {2573, 5}, {2571, 5}, {2574, 2}, {2572, 1} } },
    -- Green Scararing
    {item=6944, ingredients={ {1466, 30}, {398, 10}, {1458, 5}, {2293, 3}, {396, 1} } },
    -- Red Scararing
    {item=6945, ingredients={ {2292, 3}, {396, 1}, {1465, 30}, {398, 10}, {1457, 5} } },
    -- White Scararing
    {item=6946, ingredients={ {398, 10}, {1456, 5}, {2290, 3}, {396, 1}, {1467, 30} } },
    -- Blue Scararing
    {item=6947, ingredients={ {2291, 3}, {396, 1}, {1464, 30}, {398, 10}, {1455, 5} } },
    -- Boowolf Ring
    {item=6956, ingredients={ {439, 30}, {440, 22}, {2579, 20}, {2578, 10}, {291, 5}, {438, 1} } },
    -- Gangster Ring
    {item=6961, ingredients={ {852, 20}, {109, 20}, {118, 20} } },
    -- Ring o'Stradamus
    {item=6996, ingredients={ {7264, 2}, {359, 2}, {7035, 2}, {7036, 2}, {747, 2} } },
    -- Conno Ring
    {item=6997, ingredients={ {7035, 1}, {750, 1}, {7263, 2}, {7028, 1}, {7026, 1}, {7036, 1} } },
    -- Elya Wood's Wedding Ring
    {item=6998, ingredients={ {6457, 2}, {7036, 2}, {7035, 2}, {7269, 1}, {7026, 5}, {7027, 5}, {7028, 5}, {7017, 4} } },
    -- Omerta Ring
    {item=7116, ingredients={ {7028, 1}, {747, 1}, {359, 1}, {7266, 15}, {7268, 15}, {7267, 15} } },
    -- Ostarr Ring
    {item=7117, ingredients={ {6458, 2}, {7027, 1}, {2509, 6}, {2259, 5}, {7369, 4}, {7036, 4} } },
    -- Ugle Ring
    {item=7118, ingredients={ {2279, 8}, {7036, 5}, {2328, 4}, {7027, 2}, {7026, 2}, {2581, 1}, {7369, 8} } },
    -- Nonsenz Ring
    {item=7119, ingredients={ {7268, 20}, {7369, 6}, {2322, 6}, {7036, 5}, {2463, 4}, {7035, 4} } },
    -- Ignoah Ring
    {item=7120, ingredients={ {2328, 1}, {7369, 1}, {7036, 1}, {7027, 1}, {7017, 1} } },
    -- Nozokomial Ring
    {item=7121, ingredients={ {7036, 1}, {7286, 5}, {2463, 2}, {2322, 2}, {747, 1}, {7026, 1} } },
    -- Blubba Ring
    {item=7122, ingredients={ {313, 5}, {350, 3}, {7033, 1} } },
    -- Ab'ho Ring
    {item=7123, ingredients={ {7369, 5}, {750, 4}, {7036, 2}, {7035, 2}, {7264, 1}, {7027, 1} } },
    -- Okaringa
    {item=7128, ingredients={ {7369, 8}, {7035, 5}, {7036, 5}, {748, 3}, {749, 2}, {746, 2} } },
    -- Pota Ring
    {item=7131, ingredients={ {6457, 4}, {750, 3}, {7035, 3}, {749, 2}, {7036, 2}, {7026, 1} } },
    -- O-Ring Ami
    {item=7132, ingredients={ {313, 6}, {442, 3}, {350, 2}, {749, 1}, {7036, 1} } },
    -- Akwadala Wedding Ring
    {item=7246, ingredients={ {7222, 50}, {350, 3}, {313, 2}, {747, 1} } },
    -- Terrdala Wedding Ring
    {item=7247, ingredients={ {7013, 5}, {747, 2}, {750, 2}, {7027, 1}, {7224, 50}, {7016, 5} } },
    -- Feudala Wedding Ring
    {item=7248, ingredients={ {449, 5}, {7028, 3}, {7026, 1}, {7225, 50}, {7016, 8}, {747, 6}, {7035, 5} } },
    -- Aerdala Wedding Ring
    {item=7249, ingredients={ {7028, 1}, {7223, 50}, {7013, 8}, {7016, 1}, {7035, 1}, {747, 1} } },
    -- Kitsou Ring
    {item=7341, ingredients={ {7277, 1}, {7282, 1}, {7281, 1}, {7275, 1}, {7278, 1}, {7279, 1} } },
    -- Turkoring
    {item=7555, ingredients={ {2599, 20}, {2598, 15}, {2586, 13}, {2588, 9}, {2591, 2}, {2594, 2} } },
    -- Gobkool Ring
    {item=7881, ingredients={ {7906, 5}, {2419, 1}, {2179, 1}, {7903, 10}, {383, 10} } },
    -- Koalak Ring
    {item=8004, ingredients={ {8083, 1}, {8002, 1}, {8059, 1}, {8062, 1}, {8060, 1}, {8061, 1} } },
    -- Tofu Ring
    {item=8109, ingredients={ {367, 8}, {8557, 1}, {301, 13}, {366, 12} } },
    -- Golden Scarabugly Ring
    {item=8121, ingredients={ {6947, 1}, {6946, 1}, {8161, 1}, {6944, 1}, {6945, 1}, {8136, 1} } },
    -- Farmer Moth-Eaten Mittens
    {item=8126, ingredients={ {400, 25}, {2659, 10}, {2662, 2} } },
    -- Black Scararing
    {item=8136, ingredients={ {8140, 1}, {6947, 1}, {6944, 1}, {6946, 1}, {6945, 1} } },
    -- Legendary Cracklering
    {item=8149, ingredients={ {431, 20}, {466, 10}, {7370, 7}, {8102, 6}, {2306, 3}, {315, 1}, {2251, 1} } },
    -- Red Piwi Ring
    {item=8219, ingredients={ {6900, 1}, {287, 1} } },
    -- Blue Piwi Ring
    {item=8220, ingredients={ {6897, 1}, {287, 1} } },
    -- Purple Piwi Ring
    {item=8221, ingredients={ {6898, 1}, {287, 1} } },
    -- Green Piwi Ring
    {item=8222, ingredients={ {287, 1}, {6899, 1} } },
    -- Yellow Piwi Ring
    {item=8223, ingredients={ {6902, 1}, {287, 1} } },
    -- Pink Piwi Ring
    {item=8224, ingredients={ {287, 1}, {6903, 1} } },
    -- Moowolf Ring
    {item=8263, ingredients={ {8400, 1}, {6956, 1}, {8144, 1}, {1696, 1}, {2554, 120}, {8058, 10}, {1691, 5}, {2581, 5} } },
    -- Minotoror Ring
    {item=8269, ingredients={ {2179, 30}, {8311, 20}, {467, 9}, {8321, 5}, {2998, 4}, {1680, 1}, {8056, 1} } },
    -- Minotot Bracelet
    {item=8270, ingredients={ {7035, 16}, {8159, 3}, {8269, 1}, {8313, 1}, {8394, 1}, {8410, 1}, {8308, 60}, {8138, 30} } },
    -- Dragon Pig Ring
    {item=8271, ingredients={ {466, 5}, {467, 5}, {2645, 5}, {2643, 1}, {463, 9}, {479, 8}, {7369, 8} } },
    -- Dreggon Ring
    {item=8289, ingredients={ {8349, 10}, {8350, 10}, {8351, 10}, {8348, 10}, {8357, 5}, {8368, 3}, {8361, 2}, {8402, 1} } },
    -- Shika's Magic Bracelet
    {item=8299, ingredients={ {466, 10}, {8001, 10}, {464, 5}, {7370, 1}, {8311, 30}, {424, 10}, {7369, 10} } },
    -- Black Rat Ring
    {item=8448, ingredients={ {2179, 30}, {8488, 3}, {7035, 2}, {466, 1}, {467, 1}, {7341, 1}, {8004, 1} } },
    -- White Rat Gauntlet
    {item=8454, ingredients={ {7369, 3}, {8489, 2}, {6458, 2}, {3203, 1}, {2179, 50}, {747, 10}, {310, 10} } },
    -- Lord of the Rats' Ceremonial Ring
    {item=8460, ingredients={ {838, 20}, {8488, 6}, {918, 3}, {8490, 2}, {2561, 1}, {2500, 1}, {8454, 1}, {8448, 1} } },
    -- Ancestral Ring
    {item=8466, ingredients={ {2420, 1}, {836, 1}, {434, 100}, {435, 100}, {8496, 3}, {918, 3}, {1602, 1} } },
    -- Soft Oak Ring
    {item=8470, ingredients={ {836, 1}, {6490, 1}, {1559, 1}, {2420, 1}, {1602, 1}, {6488, 5}, {6486, 4}, {926, 3} } },
    -- Honoh Ring
    {item=8714, ingredients={ {8796, 5}, {8792, 5}, {8793, 4}, {8810, 4}, {8771, 3}, {8789, 1}, {8790, 18}, {8788, 16} } },
    -- Blaber Ring
    {item=8715, ingredients={ {8790, 18}, {8788, 16}, {8807, 6}, {8796, 5}, {8792, 5}, {8793, 4}, {8771, 3}, {8789, 1} } },
    -- Lion Ring
    {item=8716, ingredients={ {8789, 1}, {8790, 18}, {8788, 16}, {8796, 5}, {8792, 5}, {8793, 4}, {8736, 4}, {8771, 3} } },
    -- Ring Bellious
    {item=8717, ingredients={ {8789, 1}, {8790, 18}, {8788, 16}, {8796, 5}, {8792, 5}, {8793, 4}, {8737, 4}, {8771, 4} } },
    -- Memo Ring
    {item=8718, ingredients={ {8811, 4}, {8771, 3}, {8789, 1}, {8790, 18}, {8788, 16}, {8792, 5}, {8796, 5}, {8793, 4} } },
    -- Rememb Ring
    {item=8719, ingredients={ {8788, 16}, {8792, 5}, {8796, 5}, {8793, 4}, {8741, 4}, {8771, 3}, {8789, 1}, {8790, 18} } },
    -- Chee Ring
    {item=8720, ingredients={ {8771, 3}, {8789, 1}, {8790, 18}, {8788, 16}, {8792, 5}, {8796, 5}, {8755, 4}, {8793, 4} } },
    -- Ear Ring
    {item=8721, ingredients={ {8796, 5}, {8739, 4}, {8793, 4}, {8771, 3}, {8789, 1}, {8790, 18}, {8788, 16}, {8792, 5} } },
    -- Elkebi Ring
    {item=8722, ingredients={ {8771, 3}, {8789, 1}, {8790, 18}, {8788, 16}, {8792, 5}, {8796, 5}, {2280, 4}, {8793, 4} } },
    -- Subma Ring
    {item=8723, ingredients={ {8771, 3}, {2560, 2}, {8789, 1}, {8790, 18}, {8788, 16}, {8796, 5}, {8792, 5}, {8793, 4} } },
    -- Hai Ring
    {item=8724, ingredients={ {8788, 16}, {8796, 5}, {8792, 5}, {8754, 4}, {8793, 4}, {8771, 3}, {8789, 1}, {8790, 18} } },
    -- Ring Neinwonwon
    {item=8725, ingredients={ {8788, 16}, {8796, 5}, {8792, 5}, {8793, 4}, {8729, 3}, {8771, 3}, {8789, 1}, {8790, 18} } },
    -- Mopy King Sovereign Seal
    {item=8859, ingredients={ {8801, 250}, {8740, 15}, {8326, 10}, {8811, 6}, {8807, 6}, {8810, 5}, {8809, 4}, {8916, 1} } },
    -- Coral Ring
    {item=8860, ingredients={ {2505, 1}, {8744, 15}, {7032, 10}, {7033, 8}, {8729, 1}, {8738, 1} } },
    -- Snailmet Ring
    {item=8865, ingredients={ {8793, 8}, {8738, 5}, {8764, 4}, {8121, 1}, {8832, 45}, {2538, 32}, {8681, 28}, {8736, 15} } },
    -- Ring Dikuloos
    {item=8872, ingredients={ {8733, 1}, {8735, 1}, {8734, 1}, {8730, 1}, {8732, 1} } },
    -- Kringlove
    {item=8877, ingredients={ {8801, 31}, {8808, 23}, {8806, 18}, {8810, 12}, {1830, 8}, {1837, 4}, {8813, 2}, {8812, 1} } },
    -- Polished Ring
    {item=8879, ingredients={ {8762, 25}, {8736, 3}, {8737, 2}, {8769, 1}, {8741, 1}, {2058, 1} } },
    -- Solo's Ring
    {item=8881, ingredients={ {8730, 8}, {2549, 1}, {2540, 1}, {2543, 1}, {8744, 15} } },
    -- Head Band
    {item=8991, ingredients={ {8812, 1}, {600, 200}, {8801, 41}, {8807, 20}, {8811, 12}, {1817, 5}, {1828, 4}, {8813, 3} } },
    -- Cherry Blop Ring
    {item=9122, ingredients={ {1776, 10}, {9382, 1}, {9388, 1}, {6464, 1}, {2556, 50}, {1775, 50} } },
    -- Pippin Blop Ring
    {item=9123, ingredients={ {1773, 50}, {2556, 50}, {1774, 10}, {6464, 1}, {9388, 1}, {9382, 1} } },
    -- Coco Blop Ring
    {item=9124, ingredients={ {1772, 10}, {6464, 1}, {9382, 1}, {9388, 1}, {2556, 50}, {1770, 50} } },
    -- Indigo Blop Ring
    {item=9125, ingredients={ {9382, 1}, {1777, 50}, {2556, 50}, {1778, 10}, {6464, 1}, {9388, 1} } },
    -- Royal Cherry Blop Ring
    {item=9126, ingredients={ {9383, 10}, {9388, 5}, {9384, 1}, {9122, 1}, {1775, 100}, {2556, 100}, {9381, 10} } },
    -- Royal Pippin Blop Ring
    {item=9127, ingredients={ {2556, 100}, {1773, 100}, {9381, 10}, {9383, 10}, {9388, 5}, {9123, 1}, {9387, 1} } },
    -- Royal Coco Blop Ring
    {item=9128, ingredients={ {9124, 1}, {9385, 1}, {1770, 100}, {2556, 100}, {9381, 10}, {9383, 10}, {9388, 5} } },
    -- Royal Indigo Blop Ring
    {item=9129, ingredients={ {9383, 10}, {9388, 5}, {9386, 1}, {9125, 1}, {1777, 100}, {2556, 100}, {9381, 10} } },
    -- Colette's Ring
    {item=9131, ingredients={ {2561, 1}, {1674, 15}, {9278, 11}, {9279, 10}, {8786, 5}, {9277, 3}, {9280, 2}, {8879, 1} } },
    -- Tash Ring
    {item=9132, ingredients={ {9277, 12}, {9279, 10}, {9267, 10}, {8770, 3}, {8755, 18}, {8788, 17}, {8804, 15}, {8803, 13} } },
    -- Fungal Ring
    {item=9133, ingredients={ {2035, 3}, {9280, 2}, {8785, 27}, {8783, 21}, {9269, 12}, {9263, 11}, {8810, 9}, {8784, 6} } },
    -- Royal Rainbow Blop Ring
    {item=9148, ingredients={ {9391, 1}, {9127, 1}, {9129, 1}, {9126, 1}, {9382, 10}, {9389, 5}, {9128, 1} } },
    -- Pot-Bellied Bracelet
    {item=9177, ingredients={ {6805, 1}, {8404, 1}, {2559, 50}, {8388, 15}, {8772, 15}, {8786, 10}, {8996, 3}, {9401, 2} } },
    -- Nose Ring
    {item=9178, ingredients={ {8916, 3}, {9401, 3}, {6805, 1}, {2561, 1}, {8773, 15}, {8805, 10}, {8791, 9}, {8380, 8} } },
    -- Ilyzaring
    {item=11543, ingredients={ {11526, 4}, {8387, 2}, {6998, 1}, {1894, 90}, {8359, 28}, {11530, 23}, {7404, 15}, {11529, 10} } },
    -- Qu'Tan Ring
    {item=11544, ingredients={ {11536, 22}, {8385, 16}, {11535, 16}, {11533, 8}, {9280, 5}, {8460, 1}, {2323, 72}, {2642, 46} } },
}

-- Craft for Create an Amulet
local sk12Crafts = {
    -- Small Owl Amulet
    {item=39, ingredients={ {473, 1}, {441, 1} } },
    -- Owl Amulet
    {item=68, ingredients={ {441, 2}, {473, 2} } },
    -- Beautiful Owl Amulet
    {item=69, ingredients={ {473, 3}, {441, 3} } },
    -- Powerful Owl Amulet
    {item=70, ingredients={ {441, 4}, {473, 4} } },
    -- Small Bear Amulet
    {item=74, ingredients={ {476, 1}, {442, 1} } },
    -- Bear Amulet
    {item=75, ingredients={ {476, 2}, {442, 2} } },
    -- Beautiful Bear Amulet
    {item=76, ingredients={ {442, 3}, {476, 3} } },
    -- Powerful Bear Amulet
    {item=77, ingredients={ {476, 4}, {442, 4} } },
    -- Small Wolf Amulet
    {item=81, ingredients={ {442, 1}, {303, 1} } },
    -- Wolf Amulet
    {item=82, ingredients={ {442, 2}, {303, 2} } },
    -- Beautiful Wolf Amulet
    {item=83, ingredients={ {442, 3}, {303, 3} } },
    -- Powerful Wolf Amulet
    {item=84, ingredients={ {303, 4}, {442, 4} } },
    -- The Gougnole
    {item=157, ingredients={ {351, 10}, {441, 8}, {471, 6} } },
    -- Golden Destiny
    {item=158, ingredients={ {441, 4}, {460, 2}, {445, 2} } },
    -- The Celestial Brooch
    {item=159, ingredients={ {315, 1}, {313, 1}, {446, 1}, {316, 1}, {441, 14} } },
    -- The Stars Custard Tart
    {item=160, ingredients={ {367, 20}, {369, 20}, {757, 15}, {370, 5}, {316, 2}, {466, 2} } },
    -- Fame
    {item=161, ingredients={ {441, 8}, {442, 8}, {443, 7}, {444, 7} } },
    -- Gobball Tear
    {item=279, ingredients={ {441, 2}, {385, 1} } },
    -- Rainbow Necklace
    {item=280, ingredients={ {442, 4}, {473, 2} } },
    -- Kam Assutra Amulet
    {item=323, ingredients={ {466, 2}, {292, 2}, {446, 2}, {442, 2}, {313, 1} } },
    -- Krokette Amulet
    {item=324, ingredients={ {441, 3}, {476, 3} } },
    -- Sram Amulet
    {item=325, ingredients={ {476, 2}, {310, 1} } },
    -- Deulegarnoulle Amulet
    {item=326, ingredients={ {315, 1}, {443, 5}, {442, 4}, {444, 4}, {445, 4} } },
    -- Captain Chafer Amulet
    {item=458, ingredients={ {310, 1}, {441, 8}, {444, 7} } },
    -- Crab Amulet
    {item=610, ingredients={ {2303, 20}, {379, 20}, {2302, 20} } },
    -- Fisherman Amulet
    {item=616, ingredients={ {313, 10}, {598, 10}, {1490, 1} } },
    -- Pirate Amulet
    {item=617, ingredients={ {310, 10}, {1692, 1}, {2336, 1} } },
    -- Koliet Aclou
    {item=766, ingredients={ {303, 5}, {473, 5} } },
    -- Lumberjack Amulet
    {item=783, ingredients={ {460, 3}, {461, 2}, {449, 2}, {474, 2} } },
    -- Puny Owl Pendant
    {item=786, ingredients={ {442, 3}, {303, 1} } },
    -- Dragolyre
    {item=867, ingredients={ {474, 5}, {848, 1}, {463, 1}, {460, 10}, {472, 5} } },
    -- Silicate Amulet
    {item=916, ingredients={ {474, 2}, {463, 2}, {467, 1}, {313, 2}, {446, 2} } },
    -- Autumn Leaf
    {item=917, ingredients={ {466, 2}, {920, 1}, {472, 10}, {449, 10}, {474, 10}, {470, 10}, {313, 10} } },
    -- Amulet Itbe
    {item=1330, ingredients={ {312, 5}, {473, 4}, {441, 1} } },
    -- Oly Medal
    {item=1331, ingredients={ {474, 8}, {461, 4}, {449, 3}, {465, 1}, {350, 10}, {460, 10} } },
    -- Xelor Amulet
    {item=1474, ingredients={ {313, 3}, {446, 2}, {467, 1}, {465, 1}, {466, 1}, {315, 1} } },
    -- Crackler Pendant
    {item=1476, ingredients={ {350, 14}, {431, 9}, {545, 2}, {546, 2}, {467, 1} } },
    -- Lucky Mulette
    {item=1477, ingredients={ {460, 5}, {472, 5}, {747, 3}, {746, 2}, {748, 1} } },
    -- Harmony
    {item=1478, ingredients={ {463, 2}, {461, 12}, {444, 11}, {445, 11}, {471, 10} } },
    -- Peace
    {item=1480, ingredients={ {471, 8}, {463, 2}, {461, 8}, {442, 8}, {443, 8} } },
    -- Understanding
    {item=1481, ingredients={ {442, 8}, {443, 8}, {476, 4}, {460, 4}, {463, 1} } },
    -- Palid Emblem
    {item=1482, ingredients={ {460, 8}, {461, 8}, {444, 8}, {445, 8}, {464, 1} } },
    -- Kabbala
    {item=1483, ingredients={ {308, 1}, {442, 8}, {443, 4}, {445, 2} } },
    -- Modified Amulet Itbe
    {item=1484, ingredients={ {441, 8}, {312, 8}, {473, 4} } },
    -- Pippin
    {item=1485, ingredients={ {444, 7}, {442, 2}, {471, 1}, {158, 1} } },
    -- Holy Medal
    {item=1486, ingredients={ {443, 4}, {445, 4}, {461, 6}, {471, 4}, {460, 4} } },
    -- Pin of Mhete
    {item=1489, ingredients={ {445, 3}, {444, 3}, {471, 2}, {843, 1} } },
    -- Nhanor Kibrill Chain
    {item=1490, ingredients={ {441, 10}, {442, 1}, {444, 1} } },
    -- Liche Chain
    {item=1491, ingredients={ {449, 10}, {2358, 10}, {1476, 1}, {313, 1}, {465, 1} } },
    -- The Treemu
    {item=1619, ingredients={ {1611, 5}, {1612, 5}, {1660, 1}, {464, 1}, {1610, 5} } },
    -- Moskito Ambamulet
    {item=1661, ingredients={ {445, 1}, {474, 1}, {461, 1}, {463, 15}, {444, 2} } },
    -- Arachnee Ambamulet
    {item=1662, ingredients={ {445, 1}, {464, 6}, {444, 3}, {474, 1}, {461, 1} } },
    -- Claw's Jigsaw Amulet
    {item=2388, ingredients={ {2357, 10}, {313, 10}, {274, 1}, {420, 1}, {2358, 10} } },
    -- Badoul's Amulet
    {item=2389, ingredients={ {2358, 10}, {313, 10}, {2357, 10}, {444, 8}, {276, 1} } },
    -- Amulet of Luck
    {item=2390, ingredients={ {1773, 10}, {1770, 10}, {1775, 10}, {1777, 10}, {467, 2} } },
    -- Amulet of Double Luck
    {item=2391, ingredients={ {1773, 30}, {1777, 30}, {1775, 30}, {1770, 30}, {313, 10}, {467, 6}, {465, 6} } },
    -- Modified Dragolyre
    {item=2392, ingredients={ {844, 2}, {847, 2}, {846, 2}, {845, 2}, {867, 1} } },
    -- Omelette Amulet
    {item=2393, ingredients={ {367, 50}, {442, 10}, {443, 10} } },
    -- Bud
    {item=2394, ingredients={ {594, 20}, {1774, 4}, {1611, 2}, {1687, 1}, {437, 30} } },
    -- Mad Bacon's Scarf
    {item=2395, ingredients={ {2315, 10}, {388, 10}, {418, 10}, {2268, 10}, {2282, 2} } },
    -- Hozuki Lampulet
    {item=2396, ingredients={ {1686, 1}, {1333, 1}, {1891, 1} } },
    -- The Bibelaw
    {item=2397, ingredients={ {2358, 8}, {415, 6}, {365, 4}, {2179, 2}, {1652, 2}, {1775, 1} } },
    -- Eye of the Kaniger
    {item=2398, ingredients={ {446, 8}, {444, 8}, {350, 8}, {313, 8}, {2552, 1}, {2656, 1} } },
    -- Gris-Gris
    {item=2399, ingredients={ {2358, 2}, {1690, 1}, {420, 1} } },
    -- Fire Amukwak
    {item=2424, ingredients={ {313, 3}, {412, 3}, {446, 2}, {350, 2}, {442, 2} } },
    -- Gobball Amulet
    {item=2425, ingredients={ {384, 1}, {883, 1}, {383, 1} } },
    -- Arachnamu
    {item=2426, ingredients={ {435, 1}, {463, 1}, {2249, 1}, {365, 1} } },
    -- Royal Gobball Amulet
    {item=2443, ingredients={ {2465, 5}, {2425, 1}, {2453, 20}, {2460, 20}, {383, 10}, {2463, 10} } },
    -- Gelamu
    {item=2472, ingredients={ {757, 15}, {994, 10}, {995, 10}, {993, 10}, {369, 8}, {996, 1} } },
    -- Adventurer Amulet
    {item=2478, ingredients={ {70, 2}, {77, 2} } },
    -- Vegamu
    {item=2498, ingredients={ {421, 30}, {533, 25}, {306, 20}, {307, 2}, {6789, 1} } },
    -- Kaa Amulet
    {item=4381, ingredients={ {465, 3}, {467, 3}, {313, 30}, {445, 30}, {446, 30}, {2357, 10}, {1660, 4} } },
    -- Plain Crackler Amulet
    {item=4684, ingredients={ {2304, 5}, {2252, 2}, {2306, 1}, {2305, 1}, {2251, 1}, {313, 20} } },
    -- Skill of Dolls
    {item=5122, ingredients={ {467, 3}, {1660, 2}, {926, 1}, {470, 10}, {449, 10}, {465, 3} } },
    -- Renewed Amulet
    {item=6443, ingredients={ {466, 10}, {316, 10}, {479, 10}, {470, 8}, {926, 1}, {313, 30}, {465, 20}, {1660, 20} } },
    -- Turquoise Amulet
    {item=6444, ingredients={ {313, 20}, {446, 20}, {474, 10}, {449, 10}, {1660, 2}, {467, 2} } },
    -- Dark Miner Amulet
    {item=6459, ingredients={ {2357, 4}, {2358, 4}, {746, 1}, {748, 1} } },
    -- Winter Leaf
    {item=6460, ingredients={ {750, 1}, {926, 1}, {479, 5}, {467, 4}, {466, 2}, {6457, 1}, {6458, 1} } },
    -- Spring Leaf
    {item=6461, ingredients={ {6458, 2}, {917, 1}, {2251, 1}, {465, 6}, {316, 5}, {748, 4}, {6457, 3}, {750, 3} } },
    -- Summer Leaf
    {item=6462, ingredients={ {2358, 5}, {470, 1}, {748, 1}, {746, 1}, {308, 1} } },
    -- Shika Amulet
    {item=6466, ingredients={ {2357, 3}, {431, 1}, {313, 5}, {350, 5}, {474, 3} } },
    -- Wabbit-tooth Amulet
    {item=6499, ingredients={ {2288, 10}, {654, 10}, {361, 5}, {418, 2}, {305, 150}, {372, 26} } },
    -- Bearman's Necklace
    {item=6723, ingredients={ {2318, 1}, {313, 6}, {350, 5}, {476, 3} } },
    -- Ice Kwakamulet
    {item=6731, ingredients={ {442, 2}, {350, 2}, {411, 3}, {313, 3}, {446, 2} } },
    -- Deceitful Amulet
    {item=6742, ingredients={ {6736, 5}, {6737, 5}, {420, 1}, {2254, 50}, {2602, 30} } },
    -- Earth Kwakamulet
    {item=6746, ingredients={ {313, 3}, {442, 2}, {350, 2}, {446, 2}, {1142, 3} } },
    -- Wind Kwakamulet
    {item=6747, ingredients={ {313, 3}, {442, 2}, {446, 2}, {350, 2}, {413, 3} } },
    -- Farle's Ears
    {item=6766, ingredients={ {465, 3}, {467, 2}, {420, 1}, {316, 1}, {315, 1}, {313, 60}, {2018, 6} } },
    -- Field Amulet
    {item=6789, ingredients={ {374, 10}, {309, 10}, {2661, 2} } },
    -- Chief Crocodyl Amulet
    {item=6817, ingredients={ {316, 1}, {315, 1}, {1663, 25}, {1613, 20}, {467, 2}, {2296, 1} } },
    -- Moskito Amulet
    {item=6918, ingredients={ {371, 10}, {519, 5}, {307, 2} } },
    -- Lars Amulet
    {item=6923, ingredients={ {39, 1}, {74, 1}, {81, 1} } },
    -- Crackler Amulet
    {item=6934, ingredients={ {447, 1}, {2252, 4}, {450, 1}, {431, 1}, {448, 1} } },
    -- Lahilama Medal
    {item=6999, ingredients={ {7033, 5}, {7032, 5}, {7013, 3}, {749, 1}, {7264, 1} } },
    -- Bambamulet
    {item=7000, ingredients={ {7016, 4}, {7036, 1}, {7262, 1}, {7033, 6}, {7032, 6} } },
    -- Bow'hee Talisman
    {item=7001, ingredients={ {7264, 8}, {7036, 3}, {7035, 2}, {7028, 1}, {7262, 8}, {7263, 8} } },
    -- Lil' Resin
    {item=7002, ingredients={ {7027, 1}, {7036, 1}, {7274, 1}, {7265, 1}, {7035, 1}, {7026, 1}, {7028, 1} } },
    -- Elya Wood's Talisman
    {item=7003, ingredients={ {543, 10}, {2239, 10}, {546, 10}, {7028, 5}, {7304, 2}, {316, 2}, {6441, 1}, {7024, 70} } },
    -- Smoothitch o'Bal
    {item=7012, ingredients={ {2034, 1}, {995, 20}, {994, 20}, {993, 20}, {2242, 10}, {996, 1} } },
    -- Larvamulet
    {item=7106, ingredients={ {363, 5}, {364, 2}, {362, 2}, {2328, 1} } },
    -- Aykido Medal
    {item=7136, ingredients={ {7270, 1}, {7403, 1}, {7026, 10}, {7035, 9}, {7036, 8}, {7370, 4}, {7272, 1}, {7271, 1} } },
    -- Tanukouï San Collar
    {item=7221, ingredients={ {2328, 30}, {7283, 30}, {7027, 10}, {7370, 8}, {7036, 5}, {7386, 1}, {7410, 1}, {7284, 40} } },
    -- Akwadala Amulet
    {item=7250, ingredients={ {7222, 50}, {7013, 5}, {7032, 5}, {472, 5}, {7033, 2} } },
    -- Amulet Terrdala
    {item=7251, ingredients={ {7224, 50}, {7016, 10}, {747, 4}, {315, 3}, {7035, 3}, {7272, 1}, {7028, 1} } },
    -- Feudala Amulet
    {item=7252, ingredients={ {7225, 50}, {7035, 10}, {7016, 10}, {7036, 5}, {7028, 4}, {316, 3}, {7270, 2} } },
    -- Aerdala Amulet
    {item=7253, ingredients={ {7223, 50}, {7033, 10}, {7028, 5}, {7032, 1}, {7263, 1}, {7262, 1} } },
    -- Kitsou Amulet
    {item=7342, ingredients={ {7279, 1}, {7281, 1}, {7278, 1}, {7282, 1} } },
    -- Gobkool Amulet
    {item=7880, ingredients={ {383, 10}, {7906, 5}, {2425, 1} } },
    -- Koalak Amulet
    {item=8003, ingredients={ {8083, 2}, {8002, 2}, {8060, 1}, {8059, 1}, {8062, 1}, {8050, 1} } },
    -- Tofu Amulet
    {item=8108, ingredients={ {301, 18}, {366, 11}, {367, 10}, {8557, 1} } },
    -- Golden Scarabugly Amulet
    {item=8120, ingredients={ {8161, 1}, {1466, 20}, {1465, 20}, {1464, 20}, {1467, 20} } },
    -- Farmer Amulet
    {item=8123, ingredients={ {533, 15}, {400, 15}, {422, 10}, {393, 1} } },
    -- Boowolfulet
    {item=8129, ingredients={ {292, 1}, {439, 25}, {440, 15}, {2578, 10}, {2575, 1}, {2576, 1} } },
    -- Legendary Crackler Amulet
    {item=8150, ingredients={ {467, 15}, {8102, 15}, {7027, 4}, {6934, 1}, {1476, 1}, {2305, 25}, {466, 15} } },
    -- Red Piwi Amulet
    {item=8213, ingredients={ {6900, 1}, {287, 1} } },
    -- Blue Piwi Amulet
    {item=8214, ingredients={ {287, 1}, {6897, 1} } },
    -- Purple Piwi Amulet
    {item=8215, ingredients={ {287, 1}, {6898, 1} } },
    -- Green Piwi Amulet
    {item=8216, ingredients={ {6899, 1}, {287, 1} } },
    -- Yellow Piwi Amulet
    {item=8217, ingredients={ {6902, 1}, {287, 1} } },
    -- Pink Piwi Amulet
    {item=8218, ingredients={ {6903, 1}, {287, 1} } },
    -- Amoolet
    {item=8262, ingredients={ {2806, 1}, {7302, 1}, {8129, 1}, {8002, 50}, {3209, 10}, {292, 5}, {7381, 2}, {7384, 2} } },
    -- Minotoror Necklace
    {item=8268, ingredients={ {2667, 1}, {8250, 20}, {7370, 13}, {840, 10}, {2998, 5}, {2320, 1}, {2999, 1} } },
    -- Minotot Necklace
    {item=8272, ingredients={ {8268, 1}, {8402, 1}, {8141, 30}, {8161, 30}, {466, 25}, {467, 25}, {7036, 15}, {8409, 1} } },
    -- Dragon Pig Necklace
    {item=8273, ingredients={ {2646, 12}, {2287, 8}, {8323, 5}, {8393, 1}, {479, 30}, {8390, 25}, {8388, 15} } },
    -- Dreggon Amulet
    {item=8290, ingredients={ {8364, 1}, {8102, 50}, {8353, 10}, {8159, 10}, {8354, 10}, {8355, 10}, {8352, 10}, {8368, 1} } },
    -- Shika's Ears
    {item=8298, ingredients={ {2659, 130}, {2304, 100}, {2661, 50}, {7369, 12}, {1671, 6}, {918, 2}, {420, 1} } },
    -- Black Rat Necklace
    {item=8445, ingredients={ {7369, 10}, {8485, 2}, {467, 1}, {159, 1}, {2322, 50}, {2491, 20}, {2320, 10} } },
    -- White Rat Necklace
    {item=8453, ingredients={ {1613, 10}, {2323, 5}, {7370, 3}, {8489, 2}, {1474, 1}, {1663, 20}, {448, 10} } },
    -- Lord of the Rats' Ceremonial Necklace
    {item=8459, ingredients={ {8445, 1}, {8453, 1}, {2259, 100}, {8481, 100}, {2322, 50}, {8484, 5}, {8490, 5}, {8380, 1} } },
    -- Ancestral Torc
    {item=8465, ingredients={ {2426, 20}, {8494, 5}, {8496, 5}, {1619, 1}, {434, 150}, {1611, 35}, {7925, 30} } },
    -- Soft Oak Talisman
    {item=8469, ingredients={ {2426, 30}, {1611, 25}, {1610, 10}, {1612, 5}, {926, 5}, {1660, 3}, {6490, 2}, {8465, 1} } },
    -- Zothulet
    {item=8863, ingredients={ {8404, 1}, {8800, 150}, {8802, 4}, {8777, 3}, {8804, 3}, {8805, 3}, {8806, 3}, {8803, 2} } },
    -- Ouassulet
    {item=8874, ingredients={ {8807, 3}, {2392, 1}, {8809, 1}, {8801, 71}, {8810, 4}, {8811, 4}, {2632, 3} } },
    -- Amufafah
    {item=8880, ingredients={ {8755, 2}, {1558, 1}, {8763, 18}, {8756, 15}, {8766, 10}, {8767, 9}, {8753, 3} } },
    -- Ougaamulet
    {item=9130, ingredients={ {9281, 1}, {8874, 1}, {8792, 25}, {8793, 15}, {9277, 11}, {9278, 9}, {9263, 8}, {9280, 1} } },
    -- Cherry Amublop
    {item=9149, ingredients={ {1776, 10}, {9388, 1}, {1485, 1}, {9381, 1}, {1775, 50}, {2556, 50} } },
    -- Pippin Amublop
    {item=9150, ingredients={ {2556, 50}, {1774, 10}, {1485, 1}, {9381, 1}, {9388, 1}, {1773, 50} } },
    -- Coco Amublop
    {item=9151, ingredients={ {9388, 1}, {9381, 1}, {1485, 1}, {1770, 50}, {2556, 50}, {1772, 10} } },
    -- Indigo Amublop
    {item=9152, ingredients={ {1778, 10}, {1485, 1}, {9388, 1}, {9381, 1}, {2556, 50}, {1777, 50} } },
    -- Royal Cherry Amublop
    {item=9153, ingredients={ {2556, 100}, {1775, 100}, {9383, 10}, {9382, 10}, {9388, 5}, {9149, 1}, {9384, 1} } },
    -- Royal Pippin Amublop
    {item=9154, ingredients={ {1773, 100}, {2556, 100}, {9382, 10}, {9383, 10}, {9388, 5}, {9150, 1}, {9387, 1} } },
    -- Royal Coco Amublop
    {item=9155, ingredients={ {1770, 100}, {9383, 10}, {9382, 10}, {9388, 5}, {9385, 1}, {9151, 1}, {2556, 100} } },
    -- Royal Indigo Amublop
    {item=9156, ingredients={ {9386, 1}, {9152, 1}, {2556, 100}, {1777, 100}, {9383, 10}, {9382, 10}, {9388, 5} } },
    -- Royal Rainbow Amublop
    {item=9157, ingredients={ {9389, 5}, {9156, 1}, {9154, 1}, {9391, 1}, {9153, 1}, {9155, 1}, {9381, 10} } },
    -- Memori Amulet
    {item=9179, ingredients={ {8916, 3}, {9401, 2}, {8996, 2}, {6804, 1}, {8741, 32}, {8788, 25}, {8773, 21}, {8806, 15} } },
    -- Amunita
    {item=9463, ingredients={ {8765, 24}, {8790, 18}, {9267, 15}, {9269, 14}, {8811, 12}, {8802, 12}, {8398, 2}, {2527, 1} } },
    -- Kralomansion
    {item=9464, ingredients={ {8812, 1}, {8801, 45}, {8808, 28}, {8807, 13}, {1827, 6}, {1859, 5}, {8813, 4}, {8809, 1} } },
    -- Gobbamu
    {item=10836, ingredients={ {9383, 45}, {3208, 26}, {2492, 24}, {8252, 21}, {10835, 1}, {9381, 50} } },
    -- Qu'Tamulet
    {item=11546, ingredients={ {11532, 3}, {8789, 2}, {7136, 1}, {2283, 36}, {9277, 22}, {11536, 20}, {11534, 12}, {2026, 11} } },
}

local requirements = {jobID = jobID, toolIDs = toolIDs}
registerCraftSkill(11, sk11Crafts, requirements, ingredientsForCraftJob(jobID), jobID)
registerCraftSkill(12, sk12Crafts, requirements, ingredientsForCraftJob(jobID), jobID)
