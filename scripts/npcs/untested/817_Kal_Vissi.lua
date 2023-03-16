local npc = Npc(817, 1243)

npc.colors = {639553, 2506968, 4587520}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3404)
    end
end

RegisterNPCDef(npc)
