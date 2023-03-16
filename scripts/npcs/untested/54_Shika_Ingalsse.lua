local npc = Npc(54, 9032)

npc.gender = 1
npc.customArtwork = 9570

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(32)
    end
end

RegisterNPCDef(npc)
