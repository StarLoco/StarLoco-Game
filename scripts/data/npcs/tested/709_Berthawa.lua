local npc = Npc(709, 9075)

npc.accessories = {0, 0x1bea, 0, 0, 0}

function npc:onTalk(p, answer)
    FirefouxDungeon:onTalkToGateKeeper(p, answer)
end

RegisterNPCDef(npc)