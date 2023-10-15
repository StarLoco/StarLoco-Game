local jobID = AlchemistJob
local toolIDs = {1473}

--FIXME timing / Reward
local gatherSkills = {
    {id=68,  obj=Objects.Flax,            minLvl=1,   itemID=421,  xp=10, respawn={6000, 10000} },
    {id=69,  obj=Objects.Hemp,            minLvl=10,  itemID=428,  xp=15, respawn={6000, 10000} },
    {id=71,  obj=Objects.FiveLeafClover,  minLvl=20,  itemID=395,  xp=20, respawn={6000, 10000} },
    {id=72,  obj=Objects.WildMint,        minLvl=30,  itemID=380,  xp=25, respawn={6000, 10000} },
    {id=73,  obj=Objects.FreyesqueOrchid, minLvl=40,  itemID=593,  xp=30, respawn={6000, 10000} },
    {id=74,  obj=Objects.Edelweiss,       minLvl=50,  itemID=594,  xp=35, respawn={6000, 10000} },
    {id=160, obj=Objects.Pandkin,         minLvl=50,  itemID=7059, xp=35, respawn={6000, 10000} },
}

registerGatherJobSkills(jobID, {toolIDs=toolIDs}, gatherSkills)

registerCraftSkill(47, {jobID = jobID, toolIDs=toolIDs})
registerCraftSkill(122, {jobID = jobID, toolIDs=toolIDs})
