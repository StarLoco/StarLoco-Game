local npc = Npc(109, 9042)

npc.gender = 1
npc.colors = {16312467,-1,-1}

function npc:onTalk(player, answer)
    player:ask(364, {})
end




RegisterNPCDef(npc)