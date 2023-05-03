local npc = Npc(502, 9045)

npc.colors = {14529459, 16713222, 0}
npc.accessories = {0, 706, 6501, 0, 0}

npc.sales = {
    {item=286},
    {item=1731},
    {item=1730},
    {item=1736},
    {item=1973},
    {item=1975},
    {item=1977},
    {item=1978},
    {item=1983},
    {item=1984},
    {item=1985},
    {item=1986},
    {item=2012},
    {item=6765},
    {item=311},
    {item=1734}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2168)
    end
end

RegisterNPCDef(npc)
