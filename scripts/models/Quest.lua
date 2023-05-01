---@type table<number, Quest>
QUESTS = {}
---@type table<number, QuestStep>
QUEST_STEPS = {}

ExtraClipSimpleQuest = 4
ExtraClipAccountQuest = 6
ExtraClipRepeatableQuest = 7

---@class Quest
---@field id number
---@field steps QuestStep[]
---@field isRepeatable boolean defaults to false
---@field isAccountBound boolean defaults to false
Quest = {}
Quest.__index = Quest

setmetatable(Quest, {
    __call = function(_, id, steps)
        local self = setmetatable({}, Quest)
        self.id = id
        self.steps = steps
        self.isRepeatable = false
        self.isAccountBound = false

        QUESTS[id] = self
        return self
    end,
})

---@param sId number
---@return number,QuestStep
function Quest:step(sId)
    for i, s in ipairs(self.steps) do
        if s.id == sId then
            return i, s
        end
    end
end

---@param p Player
function Quest:availableTo(p)
    return p:_questAvailable(self.id)
end

---@param p Player
function Quest:ongoingFor(p)
    return p:_questOngoing(self.id)
end

---@param p Player
---@param npcId number
function Quest:startFor(p, npcId)
    if not self:availableTo(p) then return false end
    if p:_startQuest(self.id, self.steps[1].id, self.isAccountBound) then
        p:map():updateNpcExtraForPlayer(npcId, p)
    end
    return true
end

---@param p Player
---@param id number
---@return boolean
function Quest:hasCompletedObjective(p, id)
    return table.contains(p:_completedObjectives(self.id), id)
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
---@param losers Fighter[]
---@return boolean true if should complete objective
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

        if questFinished ~= 0 and not p:_questFinished(questFinished) then
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
---@field questionId number
---@field objectives QuestObjective[]|fun(p:Player):QuestObjective[]
---@field rewardFn fun(p:Player)
QuestStep = {}
QuestStep.__index = QuestStep

setmetatable(QuestStep, {
    __call = function(_, id, questionId)
        local self = setmetatable({}, QuestStep)
        self.id = id
        self.questionId = questionId

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

---@class QuestObjective
---@field id number
---@field type number
---
local QuestObjective = {}
QuestObjective.__index = QuestObjective

---@class GenericQuestObjective : QuestObjective
---@field params table
GenericQuestObjective = {}
GenericQuestObjective.__index = GenericQuestObjective

setmetatable(GenericQuestObjective, {
    __call = function(_, id, params)
        local self = setmetatable({}, GenericQuestObjective)
        self.id = id
        self.type = GenericObjectiveType
        self.params = params

        return self
    end,
})

---@class TalkWithQuestObjective:QuestObjective
---@field npcId number
TalkWithQuestObjective = {}
TalkWithQuestObjective.__index = TalkWithQuestObjective

setmetatable(TalkWithQuestObjective, {
    __call = function(_, id, npcId)
        local self = setmetatable({}, TalkWithQuestObjective)
        self.id = id
        self.type = TalkWithObjectiveType
        self.npcId = npcId

        return self
    end,
})

---@class ShowItemObjective:QuestObjective
---@field npcId number
---@field itemId number
---@field quantity number
ShowItemObjective = {}
ShowItemObjective.__index = ShowItemObjective

setmetatable(ShowItemObjective, {
    __call = function(_, id, npcId, itemId, quantity)
        local self = setmetatable({}, ShowItemObjective)
        self.id = id
        self.type = ShowItemObjectiveType
        self.npcId = npcId
        self.itemId = itemId
        self.quantity = quantity

        return self
    end,
})

---@class BringItemObjective:QuestObjective
---@field npcId number
---@field itemId number
---@field quantity number
BringItemObjective = {}
BringItemObjective.__index = BringItemObjective

setmetatable(BringItemObjective, {
    __call = function(_, id, npcId, itemId, quantity)
        local self = setmetatable({}, BringItemObjective)
        self.id = id
        self.type = BringItemObjectiveType
        self.npcId = npcId
        self.itemId = itemId
        self.quantity = quantity

        return self
    end,
})

---@class DiscoverMapObjective:QuestObjective
---@field mapId number
DiscoverMapObjective = {}
DiscoverMapObjective.__index = DiscoverMapObjective

setmetatable(DiscoverMapObjective, {
    __call = function(_, id, mapId)
        local self = setmetatable({}, DiscoverMapObjective)
        self.id = id
        self.type = DiscoverMapObjectiveType
        self.mapId = mapId

        return self
    end,
})

---@class DiscoverAreaObjective:QuestObjective
---@field areaId number
DiscoverAreaObjective = {}
DiscoverAreaObjective.__index = DiscoverAreaObjective

setmetatable(DiscoverAreaObjective, {
    __call = function(_, id, areaId)
        local self = setmetatable({}, DiscoverAreaObjective)
        self.id = id
        self.type = DiscoverAreaObjectiveType
        self.areaId = areaId

        return self
    end,
})

---@class KillMonsterSingleFightObjective:QuestObjective
---@field monsterId number
---@field amount number
KillMonsterSingleFightObjective = {}
KillMonsterSingleFightObjective.__index = KillMonsterSingleFightObjective

setmetatable(KillMonsterSingleFightObjective, {
    __call = function(_, id, monsterId, amount)
        local self = setmetatable({}, KillMonsterSingleFightObjective)
        self.id = id
        self.type = KillMonsterSingleFightObjectiveType
        self.monsterId = monsterId
        self.amount = amount

        return self
    end,
})

---@param losers Fighter[]
---@return boolean true if should complete objective
function KillMonsterSingleFightObjective:onEndFightCheck(losers)
    return countFightersForMobId(losers, self.monsterId) >= self.amount
end

---@class KillMonsterObjective:QuestObjective
---@field monsterId number
KillMonsterObjective = {}
KillMonsterObjective.__index = KillMonsterObjective

setmetatable(KillMonsterObjective, {
    __call = function(_, id, monsterId)
        local self = setmetatable({}, KillMonsterObjective)
        self.id = id
        self.type = KillMonsterObjectiveType
        self.monsterId = monsterId

        return self
    end,
})

---@class UseObjectObjective:QuestObjective
---@field objectId number
UseObjectObjective = {}
UseObjectObjective.__index = UseObjectObjective

setmetatable(UseObjectObjective, {
    __call = function(_, id, objectId)
        local self = setmetatable({}, UseObjectObjective)
        self.id = id
        self.type = UseObjectObjectiveType
        self.objectId = objectId

        return self
    end,
})

---@class TalkAgainToObjective:QuestObjective
---@field npcId number
TalkAgainToObjective = {}
TalkAgainToObjective.__index = TalkAgainToObjective

setmetatable(TalkAgainToObjective, {
    __call = function(_, id, npcId)
        local self = setmetatable({}, TalkAgainToObjective)
        self.id = id
        self.type = TalkAgainToObjectiveType
        self.npcId = npcId

        return self
    end,
})

---@class EscortObjective:QuestObjective
---@field npcId number
---@field mapId number
EscortObjective = {}
EscortObjective.__index = EscortObjective

setmetatable(EscortObjective, {
    __call = function(_, id, npcId, mapId)
        local self = setmetatable({}, EscortObjective)
        self.id = id
        self.type = EscortObjectiveType
        self.npcId = npcId
        self.mapId = mapId

        return self
    end,
})

---@class WinDuelObjective:QuestObjective
-- What is the param for that objective ?
WinDuelObjective = {}
WinDuelObjective.__index = WinDuelObjective

setmetatable(WinDuelObjective, {
    __call = function(_, id)
        local self = setmetatable({}, WinDuelObjective)
        self.id = id
        self.type = WinDuelObjectiveType
        return self
    end,
})

---@class BringSoulsObjective:QuestObjective
---@field npcId number
---@field monsterId number
---@field amount number
BringSoulsObjective = {}
BringSoulsObjective.__index = BringSoulsObjective

setmetatable(BringSoulsObjective, {
    __call = function(_, id, npcId, monsterId, amount)
        local self = setmetatable({}, BringSoulsObjective)
        self.id = id
        self.type = BringSoulsObjectiveType
        self.npcId = npcId
        self.monsterId = monsterId
        self.amount = amount

        return self
    end,
})

---@class EliminateObjective:QuestObjective
---@field unk any
EliminateObjective = {}
EliminateObjective.__index = EliminateObjective

setmetatable(EliminateObjective, {
    __call = function(_, id, unk)
        local self = setmetatable({}, EliminateObjective)
        self.id = id
        self.type = EliminateObjectiveType
        self.unk = unk

        return self
    end,
})
