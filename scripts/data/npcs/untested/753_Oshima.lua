local npc = Npc(753, 9005)

npc.colors = {0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3118, {2741, 2787})
    elseif answer == 2787 then p:endDialog()
    end
end

RegisterNPCDef(npc)
