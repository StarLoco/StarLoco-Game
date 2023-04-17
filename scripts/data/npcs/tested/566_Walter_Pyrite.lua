local npc = Npc(566, 30)

npc.colors = {2827809, -1, -1}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2368)
    end
end

RegisterNPCDef(npc)
