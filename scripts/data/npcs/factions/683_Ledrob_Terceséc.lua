local npc = Npc(683, 9007)
--TODO: Lié à la quête d'Alignement 20 Brâkmar
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1211)
    end
end

RegisterNPCDef(npc)
