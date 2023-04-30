local npc = Npc(701, 1273)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then 
        p:ask(2888, {2528})	
    elseif answer == 2528 then
		if p:spellLevel(413) < 1 then
			p:setSpellLevel(413, 1)
			p:teleport(8244, 138)
			p:endDialog()
		else
			p:teleport(8244, 138)
			p:endDialog()
		end
	end
end

RegisterNPCDef(npc)
