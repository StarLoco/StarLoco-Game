local npc = Npc(872, 1207)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3731, {3352, 3279, 3278})
    elseif answer == 3352 then p:ask(3821)
    elseif answer == 3279 then p:ask(3735)
    elseif answer == 3278 then p:ask(3734, {3280, 3287})
    elseif answer == 3280 then p:ask(3736, {3286})
    elseif answer == 3286 then
        p:endDialog()
        p:teleport(10302, 296)
    elseif answer == 3287 then p:endDialog()
    end
end

RegisterNPCDef(npc)
