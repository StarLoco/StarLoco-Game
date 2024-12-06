---@type table<number, Quest>
QUESTS = {}
---@type table<number, QuestStep>
QUEST_STEPS = {}

ExtraClipSimpleQuest = 4
ExtraClipAccountQuest = 6
ExtraClipRepeatableQuest = 7

---@alias QuestStepRewardFn fun(p:Player)

---@class Quest
---@field id number
---@field steps QuestStep[]
---@field isRepeatable boolean defaults to false
---@field isAccountBound boolean defaults to false
---@field startFromDocHref number[] defaults to empty

---@type fun(id:number, steps:QuestStep[]):Quest
Quest = {}
Quest.__index = Quest

setmetatable(Quest, {
    __call = function(_, id, steps)
        local self = setmetatable({}, Quest)
        self.id = id
        self.steps = steps
        self.isRepeatable = false
        self.isAccountBound = false
        self.startFromDocHref = {}

        QUESTS[id] = self
        return self
    end,
})

---@param sId number
---@return number,QuestStep -- Index,QuestStep
function Quest:step(sId)
    for i, s in ipairs(self.steps) do
        if s.id == sId then
            return i, s
        end
    end
end

---@param p Player
---@return boolean
function Quest:availableTo(p)
    return p:_questAvailable(self.id)
end

---@param p Player
---@return boolean
function Quest:ongoingFor(p)
    return p:_questOngoing(self.id)
end

---@param p Player
---@return QuestStep
function Quest:currentStepFor(p)
    local sId = p:_currentStep(self.id)
    if not sId then return nil end
    local _, step = self:step(sId)
    return step
end

---@param p Player
---@param npcId number
---@return boolean worked
function Quest:startFor(p, npcId)
    if not self:availableTo(p) then return false end
    if p:_startQuest(self.id, self.steps[1].id, self.isAccountBound) then
        if npcId then
            p:map():updateNpcExtraForPlayer(npcId, p)
        end
    end
    return true
end

---@param p Player
---@param id number
---@return boolean
function Quest:hasCompletedObjective(p, id)
    -- Currently, this would crash when called with a finished quest (there are no more objectives)
    -- Should we just return true when the quest is finished ?
    return table.contains(p:_completedObjectives(self.id), id)
end

---@param p Player
---@param ids number[]
---@return boolean
function Quest:hasCompletedObjectives(p, ids)
    -- Currently, this would crash when called with a finished quest (there are no more objectives)
    -- Should we just return true when the quest is finished ?
    local completed = p:_completedObjectives(self.id)

    for _, o in pairs(ids) do
        if not table.contains(completed, o) then
            return false
        end
    end

    return true
end

---@param p Player
---@return boolean
function Quest:finishedBy(p)
    return p:_questFinished(self.id)
end

---@param p Player
---@param id number
function Quest:completeObjective(p, id)
    self:completeObjectives(p, {id})
end

---@param p Player
---@param ids number[]
function Quest:completeObjectives(p, ids)
    local stepIdx, step = self:step(p:_currentStep(self.id))
    if not step then
        error("player doesn't have the quest")
    end
    for _, id in ipairs(ids) do
        if not p:_completeObjective(self.id, id) then
            return
        end
    end

    -- Let's see if the player is done with the current step
    local uncompletedObjectives = self:uncompletedObjectives(p)
    if #uncompletedObjectives ~= 0 then
        JLogF("PLAYER NOT DONE WITH OBJECTIVE {}", uncompletedObjectives[1])
        return
    end

    -- All objectives are completed, reward + next step
    local nextStep = stepIdx < #(self.steps) and self.steps[stepIdx + 1] or nil

    step.rewardFn(p)
    if not nextStep then
        -- Quest finished
        p:_completeQuest(self.id, self.isRepeatable)
        return
    end
    -- Go to next step
    p:_setCurrentStep(self.id, nextStep.id)
end

---@param p Player
---@return QuestObjective[]
function Quest:uncompletedObjectives(p)
    local _, step = self:step(p:_currentStep(self.id))
    if not step then return {} end

    local completedObjectives = p:_completedObjectives(self.id)

    local uncompleted = {}
    for _, obj in ipairs(step:ObjectivesForPlayer(p)) do
        if not table.contains(completedObjectives, obj.id) then
            table.insert(uncompleted, obj)
        end
    end
    return uncompleted
end

---@param p Player
---@return number[]
function Quest:uncompletedObjectiveIDs(p)
    local uncompleted = {}
    for _, obj in ipairs(self:uncompletedObjectives(p)) do
        table.insert(uncompleted, obj.id)
    end
    return uncompleted
end

---@param p Player
---@param id number objectiveID
---@return boolean
function Quest:canCompleteObjective(p, id)
    return table.contains(self:uncompletedObjectiveIDs(p), id)
end

---@param p Player
function Quest:onMapEnterCheck(p)
    local justCompleted = {}

    for _, obj in ipairs(self:uncompletedObjectives(p)) do
        if obj.onMapEnterCheck and obj:onMapEnterCheck(p) then
            table.insert(justCompleted, obj.id)
        end
    end

    if #justCompleted == 0 then return end
    self:completeObjectives(p, justCompleted)
end

---@param p Player
---@param losers Fighter[]
function Quest:onEndFightCheck(p, losers)
    local justCompleted = {}

    for _, obj in ipairs(self:uncompletedObjectives(p)) do
        if obj.onEndFightCheck and obj:onEndFightCheck(losers) then
            table.insert(justCompleted, obj.id)
        end
    end

    if #justCompleted == 0 then return end
    self:completeObjectives(p, justCompleted)
end

---Helper function that allow objectives to appear only once the previous objective is completed
---@param questId number
---@param objs QuestObjective[]
---@return fun(p:Player):QuestObjective[]
function Quest:SequentialObjectives(objs)
    ---@param p Player
    return function(p)
        local objectives = {}
        local completed = p:_completedObjectives(self.id)

        for _, obj in ipairs(objs) do
            table.insert(objectives, obj)

            if not table.contains(completed, obj.id) then
                -- This is the first uncompleted objective, return the list now
                return objectives
            end
        end

        return objectives
    end
end

---@param p Player
---@param npcID number
---@return boolean true if success
function Quest:tryCompleteBringItemObjectives(p, npcID)
    -- Make sure we can complete all objectives
    for _, obj in ipairs(self:uncompletedObjectives(p)) do
        if obj.canBringItemCheck then
            if not obj:canBringItemCheck(p, npcID) then
                return false
            end
        end
    end

    -- Consume everything
    local canComplete = {}
    for _, obj in ipairs(self:uncompletedObjectives(p)) do
        if not obj:onBringItemCheck(p, npcID) then
            error("race condition (SHOULD NEVER HAPPEN)")
        else
            table.insert(canComplete, obj.id)
        end
    end

    if #canComplete == 0 then return false end

    self:completeObjectives(p, canComplete)

    return true
end


---@param minLevel number
---@param questFinished number
---@param reqBreed number[]|nil
---@return fun(q:Quest, p:Player):boolean
function questRequirements(minLevel, questFinished, reqBreed)
    ---@param q Quest
    ---@param p Player
    return function(q, p)
        if not p:_questAvailable(q.id) then
            return false
        end
        if p:level() < minLevel then
            return false
        end

        if questFinished and not p:_questFinished(questFinished) then
            return false
        end

        if reqBreed and not table.contains(reqBreed, p:breed()) then
            return false
        end

        return true
    end
end

---@class QuestStep
---@field id number
---@field questionId number defaults to nil
---@field objectives QuestObjective[]|fun(p:Player):QuestObjective[]
---@field rewardFn QuestStepRewardFn

---@type fun(id:number, questionId:number):Quest
QuestStep = {}
QuestStep.__index = QuestStep

setmetatable(QuestStep, {
    __call = function(_, id, questionId)
        local self = setmetatable({}, QuestStep)
        self.id = id
        self.questionId = questionId or nil
        self.objectives = {}

        QUEST_STEPS[id] = self
        return self
    end,
})

---@param p Player
---@return QuestObjective[]
function QuestStep:ObjectivesForPlayer(p)
    if type(self.objectives) == "function" then
        return self.objectives(p)
    end
    return self.objectives
end

---@param exp number
---@param kamas number
---@return QuestStepRewardFn
function QuestBasicReward(exp, kamas)
    if exp < 0 then
        error("quest reward cannot remove exp")
    end
    if kamas < 0 then
        error("quest reward cannot remove kamas")
    end
    ---@param p Player
    return function(p)
        if exp > 0 then p:addXP(exp) end
        if kamas > 0 then p:modKamas(kamas) end
    end
end
