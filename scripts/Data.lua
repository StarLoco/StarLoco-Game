-- Init script for static data VM

---@type table<fun>
POST_INITS = {}

-- Load constant data
requireReload("data/AdminCommands")
requireReload("data/AdminGroups")
requireReload("data/Breed")
requireReload("data/ExchangeActions")
requireReload("data/Experience")
requireReload("data/FightTypes")
requireReload("data/GearSlots")
requireReload("data/Animations")
requireReload("data/InteractiveObjects")
requireReload("data/Jobs")
requireReload("data/ObjectiveTypes")
requireReload("data/Skills")

-- Define classes
requireReload("models/NPC")
requireReload("models/MapDef")
requireReload("models/Quest")
requireReload("models/QuestObjectives")
requireReload("models/InteractiveObjectDef")

-- Load instances
loadPack("data/animations")
loadPack("data/objects") -- Always load after animations
loadPack("data/skills") -- Always load after objects
loadPack("data/npcs")
loadPack("data/maps")
loadPack("data/quests")
loadPack("data/dungeons") -- Always load after maps

-- Load other gameplay features
requireReload("data/Dopples")
requireReload("data/Wanted")


-- Load event handlers
loadPack("eventhandlers")

-- Register Maps to Java
for _, map in pairs(MAPS) do
    RegisterMapDef(map)
end

-- Run POST_INITS handlers
for _, fn in ipairs(POST_INITS) do
    fn()
end
