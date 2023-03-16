local npc = Npc(170, 9039)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(628, {532})
    end
end

RegisterNPCDef(npc)
