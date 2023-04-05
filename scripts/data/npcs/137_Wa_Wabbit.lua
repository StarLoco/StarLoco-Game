local npc = Npc(137, 9055)

---@param p Player
function npc:onTalk(p, answer)
    if answer == 0 then 
        p:ask(470, {394, 395, 396})
	elseif answer == 394 then
		p:addItem(970, 1, false)
		p:teleport(167, 273)
		p:endDialog()
	elseif answer == 395 then
		p:addItem(969, 1, false)
		p:teleport(167, 273)
		p:endDialog()
		elseif answer == 396 then
		p:addItem(971, 1, false)
		p:teleport(167, 273)
		p:endDialog()
	end
end
RegisterNPCDef(npc)