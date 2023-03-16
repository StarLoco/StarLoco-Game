local npc = Npc(535, 80)

npc.colors = {10420224, 15987467, 7164831}
npc.accessories = {0, 6863, 6886, 0, 7069}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2276, {1907, 1908})
    elseif answer == 1907 then p:ask(2277, {1909})
    elseif answer == 1909 then p:ask(2278, {1910})
    elseif answer == 1910 then p:endDialog()
    elseif answer == 1908 then p:ask(2277, {1909})
    end
end

RegisterNPCDef(npc)
