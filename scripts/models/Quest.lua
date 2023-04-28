---@type table<number, Quest>
QUESTS = {}
QUEST_STEPS = {}

ExtraClipSimpleQuest = 4
ExtraClipAccountQuest= 6
ExtraClipRepeatableQuest = 7

---@class Quest
---@field id number
---@field steps QuestStep[]
---@field isRepeatable boolean defaults to false
---@field isAccountBound boolean defaults to false -- Not supported for now
Quest = {}
Quest.__index = Quest

setmetatable(Quest, {
    __call = function (_, id, steps)
        local self = setmetatable({}, Quest)
        self.id = id
        self.steps = steps
        self.minLevel = 0
        self.isRepeatable = false
        self.isAccountBound = false

        QUESTS[id] = self
        return self
    end,
})

---@param p Player
function Quest:availableTo(p)
    return p:questAvailable(self.id)
end


---@param minLevel number
---@param questFinished number
---@param reqBreed number[]|nil
---@return fun(q:Quest, p:Player):boolean
function questRequirements(minLevel, questFinished, reqBreed)
    ---@param q Quest
    ---@param p Player
    return function(q, p)
        if not p:questAvailable(q.id) then
            return false
        end
        if p:level() < minLevel then
            return false
        end

        if questFinished ~= 0 and not p:questFinished(questFinished) then
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
    __call = function (_, id, questionId)
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
    if exp <0 then
        error("quest reward cannot remove exp")
    end
    if kamas <0 then
        error("quest reward cannot remove kamas")
    end
    ---@param p Player
    return function(p)
        p:addXP(exp)
        p:modKamas(kamas)
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
    __call = function (_, id, params)
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
    __call = function (_, id, npcId)
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
    __call = function (_, id, npcId, itemId, quantity)
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
    __call = function (_, id, npcId, itemId, quantity)
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
    __call = function (_, id, mapId)
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
    __call = function (_, id, areaId)
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
    __call = function (_, id, monsterId, amount)
        local self = setmetatable({}, KillMonsterSingleFightObjective)
        self.id = id
        self.type = KillMonsterSingleFightObjectiveType
        self.monsterId = monsterId
        self.amount = amount

        return self
    end,
})

---@class KillMonsterObjective:QuestObjective
---@field monsterId number
KillMonsterObjective = {}
KillMonsterObjective.__index = KillMonsterObjective

setmetatable(KillMonsterObjective, {
    __call = function (_, id, monsterId)
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
    __call = function (_, id, objectId)
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
    __call = function (_, id, npcId)
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
    __call = function (_, id, npcId, mapId)
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
    __call = function (_, id)
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
    __call = function (_, id, npcId, monsterId, amount)
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
    __call = function (_, id, unk)
        local self = setmetatable({}, EliminateObjective)
        self.id = id
        self.type = EliminateObjectiveType
        self.unk = unk

        return self
    end,
})