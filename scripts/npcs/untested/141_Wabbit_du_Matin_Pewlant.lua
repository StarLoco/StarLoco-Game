local npc = Npc(141, 9054)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(467, {391})
    end
end

RegisterNPCDef(npc)
