local jobID = MinerJob
local toolType = 21


--FIXME timing / Reward
local gatherSkills = {
    {id=24,  obj=Objects.Iron,      minLvl=1,   itemID=312,  xp=10, respawn={6000, 10000} },
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

registerGatherJobSkills(jobID, toolType, gatherSkills)

registerCraftSkill(47, {jobID = jobID, toolType = toolType})
registerCraftSkill(122, {jobID = jobID, toolType = toolType})
