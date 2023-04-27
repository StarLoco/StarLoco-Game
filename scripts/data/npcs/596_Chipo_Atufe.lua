local npc = Npc(596, 30)

npc.colors = {14330916, 2566968, 15000282}

npc.sales = {
    {item=497}
}

local jobId = Miner
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2399, {2027, 2028})
    elseif answer == 2027 then p:ask(2400)
    elseif answer == 2028 then 
		if p:jobLevel(jobId) < 1 then
			p:ask(2401, {2036, 2035})
		else
			p:ask(1489)
		end		
    elseif answer == 2035 then
		p:endDialog()
    elseif answer == 2036 then 
		if p:tryLearnJob(jobId) then
			p:ask(2402)
		else
			p:ask(1489)
		end
    end
end

RegisterNPCDef(npc)
