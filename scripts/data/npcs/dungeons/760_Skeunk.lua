local npc = Npc(760, 1450)

local dungeonKeyId = 8073

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local hasAllBroches = p:getItem(7935) and p:getItem(7936) and p:getItem(7937) and p:getItem(7938)
    local hasItemsAndKey = hasAllBroches and p:getItem(dungeonKeyId)
    local hasItemsAndKeyChain = hasAllBroches and hasKeyChainFor(p, dungeonKeyId)

    local showKeyResponse = 2767
    local showKeyChainResponse = 6628

    if answer == 0 then
        p:ask(3143, {2766})
    elseif answer == 2766 then
        local responses = {}
        if hasItemsAndKey and hasItemsAndKeyChain then
            responses = {showKeyResponse, showKeyChainResponse, 3581}
        elseif hasItemsAndKey then
            responses = {showKeyResponse, 3581}
        elseif hasItemsAndKeyChain then
            responses = {showKeyChainResponse, 3581}
        else
            responses = {}
        end
        p:ask(3144, responses)
    elseif answer == showKeyResponse and hasItemsAndKey then
        local consumedAll = p:consumeItem(7935, 1) and
                p:consumeItem(7936, 1) and
                p:consumeItem(7937, 1) and
                p:consumeItem(7938, 1) and
                p:consumeItem(dungeonKeyId, 1)
        if consumedAll then
            p:addItem(8072)
            p:teleport(9503, 357)
            p:endDialog()
        end
    elseif answer == showKeyChainResponse and hasItemsAndKeyChain then
        local consumedAll = p:consumeItem(7935, 1) and
                p:consumeItem(7936, 1) and
                p:consumeItem(7937, 1) and
                p:consumeItem(7938, 1) and
                useKeyChainFor(p, dungeonKeyId)
        if consumedAll then
            p:addItem(8072)
            p:teleport(9503, 357)
        end
        p:endDialog()
    elseif answer == 3581 then
        p:teleport(9503, 357)
        p:endDialog()
    end
end

RegisterNPCDef(npc)
