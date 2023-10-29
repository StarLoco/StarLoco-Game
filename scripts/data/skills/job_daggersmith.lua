local jobID = DaggerSmithJob
local toolIDs = {495}

-- Craft for Forge a Dagger
local sk18Crafts = {
    -- Small Twiggy Daggers
    {item=94, ingredients={ {473, 3}, {303, 1} } },
    -- Twiggy Daggers
    {item=95, ingredients={ {473, 4}, {303, 1} } },
    -- Great Twiggy Daggers
    {item=96, ingredients={ {303, 1}, {473, 5} } },
    -- Powerful Twiggy Daggers
    {item=97, ingredients={ {473, 6}, {303, 1} } },
    -- Small Smithy Daggers
    {item=205, ingredients={ {443, 1}, {312, 4}, {444, 2} } },
    -- Smithy Daggers
    {item=206, ingredients={ {312, 5}, {444, 2}, {443, 1} } },
    -- Great Smithy Daggers
    {item=207, ingredients={ {312, 4}, {444, 2}, {443, 2} } },
    -- Powerful Smithy Daggers
    {item=208, ingredients={ {444, 3}, {312, 3}, {443, 2} } },
    -- Small Croclage Daggers
    {item=211, ingredients={ {312, 5}, {473, 3}, {445, 1} } },
    -- Croclage Daggers
    {item=212, ingredients={ {312, 5}, {473, 4}, {445, 2} } },
    -- Great Croclage Daggers
    {item=213, ingredients={ {473, 5}, {312, 5}, {445, 3} } },
    -- Impressive Croclage Daggers
    {item=214, ingredients={ {312, 6}, {473, 5}, {445, 4} } },
    -- Bashers
    {item=218, ingredients={ {350, 4}, {449, 4}, {445, 4}, {444, 5} } },
    -- Blessdags
    {item=219, ingredients={ {449, 5}, {463, 5}, {474, 4}, {477, 1}, {446, 10}, {313, 10} } },
    -- Chakra Style
    {item=220, ingredients={ {313, 10}, {446, 10}, {472, 10}, {463, 5}, {315, 2}, {449, 10} } },
    -- Leurnettes
    {item=340, ingredients={ {445, 4}, {476, 4} } },
    -- Eurfolles Daggers
    {item=341, ingredients={ {449, 9}, {446, 8}, {463, 4}, {465, 2}, {474, 1} } },
    -- Setter
    {item=491, ingredients={ {312, 1}, {303, 2} } },
    -- Staff Slasher
    {item=498, ingredients={ {312, 2}, {303, 1} } },
    -- Wand Slasher
    {item=499, ingredients={ {441, 1}, {303, 2} } },
    -- Bow Slasher
    {item=500, ingredients={ {441, 2}, {303, 1} } },
    -- Leather Cutter
    {item=579, ingredients={ {312, 2}, {476, 1} } },
    -- Small Sylvan Daggers
    {item=893, ingredients={ {471, 3}, {444, 2}, {461, 2}, {350, 2} } },
    -- Sylvan Daggers
    {item=894, ingredients={ {471, 4}, {461, 3}, {350, 2}, {444, 2} } },
    -- Great Sylvan Daggers
    {item=895, ingredients={ {471, 5}, {461, 4}, {444, 2}, {350, 2} } },
    -- Powerful Sylvan Daggers
    {item=896, ingredients={ {471, 6}, {461, 5}, {444, 2}, {350, 2} } },
    -- Small Dagguise
    {item=897, ingredients={ {449, 3}, {350, 3}, {461, 3}, {445, 2}, {474, 3} } },
    -- Dagguise
    {item=898, ingredients={ {449, 4}, {474, 4}, {445, 3}, {461, 3}, {350, 3} } },
    -- Great Dagguise
    {item=899, ingredients={ {350, 3}, {461, 3}, {474, 5}, {449, 5}, {445, 4} } },
    -- Powerful Dagguise
    {item=900, ingredients={ {445, 5}, {461, 3}, {350, 3}, {474, 6}, {449, 6} } },
    -- Ancestral Daggers
    {item=919, ingredients={ {472, 1}, {449, 1}, {918, 1}, {313, 4}, {474, 1} } },
    -- Small Stek Knife
    {item=1026, ingredients={ {442, 4}, {460, 8}, {471, 8}, {461, 5}, {444, 4} } },
    -- Stek Knife
    {item=1027, ingredients={ {471, 8}, {460, 8}, {461, 6}, {442, 5}, {444, 5} } },
    -- Great Stek Knife
    {item=1028, ingredients={ {460, 8}, {471, 8}, {461, 7}, {442, 6}, {444, 6} } },
    -- Powerful Stek Knife
    {item=1029, ingredients={ {460, 8}, {444, 7}, {442, 7}, {461, 8}, {471, 8} } },
    -- Kitten Tails
    {item=1030, ingredients={ {2358, 9}, {2357, 9}, {441, 10}, {446, 10} } },
    -- Elorie Entuwan's Cruel Daggers
    {item=1031, ingredients={ {303, 5}, {471, 5}, {312, 5}, {446, 4}, {444, 4} } },
    -- Small Rowler Blade
    {item=1032, ingredients={ {461, 6}, {350, 5}, {445, 5}, {446, 4}, {474, 6} } },
    -- Rowler Blade
    {item=1033, ingredients={ {445, 6}, {446, 5}, {350, 6}, {474, 6}, {461, 6} } },
    -- Great Rowler Blade
    {item=1034, ingredients={ {350, 7}, {445, 7}, {461, 6}, {446, 6}, {474, 6} } },
    -- Powerful Rowler Blade
    {item=1035, ingredients={ {446, 7}, {474, 6}, {461, 6}, {445, 8}, {350, 8} } },
    -- Small Deceitful Dagger
    {item=1036, ingredients={ {449, 6}, {444, 6}, {313, 5}, {430, 4}, {472, 3}, {543, 1} } },
    -- Deceitful Dagger
    {item=1037, ingredients={ {313, 6}, {430, 5}, {472, 4}, {543, 1}, {444, 7}, {449, 6} } },
    -- Beautiful Deceitful Dagger
    {item=1038, ingredients={ {430, 6}, {449, 6}, {472, 5}, {543, 1}, {444, 8}, {313, 7} } },
    -- Hypnotic Deceitful Dagger
    {item=1039, ingredients={ {444, 9}, {430, 7}, {313, 7}, {449, 6}, {472, 6}, {543, 1} } },
    -- Ortimus Contrari's Bloody Blades
    {item=1040, ingredients={ {750, 2}, {6458, 2}, {1660, 1}, {466, 1}, {461, 12}, {474, 10}, {473, 10} } },
    -- Unlucky Knight's Broken Sword
    {item=1041, ingredients={ {471, 5}, {443, 4}, {446, 4}, {441, 3}, {444, 3} } },
    -- Table Knives
    {item=1369, ingredients={ {446, 4}, {441, 4}, {443, 4}, {476, 3}, {303, 5} } },
    -- Ergot Mina
    {item=1370, ingredients={ {474, 5}, {750, 2}, {6458, 2}, {466, 1} } },
    -- Eulasse Daggers
    {item=1371, ingredients={ {446, 4}, {473, 4}, {303, 4}, {460, 4}, {443, 4} } },
    -- Otof'Mai'We Daggers
    {item=1372, ingredients={ {746, 1}, {472, 6}, {461, 6}, {464, 4}, {2250, 2} } },
    -- The Infernal Razor
    {item=1373, ingredients={ {442, 6}, {443, 5}, {472, 5}, {471, 5}, {441, 4} } },
    -- Tylo Daggers
    {item=1374, ingredients={ {473, 5}, {303, 5}, {312, 4}, {443, 4}, {441, 4} } },
    -- Billy-Ray's Daggers
    {item=1504, ingredients={ {473, 5}, {446, 5}, {441, 4}, {476, 4}, {443, 4} } },
    -- Magic Bow Slasher
    {item=1563, ingredients={ {312, 10}, {463, 1} } },
    -- Magic Wand Slasher
    {item=1564, ingredients={ {444, 5}, {463, 1} } },
    -- Magic Staff Slasher
    {item=1565, ingredients={ {442, 8}, {463, 1} } },
    -- Dagger O'Hair
    {item=3647, ingredients={ {466, 2}, {750, 2}, {6457, 2}, {6458, 2}, {470, 10}, {2358, 10}, {479, 10} } },
    -- Captain Chafer's Small Daggers
    {item=3648, ingredients={ {2357, 10}, {461, 10}, {1612, 5}, {1610, 5}, {6457, 3}, {749, 3}, {316, 2} } },
    -- Daguiero's Daggers
    {item=3649, ingredients={ {1660, 2}, {316, 1}, {449, 15}, {1610, 10}, {750, 4}, {6457, 3}, {467, 2} } },
    -- Regah Daggers
    {item=3650, ingredients={ {479, 50}, {470, 20}, {6458, 9}, {6457, 9}, {750, 9}, {316, 3}, {926, 2}, {2643, 2} } },
    -- Lutination Daggers
    {item=3651, ingredients={ {472, 15}, {2250, 10}, {748, 4}, {750, 3}, {6458, 2}, {2566, 1}, {465, 1} } },
    -- Dagger Manic
    {item=6516, ingredients={ {1610, 20}, {1612, 20}, {749, 8}, {6457, 8}, {1660, 8}, {6458, 8}, {466, 4}, {449, 20} } },
    -- Hischantes Daggers
    {item=6517, ingredients={ {449, 4}, {748, 4}, {467, 1}, {474, 10}, {461, 10}, {746, 5}, {6458, 4} } },
    -- Robber Daggers
    {item=6924, ingredients={ {205, 8}, {211, 8}, {94, 8} } },
    -- Ice Daggers
    {item=6981, ingredients={ {7035, 5}, {7291, 3}, {7406, 1}, {7014, 19}, {7289, 7}, {7036, 6}, {7027, 6}, {749, 5} } },
    -- Dagg'Onies
    {item=6982, ingredients={ {473, 8}, {1002, 6}, {472, 3}, {7013, 3}, {7369, 1} } },
    -- Dagg'Ressif
    {item=6983, ingredients={ {7291, 2}, {7404, 1}, {918, 8}, {7035, 6}, {7370, 5}, {7027, 5}, {7036, 5}, {6458, 4} } },
    -- Gobbly Killer Apprentice Daggers
    {item=6984, ingredients={ {7035, 3}, {7026, 2}, {7369, 2}, {7016, 12}, {750, 6}, {7036, 4}, {747, 3} } },
    -- Citrus Daggers
    {item=7188, ingredients={ {7261, 3}, {7027, 2}, {7016, 10}, {7035, 5}, {6457, 4}, {7036, 3}, {7369, 3} } },
    -- Maydhyn China Daggers
    {item=7190, ingredients={ {449, 12}, {7016, 6}, {7013, 6}, {750, 3}, {7036, 2}, {7028, 2}, {748, 2} } },
    -- Ramougre's Setter
    {item=7191, ingredients={ {749, 3}, {7027, 2}, {7028, 2}, {7403, 1}, {7370, 1}, {7036, 4}, {6458, 3}, {7014, 3} } },
    -- Aerdala Daggers
    {item=7256, ingredients={ {7264, 10}, {7262, 10}, {7036, 3}, {7035, 1}, {7026, 1}, {7223, 50} } },
    -- Jewelmagus' Setter
    {item=7493, ingredients={ {431, 1}, {303, 18} } },
    -- Shoemagus Leather Cutter
    {item=7495, ingredients={ {460, 10}, {431, 1} } },
    -- Ostwogoth Daggers
    {item=8092, ingredients={ {7035, 11}, {7411, 2}, {7405, 2}, {8344, 1}, {8346, 1}, {8083, 90}, {1610, 23}, {6457, 12} } },
    -- Dreggon Daggers
    {item=8414, ingredients={ {7410, 2}, {8345, 1}, {433, 25}, {8350, 12}, {8057, 6}, {7289, 6}, {749, 5}, {7035, 3} } },
    -- Black Rat Daggers
    {item=8444, ingredients={ {746, 5}, {8488, 4}, {467, 4}, {305, 45}, {2322, 45}, {439, 15}, {748, 6} } },
    -- Emment Daggers
    {item=8598, ingredients={ {2596, 10}, {8765, 1}, {1771, 1}, {2266, 1}, {8754, 1}, {8756, 20} } },
    -- Clawettes
    {item=8599, ingredients={ {8328, 1}, {8680, 20}, {8763, 15}, {8012, 2}, {8758, 2}, {8681, 2} } },
    -- Dagg'Adou
    {item=8926, ingredients={ {8076, 4}, {8795, 3}, {8789, 1}, {8404, 1}, {8756, 113}, {8783, 82}, {8786, 11}, {8803, 10} } },
    -- Gaga Daggers
    {item=8927, ingredients={ {8800, 45}, {8787, 43}, {8811, 8}, {8806, 7}, {2527, 1}, {8998, 1}, {7433, 150}, {8788, 62} } },
    -- Porkeez Daggers
    {item=8928, ingredients={ {7033, 23}, {8776, 6}, {2620, 1}, {8387, 1}, {2633, 1}, {8795, 1}, {2632, 90}, {8801, 59} } },
    -- Kriss Tobal
    {item=8929, ingredients={ {8384, 1}, {8996, 1}, {8787, 38}, {8783, 38}, {7032, 28}, {7434, 26}, {8776, 4}, {1679, 2} } },
    -- Mush Cuteers
    {item=9137, ingredients={ {8365, 2}, {9280, 2}, {8782, 50}, {8802, 20}, {9279, 15}, {9263, 12}, {9277, 11}, {9267, 10} } },
    -- Day'inda Knife
    {item=9176, ingredients={ {8762, 150}, {8765, 22}, {8784, 11}, {8916, 2}, {2295, 2}, {2623, 1}, {9401, 1}, {6799, 1} } },
    -- Handle Slasher
    {item=9974, ingredients={ {441, 10}, {473, 1} } },
}

registerCraftSkill(18, sk18Crafts, {jobID = jobID, toolIDs = toolIDs}, ingredientsForCraftJob(jobID), jobID)
