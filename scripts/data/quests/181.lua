local qs343 = QuestStep(343, 3652)
--TODO: FUNCTION DIABU
qs343.objectives = {
    TalkWithQuestObjective(745, 857),
    BringItemObjective(744, 857, 8528, 1)
}
qs343.rewardFn = QuestBasicReward(75, 0)

local q181 = Quest(181, {qs343})
