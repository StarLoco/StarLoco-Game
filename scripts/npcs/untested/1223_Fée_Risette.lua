local npc = Npc(1223, 71)

npc.gender = 1
npc.accessories = {0, 706, 0, 7704, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(5452, {7491, 7490})
    elseif answer == 7490 then p:endDialog()
    elseif answer == 7491 then p:endDialog()
    end
end

RegisterNPCDef(npc)
