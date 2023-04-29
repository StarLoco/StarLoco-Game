local npc = Npc(91, 9045)

npc.colors = {5524810, 9612261, 14674175}

npc.barters = {
    {to={itemID=300, quantity= 1}, from= {
        {itemID=379, quantity= 3},
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(283, {226})
    elseif answer == 226 then p:ask(284)
    end
end

RegisterNPCDef(npc)
