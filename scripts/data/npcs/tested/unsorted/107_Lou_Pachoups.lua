local npc = Npc(107, 9043)

npc.gender = 1
npc.accessories = {}

npc.barters = {
    {to={itemID= 810, quantity= 1}, from= {
        {itemID= 757, quantity= 60},
        {itemID= 369, quantity= 25},
        {itemID= 368, quantity= 30},
        {itemID= 370, quantity= 1},
        {itemID= 2242, quantity= 1}
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(360, {300})
    elseif answer == 300 then p:ask(368)
    end
end

RegisterNPCDef(npc)
