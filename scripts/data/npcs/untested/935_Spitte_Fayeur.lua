local npc = Npc(935, 11)

npc.gender = 1
npc.accessories = {0, 8918, 8919, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(4172, {3656, 3657, 365})
    elseif answer == 3656 then p:endDialog()
    elseif answer == 3657 then p:endDialog()
    elseif answer == 365 then p:ask(437)
    end
end

RegisterNPCDef(npc)
