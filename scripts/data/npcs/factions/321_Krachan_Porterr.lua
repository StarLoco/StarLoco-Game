local npc = Npc(321, 1012)
--TODO: Lié à la quête d'Alignement 22 Brâkmar
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1284)
    end
end

RegisterNPCDef(npc)
