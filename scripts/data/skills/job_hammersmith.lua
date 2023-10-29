local jobID = HammerSmithJob
local toolIDs = {493}

-- Craft for Forge a Hammer
local sk19Crafts = {
    -- Small Twiggy Hammer
    {item=144, ingredients={ {473, 2}, {303, 1} } },
    -- Twiggy Hammer
    {item=145, ingredients={ {473, 3}, {303, 1} } },
    -- Great Twiggy Hammer
    {item=146, ingredients={ {473, 4}, {303, 1} } },
    -- Powerful Twiggy Hammer
    {item=147, ingredients={ {473, 5}, {303, 1} } },
    -- Small Scraping Hammer
    {item=221, ingredients={ {473, 6}, {476, 5}, {445, 1} } },
    -- Scraping Hammer
    {item=222, ingredients={ {476, 5}, {473, 5}, {445, 2} } },
    -- Great Scraping Hammer
    {item=223, ingredients={ {476, 5}, {473, 4}, {445, 3} } },
    -- Powerful Scraping Hammer
    {item=224, ingredients={ {473, 5}, {445, 3}, {476, 5} } },
    -- Small Ivory Hammer
    {item=227, ingredients={ {479, 3}, {443, 3}, {476, 2} } },
    -- Ivory Hammer
    {item=228, ingredients={ {479, 4}, {443, 3}, {476, 3} } },
    -- Great Ivory Hammer
    {item=229, ingredients={ {479, 5}, {443, 4}, {476, 2} } },
    -- Powerful Ivory Hammer
    {item=230, ingredients={ {479, 6}, {443, 4}, {476, 3} } },
    -- Kaiser
    {item=233, ingredients={ {460, 8}, {474, 8}, {461, 8}, {313, 4}, {350, 1}, {481, 1}, {315, 1} } },
    -- Ragnarok
    {item=234, ingredients={ {313, 7}, {465, 5}, {316, 3}, {449, 1}, {474, 12}, {350, 10} } },
    -- Cerberus
    {item=235, ingredients={ {474, 2}, {460, 1}, {441, 8} } },
    -- Barabas
    {item=236, ingredients={ {476, 5}, {445, 1}, {444, 10}, {442, 5} } },
    -- Sargasse
    {item=237, ingredients={ {473, 6}, {471, 5}, {446, 4}, {441, 2} } },
    -- The Chafer Hammer
    {item=271, ingredients={ {471, 1}, {310, 9}, {445, 2}, {460, 1} } },
    -- Birming Hammer
    {item=342, ingredients={ {441, 10}, {412, 2}, {411, 1} } },
    -- Hammer of Dawn
    {item=455, ingredients={ {446, 5}, {460, 2}, {463, 1}, {471, 1} } },
    -- Hammer Smith's Hammer
    {item=493, ingredients={ {303, 2}, {312, 1} } },
    -- Sword Smith's Hammer
    {item=494, ingredients={ {312, 2}, {303, 1} } },
    -- Dagger Smith's Hammer
    {item=495, ingredients={ {303, 2}, {441, 1} } },
    -- Shovel Smith's Hammer
    {item=496, ingredients={ {441, 2}, {303, 1} } },
    -- Axe Smith's Hammer
    {item=922, ingredients={ {312, 2}, {476, 1} } },
    -- Dha's Small Mace
    {item=1060, ingredients={ {60, 1}, {473, 4}, {312, 4} } },
    -- Dha's Mace
    {item=1061, ingredients={ {312, 6}, {473, 5}, {60, 1} } },
    -- Dha's Solid Mace
    {item=1062, ingredients={ {312, 8}, {473, 6}, {60, 1} } },
    -- Dha's Unbreakable Mace
    {item=1063, ingredients={ {312, 10}, {473, 7}, {60, 1} } },
    -- Small R'Unique Hammer
    {item=1064, ingredients={ {852, 10}, {476, 4}, {444, 3} } },
    -- R'Unique Hammer
    {item=1065, ingredients={ {852, 12}, {476, 5}, {444, 4} } },
    -- Great R'Unique Hammer
    {item=1066, ingredients={ {852, 13}, {476, 6}, {444, 6} } },
    -- Imposing R'Unique Hammer
    {item=1067, ingredients={ {852, 15}, {444, 7}, {476, 7} } },
    -- Small Mace Tmosfer
    {item=1068, ingredients={ {350, 5}, {431, 3}, {940, 2}, {471, 1} } },
    -- Mace Tmosfer
    {item=1069, ingredients={ {940, 2}, {350, 6}, {431, 4}, {471, 2} } },
    -- Long Mace Tmosfer
    {item=1070, ingredients={ {350, 7}, {431, 4}, {940, 3}, {471, 2} } },
    -- Fearsome Mace Tmosfer
    {item=1071, ingredients={ {350, 8}, {431, 5}, {940, 3}, {471, 3} } },
    -- Small Outar Hammer
    {item=1072, ingredients={ {256, 2}, {473, 20}, {138, 3}, {313, 3} } },
    -- Outar Hammer
    {item=1073, ingredients={ {473, 20}, {313, 4}, {140, 3}, {257, 2} } },
    -- Great Outar Hammer
    {item=1074, ingredients={ {313, 5}, {183, 3}, {258, 2}, {473, 20} } },
    -- Imposing Outar Hammer
    {item=1075, ingredients={ {185, 3}, {259, 2}, {473, 20}, {313, 6} } },
    -- Small Aj Taye Mace
    {item=1076, ingredients={ {449, 4}, {752, 2}, {303, 10}, {325, 6}, {476, 5} } },
    -- Aj Taye Mace
    {item=1077, ingredients={ {476, 6}, {449, 5}, {752, 3}, {303, 10}, {325, 6} } },
    -- Great Aj Taye Mace
    {item=1078, ingredients={ {752, 4}, {303, 10}, {476, 7}, {325, 7}, {449, 6} } },
    -- Powerful Aj Taye Mace
    {item=1079, ingredients={ {303, 10}, {476, 7}, {449, 7}, {325, 7}, {752, 5} } },
    -- Small Pospodrol Hammer
    {item=1080, ingredients={ {1087, 1}, {519, 15}, {442, 10}, {472, 5}, {450, 2}, {734, 2} } },
    -- Pospodrol Hammer
    {item=1081, ingredients={ {519, 20}, {442, 10}, {472, 5}, {450, 3}, {734, 2}, {1087, 1} } },
    -- Large Pospodrol Hammer
    {item=1082, ingredients={ {519, 25}, {442, 13}, {472, 6}, {450, 4}, {734, 2}, {1087, 1} } },
    -- Powerful Pospodrol Hammer
    {item=1083, ingredients={ {734, 2}, {1087, 1}, {519, 25}, {442, 15}, {472, 7}, {450, 5} } },
    -- Hammer of Wrongs
    {item=1084, ingredients={ {748, 5}, {750, 5}, {465, 4}, {466, 4}, {2566, 2}, {2305, 10}, {6458, 5} } },
    -- Falistos' Maul
    {item=1085, ingredients={ {6457, 5}, {2306, 4}, {2251, 4}, {316, 4}, {926, 2}, {2250, 15}, {6458, 5} } },
    -- The Bhharnsheee Hammer
    {item=1379, ingredients={ {1348, 5}, {1338, 3}, {271, 1}, {449, 1}, {312, 15} } },
    -- Tortoi Hammer
    {item=1383, ingredients={ {461, 4}, {472, 4}, {746, 1}, {6457, 1}, {748, 1}, {747, 1} } },
    -- The Metronome
    {item=1393, ingredients={ {441, 3}, {746, 1}, {2304, 1}, {747, 1}, {471, 4}, {472, 4} } },
    -- Aclou Mallet
    {item=1399, ingredients={ {441, 4}, {444, 4}, {474, 3} } },
    -- Ha Hammer
    {item=1403, ingredients={ {312, 5}, {442, 5}, {460, 5}, {446, 2}, {303, 6} } },
    -- Toh'Lo Hammer
    {item=1404, ingredients={ {461, 4}, {470, 4}, {750, 1}, {748, 1}, {2251, 1} } },
    -- The Cardboard Twot
    {item=1479, ingredients={ {747, 1}, {476, 3}, {2358, 3}, {748, 1}, {431, 1} } },
    -- Dagger Smithmagus Hammer
    {item=1520, ingredients={ {476, 10}, {431, 1} } },
    -- Sword Forgemage Hammer
    {item=1539, ingredients={ {431, 1}, {460, 5} } },
    -- Shovel Smithmagus Hammer
    {item=1560, ingredients={ {303, 18}, {431, 1} } },
    -- Hammer Smithmagus Hammer
    {item=1561, ingredients={ {460, 9}, {431, 1} } },
    -- Axe Smithmagus's Hammer
    {item=1562, ingredients={ {474, 5}, {431, 1} } },
    -- Gobball Hammer
    {item=2416, ingredients={ {304, 10}, {2451, 1}, {383, 1} } },
    -- Knuckle Rapping Hammer
    {item=3357, ingredients={ {474, 5}, {446, 5}, {472, 5}, {746, 1}, {431, 1} } },
    -- Polpullet Hammer
    {item=6503, ingredients={ {472, 4}, {476, 4}, {2358, 4}, {749, 1}, {2304, 1}, {6457, 1} } },
    -- Fantal Hammer
    {item=6504, ingredients={ {750, 3}, {470, 6}, {449, 6}, {2250, 5}, {2252, 4}, {465, 3}, {6457, 3} } },
    -- Rehadaure Hammer
    {item=6505, ingredients={ {926, 2}, {2305, 10}, {449, 6}, {2250, 5}, {6458, 3}, {467, 3}, {466, 3} } },
    -- Terps Hammer
    {item=6507, ingredients={ {470, 15}, {2566, 8}, {466, 4}, {467, 4}, {748, 3}, {6457, 3}, {1660, 2} } },
    -- Pinambour Hammer
    {item=6508, ingredients={ {1612, 2}, {746, 1}, {466, 1}, {6458, 1}, {474, 5}, {1610, 2} } },
    -- Crick Hammer
    {item=6509, ingredients={ {2306, 10}, {465, 8}, {2566, 8}, {6458, 20}, {1610, 12}, {1612, 12}, {2251, 10}, {2305, 10} } },
    -- Castr Hammer
    {item=6510, ingredients={ {316, 4}, {1002, 200}, {461, 20}, {449, 20}, {749, 12}, {6458, 10}, {920, 8}, {1660, 6} } },
    -- Blarney Hammer
    {item=6511, ingredients={ {467, 5}, {920, 2}, {2643, 1}, {470, 20}, {1610, 20}, {749, 20}, {1612, 20}, {465, 5} } },
    -- Pog Hammer
    {item=6512, ingredients={ {2357, 12}, {2358, 12}, {2304, 12}, {578, 10}, {750, 5}, {466, 3}, {465, 2} } },
    -- Refactor Hammer
    {item=6513, ingredients={ {461, 5}, {472, 5}, {464, 1}, {749, 1}, {748, 1}, {434, 8} } },
    -- Shield Smith's Hammer
    {item=7098, ingredients={ {7013, 10}, {7016, 10} } },
    -- Metal Hammer
    {item=7153, ingredients={ {926, 1}, {7407, 1}, {7014, 10}, {7035, 6}, {7036, 6}, {7026, 4}, {7027, 3}, {7028, 3} } },
    -- Toy Hammer
    {item=7154, ingredients={ {7016, 16}, {748, 7}, {750, 5}, {7035, 4}, {7027, 4}, {466, 3}, {7013, 20} } },
    -- Hammer Ingthaiphoons
    {item=7155, ingredients={ {7261, 10}, {7036, 5}, {6457, 4}, {7027, 3}, {750, 2}, {7028, 2}, {2304, 20} } },
    -- Hammer Ican
    {item=7156, ingredients={ {7013, 15}, {7016, 10}, {2305, 4}, {7035, 4}, {750, 4}, {7027, 3}, {7026, 1} } },
    -- Toll Hammer
    {item=7157, ingredients={ {7028, 2}, {7027, 2}, {7403, 1}, {7016, 19}, {2563, 12}, {7017, 7}, {7036, 6}, {6735, 6} } },
    -- Trikidiki Hammer
    {item=7158, ingredients={ {7406, 1}, {7016, 20}, {7289, 18}, {7036, 8}, {7370, 7}, {7290, 6}, {7028, 5}, {7269, 2} } },
    -- Cogito Hammer
    {item=7159, ingredients={ {7035, 4}, {2251, 3}, {7027, 2}, {7405, 1}, {7265, 25}, {7016, 10}, {7017, 6}, {7026, 4} } },
    -- Hammer Sheys
    {item=7197, ingredients={ {918, 10}, {7027, 10}, {7036, 10}, {7370, 9}, {7291, 3}, {7269, 2}, {7410, 1}, {7014, 15} } },
    -- Handyman Hammer
    {item=7650, ingredients={ {446, 10}, {449, 10} } },
    -- Gobkool Hammer
    {item=7882, ingredients={ {2416, 1}, {383, 5}, {2509, 2}, {7906, 2}, {7925, 1} } },
    -- Dunb Hammer
    {item=8096, ingredients={ {8055, 8}, {7404, 2}, {8394, 1}, {2304, 21}, {7925, 15}, {7016, 15}, {7370, 10}, {8363, 10} } },
    -- Hammer O'In
    {item=8097, ingredients={ {7925, 15}, {748, 12}, {8102, 10}, {746, 9}, {7027, 4}, {7410, 2}, {7272, 1}, {2304, 21} } },
    -- Legendary Crackler Hammer
    {item=8148, ingredients={ {448, 15}, {2252, 10}, {750, 4}, {8102, 3}, {6957, 1}, {2251, 1}, {431, 30} } },
    -- Dreggon Hammer
    {item=8294, ingredients={ {8402, 1}, {8058, 1}, {8347, 1}, {387, 100}, {7403, 5}, {8055, 5}, {7036, 5}, {8056, 5} } },
    -- Red Hammer
    {item=8615, ingredients={ {8729, 5}, {8741, 3}, {1404, 1}, {8731, 1}, {8762, 25}, {752, 20}, {2455, 6} } },
    -- Stephammer
    {item=8616, ingredients={ {8789, 1}, {233, 1}, {8767, 90}, {8756, 75}, {8326, 18}, {7035, 10}, {8786, 9}, {8777, 4} } },
    -- Great Coralator Mace
    {item=8825, ingredients={ {8786, 3}, {8775, 3}, {8785, 35}, {8779, 25}, {8738, 10}, {7014, 10}, {472, 10}, {8731, 3} } },
    -- Zoth Girl Hammer
    {item=8826, ingredients={ {8800, 25}, {465, 5}, {8805, 3}, {8765, 3}, {1555, 3}, {1999, 2}, {8770, 1}, {8781, 35} } },
    -- Coralator Mace
    {item=8833, ingredients={ {8765, 1}, {8730, 23}, {2358, 13}, {8729, 4}, {8738, 2}, {8769, 2}, {8755, 1} } },
    -- Kidommer
    {item=8878, ingredients={ {8741, 4}, {8758, 1}, {8767, 24}, {8766, 18}, {461, 9}, {7925, 7}, {1988, 5} } },
    -- Ougaammer
    {item=9117, ingredients={ {9280, 1}, {8765, 28}, {9267, 11}, {9278, 8}, {8770, 2}, {9281, 1}, {8826, 1}, {2640, 1} } },
    -- Hurrian Hammer
    {item=9961, ingredients={ {303, 1}, {749, 1} } },
    -- Scratcher Smith Hammer
    {item=9972, ingredients={ {460, 1}, {444, 10} } },
}

registerCraftSkill(19, sk19Crafts, {jobID = jobID, toolIDs = toolIDs}, ingredientsForCraftJob(jobID), jobID)
