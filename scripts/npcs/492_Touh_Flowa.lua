local npc = Npc(492, 100)

npc.colors = {15720002, 15713442, 7891744}

function npc:onTalk(player, answer)
    player:ask(1998, {})
end

RegisterNPCDef(npc)
