local npc = Npc(256, 9015)
--TODO: Lié à la quête Alignement Bonta #12
npc.sales = {
    {item=493},
	{item=494},
	{item=495},
	{item=496},
	{item=922},
	{item=1520},
	{item=1539},
	{item=1560},
	{item=1561},
	{item=1562}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(945)
    end
end

RegisterNPCDef(npc)
