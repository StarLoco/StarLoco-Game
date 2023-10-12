local npc = Npc(38, 9027)
--TODO: NPC Temple Iop > Check dialogs offi en étant Iop
npc.sales = {
    {item=494},
    {item=578},
    {item=1326}
}

npc.barters = {
    {to={itemID=683, quantity= 1}, from= {
        {itemID=377, quantity= 100}
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(100, {203, 372})
    elseif answer == 203 then p:ask(254)
    elseif answer == 372 then p:ask(445)
    end
end

RegisterNPCDef(npc)
