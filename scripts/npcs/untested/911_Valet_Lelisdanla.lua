local npc = Npc(911, 9038)

npc.colors = {15570193, 16229645, 16290585}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(4013)
    end
end

RegisterNPCDef(npc)
