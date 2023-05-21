local npc = Npc(331, 9004)
--TODO: Lié à la quête d'Alignement 39 Bonta
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1576)
    end
end

RegisterNPCDef(npc)
