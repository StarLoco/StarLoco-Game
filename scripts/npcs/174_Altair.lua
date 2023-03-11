local npc = Npc(174, 9059)

npc.colors = {6380875, 16777215, 11250604}

function npc:onTalk(player, answer)
    BouftousDungeon:onTalkToGateKeeper(player, answer)
end

RegisterNPCDef(npc)