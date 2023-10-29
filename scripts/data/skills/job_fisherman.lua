local jobID = FishermanJob
local toolIDs = {596, 1860, 1861, 1862, 1863, 1864, 1865, 1866, 1867, 1868, 8541}
local rareFishChance = 0.1

--FIXME timing
local gatherSkills = {
    {id=136,  obj=Objects.Snapper,        xp=5,  minLvl=0,  respawn={6000, 10000}, fishes={2187}, toolID = 2188 },
    {id=140,  obj=Objects.StrangeShadow,  xp=50, minLvl=0,  respawn={6000, 10000}, fishes={1759} },

    {id=124,  obj=Objects.SmallRiverFish, xp=10, minLvl=0,  respawn={6000, 10000}, fishes={1782, 1844, 603} },
    {id=125,  obj=Objects.RiverFish,      xp=15, minLvl=10, respawn={6000, 10000}, fishes={1844, 603, 1847, 1794} },
    {id=126,  obj=Objects.BigRiverFish,   xp=30, minLvl=40, respawn={6000, 10000}, fishes={603, 1847, 1794, 1779} },
    {id=127,  obj=Objects.GiantRiverFish, xp=45, minLvl=70, respawn={6000, 10000}, fishes={1847, 1794, 1779, 1801} },

    {id=128,  obj=Objects.SmallSeaFish,   xp=10, minLvl=0,  respawn={6000, 10000}, fishes={598, 1757, 1750} },
    {id=129,  obj=Objects.SeaFish,        xp=20, minLvl=20, respawn={6000, 10000}, fishes={1757, 1805, 600} },
    {id=130,  obj=Objects.BigSeaFish,     xp=35, minLvl=50, respawn={6000, 10000}, fishes={1805, 1750, 1784, 600} },
    {id=131,  obj=Objects.GiantSeaFish,   xp=50, minLvl=75, respawn={6000, 10000}, fishes={600, 1805, 602, 1784} },
}

-- Craft for Empty
local sk133Crafts = {
    -- Gutted Breaded Fish
    {item=1751, ingredients={ {1750, 1} } },
    -- Gutted Igloo Fish
    {item=1755, ingredients={ {1754, 1} } },
    -- Gutted Surimice Crab
    {item=1758, ingredients={ {1757, 1} } },
    -- Gutted Exotic Surimice Crab
    {item=1760, ingredients={ {1759, 1} } },
    -- Gutted Kittenfish
    {item=1761, ingredients={ {603, 1} } },
    -- Gutted Tiger Fish
    {item=1763, ingredients={ {1762, 1} } },
    -- Gutted Lard Bass
    {item=1780, ingredients={ {1779, 1} } },
    -- Gutted Grawn
    {item=1781, ingredients={ {598, 1} } },
    -- Gutted Gudgeon
    {item=1783, ingredients={ {1782, 1} } },
    -- Gutted Blue Skate
    {item=1785, ingredients={ {1784, 1} } },
    -- Gutted Horror Grawn
    {item=1787, ingredients={ {1786, 1} } },
    -- Gutted Farle's Ray
    {item=1789, ingredients={ {1788, 1} } },
    -- Gutted Kiye Gudgeon
    {item=1791, ingredients={ {1790, 1} } },
    -- Gutted Siktrin Bass
    {item=1793, ingredients={ {1792, 1} } },
    -- Gutted Ediem Carp
    {item=1795, ingredients={ {1794, 1} } },
    -- Gutted Small Sandy Carp
    {item=1797, ingredients={ {1796, 1} } },
    -- Gutted Kralove
    {item=1798, ingredients={ {600, 1} } },
    -- Gutted Unique Kralove
    {item=1800, ingredients={ {1799, 1} } },
    -- Gutted Perch
    {item=1802, ingredients={ {1801, 1} } },
    -- Gutted Kittenperch
    {item=1804, ingredients={ {1803, 1} } },
    -- Gutted Shiny Sardine
    {item=1806, ingredients={ {1805, 1} } },
    -- Gutted Dark Sardine
    {item=1808, ingredients={ {1807, 1} } },
    -- Gutted Trout
    {item=1845, ingredients={ {1844, 1} } },
    -- Gutted Pike
    {item=1848, ingredients={ {1847, 1} } },
    -- Gutted Tupe-Halett Pike
    {item=1851, ingredients={ {1849, 1} } },
    -- Gutted Sickle-Hammerhead Shark
    {item=1852, ingredients={ {602, 1} } },
    -- Gutted Open-Market Shark
    {item=1854, ingredients={ {1853, 1} } },
    -- Gutted Ancestral Trout
    {item=1976, ingredients={ {1846, 1} } },
}

registerCraftSkill(133, sk133Crafts,{jobID = jobID, toolIDs=toolIDs}, ingredientsForCraftJob(jobID), jobID)

local successChanceForSkill = function(sk, p)
    -- Snapper
    if sk.id == 136 then return 100 end

    local jlvl = p:jobLevel(jobID)

    if jlvl < 30 then
        -- TODO: Find official chance
        return 40
    end

    -- 100% success chance at certain times
    local _, _, _, h, _, _ = World:datetime()
    if h >= 23 or h < 3 or (h >= 6 and h < 9) or (h >= 18 and h < 21) then
        return 100
    end

    return 66.6
end

local rewardForSkill = function(sk)
    ---@param p Player
    return function(p)
        local success = p:getCtxVal("job_success")
        if not success then return end

        local lvlDiff = p:jobLevel(jobID) - sk.minLvl
        local quantity = math.random(1, 2 + math.floor(lvlDiff / 5))

        -- TODO: Support rareFishChance

        local itemID = sk.fishes[math.random(#sk.fishes)]

        gatherSkillAddItem(p, itemID, quantity)
        p:addJobXP(jobID, sk.xp)
    end
end

local durationForSkill = function(sk)
    return function(p)
        local lvlDiff = p:jobLevel(jobID) - sk.minLvl
        local success = (math.random()*100) <= successChanceForSkill(sk, p)

        local duration = 16000 - 100 * lvlDiff
        if success then  duration = duration/2  end

        p:setCtxVal("job_success", success)

        return duration
    end
end

for _, sk in pairs(gatherSkills) do
    local req = {jobID = jobID, jobLvl = sk.minLvl, toolIDs=toolIDs}
    if sk.toolID then req.toolIDs = {sk.toolID} end

    registerGatherSkill(
        sk.id,
        nil,
        durationForSkill(sk),
        rewardForSkill(sk),
        respawnBetweenMillis(sk.respawn[1], sk.respawn[2]),
        req
    )
end