local qs201 = QuestStep(201)
local q115 = Quest(115, {qs201})

qs201.objectives = {
    GenericQuestObjective(421, {})
}
qs201.rewardFn = QuestBasicReward(0, 6000)

q115.availableTo = questRequirements(24)

q115.startFromDocHref = {93}
