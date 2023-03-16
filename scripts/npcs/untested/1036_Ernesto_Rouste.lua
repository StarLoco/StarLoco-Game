local npc = Npc(1036, 120)

npc.colors = {1880009, 1880009, 12562815}
npc.accessories = {0, 7680, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(5471, {5471, 4580, 6622, 4586})
    elseif answer == 4580 then p:endDialog()
    elseif answer == 4586 then p:ask(5476)
    elseif answer == 6622 then p:endDialog()
    end
end

RegisterNPCDef(npc)
