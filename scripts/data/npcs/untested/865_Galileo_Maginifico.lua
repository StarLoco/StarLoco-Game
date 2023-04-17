local npc = Npc(865, 9041)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3701)
    end
end

RegisterNPCDef(npc)
