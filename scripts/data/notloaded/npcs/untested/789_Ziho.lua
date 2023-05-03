local npc = Npc(789, 70)

npc.accessories = {0, 8287, 8286, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3236, {2876, 2877, 2878})
    elseif answer == 2876 then p:endDialog()
    elseif answer == 2877 then p:endDialog()
    elseif answer == 2878 then p:endDialog()
    end
end

RegisterNPCDef(npc)
