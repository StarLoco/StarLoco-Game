local npc = Npc(844, 1003)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3562, {3143, 3144})
    elseif answer == 3143 then p:ask(3563, {3146, 3148})
    elseif answer == 3146 then p:ask(3565, {3147})
    elseif answer == 3147 then p:endDialog()
    elseif answer == 3148 then p:endDialog()
    elseif answer == 3144 then p:ask(3564, {3145})
    elseif answer == 3145 then p:endDialog()
    end
end

RegisterNPCDef(npc)
