local npc = Npc(318, 9045)

npc.colors = {10878976, 14534049, 15306274}
npc.accessories = {0, 1906, 957, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1298)
    end
end

RegisterNPCDef(npc)
