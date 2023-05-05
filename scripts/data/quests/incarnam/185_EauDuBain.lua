local npcId = 870

local qs350 = QuestStep(350, 3718)
qs350.objectives = {
    BringItemObjective(758, npcId, 311, 4)
}
qs350.rewardFn = QuestBasicReward(75, 25)

local q185 = Quest(185, {qs350})
