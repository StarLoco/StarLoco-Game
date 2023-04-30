local npc = Npc(878, 40)
--TODO: Lié à la quête 190
npc.colors = {8158389, 13677665, 3683117}
npc.accessories = {0, 8441, 677, 7518, 7070}
npc.customArtwork = 9092

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3786, {3323, 3324})
    elseif answer == 3323 then p:ask(3787, {3325, 3326})
    end
end

RegisterNPCDef(npc)
