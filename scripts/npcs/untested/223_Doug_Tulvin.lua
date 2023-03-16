local npc = Npc(223, 9049)

npc.colors = {0, 12700125, 6125728}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(950, {681, 673})
    elseif answer == 673 then p:ask(951, {677, 678, 679, 680})
    elseif answer == 681 then p:ask(960, {682, 683})
    end
end

RegisterNPCDef(npc)
