local npc = Npc(370, 9041)

npc.colors = {11749962, 10889728, 8429312}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1562)
    end
end

RegisterNPCDef(npc)
