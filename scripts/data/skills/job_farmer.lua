local jobID = FarmerJob
local toolType = 22


--FIXME timing / Reward
--FIXME Reward special cereals sometimes
local gatherSkills = {
    {id=45,  obj=Objects.Wheat,  minLvl=0,   itemID=289,  xp=10, respawn={6000, 10000} },
    {id=53,  obj=Objects.Barley, minLvl=10,  itemID=400,  xp=15, respawn={6000, 10000} },
    {id=57,  obj=Objects.Oats,   minLvl=20,  itemID=533,  xp=20, respawn={6000, 10000} },
    {id=46,  obj=Objects.Hop,    minLvl=30,  itemID=401,  xp=25, respawn={6000, 10000} },
    {id=50,  obj=Objects.Flax,   minLvl=40,  itemID=423,  xp=30, respawn={6000, 10000} },
    {id=159, obj=Objects.Rice,   minLvl=50,  itemID=7018, xp=35, respawn={6000, 10000} },
    {id=52,  obj=Objects.Rye,    minLvl=50,  itemID=532,  xp=35, respawn={6000, 10000} },
    {id=58,  obj=Objects.Malt,   minLvl=50,  itemID=405,  xp=40, respawn={6000, 10000} },
    {id=54,  obj=Objects.Hemp,   minLvl=50,  itemID=425,  xp=45, respawn={6000, 10000} },
}

registerGatherJobSkills(jobID, {toolType=toolType}, gatherSkills)

-- Craft for Shell
local sk122Crafts = {
    -- Flax Seed
    {item=422, ingredients={ {421, 5} } },
    -- Hemp Seed
    {item=427, ingredients={ {428, 5} } },
}

-- Craft for Grind
local sk47Crafts = {
    -- Wheat Flour
    {item=285, ingredients={ {289, 2} } },
    -- Wild Sunflower Oil
    {item=389, ingredients={ {288, 10} } },
    -- Walnut Oil
    {item=390, ingredients={ {392, 5}, {391, 5} } },
    -- Palm Oil
    {item=396, ingredients={ {398, 8} } },
    -- Oilbow Grease
    {item=397, ingredients={ {422, 5}, {392, 5}, {391, 5} } },
    -- Sesame Oil
    {item=399, ingredients={ {287, 10} } },
    -- Barley Flour
    {item=529, ingredients={ {400, 2} } },
    -- Rye Flour
    {item=530, ingredients={ {532, 2} } },
    -- Oats Flour
    {item=531, ingredients={ {533, 2} } },
    -- Malt Flour
    {item=534, ingredients={ {405, 2} } },
    -- Hop Flour
    {item=535, ingredients={ {401, 2} } },
    -- White Flour
    {item=582, ingredients={ {289, 2}, {532, 2} } },
    -- Minx Flour
    {item=583, ingredients={ {532, 2}, {289, 2}, {400, 2} } },
    -- Peasant Flour
    {item=586, ingredients={ {533, 2}, {289, 2}, {532, 2}, {400, 2} } },
    -- Wheatmeal
    {item=587, ingredients={ {401, 2}, {533, 2}, {400, 2}, {532, 2}, {289, 2} } },
    -- Flax Flour
    {item=690, ingredients={ {423, 2} } },
    -- Golden Wheat Flour
    {item=2019, ingredients={ {2018, 1} } },
    -- Bright Hop Flour
    {item=2022, ingredients={ {2021, 1} } },
    -- Storm Flax Flour
    {item=2027, ingredients={ {2026, 1} } },
    -- Resistant Rye Flour
    {item=2030, ingredients={ {2029, 1} } },
    -- Sweet Barley Flour
    {item=2033, ingredients={ {2032, 1} } },
    -- Gold-Bearing Oats Flour
    {item=2037, ingredients={ {2036, 1} } },
    -- Xavier the Baker's Flour
    {item=6672, ingredients={ {289, 2}, {401, 2}, {405, 2}, {533, 2}, {425, 2}, {400, 2}, {532, 2}, {423, 2} } },
    -- Rice Flour
    {item=7068, ingredients={ {7018, 2} } },
}


local requirements = {jobID = jobID, toolType = toolType}
registerCraftSkill(47, sk47Crafts, requirements, ingredientsForCraftJob(jobID), jobID)
registerCraftSkill(122, sk122Crafts, requirements, ingredientsForCraftJob(jobID), jobID)
