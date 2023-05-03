local npc = Npc(762, 1357)

npc.gender = 1
npc.accessories = {0, 0, 0, 0x1ee7, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
	local hasAllItems = p:getItem(8003) and p:getItem(8004) and p:getItem(8005) and p:getItem(8006) and p:getItem(8007) and p:getItem(8008) and p:getItem(8009)
    if answer == 0 then
		p:ask(3146, {3550})
	elseif answer == 3550 then
		p:ask(4073, {3551})
	elseif answer == 3551 then
		if hasAllItems then
			p:ask(4074, {3552})
		else
			p:ask (4074)
		end
	elseif answer == 3552 then
		local consumedAll = p:consumeItem(8003, 1) and
			p:consumeItem(8004, 1) and
			p:consumeItem(8005, 1) and
			p:consumeItem(8006, 1) and
			p:consumeItem(8007, 1) and
			p:consumeItem(8008, 1) and
			p:consumeItem(8009, 1)
		if consumedAll then
			p:addItem(7911)
		end
		p:endDialog()
	else
		p:ask(4074)
	end
end

RegisterNPCDef(npc)
