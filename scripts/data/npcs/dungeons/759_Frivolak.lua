local npc = Npc(759, 1363)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
local hasAllBroches = p:getItem(7935) and p:getItem(7936) and p:getItem(7937) and p:getItem(7938)
    if answer == 0 then 
		if hasAllBroches then
			p:ask(3142, {2765})
        else
			p:ask(3139, {2763}) end
    elseif answer == 2765 and hasAllBroches then
        p:teleport(8977, 448)
        p:endDialog()
    elseif answer == 2763 then
		p:ask(3140, {2764})
    elseif answer == 2764 then
		p:ask(3141, {})
	else
		p:endDialog()
    end
end

RegisterNPCDef(npc)
