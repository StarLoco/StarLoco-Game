local npc = Npc(269, 40)

npc.colors = {7753513, 8018501, 4077607}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1130, {805, 806})
    elseif answer == 805 then p:ask(1141)
    elseif answer == 806 then p:endDialog()
    end
end

RegisterNPCDef(npc)
