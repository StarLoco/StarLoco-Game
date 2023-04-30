local npc = Npc(110, 9043)

npc.gender = 1

npc.barters = {
    {to={itemID=807, quantity= 1}, from= {
        {itemID=757, quantity= 80},
        {itemID=369, quantity= 30}
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(367, {300, 301})
    elseif answer == 300 then p:ask(368)
    elseif answer == 301 then p:ask(369)
    end
end

RegisterNPCDef(npc)
