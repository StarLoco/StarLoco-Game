local npc = Npc(846, 80)

npc.colors = {16760576, 13055806, 10627125}
npc.accessories = {8099, 629, 2387, 0, 7080}
npc.customArtwork = 9087

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3678)
    end
end

RegisterNPCDef(npc)