local npc = Npc(115, 9029)

npc.sales = {
    {item=286},
    {item=1731},
    {item=1734},
    {item=1736},
    {item=1973},
    {item=1975},
    {item=1977},
    {item=1978},
    {item=1983},
    {item=1984},
    {item=1985},
    {item=1986},
    {item=2012}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(379)
    end
end

RegisterNPCDef(npc)