local npc = Npc(149, 1056)

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
--TODO : SECOND DIALOG APRES QUON LUI A PARLER > 500, {415} > 415 teleport to 167, 273
RegisterNPCDef(npc)
