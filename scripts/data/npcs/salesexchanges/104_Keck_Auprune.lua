local npc = Npc(104, 9045)

npc.barters = {
    {to={itemID=683, quantity= 1}, from= {
        {itemID=362, quantity= 100}
    }},
    {to={itemID=684, quantity= 1}, from= {
        {itemID=315, quantity= 5}
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(354)
    end
end

RegisterNPCDef(npc)
