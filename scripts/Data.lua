-- Init script for static data VM

-- Load constant data
requireReload("./data/AdminCommands")
requireReload("./data/AdminGroups")
requireReload("./data/Experience")
requireReload("./data/FightTypes")
requireReload("./data/GearSlots")
requireReload("./data/InteractiveObjects")
requireReload("./data/Jobs")
requireReload("./data/ObjectiveTypes")

-- Define classes
requireReload("./models/NPC")
requireReload("./models/MapDef")
requireReload("./models/Quest")
requireReload("./models/InteractiveObjectDef")

-- Load instances
loadPack("./data/notloaded/npcs/untested")
loadPack("./data/npcs")
loadPack("./data/maps")
loadPack("./data/quests")
loadPack("./data/objects")
loadPack("./data/skills")

loadPack("./data/dungeons") -- Always load last

-- Load event handlers
loadPack("./eventhandlers")