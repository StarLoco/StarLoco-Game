local npc = Npc(942, 40)

npc.colors = {6247763, 6247763, 6247763}
npc.accessories = {0, 8829, 0, 0, 0}

local dungeonKeyId = 8971
local enterDungeonDest = {10360, 364}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local hasDungeonKey = p:getItem(dungeonKeyId) or hasKeyChainFor(p, dungeonKeyId)
    if p:mapID() == 11066 then
        local showKeyResponse = 3673

        if answer == 0 then
            p:ask(4192, {3670})
        elseif answer == 3670 then
            p:ask(4193, {3671})
        elseif answer == 3671 then
            p:ask(4194, {3672})
        elseif answer == 3672 then
            local responses = hasDungeonKey and {3673} or {}
            p:ask(4195, responses)
        elseif answer == showKeyResponse then
            if hasDungeonKey then
                p:teleport(enterDungeonDest[1], enterDungeonDest[2])
                p:endDialog()
            else
                p:endDialog()
            end
        end
    elseif p:mapID() == 11069 then
        local hasItems = hasDungeonKey
        local showKeyResponse = 3679
        if answer == 0 then
            local responses = hasItems and {showKeyResponse} or {}
            p:ask(4196, responses)
        elseif answer == showKeyResponse then
            if not useKeyChainFor(p, dungeonKeyId) and not p:consumeItem(dungeonKeyId, 1) then
                -- Should not happen (cheat?)
                p:endDialog()
                return
            end
            p:addItem(8972)
            p:addItem(8672)
            p:teleport(11066, 293)
            p:endDialog()
        else
            p:endDialog()
        end
    end
end

RegisterNPCDef(npc)
