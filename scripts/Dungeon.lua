---@class Dungeon
---@field keyID number
---@field questionID number
---@field keychainResponseID number
---@field keyResponseID number
---@field tpDest number[] mapID,cellID
Dungeon = Dungeon or {}
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

---@param player SPlayer
---@return boolean
function Dungeon:hasKeyChain(player)
    local item = player:getItem(keychainTemplateID, 1)
    if not item then return false end
    -- Keys are stored as hex value in keychain
    return item:hasTxtStat(keychainStatID, hex(self.keyID))
end

---@param player SPlayer
---@return boolean
function Dungeon:useKeyChain(player)
    local item = player:getItem(keychainTemplateID, 1)
    if not item then return false end
    -- Keys are stored as hex value in keychain
    return item:consumeTxtStat(player, keychainStatID, hex(self.keyID))
end

---@param player SPlayer
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

-- TODO: Rename to english
BouftousDungeon = Dungeon(1568, 660, 6602, 564, {2073, 409})
ForgeronsDungeon = Dungeon(1569, 7069, 6602, 6632, {2085, 408})
SquelettesDungeon = Dungeon(1570, 7070, 6602, 7708, {2110, 118})
BworkerDungeon = Dungeon(6884, 7071, 6602, 6636, {7522, 432})
PandikazesDungeon = Dungeon(7309, 2940, 6602, 2576, {8326, 433})
BulbesDungeon = Dungeon(7310, 7072, 6602, 6638, {8294, 407})
FirefouxDungeon = Dungeon(7312, 2943, 6602, 2577, {8502, 388})
AbraknydesDungeon = Dungeon(7557, 3272, 6602, 2927, 6605, {8713, 292})
KoulosseDungeon = Dungeon(7908, 3119, 6602, 2742, {8963, 350})
MaitreCorbacDungeon = Dungeon(7926, 3200, 6602, 2829, {9589, 436})
CraqueleursDungeon = Dungeon(7927, 3163, 6602, 2781, {9578, 384})
SkeunkDungeon = Dungeon(8073, 3138, 6602, 2762, {8969, 393})
BworksDungeon = Dungeon(8135, 3172, 6602, 2794, {9755, 268})
ScarafeuillesDungeon = Dungeon(8139, 3179, 6602, 2947, {9775, 436})
TofusDungeon = Dungeon(7918, 3175, 6602, 2797, {9521, 451})
TofulaillerRoyalDungeon = Dungeon(8142, 3176, 6602, 2798, 2799, 2800, {})
ChampsDungeon = Dungeon(8143, 3178, 6602, 2802, {9771, 293})
KaniDungeon = Dungeon(8342, 3177, 6602, 2801, {9658, 408})
CheneMouDungeon = Dungeon(8436, 3276, 6602, 2930, {8714, 112})
EnsableDungeon = Dungeon(8545, 3289, 6602, 2944, {10156, 247})