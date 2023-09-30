local npcId = 887

local qs372 = QuestStep(372, 3832)
qs372.objectives = {
    BringItemObjective(810, npcId, 884, 2)
}
qs372.rewardFn = QuestBasicReward(75, 75)

local q199 = Quest(199, {qs372})
