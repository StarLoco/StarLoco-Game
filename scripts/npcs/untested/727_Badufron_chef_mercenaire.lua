local npc = Npc(727, 20)

npc.colors = {14317092, 15579267, 7170666}
npc.accessories = {0, 6863, 6886, 0, 7069}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2978)
    end
end

RegisterNPCDef(npc)