local npcId = 871

local qs759 = QuestStep(351, 3728)
local q186 = Quest(186, {qs759})

qs759.objectives = {
    TalkWithQuestObjective(759, 870)
}
qs759.rewardFn = QuestBasicReward(75, 0)
