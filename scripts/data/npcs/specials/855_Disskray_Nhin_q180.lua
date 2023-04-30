local npc = Npc(855, 30)
--TODO: Lié à la quête 180
npc.colors = {8388615, 16755731, 5117722}
npc.accessories = {7213, 702, 677, 7520, 0}
npc.customArtwork = 9098

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3636)
    end
end

RegisterNPCDef(npc)
