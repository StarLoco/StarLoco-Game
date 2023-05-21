local npc = Npc(254, 1205)
--TODO: Lié à la quête Alignement 8 Brâkmar
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1065)
    end
end

RegisterNPCDef(npc)
