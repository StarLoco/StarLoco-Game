local npc = Npc(99, 9033)

npc.gender = 1

npc.barters = {
    {to={itemID=304, quantity= 1}, from= {
        {itemID=761, quantity= 11}
    }},
	{to={itemID=678, quantity= 1}, from= {
    {itemID=309, quantity= 25}
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(308, {250, 298})
    elseif answer == 250 then p:ask(309)
	elseif answer == 298 then p:ask(310)
    end
end

RegisterNPCDef(npc)
