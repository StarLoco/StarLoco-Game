local npc = Npc(123, 9047)

npc.barters = {
    {to={itemID=817, quantity= 1}, from= {
        {itemID=376, quantity= 40},
        {itemID=362, quantity= 60},
        {itemID=364, quantity= 50},
        {itemID=363, quantity= 50},
        {itemID=752, quantity= 30}
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(396, {316})
    elseif answer == 316 then p:ask(397)
    end
end

RegisterNPCDef(npc)
