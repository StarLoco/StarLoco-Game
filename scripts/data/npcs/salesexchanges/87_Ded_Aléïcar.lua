local npc = Npc(87, 9036)

npc.barters = {
    {to={itemID=818, quantity= 1}, from= {
        {itemID=301, quantity= 10},
        {itemID=757, quantity= 2}
    }}
}

function npc:onTalk(p, answer)
    if answer == 0 then p:ask(238, {191, 192})
	elseif answer == 191 then p:ask(239)
	elseif answer == 192 then p:ask(240, {193})
	elseif answer == 193 then p:ask(241, {194})
	elseif answer == 194 then p:ask(242)
    end
end

RegisterNPCDef(npc)
