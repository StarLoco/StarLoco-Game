local npc = Npc(248, 1205)
--TODO: Lié à la quête Alignement 14 Brâkmar
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1228)
    end
end

RegisterNPCDef(npc)
