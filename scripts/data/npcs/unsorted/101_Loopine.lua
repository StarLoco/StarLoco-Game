local npc = Npc(101, 9015)

npc.barters = {
    {to={itemID=799, quantity= 1}, from= {
        {itemID=373, quantity= 70}
    }},
    {to={itemID=797, quantity= 1}, from= {
        {itemID=432, quantity= 45},
        {itemID=407, quantity= 75},
        {itemID=379, quantity= 75},
        {itemID=448, quantity= 30}
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(326, {292, 366})
    elseif answer == 292 then p:ask(356)
    elseif answer == 366 then p:ask(438)
    end
end

RegisterNPCDef(npc)
