local npc = Npc(778, 9032)

local dungeonKeyId = 8142
local dungeonDestination = {9528, 462}
local dungeonDestination2 = {9529, 422}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local hasDungeonKey = p:getItem(dungeonKeyId)
    local hasKeyChain = hasKeyChainFor(p, dungeonKeyId)
    local showKeyResponse = 2798
    local showKeyResponse2 = 2799
    local showKeyChainResponse = 6629
    local showKeyChainResponse2 = 6630

    if answer == 0 then
        local responses = {}

        if hasDungeonKey then
            table.insert(responses, showKeyResponse)
            table.insert(responses, showKeyResponse2)
        end

        table.insert(responses, 2800)

        if hasKeyChain then
            table.insert(responses, showKeyChainResponse)
            table.insert(responses, showKeyChainResponse2)
        end
        p:ask(3176, responses)
    elseif answer == showKeyResponse then
        if hasDungeonKey then
            p:consumeItem(dungeonKeyId, 1)
            p:teleport(dungeonDestination[1], dungeonDestination[2])
            p:endDialog()
        end
    elseif answer == showKeyChainResponse then
        if hasKeyChain then
            useKeyChainFor(p, dungeonKeyId)
            p:teleport(dungeonDestination[1], dungeonDestination[2])
            p:endDialog()
        end
    elseif answer == showKeyResponse2 then
        if hasDungeonKey then
            p:consumeItem(dungeonKeyId, 1)
            p:teleport(dungeonDestination2[1], dungeonDestination2[2])
            p:endDialog()
        end
    elseif answer == showKeyChainResponse2 then
        if hasKeyChain then
            useKeyChainFor(p, dungeonKeyId)
            p:teleport(dungeonDestination2[1], dungeonDestination2[2])
            p:endDialog()
        end
    elseif answer == 2800 then
        p:teleport(9530, 90)
        p:endDialog()
    end
end

RegisterNPCDef(npc)
