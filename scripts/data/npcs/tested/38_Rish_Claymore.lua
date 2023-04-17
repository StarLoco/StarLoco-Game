local npc = Npc(38, 9027)

npc.sales = {
    {item=1326},
    {item=578}
}

npc.barters = {
    {to={itemID=683, quantity= 1}, from= {
        {itemID=377, quantity= 100},
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(100, {203, 37})
    elseif answer == 203 then p:ask(254)
    end
end

RegisterNPCDef(npc)
