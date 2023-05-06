local npc = Npc(758, 1439)

local dungeonKeyId = 8073
local enterDungeonDest = {8969, 380}


---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local hasDungeonKey = p:getItem(dungeonKeyId) or hasKeyChainFor(p, dungeonKeyId)
    local showKeyResponse = 2762

    if answer == 0 then
        local responses = hasDungeonKey and {showKeyResponse} or {}
        p:ask(3138, responses)
    elseif answer == showKeyResponse then
        if hasDungeonKey then
            p:teleport(enterDungeonDest[1], enterDungeonDest[2])
            p:endDialog()
        else
            p:endDialog()
        end
    end
end

RegisterNPCDef(npc)
