local npc = Npc(51, 9030)

npc.sales = {
    {item=286},
    {item=492}
}

npc.barters = {
    {to={itemID=286, quantity= 1}, from= {
        {itemID=303, quantity= 1}
    }},
    {to={itemID=492, quantity= 1}, from= {
        {itemID=287, quantity= 1}
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(25, {144, 357, 365})
    elseif answer == 144 then p:ask(170)
    elseif answer == 357 then p:ask(432, {358})
    elseif answer == 358 then
        if p:tryLearnJob(Baker) then
            p:ask(335)
        else
            p:ask(336)
        end
    elseif answer == 365 then p:ask(437)
    end
end

RegisterNPCDef(npc)
