local npc = Npc(277, 9018)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1118)
    end
end

RegisterNPCDef(npc)
