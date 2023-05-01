local npc = Npc(282, 9040)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1133)
    end
end

RegisterNPCDef(npc)
