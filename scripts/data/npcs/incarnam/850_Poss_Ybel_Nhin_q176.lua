local npc = Npc(850, 31)
--TODO: Lié à la quête 176
npc.gender = 1
npc.colors = {173494, 15924991, 3482728}
npc.accessories = {0, 7150, 2534, 6716, 0}
npc.customArtwork = 9101

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3609)
    end
end

RegisterNPCDef(npc)
