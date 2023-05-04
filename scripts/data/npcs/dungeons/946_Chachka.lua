local npc = Npc(946, 121)

npc.gender = 1
npc.accessories = {0, 8704, 0, 0, 0}

local dungeonKeyId = 8977
local enterDungeonDest = {11108, 405}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local hasDungeonKey = p:getItem(dungeonKeyId) or hasKeyChainFor(p, dungeonKeyId)
    if p:mapID() == 11136 then
        local showKeyResponse = 3685

        if answer == 0 then
            p:ask(4206, {3684})
        elseif answer == 3684 then
            local responses = hasDungeonKey and {showKeyResponse} or {}
            p:ask(4207, responses)
        elseif answer == showKeyResponse then
            if hasDungeonKey then
                p:teleport(enterDungeonDest[1], enterDungeonDest[2])
                p:endDialog()
            else
                p:endDialog()
            end
        end
    elseif p:mapID() == 11518 then
        local hasKey = p:getItem(dungeonKeyId)
        local hasKeyChain = hasKeyChainFor(p, dungeonKeyId)
        local showKeyResponse = 3686
        local showKeyChainResponse = 6627
        if answer == 0 then
            local responses = {}
            if hasKey then
                table.insert(responses, showKeyResponse)
            end
            if hasKeyChain then
                table.insert(responses, showKeyChainResponse)
            end
            p:ask(4208, responses)
        elseif answer == showKeyResponse and hasKey or answer == showKeyChainResponse and hasKeyChain then
            if answer == showKeyResponse then
                if not p:consumeItem(dungeonKeyId, 1) then
                    -- Should not happen (cheat?)
                    p:endDialog()
                    return
                end
            else
                if not useKeyChainFor(p, dungeonKeyId) then
                    -- Should not happen (cheat?)
                    p:endDialog()
                    return
                end
            end
            p:addItem(8675)
            p:teleport(11136, 280)
            p:endDialog()
        end
    end
end

RegisterNPCDef(npc)
