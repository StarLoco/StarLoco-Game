local qs343 = QuestStep(343, 3652)
qs343.objectives = {
    TalkWithQuestObjective(745, 857),
}
qs343.rewardFn = QuestBasicReward(50, 0)

local qs342 = QuestStep(342, 3657)
qs342.objectives = {
    BringItemObjective(744, 858, 8528, 1),
}
qs342.rewardFn = QuestBasicReward(0, 75)

local q181 = Quest(181, {qs343, qs342})
