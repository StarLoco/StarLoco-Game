local npc = Npc(250, 1205)
--TODO: Lié à la quête Alignement 70 Brâkmar
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1091)
    end
end

RegisterNPCDef(npc)
