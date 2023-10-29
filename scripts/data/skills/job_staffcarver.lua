local jobID = StaffCarverJob
local toolIDs = {498}

-- Craft for Carve a Staff
local sk17Crafts = {
    -- Twiggy Staff
    {item=138, ingredients={ {473, 3}, {303, 2} } },
    -- Great Twiggy Staff
    {item=139, ingredients={ {473, 4}, {303, 3} } },
    -- Powerful Twiggy Staff
    {item=140, ingredients={ {473, 5}, {303, 4} } },
    -- Small Leafy Staff
    {item=182, ingredients={ {471, 3}, {460, 3}, {473, 2} } },
    -- Leafy Staff
    {item=183, ingredients={ {471, 3}, {460, 3}, {473, 3} } },
    -- Great Leafy Staff
    {item=184, ingredients={ {473, 4}, {471, 3}, {460, 3} } },
    -- Powerful Leafy Staff
    {item=185, ingredients={ {473, 5}, {460, 3}, {471, 3} } },
    -- Staff Bonely
    {item=188, ingredients={ {476, 5}, {460, 2}, {473, 1} } },
    -- Sacred Staff
    {item=194, ingredients={ {303, 4}, {473, 4}, {476, 5} } },
    -- God Rod
    {item=200, ingredients={ {313, 12}, {470, 5}, {472, 4}, {316, 2}, {467, 1}, {315, 1} } },
    -- Hook
    {item=201, ingredients={ {472, 7}, {466, 5}, {315, 1}, {316, 1}, {449, 10} } },
    -- Kryst O'Ball
    {item=204, ingredients={ {463, 4}, {465, 1}, {316, 1}, {449, 10}, {472, 10}, {446, 4} } },
    -- Small Tabi Staff
    {item=317, ingredients={ {473, 2}, {303, 5}, {472, 4}, {449, 4}, {471, 3} } },
    -- Tabi Staff
    {item=318, ingredients={ {473, 4}, {471, 3}, {303, 8}, {472, 5}, {449, 4} } },
    -- Great Tabi Staff
    {item=319, ingredients={ {473, 4}, {303, 23}, {472, 4}, {449, 4}, {471, 4} } },
    -- Powerful Tabi Staff
    {item=320, ingredients={ {472, 7}, {303, 5}, {473, 5}, {449, 4}, {471, 9} } },
    -- Magus Bwork Staff
    {item=335, ingredients={ {471, 21}, {449, 12}, {474, 12}, {409, 10}, {410, 1} } },
    -- Gwandpa Wabbit's Staff
    {item=336, ingredients={ {418, 2}, {471, 8}, {476, 4}, {474, 4}, {472, 3} } },
    -- Treechnid Root Staff
    {item=436, ingredients={ {435, 12}, {434, 10}, {460, 2}, {471, 2}, {476, 1} } },
    -- Gobball Master Staff
    {item=619, ingredients={ {470, 20}, {449, 20}, {472, 10}, {474, 10}, {464, 4}, {316, 2}, {315, 2} } },
    -- Tofu Master Staff
    {item=657, ingredients={ {313, 5}, {465, 1}, {449, 10}, {474, 9}, {470, 6}, {472, 5} } },
    -- Tabi Master Staff
    {item=658, ingredients={ {460, 6}, {472, 6}, {474, 5}, {449, 2}, {464, 1} } },
    -- Horned Staff
    {item=742, ingredients={ {313, 2}, {474, 2}, {463, 1}, {472, 3} } },
    -- Treechnidis Vivitus
    {item=791, ingredients={ {464, 1}, {436, 1}, {311, 1}, {501, 1}, {463, 1} } },
    -- Small Fake Magic Staff
    {item=1109, ingredients={ {583, 10}, {443, 5}, {461, 3}, {101, 1} } },
    -- Fake Magic Staff
    {item=1110, ingredients={ {583, 12}, {443, 6}, {461, 4}, {101, 1} } },
    -- Great Fake Magic Staff
    {item=1111, ingredients={ {583, 14}, {443, 7}, {461, 5}, {101, 1} } },
    -- True Fake Magic Staff
    {item=1147, ingredients={ {583, 16}, {443, 8}, {461, 6}, {101, 1} } },
    -- Small Dakn Staff
    {item=1148, ingredients={ {471, 7}, {476, 7}, {519, 5}, {368, 2} } },
    -- Dakn Staff
    {item=1149, ingredients={ {476, 8}, {471, 8}, {519, 8}, {368, 2} } },
    -- Big Dakn Staff
    {item=1150, ingredients={ {368, 2}, {519, 10}, {471, 9}, {476, 9} } },
    -- Tremendous Dakn Staff
    {item=1151, ingredients={ {519, 14}, {471, 10}, {476, 10}, {368, 2} } },
    -- Small Crystal Staff-Ball
    {item=1152, ingredients={ {313, 3}, {460, 8}, {474, 5}, {757, 4} } },
    -- Crystal Staff-Ball
    {item=1153, ingredients={ {460, 9}, {474, 6}, {757, 5}, {313, 4} } },
    -- Great Crystal Staff-Ball
    {item=1154, ingredients={ {460, 10}, {313, 7}, {474, 7}, {757, 6} } },
    -- Omniscient Crystal Staff-Ball
    {item=1157, ingredients={ {474, 8}, {757, 7}, {313, 6}, {460, 11} } },
    -- Simple Kings' Staff
    {item=1161, ingredients={ {312, 10}, {361, 10}, {449, 8}, {313, 6}, {372, 5} } },
    -- Excellent Kings' Staff
    {item=1162, ingredients={ {312, 20}, {361, 16}, {449, 12}, {313, 9}, {372, 8} } },
    -- Kings' Staff
    {item=1163, ingredients={ {361, 12}, {449, 10}, {313, 7}, {372, 6}, {312, 14} } },
    -- Great Kings' Staff
    {item=1164, ingredients={ {312, 18}, {361, 14}, {449, 12}, {313, 8}, {372, 7} } },
    -- Tont'Ata Staff
    {item=1363, ingredients={ {437, 10}, {303, 5}, {471, 4}, {476, 4}, {474, 2} } },
    -- Forgetfulness Staff
    {item=1364, ingredients={ {472, 3}, {463, 1}, {476, 5}, {2358, 4}, {460, 4} } },
    -- Flatu Lance
    {item=1365, ingredients={ {473, 4}, {435, 4}, {472, 3}, {461, 5} } },
    -- Gobbherd Staff
    {item=1366, ingredients={ {312, 5}, {1340, 3}, {138, 1} } },
    -- Staff of Dina Mite
    {item=1367, ingredients={ {434, 6}, {2358, 4}, {473, 3}, {471, 3}, {303, 10} } },
    -- '110' Spear
    {item=1368, ingredients={ {473, 5}, {476, 4}, {310, 2}, {460, 2} } },
    -- Kaniger Staff
    {item=2066, ingredients={ {2358, 7}, {1002, 5}, {463, 1}, {435, 10}, {474, 10}, {434, 10} } },
    -- Treestaff
    {item=2417, ingredients={ {2249, 20}, {435, 20}, {434, 5}, {463, 4}, {2250, 1} } },
    -- Carnivorous Staff
    {item=2640, ingredients={ {290, 22}, {377, 20}, {426, 10}, {1674, 2}, {6919, 1} } },
    -- Marie Aigue's Staff
    {item=3652, ingredients={ {1002, 20}, {2563, 20}, {474, 15}, {466, 2}, {316, 2}, {465, 2}, {926, 1} } },
    -- Aga Dou's Staff
    {item=6442, ingredients={ {464, 1}, {316, 1}, {461, 15}, {1002, 10}, {2357, 10}, {2250, 10}, {465, 1} } },
    -- Dark Treestaff
    {item=6454, ingredients={ {470, 10}, {2358, 10}, {1611, 6}, {1612, 6}, {1610, 6}, {466, 2}, {1660, 1} } },
    -- Wobot Staff
    {item=6492, ingredients={ {305, 30}, {372, 10}, {648, 3}, {2288, 2}, {646, 1}, {336, 1} } },
    -- Earlik Branch
    {item=6521, ingredients={ {466, 4}, {918, 1}, {926, 1}, {470, 25}, {2565, 25}, {449, 20}, {315, 4}, {2566, 4} } },
    -- Cinati Root
    {item=6522, ingredients={ {449, 25}, {466, 4}, {315, 4}, {1660, 4}, {920, 4}, {918, 2}, {2564, 30}, {470, 25} } },
    -- The Hagogue Root
    {item=6523, ingredients={ {461, 25}, {474, 25}, {1611, 20}, {1612, 20}, {1610, 20}, {315, 3}, {465, 3}, {2566, 2} } },
    -- Savage Root
    {item=6524, ingredients={ {2358, 10}, {470, 10}, {2357, 10}, {449, 10}, {461, 10}, {464, 2} } },
    -- Feca Staff
    {item=6525, ingredients={ {470, 6}, {465, 1}, {466, 1}, {434, 25}, {435, 25}, {2357, 10}, {461, 10} } },
    -- Bearman's Staff
    {item=6725, ingredients={ {2357, 5}, {2358, 5}, {471, 5}, {6843, 1} } },
    -- Ambusher-up
    {item=6762, ingredients={ {6735, 1}, {2634, 1}, {997, 1}, {1002, 20}, {6737, 8} } },
    -- Farle's Staff
    {item=6769, ingredients={ {926, 1}, {2357, 12}, {2358, 12}, {449, 8}, {6735, 4}, {470, 4}, {1660, 1} } },
    -- Deflower Staff
    {item=6792, ingredients={ {306, 20}, {373, 15}, {309, 15} } },
    -- Root Well
    {item=7176, ingredients={ {7269, 1}, {7265, 20}, {7289, 15}, {7014, 10}, {918, 10}, {7026, 3}, {7291, 1}, {7411, 1} } },
    -- Froot Root
    {item=7178, ingredients={ {7028, 1}, {449, 6}, {7016, 6}, {7013, 6}, {7369, 4}, {470, 4}, {7036, 2} } },
    -- Red Root Chileepaperz
    {item=7181, ingredients={ {7013, 12}, {7016, 10}, {6457, 3}, {7036, 2}, {7289, 2}, {7027, 2}, {918, 1} } },
    -- Black Mel Root
    {item=7182, ingredients={ {7026, 1}, {7016, 10}, {7264, 8}, {7262, 6}, {7263, 6}, {464, 5}, {7271, 1} } },
    -- Lady Root
    {item=7183, ingredients={ {7027, 1}, {461, 12}, {7016, 8}, {7286, 8}, {7013, 6}, {7035, 2}, {7270, 1} } },
    -- Disast Root
    {item=7184, ingredients={ {7026, 1}, {470, 15}, {7013, 12}, {7016, 10}, {7036, 3}, {7261, 2}, {7369, 2} } },
    -- Root 'Een
    {item=7185, ingredients={ {2564, 4}, {7028, 1}, {918, 1}, {7290, 1}, {473, 18}, {7016, 12}, {2357, 12} } },
    -- Thrusty Staff
    {item=7189, ingredients={ {7291, 2}, {200, 1}, {7406, 1}, {7016, 25}, {7014, 20}, {918, 16}, {7017, 8}, {7370, 8} } },
    -- Shodanwa Staff
    {item=7204, ingredients={ {7262, 2}, {7263, 1}, {471, 6}, {2357, 4}, {7013, 2} } },
    -- Nidanwa Staff
    {item=7205, ingredients={ {749, 1}, {747, 1}, {467, 1}, {2357, 4}, {7013, 3}, {185, 1} } },
    -- Sandanwa Staff
    {item=7206, ingredients={ {750, 6}, {544, 4}, {2239, 1}, {7017, 1}, {7272, 1}, {474, 10}, {445, 7} } },
    -- Yondanwa Staff
    {item=7207, ingredients={ {7264, 1}, {918, 1}, {7016, 12}, {7369, 4}, {470, 4}, {7017, 3}, {2417, 1} } },
    -- Akwadala Staff
    {item=7254, ingredients={ {7265, 5}, {7262, 5}, {7264, 4}, {7222, 50}, {7013, 8} } },
    -- Caribbean Staff
    {item=8090, ingredients={ {8157, 1}, {2653, 1}, {2249, 150}, {7263, 30}, {7289, 20}, {7925, 12}, {918, 2} } },
    -- Palm Cane
    {item=8091, ingredients={ {7370, 21}, {2358, 21}, {7925, 20}, {8357, 16}, {8361, 12}, {7017, 6}, {7290, 1}, {6737, 50} } },
    -- Minotot Sceptre
    {item=8275, ingredients={ {8057, 15}, {7404, 10}, {7370, 10}, {8386, 1}, {8274, 1}, {8409, 1}, {8308, 60}, {466, 30} } },
    -- Dreggon Staff
    {item=8297, ingredients={ {8363, 8}, {2250, 25}, {8355, 21}, {8354, 21}, {8352, 21}, {8353, 21}, {7925, 18}, {7014, 10} } },
    -- Shika's Staff
    {item=8300, ingredients={ {8057, 6}, {7925, 20}, {450, 20}, {1612, 12}, {1610, 12}, {6737, 10}, {2516, 10} } },
    -- Studded Staff
    {item=8607, ingredients={ {8995, 1}, {8766, 35}, {8790, 15}, {7925, 15}, {8783, 15}, {766, 10}, {8765, 6} } },
    -- Scrap Staff
    {item=8608, ingredients={ {8761, 1}, {8756, 21}, {8766, 17}, {8750, 16}, {8751, 16}, {8765, 2}, {8796, 2} } },
    -- Rotaflor Stem
    {item=8836, ingredients={ {8798, 1}, {8779, 56}, {471, 18}, {8790, 13}, {8772, 11}, {8786, 2}, {8770, 1} } },
    -- Zoth Master Staff
    {item=8837, ingredients={ {8800, 112}, {8788, 23}, {8804, 18}, {8792, 5}, {8765, 5}, {8764, 3}, {920, 2}, {8789, 1} } },
    -- Tynril Rhizome
    {item=8838, ingredients={ {8795, 1}, {8384, 1}, {8770, 1}, {8916, 1}, {8796, 11}, {929, 5}, {7026, 4}, {8777, 4} } },
    -- Barkritter Root
    {item=8849, ingredients={ {8764, 4}, {8791, 4}, {8781, 43}, {7013, 38}, {8788, 36}, {8756, 29}, {8774, 11}, {8807, 8} } },
    -- Floramor Root
    {item=8850, ingredients={ {8771, 13}, {8807, 6}, {8792, 4}, {8810, 3}, {8777, 3}, {8770, 1}, {8778, 66}, {8832, 26} } },
    -- Mush Mishish Staff
    {item=9136, ingredients={ {9280, 1}, {8785, 35}, {8765, 31}, {8781, 21}, {9263, 11}, {9278, 10}, {9277, 6}, {8365, 2} } },
}

registerCraftSkill(17, sk17Crafts, {jobID = jobID, toolIDs = toolIDs}, ingredientsForCraftJob(jobID), jobID)
