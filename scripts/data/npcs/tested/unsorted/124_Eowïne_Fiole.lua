local npc = Npc(124, 9014)

npc.barters = {
    {to={itemID=815, quantity= 1}, from= {
        {itemID=362, quantity= 50},
        {itemID=364, quantity= 40},
        {itemID=363, quantity= 40}
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(398, {317})
    elseif answer == 317 then p:ask(399)
    end
end

RegisterNPCDef(npc)
