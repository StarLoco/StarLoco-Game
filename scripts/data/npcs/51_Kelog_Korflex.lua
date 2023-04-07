local npc = Npc(51, 9030)

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
    if answer == 0 then p:ask(25, {144, 365, 357})
    elseif answer == 144 then p:ask(170)
    elseif answer == 357 then p:ask(9256, {9656})
    elseif answer == 365 then p:ask(437)
    end
end

RegisterNPCDef(npc)
