local classBangQuestID = 470
local requiredLevel = 9
local dateStatID = 805 -- Move to stat.lua whenever
local questIntervalMs = 82800000 -- 23 Hours

local rewardInitID = 7106 -- Used on init after killing objective is finished
local busyInitID = 1834 -- Used when player cannot start new dopple fight
local tooLowInitID = 1784 -- Used when the player doesn't have the level


-- K: Grade V: {Kamas, XPSingle, XPClassBang}
local rewardsPerGrade = {
    {100,  1300,   3000},
    {200,  8000,   25000},
    {300,  18000,  50000},
    {400,  30000,  75000},
    {500,  45000,  125000},
    {600,  60000,  300000},
    {700,  85000,  300000},
    {800,  130000, 500000},
    {900,  170000, 750000},
    {1000, 185000, 1000000},
}

---@type table<SaleOffer>
local doploonSales = {
    -- Small scrolls
    {item=683, price=7}, -- STR
    {item=686, price=7}, -- INT
    {item=798, price=7}, -- AGI
    {item=802, price=7}, -- WIS
    {item=806, price=7}, -- VIT
    {item=809, price=7}, -- CHA

    -- Normal scrolls
    {item=795, price=14}, -- STR
    {item=815, price=14}, -- INT
    {item=799, price=14}, -- AGI
    {item=803, price=14}, -- WIS
    {item=807, price=14}, -- VIT
    {item=811, price=14}, -- CHA

    -- Big scrolls
    {item=796, price=28}, -- STR
    {item=816, price=28}, -- INT
    {item=800, price=28}, -- AGI
    {item=804, price=28}, -- WIS
    {item=808, price=28}, -- VIT
    {item=812, price=28}, -- CHA

    -- Powerful scrolls
    {item=797, price=70}, -- STR
    {item=817, price=70}, -- INT
    {item=801, price=70}, -- AGI
    {item=805, price=70}, -- WIS
    {item=810, price=70}, -- VIT
    {item=814, price=70}, -- CHA
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
    p:addXP(rewards[3])
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
        dialog = {init=2851, info=6757, train=6429, trade=6598, classBang=6778},
        doploon = 10309,
        certificate = 10296,
    },
    -- Enu
    {
        mob = 162,
        npc = {id=440, gfx=30, colors = {16419588, 16710905, 10929091}},
        quest = {id=464, step=987, bangObjective=3189, fightObjective=3170, npcObjective=3171 },
        dialog = {init=1764, info=1417, train=172, trade=6752, classBang=6774},
        doploon = 10305,
        certificate = 10292,
    },
    --Eca
    {
        mob = 165,
        npc = {id=438, gfx=60, colors = {16777215, 16448765, 12498572}, accessories = {0, 0, 2629, 0, 0}},
        quest = {id=459, step=982, bangObjective=3190, fightObjective=3160, npcObjective=3161 },
        dialog = {init=1772, info=1425, train=1515, trade=6750, classBang=6769},
        doploon = 10303,
        certificate = 10290,
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
    -- Osa
    {
        mob = 161,
        npc = {id=436, gfx=20, accessories = {0, 2411, 2414, 0, 0}},
        quest = {id=461, step=984, bangObjective=3192, fightObjective=3164, npcObjective=3165 },
        dialog = {init=1766, info=1418, train=1485, trade=6755, classBang=6777},
        doploon = 10308,
        certificate = 10295,
    },
    -- Eni
    {
        mob = 166,
        npc = {id=435, gfx=70, colors = {0xd0000, 0xbb8162, -1}, accessories = {0, 0, 6449, 0, 0}},
        quest = {id=462, step=985, bangObjective=3193, fightObjective=3166, npcObjective=3167 },
        dialog = {init=1768, info=1535, train=1559, trade=6751, classBang=6773},
        doploon = 10304,
        certificate = 10291,
    },
    --Sacri
    {
        mob = 455,
        npc = {id=443, gfx=110, colors= {0x62827d, 0xae0404, -1}, accessories = {0, 0, 6449, 2074, 0}},
        quest = {id=463, step=986, bangObjective=3194, fightObjective=3168, npcObjective=3169 },
        dialog = {init=1799, info=1519, train=1520, trade=6758, classBang=6779},
        doploon = 10310,
        certificate = 10297,
    },
    -- Sadi
    {
        mob = 169,
        npc = {id=442, gfx=100, colors = {328193, 12226652, 12226652}, accessories = {0, 0, 772, 0, 0}},
        quest = {id=465, step=988, bangObjective=3195, fightObjective=3172, npcObjective=3173 },
        dialog = {init=1770, info=1422, train=1581, trade=6759, classBang=6780},
        doploon = 10311,
        certificate = 10298,
    },
    -- Feca
    {
        mob = 160,
        npc = {id=433, gfx=10, accessories = {0, 697, 697, 1711, 0}},
        quest = {id=469, step=992, bangObjective=3196, fightObjective=3180, npcObjective=3181 },
        dialog = {init=1769, info=1420, train=216, trade=6753, classBang=6775},
        doploon = 10306,
        certificate = 10293,
    },
    --Cra
    {
        mob = 168,
        npc = {id=439, gfx=90, colors = {7217955, 2902400, 16777215}, accessories = {0, 0, 779, 0, 0}},
        quest = {id=458, step=982, bangObjective=3197, fightObjective=3147, npcObjective=3148 },
        dialog = {init=1767, info=1419, train=1509, trade=6697, classBang=6772},
        doploon = 10302,
        certificate = 10289,
    },
    --Xelor
    {
        mob = 164,
        npc = {id=437, gfx=50, colors = {1245184, 1703936, 16777215}},
        quest = {id=468, step=991, bangObjective=3198, fightObjective=3178, npcObjective=3179 },
        dialog = {init=1758, info=168, train=1415, trade=6761, classBang=6782},
        doploon = 10313,
        certificate = 10300,
    },
    --Sram
    {
        mob = 163,
        npc = {id=441, gfx=40, colors = {10891569, 0, 16050666}, accessories = {0, 699, 677, 0, 0}},
        quest = {id=467, step=990, bangObjective=3199, fightObjective=3176, npcObjective=3177 },
        dialog = {init=1773, info=1426, train=1575, trade=6760, classBang=6781},
        doploon = 10312,
        certificate = 10299,
    },
}

--region NPC

--- Doploon trade menu
---@return boolean true if matching
local function makeTradeMenu(info)
    -- Official servers duplicate dialogs/responses a lot which would make the script way less maintainable
    -- Instead, we will just use the dialogs with the lowest ID for every NPC and store choices in action's context
    local ctxScrollSize = "scroll_size"
    local ctxScrollStat = "scroll_stat"

    local notEnoughDoploonsQID = 7519

    local exitRID = 6768
    local scrollRID = 7355
    local scrollSizeRID = {7356, 7357, 7358, 7359} -- little/medium/big/powerful
    local scrollStatRID = {7364, 7362, 7360, 7365, 7363, 7361} -- WIS / CHA / AGI / VIT / STR / INT
    local classDoploonRID = {7395, 7396} -- each / this temple


    return function(p, answer)
        if answer == info.dialog.trade then
            local responses = {exitRID, scrollRID}

            p:ask(7108, responses)
        elseif answer == scrollRID then p:ask(7510, scrollSizeRID)
        elseif table.contains(scrollSizeRID, answer) then
            p:setCtxVal(ctxScrollSize, answer)
            p:ask(7511, scrollStatRID)
        elseif table.contains(scrollStatRID, answer) then
            p:setCtxVal(ctxScrollStat, answer)
            p:ask(7524, classDoploonRID)
        elseif answer == exitRID then p:endDialog()
        else return false -- Nothing else matched
        end
        -- Something matched
        return true
    end
end

local rewardPlayer = function(p, doploon)
    local rewards = rewardsPerGrade[gradeForPlayer(p)]

    p:addItem(doploon)
    p:modKamas(rewards[1])
    p:addXP(rewards[2])
end

local onTalkDoppleMaster = function(info)
    local tradeMenu = makeTradeMenu(info)

    return function(self, p, answer)
        local doppleQuest = QUESTS[info.quest.id]
        local classBangQuest = QUESTS[classBangQuestID]

        if answer == 0 then
            if p:level() < requiredLevel then
                p:ask(tooLowInitID)
                return
            end
            -- Player already finished this dopple quest too recently
            if not doppleQuest:availableTo(p) and not doppleQuest:ongoingFor(p) then
                p:ask(busyInitID, {info.dialog.trade})
                return
            end

            -- Player finished killing this dopple
            if doppleQuest:ongoingFor(p) and doppleQuest:hasCompletedObjective(p, info.quest.fightObjective) then
                doppleQuest:completeObjective(p, info.quest.npcObjective)
                rewardPlayer(p, info.doploon)
                p:ask(rewardInitID)
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
        elseif answer == info.dialog.info then p:ask(205)
        elseif tradeMenu(p, answer) then  return
        end
    end
end

local createNPC = function(info)
    local npc = Npc(info.npc.id, info.npc.gfx)

    if info.npc.colors then npc.colors = info.npc.colors end
    if info.npc.accessories then npc.accessories = info.npc.accessories end

    npc.onTalk = onTalkDoppleMaster(info)
    npc.quests = {info.quest.id}
    npc.sales = {}

    for _, s in ipairs(doploonSales) do
        table.insert(npc.sales, {item=s.item, price=s.price, currency=info.doploon})
    end

    RegisterNPCDef(npc)
end

--endregion

--region Quest

---@param q Quest
---@param p Player
local questAvailableTo = function(info)
    return function(q, p)
        -- dateStatID
        if not p:_questAvailable(q.id) then return false end
        if p:level() < requiredLevel then return false end

        local item = p:getItem(info.certificate, 1)

        -- No certificate, quest is available
        if not item then return true end

        local lastKill = item:dateStatTS(dateStatID)
        if not lastKill or lastKill == 0 then error("dopple certificate doesn't have a date") end

        local diff = World:clock() - lastKill

        return diff > questIntervalMs
    end
end

local createQuest = function(info)
    -- Add dopple as objective for Class Bang quest
    table.insert(defeatAllStep.objectives, KillMonsterSingleFightObjective(info.quest.bangObjective, info.mob, 1))

    -- Create quest step for killing dopple
    local defeatSingleStep = QuestStep(info.quest.step)

    --Create quest for killing a single dopple
    local defeatSingleQuest = Quest(info.quest.id, {defeatSingleStep})
    defeatSingleQuest.availableTo = questAvailableTo(info)
    defeatSingleQuest.isRepeatable = true

    -- Step objectives
    defeatSingleStep.objectives = defeatSingleQuest:SequentialObjectives({
        KillMonsterSingleFightObjective(info.quest.fightObjective, info.mob, 1),
        TalkWithQuestObjective(info.quest.npcObjective, info.npc.id),
    })

    -- Step reward
    defeatSingleStep.rewardFn = function(p)
        -- FIXME Certificates are drops on official servers
        -- Remove old certificate
        p:consumeItem(info.certificate, 1)
        p:addItem(info.certificate) -- TODO: Set date on item ?
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
        p:forceFight(-1, {
            {info.mob, {grade}}
        })
        return true
    end
end
--endregion

for _, info in pairs(dopples) do
    createNPC(info)
    createQuest(info)
end