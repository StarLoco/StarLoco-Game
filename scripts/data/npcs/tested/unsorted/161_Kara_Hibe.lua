local npc = Npc(161, 9062)

npc.barters = {
    {to={itemID=1023, quantity= 1}, from= {
        {itemID=1001, quantity= 2},
        {itemID=1018, quantity= 1}
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(591, {490})
    elseif answer == 490 then p:ask(592)
    end
end

RegisterNPCDef(npc)
