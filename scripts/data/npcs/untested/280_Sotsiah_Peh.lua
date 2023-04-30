local npc = Npc(280, 9067)

npc.colors = {13765674, 4074807, 0}

npc.sales = {
    {item=1463},
    {item=1459},
    {item=1460},
    {item=1468},
    {item=1473},
    {item=1539},
    {item=1560},
    {item=1561},
    {item=1562},
    {item=1520}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1126)
    end
end

RegisterNPCDef(npc)
