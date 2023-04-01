-- Init script for static data VM

-- Define classes
require("./models/NPC")
require("./models/MapDef")


-- Load data
require("./data/AdminCommands")
require("./data/AdminGroups")
require("./data/Experience")

loadDir("./data/npcs")
loadDir("./data/maps")
