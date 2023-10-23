local qs203 = QuestStep(203)
local q117 = Quest(117, {qs203})

qs203.objectives = {
    GenericQuestObjective(425, {})
}
qs203.rewardFn = QuestBasicReward(0, 25000)

qs203.availableTo =  questRequirements(50)

q117.startFromDocHref = {96}
