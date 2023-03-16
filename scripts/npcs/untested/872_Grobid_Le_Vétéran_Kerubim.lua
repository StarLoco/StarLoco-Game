local npc = Npc(872, 1207)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3731, {3277, 3352, 3279, 327})
    elseif answer == 3352 then p:ask(3821)
    elseif answer == 3277 then p:ask(3733, {3281, 328})
    elseif answer == 3281 then p:endDialog()
    elseif answer == 328 then p:ask(409)
    elseif answer == 3279 then p:ask(3735)
    end
end

RegisterNPCDef(npc)
