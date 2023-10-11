--FIXME timing / Reward
--FIXME Reward special cereals sometimes

local skills = {
    {id=45,  obj=Objects.Wheat,  minLvl=1,   itemID=289,  xp=10, respawn={6000, 10000} },
    {id=53,  obj=Objects.Barley, minLvl=10,  itemID=400,  xp=15, respawn={6000, 10000} },
    {id=57,  obj=Objects.Oats,   minLvl=20,  itemID=533,  xp=20, respawn={6000, 10000} },
    {id=46,  obj=Objects.Hop,    minLvl=30,  itemID=401,  xp=25, respawn={6000, 10000} },
    {id=50,  obj=Objects.Flax,   minLvl=40,  itemID=423,  xp=30, respawn={6000, 10000} },
    {id=159, obj=Objects.Rice,   minLvl=50,  itemID=7018, xp=35, respawn={6000, 10000} },
    {id=52,  obj=Objects.Rye,    minLvl=50,  itemID=532,  xp=35, respawn={6000, 10000} },
    {id=58,  obj=Objects.Malt,   minLvl=50,  itemID=405,  xp=40, respawn={6000, 10000} },
    {id=54,  obj=Objects.Hemp,   minLvl=50,  itemID=425,  xp=45, respawn={6000, 10000} },
}

registerGatherJobSkills(FarmerJob, 22, skills)