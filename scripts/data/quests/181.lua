local qs343 = QuestStep(343, 3652)
local q181 = Quest(181, {qs343})

qs343.objectives = q181:SequentialObjectives({
    TalkWithQuestObjective(745, 857),
    BringItemObjective(744, 857, 8528, 1)
})
qs343.rewardFn = QuestBasicReward(75, 0)