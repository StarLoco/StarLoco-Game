local npc = Npc(371, 9004)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1563)
    end
end

RegisterNPCDef(npc)
