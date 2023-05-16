local npc = Npc(328, 1014)
--TODO: Lié à la quête d'Alignement 22 Brâkmar
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1291)
    end
end

RegisterNPCDef(npc)
