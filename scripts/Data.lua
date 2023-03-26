-- Init script for static data VM

-- Define classes
require("./models/NPC")
-- require("MapDef")


-- Load data
require("./data/AdminCommands")
require("./data/AdminGroups")
loadDir("./data/npcs")
-- loadDir("./scripts/maps")
