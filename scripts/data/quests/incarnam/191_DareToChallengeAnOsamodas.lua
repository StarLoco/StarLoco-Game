
local mobID = 1002
local objID = 786
local mobCount = 1
local minLevel = 7

local qs363 = QuestStep(363) -- TODO Manque la bubulle de discussion

local q191 = Quest(191, {qs363})

q191.availableTo =  questRequirements(minLevel)

qs363.objectives = {
    KillMonsterSingleFightObjective(objID, mobID, mobCount)
}

qs363.rewardFn = function(p)
    p:addXP(500)
    p:addItem(8534)
end
