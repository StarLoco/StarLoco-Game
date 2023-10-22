local npcId = 505
local wildeNpcId = 506
local mapId = 7571

local qs135 = QuestStep(135, 1858)
local qs146 = QuestStep(146, 1858)
local q45 = Quest(45, {qs135, qs146})

qs135.objectives = {
    DiscoverMapObjective(264, mapId)
}
qs135.rewardFn = QuestBasicReward(4000, 0)

qs146.objectives = q45:SequentialObjectives( {
    KillMonsterSingleFightObjective(305, 465, 1),
    TalkWithQuestObjective(306, wildeNpcId),
    TalkWithQuestObjective(309, npcId)
})
qs146.rewardFn = QuestBasicReward(8000, 0)
