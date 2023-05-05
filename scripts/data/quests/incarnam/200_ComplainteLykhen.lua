local npcId = 891

local qs373 = QuestStep(373, 3854)
qs373.objectives = {
    TalkWithQuestObjective(811, npcId)
}
qs373.rewardFn = QuestBasicReward(15, 0)

local q200 = Quest(200, {qs373})
