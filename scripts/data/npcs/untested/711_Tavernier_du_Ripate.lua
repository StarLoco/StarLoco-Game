local npc = Npc(711, 1207)

npc.colors = {6961186, 14408668, 1907997}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(4557)
    end
end

RegisterNPCDef(npc)
