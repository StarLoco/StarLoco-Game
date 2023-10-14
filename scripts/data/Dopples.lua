local classBangQuestID = 470
local requiredLevel = 9
local questIntervalMs = 82800000 -- 23 Hours
local busyInitID = 1834 -- Used when player cannot start new dopple fight

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

---@class DoppleInfo
---@field mob number
---@field npc table
---@field quest table
---@field dialog table
---@field doploon number
---@field certificate number

---@type table<DoppleInfo>
local dopples = {
    -- Panda
    {
        mob = 2691,
        npc = {id=672, gfx=1260},
        quest = {id=466, step=991, bangObjective=3188, fightObjective=3174, npcObjective=3175 },
        dialog = {init=2851, info=6757, train=6429, trade=6598, classBang=6778, reward=??},
        doploon = 10309,
        certificate = 10296,
    },
    -- Iop
    {
        mob = 167,
        npc = {id=434, gfx=80, colors = {10539564, 13816530, 15130814}, accessories = {0, 0, 744, 0, 0}},
        quest = {id=460, step=979, bangObjective=3191, fightObjective=3162, npcObjective=3163 },
        dialog = {init=1771, info=1424, train=1559, trade=6754, classBang=6776},
        doploon = 10307,
        certificate = 10294,
    },
}

--region NPC
---@return boolean true if matching
local function makeTradeMenu(info)
    return function(p, answer)
        if answer == info.dialog.trade then
            p:endDialog() -- FIXME
        end
        return false
    end
end

---@return boolean true if matching
local function makeInfoMenu(info)
    return function(p, answer)
        if answer == info.dialog.info then
            p:endDialog() -- FIXME
        end
        return false
    end
end

local onTalkDoppleMaster = function(info)
    local infoMenu = makeInfoMenu(info)
    local tradeMenu = makeTradeMenu(info)

    return function(self, p, answer)
        local doppleQuest = QUESTS[info.quest.id]
        local classBangQuest = QUESTS[classBangQuestID]

        if answer == 0 then
            -- Player already finished this dopple quest too recently
            if not doppleQuest:availableTo(p) and not doppleQuest:ongoingFor(p) then
                p:ask(busyInitID, {info.dialog.trade})
                return
            end

            local responses = {info.dialog.trade}
            if doppleQuest:availableTo(p) or doppleQuest:ongoingFor(p) then
                table.insert(responses, info.dialog.train)
                table.insert(responses, info.dialog.info)
            end
            if classBangQuest:availableTo(p) then table.insert(responses, info.dialog.classBang) end

            p:ask(info.dialog.init, responses)
        elseif answer == info.dialog.classBang then
            -- TODO: add intermediary dialog
            p:endDialog()
            classBangQuest:startFor(p, self.id)
        elseif answer == info.dialog.train then
            -- TODO: add intermediary dialog
            if not doppleQuest:startFightFor(p, self.id) then
                error("cheat attempt")
            end
        elseif infoMenu(p, answer) then return
        elseif tradeMenu(p, answer) then  return
        end
    end
end

local createNPC = function(info)
    local npc = Npc(info.npc.id, info.npc.gfx)

    if info.colors then npc.colors = info.colors end
    if info.accessories then npc.accessories = info.accessories end

    npc.onTalk = onTalkDoppleMaster(info)
    npc.quests = {info.quest.id}

    RegisterNPCDef(npc)
end

--endregion

--region Quest

local createQuest = function(info)
    -- Add dopple as objective for Class Bang quest
    table.insert(defeatAllStep.objectives, KillMonsterSingleFightObjective(info.quest.bangObjective, info.mob, 1))

    -- Create quest step for killing dopple
    local defeatSingleStep = QuestStep(info.quest.step)

    --Create quest for killing a single dopple
    local defeatSingleQuest = Quest(info.quest.id, {defeatSingleStep})
    defeatSingleQuest.availableTo = questRequirements(requiredLevel) -- TODO: Check certificate date ?
    defeatSingleQuest.isRepeatable = true

    -- Step objectives
    defeatSingleStep.objectives = defeatSingleQuest:SequentialObjectives({
        KillMonsterSingleFightObjective(info.quest.fightObjective, info.mob, 1),
        TalkWithQuestObjective(info.quest.npcObjective, info.npc.id),
    })

    -- Step reward
    defeatSingleStep.rewardFn = function(p)
        local rewards = rewardsPerGrade[gradeForPlayer(p)]

        -- FIXME Certificates are drops on official servers
        -- Remove old certificate
        p:consumeItem(info.certificate, 1)
        p:addItem(info.certificate) -- TODO: Set date on item ?

        p:addItem(info.doploon)
        p:modKamas(rewards[1])
        p:addXP(rewards[2])
        return
    end

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
            {info.mob, {grade}}
        }})
        return true
    end
end
--endregion

for _, info in pairs(dopples) do
    createNPC(info)
    createQuest(info)
end