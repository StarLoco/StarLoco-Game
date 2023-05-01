local npc = Npc(369, 9045)

npc.colors = {0, 12956025, 6710886}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1529)
    end
end

RegisterNPCDef(npc)
