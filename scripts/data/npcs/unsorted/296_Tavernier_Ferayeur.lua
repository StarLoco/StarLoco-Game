local npc = Npc(296, 1207)

npc.sales = {
    {item=2090},
	{item=311}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(5169)
    end
end

RegisterNPCDef(npc)
