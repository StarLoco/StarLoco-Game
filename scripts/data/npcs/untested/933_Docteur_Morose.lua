local npc = Npc(933, 9107)

npc.customArtwork = 9022

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(4167, {379})
    end
end

RegisterNPCDef(npc)
