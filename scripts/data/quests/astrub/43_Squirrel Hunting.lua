local mobID = 469
local mobCount = 3
local objectiveID = 301

local qs131 = QuestStep(131,2245)

local q43 = Quest(43, {qs131})

qs131.objectives = {
    KillMonsterSingleFightObjective( objectiveID, mobID, mobCount),
}

qs131.rewardFn = QuestBasicReward(2500, 20)
