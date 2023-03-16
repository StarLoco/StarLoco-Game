local npc = Npc(806, 30)

npc.colors = {99585, 8947715, 16774854}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3322, {2972, 2973})
    elseif answer == 2972 then p:endDialog()
    elseif answer == 2973 then p:endDialog()
    end
end

RegisterNPCDef(npc)
