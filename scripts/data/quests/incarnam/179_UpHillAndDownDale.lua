local exitNpcId = 860
local bridgeNpcId = 861
local graveyardNpcId = 862
local npcId = 855

local qs345 = QuestStep(345, 3636)
local q179 = Quest(179, {qs345})

qs345.objectives = q179:SequentialObjectives( {
    TalkWithQuestObjective(747, exitNpcId),
    TalkWithQuestObjective(748, bridgeNpcId),
    TalkWithQuestObjective(749, graveyardNpcId),
    TalkWithQuestObjective(751, npcId)
})
qs345.rewardFn = QuestBasicReward(230, 0)
