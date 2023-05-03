local npc = Npc(885, 50)

npc.colors = {16777215, 16777215, 12976128}
npc.accessories = {0, 7516, 2629, 0, 7081}
npc.customArtwork = 9088

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3822)
    end
end

RegisterNPCDef(npc)
