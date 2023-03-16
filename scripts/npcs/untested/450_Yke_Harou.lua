local npc = Npc(450, 9058)

npc.customArtwork = 9017

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2104)
    end
end

RegisterNPCDef(npc)
