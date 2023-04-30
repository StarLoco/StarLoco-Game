local npcId = 868

local qs348 = QuestStep(348, nil)
qs348.objectives = {
    TalkWithQuestObjective(756, npcId)
}
qs348.rewardFn = QuestBasicReward(0, 0)

local q183 = Quest(183, {qs348})
