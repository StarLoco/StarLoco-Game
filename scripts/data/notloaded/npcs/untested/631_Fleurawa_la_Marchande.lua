local npc = Npc(631, 121)

npc.gender = 1
npc.colors = {15435243, 16777215, 16174583}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2527, {2223, 2224, 2225, 2226, 2164})
    elseif answer == 2224 then p:ask(2603)
    elseif answer == 2225 then p:ask(2603)
    elseif answer == 2226 then p:ask(2603)
    elseif answer == 2164 then p:ask(2603)
    elseif answer == 2223 then p:ask(2603)
    end
end

RegisterNPCDef(npc)
