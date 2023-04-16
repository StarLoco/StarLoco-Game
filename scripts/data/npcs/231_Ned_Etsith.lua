local npc = Npc(231, 9045)

npc.colors = {4466728, 16119285, 12303292}

function npc:onTalk(p, answer)
    if answer == 0 then p:ask(983)
	end
end

RegisterNPCDef(npc)
