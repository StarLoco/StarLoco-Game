local jobID = BowCarverJob
local toolIDs = {500}

-- Craft for Carve a Bow
local sk15Crafts = {
    -- Small Twiggy Bow
    {item=88, ingredients={ {303, 4}, {420, 1} } },
    -- Twiggy Bow
    {item=89, ingredients={ {303, 5}, {420, 1} } },
    -- Great Twiggy Bow
    {item=90, ingredients={ {303, 6}, {420, 1} } },
    -- Powerful Twiggy Bow
    {item=91, ingredients={ {303, 7}, {420, 1} } },
    -- Small Disconcerting Bow
    {item=264, ingredients={ {476, 7}, {473, 7}, {420, 1} } },
    -- Disconcerting Bow
    {item=265, ingredients={ {476, 8}, {420, 1}, {473, 9} } },
    -- Great Disconcerting Bow
    {item=266, ingredients={ {476, 10}, {473, 10}, {420, 1} } },
    -- Powerful Disconcerting Bow
    {item=267, ingredients={ {473, 12}, {476, 11}, {420, 1} } },
    -- Xaver
    {item=270, ingredients={ {472, 10}, {474, 5}, {313, 4}, {316, 3}, {449, 2}, {420, 1} } },
    -- Nomoon
    {item=272, ingredients={ {350, 5}, {449, 2}, {420, 1}, {474, 5} } },
    -- Yamato String
    {item=273, ingredients={ {420, 1}, {441, 1}, {474, 5}, {449, 5}, {461, 5} } },
    -- The Arc de Triomphe
    {item=329, ingredients={ {420, 1}, {313, 1}, {474, 3}, {461, 2}, {449, 2} } },
    -- Rain Bow
    {item=331, ingredients={ {461, 5}, {449, 2}, {420, 1}, {460, 1} } },
    -- Joan's Bow
    {item=332, ingredients={ {474, 2}, {471, 1}, {420, 1}, {461, 5}, {449, 5} } },
    -- Bwork Archer Bow
    {item=337, ingredients={ {449, 5}, {460, 2}, {420, 1}, {429, 1} } },
    -- Flax String
    {item=420, ingredients={ {423, 10} } },
    -- Treechnid Root Bow
    {item=462, ingredients={ {420, 1}, {435, 10}, {464, 2}, {461, 1} } },
    -- Ivan Nowe String
    {item=592, ingredients={ {461, 7}, {460, 7}, {449, 6}, {474, 1}, {420, 1} } },
    -- Rento Bow
    {item=640, ingredients={ {435, 5}, {2358, 4}, {2357, 4}, {1002, 4}, {420, 1} } },
    -- Whistling Bow
    {item=828, ingredients={ {420, 1}, {471, 3}, {460, 2} } },
    -- Com Bow
    {item=829, ingredients={ {476, 2}, {303, 4} } },
    -- Small Buzzard Bow
    {item=1092, ingredients={ {301, 7}, {303, 4}, {420, 2} } },
    -- Buzzard Bow
    {item=1093, ingredients={ {420, 2}, {301, 9}, {303, 4} } },
    -- Great Buzzard Bow
    {item=1094, ingredients={ {301, 10}, {303, 5}, {420, 2}, {473, 2} } },
    -- Powerful Buzzard Bow
    {item=1095, ingredients={ {301, 11}, {303, 6}, {473, 2}, {420, 2} } },
    -- Small Gobball Horn Bow
    {item=1096, ingredients={ {383, 6}, {312, 5}, {473, 4}, {420, 2}, {461, 1} } },
    -- Gobball Horn Bow
    {item=1097, ingredients={ {420, 2}, {461, 1}, {383, 8}, {312, 6}, {473, 4} } },
    -- Heavy Gobball Horn Bow
    {item=1098, ingredients={ {383, 10}, {312, 7}, {473, 4}, {420, 2}, {461, 1} } },
    -- Royal Gobball Horn Bow
    {item=1112, ingredients={ {383, 12}, {312, 8}, {473, 4}, {420, 2}, {461, 1} } },
    -- I Kea Bow, Sale Model
    {item=1113, ingredients={ {459, 4}, {578, 1}, {420, 1}, {445, 8}, {471, 4} } },
    -- I Kea Bow, Standard Model
    {item=1114, ingredients={ {420, 1}, {445, 10}, {459, 4}, {471, 4}, {578, 2} } },
    -- I Kea Bow, Special Model
    {item=1115, ingredients={ {578, 3}, {420, 1}, {445, 12}, {459, 4}, {471, 4} } },
    -- I Kea Bow, New Model
    {item=1116, ingredients={ {471, 4}, {459, 4}, {578, 4}, {420, 1}, {445, 14} } },
    -- Small Eco Bow
    {item=1117, ingredients={ {420, 1}, {437, 4}, {473, 3} } },
    -- Eco Bow
    {item=1118, ingredients={ {437, 6}, {473, 3}, {476, 3}, {420, 1} } },
    -- Great Eco Bow
    {item=1119, ingredients={ {420, 1}, {437, 8}, {476, 4}, {473, 4} } },
    -- Renowned Eco Bow
    {item=1120, ingredients={ {437, 10}, {476, 4}, {473, 4}, {460, 3}, {420, 1} } },
    -- Opaque Crystal Bow
    {item=1121, ingredients={ {519, 10}, {465, 1}, {420, 1}, {88, 1}, {847, 1} } },
    -- Crystal Bow
    {item=1122, ingredients={ {519, 20}, {465, 1}, {89, 1}, {847, 1}, {420, 1} } },
    -- Translucent Crystal Bow
    {item=1123, ingredients={ {420, 1}, {465, 1}, {90, 1}, {847, 1}, {519, 30}, {473, 8} } },
    -- Pure Crystal Bow
    {item=1124, ingredients={ {519, 40}, {473, 10}, {91, 1}, {420, 1}, {847, 1}, {465, 1} } },
    -- Small King of Borins Bow
    {item=1125, ingredients={ {470, 1}, {350, 10}, {476, 10}, {420, 4}, {359, 2}, {893, 1} } },
    -- King of Borins Bow
    {item=1126, ingredients={ {350, 15}, {476, 15}, {420, 4}, {359, 3}, {470, 1}, {894, 1} } },
    -- Stylish King of Borins Bow
    {item=1127, ingredients={ {895, 1}, {350, 20}, {476, 20}, {359, 4}, {420, 4}, {470, 1} } },
    -- Ultra-powerful King of Borins Bow
    {item=1128, ingredients={ {420, 4}, {896, 1}, {470, 1}, {476, 25}, {350, 25}, {359, 5} } },
    -- Strong Bow
    {item=1350, ingredients={ {311, 10}, {460, 8}, {1335, 1} } },
    -- Ykulf Bow
    {item=1351, ingredients={ {420, 1}, {748, 1}, {473, 12}, {460, 8}, {471, 8} } },
    -- Mushd Bow
    {item=1352, ingredients={ {417, 20}, {2584, 12}, {303, 10}, {420, 1}, {2585, 1} } },
    -- Sawn-Off Pulley Bow
    {item=1353, ingredients={ {1002, 2}, {420, 1}, {434, 8}, {473, 8}, {472, 6} } },
    -- Hickory Tree
    {item=1354, ingredients={ {449, 5}, {435, 5}, {420, 1}, {303, 12}, {2358, 8}, {474, 8} } },
    -- Hidsad Bow
    {item=1355, ingredients={ {460, 8}, {2357, 5}, {420, 1}, {303, 20}, {473, 10} } },
    -- Treebow
    {item=1620, ingredients={ {434, 25}, {1612, 2}, {1610, 2}, {435, 25}, {437, 25} } },
    -- Arch Bow
    {item=6445, ingredients={ {461, 10}, {470, 8}, {479, 5}, {420, 1}, {467, 1}, {1002, 20}, {2357, 10} } },
    -- Chiduc's Arc
    {item=6446, ingredients={ {472, 12}, {464, 5}, {747, 4}, {6458, 4}, {420, 1}, {1002, 20}, {449, 12} } },
    -- Angel Bow
    {item=6451, ingredients={ {2358, 10}, {1612, 4}, {2564, 2}, {420, 1}, {2565, 1}, {2357, 10}, {472, 10} } },
    -- Draught Bow
    {item=6452, ingredients={ {926, 6}, {316, 4}, {2566, 4}, {466, 4}, {2656, 1}, {2250, 30}, {470, 30}, {2565, 10} } },
    -- Fishing Bow
    {item=6453, ingredients={ {420, 1}, {434, 25}, {1610, 10}, {461, 10}, {2357, 10}, {1612, 8}, {2250, 2} } },
    -- Sram Archer Bow
    {item=6484, ingredients={ {1612, 10}, {467, 2}, {420, 1}, {926, 1}, {2250, 12}, {1610, 10}, {1611, 10} } },
    -- Chafer Archer Bow
    {item=6485, ingredients={ {2565, 12}, {2300, 10}, {466, 4}, {2566, 4}, {465, 4}, {2656, 1}, {449, 25}, {461, 25} } },
    -- Trunknydum
    {item=6491, ingredients={ {2565, 1}, {420, 1}, {2250, 10}, {2563, 8}, {1610, 3}, {2564, 2} } },
    -- Doozi Bow
    {item=7160, ingredients={ {7017, 6}, {7014, 4}, {7265, 3}, {7028, 2}, {7408, 1}, {2656, 1}, {7271, 1}, {7036, 1} } },
    -- Young Wild Bow
    {item=7161, ingredients={ {7264, 30}, {7016, 30}, {449, 15}, {7369, 10}, {7265, 6}, {7270, 2}, {2656, 1} } },
    -- Placee Bow
    {item=7162, ingredients={ {7271, 2}, {918, 2}, {2656, 1}, {7407, 1}, {1002, 30}, {2563, 12}, {7014, 6}, {7027, 3} } },
    -- Runaway Bow
    {item=7163, ingredients={ {7270, 2}, {7405, 1}, {2656, 1}, {7016, 12}, {7027, 4}, {7370, 2}, {7289, 2}, {7272, 2} } },
    -- Marilyn Mun Bow
    {item=7164, ingredients={ {7014, 5}, {7026, 5}, {7017, 4}, {918, 4}, {7261, 3}, {2656, 1}, {7406, 1}, {7265, 50} } },
    -- Dubya Bow
    {item=7187, ingredients={ {7014, 5}, {7289, 4}, {7017, 2}, {2656, 1}, {7405, 1}, {7291, 1}, {7370, 1}, {7016, 20} } },
    -- Arkanum Bow
    {item=7194, ingredients={ {420, 1}, {7017, 1}, {7013, 12}, {7369, 8}, {7016, 5}, {918, 1}, {7270, 1} } },
    -- Koalak Bow
    {item=8005, ingredients={ {7925, 16}, {8083, 4}, {8002, 3}, {8059, 2}, {8060, 2}, {8084, 1} } },
    -- Web Bow
    {item=8103, ingredients={ {8345, 1}, {1612, 20}, {7925, 13}, {2357, 12}, {7263, 11}, {466, 8}, {8361, 4}, {7290, 1} } },
    -- Chtelion Bow
    {item=8104, ingredients={ {7906, 10}, {8363, 2}, {918, 2}, {926, 1}, {8405, 1}, {8083, 25}, {7925, 15}, {467, 11} } },
    -- Dreggon Bow
    {item=8295, ingredients={ {382, 25}, {2300, 10}, {7405, 4}, {7407, 3}, {8363, 2}, {8345, 1}, {8083, 30} } },
    -- Bushi'Bow
    {item=8609, ingredients={ {8758, 3}, {8753, 3}, {643, 1}, {8750, 19}, {8763, 19}, {8760, 12}, {8746, 3} } },
    -- Bow Liwood
    {item=8610, ingredients={ {8736, 3}, {8995, 1}, {462, 1}, {519, 80}, {8762, 15}, {8757, 12}, {8682, 4} } },
    -- Snailmet Bow
    {item=8864, ingredients={ {8793, 8}, {8786, 6}, {8777, 4}, {8802, 4}, {8798, 1}, {8832, 69}, {8057, 24}, {7925, 17} } },
    -- Rahm Bow
    {item=8924, ingredients={ {8791, 9}, {8771, 3}, {2620, 3}, {643, 2}, {8792, 2}, {8795, 2}, {8744, 130}, {8788, 32} } },
    -- Little Bow Sleep
    {item=8925, ingredients={ {1347, 6}, {8764, 3}, {8381, 1}, {8998, 1}, {461, 32}, {8807, 12}, {8796, 8}, {1345, 6} } },
    -- Beddy Bow
    {item=9134, ingredients={ {8765, 21}, {9278, 20}, {8802, 13}, {8792, 11}, {9263, 10}, {8410, 3}, {8741, 25}, {8779, 24} } },
    -- Arcadia Bow
    {item=11539, ingredients={ {6458, 23}, {11528, 21}, {11531, 6}, {11526, 5}, {6489, 2}, {8295, 1}, {8310, 58}, {9389, 43} } },
}

registerCraftSkill(15, sk15Crafts, {jobID = jobID, toolIDs = toolIDs}, ingredientsForCraftJob(jobID), jobID)
