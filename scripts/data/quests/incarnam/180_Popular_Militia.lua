local xeloratNpcId = 859
local cripeledNpcId = 848
local babbleraNpcId = 858
local npcId = 855

local qs347 = QuestStep(347, 3681)
local q180 = Quest(180, {qs347})

q180.availableTo = questRequirements(0, 179, nil)

-- qs347.objectives = {
--     TalkWithQuestObjective(752, babbleraNpcId),
--     TalkWithQuestObjective(753, xeloratNpcId),
--     TalkWithQuestObjective(754, cripeledNpcId),
--     TalkAgainToObjective(755, npcId)
-- }

qs347.objectives = function(p)
    local objectives = {
        TalkWithQuestObjective(752, babbleraNpcId),
        TalkWithQuestObjective(753, xeloratNpcId),
        TalkWithQuestObjective(754, cripeledNpcId),
    }

    if q180:hasCompletedObjectives(p, {752, 753, 754}) then
        table.insert(objectives, TalkAgainToObjective(755, npcId))
    end

    return objectives 
end

qs347.rewardFn = function(p)
    p:addXP(150)
    p:addItem(8535, 1)
end
