local npc = Npc(888, 9059)

npc.gender = 0
npc.colors = {-1, -1, 16777215}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3840, {3373, 338})
    elseif answer == 338 then
        p:ask(418)
    elseif answer == 3373 then
        teleportByBreed(p, ASTRUB_STATUES)
        p:endDialog()
    end
end

RegisterNPCDef(npc)
