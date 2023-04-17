local npc = Npc(707, 9075)

function npc:onTalk(p, answer)
    KitsouneDungeon:onTalkToGateKeeper(p, answer)
end

RegisterNPCDef(npc)