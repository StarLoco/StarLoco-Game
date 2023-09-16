-- SKILLS NOT LINKED TO A JOB

-- Save Zaap
SKILLS[44] = function(p, _)
    local md = p:map():def()
    p:savePosition(md.id, md.zaapCell)
    return true
end

-- Use Zaap
SKILLS[114] = function(p, _) p:openZaap() return true end

-- Use Switch
SKILLS[179] = function(p, cellId)
    local switchHandler = p:map():def().switches[cellId]
    if not switchHandler then return end
    if p:map():getAnimationState(cellId) ~= AnimStates.READY then return end

    if switchHandler(p) then
        p:map():setAnimationState(cellId, AnimStates.IN_USE)
    end
end
