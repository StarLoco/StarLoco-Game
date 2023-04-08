-- Init script for static data VM

-- Define classes
require("./models/NPC")
require("./models/MapDef")


-- Load data
require("./data/AdminCommands")
require("./data/AdminGroups")
require("./data/Experience")
require("./data/FightTypes")
require("./data/GearSlots")
require("./data/Jobs")

loadDir("./data/npcs")
loadDir("./data/maps")

loadDir("./data/dungeons") -- Always load last