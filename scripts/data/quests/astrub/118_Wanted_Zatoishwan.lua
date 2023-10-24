local qs204 = QuestStep(204)
local q118 = Quest(118, {qs204})

qs204.objectives = {
    GenericQuestObjective(428, {})
}
qs204.rewardFn = QuestBasicReward(0, 112500)

q118.availableTo = questRequirements(150)

q118.startFromDocHref = {98}
