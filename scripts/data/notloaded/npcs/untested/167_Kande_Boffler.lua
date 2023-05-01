local npc = Npc(167, 9039)

npc.colors = {0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(626, {531})
    end
end

RegisterNPCDef(npc)
