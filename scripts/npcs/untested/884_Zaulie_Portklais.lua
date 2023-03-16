local npc = Npc(884, 121)

npc.gender = 1
npc.accessories = {0, 7887, 7884, 8151, 0}
npc.customArtwork = 9091

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3813, {3349, 3348})
    elseif answer == 3348 then p:endDialog()
    end
end

RegisterNPCDef(npc)
