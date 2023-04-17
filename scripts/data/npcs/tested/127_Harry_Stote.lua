local npc = Npc(127, 9048)

npc.colors = {12614494, 10843992, 15461834}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(402, {319, 320})
    elseif answer == 320 then p:ask(403, {607})
    elseif answer == 607 then p:ask(678, {608})
    elseif answer == 608 then p:ask(679)
    elseif answer == 319 then p:ask(403, {607})
    end
end

RegisterNPCDef(npc)
