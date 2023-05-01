local npc = Npc(236, 9013)

npc.gender = 1
npc.colors = {4096672, 16777215, 1483286}

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
    {item=286},
    {item=311}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1032)
    end
end

RegisterNPCDef(npc)
