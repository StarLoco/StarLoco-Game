local npc = Npc(1163, 1047)

function npc:onTalk(p, answer)
    BulbDungeon:onTalkToGateKeeper(p, answer)
end

RegisterNPCDef(npc)