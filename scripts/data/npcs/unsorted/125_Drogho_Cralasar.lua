local npc = Npc(125, 9045)

npc.barters = {
    {to={itemID=816, quantity= 1}, from= {
        {itemID=376, quantity= 30},
        {itemID=362, quantity= 45},
        {itemID=364, quantity= 45},
        {itemID=363, quantity= 45}
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(400, {318})
    elseif answer == 318 then p:ask(401)
    end
end

RegisterNPCDef(npc)
