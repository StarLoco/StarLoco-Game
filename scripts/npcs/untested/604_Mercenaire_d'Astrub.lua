local npc = Npc(604, 50)

npc.colors = {14317092, 15579267, 7170666}
npc.accessories = {0, 6863, 6886, 0, 7069}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2369, {1971, 1972})
    elseif answer == 1971 then p:ask(2370, {1975, 1976, 1977, 1978, 2025, 545})
    elseif answer == 1972 then p:ask(2371, {5730})
    end
end

RegisterNPCDef(npc)
