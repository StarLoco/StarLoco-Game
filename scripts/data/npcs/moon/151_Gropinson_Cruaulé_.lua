local npc = Npc(151, 9064)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local hasAllItems = p:getItem(1014) and p:getItem(1015) and p:getItem(1016) and p:getItem(1017) and p:getItem(1086)
    if answer == 0 then p:ask(523, {478, 479, 482, 485})
    elseif answer == 479 then p:ask(568, {480, 510})
    elseif answer == 480 then p:ask(569, {481})
    elseif answer == 481 then p:aks(570)
    elseif answer == 510 then p:ask(606)
    elseif answer == 482 then p:ask(571, {483})
    elseif answer == 483 then p:ask(572, {484})
    elseif answer == 484 then p:ask(573)
    elseif answer == 478 then p:ask(567)
    elseif answer == 485 then p:ask(574, {486})
    elseif answer == 486 then p:ask(576, {487})
    elseif answer == 487 then
        if hasAllItems then
            local consumeAll = p:consumeItem(1014, 1) and p:consumeItem(1015, 1) and p:consumeItem(1016, 1) and p:consumeItem(1017, 1) and p:consumeItem(1086, 1)
            if consumeAll then
                p:addItem(1088)
                p:ask(577)
            else
                p:ask(578)
            end
        else
            p:ask(578)
        end
    end
end

RegisterNPCDef(npc)
