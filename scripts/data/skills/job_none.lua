-- SKILLS NOT LINKED TO A JOB

-- No skill object use
SKILLS[0] = function(p, cellID)
    print("SKILL 0 USED")
    -- Use map object without skill
    local mapDef = p:map():def()
    local handler = mapDef.onObjectUse[cellID]
    if not handler then
        print("NO HANDLER FOR CELL", mapDef.id, cellID)
        return false
    end

    if type(handler) ~= "function" then
        error("Non-skill map object use handler must be functions")
    end
    return handler(p)
end

-- Save Zaap
SKILLS[44] = function(p, _)
    local md = p:map():def()
    p:savePosition(md.id, md.zaapCell)
end

-- Draw water from well
registerGatherSkill(102,
    4,
    function(_) return 1500 end,
    function(p)
        -- 311: Water
        gatherSkillAddItem(p, 311, math.random(1, 10))
    end,
    respawnBetweenMillis(120000, 420000)
)

-- Use Zaap
SKILLS[114] = function(p, _) p:openZaap() return true end

-- Use garbage bin
SKILLS[153] = function(p, cellID)
    p:openTrunk(cellID) return true end

-- Use Switch
SKILLS[179] = function(p, cellId)
    local switchHandler = p:map():def().switches[cellId]
    if not switchHandler then return end
    if p:map():getAnimationState(cellId) ~= AnimStates.READY then return end

    if switchHandler(p) then
        p:map():setAnimationState(cellId, AnimStates.IN_USE)
    end
end

-- Use Astrub Breed Statue
SKILLS[183] = function(p, _)
    teleportByBreed(p, INCARNAM_STATUES)
end
