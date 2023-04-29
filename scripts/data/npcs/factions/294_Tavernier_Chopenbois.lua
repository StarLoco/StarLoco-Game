local npc = Npc(294, 1207)
--TODO: Lié à la quête Alignement Bonta #91 (est-ce que implanté sur offi ?) ou #41/#42 Brâkmar
npc.colors = {5643035, -1, -1}

npc.sales = {
    {item=2088},
	{item=311}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(4420)
    end
end

RegisterNPCDef(npc)
