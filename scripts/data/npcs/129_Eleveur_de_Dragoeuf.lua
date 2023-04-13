local npc = Npc(129, 9003)

npc.barters = {
    {to={itemID=848, quantity= 1}, from= {
        {itemID=847, quantity= 1}
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(433, {359})
    elseif answer == 359 then p:ask(434, {360})
    elseif answer == 360 then p:ask(435, {4840})
    elseif answer == 4840 then p:endDialog()
    end
end

RegisterNPCDef(npc)
