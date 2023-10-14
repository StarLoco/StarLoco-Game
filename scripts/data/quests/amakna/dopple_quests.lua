local requiredLevel = 9
local questIntervalMs = 82800000 -- 23 Hours

-- TODO: Killing a dopple of the player's breed reward more XP. Probably directly in fight

-- K: Grade V: {Kamas, XP}
local rewardsPerGrade = {
    {100,  1300},
    {200,  8000},
    {300,  18000},
    {400,  30000},
    {500,  45000},
    {600,  60000},
    {700,  85000},
    {800,  130000},
    {900,  170000},
    {1000, 185000},
}

---@param p Player
---@return number
local gradeForPlayer = function(p)
    return math.min(1 + math.floor(p:level() / 20), 10)
end

--region Class Bang (kill all dopples)
local defeatAllStep = QuestStep(995)

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
    {bangObjectiveID=3188, mobID=2691, questID=466, stepID=991, objectiveID=3174, doploonID=10309, certificateID=10296 },
    -- Enu
    {bangObjectiveID=3189, mobID=162,  questID=464, stepID=987, objectiveID=3170, doploonID=10305, certificateID=10292 },
    -- Eca
    {bangObjectiveID=3190, mobID=165,  questID=459, stepID=982, objectiveID=3160, doploonID=10303, certificateID=10290 },
    -- Iop
    {bangObjectiveID=3191, mobID=167,  questID=460, stepID=979, objectiveID=3162, doploonID=10307, certificateID=10294 },
    -- Osa
    {bangObjectiveID=3192, mobID=161,  questID=461, stepID=984, objectiveID=3164, doploonID=10308, certificateID=10295 },
    -- Eni
    {bangObjectiveID=3193, mobID=166,  questID=462, stepID=985, objectiveID=3166, doploonID=10304, certificateID=10291 },
    -- Sacri
    {bangObjectiveID=3194, mobID=455,  questID=463, stepID=986, objectiveID=3168, doploonID=10310, certificateID=10297 },
    -- Sadi
    {bangObjectiveID=3195, mobID=169,  questID=465, stepID=988, objectiveID=3172, doploonID=10311, certificateID=10298 },
    -- Feca
    {bangObjectiveID=3196, mobID=160,  questID=469, stepID=992, objectiveID=3180, doploonID=10306, certificateID=10293 },
    -- Cra
    {bangObjectiveID=3197, mobID=168,  questID=458, stepID=982, objectiveID=3147, doploonID=10302, certificateID=10289 },
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

        -- Remove old certificate
        p:consumeItem(d.certificateID, 1)
        p:addItem(d.certificateID) -- TODO: Set date on item ?

        p:addItem(d.doploonID)
        p:modKamas(rewards[1])
        p:addXP(rewards[2])
        return
    end

    local defeatSingleQuest = Quest(d.questID, {defeatSingleStep})
    defeatSingleQuest.availableTo = questRequirements(requiredLevel) -- TODO: Check certificate date ?
    defeatSingleQuest.isRepeatable = true

    -- Helper for starting the fight for a player
    ---@param self Quest
    ---@param p Player
    ---@return boolean worked
    defeatSingleQuest.startFightFor = function(self, p, npcId)
        if self:availableTo(p) then
            self:startFor(p, npcId)
        elseif not self:ongoingFor(p) then return false
        end

        local grade = gradeForPlayer(p)
        p:endDialog()
        p:forceFight({-1, {
            {d.mobID, {grade}}
        }})
        return true
    end
end