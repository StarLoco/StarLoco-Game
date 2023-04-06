local npc = Npc(175, 1014)

---@param p Player
function npc:onTalk(p, answer)
    if answer == 0 then 
        p:ask(662, {598})	
    elseif answer == 598 then 	
        if p:spellLevel(373) > 0 then
            p:ask(674, {600})
        else
            p:ask(674, {599, 600})
        end
    elseif answer == 599 then
        p:setSpellLevel(373, 1)
        p:ask(675, {602, 601})
	elseif answer == 600 then
        p:ask(675, {602, 601})
    elseif answer == 601 or answer == 602 then
        p:teleport(1195, 326)
		p:endDialog()
    end
end
RegisterNPCDef(npc)