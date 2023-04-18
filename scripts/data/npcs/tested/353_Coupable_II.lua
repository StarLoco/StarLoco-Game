local npc = Npc(353, 1205)
--TODO:  Lié à la quête Alignement Brâkmar #27, il se trouve map 6259 on ne peut pas y accéder tant qu'on est pas à cette quête donc vérifier le gfxID un jour

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1407)
    end
end

RegisterNPCDef(npc)
