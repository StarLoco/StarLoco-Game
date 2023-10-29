local jobID = AxeSmithJob
local toolIDs = {992}

-- Craft for Forge an Axe
local sk65Crafts = {
    -- Lumberjack Axe
    {item=454, ingredients={ {303, 2}, {312, 1} } },
    -- Oak Killer
    {item=456, ingredients={ {449, 4}, {350, 2}, {446, 2}, {472, 2}, {474, 4} } },
    -- Charm Pruner
    {item=478, ingredients={ {461, 9}, {444, 5}, {313, 4}, {449, 4}, {472, 4} } },
    -- Yew Axe
    {item=502, ingredients={ {476, 3}, {460, 3}, {443, 5} } },
    -- Boulgourde of the Glades
    {item=515, ingredients={ {449, 5}, {474, 5}, {470, 5}, {472, 5}, {463, 1}, {313, 10} } },
    -- Chestnut Tree Eater
    {item=673, ingredients={ {312, 5}, {303, 3} } },
    -- Cherry Splitter
    {item=674, ingredients={ {471, 9}, {444, 4}, {460, 4}, {445, 4}, {461, 4} } },
    -- Walnut Cutter
    {item=675, ingredients={ {476, 4}, {473, 4}, {460, 7}, {442, 5} } },
    -- Maple Tree Eater
    {item=676, ingredients={ {460, 10}, {303, 5}, {443, 4}, {476, 4} } },
    -- Ash Tree Eater
    {item=771, ingredients={ {303, 4}, {312, 4} } },
    -- Agride
    {item=782, ingredients={ {473, 15}, {303, 13}, {441, 10}, {460, 3} } },
    -- Treechnid Splitter
    {item=923, ingredients={ {446, 10}, {313, 5}, {449, 5}, {472, 5}, {474, 5}, {316, 1}, {926, 1} } },
    -- Ancestral Treechnid Splitter
    {item=927, ingredients={ {472, 6}, {920, 5}, {313, 5}, {464, 5}, {466, 2}, {470, 10}, {446, 10}, {434, 6} } },
    -- Terophyle Axe
    {item=1375, ingredients={ {460, 3}, {443, 6}, {446, 3}, {476, 3} } },
    -- Huvant Axe
    {item=1376, ingredients={ {350, 8}, {449, 6}, {472, 3}, {446, 2} } },
    -- Samuel J. Axe
    {item=1377, ingredients={ {445, 9}, {460, 8}, {471, 5}, {444, 3}, {461, 1} } },
    -- Jon Lemon
    {item=1378, ingredients={ {441, 20}, {303, 10}, {473, 10}, {472, 2} } },
    -- Lumberjack Hatchet
    {item=2587, ingredients={ {473, 3}, {312, 1} } },
    -- Hevlalav Hatchet
    {item=2589, ingredients={ {441, 3}, {476, 1} } },
    -- Vôr'Om Axe
    {item=2590, ingredients={ {312, 5}, {476, 3}, {442, 1} } },
    -- Bombu Cutter
    {item=2592, ingredients={ {2358, 3}, {449, 2}, {350, 1}, {476, 1}, {442, 5} } },
    -- Oliviolet Pruner
    {item=2593, ingredients={ {444, 3}, {2357, 3}, {461, 2}, {443, 2}, {350, 1} } },
    -- Stone Axe
    {item=2595, ingredients={ {450, 10}, {2304, 10}, {474, 8}, {431, 7}, {471, 6}, {2305, 4} } },
    -- Crack Axe
    {item=2597, ingredients={ {449, 3}, {2357, 3}, {461, 3}, {2634, 1}, {442, 5}, {312, 5} } },
    -- Whistle Cutter
    {item=2600, ingredients={ {2357, 5}, {446, 4}, {313, 4}, {470, 1}, {578, 1}, {545, 1} } },
    -- Darsson's Axe
    {item=2601, ingredients={ {445, 3}, {350, 5}, {474, 5}, {2357, 5}, {449, 5}, {443, 5} } },
    -- Limb Chopper
    {item=2603, ingredients={ {313, 10}, {472, 10}, {2358, 6}, {441, 5}, {446, 5}, {479, 2} } },
    -- Militiaman Bardiche
    {item=2604, ingredients={ {461, 10}, {313, 10}, {449, 8}, {470, 2}, {466, 1}, {2484, 1} } },
    -- Clever Hatchet
    {item=2606, ingredients={ {1535, 1}, {312, 12}, {460, 10}, {473, 10}, {470, 10}, {544, 1}, {465, 1} } },
    -- Pole Axe
    {item=2608, ingredients={ {2634, 1}, {350, 10}, {7013, 5}, {461, 5}, {2357, 5}, {470, 5}, {466, 1} } },
    -- Pesc Axe
    {item=2612, ingredients={ {2484, 1}, {465, 1}, {2357, 10}, {461, 10}, {445, 10}, {446, 10}, {602, 5} } },
    -- Dame Zel Axe
    {item=2614, ingredients={ {2357, 10}, {470, 10}, {467, 1}, {350, 10}, {449, 10}, {474, 10}, {7013, 10} } },
    -- Lookabeer Axe
    {item=2615, ingredients={ {471, 5}, {2357, 5}, {441, 5}, {313, 10}, {470, 10}, {442, 10}, {7013, 5} } },
    -- Bards' Hall
    {item=2616, ingredients={ {467, 1}, {315, 1}, {445, 10}, {2357, 10}, {2358, 10}, {920, 1}, {465, 1} } },
    -- Arachnee Cutter
    {item=6914, ingredients={ {365, 5}, {447, 1} } },
    -- Shodanwa Axe
    {item=7208, ingredients={ {7013, 5}, {7263, 3}, {7286, 2}, {7264, 2}, {747, 1} } },
    -- Nidanwa Axe
    {item=7209, ingredients={ {7286, 10}, {7262, 3}, {7035, 1}, {7036, 1}, {7013, 15}, {7016, 10} } },
    -- Sandanwa Axe
    {item=7210, ingredients={ {7036, 5}, {7028, 4}, {7261, 4}, {7016, 15}, {7264, 6}, {7035, 6}, {7263, 5} } },
    -- Yondanwa Axe
    {item=7211, ingredients={ {7014, 7}, {7035, 6}, {7036, 5}, {7028, 3}, {7017, 3}, {7026, 1}, {7013, 17} } },
    -- Bamboo Slayer
    {item=7212, ingredients={ {303, 15}, {7013, 4}, {749, 1}, {747, 1} } },
    -- Axe Terrdala
    {item=7255, ingredients={ {7035, 2}, {7028, 1}, {7017, 1}, {7224, 50}, {7013, 20}, {7016, 15}, {7036, 2} } },
    -- Axe of Lies
    {item=8099, ingredients={ {8055, 9}, {7036, 8}, {7026, 1}, {7411, 1}, {2563, 20}, {2357, 15}, {7925, 11} } },
    -- The Warf Axe
    {item=8100, ingredients={ {7370, 10}, {467, 8}, {8056, 3}, {7410, 1}, {749, 11}, {7925, 11}, {7035, 10} } },
    -- Kape Axe
    {item=8101, ingredients={ {7036, 5}, {918, 4}, {8058, 3}, {7270, 1}, {2357, 13}, {7925, 12}, {2304, 11}, {463, 10} } },
    -- Boowolf Axe
    {item=8130, ingredients={ {439, 19}, {440, 15}, {291, 5}, {2579, 4}, {2578, 3}, {8396, 1} } },
    -- Minotoror Axe
    {item=8274, ingredients={ {8083, 13}, {8057, 12}, {750, 11}, {8327, 1}, {434, 25}, {1610, 24}, {7925, 16} } },
    -- Dreggon Axe
    {item=8293, ingredients={ {2564, 3}, {8386, 1}, {8346, 1}, {2487, 21}, {7925, 15}, {466, 9}, {8361, 6}, {8138, 5} } },
    -- Purrin Axe
    {item=8617, ingredients={ {8751, 4}, {8752, 4}, {8750, 4}, {8731, 1}, {2637, 1}, {8749, 4} } },
    -- Koss Axe
    {item=8618, ingredients={ {2357, 15}, {746, 6}, {8765, 3}, {8761, 1}, {8737, 1}, {3001, 20} } },
    -- Zoth Warrior Axe
    {item=8827, ingredients={ {1557, 1}, {8806, 1}, {8780, 37}, {8800, 28}, {8832, 18}, {8802, 5}, {8773, 3}, {8770, 1} } },
    -- Cheeken Axe
    {item=8868, ingredients={ {8764, 2}, {8997, 1}, {8787, 56}, {476, 32}, {8783, 18}, {2416, 17}, {8786, 9}, {8776, 4} } },
    -- Sick Axe
    {item=8933, ingredients={ {8782, 39}, {445, 26}, {750, 18}, {8784, 8}, {8769, 5}, {8764, 4}, {2007, 2}, {2614, 1} } },
    -- Michael Dougle Axe
    {item=8934, ingredients={ {7289, 12}, {8777, 2}, {8798, 1}, {2527, 1}, {8797, 65}, {8788, 49}, {394, 35}, {7032, 15} } },
    -- Canni Blade
    {item=9138, ingredients={ {8797, 10}, {8868, 1}, {2623, 1}, {8762, 160}, {8765, 36}, {8739, 33}, {9267, 15}, {1674, 13} } },
}

local requirements = {jobID = jobID, toolIDs = toolIDs}
registerCraftSkill(65, sk65Crafts, requirements, ingredientsForCraftJob(jobID), jobID)
