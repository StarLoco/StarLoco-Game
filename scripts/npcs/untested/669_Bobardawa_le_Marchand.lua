local npc = Npc(669, 120)

npc.accessories = {0, 6926, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2742, {2359})
    elseif answer == 2359 then p:ask(2743, {2360})
    elseif answer == 2360 then p:endDialog()
    end
end

RegisterNPCDef(npc)
