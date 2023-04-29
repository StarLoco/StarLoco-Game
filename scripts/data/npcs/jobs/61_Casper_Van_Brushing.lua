local npc = Npc(61, 9036)

npc.sales = {
    {item=497}
}

npc.barters = {
    {to={itemID=497, quantity= 1}, from= {
        {itemID=303, quantity= 10}
    }},
    {to={itemID=1019, quantity= 1}, from= {
        {itemID=884, quantity= 10},
        {itemID=420, quantity= 10},
        {itemID=384, quantity= 5}
    }}
}
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(36, {502, 321})
    elseif answer == 502 then
        p:ask(596)
    elseif answer == 321 then
        p:ask(404, {322, 323, 324})
    elseif answer == 322 then
        if p:tryLearnJob(Miner) then
           p:ask(335)
        else
           p:ask(336)
        end
    elseif answer == 323 then
        p:ask(405)
    elseif answer == 324 then
        p:ask(406, {325})
    elseif answer == 325 then
        p:ask(407)
    end
end

RegisterNPCDef(npc)
