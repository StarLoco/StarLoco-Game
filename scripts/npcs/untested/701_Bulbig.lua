local npc = Npc(701, 1273)

npc.colors = {0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2888, {2528})
    end
end

RegisterNPCDef(npc)