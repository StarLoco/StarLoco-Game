local npc = Npc(117, 9045)

npc.barters = {
    {to={itemID=814, quantity= 1}, from= {
        {itemID=382, quantity= 40},
        {itemID=365, quantity= 60},
        {itemID=395, quantity= 55},
        {itemID=438, quantity= 15}
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(382, {308})
    elseif answer == 308 then p:ask(383)
    end
end

RegisterNPCDef(npc)
