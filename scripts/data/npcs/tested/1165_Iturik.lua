local npc = Npc(1165, 1265)

function npc:onTalk(p, answer)
    BulbDungeon:onTalkToGateKeeper(p, answer)
end

RegisterNPCDef(npc)