local npc = Npc(127, 9048)

npc.colors = {12614494, 10843992, 15461834}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(402, {320, 319})
    elseif answer == 320 then p:ask(403, {607})
    elseif answer == 319 then p:ask(403, {607})
    end
end

RegisterNPCDef(npc)
