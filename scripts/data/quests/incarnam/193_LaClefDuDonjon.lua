local qs365 = QuestStep(365, 3813)
local q193 = Quest(193, {qs365})
local npcId = 884

qs365.objectives = q193:SequentialObjectives({
    KillMonsterSingleFightObjective(793, 984, 1),
    TalkAgainToObjective(794, npcId),
})

qs365.rewardFn = function(p)
    p:addXP(55)
    p:addItem(8545)
end

