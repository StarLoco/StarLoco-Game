local jobID = WandCarverJob
local toolIDs = {499}

-- Craft for Carve a Wand
local sk16Crafts = {
    -- Twiggy Wand
    {item=132, ingredients={ {303, 2}, {473, 2} } },
    -- Great Twiggy Wand
    {item=133, ingredients={ {473, 3}, {303, 2} } },
    -- Powerful Twiggy Wand
    {item=134, ingredients={ {473, 4}, {303, 2} } },
    -- Small Ice Wand
    {item=162, ingredients={ {460, 1}, {473, 5}, {414, 1} } },
    -- Ice Wand
    {item=163, ingredients={ {473, 6}, {460, 2}, {414, 1} } },
    -- Great Ice Wand
    {item=164, ingredients={ {473, 6}, {460, 3}, {414, 1} } },
    -- Powerful Ice Wand
    {item=165, ingredients={ {473, 7}, {460, 4}, {414, 1} } },
    -- Small Intelligence Wand
    {item=168, ingredients={ {441, 2}, {474, 2}, {449, 2}, {461, 2} } },
    -- Small Wisdom Wand
    {item=174, ingredients={ {460, 2}, {474, 2}, {471, 3}, {350, 2} } },
    -- Limbo Wand
    {item=180, ingredients={ {6458, 10}, {1660, 10}, {316, 6}, {466, 5}, {315, 5}, {470, 14}, {472, 12}, {467, 12} } },
    -- Sky Wand
    {item=181, ingredients={ {466, 1}, {315, 1}, {474, 11}, {461, 10}, {449, 5}, {467, 2} } },
    -- Ber Ed Stick
    {item=333, ingredients={ {286, 25}, {470, 12}, {472, 1}, {284, 50}, {311, 50}, {285, 25} } },
    -- Liriel's Wand
    {item=334, ingredients={ {310, 8}, {471, 1}, {449, 1}, {474, 1} } },
    -- Treechnid Root Wand
    {item=469, ingredients={ {464, 2}, {476, 2}, {473, 20}, {435, 5}, {434, 5} } },
    -- Intelligence Wand
    {item=830, ingredients={ {441, 2}, {449, 2}, {461, 3}, {474, 3} } },
    -- Great Intelligence Wand
    {item=831, ingredients={ {474, 4}, {461, 4}, {449, 2}, {441, 2} } },
    -- Powerful Intelligence Wand
    {item=832, ingredients={ {461, 5}, {474, 5}, {441, 2}, {449, 2} } },
    -- Wisdom Wand
    {item=833, ingredients={ {471, 3}, {474, 3}, {460, 3}, {350, 2} } },
    -- Great Wisdom Wand
    {item=834, ingredients={ {460, 4}, {474, 4}, {471, 3}, {350, 2} } },
    -- Powerful Wisdom Wand
    {item=835, ingredients={ {460, 5}, {471, 3}, {350, 2}, {474, 5} } },
    -- Small Hour Wand
    {item=1101, ingredients={ {443, 6}, {429, 1}, {461, 1} } },
    -- Hour Wand
    {item=1102, ingredients={ {443, 8}, {461, 1}, {429, 1} } },
    -- Great Hour Wand
    {item=1103, ingredients={ {443, 10}, {461, 1}, {429, 1} } },
    -- Terrifying Hour Wand
    {item=1104, ingredients={ {443, 12}, {429, 1}, {461, 1} } },
    -- Small Clergy Wand
    {item=1105, ingredients={ {442, 8}, {460, 7}, {350, 2}, {752, 2} } },
    -- Clergy Wand
    {item=1107, ingredients={ {442, 10}, {460, 9}, {752, 2}, {350, 2} } },
    -- Heavy Clergy Wand
    {item=1108, ingredients={ {442, 12}, {460, 11}, {752, 2}, {350, 2} } },
    -- Imposing Clergy Wand
    {item=1131, ingredients={ {442, 14}, {460, 13}, {752, 2}, {350, 2} } },
    -- Ni'Ninnin Wand
    {item=1132, ingredients={ {1002, 10}, {1611, 10}, {470, 8}, {461, 8}, {2250, 5}, {466, 1} } },
    -- Small Inacleft Stick
    {item=1133, ingredients={ {443, 6}, {851, 5}, {883, 10}, {471, 8} } },
    -- Inacleft Stick
    {item=1134, ingredients={ {851, 7}, {883, 12}, {471, 10}, {443, 7} } },
    -- Great Inacleft Stick
    {item=1135, ingredients={ {883, 14}, {471, 12}, {851, 9}, {443, 8} } },
    -- Essential Inacleft Stick
    {item=1136, ingredients={ {883, 16}, {471, 14}, {851, 11}, {443, 9} } },
    -- Houvette's Discreet Wand
    {item=1137, ingredients={ {312, 5}, {449, 5}, {437, 4}, {407, 11}, {373, 10} } },
    -- Houvette's Wand
    {item=1138, ingredients={ {407, 11}, {312, 8}, {449, 6}, {437, 6}, {373, 14} } },
    -- Houvette's Great Wand
    {item=1139, ingredients={ {373, 18}, {407, 11}, {312, 11}, {437, 8}, {449, 7} } },
    -- Houvette's Traumatic Wand
    {item=1140, ingredients={ {449, 8}, {437, 8}, {373, 22}, {312, 14}, {407, 11} } },
    -- Small Iron Wand
    {item=1143, ingredients={ {854, 10}, {764, 10}, {376, 5}, {472, 3}, {942, 3}, {465, 1} } },
    -- Iron Wand
    {item=1144, ingredients={ {465, 1}, {68, 10}, {75, 10}, {376, 7}, {942, 4}, {472, 3} } },
    -- Great Iron Wand
    {item=1145, ingredients={ {76, 10}, {69, 10}, {376, 9}, {942, 5}, {472, 4}, {465, 1} } },
    -- Excellent Iron Wand
    {item=1146, ingredients={ {70, 10}, {77, 10}, {942, 6}, {472, 5}, {465, 1}, {376, 11} } },
    -- Half Loaf
    {item=1356, ingredients={ {303, 10}, {526, 10}, {312, 1} } },
    -- Wangs
    {item=1357, ingredients={ {350, 2}, {1102, 1}, {218, 1}, {471, 5}, {1337, 4} } },
    -- Wily Wand
    {item=1359, ingredients={ {134, 1}, {313, 5}, {449, 5}, {1343, 5}, {1345, 4}, {1660, 1} } },
    -- The Hyldegarde
    {item=1360, ingredients={ {748, 1}, {473, 5}, {2358, 3}, {472, 3}, {437, 2} } },
    -- Sylvian Wand
    {item=1361, ingredients={ {437, 2}, {463, 1}, {747, 1}, {476, 4}, {473, 4} } },
    -- Vampiric Stake
    {item=1362, ingredients={ {334, 1}, {350, 10}, {1345, 5}, {445, 5}, {1462, 5}, {472, 3} } },
    -- Will-o'-the-Wisp's Wand
    {item=1503, ingredients={ {441, 10}, {1333, 5}, {461, 1}, {132, 1} } },
    -- Red-Hot Wand
    {item=1739, ingredients={ {1610, 2}, {2254, 2}, {1535, 1}, {2358, 10}, {1018, 6}, {449, 5}, {474, 5} } },
    -- Hell's Wand
    {item=5999, ingredients={ {2357, 10}, {470, 10}, {313, 10}, {2250, 4}, {376, 2}, {2506, 1} } },
    -- Limp Wand
    {item=6438, ingredients={ {2558, 2}, {316, 1}, {476, 10}, {2594, 10}, {472, 10}, {2504, 3}, {2584, 3} } },
    -- Unreal Wand
    {item=6439, ingredients={ {310, 10}, {432, 10}, {2642, 10}, {1675, 1}, {2323, 1}, {2281, 1}, {2676, 1} } },
    -- Hairy Wand
    {item=6440, ingredients={ {470, 10}, {1890, 2}, {1691, 1}, {840, 1}, {2248, 10}, {449, 10}, {649, 10} } },
    -- Elya Wood's Wand
    {item=6494, ingredients={ {316, 4}, {926, 3}, {466, 3}, {920, 2}, {2566, 2}, {470, 20}, {1610, 20}, {1612, 20} } },
    -- Soft Oak Wand
    {item=6495, ingredients={ {6487, 3}, {8347, 3}, {6496, 1}, {8785, 100}, {7014, 30}, {926, 10}, {6490, 8}, {8774, 5} } },
    -- Dark Treechnid Root Wand
    {item=6496, ingredients={ {461, 10}, {474, 10}, {1611, 5}, {1612, 5}, {1610, 5}, {1660, 2} } },
    -- Clearing Balgourde
    {item=6497, ingredients={ {749, 1}, {1660, 1}, {474, 5}, {2250, 5}, {470, 5}, {467, 1} } },
    -- Amrothiline
    {item=6518, ingredients={ {1610, 20}, {1612, 20}, {470, 20}, {449, 20}, {1660, 4}, {315, 4}, {467, 2}, {2564, 30} } },
    -- Kouartz Wand
    {item=6519, ingredients={ {465, 1}, {315, 1}, {926, 1}, {750, 20}, {2358, 12}, {449, 12}, {2357, 12} } },
    -- The Migraine
    {item=6520, ingredients={ {474, 8}, {2565, 5}, {464, 5}, {467, 1}, {1002, 20}, {2358, 10}, {472, 9} } },
    -- The Xyothine
    {item=7103, ingredients={ {7404, 1}, {470, 30}, {7014, 20}, {846, 10}, {847, 10}, {7035, 6}, {7026, 5}, {918, 4} } },
    -- Larvaresque Wand
    {item=7110, ingredients={ {363, 3}, {364, 2}, {2328, 1}, {519, 10}, {362, 4} } },
    -- Kilih Wand Jaro
    {item=7165, ingredients={ {7286, 15}, {470, 6}, {7369, 5}, {7289, 2}, {7027, 1}, {7265, 1}, {7290, 1} } },
    -- Wand Hering
    {item=7167, ingredients={ {7262, 1}, {473, 6}, {1002, 3}, {461, 2}, {7013, 1} } },
    -- Pinted Wand
    {item=7168, ingredients={ {2357, 6}, {7264, 6}, {1002, 5}, {460, 3}, {472, 3}, {7013, 2} } },
    -- Sparkly Wand
    {item=7169, ingredients={ {1002, 8}, {461, 6}, {7369, 3}, {7264, 1}, {7261, 1}, {7262, 1}, {7263, 1} } },
    -- Creizy-Stufh Wand
    {item=7170, ingredients={ {7028, 1}, {7016, 12}, {2563, 12}, {2564, 10}, {7262, 9}, {470, 6}, {7369, 5} } },
    -- Wand Heroff
    {item=7171, ingredients={ {7263, 5}, {7014, 3}, {7291, 1}, {7408, 1}, {7270, 1}, {7370, 1}, {918, 1}, {7016, 15} } },
    -- Boogey Wand
    {item=7172, ingredients={ {2357, 12}, {1002, 10}, {472, 9}, {470, 6}, {7369, 2}, {7290, 1}, {7286, 15} } },
    -- Strangly Wand
    {item=7173, ingredients={ {7262, 1}, {7263, 1}, {7264, 1}, {7286, 2} } },
    -- Star Wand
    {item=7368, ingredients={ {7026, 1}, {918, 1}, {7016, 12}, {7263, 4}, {7017, 3}, {7270, 2}, {7411, 1}, {7370, 1} } },
    -- Gyver Wand
    {item=8088, ingredients={ {2663, 10}, {918, 5}, {7925, 1}, {2449, 100}, {2245, 60}, {7287, 50}, {7288, 42}, {2483, 40} } },
    -- Iots Wand
    {item=8089, ingredients={ {7925, 24}, {7262, 23}, {8161, 20}, {408, 15}, {7261, 3}, {1682, 2}, {7408, 2}, {2245, 110} } },
    -- Tofu Wand
    {item=8110, ingredients={ {366, 18}, {301, 16}, {367, 11}, {8557, 1} } },
    -- Golden Scarabugly Wand
    {item=8118, ingredients={ {2292, 2}, {2290, 2}, {2291, 2}, {2293, 2}, {8160, 1}, {8161, 1} } },
    -- Dreggon Wand
    {item=8296, ingredients={ {8056, 3}, {7408, 2}, {8327, 1}, {8060, 30}, {8353, 19}, {7925, 16}, {2357, 14}, {470, 10} } },
    -- Wand Else?
    {item=8611, ingredients={ {8731, 1}, {8740, 15}, {471, 12}, {2358, 8}, {8682, 3}, {8739, 2} } },
    -- The Bidjiz
    {item=8612, ingredients={ {546, 17}, {8756, 17}, {465, 3}, {8486, 2}, {8784, 1}, {8761, 1} } },
    -- Dark Treeckler Branch
    {item=8834, ingredients={ {8772, 3}, {8765, 2}, {8773, 2}, {8774, 1}, {8996, 1}, {8785, 48}, {8784, 8} } },
    -- Light Treeckler Branch
    {item=8835, ingredients={ {8781, 28}, {8796, 9}, {8771, 3}, {8775, 2}, {8997, 1}, {8797, 52}, {8780, 34} } },
    -- Wanderelle
    {item=9135, ingredients={ {8775, 13}, {8792, 12}, {9263, 10}, {8770, 2}, {7103, 1}, {9267, 21}, {8765, 19}, {8772, 14} } },
}

registerCraftSkill(16, sk16Crafts, {jobID = jobID, toolIDs = toolIDs}, ingredientsForCraftJob(jobID), jobID)
