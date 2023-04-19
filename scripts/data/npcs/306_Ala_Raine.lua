local npc = Npc(306, 1207)

npc.sales = {
    {item=1945}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1180, {850})
	elseif answer == 850 then p:ask(1181, {851})
	elseif answer == 851 then
		 if p:tryLearnJob(Butcher) then
			p:ask(1182)
		else
			p:ask(1486)
		end
    end
end

RegisterNPCDef(npc)
