local npc = Npc(151, 9064)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(523, {485, 482, 479, 478})
    elseif answer == 482 then p:ask(571, {483})
    elseif answer == 483 then p:ask(572, {484})
    elseif answer == 484 then p:ask(573)
    elseif answer == 485 then p:ask(574, {486})
    elseif answer == 486 then p:ask(576, {487})
    elseif answer == 478 then p:ask(567)
    elseif answer == 479 then p:ask(568, {510, 480})
    elseif answer == 480 then p:ask(569, {48})
    elseif answer == 510 then p:ask(606)
    end
end

RegisterNPCDef(npc)
