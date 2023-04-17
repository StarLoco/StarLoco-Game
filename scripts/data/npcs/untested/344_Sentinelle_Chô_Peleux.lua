local npc = Npc(344, 1205)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1386)
    end
end

RegisterNPCDef(npc)
