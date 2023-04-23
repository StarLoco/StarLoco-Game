local npcId = 870

local s350 = QuestStep(350, 3718) -- TODO: Check dialog
s350.objectives = {
    BringItemObjective(758, npcId, 311, 4)
}


local quest = Quest(185, {s350})

