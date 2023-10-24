local qs206 = QuestStep(206)
local q119 = Quest(119, {qs206})

qs206.objectives = {
    GenericQuestObjective(431, {})
}
qs206.rewardFn = QuestBasicReward(0, 16000)

q119.availableTo = questRequirements(50)

q119.startFromDocHref = {100}
