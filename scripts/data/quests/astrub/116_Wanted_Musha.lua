local qs202 = QuestStep(202)
local q116 = Quest(116, {qs202})

qs202.objectives = {
    GenericQuestObjective(423, {})
}
qs202.rewardFn = QuestBasicReward(0, 64000)

q116.availableTo = questRequirements(80)

q116.startFromDocHref = {94}
