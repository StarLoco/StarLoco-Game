local npcId = 846
local mapId = 10307

local qs332 = QuestStep(332, 3569)
qs332.objectives = {
    BringItemObjective(720, npcId, 384, 3)
}
qs332.rewardFn = QuestBasicReward(120, 150)

local qs346 = QuestStep(346, 3571)
qs346.objectives = {
    DiscoverMapObjective(750, mapId)
}
qs346.rewardFn = QuestBasicReward(25, 0)

local q173 = Quest(173, {qs332, qs346})
