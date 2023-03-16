local npc = Npc(832, 1003)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3495, {3080, 3081})
    elseif answer == 3080 then p:ask(3496, {3083, 3084})
    elseif answer == 3083 then p:endDialog()
    elseif answer == 3084 then p:ask(3498, {308})
    elseif answer == 308 then p:ask(383)
    elseif answer == 3081 then p:ask(3496, {3083, 3084})
    end
end

RegisterNPCDef(npc)
