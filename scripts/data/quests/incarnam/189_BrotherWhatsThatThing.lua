local npcId = 879

-- Étape de quête
local qs361 = QuestStep(361, 3330) -- Objectif : addFriendObjective
local q189 = Quest(189, {qs361})

-- Objectifs
qs361.objectives = {
    TalkWithQuestObjective(782, npcId)
}
qs361.rewardFn = QuestBasicReward(20, 0)