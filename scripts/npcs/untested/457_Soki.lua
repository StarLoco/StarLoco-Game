local npc = Npc(457, 9001)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1842, {1602})
    elseif answer == 1602 then p:ask(1843, {1724})
    elseif answer == 1724 then p:endDialog()
    end
end

RegisterNPCDef(npc)
