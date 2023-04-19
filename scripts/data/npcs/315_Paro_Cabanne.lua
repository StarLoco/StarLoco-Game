local npc = Npc(315, 21)

npc.gender = 1
npc.colors = {5918046, 16777215, -1}
npc.accessories = {0, 0, 945, 0, 0}

npc.sales = {
    {item=579},
	{item=951}
}


---@param p Player
---@param answer number
local price = 50
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1245, {1178, 1177})
	elseif answer == 1178 then p:ask(1543)
	elseif answer == 1177 then
		local responses = p:kamas() >= price and {1179} or {}
		p:ask(1542, responses)
	elseif answer == 1179 then
		if p:modKamas(-price) then
			p:ask(1544, {1181, 1180, 1183})
		end
	elseif answer == 1181 then p:ask(1549)
	elseif answer == 1180 then p:ask(1547)
	elseif answer == 1183 then p:ask(1552)
    end
end

RegisterNPCDef(npc)
