local npc = Npc(120, 9041)

npc.colors = {2099287, 6241306, 11635033}

npc.barters = {
    {to={itemID=799, quantity= 1}, from= {
        {itemID=393, quantity= 70},
        {itemID=374, quantity= 70}
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(389)
    end
end

RegisterNPCDef(npc)
