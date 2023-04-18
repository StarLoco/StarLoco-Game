local npc = Npc(798, 30)

npc.accessories = {0, 0x2043, 0x2042, 0, 0x1ba5}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if p:mapID() == 10155 then 
		SandyDungeon:onTalkToGateKeeper(p, answer)
	elseif p:mapID() == 10165 then
		if answer == 0 then
			p:ask(3290, {2945})
		elseif answer == 2945 then
			p:ask(3291, {2946})
		elseif answer == 2946 then
			p:learnEmote(19)
			p:teleport(10155, 209)
			p:endDialog()
		end
	end
end

RegisterNPCDef(npc)
