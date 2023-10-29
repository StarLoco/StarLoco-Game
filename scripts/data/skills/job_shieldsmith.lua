local jobID = ShieldSmithJob
local toolIDs = {7098}

-- Craft for Forge a Shield
local sk156Crafts = {
    -- Sharkal Shield
    {item=8855, ingredients={ {1780, 3}, {460, 3}, {1852, 1}, {8681, 1}, {1806, 5}, {7032, 3} } },
    -- Sidimote Shield
    {item=9001, ingredients={ {7013, 25}, {2357, 12}, {7016, 10}, {449, 10}, {748, 4}, {7036, 2} } },
    -- Phtalmo
    {item=9002, ingredients={ {450, 1}, {6457, 10}, {2552, 10}, {2579, 10}, {2453, 10}, {7013, 10}, {313, 2} } },
    -- Inn Shield
    {item=9003, ingredients={ {7275, 10}, {443, 25}, {7277, 10}, {7278, 10}, {460, 10}, {7276, 10} } },
    -- Akwadala Shield
    {item=9004, ingredients={ {7222, 50}, {461, 5}, {2357, 4}, {2291, 2}, {747, 1} } },
    -- Fan Shield
    {item=9005, ingredients={ {361, 2}, {7304, 2}, {1694, 1}, {7301, 51}, {445, 30}, {745, 10}, {7285, 10}, {7259, 2} } },
    -- Sadida Shield
    {item=9006, ingredients={ {885, 50}, {582, 10}, {7016, 10}, {7305, 10}, {749, 10}, {7272, 3}, {7270, 3} } },
    -- Shield in Briefs
    {item=9007, ingredients={ {7036, 10}, {1736, 10}, {445, 8}, {7261, 2}, {3208, 2}, {7017, 1}, {2480, 10} } },
    -- Asse Shield
    {item=9008, ingredients={ {2275, 12}, {749, 10}, {312, 10}, {7013, 10}, {600, 4}, {464, 2}, {7305, 1} } },
    -- Treechnid Shield
    {item=9009, ingredients={ {7261, 1}, {435, 10}, {434, 10}, {476, 2}, {459, 2} } },
    -- Pandawa Shield
    {item=9010, ingredients={ {7258, 2}, {7016, 2}, {7302, 2}, {7059, 1}, {7018, 10} } },
    -- Fire Dial
    {item=9011, ingredients={ {843, 3}, {7289, 2}, {461, 1}, {747, 1} } },
    -- Air Dial
    {item=9012, ingredients={ {461, 1}, {747, 1}, {842, 3}, {7289, 2} } },
    -- Earth Dial
    {item=9013, ingredients={ {308, 3}, {7289, 2}, {747, 1}, {461, 1} } },
    -- Water Dial
    {item=9014, ingredients={ {1129, 3}, {7289, 2}, {461, 1}, {747, 1} } },
    -- Lumberjack Shield
    {item=9015, ingredients={ {442, 5}, {303, 5}, {7013, 5} } },
    -- Mount Stinkky Shield
    {item=9016, ingredients={ {747, 3}, {7270, 2}, {7013, 10}, {471, 10}, {6458, 3} } },
    -- Imp Shield
    {item=9017, ingredients={ {7036, 2}, {7017, 1}, {7013, 25}, {7016, 20}, {7035, 5}, {6457, 4}, {748, 3} } },
    -- Heart Quarter
    {item=9018, ingredients={ {7285, 5}, {449, 3}, {746, 3}, {2306, 2}, {2605, 1}, {2594, 20} } },
    -- Bawbawian Shield
    {item=9019, ingredients={ {289, 15}, {473, 10}, {749, 10}, {885, 8}, {406, 6}, {7014, 1} } },
    -- Derma Tho
    {item=9020, ingredients={ {747, 4}, {7280, 2}, {7279, 2}, {459, 20} } },
    -- Captain Amakna Shield
    {item=9021, ingredients={ {7294, 15}, {7032, 10}, {1686, 4}, {920, 3}, {7014, 3}, {316, 2}, {487, 1}, {6457, 50} } },
    -- Kloome
    {item=9022, ingredients={ {7016, 10}, {7289, 4}, {2805, 2}, {7304, 1}, {472, 1}, {519, 80}, {2252, 50} } },
    -- Flying Shield
    {item=9023, ingredients={ {7013, 4}, {7260, 3}, {7301, 1} } },
    -- LeChouque's Shield
    {item=9024, ingredients={ {301, 10}, {997, 10}, {7016, 7}, {1610, 4}, {449, 2}, {315, 1}, {6441, 1}, {2358, 10} } },
    -- Gobball Shield
    {item=9025, ingredients={ {384, 5}, {885, 5} } },
    -- Bowisse's Shield
    {item=9026, ingredients={ {7262, 2}, {7017, 4}, {7263, 2}, {7265, 2}, {444, 2}, {7264, 2} } },
    -- Crackler Shield
    {item=9027, ingredients={ {2306, 1}, {312, 15}, {350, 10}, {446, 5}, {313, 5} } },
    -- Terrdala Shield
    {item=9028, ingredients={ {350, 6}, {7261, 3}, {2293, 2}, {7305, 1}, {7224, 50}, {471, 10} } },
    -- Feudala Shield
    {item=9029, ingredients={ {7298, 1}, {7225, 50}, {473, 15}, {745, 13}, {7016, 4}, {2292, 2}, {470, 1} } },
    -- Aerdala Shield
    {item=9030, ingredients={ {7017, 1}, {449, 1}, {7261, 1}, {7223, 50}, {303, 10}, {2290, 2} } },
}

local requirements = {jobID = jobID, toolIDs = toolIDs}
registerCraftSkill(156, sk156Crafts, requirements, ingredientsForCraftJob(jobID), jobID)
