local npc = Npc(760, 1450)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3143, {2766})
    elseif answer == 2766 then p:ask(3144, {6628, 2767})
    elseif answer == 6628 then p:endDialog()
    elseif answer == 2767 then p:endDialog()
    end
end

RegisterNPCDef(npc)
