local npc = Npc(139, 9051)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(464)
    end
end

RegisterNPCDef(npc)
