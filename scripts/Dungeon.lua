Dungeon = Dungeon or {}
Dungeon.__index = Dungeon

local keychainTemplateID = 10207
local keychainStatID = 814

setmetatable(Dungeon, {
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

function Dungeon:hasKeyChain(player)
    local item = player:getItem(keychainTemplateID, 1)
    if not item then return false end
    -- Keys are stored as hex value in keychain
    return item:hasTxtStat(keychainStatID, string.format("%x", self.keyID))
end

function Dungeon:useKeyChain(player)
    local item = player:getItem(keychainTemplateID, 1)
    if not item then return false end
    -- Keys are stored as hex value in keychain
    return item:consumeTxtStat(player, keychainStatID, string.format("%x", self.keyID))
end


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

-- Bouftou
BouftouDungeon = Dungeon(1568, 660, 6602, 564, {2073, 409})
