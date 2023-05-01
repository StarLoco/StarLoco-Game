local npc = Npc(858, 31)

npc.gender = 1
npc.colors = {16755213, 4073268, 16773017}
npc.accessories = {0, 2531, 2532, 0, 7076}
npc.customArtwork = 9095

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3658)
    end
end

RegisterNPCDef(npc)
