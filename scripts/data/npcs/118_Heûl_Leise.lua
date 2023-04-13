local npc = Npc(118, 9037)

npc.barters = {
    {to={itemID=796, quantity= 1}, from= {
        {itemID=432, quantity= 40},
        {itemID=407, quantity= 70},
        {itemID=379, quantity= 75}
    }},
    {to={itemID=799, quantity= 1}, from= {
        {itemID=393, quantity= 70},
        {itemID=374, quantity= 70}
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(384, {309})
    elseif answer == 309 then p:ask(385)
    end
end

RegisterNPCDef(npc)
