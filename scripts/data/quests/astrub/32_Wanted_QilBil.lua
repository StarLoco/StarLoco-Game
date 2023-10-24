local qs115 = QuestStep(115)
local q32 = Quest(32, {qs115})

qs115.objectives = {
    GenericQuestObjective(425, {}) -- FIXME Wrong objective
}
qs115.rewardFn = QuestBasicReward(0, 16000)

q32.availableTo = questRequirements(50)

q32.startFromDocHref = {67}
