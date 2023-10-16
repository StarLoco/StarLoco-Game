local npc = Npc(155, 9063)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local hasAllItems = p:getItem(1010, 1) and p:getItem(1011, 1) and p:getItem(1012, 1) and p:getItem(1013, 1)
    if answer == 0 then
        p:ask(546, {498})
    elseif answer == 498 then
        p:ask(547, {492})
    elseif answer == 492 then
        local consumeAll = p:consumeItem(1010, 1) and p:consumeItem(1011, 1) and p:consumeItem(1012, 1) and p:consumeItem(1013, 1)

        if hasAllItems then
            if consumeAll then
                p:ask(584, {496})
            else
                p:endDialog()
            end
        else
            p:ask(585)
        end
    elseif answer == 497 then
        p:teleport(1766, 332)
        p:endDialog()
    end
end

RegisterNPCDef(npc)
