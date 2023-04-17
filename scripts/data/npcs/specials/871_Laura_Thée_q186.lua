local npc = Npc(871, 81)
--TODO: Lié à la quête 186
npc.gender = 1
npc.colors = {16777215, 16737555, 14635481}
npc.accessories = {4241, 7921, 0, 1728, 0}
npc.customArtwork = 9094

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3726, {3268, 326})
    elseif answer == 3268 then p:endDialog()
    elseif answer == 326 then p:ask(408, {327, 328})
    elseif answer == 328 then p:ask(409)
    end
end

RegisterNPCDef(npc)
