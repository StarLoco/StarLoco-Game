local npc = Npc(273, 1014)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1127)
    end
end

RegisterNPCDef(npc)
