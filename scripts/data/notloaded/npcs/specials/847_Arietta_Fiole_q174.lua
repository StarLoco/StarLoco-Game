local npc = Npc(847, 111)
--TODO: Lié à la quête 174
npc.gender = 1
npc.colors = {2133067, 13458604, 16175502}
npc.accessories = {3650, 7142, 1695, 0, 0}
npc.customArtwork = 9085

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3573, {3156})
    elseif answer == 3156 then p:endDialog()
    end
end

RegisterNPCDef(npc)
