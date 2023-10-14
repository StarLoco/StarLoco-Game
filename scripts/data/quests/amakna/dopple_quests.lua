local requiredLevel = 9

-- TODO: Killing a dopple of the player's breed reward more XP

-- K: Grade V: {Kamas, XP}
local rewardsPerGrade = {
    {100,  1300},
    {200,  8000},
    {300,  18000},
    {400,  30000},
    {500,  45000},
    {600,  60000},
    {700,  85000},
    {800,   130000},
    {900,  170000},
    {1000, 185000},
}

local gradeForPlayer = function(p)
    return math.min(rewardsPerGrade[1 + math.floor(p:level() / 20)], 10)
end

--region Class Bang (kill all dopples)
local defeatAllStep = QuestStep(343)

defeatAllStep.rewardFn = function(p)
    local rewards = rewardsPerGrade[gradeForPlayer(p)]

    -- TODO
    -- p:modKamas(rewards[1])
    -- p:addXP(rewards[2])
end


local q470 = Quest(470, {defeatAllStep})
q470.availableTo = questRequirements(requiredLevel)
q470.isRepeatable = true

--endregion


local doppleInfo = {
    -- Panda
    {bangObjectiveID=3188, mobID=2691, questID=466, doploonID=10309, certificateID=10296 },
    -- Enu
    {bangObjectiveID=3189, mobID=162,  questID=464, doploonID=10305, certificateID=10292 },
    -- Eca
    {bangObjectiveID=3190, mobID=165,  questID=459, doploonID=10303, certificateID=10290 },
    -- Iop
    {bangObjectiveID=3191, mobID=167,  questID=460, doploonID=10307, certificateID=10294 },
    -- Osa
    {bangObjectiveID=3192, mobID=161,  questID=461, doploonID=10308, certificateID=10295 },
    -- Eni
    {bangObjectiveID=3193, mobID=166,  questID=462, doploonID=10304, certificateID=10291 },
    -- Sacri
    {bangObjectiveID=3194, mobID=455,  questID=463, doploonID=10310, certificateID=10297 },
    -- Sadi
    {bangObjectiveID=3195, mobID=169,  questID=465, doploonID=10311, certificateID=10298 },
    -- Feca
    {bangObjectiveID=3196, mobID=160,  questID=469, doploonID=10306, certificateID=10293 },
    -- Cra
    {bangObjectiveID=3197, mobID=168,  questID=458, doploonID=10302, certificateID=10289 },
    -- Xelor
    {bangObjectiveID=3198, mobID=164,  questID=468, stepID=991, objectiveID=3178, doploonID=10313, certificateID=10300 },
    -- Sram
    {bangObjectiveID=3199, mobID=163,  questID=467, stepID=990, objectiveID=3176, doploonID=10312, certificateID=10299 },
}


for _, d in pairs(doppleInfo) do
    -- Add dopple as objective for Class Bang quest
    table.insert(defeatAllStep.objectives, KillMonsterSingleFightObjective(d.bangObjectiveID, d.mobID, 1))

    --Create quest for killing a single dopple
    local defeatSingleStep = QuestStep(d.stepID)
    defeatSingleStep.objectives = {
        KillMonsterSingleFightObjective(d.objectiveID, d.mobID, 1)
    }
    defeatSingleStep.rewardFn = function(p)
        local rewards = rewardsPerGrade[gradeForPlayer(p)]

        p:modKamas(rewards[1])
        p:addXP(rewards[2])
        return
    end

    local defeatSingleQuest = Quest(d.questID, {defeatSingleStep})
    defeatSingleQuest.availableTo = questRequirements(requiredLevel)
    defeatSingleQuest.isRepeatable = true
end