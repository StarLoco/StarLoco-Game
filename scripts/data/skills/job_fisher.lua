local jobID = FisherJob
local toolType = 22


--FIXME timing / Reward
local gatherSkills = {
    {id=45,  obj=Objects.SmallSeaFish,      minLvl=1,   itemID=289,  xp=10, respawn={6000, 10000} },
    {id=53,  obj=Objects.AggressiveSalmoon, minLvl=10,  itemID=400,  xp=15, respawn={6000, 10000} },
    {id=57,  obj=Objects.Octopwus,          minLvl=20,  itemID=533,  xp=20, respawn={6000, 10000} },
    {id=46,  obj=Objects.RiverFish,         minLvl=30,  itemID=401,  xp=25, respawn={6000, 10000} },
    {id=50,  obj=Objects.SmallSeaFish,      minLvl=40,  itemID=423,  xp=30, respawn={6000, 10000} },
    {id=159, obj=Objects.BigRiverFish,      minLvl=50,  itemID=7018, xp=35, respawn={6000, 10000} },
    {id=52,  obj=Objects.SeaFish,           minLvl=50,  itemID=532,  xp=35, respawn={6000, 10000} },
    {id=58,  obj=Objects.BigSeaFish,        minLvl=50,  itemID=405,  xp=40, respawn={6000, 10000} },
    {id=54,  obj=Objects.GiantRiverFish,    minLvl=50,  itemID=425,  xp=45, respawn={6000, 10000} },
    {id=54,  obj=Objects.SludgyTrout,       minLvl=50,  itemID=425,  xp=45, respawn={6000, 10000} },
    {id=54,  obj=Objects.GiantSeaFish,      minLvl=50,  itemID=425,  xp=45, respawn={6000, 10000} },
}

registerGatherJobSkills(jobID, {toolType=toolType}, gatherSkills)

registerCraftSkill(47, {jobID = jobID, toolType = toolType})
registerCraftSkill(122, {jobID = jobID, toolType = toolType})
