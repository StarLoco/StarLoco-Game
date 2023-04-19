local npc = Npc(774, 9048)

-- TODO: How to get to questions 3183, 3200 ?

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if p:mapID() == 2985 then 
		LordCrowDungeon:onTalkToGateKeeper(p, answer)
	elseif p:mapID() == 9604 then
		if answer == 0 then
			p:ask(3181, {2810})
		elseif answer == 2810 then
			p:addItem(7703)
			p:teleport(2985, 294)
			p:endDialog()
		end
	end
end

RegisterNPCDef(npc)
