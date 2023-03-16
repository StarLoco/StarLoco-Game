local npc = Npc(891, 9091)

npc.customArtwork = 9100

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3850, {3388, 3389})
    elseif answer == 3388 then p:endDialog()
    elseif answer == 3389 then p:ask(3851, {3390, 3391})
    elseif answer == 3391 then p:ask(3852, {3392, 3393})
    elseif answer == 3392 then p:endDialog()
    elseif answer == 3393 then p:ask(3853, {3394})
    elseif answer == 3394 then p:endDialog()
    end
end

RegisterNPCDef(npc)
