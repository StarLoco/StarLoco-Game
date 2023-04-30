local npc = Npc(47, 9028)

npc.gender = 1

npc.sales = {
    {item=500},
    {item=1304},
    {item=1305},
    {item=1306},
    {item=1307},
    {item=1308},
    {item=1309},
    {item=1310},
    {item=1311},
    {item=1312},
    {item=1313},
    {item=1314},
    {item=1315},
    {item=1316},
    {item=1317},
    {item=1318},
    {item=1319},
    {item=1320},
    {item=1321},
    {item=1322},
    {item=1323}
}

npc.barters = {
    {to={itemID=798, quantity= 1}, from= {
        {itemID=850, quantity= 100},
    }},
    {to={itemID=420, quantity= 100}, from= {
        {itemID=423, quantity= 100},
    }},
    {to={itemID=420, quantity= 1}, from= {
        {itemID=303, quantity= 1},
    }}
}
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(90, {111, 109, 110, 209, 37})
    elseif answer == 209 then p:ask(262)
    elseif answer == 109 then p:ask(127, {65})
    elseif answer == 110 then p:ask(128)
    elseif answer == 111 then p:ask(129)
    end
end

RegisterNPCDef(npc)
