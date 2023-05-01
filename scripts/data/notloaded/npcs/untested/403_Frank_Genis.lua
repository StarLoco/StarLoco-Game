local npc = Npc(403, 9016)

npc.colors = {265506, 0, 11593722}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1660)
    end
end

RegisterNPCDef(npc)
