local npc = Npc(316, 9030)

npc.colors = {1381387, 16500937, 16777215}

npc.sales = {
    {item=286},
	{item=492}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1247)
    end
end

RegisterNPCDef(npc)
