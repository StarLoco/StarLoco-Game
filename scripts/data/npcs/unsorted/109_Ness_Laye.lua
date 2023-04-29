local npc = Npc(109, 9042)

npc.gender = 1
npc.colors = {16312467, -1, -1}

npc.barters = {
    {to={itemID=808, quantity= 1},from= {
        {itemID=368, quantity= 25},
        {itemID=369, quantity= 20},
        {itemID=757, quantity= 60}
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(364, {297})
    elseif answer == 297 then p:ask(365)
    end
end

RegisterNPCDef(npc)
