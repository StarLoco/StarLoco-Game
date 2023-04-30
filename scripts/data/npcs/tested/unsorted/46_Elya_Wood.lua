local npc = Npc(46, 9026)

npc.sales = {
    {item=499},
    {item=1339}
}

npc.barters = {
    {to={itemID=686, quantity= 1}, from= {
        {itemID=860, quantity= 100}
    }},
    {to={itemID=290, quantity= 1}, from= {
        {itemID=499, quantity= 1}
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(91, {373, 220})
    elseif answer == 373 then
        p:ask(446)
    elseif answer == 220 then
        p:ask(275)
    end
end

RegisterNPCDef(npc)
