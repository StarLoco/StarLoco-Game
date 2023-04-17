local npc = Npc(455, 11)

npc.gender = 1
npc.colors = {16357311, 8590870, 12153950}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1888, {2119})
    elseif answer == 2119 then p:endDialog()
    end
end

RegisterNPCDef(npc)
