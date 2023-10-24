local qs119 = QuestStep(119)
local q30 = Quest(30, {qs119})

qs119.objectives = {
    GenericQuestObjective(213, {})
}
qs119.rewardFn = QuestBasicReward(0, 2000)

q30.availableTo = questRequirements(20)

q30.startFromDocHref = {63}
