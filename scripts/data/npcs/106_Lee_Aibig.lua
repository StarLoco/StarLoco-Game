local npc = Npc(106, 9047)

npc.barters = {
    {to={itemID=800, quantity= 1}, from= {
        {itemID=393, quantity= 75},
        {itemID=374, quantity= 75},
        {itemID=309, quantity= 70}
    }},
    {to={itemID=801, quantity= 1}, from= {
        {itemID=393, quantity= 80},
        {itemID=374, quantity= 80},
        {itemID=309, quantity= 80},
        {itemID=378, quantity= 20}
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(357, {293, 294})
    elseif answer == 293 then p:ask(359)
    elseif answer == 294 then p:ask(358)
    end
end

RegisterNPCDef(npc)
