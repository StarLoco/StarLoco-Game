local qs111 = QuestStep(111)
local q34 = Quest(34, {qs111})

qs111.objectives = {
    GenericQuestObjective(207, {})
}
qs111.rewardFn = QuestBasicReward(0, 200)

q34.availableTo = questRequirements(5)

q34.startFromDocHref = {70}
