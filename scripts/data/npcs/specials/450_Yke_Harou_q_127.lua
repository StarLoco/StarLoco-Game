local npc = Npc(450, 9058)
--TODO: Lié à la quête 127
npc.customArtwork = 9017

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2104)
    end
end

RegisterNPCDef(npc)
