local npc = Npc(834, 9090)

npc.colors = {2023454, 15061273, 16254506}
npc.customArtwork = 1076

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3474, {3068})
    elseif answer == 3068 then p:ask(3478, {3069})
    elseif answer == 3069 then p:ask(3479, {3070, 3071})
    elseif answer == 3070 then p:endDialog()
    elseif answer == 3071 then p:ask(3481, {3072})
    elseif answer == 3072 then p:endDialog()
    end
end

RegisterNPCDef(npc)
