local npc = Npc(108, 9014)

npc.gender = 1

npc.barters = {
    {to={itemID=683, quantity= 1}, from= {
        {itemID=407, quantity= 80}
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(362, {296})
    elseif answer == 296 then p:ask(363)
    end
end

RegisterNPCDef(npc)
