local npc = Npc(121, 9048)

npc.barters = {
    {to={itemID=811, quantity= 1}, from= {
        {itemID=365, quantity= 90},
        {itemID=395, quantity= 65}
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(390, {314})
    elseif answer == 314 then p:ask(393)
    end
end

RegisterNPCDef(npc)
