local npc = Npc(726, 9006)

npc.colors = {16186867, 16716563, 15924054}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2975, {2784})
    elseif answer == 2784 then p:endDialog()
    end
end

RegisterNPCDef(npc)