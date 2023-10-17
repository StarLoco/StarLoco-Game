
---@param p Player
---@param cellID number
---@param skillID number
Handlers.players.onSkillUse = function(p, cellID, skillID)
    if not SKILLS[skillID] then
        JLogF("unknown skill {}", skillID)
        return
    end
    SKILLS[skillID](p, cellID)
    return
end

---@param p Player
Handlers.players.onJobSkillRequest = function(p)
    local jobInfo = {}

    for _, jobID in pairs(p:jobs()) do
        jobInfo[jobID] = {}
    end

    return jobInfo
end
