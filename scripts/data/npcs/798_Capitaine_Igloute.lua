local npc = Npc(798, 30)

npc.accessories = {0, 2043, 2042, 0, 0x1ba5}

function npc:onTalk(p, answer)
    SandyDungeon:onTalkToGateKeeper(p, answer) --TODO: Npc dialog end dungeon
end

RegisterNPCDef(npc)