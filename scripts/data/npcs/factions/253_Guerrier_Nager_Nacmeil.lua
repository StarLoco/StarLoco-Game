local npc = Npc(253, 1205)
--TODO: Lié à la quête Alignement 8 Brâkmar
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1077)
    end
end

RegisterNPCDef(npc)
