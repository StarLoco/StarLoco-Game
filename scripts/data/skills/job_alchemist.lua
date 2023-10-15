local jobID = AlchemistJob
local toolType = 22


--FIXME timing / Reward
local gatherSkills = {
    {id=50,  obj=Objects.Flax,            minLvl=1,   itemID=423,  xp=10, respawn={6000, 10000} },
    {id=54,  obj=Objects.Hemp,            minLvl=10,  itemID=428,  xp=15, respawn={6000, 10000} },
    {id=71,  obj=Objects.FiveLeafClover,  minLvl=20,  itemID=395,  xp=20, respawn={6000, 10000} },
    {id=72,  obj=Objects.WildMint,        minLvl=30,  itemID=380,  xp=25, respawn={6000, 10000} },
    {id=73,  obj=Objects.FreyesqueOrchid, minLvl=40,  itemID=593,  xp=30, respawn={6000, 10000} },
    {id=74,  obj=Objects.Edelweiss,       minLvl=50,  itemID=594,  xp=35, respawn={6000, 10000} },
    {id=160, obj=Objects.Pandkin,         minLvl=50,  itemID=7059, xp=35, respawn={6000, 10000} },
}

registerGatherJobSkills(jobID, toolType, gatherSkills)

registerCraftSkill(47, {jobID = jobID, toolType = toolType})
registerCraftSkill(122, {jobID = jobID, toolType = toolType})
