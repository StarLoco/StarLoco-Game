local jobID = ShovelSmithJob
local toolIDs = {496}

-- Craft for Forge a Shovel
local sk21Crafts = {
    -- Small Twiggy Shovel
    {item=150, ingredients={ {312, 2}, {303, 1} } },
    -- Twiggy Shovel
    {item=151, ingredients={ {312, 3}, {303, 1} } },
    -- Great Twiggy Shovel
    {item=152, ingredients={ {312, 4}, {303, 1} } },
    -- Powerful Twiggy Shovel
    {item=153, ingredients={ {312, 5}, {303, 1} } },
    -- Crapouille's Small Shovel
    {item=238, ingredients={ {476, 6}, {441, 2}, {473, 6} } },
    -- Crapouille's Shovel
    {item=239, ingredients={ {473, 6}, {476, 6}, {441, 4} } },
    -- Crapouille Great Shovel
    {item=240, ingredients={ {473, 6}, {476, 6}, {441, 6} } },
    -- Crapouille's Powerful Shovel
    {item=241, ingredients={ {476, 6}, {473, 6}, {441, 8} } },
    -- Small Scratchy Shovel
    {item=244, ingredients={ {476, 6}, {445, 2}, {460, 2} } },
    -- Scratchy Shovel
    {item=245, ingredients={ {460, 4}, {445, 3}, {476, 6} } },
    -- Great Scratchy Shovel
    {item=246, ingredients={ {460, 3}, {476, 6}, {445, 4} } },
    -- Powerful Scratchy Shovel
    {item=247, ingredients={ {476, 5}, {445, 5}, {460, 4} } },
    -- Axel
    {item=250, ingredients={ {449, 10}, {2250, 10}, {313, 10}, {465, 5}, {316, 2}, {446, 20} } },
    -- Graybeard
    {item=251, ingredients={ {461, 3}, {474, 2}, {350, 1}, {444, 1} } },
    -- Small Crackler Shovel
    {item=293, ingredients={ {471, 4}, {445, 2}, {444, 1}, {460, 5} } },
    -- Crackler Shovel
    {item=294, ingredients={ {445, 4}, {444, 1}, {460, 6}, {471, 5} } },
    -- Great Crackler Shovel
    {item=295, ingredients={ {471, 6}, {445, 6}, {444, 1}, {460, 7} } },
    -- Powerful Crackler Shovel
    {item=296, ingredients={ {445, 8}, {460, 8}, {471, 7}, {444, 1} } },
    -- Small Pushn Shovel
    {item=345, ingredients={ {446, 3}, {461, 3}, {445, 2}, {460, 2} } },
    -- Golden Rhizome
    {item=347, ingredients={ {445, 6}, {461, 6}, {313, 5}, {471, 5}, {441, 2} } },
    -- Pushn Shovel
    {item=482, ingredients={ {445, 2}, {461, 4}, {446, 3}, {460, 3} } },
    -- Great Pushn Shovel
    {item=483, ingredients={ {445, 3}, {461, 3}, {446, 4}, {460, 4} } },
    -- Powerful Pushn Shovel
    {item=484, ingredients={ {461, 5}, {460, 5}, {445, 4}, {446, 3} } },
    -- Small Sleeping Shovel
    {item=1042, ingredients={ {472, 2}, {298, 1}, {445, 6}, {350, 6} } },
    -- Sleeping Shovel
    {item=1043, ingredients={ {445, 8}, {350, 7}, {472, 2}, {298, 1} } },
    -- Great Sleeping Shovel
    {item=1044, ingredients={ {445, 10}, {350, 8}, {472, 3}, {298, 1} } },
    -- Magnificent Sleeping Shovel
    {item=1045, ingredients={ {445, 12}, {350, 9}, {472, 3}, {298, 1} } },
    -- Small Mishmashovel
    {item=1046, ingredients={ {471, 6}, {288, 5}, {374, 5}, {309, 5}, {442, 4} } },
    -- Mishmashovel
    {item=1047, ingredients={ {471, 6}, {288, 5}, {442, 4}, {309, 7}, {374, 7} } },
    -- Great Mishmashovel
    {item=1048, ingredients={ {442, 5}, {309, 9}, {374, 9}, {471, 7}, {288, 6} } },
    -- Powerful Mishmashovel
    {item=1049, ingredients={ {374, 11}, {309, 11}, {471, 7}, {288, 7}, {442, 6} } },
    -- Small Woukuis Shovel
    {item=1050, ingredients={ {396, 1}, {367, 10}, {284, 10}, {461, 6}, {446, 4} } },
    -- Woukuis Shovel
    {item=1051, ingredients={ {284, 14}, {367, 13}, {461, 7}, {446, 5}, {396, 2} } },
    -- Great Woukuis Shovel
    {item=1052, ingredients={ {396, 3}, {284, 18}, {367, 16}, {461, 8}, {446, 6} } },
    -- Imposing Woukuis Shovel
    {item=1053, ingredients={ {461, 9}, {446, 7}, {396, 4}, {284, 20}, {367, 18} } },
    -- Cruel Trovel
    {item=1054, ingredients={ {2358, 4}, {474, 4}, {746, 1}, {441, 12}, {443, 10}, {473, 5} } },
    -- Koutoulou's Small Shovel
    {item=1055, ingredients={ {764, 5}, {375, 4}, {305, 3}, {460, 8} } },
    -- Koutoulou's Shovel
    {item=1056, ingredients={ {460, 8}, {68, 5}, {375, 4}, {305, 3} } },
    -- Koutoulou's Great Shovel
    {item=1057, ingredients={ {460, 8}, {69, 5}, {375, 4}, {305, 3} } },
    -- Koutoulou's Frightening Shovel
    {item=1058, ingredients={ {460, 8}, {70, 5}, {375, 4}, {305, 3} } },
    -- Field Shovel
    {item=1059, ingredients={ {303, 6}, {312, 4}, {473, 4} } },
    -- Layte's Slicer
    {item=1426, ingredients={ {444, 10}, {111, 10}, {1335, 5}, {483, 1} } },
    -- Zebuth Shovel
    {item=1427, ingredients={ {473, 12}, {471, 8}, {476, 8}, {441, 5}, {746, 1}, {747, 1} } },
    -- Small Mechba Shovel
    {item=1429, ingredients={ {312, 4}, {474, 2}, {444, 1}, {442, 4} } },
    -- Mechba Shovel
    {item=1430, ingredients={ {444, 2}, {474, 2}, {442, 4}, {312, 4} } },
    -- Great Mechba Shovel
    {item=1431, ingredients={ {312, 2}, {442, 2}, {474, 1}, {444, 1}, {1429, 1} } },
    -- Houze Shovel
    {item=1432, ingredients={ {312, 8}, {444, 8}, {476, 4}, {473, 4} } },
    -- Security Shovel
    {item=1433, ingredients={ {303, 6}, {449, 6}, {747, 1}, {746, 1}, {472, 6}, {460, 6} } },
    -- The Fat Shovel
    {item=1435, ingredients={ {1333, 5}, {1345, 5}, {1343, 5}, {470, 1}, {444, 10}, {350, 10}, {449, 10} } },
    -- Ikan Shovel
    {item=1436, ingredients={ {449, 5}, {467, 1}, {379, 15}, {313, 5} } },
    -- Doudish Shovel
    {item=3356, ingredients={ {473, 10}, {444, 5}, {313, 5}, {476, 5}, {2358, 4}, {747, 1} } },
    -- Hebuse Shovel
    {item=6526, ingredients={ {461, 12}, {474, 12}, {747, 2}, {746, 2}, {6457, 2}, {467, 1}, {465, 1} } },
    -- Uftoon Shovel
    {item=6527, ingredients={ {444, 5}, {446, 5}, {476, 4}, {313, 3}, {746, 2}, {471, 6} } },
    -- Travel Shovel
    {item=6528, ingredients={ {6457, 2}, {6458, 2}, {316, 1}, {1002, 10}, {470, 9}, {750, 3}, {466, 2} } },
    -- Hikule Shovel
    {item=6535, ingredients={ {316, 3}, {315, 3}, {465, 2}, {2250, 20}, {470, 16}, {749, 15}, {750, 10}, {6458, 9} } },
    -- Dark Miner Shovel
    {item=6536, ingredients={ {449, 25}, {750, 10}, {6457, 10}, {6458, 10}, {1660, 5}, {316, 2}, {467, 2}, {480, 1} } },
    -- Mairhe Shovel
    {item=6537, ingredients={ {748, 5}, {750, 4}, {1610, 2}, {465, 2}, {749, 2}, {1002, 25}, {461, 9} } },
    -- Vaidaire Shovel
    {item=6538, ingredients={ {467, 1}, {435, 30}, {434, 30}, {1002, 15}, {461, 15}, {466, 2}, {464, 1} } },
    -- Gicque Shovel
    {item=6539, ingredients={ {474, 10}, {2565, 10}, {746, 5}, {750, 4}, {465, 2}, {1660, 1} } },
    -- Helabete Shovel
    {item=6540, ingredients={ {465, 2}, {315, 2}, {2357, 15}, {449, 15}, {2565, 10}, {750, 7}, {6457, 4} } },
    -- Splitting Shovel
    {item=7213, ingredients={ {7017, 4}, {7026, 3}, {7027, 3}, {7406, 1}, {7014, 15}, {7036, 8}, {7035, 7}, {7028, 4} } },
    -- Alani Shovel
    {item=8417, ingredients={ {750, 6}, {8159, 5}, {7407, 3}, {7369, 22}, {2305, 17}, {7925, 16}, {8057, 11}, {2564, 10} } },
    -- Shovel Ijah
    {item=8419, ingredients={ {8102, 3}, {8058, 3}, {2304, 19}, {7925, 15}, {746, 6}, {466, 5}, {6457, 5} } },
    -- Dreggon Shovel
    {item=8420, ingredients={ {7261, 4}, {7036, 3}, {7411, 2}, {8347, 1}, {2528, 50}, {8083, 40}, {748, 10}, {8351, 4} } },
    -- Shovel Minster
    {item=8613, ingredients={ {8767, 21}, {444, 16}, {476, 14}, {8745, 5}, {8754, 3}, {8994, 1} } },
    -- RIP Shovel
    {item=8614, ingredients={ {1992, 8}, {8748, 4}, {8729, 3}, {8731, 1}, {8752, 24}, {1994, 16} } },
    -- Hickory Shovel
    {item=8935, ingredients={ {8916, 1}, {461, 22}, {8736, 10}, {8737, 8}, {8804, 8}, {929, 5}, {2003, 3}, {8809, 2} } },
    -- Shovel Shattkitou
    {item=8936, ingredients={ {474, 19}, {8776, 5}, {8811, 3}, {316, 3}, {8996, 1}, {8762, 48}, {8763, 29}, {8757, 27} } },
    -- Shovel Egant
    {item=8937, ingredients={ {8760, 35}, {7032, 28}, {470, 14}, {1989, 2}, {699, 1}, {8998, 1}, {2623, 1}, {8740, 68} } },
    -- Shovel Emlaka
    {item=9468, ingredients={ {8802, 8}, {8405, 4}, {8753, 3}, {2640, 1}, {8765, 26}, {8784, 10}, {1674, 10}, {8803, 9} } },
}

registerCraftSkill(21, sk21Crafts, {jobID = jobID, toolIDs = toolIDs}, ingredientsForCraftJob(jobID), jobID)
