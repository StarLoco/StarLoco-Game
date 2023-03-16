local npc = Npc(876, 9059)

npc.colors = {12571268, 6304805, 7119687}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3772, {3400})
    elseif answer == 3400 then p:endDialog()
    end
end

RegisterNPCDef(npc)
