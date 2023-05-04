local npc = Npc(943, 121)

npc.gender = 1
npc.accessories = {0, 8823, 0, 0, 0}

local dungeonKeyId = 8975
local enterDungeonDest = {10807, 125}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local hasDungeonKey = p:getItem(dungeonKeyId) or hasKeyChainFor(p, dungeonKeyId)
    if p:mapID() == 10509 then
        local showKeyResponse = 3678

        if answer == 0 then
            p:ask(4197, {3675})
        elseif answer == 3675 then
            p:ask(4198, {3676})
        elseif answer == 3676 then
            p:ask(4199, {3677})
        elseif answer == 3677 then
        local responses = hasDungeonKey and {showKeyResponse} or {}
            p:ask(4200, responses)
        elseif answer == showKeyResponse then
            if hasDungeonKey then
                p:teleport(enterDungeonDest[1], enterDungeonDest[2])
                p:endDialog()
            else
                p:endDialog()
            end
        end
    elseif p:mapID() == 10813 then
        local hasItems = hasDungeonKey
        local showKeyResponse = 3674
        if answer == 0 then
            local responses = hasItems and {showKeyResponse} or {}
            p:ask(4201, responses)
        elseif answer == showKeyResponse then
            if not useKeyChainFor(p, dungeonKeyId) and not p:consumeItem(dungeonKeyId, 1) then
                -- Should not happen (cheat?)
                p:endDialog()
                return
            end
            p:addItem(8977)
            p:addItem(8674)
            p:teleport(10509, 311)
            p:endDialog()
        else
            p:endDialog()
        end
    end
end

RegisterNPCDef(npc)
