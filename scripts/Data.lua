-- Init script for static data VM

-- Load constant data
require("./data/AdminCommands")
require("./data/AdminGroups")
require("./data/Experience")
require("./data/FightTypes")
require("./data/GearSlots")
require("./data/Jobs")

-- Define classes
require("./models/NPC")
require("./models/MapDef")
require("./models/Quest")

-- Load instances

loadDir("./data/npcs")
loadDir("./data/maps")
loadDir("./data/quests")

loadDir("./data/dungeons") -- Always load last