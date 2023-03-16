local npc = Npc(741, 80)

npc.colors = {2909906, 12403015, 11764810}
npc.accessories = {0, 6863, 6886, 0, 7069}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3113, {2739, 2740})
    elseif answer == 2740 then p:endDialog()
    end
end

RegisterNPCDef(npc)