local npc = Npc(154, 9061)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local hasAllItems = p:getItem(1000, 6) and p:getItem(999, 1) and p:getItem(1001, 2) and p:getItem(998, 1) and p:getItem(1004, 4) and p:getItem(1003, 1) and p:getItem(1002, 1) and p:getItem(1018, 10)
    if answer == 0 then p:ask(543, {461, 460, 499})
    elseif answer == 460 then p:ask(544, {490})
    elseif answer == 490 then p:ask(582, {491})
    elseif answer == 491 then p:ask(607)
    elseif answer == 461 then p:ask(545)
    elseif answer == 499 then
        if hasAllItems then
            local consumeAll = p:consumeItem(1000, 6) and p:consumeItem(999, 1) and p:consumeItem(1001, 2) and p:consumeItem(998, 1) and p:consumeItem(1004, 4) and p:consumeItem(1003, 1) and p:consumeItem(1002, 1) and p:consumeItem(1018, 10)
            if consumeAll then
                p:addItem(272)
                p:endDialog()
            else
                p:endDialog()
            end
        else p:ask(1836)
        end
    end
end

RegisterNPCDef(npc)
