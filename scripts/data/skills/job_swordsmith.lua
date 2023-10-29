local jobID = SwordSmithJob
local toolIDs = {494}

-- Craft for Forge a Sword
local sk20Crafts = {
    -- Small Twiggy Sword
    {item=40, ingredients={ {303, 2}, {473, 2} } },
    -- Twiggy Sword
    {item=42, ingredients={ {473, 3}, {303, 2} } },
    -- Great Twiggy Sword
    {item=43, ingredients={ {473, 4}, {303, 2} } },
    -- Powerful Twiggy Sword
    {item=44, ingredients={ {473, 5}, {303, 2} } },
    -- Small Smithy Sword
    {item=46, ingredients={ {312, 5}, {473, 5}, {441, 3} } },
    -- Smithy Sword
    {item=47, ingredients={ {476, 5}, {441, 4}, {473, 5} } },
    -- Great Smithy Sword
    {item=48, ingredients={ {312, 5}, {473, 5}, {441, 5} } },
    -- Powerful Smithy Sword
    {item=49, ingredients={ {441, 6}, {312, 5}, {473, 5} } },
    -- Small Fwell Sword
    {item=51, ingredients={ {446, 3}, {444, 1}, {441, 4}, {442, 3} } },
    -- Fwell Sword
    {item=53, ingredients={ {442, 5}, {441, 4}, {446, 3}, {350, 1} } },
    -- Great Fwell Sword
    {item=54, ingredients={ {444, 1}, {442, 5}, {441, 4}, {446, 4} } },
    -- Powerful Fwell Sword
    {item=55, ingredients={ {446, 5}, {441, 4}, {442, 3}, {444, 1} } },
    -- The Sad Blade
    {item=58, ingredients={ {444, 1}, {350, 5}, {442, 4}, {441, 2} } },
    -- Small Ha Sword
    {item=59, ingredients={ {443, 4}, {442, 4}, {444, 1} } },
    -- Ha Sword
    {item=60, ingredients={ {443, 5}, {442, 4}, {444, 1} } },
    -- Great Ha Sword
    {item=61, ingredients={ {443, 5}, {442, 4}, {444, 2} } },
    -- Powerful Ha Sword
    {item=62, ingredients={ {442, 6}, {443, 5}, {444, 2} } },
    -- Goultard
    {item=65, ingredients={ {463, 5}, {315, 2}, {466, 2}, {313, 1}, {477, 1} } },
    -- Badoul's Mane
    {item=66, ingredients={ {474, 10}, {350, 6}, {312, 5}, {442, 5}, {466, 5} } },
    -- Infernal Sharp
    {item=67, ingredients={ {446, 3}, {350, 1}, {476, 5}, {441, 3} } },
    -- Raziel
    {item=202, ingredients={ {316, 2}, {465, 2}, {441, 4}, {466, 2}, {446, 2}, {315, 2} } },
    -- Karne Rider Blade
    {item=327, ingredients={ {443, 5}, {471, 4}, {441, 5} } },
    -- Slash
    {item=328, ingredients={ {445, 3}, {350, 1}, {312, 6}, {442, 5} } },
    -- Little Bwork Blade
    {item=338, ingredients={ {445, 8}, {442, 4}, {350, 1}, {474, 1} } },
    -- Chafer Blade
    {item=339, ingredients={ {446, 5}, {441, 2}, {350, 2}, {442, 2} } },
    -- Small Holy Sword
    {item=352, ingredients={ {441, 3}, {473, 1}, {444, 5} } },
    -- Little Knight Sword
    {item=353, ingredients={ {441, 4}, {350, 4}, {444, 2}, {449, 1} } },
    -- The Klebik
    {item=354, ingredients={ {441, 9}, {442, 5}, {446, 3}, {444, 2} } },
    -- The Tail
    {item=357, ingredients={ {312, 3}, {445, 2}, {444, 3} } },
    -- The Infernal Tail
    {item=358, ingredients={ {444, 9}, {313, 6}, {442, 10}, {445, 9} } },
    -- Kralove Cutting Sword
    {item=620, ingredients={ {379, 20}, {2302, 20}, {748, 10}, {749, 10}, {2303, 1}, {2583, 20} } },
    -- Holy Sword
    {item=819, ingredients={ {444, 6}, {441, 3}, {473, 1} } },
    -- Great Holy Sword
    {item=820, ingredients={ {444, 7}, {441, 3}, {473, 1} } },
    -- Powerful Holy Sword
    {item=821, ingredients={ {444, 8}, {441, 3}, {473, 1} } },
    -- Knight Sword
    {item=822, ingredients={ {441, 4}, {350, 4}, {449, 2}, {444, 2} } },
    -- Great Knight Sword
    {item=823, ingredients={ {441, 4}, {350, 4}, {449, 3}, {444, 2} } },
    -- Powerful Knight Sword
    {item=824, ingredients={ {441, 4}, {449, 4}, {350, 4}, {444, 2} } },
    -- Bwork Blade
    {item=825, ingredients={ {350, 1}, {445, 8}, {442, 4}, {474, 2} } },
    -- Great Bwork Blade
    {item=826, ingredients={ {442, 4}, {474, 3}, {350, 1}, {445, 8} } },
    -- Powerful Bwork Blade
    {item=827, ingredients={ {445, 8}, {442, 4}, {474, 4}, {350, 1} } },
    -- Ogralimde's Sword
    {item=928, ingredients={ {441, 6}, {350, 5}, {474, 1}, {60, 1}, {312, 8} } },
    -- Small Evening Star
    {item=1636, ingredients={ {441, 10}, {461, 10}, {313, 10}, {472, 10}, {545, 1} } },
    -- Evening Star
    {item=1637, ingredients={ {461, 17}, {313, 17}, {441, 17}, {472, 17}, {545, 1} } },
    -- Bright Evening Star
    {item=1638, ingredients={ {461, 20}, {472, 20}, {441, 20}, {313, 20}, {545, 1} } },
    -- Immortal Evening Star
    {item=1639, ingredients={ {461, 25}, {545, 1}, {313, 25}, {441, 25}, {472, 25} } },
    -- Small Craft Knife
    {item=1640, ingredients={ {546, 2}, {446, 13}, {445, 13}, {350, 13}, {2357, 13} } },
    -- Craft Knife
    {item=1641, ingredients={ {350, 15}, {445, 15}, {446, 15}, {2357, 15}, {546, 2} } },
    -- Great Craft Knife
    {item=1650, ingredients={ {445, 20}, {350, 20}, {446, 20}, {2357, 20}, {466, 2} } },
    -- Bloody Craft Knife
    {item=1651, ingredients={ {2357, 20}, {313, 20}, {445, 20}, {446, 20}, {466, 2} } },
    -- Fire Kwakblade
    {item=2415, ingredients={ {460, 4}, {313, 3}, {446, 2}, {2648, 2}, {412, 2} } },
    -- Royal Gobball Sword
    {item=2440, ingredients={ {383, 2}, {2465, 2}, {2416, 50}, {2460, 25}, {2453, 15}, {2463, 5} } },
    -- Croblade
    {item=2544, ingredients={ {1889, 20}, {2463, 2}, {2056, 1}, {2179, 100}, {2060, 30} } },
    -- The Claw
    {item=2637, ingredients={ {2358, 2}, {2516, 2}, {2323, 2}, {350, 10}, {2648, 5} } },
    -- The Sharp Claw
    {item=2638, ingredients={ {466, 2}, {2528, 5}, {2358, 5}, {350, 5}, {2316, 5}, {2490, 5} } },
    -- Fake Ceangal Claw
    {item=2639, ingredients={ {2358, 10}, {2598, 5}, {2528, 5}, {316, 2}, {2494, 1}, {2664, 1}, {350, 10} } },
    -- Pink Claw
    {item=4241, ingredients={ {313, 30}, {2357, 5}, {316, 2}, {467, 2}, {2494, 1}, {2488, 1}, {2294, 1} } },
    -- Slicing Fan
    {item=4242, ingredients={ {2503, 1}, {313, 30}, {470, 20}, {465, 10}, {2624, 5}, {2574, 1} } },
    -- Tyse Pick
    {item=4382, ingredients={ {2508, 1}, {1536, 1}, {444, 5}, {414, 5}, {2291, 5}, {446, 5}, {2318, 2} } },
    -- Ice Kwakblade
    {item=6750, ingredients={ {460, 4}, {313, 3}, {446, 2}, {2648, 2}, {411, 2} } },
    -- Earth Kwakblade
    {item=6751, ingredients={ {460, 4}, {313, 3}, {2648, 2}, {446, 2}, {1142, 2} } },
    -- Wind Kwakblade
    {item=6752, ingredients={ {446, 2}, {2648, 2}, {460, 4}, {313, 3}, {413, 2} } },
    -- Chief Crocodyl Blade
    {item=6821, ingredients={ {467, 2}, {746, 1}, {2295, 1}, {749, 1}, {747, 3}, {6458, 2} } },
    -- Crackler Blade
    {item=6957, ingredients={ {546, 1}, {2252, 22}, {448, 16}, {450, 10}, {2306, 1} } },
    -- Ice Knight Sword
    {item=7102, ingredients={ {442, 1}, {441, 1}, {303, 1}, {473, 1}, {476, 1}, {460, 1}, {312, 1}, {444, 1} } },
    -- Grizmine Claw
    {item=7192, ingredients={ {2581, 5}, {7035, 4}, {2279, 3}, {7027, 2}, {7036, 2}, {7026, 1}, {7274, 9} } },
    -- Dark Vlad Sword
    {item=7193, ingredients={ {7405, 1}, {470, 30}, {918, 10}, {1677, 6}, {7370, 5}, {7036, 5}, {7035, 3}, {1679, 1} } },
    -- Furritung
    {item=7195, ingredients={ {7291, 3}, {7406, 1}, {7014, 15}, {7035, 9}, {7036, 9}, {7026, 5}, {7027, 5}, {7269, 4} } },
    -- Evening Razor
    {item=7198, ingredients={ {7026, 1}, {7289, 6}, {7036, 6}, {748, 5}, {7035, 4}, {750, 3}, {7028, 2} } },
    -- Abatz Sword
    {item=7199, ingredients={ {7014, 12}, {7036, 7}, {918, 6}, {7035, 6}, {7026, 4}, {7027, 3}, {7028, 3}, {7407, 1} } },
    -- Shodanwa Sabre
    {item=7200, ingredients={ {7369, 4}, {747, 1}, {7036, 1}, {7013, 8}, {1002, 5} } },
    -- Nidanwa Sabre
    {item=7201, ingredients={ {7369, 6}, {7016, 4}, {6457, 3}, {7035, 2}, {7036, 2}, {7027, 1} } },
    -- Sandanwa Sabre
    {item=7202, ingredients={ {747, 2}, {918, 1}, {7036, 1}, {7261, 6}, {750, 4}, {7027, 2}, {7035, 2} } },
    -- Yondanwa Sabre
    {item=7203, ingredients={ {6458, 3}, {7036, 3}, {7027, 2}, {7028, 2}, {747, 10}, {7016, 10}, {7035, 3} } },
    -- Feudala Sabre
    {item=7257, ingredients={ {7225, 50}, {7036, 3}, {7035, 3}, {7028, 1}, {7027, 1}, {7026, 1} } },
    -- Killarity Sword
    {item=8094, ingredients={ {8056, 21}, {2357, 21}, {7925, 18}, {7036, 12}, {7410, 6}, {2566, 4}, {8386, 2}, {8058, 25} } },
    -- Andy War Sword
    {item=8095, ingredients={ {2488, 2}, {7014, 17}, {7925, 16}, {8361, 12}, {2305, 11}, {750, 11}, {7036, 10}, {7403, 4} } },
    -- Dreggon Sword
    {item=8292, ingredients={ {8344, 1}, {8350, 13}, {8348, 13}, {8349, 13}, {8351, 13}, {8363, 3}, {8361, 3}, {8137, 1} } },
    -- White Rat Sword
    {item=8450, ingredients={ {66, 1}, {305, 40}, {439, 40}, {2463, 40}, {7036, 6}, {8489, 5}, {749, 4} } },
    -- Crimson Claw
    {item=8596, ingredients={ {7302, 9}, {8808, 7}, {8777, 3}, {1696, 2}, {8798, 1}, {6898, 100}, {2271, 100}, {7295, 15} } },
    -- Stalk Sword
    {item=8604, ingredients={ {7033, 15}, {8832, 10}, {8324, 7}, {8764, 5}, {8791, 2}, {8786, 1}, {8761, 1} } },
    -- Rubber Sword
    {item=8605, ingredients={ {8739, 4}, {397, 3}, {8745, 2}, {8746, 2}, {8748, 2}, {8747, 2} } },
    -- Sword Hikk
    {item=8695, ingredients={ {8798, 1}, {8404, 1}, {1558, 1}, {6904, 1}, {8778, 55}, {8792, 5}, {8804, 1}, {8998, 1} } },
    -- Fuchsia
    {item=8930, ingredients={ {2294, 2}, {6662, 1}, {8144, 1}, {8770, 1}, {8387, 1}, {8779, 59}, {8797, 47}, {8805, 11} } },
    -- Az'tech
    {item=8931, ingredients={ {8795, 2}, {8998, 1}, {8996, 1}, {8381, 1}, {745, 100}, {8790, 60}, {8736, 21}, {8793, 9} } },
    -- Kukri Kura
    {item=8932, ingredients={ {6457, 16}, {8803, 13}, {8403, 8}, {8789, 1}, {2623, 1}, {8801, 98}, {8763, 69}, {8788, 68} } },
    -- Blord Warrior's Cursed Sword
    {item=8993, ingredients={ {7102, 1}, {8094, 1}, {202, 1}, {65, 1}, {4241, 1}, {7192, 1}, {7195, 1}, {2639, 1} } },
    -- Sword Indinz
    {item=9469, ingredients={ {8769, 20}, {9269, 14}, {8761, 5}, {2035, 5}, {8410, 3}, {8916, 1}, {8680, 100}, {8774, 31} } },
}

registerCraftSkill(20, sk20Crafts, {jobID = jobID, toolIDs = toolIDs}, ingredientsForCraftJob(jobID), jobID)
