local npc = Npc(534, 80)

npc.accessories = {0, 6863, 6886, 0, 7069}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2271, {1903, 1904})
    elseif answer == 1904 then p:ask(2273, {1906})
    elseif answer == 1906 then p:ask(2275)
    elseif answer == 1903 then p:ask(2272, {1905})
    elseif answer == 1905 then p:ask(2274)
    end
end

RegisterNPCDef(npc)
