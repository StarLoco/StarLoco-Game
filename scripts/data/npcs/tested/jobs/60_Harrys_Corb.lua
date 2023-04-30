local npc = Npc(60, 9030)

npc.sales = {
    {item=286},
    {item=492}
}

npc.barters = {
    {to={itemID=286, quantity= 1}, from= {
        {itemID=303, quantity= 1},
    }},
    {to={itemID=492, quantity= 1}, from= {
        {itemID=287, quantity= 1},
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(34, {364, 363, 1945})
    elseif answer == 364 then p:ask(432, {358})
    elseif answer == 358 then
        if p:tryLearnJob(Baker) then
           p:ask(335)
        else
           p:ask(336)
        end
    elseif answer == 365 then p:ask(170)
    elseif answer == 1945 then p:ask(132)
    end
end

RegisterNPCDef(npc)
