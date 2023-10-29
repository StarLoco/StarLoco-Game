
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
        -- TODO Get skills and pickup-able resources
        jobInfo[jobID] = {}
    end

    return jobInfo
end

---@param p Player
---@param skillID number
---@param ingredients table<number, number>
Handlers.players.onCraft = function(p, skillID, ingredients)
    local jobID = SKILL_JOBS[skillID] or 0

    if not SKILLS[skillID] then
        JLogF("unknown skill {}", skillID)
        return
    end




    return
end