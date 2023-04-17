local npc = Npc(410, 9016)

npc.colors = {7653097, 13652826, 7255879}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1672, {1290, 1291, 128})
    elseif answer == 128 then p:ask(164)
    elseif answer == 1290 then p:endDialog()
    elseif answer == 1291 then p:endDialog()
    end
end

RegisterNPCDef(npc)
