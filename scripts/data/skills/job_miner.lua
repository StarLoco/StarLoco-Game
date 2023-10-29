local jobID = MinerJob
local toolType = 21

--FIXME timing / Reward
local gatherSkills = {
    {id=24,  obj=Objects.Iron,      minLvl=0,   itemID=312,  xp=10, respawn={6000, 10000} },
    {id=29,  obj=Objects.Copper,    minLvl=10,  itemID=441,  xp=15, respawn={6000, 10000} },
    {id=30,  obj=Objects.Bronze,    minLvl=20,  itemID=442,  xp=20, respawn={6000, 10000} },
    {id=28,  obj=Objects.Cobalt,    minLvl=30,  itemID=443,  xp=25, respawn={6000, 10000} },
    {id=55,  obj=Objects.Manganese, minLvl=40,  itemID=445,  xp=30, respawn={6000, 10000} },
    {id=25,  obj=Objects.Tin,       minLvl=50,  itemID=444,  xp=35, respawn={6000, 10000} },
    {id=56,  obj=Objects.Silicate,  minLvl=50,  itemID=7032, xp=35, respawn={6000, 10000} },
    {id=26,  obj=Objects.Silver,    minLvl=60,  itemID=350,  xp=40, respawn={6000, 10000} },
    {id=161, obj=Objects.Bauxite,   minLvl=70,  itemID=446,  xp=45, respawn={6000, 10000} },
    {id=162, obj=Objects.Gold,      minLvl=80,  itemID=313,  xp=50, respawn={6000, 10000} },
    {id=161, obj=Objects.Dolomite,  minLvl=100, itemID=7033, xp=50, respawn={6000, 10000} },
}

registerGatherJobSkills(jobID, {toolType=toolType}, gatherSkills)

-- Craft for Melt
local sk32Crafts = {
    -- Gold Ingot
    {item=745, ingredients={ {313, 10} } },
    -- Ebonite
    {item=746, ingredients={ {442, 10}, {441, 10}, {312, 10} } },
    -- Aluminite
    {item=747, ingredients={ {312, 10}, {441, 10} } },
    -- Magnesite
    {item=748, ingredients={ {442, 10}, {312, 10}, {441, 10}, {443, 10} } },
    -- Bakelelite
    {item=749, ingredients={ {445, 10}, {443, 10}, {312, 10}, {442, 10}, {441, 10} } },
    -- Kouartz
    {item=750, ingredients={ {441, 10}, {445, 10}, {444, 10}, {312, 10}, {443, 10}, {442, 10} } },
    -- Liquid Metal Potion
    {item=2529, ingredients={ {441, 2}, {442, 2}, {1468, 1} } },
    -- Liquid Heavy Metal Potion
    {item=2538, ingredients={ {444, 2}, {445, 2}, {1468, 1}, {443, 2} } },
    -- Liquid Precious Metal Potion
    {item=2541, ingredients={ {431, 1}, {1468, 1}, {446, 2}, {313, 2}, {350, 2} } },
    -- Kriptonite
    {item=6457, ingredients={ {441, 10}, {312, 10}, {350, 10}, {445, 10}, {444, 10}, {443, 10}, {442, 10} } },
    -- Kobalite
    {item=6458, ingredients={ {445, 10}, {443, 10}, {442, 10}, {441, 10}, {312, 10}, {446, 10}, {350, 10}, {444, 10} } },
    -- Pyrute
    {item=7035, ingredients={ {445, 10}, {443, 10}, {446, 10}, {442, 10}, {7033, 10}, {350, 10}, {441, 10}, {444, 10} } },
    -- Brassic
    {item=7036, ingredients={ {441, 10}, {7032, 10}, {350, 10}, {443, 10}, {444, 10}, {313, 10}, {442, 10}, {445, 10} } },
}

-- Craft for Polish a stone
local sk48Crafts = {
    -- Diamond
    {item=315, ingredients={ {311, 10}, {543, 10} } },
    -- Emerald
    {item=316, ingredients={ {311, 10}, {544, 10} } },
    -- Crystal
    {item=465, ingredients={ {545, 10}, {311, 10} } },
    -- Sapphire
    {item=466, ingredients={ {311, 10}, {546, 10} } },
    -- Ruby
    {item=467, ingredients={ {311, 10}, {547, 10} } },
    -- Aquamarine
    {item=7026, ingredients={ {311, 10}, {7023, 10} } },
    -- Topaz
    {item=7027, ingredients={ {311, 10}, {7024, 10} } },
    -- Agathe
    {item=7028, ingredients={ {311, 10}, {7025, 10} } },
    -- Miner Rune
    {item=7461, ingredients={ {1596, 1}, {431, 1}, {463, 1}, {7369, 1} } },
    -- Farmer Rune
    {item=7462, ingredients={ {431, 1}, {1600, 1}, {463, 1}, {7369, 1} } },
    -- Alchemist Rune
    {item=7463, ingredients={ {7369, 1}, {463, 1}, {431, 1}, {1598, 1} } },
    -- Jeweller Rune
    {item=7464, ingredients={ {431, 1}, {1592, 1}, {7369, 1}, {463, 1} } },
    -- Butcher Rune
    {item=7465, ingredients={ {7369, 1}, {2186, 1}, {431, 1}, {463, 1} } },
    -- Baker Rune
    {item=7466, ingredients={ {7369, 1}, {431, 1}, {463, 1}, {1597, 1} } },
    -- Lumberjack Rune
    {item=7467, ingredients={ {431, 1}, {1587, 1}, {463, 1}, {7369, 1} } },
    -- Hunter Rune
    {item=7468, ingredients={ {463, 1}, {431, 1}, {2185, 1}, {7369, 1} } },
    -- Shoemaker Rune
    {item=7469, ingredients={ {7369, 1}, {431, 1}, {463, 1}, {1591, 1} } },
    -- Sword Smith Rune
    {item=7470, ingredients={ {463, 1}, {431, 1}, {7369, 1}, {1588, 1} } },
    -- Shield Smith Rune
    {item=7471, ingredients={ {7369, 1}, {463, 1}, {431, 1}, {7421, 1} } },
    -- Dagger Smith Rune
    {item=7472, ingredients={ {431, 1}, {1542, 1}, {463, 1}, {7369, 1} } },
    -- Axe Smith Rune
    {item=7473, ingredients={ {431, 1}, {463, 1}, {7369, 1}, {1601, 1} } },
    -- Hammer Smith Rune
    {item=7474, ingredients={ {7369, 1}, {1590, 1}, {431, 1}, {463, 1} } },
    -- Shovel Smith Rune
    {item=7475, ingredients={ {431, 1}, {7369, 1}, {463, 1}, {1595, 1} } },
    -- Fishmonger Rune
    {item=7476, ingredients={ {463, 1}, {431, 1}, {2184, 1}, {7369, 1} } },
    -- Fisherman Rune
    {item=7477, ingredients={ {431, 1}, {2183, 1}, {463, 1}, {7369, 1} } },
    -- Bow Carver Rune
    {item=7478, ingredients={ {463, 1}, {7369, 1}, {431, 1}, {1589, 1} } },
    -- Wand Carver Rune
    {item=7479, ingredients={ {463, 1}, {431, 1}, {1594, 1}, {7369, 1} } },
    -- Staff Carver Rune
    {item=7480, ingredients={ {431, 1}, {1593, 1}, {7369, 1}, {463, 1} } },
    -- Tailor Rune
    {item=7481, ingredients={ {7369, 1}, {1599, 1}, {463, 1}, {431, 1} } },
    -- Sword Smithmagus Rune
    {item=7482, ingredients={ {463, 1}, {7369, 1}, {431, 1}, {1645, 1} } },
    -- Dagger Smithmagus Rune
    {item=7483, ingredients={ {463, 1}, {7369, 1}, {1642, 1}, {431, 1} } },
    -- Hammer Smithmagus Rune
    {item=7484, ingredients={ {1644, 1}, {431, 1}, {463, 1}, {7369, 1} } },
    -- Axe Smithmagus Rune
    {item=7485, ingredients={ {1643, 1}, {431, 1}, {463, 1}, {7369, 1} } },
    -- Shovel Smithmagus Rune
    {item=7486, ingredients={ {1646, 1}, {463, 1}, {431, 1}, {7369, 1} } },
    -- Bow Carvmagus Rune
    {item=7487, ingredients={ {1647, 1}, {463, 1}, {7369, 1}, {431, 1} } },
    -- Wand Carvmagus Rune
    {item=7488, ingredients={ {1648, 1}, {7369, 1}, {463, 1}, {431, 1} } },
    -- Staff Carvmagus Rune
    {item=7489, ingredients={ {431, 1}, {7369, 1}, {463, 1}, {1649, 1} } },
    -- Shoemagus Rune
    {item=7490, ingredients={ {431, 1}, {7507, 1}, {463, 1}, {7369, 1} } },
    -- Jewelmagus Rune
    {item=7491, ingredients={ {7369, 1}, {7506, 1}, {431, 1}, {463, 1} } },
    -- Costumagus Rune
    {item=7492, ingredients={ {7505, 1}, {431, 1}, {463, 1}, {7369, 1} } },
    -- Signature Rune
    {item=7508, ingredients={ {313, 1}, {7652, 1} } },
    -- Handyman Rune
    {item=8377, ingredients={ {463, 1}, {431, 1}, {7369, 1}, {8106, 1} } },
    -- Mother of Pearl
    {item=9941, ingredients={ {311, 10}, {9940, 10} } },
}

local requirements = {jobID = jobID, toolType = toolType}
registerCraftSkill(32, sk32Crafts, requirements, ingredientsForCraftJob(jobID), jobID)
registerCraftSkill(48, sk48Crafts, requirements, ingredientsForCraftJob(jobID), jobID)
