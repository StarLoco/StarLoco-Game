local npc = Npc(168, 9039)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(624, {530})
    end
end

RegisterNPCDef(npc)
