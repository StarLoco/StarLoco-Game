local npc = Npc(96, 80)

npc.colors = {12065306, 15263976, 15461612}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
	local hasAllItems = p:getItem(387, 2) and p:getItem(388, 4)
    if answer == 0 then p:ask(297, {235})
	elseif answer == 235 then 
		if hasAllItems and p:level() >= 30 then
			p:ask(298, {236})
		else
			p:ask(299)
		end
	elseif answer == 236 then
		p:ask(209, {243})
	elseif answer == 243 then
		local consumedAll = p:consumeItem(387, 2) and p:consumeItem(388, 4)
		if consumedAll then
			p:teleport(747, 407)
			p:endDialog()
		else
			p:ask(299)
		end	
	end
end

RegisterNPCDef(npc)
