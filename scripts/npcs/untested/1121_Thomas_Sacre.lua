local npc = Npc(1121, 80)

npc.colors = {2403655, 327939, 13872146}
npc.accessories = {0, 8843, 2383, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(6594, {5899, 6491})
    elseif answer == 6491 then p:ask(7036, {6501, 6500, 656})
    elseif answer == 5899 then p:endDialog()
    end
end

RegisterNPCDef(npc)