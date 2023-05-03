local npc = Npc(467, 9045)

npc.colors = {16280076, 11397111, 15513576}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1856, {1612, 1613})
    elseif answer == 1612 then p:ask(1857)
    elseif answer == 1613 then p:ask(1858, {1649})
    elseif answer == 1649 then p:ask(2133)
    end
end

RegisterNPCDef(npc)
