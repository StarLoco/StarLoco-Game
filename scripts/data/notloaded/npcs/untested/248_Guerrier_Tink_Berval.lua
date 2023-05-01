local npc = Npc(248, 1205)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1228)
    end
end

RegisterNPCDef(npc)
