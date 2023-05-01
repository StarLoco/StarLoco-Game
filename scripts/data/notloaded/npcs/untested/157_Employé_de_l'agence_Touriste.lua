local npc = Npc(157, 9046)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(550, {466, 464, 465, 482})
    elseif answer == 464 then p:ask(551, {46})
    elseif answer == 465 then p:ask(552, {47})
    elseif answer == 482 then p:ask(571, {483})
    elseif answer == 483 then p:ask(572, {484})
    elseif answer == 484 then p:ask(573)
    elseif answer == 466 then p:ask(556, {505})
    end
end

RegisterNPCDef(npc)
