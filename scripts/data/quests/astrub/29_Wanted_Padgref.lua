local qs121 = QuestStep(121)
local q29 = Quest(29, {qs121})

qs121.objectives = {
    GenericQuestObjective(215, {})
}
qs121.rewardFn = QuestBasicReward(0, 72500)

q29.availableTo = questRequirements(126)

q29.startFromDocHref = {61}
