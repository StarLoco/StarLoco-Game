local npc = Npc(173, 9020)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(646)
    end
end

RegisterNPCDef(npc)
