local npc = Npc(709, 9075)

npc.accessories = {0, 0x1bea, 0, 0, 0}

local dungeonKeyId = 7312

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local hasDungeonKey = p:getItem(dungeonKeyId) or hasKeyChainFor(p, dungeonKeyId)
    local showKeyResponse = 2579

    if answer == 0 then
        local responses = hasDungeonKey and {showKeyResponse} or {}
        p:ask(2943, responses)
    elseif answer == showKeyResponse and hasDungeonKey then
        p:teleport(8279, 408)
        p:endDialog()
    end
end

RegisterNPCDef(npc)
