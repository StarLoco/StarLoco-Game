local npc = Npc(237, 9041)

npc.colors = {10947370, 2101538, 1576467}

npc.sales = {
    {item=2012},
    {item=1986},
    {item=1985},
    {item=1984},
    {item=1983},
    {item=1978},
    {item=1977},
    {item=1975},
    {item=1973},
    {item=1730},
    {item=1731},
    {item=1736},
    {item=1734},
    {item=286}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1033)
    end
end

RegisterNPCDef(npc)
