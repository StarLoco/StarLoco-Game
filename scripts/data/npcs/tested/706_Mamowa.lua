local npc = Npc(706, 1260)

function npc:onTalk(p, answer)
    PandikazesDungeon:onTalkToGateKeeper(p, answer)
end

RegisterNPCDef(npc)