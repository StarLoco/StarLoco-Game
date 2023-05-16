local npc = Npc(308, 9004)
--TODO: Lié à la quête d'Alignement 20 Bonta
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1208)
    end
end

RegisterNPCDef(npc)
