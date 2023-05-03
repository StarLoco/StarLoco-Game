local qs358 = QuestStep(358, 3771)
qs358.objectives = {
    KillMonsterSingleFightObjective(778, 999, 1)
}
qs358.rewardFn = QuestBasicReward(25, 0)

local q188 = Quest(188, {qs358})
