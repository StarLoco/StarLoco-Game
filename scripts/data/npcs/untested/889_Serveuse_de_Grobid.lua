local npc = Npc(889, 9012)

npc.gender = 1
npc.customArtwork = 9090

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3848)
    end
end

RegisterNPCDef(npc)
