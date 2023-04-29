---@param p Player
---@param winners Fighter[]
---@param losers Fighter[]
Handlers.players.onFightEnd = function(p, winners, losers)
    local isWinner = table.ifind(winners, function(e)
        print(type(e))
    end)

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
        previous = stepIdx > 1 and quest.steps[stepIdx - 1].id or nil,
        next = stepIdx < #(quest.steps) and quest.steps[stepIdx + 1].id or nil,
    }
end