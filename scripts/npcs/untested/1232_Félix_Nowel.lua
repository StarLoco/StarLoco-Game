local npc = Npc(1232, 30)

npc.accessories = {0, 8334, 8333, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(7607, {7611, 760})
    elseif answer == 760 then p:endDialog()
    elseif answer == 7611 then p:endDialog()
    end
end

RegisterNPCDef(npc)
