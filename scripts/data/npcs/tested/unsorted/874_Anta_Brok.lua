local npc = Npc(874, 9013)

npc.gender = 1
npc.colors = {10118252, 15590193, 15395386}
npc.accessories = {0, 0, 0, 1728, 0}

npc.barters = {
    {to={itemID=8538, quantity= 1}, from= {
        {itemID=8546, quantity= 1},
        {itemID=421, quantity= 5},
        {itemID=303, quantity= 10},
        {itemID=1782, quantity= 1},
        {itemID=289, quantity= 10},
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2003)
    end
end

RegisterNPCDef(npc)
