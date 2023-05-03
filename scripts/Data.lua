-- Init script for static data VM

-- Load constant data
requireReload("./data/AdminCommands")
requireReload("./data/AdminGroups")
requireReload("./data/Experience")
requireReload("./data/FightTypes")
requireReload("./data/GearSlots")
requireReload("./data/Jobs")

-- Define classes
requireReload("./models/NPC")
requireReload("./models/MapDef")
requireReload("./models/Quest")

-- Load instances
loadDir("./data/npcs")
loadDir("./data/maps")
loadDir("./data/quests")

loadDir("./data/dungeons") -- Always load last

-- Load event handlers
loadDir("./eventhandlers")