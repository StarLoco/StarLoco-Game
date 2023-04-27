---@class Dungeon
---@field keyID number
---@field questionID number
---@field keychainResponseID number
---@field keyResponseID number
---@field tpDest number[] mapID,cellID
Dungeon = {}
Dungeon.__index = Dungeon

local keychainTemplateID = 10207
local keychainStatID = 814

setmetatable(Dungeon, {
    ---@param keyID number
    ---@param questionID number
    ---@param keychainResponseID number
    ---@param keyResponseID number
    ---@param tpDest number[] mapID,cellID
    __call = function (cls, keyID, questionID, keychainResponseID, keyResponseID, tpDest)
        local self = setmetatable({}, Dungeon)
        self.keyID = keyID
        self.questionID = questionID
        self.keychainResponseID = keychainResponseID
        self.keyResponseID = keyResponseID
        self.tpDest = tpDest

        return self
    end,
})
---@param player Player
---@param keyID number
---@return boolean
function hasKeyChainFor(player, keyID)
    local item = player:getItem(keychainTemplateID, 1)
    if not item then return false end
    -- Keys are stored as hex value in keychain
    return item:hasTxtStat(keychainStatID, hex(keyID))
end

function useKeyChainFor(player, keyID)
    local item = player:getItem(keychainTemplateID, 1)
    if not item then return false end
    -- Keys are stored as hex value in keychain
    return item:consumeTxtStat(player, keychainStatID, hex(keyID))
end

---@param player Player
---@return boolean
function Dungeon:hasKeyChain(player)
    return hasKeyChainFor(player, self.keyID)
end

---@param player Player
---@return boolean
function Dungeon:useKeyChain(player)
    return useKeyChainFor(player, self.keyID)
end

---@param player Player
---@param answer number
function Dungeon:onTalkToGateKeeper(player, answer)
    if answer == 0 then
        -- Check if player has item / keychain
        local responses = {}
        if player:getItem(self.keyID, 1) then table.insert(responses, self.keyResponseID) end
        if self:hasKeyChain(player) then table.insert(responses, self.keychainResponseID) end
        player:ask(self.questionID, responses)
    elseif answer == self.keyResponseID then
        -- Use Key then teleport
        if player:consumeItem(self.keyID, 1) then
            player:teleport(self.tpDest[1], self.tpDest[2])
        end
         player:endDialog()
    elseif answer == self.keychainResponseID then
        -- Use Keychain then teleport
        if self:useKeyChain(player) then
            player:teleport(self.tpDest[1], self.tpDest[2])
        end
        player:endDialog()
    end
end

GobballDungeon = Dungeon(1568, 660, 6602, 564, {2073, 409})
SmithDungeon = Dungeon(1569, 7069, 6602, 6632, {2085, 408})
SkeletonDungeon = Dungeon(1570, 7070, 6602, 7708, {2110, 118})
BworkerDungeon = Dungeon(6884, 7071, 6602, 6636, {7530, 385})
PandikazesDungeon = Dungeon(7309, 2940, 6603, 2576, {8282, 444})
BulbDungeon = Dungeon(7310, 7072, 6639, 6638, {8289, 317})
FirefouxDungeon = Dungeon(7312, 2943, 6605, 2579, {8279, 408}) --TODO: Il me semble que sur offi lorsqu'on parle au pnj au début du donjon on perd pas la clé directement > à recheck
TreechnidDungeon = Dungeon(7557, 3272, 6605, 2927, {8713, 292})
KoulosseDungeon = Dungeon(7908, 3119, 6602, 2742, {8963, 350})
LordCrowDungeon = Dungeon(7926, 3200, 6602, 2829, {9589, 436})
CracklerDungeon = Dungeon(7927, 3163, 6602, 2781, {9578, 384})
SkeunkDungeon = Dungeon(8073, 3138, 6602, 2762, {8969, 393})
BworkDungeon = Dungeon(8135, 3172, 6602, 2794, {9750, 351})
ScaraleafDungeon = Dungeon(8139, 3179, 6602, 2947, {9775, 436})
TofuDungeon = Dungeon(7918, 3175, 6602, 2797, {9521, 451})
FieldDungeon = Dungeon(8143, 3178, 6602, 2802, {9768, 306})
CanidaeDungeon = Dungeon(8156, 3177, 6602, 2801, {9645, 451})
SoftOakDungeon = Dungeon(8436, 3276, 6602, 2930, {8714, 112})
SandyDungeon = Dungeon(8437, 3289, 6602, 2944, {10156, 247})
BrakmarRatDungeon = Dungeon(8438, 3294, 6618, 2950, {10194, 450})
BontaRatDungeon = Dungeon(8439, 3292, 6619, 2947, {10200, 50})
IncarnamDungeon = Dungeon(8545, 3828, 3358, 3359, {10360, 364})
BlopDungeon = Dungeon(9248, 5402, 6623, 4531, {11881, 120})
RainbowBlopDungeon = Dungeon(9254, 5410, 6624, 4544, {11892, 436})
KitsouneDungeon = Dungeon(7311, 2941, 6604, 2577, {8502, 388})
FungusDungeon = Dungeon(9247, 5471, 6622, 4580, {11931, 436})
IlyzaelleDungeon = Dungeon(11474, 5503, 7707, 4621, {11971, 32})
QuTanDungeon = Dungeon(11475, 5503, 7709, 7708, {11965, 366})