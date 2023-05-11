local npc = Npc(148, 1056)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local hasAllItems = p:getItem(969) and p:getItem(970) and p:getItem(971)
    local consumeAllItems = p:consumeItem(969, 1) and p:consumeItem(970, 1) and p:consumeItem (971, 1)
    local showDofusResponse = 414

    if answer == 0 then
        local responses = hasAllItems and {showDofusResponse} or {}
        p:ask(497, responses)
    elseif answer == showDofusResponse and hasAllItems then
        if consumeAllItems then
            p:addItem(972)
            p:endDialog()
        end
    end
end

RegisterNPCDef(npc)
