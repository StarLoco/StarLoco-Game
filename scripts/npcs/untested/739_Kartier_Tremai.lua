local npc = Npc(739, 120)

npc.colors = {2590679, 8750565, 16777215}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3057, {2723, 2724})
    elseif answer == 2723 then p:endDialog()
    elseif answer == 2724 then p:endDialog()
    end
end

RegisterNPCDef(npc)