local npc = Npc(149, 1056)

npc.colors = {0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(497, {414})
    end
end

RegisterNPCDef(npc)
