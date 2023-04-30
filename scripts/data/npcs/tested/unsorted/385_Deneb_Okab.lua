local npc = Npc(385, 9059)
--Ancien NPC utilisé dans les débuts du jeu quand la zone de départ était encore Tainéla, ne semble plus utilisé à l'heure actuelle

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1713)
	-- manque la réponse au dialog et la réponse amène le dialog 1714
    end
end

RegisterNPCDef(npc)
