local npc = Npc(854, 1197)

npc.accessories = {0, 1908, 8458, 0, 7082}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3629, {3205, 320})
    elseif answer == 320 then p:ask(403, {607})
    elseif answer == 607 then p:ask(678, {608})
    elseif answer == 608 then p:ask(679)
    elseif answer == 3205 then p:ask(3633, {320})
    elseif answer == 320 then p:ask(403, {607})
    end
end

RegisterNPCDef(npc)
