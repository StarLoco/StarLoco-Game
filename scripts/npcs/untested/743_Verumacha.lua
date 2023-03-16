local npc = Npc(743, 31)

npc.gender = 1
npc.accessories = {0, 6863, 6886, 0, 7069}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3074, {2729, 273})
    elseif answer == 273 then p:endDialog()
    elseif answer == 2729 then p:endDialog()
    end
end

RegisterNPCDef(npc)