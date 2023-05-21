local npc = Npc(246, 1205)
--TODO: Lié à la quête Alignement 9 Bonta & 14/70 Brâkmar
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1070)
    end
end

RegisterNPCDef(npc)
