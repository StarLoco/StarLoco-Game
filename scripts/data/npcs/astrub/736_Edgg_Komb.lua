local npc = Npc(736, 110)

npc.colors = {14317092, 15579267, 7170666}
npc.accessories = {0, 6863, 6886, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3037, {2685})
    elseif answer == 2685 then p:ask(3039, {2686})
    end
end

RegisterNPCDef(npc)
