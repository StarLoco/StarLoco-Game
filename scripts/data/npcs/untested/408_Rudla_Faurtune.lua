local npc = Npc(408, 9016)

npc.colors = {15323913, 6385110, 2954003}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1646, {1273}, "13450")
    elseif answer == 1273 then p:ask(366)
    end
end

RegisterNPCDef(npc)
