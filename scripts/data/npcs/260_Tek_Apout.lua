local npc = Npc(260, 9004)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(943)
    end
end

RegisterNPCDef(npc)
