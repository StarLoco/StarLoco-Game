local npc = Npc(945, 50)

npc.colors = {16773874, 16719648, 16510736}
npc.accessories = {0, 8842, 0, 0, 0}

local dungeonKeyId = 8972
local enterDungeonDest = {11259, 447}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local hasDungeonKey = p:getItem(dungeonKeyId) or hasKeyChainFor(p, dungeonKeyId)
    if p:mapID() == 11260 then
        local showKeyResponse = 3682

        if answer == 0 then
            p:ask(4203, {3681})
        elseif answer == 3681 then
            local responses = hasDungeonKey and {showKeyResponse} or {}
            p:ask(4204, responses)
        elseif answer == showKeyResponse then
            if hasDungeonKey then
                p:teleport(enterDungeonDest[1], enterDungeonDest[2])
                p:endDialog()
            else
                p:endDialog()
            end
        end
    elseif p:mapID() == 11517 then
        local hasItems = hasDungeonKey
        local showKeyResponse = 3683
        if answer == 0 then
            local responses = hasItems and {showKeyResponse} or {}
            p:ask(4196, responses)
        elseif answer == showKeyResponse then
            if not useKeyChainFor(p, dungeonKeyId) and not p:consumeItem(dungeonKeyId, 1) then
                -- Should not happen (cheat?)
                p:endDialog()
                return
            end
            p:addItem(8975)
            p:addItem(8673)
            p:teleport(11260, 264)
            p:endDialog()
        else
            p:endDialog()
        end
    end
end

RegisterNPCDef(npc)