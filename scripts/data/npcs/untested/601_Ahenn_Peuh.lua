local npc = Npc(601, 9013)

npc.gender = 1
npc.colors = {1841667, 14047709, 1901064}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2412, {2050, 2051})
    elseif answer == 2050 then p:ask(2413, {2051, 2052})
    elseif answer == 2051 then p:ask(2414)
    elseif answer == 2052 then p:ask(2415)
    elseif answer == 2051 then p:ask(2414)
    end
end

RegisterNPCDef(npc)
