local npc = Npc(861, 9019)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3692)
    end
end

RegisterNPCDef(npc)
