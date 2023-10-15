local npcId = 847

local qs333 = QuestStep(333, 3573)
qs333.objectives = {
    KillMonsterSingleFightObjective(721, 976, 1),
    KillMonsterSingleFightObjective(722, 974, 1),
    KillMonsterSingleFightObjective(723, 971, 1),
    KillMonsterSingleFightObjective(807, 980, 1)
}
qs333.rewardFn = function(p)
    p:addXP(370)
    p:addItem(8536)
end

local q174 = Quest(174, {qs333})
