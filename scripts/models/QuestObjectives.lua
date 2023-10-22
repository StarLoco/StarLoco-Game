
---@class QuestObjective
---@field id number
---@field type number
---
local QuestObjective = {}
QuestObjective.__index = QuestObjective

---@class GenericQuestObjective : QuestObjective
---@field params table

---@type fun(id:number, params:table):GenericQuestObjective
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

---@type fun(id:number, npcId:number):TalkWithQuestObjective
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

---@type fun(id:number, npcId:number, itemId:number, quantity:number):ShowItemObjective
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

---@type fun(id:number, npcId:number, itemId:number, quantity:number):BringItemObjective
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

---@param p Player
---@param npcID number
---@return boolean true if should complete objective
function BringItemObjective:canBringItemCheck(p, npcID)
    if npcID ~= self.npcId then return false end

    return p:getItem(self.itemId, self.quantity) ~= nil
end

---@param p Player
---@param npcID number
---@return boolean true if should complete objective
function BringItemObjective:onBringItemCheck(p, npcID)
    if npcID ~= self.npcId then return false end

    return p:consumeItem(self.itemId, self.quantity)
end


---@class DiscoverMapObjective:QuestObjective
---@field mapId number

---@type fun(id:number, mapId:number):DiscoverMapObjective
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

---@param p Player
---@return boolean true if should complete objective
function DiscoverMapObjective:onMapEnterCheck(p)
    return p:mapID() == self.mapId
end

---@class DiscoverAreaObjective:QuestObjective
---@field areaId number

---@type fun(id:number, areaId:number):DiscoverAreaObjective
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

---@param p Player
---@return boolean true if should complete objective
function DiscoverAreaObjective:onMapEnterCheck(p)
    return p:map():area():id() == self.areaId
end

---@class KillMonsterSingleFightObjective:QuestObjective
---@field monsterId number
---@field amount number

---@type fun(id:number, monsterId:number, amount:number):KillMonsterSingleFightObjective
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

---@type fun(id:number, monsterId:number):KillMonsterObjective
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

---@param losers Fighter[]
---@return boolean true if should complete objective
function KillMonsterObjective:onEndFightCheck(losers)
    return countFightersForMobId(losers, self.monsterId) >= self.amount
end

---@class UseObjectObjective:QuestObjective
---@field objectId number

---@type fun(id:number, objectId:number):UseObjectObjective
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

---@type fun(id:number, npcId:number):TalkAgainToObjective
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

---@type fun(id:number, npcId:number, mapId:number):EscortObjective
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

---@type fun(id:number):WinDuelObjective
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

---@type fun(npcId:number, monsterId:number, amount:number):BringSoulsObjective
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
-- Used for PvP stalk quest ?

---@type fun(id:number, unk:any):EliminateObjective
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
