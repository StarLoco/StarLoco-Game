local qs113 = QuestStep(113)
local q33 = Quest(33, {qs113})

qs113.objectives = {
    GenericQuestObjective(209, {})
}
qs113.rewardFn = QuestBasicReward(0, 33600)

q33.availableTo = questRequirements(58)

q33.startFromDocHref = {68}
