local jobID = LumberjackJob
local toolType = 19

-- TODO: Fix respawn timers
local gatherSkills = {
    {id=6,   obj=Objects.Ash,        minLvl=0,   itemID=303,  xp=10, respawn={6000, 10000} },
    {id=39,  obj=Objects.Chestnut,   minLvl=10,  itemID=473,  xp=15, respawn={6000, 10000} },
    {id=40,  obj=Objects.Walnut,     minLvl=20,  itemID=476,  xp=20, respawn={6000, 10000} },
    {id=10,  obj=Objects.Oak,        minLvl=30,  itemID=460,  xp=25, respawn={6000, 10000} },
    {id=139, obj=Objects.Bombu,      minLvl=35,  itemID=2358, xp=30, respawn={6000, 10000} },
    {id=141, obj=Objects.Oliviolet,  minLvl=35,  itemID=2357, xp=30, respawn={6000, 10000} },
    {id=37,  obj=Objects.Maple,      minLvl=40,  itemID=471,  xp=35, respawn={6000, 10000} },
    {id=33,  obj=Objects.Yew,        minLvl=50,  itemID=461,  xp=40, respawn={6000, 10000} },
    {id=154, obj=Objects.Bamboo,     minLvl=50,  itemID=7013, xp=40, respawn={6000, 10000} },
    {id=41,  obj=Objects.Cherry,     minLvl=60,  itemID=474,  xp=45, respawn={6000, 10000} },
    {id=34,  obj=Objects.Ebony,      minLvl=70,  itemID=449,  xp=50, respawn={6000, 10000} },
    {id=174, obj=Objects.Kaliptus,   minLvl=75,  itemID=7925, xp=55, respawn={6000, 10000} },
    {id=38,  obj=Objects.Charm,      minLvl=80,  itemID=472,  xp=65, respawn={6000, 10000} },
    {id=155, obj=Objects.DarkBamboo, minLvl=80,  itemID=7016, xp=65, respawn={6000, 10000} },
    {id=35,  obj=Objects.Elm,        minLvl=90,  itemID=470,  xp=70, respawn={6000, 10000} },
    {id=158, obj=Objects.HolyBamboo, minLvl=100, itemID=7014, xp=75, respawn={6000, 10000} },
}

registerGatherJobSkills(jobID, {toolType=toolType}, gatherSkills)

-- Craft for Saw
local sk101Crafts = {
    -- Ash Plank
    {item=459, ingredients={ {303, 20} } },
    -- Wood Shavings Potion
    {item=2539, ingredients={ {473, 2}, {476, 2} } },
    -- Strong Wood Shavings Potion
    {item=2540, ingredients={ {460, 2}, {461, 2}, {471, 2} } },
    -- Rare Wood Shavings Potion
    {item=2543, ingredients={ {474, 2}, {449, 2}, {472, 1}, {2250, 1} } },
    -- Chestnut Plank
    {item=6868, ingredients={ {473, 20} } },
    -- Oak Plank
    {item=7653, ingredients={ {460, 20} } },
    -- Yew Plank
    {item=7654, ingredients={ {461, 20} } },
    -- Ebony Plank
    {item=7655, ingredients={ {449, 20} } },
    -- Elm Plank
    {item=7656, ingredients={ {470, 20} } },
    -- Maple Plank
    {item=7657, ingredients={ {471, 20} } },
    -- Hornbeam Plank
    {item=7658, ingredients={ {472, 20} } },
    -- Walnut Plank
    {item=7659, ingredients={ {476, 20} } },
    -- Cherry Plank
    {item=7660, ingredients={ {474, 20} } },
    -- Bombu Plank
    {item=7661, ingredients={ {2358, 20} } },
    -- Oliviolet Plank
    {item=7662, ingredients={ {2357, 20} } },
    -- Bamboo Plank
    {item=7663, ingredients={ {7013, 20} } },
    -- Dark Bamboo Plank
    {item=7664, ingredients={ {7016, 20} } },
    -- Holy Bamboo Plank
    {item=7665, ingredients={ {7014, 20} } },
    -- Ancestral Wood Plank
    {item=7666, ingredients={ {920, 20} } },
    -- Bewitched Wood Plank
    {item=7667, ingredients={ {926, 20} } },
    -- Treechnid Wood Plank
    {item=7668, ingredients={ {2250, 20} } },
    -- Golden Bamboo Plank
    {item=7669, ingredients={ {7017, 20} } },
    -- Magic Bamboo Plank
    {item=7670, ingredients={ {7261, 20} } },
    -- Bambooto Plank
    {item=7671, ingredients={ {7286, 20} } },
    -- Holy Bambooto Plank
    {item=7672, ingredients={ {7289, 20} } },
    -- Kaliptus Plank
    {item=8078, ingredients={ {7925, 20} } },
}


registerCraftSkill(101, sk101Crafts, {jobID = jobID, toolType = toolType}, ingredientsForCraftJob(jobID), jobID)
