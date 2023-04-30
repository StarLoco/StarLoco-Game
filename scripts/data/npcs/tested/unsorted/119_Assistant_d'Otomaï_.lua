local npc = Npc(119, 120)

npc.accessories = {0, 8208, 0, 0, 0}

npc.barters = {
    {to={itemID=812, quantity= 1}, from= {
        {itemID=382, quantity= 40},
        {itemID=365, quantity= 75},
        {itemID=395, quantity= 60}
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(386, {310}, "[name]")
    elseif answer == 310 then p:ask(387)
    end
end

RegisterNPCDef(npc)
