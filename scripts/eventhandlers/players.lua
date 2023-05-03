
---@param p Player
---@param type number
---@param isWinner boolean
---@param losers Fighter[]
local function tryCompleteKillQuestObjectives(p, type, isWinner, losers)
    print("BLEH DEBUG")
    if not isWinner then return end
    if type ~= PVMFightType then return end


    for _, qId in ipairs(p:_ongoingQuests()) do
        local quest = QUESTS[qId]
        if quest then
            quest:onEndFightCheck(p, losers)
        end
    end
end

---@param p Player
---@param type number
---@param isWinner boolean
---@param winners Fighter[]
---@param losers Fighter[]
Handlers.players.onFightEnd = function(p, type, isWinner, winners, losers)
    tryCompleteKillQuestObjectives(p, type, isWinner, losers)

end

---@param player Player
---@param questId number
---@param stepId number
---@return table{objectives:number[], previous:number, next:number, question:number}
Handlers.players.onQuestStatusRequest = function(player, questId, stepId)
    local quest = QUESTS[questId]
    if not quest then
        error("quest not found")
    end
    ---@type QuestStep
    local step
    local stepIdx

    -- Find current step
    for idx, s in ipairs(quest.steps) do
        if s.id == stepId then
            stepIdx = idx
            step = s
            break
        end
    end

    -- Get objectives Ids
    local objectives = {}

    if step then
        for _, o in ipairs(step:ObjectivesForPlayer(player)) do
            table.insert(objectives, o.id)
        end
    end

    return {
        objectives = objectives,
        question = step and step.questionId or nil,
        previous = stepIdx and stepIdx > 1 and quest.steps[stepIdx - 1].id or nil,
        next = stepIdx and stepIdx < #(quest.steps) and quest.steps[stepIdx + 1].id or nil,
        isAccount = quest.isAccountBound,
        isRepeatable = quest.isRepeatable
    }
end