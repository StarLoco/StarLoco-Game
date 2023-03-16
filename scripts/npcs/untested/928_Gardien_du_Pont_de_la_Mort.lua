local npc = Npc(928, 30)

npc.colors = {15787994, 9594674, 16773874}
npc.accessories = {0, 8845, 0, 0, 0}
npc.customArtwork = 9105

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(4122, {3616})
    elseif answer == 3616 then p:endDialog()
    end
end

RegisterNPCDef(npc)
