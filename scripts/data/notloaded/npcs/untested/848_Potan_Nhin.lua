local npc = Npc(848, 30)

npc.colors = {394758, 16121664, 13070517}
npc.accessories = {0, 7143, 0, 0, 0}
npc.customArtwork = 9096

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3588)
    end
end

RegisterNPCDef(npc)
