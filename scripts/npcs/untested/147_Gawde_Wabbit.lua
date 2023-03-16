local npc = Npc(147, 1058)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(477, {402})
    end
end

RegisterNPCDef(npc)
