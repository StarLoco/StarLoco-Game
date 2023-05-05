local qs344 = QuestStep(344, 3661)

qs344.objectives = {
    BringItemObjective(746, 859, 8529, 1)
}
qs344.rewardFn = QuestBasicReward(150, 0)

local q182 = Quest(182, {qs344})
q182.availableTo = questRequirements(0, 181, nil)
