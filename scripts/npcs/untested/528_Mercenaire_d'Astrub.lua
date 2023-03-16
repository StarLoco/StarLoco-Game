local npc = Npc(528, 11)

npc.gender = 1
npc.accessories = {0, 6863, 6886, 0, 7069}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2369, {1971, 1972})
    elseif answer == 1971 then p:ask(2370, {1975, 1976, 1977, 1978, 2025, 545})
    elseif answer == 545 then p:ask(642)
    elseif answer == 1975 then p:endDialog()
    elseif answer == 1976 then p:endDialog()
    elseif answer == 2025 then p:endDialog()
    elseif answer == 1977 then p:endDialog()
    elseif answer == 1978 then p:endDialog()
    elseif answer == 1972 then p:ask(2371, {5730})
    elseif answer == 5730 then p:endDialog()
    end
end

RegisterNPCDef(npc)
