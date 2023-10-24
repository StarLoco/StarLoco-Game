local qs109 = QuestStep(109)
local q35 = Quest(35, {qs109})

qs109.objectives = {
    GenericQuestObjective(313, {})
}
qs109.rewardFn = QuestBasicReward(0, 32000)

q35.availableTo = questRequirements(42)

q35.startFromDocHref = {69}
