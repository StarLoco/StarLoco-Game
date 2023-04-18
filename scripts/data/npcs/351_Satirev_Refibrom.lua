local npc = Npc(351, 1205)
--TODO: Lié à la quête Alignement Brâkmar #27, il se trouve map 6259 on ne peut pas y accéder tant qu'on est pas à cette quête donc vérifier le gfxID un jour

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then 
		p:ask(1401, {1055, 1056, 1057, 1058, 1059})
	elseif answer == 1055 or answer == 1056 or answer == 1057 or answer == 1058 or answer == 1059 then
	    --TODO:ADD QUEST OBJECTIVE COMPLETED AND NEXT OBJECTIVE
		p:ask(1409)
    end
end
--DIALOG QUAND QUÊTE FINI = 1400
RegisterNPCDef(npc)
