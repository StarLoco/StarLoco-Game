local npcId = 869

local qs349 = QuestStep(349, 3710)
local q184 = Quest(184, {qs349})

qs349.objectives = {
    BringItemObjective(757, npcId, 289, 6),
    BringItemObjective(785, npcId, 421, 6)
}
qs349.rewardFn = function(p)
    p:addXP(250)
    p:addItem(6897, 2)
    p:addItem(8537)
end


